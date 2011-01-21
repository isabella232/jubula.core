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
package org.eclipse.jubula.client.ui.businessprocess;

import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IAutStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IRecordModeStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ITestSuiteStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.exception.Assert;


/**
 * @author BREDEX GmbH
 * @created Nov 8, 2006
 */
public class OMStartMappingModeBP extends AbstractActionBP {

    /** single instance */
    private static AbstractActionBP instance = null;

    /** is the Object Mapping Mode running */
    private boolean m_isOMRunning;
    
    /** is a Test Suite running */
    private boolean m_isTestSuiteRunning;
    
    /** is an AUT running */
    private boolean m_isAutRunning;
    
    /** listener for AUT state changes */
    private IAutStateListener m_autStateListener = 
        new IAutStateListener() {
        
            @SuppressWarnings("synthetic-access") 
            public void handleAutStateChanged(AutState state) {
                if (state == AutState.notRunning) {
                    m_isAutRunning = false;
                    // Stopping AUT server implicitly stops Object Mapping mode
                    // and Test mode
                    m_isOMRunning = false;
                    m_isTestSuiteRunning = false;
                } else {
                    m_isAutRunning = true;
                }
                
                setEnabledStatus();
                
            }
        };

    /**
     * <code>m_recordModeStateListener</code> listener for modification of Record Mode
     */
    private IRecordModeStateListener m_recordModeStateListener = 
        new IRecordModeStateListener() {
        
            @SuppressWarnings("synthetic-access") 
            public void handleRecordModeStateChanged(RecordModeState state) {
                switch (state) {
                    case running:
                        // Starting of Observation mode implicitly stops the
                        // Object Mapping mode
                        m_isOMRunning = false;
                        break;
                    case notRunning:
                        break;
                    default:
                        Assert.notReached(Messages.UnsupportedRecordModeState);
                }
                setEnabledStatus();
            }
        };

        /** listener for OM mode state changes */
    private ITestSuiteStateListener m_testSuiteStateListener = 
        new ITestSuiteStateListener() {
            
            @SuppressWarnings("synthetic-access") 
            public void handleTSStateChanged(TestExecutionEvent event) {
                int state = event.getState();
                m_isTestSuiteRunning = 
                    (state == TestExecutionEvent.TEST_EXEC_START
                  || state == TestExecutionEvent.TEST_EXEC_PAUSED
                  || state == TestExecutionEvent.TEST_EXEC_RESTART);
                
                setEnabledStatus();
            }
        };

    /** listener for OM mode state changes */
    private IOMStateListener m_objectMappingStateListener = 
        new IOMStateListener() {
        
            @SuppressWarnings("synthetic-access") 
            public void handleOMStateChanged(OMState state) {
                m_isOMRunning = (state == OMState.running);
                
                setEnabledStatus();
            }
        };

    /**
     * Private constructor
     */
    private OMStartMappingModeBP() {
        m_isOMRunning = false;
        m_isAutRunning = false;
        
        DataEventDispatcher dispatcher = DataEventDispatcher.getInstance();
        dispatcher.addOMStateListener(m_objectMappingStateListener, true);
        dispatcher.addAutStateListener(m_autStateListener, true);
        dispatcher.addTestSuiteStateListener(m_testSuiteStateListener, true);
        dispatcher.addRecordModeStateListener(m_recordModeStateListener, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return !m_isOMRunning 
            && m_isAutRunning
            && !m_isTestSuiteRunning;
    }

    /**
     * dummy doc see OMStartMappingMode
     * @return AbstractActionBP
     */
    public static AbstractActionBP getInstance() {
        if (instance == null) {
            instance = new OMStartMappingModeBP();
        }
        return instance;
    }

}
