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
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent.RegistrationStatus;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.AutRegisteredMessage;
import org.eclipse.jubula.communication.message.Message;


/**
 * Notifies the client that an AUT has been registered/deregistered from the 
 * AUT Agent.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class AutRegisteredCommand implements ICommand {

    /** the logger */
    private static Log log = LogFactory.getLog(AutRegisteredCommand.class);

    /** the message */
    private AutRegisteredMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        AutAgentRegistration.getInstance().fireAutRegistration(
            new AutRegistrationEvent(m_message.getAutId(), 
                m_message.isRegistered() ? RegistrationStatus.Register 
                        : RegistrationStatus.Deregister));
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
        m_message = (AutRegisteredMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}
