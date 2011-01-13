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
package org.eclipse.jubula.communication.message;


import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created 13.08.2010
 */
public class SendMonitoringReportMessage extends Message {
        
    /** data */
    private byte[] m_data;
    
    
    /** default constructor 
     * @deprecated don't use the defaul constructor 
     * */
    public SendMonitoringReportMessage() {
        //for contruction
    }
    
    /**
     * constructor to build the message
     * @param data data 
     */
    public SendMonitoringReportMessage(byte[] data) {
    
        this.m_data = data;
       
                
    }
        
    /**
     * {@inheritDoc}
     */    
    public String getCommandClass() {
      
        return CommandConstants.GET_MONITORING_REPORT_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
       
        return 1.0;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return m_data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        m_data = data;
    }

}
