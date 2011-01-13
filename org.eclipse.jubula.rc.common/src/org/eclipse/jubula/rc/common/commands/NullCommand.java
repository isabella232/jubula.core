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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.NullMessage;


/**
 * Command that does nothing.
 *
 * @author BREDEX GmbH
 * @created 02.02.2006
 * 
 */
public class NullCommand implements ICommand {
    
    /** the logger */
    private static Log log = LogFactory.getLog(NullCommand.class);
    /** the message */
    private NullMessage m_message;

    /**
     * {@inheritDoc}
     * @return
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     * @param message
     */
    public void setMessage(Message message) {
        m_message = (NullMessage)message;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public Message execute() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}