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

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IAutStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IRecordModeStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.exception.Assert;


/**
 * @author BREDEX GmbH
 * @created Nov 9, 2006
 */
public class StopObservationModeBP extends AbstractActionBP {

    /** single instance */
    private static AbstractActionBP instance = null;
    
    /** is Record Mode running */
    private boolean m_isRecordModeRunning;
    
    /**
     * <code>m_autStateListener</code> listener for modification of aut state
     */
    private IAutStateListener m_autStateListener = 
        new IAutStateListener() {
        /**
         * @param state state from AUT
         */
            @SuppressWarnings("synthetic-access") 
            public void handleAutStateChanged(AutState state) {
                switch (state) {
                    case running:
                        break;
                    case notRunning:
                        m_isRecordModeRunning = false;
                        break;
                    default:
                        Assert.notReached(Messages.UnhandledAutState);
                }
                setEnabledStatus();
            }
        };

    /** listener for OM mode state changes */
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
    private StopObservationModeBP() {
        m_isRecordModeRunning = false;
        
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.addRecordModeStateListener(m_recordModeStateListener, true);
        dispatch.addAutStateListener(m_autStateListener, true);
        dispatch.addOMStateListener(m_objectMappingStateListener, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return m_isRecordModeRunning;
    }

    /**
     * dummy doc see StopObservationMode
     * @return AbstractActionBP
     */
    public static AbstractActionBP getInstance() {
        if (instance == null) {
            instance = new StopObservationModeBP();
        }
        return instance;
    }

}
