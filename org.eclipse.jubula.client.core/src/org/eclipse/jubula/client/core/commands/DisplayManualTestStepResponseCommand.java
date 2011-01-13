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

import org.eclipse.jubula.client.core.businessprocess.TestExecution.ManualTestStepCmd;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.DisplayManualTestStepResponseMessage;
import org.eclipse.jubula.communication.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Client-side handling for the result of an attempt to perform the manual test step.
 *
 * @author BREDEX GmbH
 * @created Aug 19, 2010
 */
public class DisplayManualTestStepResponseCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(DisplayManualTestStepResponseCommand.class);
    
    /** the message */
    private DisplayManualTestStepResponseMessage m_message;
    
    /**
     * <code>m_manualTestStepCmd</code>
     */
    private ManualTestStepCmd m_manualTestStepCmd;
    
    /**
     * @param manualTestStepCmd
     *            the manualTestStepCmd
     */
    public DisplayManualTestStepResponseCommand(
            ManualTestStepCmd manualTestStepCmd) {
        m_manualTestStepCmd = manualTestStepCmd;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        m_manualTestStepCmd.setComment(m_message.getComment());
        m_manualTestStepCmd.setStatus(m_message.isStatus());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public DisplayManualTestStepResponseMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (DisplayManualTestStepResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.warn(this.getClass().getName() + " timeout() called"); //$NON-NLS-1$
    }

}
