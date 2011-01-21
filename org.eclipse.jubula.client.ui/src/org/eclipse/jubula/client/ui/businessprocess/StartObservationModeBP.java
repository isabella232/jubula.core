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
public class StartObservationModeBP extends AbstractActionBP {

    /** single instance */
    private static AbstractActionBP instance = null;

    /** is the Record Mode running */
    private boolean m_isRecordModeRunning;
    
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
                    // Stopping AUT server implicitly stops Observation mode
                    // and Test mode
                    m_isRecordModeRunning = false;
                    m_isTestSuiteRunning = false;
                } else {
                    m_isAutRunning = true;
                }
                
                setEnabledStatus();
                
            }
        };

    /** listener for Test mode state changes */
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

    /** listener for Record mode state changes */
    private IRecordModeStateListener m_recordModeStateListener = 
        new IRecordModeStateListener() {
        
            @SuppressWarnings("synthetic-access") 
            public void handleRecordModeStateChanged(RecordModeState state) {
                m_isRecordModeRunning = (state == RecordModeState.running);
                
                setEnabledStatus();
            }
        };
        
    /** listener for OM mode state changes */
    private IOMStateListener m_objectMappingStateListener = 
        new IOMStateListener() {
        
            @SuppressWarnings("synthetic-access") 
            public void handleOMStateChanged(OMState state) {
                switch (state) {
                    case running:
                        // Starting of Object Mapping mode implicitly stops the
                        // Observation mode
                        m_isRecordModeRunning = false;
                        break;
                    case notRunning:
                        break;
                    default:
                        Assert.notReached(Messages.UnsupportedRecordModeState);
                }
                setEnabledStatus();
            }
        };
    
    /**
     * Private constructor
     */
    private StartObservationModeBP() {
        m_isAutRunning = false;
        m_isRecordModeRunning = false;
        m_isTestSuiteRunning = false;
        
        DataEventDispatcher dispatcher = DataEventDispatcher.getInstance();
        dispatcher.addAutStateListener(m_autStateListener, true);
        dispatcher.addRecordModeStateListener(m_recordModeStateListener, true);
        dispatcher.addTestSuiteStateListener(m_testSuiteStateListener, true);
        dispatcher.addOMStateListener(m_objectMappingStateListener, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return m_isAutRunning
            && !m_isRecordModeRunning
            && !m_isTestSuiteRunning;
    }

    /**
     * @return single instance
     */
    public static AbstractActionBP getInstance() {
        if (instance == null) {
            instance = new StartObservationModeBP();
        }
        return instance;
    }

}
