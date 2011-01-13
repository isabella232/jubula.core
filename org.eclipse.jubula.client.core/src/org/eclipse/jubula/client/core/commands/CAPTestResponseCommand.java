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
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.MessageCap;



/**
 * The command class for CAPTestResponseMessage. <br>
 * 
 * Currently the execute() methods logs the result and returns always null (no
 * message to send as response).
 * 
 * @author BREDEX GmbH
 * @created 07.09.2004
 * 
 */
public class CAPTestResponseCommand implements ICommand {
    
    /**
     * The logger
     */
    private static final Log LOG =
        LogFactory.getLog(CAPTestResponseCommand.class);
    /**
     * The message
     */
    private CAPTestResponseMessage m_capTestResponseMessage;
    
   
    /**
     * <code>m_messageCap</code> contains data sending to server for the
     * actual cap (test step)
     */
    private MessageCap m_messageCap;

    /**
     * constructor
     */
    public CAPTestResponseCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_capTestResponseMessage;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_capTestResponseMessage = (CAPTestResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        logResult();
        TestExecution.getInstance()
            .processServerResponse((CAPTestResponseMessage)getMessage());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        StringBuilder messageCapData = 
            new StringBuilder("; MessageCap: "); //$NON-NLS-1$
        if (LOG.isErrorEnabled()) {
            if (m_capTestResponseMessage != null) {
                MessageCap msgCap = m_capTestResponseMessage.getMessageCap();
                messageCapData.append("Method '") //$NON-NLS-1$
                    .append(msgCap.getMethod())
                    .append("', ComponentId '") //$NON-NLS-1$
                    .append(msgCap.getCi())
                    .append("'"); //$NON-NLS-1$
            } else {
                messageCapData.append("null"); //$NON-NLS-1$
            }
        }
        LOG.error(this.getClass().getName() 
                + " timeout() called" //$NON-NLS-1$
                + messageCapData.toString());
        TestExecution.getInstance().timeout();
    }

    
    /**
     * private method logging the result on info level
     */
    private void logResult() {
        if (LOG.isInfoEnabled()) {
            String message = "test step result:"; //$NON-NLS-1$
            int state = m_capTestResponseMessage.getState();
            
            switch (state) {
                case CAPTestResponseMessage.TEST_OK: 
                    message = message + "success"; //$NON-NLS-1$
                    if (!void.class.getName().equals(
                            m_capTestResponseMessage.getReturnType())) {
                        
                        message = message + "\nreturn type:" //$NON-NLS-1$
                                + m_capTestResponseMessage.getReturnType()
                                + "\nreturn value:" //$NON-NLS-1$
                                + m_capTestResponseMessage.getReturnValue();
                    }
                    break;
                case CAPTestResponseMessage.TEST_FAILED:
                    message = message  
                        + "general failure\n"; //$NON-NLS-1$
                    break;
                case CAPTestResponseMessage.FAILURE_SECURITY:
                    message = message 
                        + "failure: missing permission\n"; //$NON-NLS-1$
                    break;
                case CAPTestResponseMessage.FAILURE_ACCESSIBILITY:
                    message = message 
                        + "failure: method not accesible\n"; //$NON-NLS-1$
                    break;
                case CAPTestResponseMessage
                    .FAILURE_INVALID_IMPLEMENTATION_CLASS:
                    message = message
                        + "failure: missing implementation class\n"; //$NON-NLS-1$ 
                    break;
                case CAPTestResponseMessage.FAILURE_METHOD_NOT_FOUND:
                    break;
                case CAPTestResponseMessage.FAILURE_INVALID_PARAMETER:
                    message = message 
                        + "failure: parameters are not valid\n"; //$NON-NLS-1$ 
                    break;
                case CAPTestResponseMessage.FAILURE_STEP_EXECUTION:
                    message = message 
                        + "failure: implementing method " //$NON-NLS-1$
                        + "has thrown an exception\n"; //$NON-NLS-1$
                    break;
                case CAPTestResponseMessage.FAILURE_UNSUPPORTED_COMPONENT:
                    message = message 
                        + "failure: component is not supported\n"; //$NON-NLS-1$
                    break;
                case CAPTestResponseMessage.FAILURE_COMPONENT_NOT_FOUND:
                    message = message 
                        + "failure: component not found\n"; //$NON-NLS-1$
                    break;
                default:
                    message = "unknown state: " + state; //$NON-NLS-1$
            }
            LOG.debug(message);
        }
    }
    /**
     * @return Returns the messageCap.
     */
    public MessageCap getMessageCap() {
        return m_messageCap;
    }

    /**
     * @param messageCap the message cap to set
     */
    public void setMessageCap(MessageCap messageCap) {
        m_messageCap = messageCap;
    }
}
