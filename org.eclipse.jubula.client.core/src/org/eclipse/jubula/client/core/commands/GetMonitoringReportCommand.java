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
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendMonitoringReportMessage;
import org.eclipse.jubula.tools.constants.MonitoringConstants;


/** 
 * @author BREDEX GmbH
 * @created 13.08.2010
 */
public class GetMonitoringReportCommand implements ICommand {

    /** The Logger */
    private static final Log LOG = LogFactory
            .getLog(GetMonitoringReportCommand.class);

    /** The message from the agent, containing the report */
    private SendMonitoringReportMessage m_message;    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        TestResult result = TestResultBP.getInstance().getResultTestModel();
        byte[] report = m_message.getData();
        if (report == null) {
            result.setReportData(MonitoringConstants.EMPTY_REPORT);
        } else {
            result.setReportData(m_message.getData());
        }
          
        return null;    
    }
    /**
     * @param message
     *            the message to set
     */
    public void setMessage(Message message) {
        m_message = (SendMonitoringReportMessage)message;
    }
    /**
     * @return the message
     */
    public Message getMessage() {
        return m_message;
    }
    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}
