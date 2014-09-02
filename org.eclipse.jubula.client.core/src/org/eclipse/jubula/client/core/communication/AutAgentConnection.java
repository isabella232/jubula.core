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

import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.events.AutAgentEvent;
import org.eclipse.jubula.client.core.events.ServerEvent;
import org.eclipse.jubula.client.internal.BaseConnection;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class represents the connection to the AUT Agent. A call to
 * createInstance() instantiate this class. <br>
 * Call createInstance() before getting the instance with getInstance().
 * 
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public class AutAgentConnection extends BaseConnection {
    
    /**
     * Job Family ID. All Jobs dealing with connecting to the AUT Agent
     * will return <code>true</code> when their {@link Job#belongsTo(Object)}
     * method is called with this Object as the argument. 
     */
    public static final Object CONNECT_TO_AGENT_JOB_FAMILY_ID = new Object();
    
    /** the logger */
    private static Logger log = 
            LoggerFactory.getLogger(AutAgentConnection.class);
    
    /** the singleton instance */
    private static AutAgentConnection instance = null;

    /**
     * private constructor
     * @param inetAddress the host to connect to
     * @param port the port the remote host is listening to
     */
    private AutAgentConnection(InetAddress inetAddress, int port) {
        super();
        
        Communicator communicator = new Communicator(inetAddress, port, 
                this.getClass().getClassLoader()); 
        communicator.setIsServerSocketClosable(false);
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
            instance = new AutAgentConnection(inetAddress, 
                new Integer(port).intValue());
        } catch (UnknownHostException uhe) {
            // log on info level, the configuration may be bad typed
            log.info(uhe.getLocalizedMessage(), uhe);
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
    public static synchronized AutAgentConnection getInstance() throws 
        ConnectionException {
        if (instance == null) {
            String message = "ServerConnection is not initialized"; //$NON-NLS-1$
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
                    String logMessage = "connected to "  //$NON-NLS-1$
                        + inetAddress.getHostName() 
                        + StringConstants.COLON + String.valueOf(port);
                    log.info(logMessage);
                } catch (SecurityException se) {
                    log.debug("security violation while getting the host name from IP-address"); //$NON-NLS-1$
                }
            }
            ClientTest.instance().fireAutAgentStateChanged(
                new AutAgentEvent(ServerEvent.CONNECTION_GAINED));
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            log.info("connection to AUT Agent closed"); //$NON-NLS-1$
            log.info("closing connection to the AUTServer"); //$NON-NLS-1$
            
            try {
                AUTConnection.getInstance().close();
                ClientTest.instance().
                    fireAutAgentStateChanged(
                        new AutAgentEvent(ServerEvent.CONNECTION_CLOSED));
            } catch (ConnectionException ce) {
                // the connection to the AUTServer is not established
                // -> just log this
                log.debug(ce.getLocalizedMessage(), ce);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            log.warn("sending message failed:"  //$NON-NLS-1$
                + message.toString());
            log.info("closing connection to the AUT Agent"); //$NON-NLS-1$
            
            close();
        }

        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            log.error("accepting failed() called although this is a 'client':" //$NON-NLS-1$
                + String.valueOf(port));
        }

        /**
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            log.warn("connecting the AUT Agent failed"); //$NON-NLS-1$
            ClientTest.instance().
                fireAutAgentStateChanged(new AutAgentEvent(
                    AutAgentEvent.SERVER_CANNOT_CONNECTED));
        }
    }
    
}