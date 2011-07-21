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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jubula.client.core.AutStarterEvent;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.ServerEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.tools.constants.DebugConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * This class represents the connection to the AutStarter. A call to
 * createInstance() instantiate this class. <br>
 * Call createInstance() before getting the instance with getInstance().
 * 
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public class ServerConnection extends BaseConnection {
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(ServerConnection.class);
    
    /** the singleton instance */
    private static ServerConnection instance = null;

    /**
     * private constructor
     * @param inetAddress the host to connect to
     * @param port the port the remote host is listening to
     */
    private ServerConnection(InetAddress inetAddress, int port) {
        super();
        
        Communicator communicator = new Communicator(inetAddress, port, 
                this.getClass().getClassLoader()); 
        setCommunicator(communicator);
    }

    /**
     * {@inheritDoc}
     */
    protected synchronized void setCommunicator(Communicator communicator) {
        super.setCommunicator(communicator);
        communicator.addCommunicationErrorListener(
                new ServerConnectionListener());
    }

    /**
     * creates the "singleton".
     * @param serverName The name of the server.
     * @param port The port number.
     * @throws ConnectionException
     *             if a connection to the given server could not made, e.g. the
     *             name from AUTConfigPO can not resolved to a host.
     */
    public static synchronized void createInstance(String serverName, 
        String port) throws ConnectionException {
        
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(serverName);
            instance = new ServerConnection(inetAddress, 
                new Integer(port).intValue());
        } catch (UnknownHostException uhe) {
            // log on info level, the configuration may be bad typed
            log.info(DebugConstants.ERROR, uhe);
            throw new ConnectionException(uhe.getMessage(),
                MessageIDs.E_UNKNOWN_HOST);
        }
    }
    
    /**
     * Method to get the single instance of this class. <br>
     * !!! Call first createInstance() !!!
     * 
     * @throws ConnectionException
     *             when the instance was not created with createInstance()
     * @return the instance of this Singleton
     */
    public static synchronized ServerConnection getInstance() throws 
        ConnectionException {
        if (instance == null) {
            String message = Messages.ServerConnectionIsNotInitialized;
            throw new ConnectionException(message, 
                MessageIDs.E_NO_SERVER_CONNECTION_INIT);
        }

        return instance;
    }
    /**
     * The listener listening to the communicator.
     *
     * @author BREDEX GmbH
     * @created 12.08.2004
     */
    private class ServerConnectionListener implements
        ICommunicationErrorListener {

        /**
         * {@inheritDoc}
         */
        public void connectionGained(InetAddress inetAddress, int port) {
            if (log.isInfoEnabled()) {
                try {
                    String logMessage = Messages.ConnectedTo 
                        + StringConstants.SPACE 
                        + inetAddress.getHostName() 
                        + StringConstants.COLON + String.valueOf(port);
                    log.info(logMessage);
                } catch (SecurityException se) {
                    log.debug(Messages.SecurityViolationGettingHostNameFromIP);
                }
            }
            ClientTestFactory.getClientTest().fireAutStarterStateChanged(
                new AutStarterEvent(ServerEvent.CONNECTION_GAINED));
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            log.info(Messages.ConnectionToAUTAgentClosed);
            log.info(Messages.ClosingConnectionToTheAUTServer);
            
            try {
                AUTConnection.getInstance().close();
                ClientTestFactory.getClientTest().
                    fireAutStarterStateChanged(
                        new AutStarterEvent(ServerEvent.CONNECTION_CLOSED));
            } catch (ConnectionException ce) {
                // the connection to the AUTServer is not established
                // -> just log this
                log.debug(DebugConstants.ERROR, ce);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            log.warn(Messages.SendingMessageFailed + StringConstants.COLON 
                + message.toString());
            log.info(Messages.ClosingConnectionToTheAUTAgent);
            
            close();
        }

        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            log.error(Messages.AcceptingFailedCalledAlthoughThisIsClient 
                + StringConstants.COLON + String.valueOf(port));
        }

        /**
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            log.warn(Messages.ConnectingTheAUTAgentFailed);
            ClientTestFactory.getClientTest().
                fireAutStarterStateChanged(new AutStarterEvent(
                    AutStarterEvent.SERVER_CANNOT_CONNECTED));
        }
    }
    
}