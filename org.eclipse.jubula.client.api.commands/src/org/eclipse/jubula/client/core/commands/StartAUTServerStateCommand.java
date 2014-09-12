/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.commands;

import org.eclipse.jubula.communication.APICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.StartAUTServerStateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author BREDEX GmbH */
public class StartAUTServerStateCommand implements APICommand {
    /** the logger */
    private static Logger log = LoggerFactory
        .getLogger(StartAUTServerStateCommand.class);

    /** the message */
    private StartAUTServerStateMessage m_message;

    /** {@inheritDoc} */
    public Message getMessage() {
        return m_message;
    }

    /** {@inheritDoc} */
    public void setMessage(Message message) {
        m_message = (StartAUTServerStateMessage) message;
    }

    /** {@inheritDoc} */
    public Message execute() {
        // FIXME MT: currently empty
        return null;
    }

    /** {@inheritDoc} */
    public void timeout() {
        log.error(this.getClass().getName());
    }
}