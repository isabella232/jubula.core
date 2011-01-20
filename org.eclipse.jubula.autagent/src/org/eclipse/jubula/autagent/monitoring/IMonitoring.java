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
package org.eclipse.jubula.autagent.monitoring;

import java.util.Map;

import org.eclipse.jubula.tools.objects.IMonitoringValue;

/**
 * This interface contains all methods which will be called during monitoring execution.
 *  
 * @author BREDEX GmbH
 * @created 20.08.2010
 */
public interface IMonitoring {  
    
    /**
     * This method is creating the agent string for starting the AUT with
     * a monitoring agent. The AUT configuration map for the given AUT should be saved
     * in the MonitoringDataManager. Any further data read or input should
     * be done by using this manager. Note that this method will be called every
     * time a monitored application will be restarted. 
     * 
     * @return A String containing all necessary informations to launch a AUT
     * with a monitoring agent. This String is added to the _JAVA_OPTIONS 
     * environment variable 
     */ 
   
    public String createAgent();
    
    /**
     * Use this method to get data form the profiling agent. This method will
     * be called
     * 
     * @return The value of the String will be displayed in the TestResultSummaryView in the
     * "Measured Value Column"
     */
    
    public Map<String, IMonitoringValue> getMonitoringData();   
    /**
     * this method for report generation. It will be called a last.
     * @return If the profiling agent supports a report generation, use this method
     * to implement this functionality. The byte[] will be stored in the database.
     * The byte[] must be a ZIP file otherwise it can not be imported into client workspace
     */
    
    public byte[] buildMonitoringReport();
    /**
     * This Method will be executed, when AUT restart is performed.
     */
    public void autRestartOccurred();
    /**
     * to provide a reset of monitoring data.
     */
    public void resetMonitoringData();
    /** sets the autId
     * @param autId the autId to set */
    public void setAutId(String autId);
   
    
}
