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
package org.eclipse.jubula.rc.common.commands;

import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.connection.Connection;
import org.eclipse.jubula.communication.listener.IErrorHandler;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.MessageHeader;
import org.eclipse.jubula.communication.message.PrepareForShutdownMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.tools.constants.AUTServerExitConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Prepares the AUT Server for shutdown (i.e. deregisters communication 
 * error listeners).
 *
 * @author BREDEX GmbH
 * @created Mar 23, 2010
 */
public class PrepareForShutdownCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(PrepareForShutdownCommand.class);

    /** the message */
    private PrepareForShutdownMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        Communicator agentCommunicator = 
            AUTServer.getInstance().getServerCommunicator();
        if (agentCommunicator != null) {
            agentCommunicator.clearListeners();
        }
        Connection autAgentConnection = agentCommunicator.getConnection();
        if (autAgentConnection != null) {
            // Add a listener to exit the AUT normally when the connection is 
            // closed
            autAgentConnection.addErrorHandler(new IErrorHandler() {
                
                public void shutDown() {
                    terminate();
                }
                
                public void sendFailed(MessageHeader header, String message) {
                    terminate();
                }
                
                private void terminate() {
                    try {
                        AUTServer.getInstance().shutdown();
                    } finally {
                        System.exit(AUTServerExitConstants.EXIT_OK);
                    }
                }
            });
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (PrepareForShutdownMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}
