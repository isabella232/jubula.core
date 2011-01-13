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
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.TakeScreenshotResponseMessage;


/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class TakeScreenshotResponseCommand implements ICommand {
    /** the logger */
    private static Log log = LogFactory
            .getLog(TakeScreenshotResponseCommand.class);

    /** the message */
    private TakeScreenshotResponseMessage m_message;

    /** the result node */
    private TestResultNode m_testResultNode = null;

    /**
     * constructor
     * 
     * @param resultNode
     *            the result node
     */
    public TakeScreenshotResponseCommand(TestResultNode resultNode) {
        m_testResultNode = resultNode;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        m_testResultNode.setScreenshot(getMessage().getScreenshot().getData());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public TakeScreenshotResponseMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (TakeScreenshotResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}
