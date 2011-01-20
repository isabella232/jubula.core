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
package org.eclipse.jubula.client.core.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.AUTServerEvent;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.StartAUTServerStateMessage;
import org.eclipse.jubula.tools.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created 06.08.2004
 *
 * 
 *
 *
 *
 */
public class StartAUTServerStateCommand implements ICommand {
    /** the logger */
    private static Log log = LogFactory
        .getLog(StartAUTServerStateCommand.class);

    /** the message */
    private StartAUTServerStateMessage m_message;

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
        m_message = (StartAUTServerStateMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        int state = m_message.getReason();
        switch (state) {
            case StartAUTServerStateMessage.OK: 
                log.info(Messages.AUTServerIsStarting);
                break;
            case StartAUTServerStateMessage.IO:
                // HERE notify error listener -> closing system
                log.fatal(Messages.NoJavaFound + StringConstants.COLON
                    + StringConstants.SPACE
                    + m_message.getDescription());
                ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.INVALID_JAVA));
                break;
            case StartAUTServerStateMessage.DATA:
            case StartAUTServerStateMessage.EXECUTION:
            case StartAUTServerStateMessage.SECURITY:
            case StartAUTServerStateMessage.INVALID_ARGUMENTS:
            case StartAUTServerStateMessage.ERROR:
            case StartAUTServerStateMessage.COMMUNICATION:
                log.error(Messages.AUTServerCouldNotStart 
                        + StringConstants.COLON + StringConstants.SPACE
                        + m_message.getDescription());
                ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                        new AUTServerEvent(AUTServerEvent.COMMUNICATION));
                break;
            case StartAUTServerStateMessage.AUT_MAIN_NOT_DISTINCT_IN_JAR:
            case StartAUTServerStateMessage.AUT_MAIN_NOT_FOUND_IN_JAR:
                log.info(Messages.AUTServerCouldNotStart 
                        + StringConstants.COLON + StringConstants.SPACE
                        + m_message.getDescription());
                ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                        new AUTServerEvent(AUTServerEvent.NO_MAIN_IN_JAR));
                break;
            case StartAUTServerStateMessage.NO_JAR_AS_CLASSPATH:
            case StartAUTServerStateMessage.SCANNING_JAR_FAILED:
                log.info(Messages.AUTServerCouldNotStart 
                        + StringConstants.COLON + StringConstants.SPACE
                    + m_message.getDescription());
                ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                        new AUTServerEvent(AUTServerEvent.INVALID_JAR));
                break;
            case StartAUTServerStateMessage.NO_SERVER_CLASS:
                log.error(Messages.AUTServerCouldNotStart 
                        + StringConstants.COLON + StringConstants.SPACE
                        + m_message.getDescription());
                ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.SERVER_NOT_INSTANTIATED));
                break;
            case StartAUTServerStateMessage.DOTNET_INSTALL_INVALID:
                log.error(Messages.AUTServerCouldNotStart 
                        + StringConstants.COLON + StringConstants.SPACE
                    + m_message.getDescription());
                ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.DOTNET_INSTALL_INVALID));
                break;
            case StartAUTServerStateMessage.JDK_INVALID:
                log.error(Messages.AUTServerCouldNotStart 
                        + StringConstants.COLON + StringConstants.SPACE
                    + m_message.getDescription());
                ClientTestFactory.getClientTest().fireAUTServerStateChanged(
                    new AUTServerEvent(AUTServerEvent.JDK_INVALID));
                break;
                
            default:
                log.error(Messages.UnknownState + StringConstants.SPACE  
                    + String.valueOf(state) 
                    + StringConstants.COLON + m_message.getDescription());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + StringConstants.DOT
                + Messages.TimeoutCalled);
    }
}
