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
package org.eclipse.jubula.tools.utils;

import org.eclipse.jubula.tools.constants.StringConstants;

/**
 * utility class for performance measurement
 *
 * @author BREDEX GmbH
 * @created 22.01.2007
 */
public class PerformUtils {
    
    /**
     * <code>m_startTime</code> start time for Timer
     */
    private long m_startTime = 0;
    /**
     * <code>m_endTime</code> end time for Timer
     */
    private long m_endTime = 0;

    /**
     * starts the time measurement
     */
    public void startTimer() {
        m_startTime = System.currentTimeMillis();
    }
    
    /**
     * finishs the time measurement
     */
    public void stopTimer() {
        m_endTime = System.currentTimeMillis();
    }
    
    /**
     * prints the elapsed time in ms
     */
    public void getTime() {
        
        getTime(StringConstants.EMPTY);
    }
    
    /**
     * prints the elapsed time in ms together with an info message
     * @param msg info message
     */
    public void getTime(String msg) {
        if (m_startTime != 0 && m_endTime != 0) {
            System.out.println(msg + ": " + (m_endTime - m_startTime)); //$NON-NLS-1$
        }
    }
    
    /**
     * @param msg message to print to begin of measurement
     */
    public void printStartMsg(String msg) {
        System.out.println(msg);
    }

    /**
     * resets the start and end time
     */
    public void resetTimer() {
        m_startTime = 0;
        m_endTime = 0;
    }
    

}
