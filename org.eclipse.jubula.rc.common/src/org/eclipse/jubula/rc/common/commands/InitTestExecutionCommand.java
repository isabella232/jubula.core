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
import org.eclipse.jubula.communication.message.InitTestExecutionMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.RobotConfiguration;


/**
 * @author BREDEX GmbH
 * @created 07.02.2006
 * 
 */
public class InitTestExecutionCommand implements ICommand {
    
    /** Logger */
    private static final Log LOG =
        LogFactory.getLog(InitTestExecutionCommand.class);
    
    /** message */
    private InitTestExecutionMessage m_message;
    
    /**
     * {@inheritDoc}
     * @return
     */
    public Message execute() {
        RobotConfiguration.getInstance().setDefaultActivationMethod(
            m_message.getDefaultActivationMethod());
        
        try {
            IRobot robot = AUTServer.getInstance().getRobot();
            robot.activateApplication(RobotConfiguration.getInstance()
                .getDefaultActivationMethod());
        } catch (Exception exc) {
            LOG.error("error in activation of the AUT", exc); //$NON-NLS-1$
        }
        return null;
    }
    
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
        m_message = (InitTestExecutionMessage)message;
    }
    
    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}
