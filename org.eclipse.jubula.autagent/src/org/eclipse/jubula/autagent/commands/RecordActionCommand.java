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
package org.eclipse.jubula.autagent.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.CAPRecordedMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.RecordActionMessage;
import org.eclipse.jubula.tools.exception.CommunicationException;


/**
 * The command class for record CAP. <br>
 * xxx
 * 
 * @author BREDEX GmbH
 * @created 21.05.2008
 * 
 */
public class RecordActionCommand implements ICommand {
    /** the logger */
    private static Log log = LogFactory.getLog(RecordActionCommand.class);
    
    /** the message */
    private RecordActionMessage m_message;
    
    
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
        m_message = (RecordActionMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        CAPRecordedMessage messageRecCap = m_message.getCAPRecordedMessage();
        
        try {
            AutStarter.getInstance().getCommunicator().send(messageRecCap);
        } catch (CommunicationException e) { // NOPMD by al on 4/11/07 3:39 PM
            // no log available here
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}
