/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.AUTEvent;
import org.eclipse.jubula.client.core.AUTServerEvent;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.IAUTInfoListener;
import org.eclipse.jubula.client.core.ServerEvent;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.commands.ConnectToAutResponseCommand;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingProfilePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.message.ConnectToAutMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.exception.JBVersionException;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.jubula.tools.xml.businessmodell.Profile;
import org.eclipse.jubula.tools.xml.businessprocess.ProfileBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class represents the connection to the AUTServer which controls the
 * aplication under test.
 * 
 * This class is implemeted as a singleton. The server configuration contains
 * detailied information, how this instance can be contacted.
 * 
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public class AUTConnection extends BaseConnection {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTConnection.class);

    /** the singleton instance */
    private static AUTConnection instance = null;
    
    /** The m_autConnectionListener */
    private AUTConnectionListener m_autConnectionListener;

    /** 
     * The ID of the Running AUT with which a connection is currently 
     * established. 
     */
    private AutIdentifier m_connectedAutId;
    
    /**
     * private constructor. creates a communicator
     * 
     * @throws ConnectionException
     *             containing a detailed message why the connection could not
     *             initialised
     */
    private AUTConnection() throws ConnectionException {
        super();
        m_autConnectionListener = new AUTConnectionListener();
        try {
            // create a communicator on any free port
            Communicator communicator = new Communicator(0, this.getClass()
                    .getClassLoader());
            communicator
                    .addCommunicationErrorListener(m_autConnectionListener);
            setCommunicator(communicator);
        } catch (IOException ioe) {
            handleInitError(ioe);
        } catch (SecurityException se) {
            handleInitError(se);
        }
    }

    /**
     * Disconnects from the currently connected Running AUT. If no connection
     * currently exists, this method is a no-op.
     */
    private void disconnectFromAut() {
        m_connectedAutId = null;
    }
    
    /**
     * handles the fatal errors occurs during initialisation
     * 
     * @param throwable
     *            the occured exception
     * @throws ConnectionException
     *             a ConnectionException containing a detailed message
     */
    private void handleInitError(Throwable throwable)
        throws ConnectionException {
        String message = Messages.InitialisationOfAUTConnectionFailed
            + StringConstants.COLON + StringConstants.SPACE;
        log.error(message, throwable);
        throw new ConnectionException(message + throwable.getMessage(), 
            MessageIDs.E_AUT_CONNECTION_INIT);
    }

    /**
     * Method to get the single instance of this class.
     * 
     * @throws ConnectionException
     *             if an error occurs during initialisation.
     * @return the instance of this Singleton
     */
    public static synchronized AUTConnection getInstance()
        throws ConnectionException {

        if (instance == null) {
            instance = new AUTConnection();
        }
        return instance;
    }
    
    /**
     * 
     * @return the ID of the currently connected AUT, or <code>null</code> if 
     *         there is currently no connection to an AUT.
     */
    public AutIdentifier getConnectedAutId() {
        return m_connectedAutId;
    }
    
    /**
     * Resets this singleton: Closes the communicator
     * removes the listeners.<br>
     * <b>Note: </b><br>
     * This method is used by the Restart-AUT-Action only to avoid errors while
     * reconnecting with the AUTServer.<br>
     * This is necessary because the disconnect from the AUTServer is implemented
     * badly which will be corrected in a future version!
     */
    public synchronized void reset() {
        if (getCommunicator() != null) {
            getCommunicator().clearListeners();
        }

        getCommunicator().close();

        instance = null;
    }

    /**
     * Establishes a connection to the Running AUT with the given ID. 
     * 
     * @param autId The ID of the Running AUT to connect to.
     * @param monitor The progress monitor.
     * @return <code>true</code> if a connection to the AUT could be 
     *         established. Otherwise <code>false</code>.
     */
    public boolean connectToAut(AutIdentifier autId,
            IProgressMonitor monitor) {
        if (!isConnected()) {
            DataEventDispatcher.getInstance().fireAutServerConnectionChanged(
                    ServerState.Connecting);
            try {
                monitor.subTask(Messages.ConnectingToAUT);
                log.info(Messages.EstablishingConnectionToAUT);
                run();
                getCommunicator().addCommunicationErrorListener(
                        m_autConnectionListener);
                ConnectToAutResponseCommand responseCommand =
                    new ConnectToAutResponseCommand();
                ServerConnection.getInstance().getCommunicator().request(
                    new ConnectToAutMessage(
                        InetAddress.getLocalHost().getCanonicalHostName(), 
                        getCommunicator().getLocalPort(), autId), 
                    responseCommand, 10000);
                if (responseCommand.getMessage() != null 
                        && responseCommand.getMessage().getErrorMessage() 
                            != null) {
                    // Connection has failed
                    DataEventDispatcher.getInstance()
                        .fireAutServerConnectionChanged(
                            ServerState.Disconnected);
                    return false;
                }
                long timeout = 10000;
                long startTime = System.currentTimeMillis();
                while (!monitor.isCanceled() && !isConnected() 
                        && ServerConnection.getInstance().isConnected()
                        && startTime + timeout > System.currentTimeMillis()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // Do nothing. The exit conditions will be checked
                        // again on the next loop iteration.
                    }
                }
                if (isConnected()) {
                    m_connectedAutId = autId;
                    log.info(Messages.ConnectionToAUTEstablished 
                            + StringConstants.DOT);
                    IAUTMainPO aut = AutAgentRegistration.getAutForId(autId, 
                            GeneralStorage.getInstance().getProject());
                    if (aut != null) {
                        getComponentsFromAut(aut);
                        sendResourceBundlesToAut();
                        ClientTestFactory.getClientTest()
                            .setAutKeyboardLayout(10000);
                    } else {
                        log.warn(Messages.ErrorOccurredActivatingObjectMapping);
                    }
                    return true;
                }
                log.error(Messages.ConnectionToAUTCouldNotBeEstablished
                        + StringConstants.DOT);
            } catch (CommunicationException e) {
                log.error(Messages.ErrorOccurredEstablishingConnectionToAUT
                        + StringConstants.DOT, e);
            } catch (UnknownHostException e) {
                log.error(Messages.ErrorOccurredEstablishingConnectionToAUT
                        + StringConstants.DOT, e);
            } catch (JBVersionException e) {
                log.error(Messages.ErrorOccurredEstablishingConnectionToAUT
                        + StringConstants.DOT, e);
            } finally {
                monitor.done();
            }
        } else {
            log.warn(Messages.CannotEstablishNewConnectionToAUT);
        } 
        DataEventDispatcher.getInstance().fireAutServerConnectionChanged(
                ServerState.Disconnected);
        return false;
    }

    /**
     * Sends the i18n resource bundles to the AUT Server.
     */
    private void sendResourceBundlesToAut() {
        SendCompSystemI18nMessage i18nMessage =
            new SendCompSystemI18nMessage();
        i18nMessage.setResourceBundles(
                CompSystemI18n.bundlesToString());
        try {
            send(i18nMessage);
        } catch (CommunicationException ce) {
            log.error(Messages.CommunicationErrorWhileSettingResourceBundle, 
                ce); 
        }
    }

    /**
     * Sends a message to the currently connected AUT to initialize all 
     * supported component types.
     * 
     * @param aut The AUT for which to get the components.
     */
    private void getComponentsFromAut(IAUTMainPO aut) {
        Profile profile = new Profile();
        IObjectMappingProfilePO profilePo = 
            aut.getObjMap().getProfile();
        profile.setNameFactor(profilePo.getNameFactor());
        profile.setPathFactor(profilePo.getPathFactor());
        profile.setContextFactor(profilePo.getContextFactor());
        profile.setThreshold(profilePo.getThreshold());
        ProfileBuilder.setActiveProfile(profile);
        
        IAUTInfoListener listener = new IAUTInfoListener() {
            public void error(int reason) {
                log.error(Messages.ErrorOccurredWhileGettingComponentsFromAUT
                       + StringConstants.COLON + StringConstants.SPACE 
                       + reason);
            }
        };

        ClientTestFactory.getClientTest()
            .getAllComponentsFromAUT(listener, 10000);
    }
    
    /**
     * The listener listening to the communicator.
     * 
     * @author BREDEX GmbH
     * @created 12.08.2004
     */
    private class AUTConnectionListener implements ICommunicationErrorListener {

        /**
         * {@inheritDoc}
         *      int)
         */
        public void connectionGained(InetAddress inetAddress, int port) {
            if (log.isInfoEnabled()) {
                try {
                    String logMessage = Messages.ConnectedTo 
                            + inetAddress.getHostName()
                            + StringConstants.COLON + String.valueOf(port);
                    log.info(logMessage);
                } catch (SecurityException se) {
                    log.debug(Messages.SecurityViolationGettingHostNameFromIP);
                }
            }
            ClientTestFactory.getClientTest().
                fireAUTServerStateChanged(new AUTServerEvent(
                    ServerEvent.CONNECTION_GAINED));
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            if (log.isInfoEnabled()) {
                log.info(Messages.ConnectionToAUTServerClosed);
                log.info(Messages.ClosingConnectionToTheAutStarter);
            }
            disconnectFromAut();
            DataEventDispatcher.getInstance().fireAutServerConnectionChanged(
                    ServerState.Disconnected);
            ClientTestFactory.getClientTest().
                fireAUTServerStateChanged(new AUTServerEvent(
                        AUTServerEvent.TESTING_MODE));
            ClientTestFactory.getClientTest().fireAUTStateChanged(
                new AUTEvent(AUTEvent.AUT_STOPPED));
            ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                new AUTServerEvent(ServerEvent.CONNECTION_CLOSED));

        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            log.error(Messages.SendingMessageFailed + StringConstants.COLON 
                    + message.toString());
            log.error(Messages.ClosingConnectionToTheAUTServer);
            close();
        }

        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            log.warn(Messages.AcceptingFailed + StringConstants.COLON 
                    + String.valueOf(port));
        }

        /**
         * {@inheritDoc}
         *      int)
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.ConnectingFailed);
            msg.append(StringConstants.LEFT_PARENTHESES);
            msg.append(StringConstants.RIGHT_PARENTHESES);
            msg.append(StringConstants.SPACE);
            msg.append(Messages.CalledAlthoughThisIsServer);
            log.error(msg.toString());
        }
    }


}
