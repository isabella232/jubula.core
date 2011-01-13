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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.GetMonitoringDataResponseMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.tools.constants.MonitoringConstants;
import org.eclipse.jubula.tools.objects.MonitoringValue;


/**
 * @author BREDEX GmbH
 * @created 07.09.2010
 */
public class GetMonitoringDataResponseCommand implements ICommand {
    /** the logger */
    private static Log log = LogFactory
            .getLog(GetMonitoringDataResponseCommand.class);

    /** the message */
    private GetMonitoringDataResponseMessage m_message;
    /**
     * {@inheritDoc}
     */
    public Message execute() {
               
        TestResult result = TestResultBP.getInstance().getResultTestModel();
        String monitoringId = m_message.getMonitoringId();        
        if (monitoringId == null) {
            result.setMonitoringId(MonitoringConstants.EMPTY_MONITORING_ID);
        } else {
            result.setMonitoringId(m_message.getMonitoringId());
        }           
        Map<String, MonitoringValue> monitoringValue = 
            m_message.getMonitoringValues();      
        if (monitoringValue == null) {
            result.setMonitoringValues(
                    MonitoringConstants.EMPTY_MONITORING_VALUES);
        } else {
            result.setMonitoringValues(m_message.getMonitoringValues());
        }
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
        m_message = (GetMonitoringDataResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$

    }

}
