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

import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.ActivateApplicationMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * @author BREDEX GmbH
 * @created 02.01.2007
 * 
 */
public abstract class AbstractActivateApplicationCommand implements ICommand {
    /** Logger */
    private static final AutServerLogger LOG =
        new AutServerLogger(AbstractActivateApplicationCommand.class);

    /** message */
    private ActivateApplicationMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        try {
            IRobot robot = getRobot();
            robot.activateApplication(CompSystemConstants.AAM_AUT_DEFAULT);
        } catch (Exception exc) {
            LOG.error("error in activation of the AUT", exc); //$NON-NLS-1$
        }
        return null;
    }
    
    /**
     * @return the toolkit dependent robot
     */
    protected abstract IRobot getRobot();
    
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
        m_message = (ActivateApplicationMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}