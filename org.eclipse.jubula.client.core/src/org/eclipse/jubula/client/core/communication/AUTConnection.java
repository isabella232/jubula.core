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
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.commands.AUTStartedCommand;
import org.eclipse.jubula.client.core.commands.AUTStateCommand;
import org.eclipse.jubula.client.core.commands.ConnectToAutResponseCommand;
import org.eclipse.jubula.client.core.commands.GetKeyboardLayoutNameResponseCommand;
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.ServerEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingProfilePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.BaseConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.message.AUTStateMessage;
import org.eclipse.jubula.communication.message.ConnectToAutMessage;
import org.eclipse.jubula.communication.message.GetKeyboardLayoutNameMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.communication.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.communication.message.SetKeyboardLayoutMessage;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.constants.EnvConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.exception.JBVersionException;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.jubula.tools.utils.TimeUtil;
import org.eclipse.jubula.tools.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.Profile;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class represents the connection to the AUTServer which controls the
 * application under test.
 * 
 * This class is implemented as a singleton. The server configuration contains
 * detailed information, how this instance can be contacted.
 * 
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public class AUTConnection extends BaseConnection {
    /**
     * the timeout used for establishing a connection to a running AUT
     */
    public static final int CONNECT_TO_AUT_TIMEOUT = 10000;

    /** the logger */
    static final Logger LOG = LoggerFactory
            .getLogger(AUTConnection.class);

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
     *             initialized
     */
    private AUTConnection() throws ConnectionException {
        super();
        m_autConnectionListener = new AUTConnectionListener();
        try {
            // create a communicator on any free port
            Communicator communicator = new Communicator(0, this.getClass()
                    .getClassLoader());
            communicator.addCommunicationErrorListener(m_autConnectionListener);
            communicator.setIsServerSocketClosable(false);
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
     * handles the fatal errors occurs during initialization
     * 
     * @param throwable
     *            the occurred exception
     * @throws ConnectionException
     *             a ConnectionException containing a detailed message
     */
    private void handleInitError(Throwable throwable)
        throws ConnectionException {
        String message = Messages.InitialisationOfAUTConnectionFailed
            + StringConstants.COLON + StringConstants.SPACE;
        LOG.error(message, throwable);
        throw new ConnectionException(message + throwable.getMessage(), 
            MessageIDs.E_AUT_CONNECTION_INIT);
    }

    /**
     * Method to get the single instance of this class.
     * 
     * @throws ConnectionException
     *             if an error occurs during initialization.
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
        Communicator communicator = getCommunicator();
        if (communicator != null) {
            communicator.setIsServerSocketClosable(true);
            communicator.interruptAllTimeouts();
            communicator.clearListeners();
            communicator.close();
        }

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
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        if (!isConnected()) {
            ded.fireAutServerConnectionChanged(ServerState.Connecting);
            try {
                monitor.subTask(NLS.bind(Messages.ConnectingToAUT, 
                        autId.getExecutableName()));
                LOG.info(Messages.EstablishingConnectionToAUT);
                run();
                getCommunicator().addCommunicationErrorListener(
                        m_autConnectionListener);
                ConnectToAutResponseCommand responseCommand =
                    new ConnectToAutResponseCommand();
                AutAgentConnection.getInstance().getCommunicator().request(
                    new ConnectToAutMessage(
                        EnvConstants.LOCALHOST_FQDN, 
                        getCommunicator().getLocalPort(), autId), 
                    responseCommand, 10000);
                if (responseCommand.getMessage() != null 
                        && responseCommand.getMessage().getErrorMessage() 
                            != null) {
                    // Connection has failed
                    ded.fireAutServerConnectionChanged(
                            ServerState.Disconnected);
                    return false;
                }
                long startTime = System.currentTimeMillis();
                while (!monitor.isCanceled() && !isConnected() 
                        && AutAgentConnection.getInstance().isConnected()
                        && startTime + CONNECT_TO_AUT_TIMEOUT 
                            > System.currentTimeMillis()) {
                    TimeUtil.delay(200);
                }
                if (isConnected()) {
                    m_connectedAutId = autId;
                    LOG.info(Messages.ConnectionToAUTEstablished);
                    IAUTMainPO aut = AutAgentRegistration.getAutForId(autId, 
                            GeneralStorage.getInstance().getProject());
                    if (aut != null) {
                        AUTStartedCommand response = new AUTStartedCommand();
                        response.setStateMessage(new AUTStateMessage(
                                AUTStateMessage.RUNNING));
                        setup(response);
                    } else {
                        LOG.warn(Messages.ErrorOccurredActivatingObjectMapping);
                    }
                    return true;
                }
                LOG.error(Messages.ConnectionToAUTCouldNotBeEstablished);
            } catch (CommunicationException e) {
                LOG.error(Messages.ErrorOccurredEstablishingConnectionToAUT, e);
            } catch (JBVersionException e) {
                LOG.error(Messages.ErrorOccurredEstablishingConnectionToAUT, e);
            } finally {
                monitor.done();
            }
        } else {
            LOG.warn(Messages.CannotEstablishNewConnectionToAUT);
        } 
        ded.fireAutServerConnectionChanged(ServerState.Disconnected);
        return false;
    }
    
    /**
     * Sets the keyboard layout for the currently connected AUT.
     * 
     * @throws NotConnectedException if there is no connection to an AUT.
     * @throws ConnectionException if no connection to an AUT could be 
     *                             initialized.
     * @throws CommunicationException if an error occurs while communicating
     *                                with the AUT.
     */
    private void sendKeyboardLayoutToAut() 
        throws NotConnectedException, ConnectionException, 
               CommunicationException {

        final int timeoutToUse = CONNECT_TO_AUT_TIMEOUT; 
        
        GetKeyboardLayoutNameMessage request = 
            new GetKeyboardLayoutNameMessage();
        GetKeyboardLayoutNameResponseCommand response =
            new GetKeyboardLayoutNameResponseCommand();
        request(request, response, timeoutToUse);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() <= startTime + timeoutToUse
                && !response.wasExecuted() && isConnected()) {
            TimeUtil.delay(500);
        }
        
        String layoutName = response.getKeyboardLayoutName();
        if (StringUtils.isNotEmpty(layoutName)) {
            String filename = 
                Languages.KEYBOARD_MAPPING_FILE_PREFIX + layoutName 
                + Languages.KEYBOARD_MAPPING_FILE_POSTFIX;
            final InputStream stream = getClass().getClassLoader()
                .getResourceAsStream(filename);
            try {
                if (stream != null) {
                    Properties prop = new Properties();
                    prop.load(stream);
                    send(new SetKeyboardLayoutMessage(prop));
                } else {
                    LOG.error("Mapping for '" + layoutName + "' could not be found.");  //$NON-NLS-1$//$NON-NLS-2$
                }
            } catch (IOException ioe) {
                LOG.error("Error occurred while loading Keyboard Mapping.", ioe); //$NON-NLS-1$
            } catch (IllegalArgumentException iae) {
                LOG.error("Error occurred while loading Keybaord Mapping.", iae); //$NON-NLS-1$
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        LOG.warn("Error occurred while closing stream.", e); //$NON-NLS-1$
                    }
                }
            }
        }
    }
    
    /**
     * setup the connection between ITE and AUT
     * 
     * @param command
     *            the command to execute on callback
     * @throws NotConnectedException
     *             if there is no connection to an AUT.
     * @throws ConnectionException
     *             if no connection to an AUT could be initialized.
     * @throws CommunicationException
     *             if an error occurs while communicating with the AUT.
     */
    public void setup(AUTStartedCommand command)
        throws NotConnectedException, ConnectionException,
        CommunicationException {
        sendKeyboardLayoutToAut();
        sendResourceBundlesToAut();
        getAllComponentsFromAUT(command);
    }
    
    /**
     * Sends the i18n resource bundles to the AUT Server.
     */
    private void sendResourceBundlesToAut() {
        SendCompSystemI18nMessage i18nMessage = new SendCompSystemI18nMessage();
        i18nMessage.setResourceBundles(CompSystemI18n.bundlesToString());
        try {
            send(i18nMessage);
        } catch (CommunicationException e) {
            LOG.error(Messages.CommunicationErrorWhileSettingResourceBundle, e);
        }
    }

    /**
     * Query the AUTServer for all supported components.
     * <code>listener.componentInfo()</code> will be called when the answer
     * receives.
     * 
     * @param command
     *            the command to execute as a callback
     * 
     * @throws CommunicationException
     *             if an error occurs while communicating with the AUT.
     */
    private void getAllComponentsFromAUT(AUTStartedCommand command)
        throws CommunicationException {
        
        LOG.info(Messages.GettingAllComponentsFromAUT);

        try {
            SendAUTListOfSupportedComponentsMessage message = 
                MessageFactory.getSendAUTListOfSupportedComponentsMessage();
            // Send the supported components and their implementation classes
            // to the AUT server to get registered.
            CompSystem compSystem = ComponentBuilder.getInstance()
                    .getCompSystem();
            IAUTMainPO connectedAut = TestExecution.getInstance()
                    .getConnectedAut();
            String autToolkitId = connectedAut.getToolkit();
            List<Component> components = compSystem.getComponents(
                    autToolkitId, true);

            // optimization: only concrete components need to be registered,
            // as abstract components do not have a corresponding tester class
            components.retainAll(compSystem.getConcreteComponents());
            message.setComponents(components);
            
            Profile profile = new Profile();
            IObjectMappingProfilePO profilePo = connectedAut.getObjMap()
                    .getProfile();
            profile.setNameFactor(profilePo.getNameFactor());
            profile.setPathFactor(profilePo.getPathFactor());
            profile.setContextFactor(profilePo.getContextFactor());
            profile.setThreshold(profilePo.getThreshold());

            message.setProfile(profile);
            
            int timeoutToUse = AUTStateCommand.AUT_COMPONENT_RETRIEVAL_TIMEOUT;
            request(message, command, timeoutToUse);

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() <= startTime + timeoutToUse
                    && !command.wasExecuted() && isConnected()) {
                TimeUtil.delay(500);
            }
            if (!command.wasExecuted() && isConnected()) {
                throw new CommunicationException(
                        Messages.CouldNotRequestComponentsFromAUT,
                        MessageIDs.E_COMMUNICATOR_CONNECTION);
            }
        } catch (UnknownMessageException ume) {
            ClientTest.instance().fireAUTServerStateChanged(
                    new AUTServerEvent(ume.getErrorId()));
        } 
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
         */
        public void connectionGained(InetAddress inetAddress, int port) {
            if (LOG.isInfoEnabled()) {
                try {
                    String logMessage = Messages.ConnectedTo 
                            + inetAddress.getHostName()
                            + StringConstants.COLON + String.valueOf(port);
                    LOG.info(logMessage);
                } catch (SecurityException se) {
                    LOG.debug(Messages.SecurityViolationGettingHostNameFromIP);
                }
            }
            ClientTest.instance().
                fireAUTServerStateChanged(new AUTServerEvent(
                    ServerEvent.CONNECTION_GAINED));
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            if (LOG.isInfoEnabled()) {
                LOG.info(Messages.ConnectionToAUTServerClosed);
                LOG.info(Messages.ClosingConnectionToTheAutStarter);
            }
            disconnectFromAut();
            DataEventDispatcher.getInstance().fireAutServerConnectionChanged(
                    ServerState.Disconnected);
            IClientTest clientTest = ClientTest.instance();
            clientTest.fireAUTServerStateChanged(new AUTServerEvent(
                    AUTServerEvent.TESTING_MODE));
            clientTest.fireAUTStateChanged(new AUTEvent(AUTEvent.AUT_STOPPED));
            clientTest.fireAUTServerStateChanged(new AUTServerEvent(
                    ServerEvent.CONNECTION_CLOSED));
        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            LOG.error(Messages.SendingMessageFailed + StringConstants.COLON 
                    + message.toString());
            LOG.error(Messages.ClosingConnectionToTheAUTServer);
            close();
        }

        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            LOG.warn(Messages.AcceptingFailed + StringConstants.COLON 
                    + String.valueOf(port));
        }

        /**
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.ConnectingFailed);
            msg.append(StringConstants.LEFT_PARENTHESES);
            msg.append(StringConstants.RIGHT_PARENTHESES);
            msg.append(StringConstants.SPACE);
            msg.append(Messages.CalledAlthoughThisIsServer);
            LOG.error(msg.toString());
        }
    }
}
