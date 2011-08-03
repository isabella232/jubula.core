/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.commands;

import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.GetKeyboardLayoutNameResponseMessage;
import org.eclipse.jubula.communication.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accepts the name of the keyboard layout used for the AUT.
 *
 * @author BREDEX GmbH
 * @created Aug 2, 2011
 */
public class GetKeyboardLayoutNameResponseCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(GetKeyboardLayoutNameResponseCommand.class);
    
    /** flag that is set at the end of execution */
    private boolean m_wasExecuted = false;

    /** the message */
    private GetKeyboardLayoutNameResponseMessage m_message;

    /**
     * 
     * {@inheritDoc}
     */
    public GetKeyboardLayoutNameResponseMessage getMessage() {
        return m_message;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (GetKeyboardLayoutNameResponseMessage)message;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        m_wasExecuted = true;
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

    /**
     * 
     * @return the retrieved keyboard layout name.
     */
    public String getKeyboardLayoutName() {
        return getMessage() != null 
            ? getMessage().getKeyboardLayoutName() : null;
    }
    
    /**
     * 
     * @return <code>true</code> if the command has been succesfully executed.
     *         Otherwise, <code>false</code>.
     */
    public boolean wasExecuted() {
        return m_wasExecuted;
    }
}
