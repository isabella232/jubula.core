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
 * @created Nov 8, 2006
 */
public class OMStopMappingModeBP extends AbstractActionBP {

    /** single instance */
    private static AbstractActionBP instance = null;

    /** is the Object Mapping Mode running */
    private boolean m_isOMRunning;
    
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
                        m_isOMRunning = false;
                        break;
                    default:
                        Assert.notReached(Messages.UnhandledAutState);
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

    /**
     * Private constructor
     */
    private OMStopMappingModeBP() {
        m_isOMRunning = false;
        
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.addOMStateListener(m_objectMappingStateListener, true);
        dispatch.addAutStateListener(m_autStateListener, true);
        dispatch.addRecordModeStateListener(m_recordModeStateListener, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return m_isOMRunning;
    }

    /**
     * dummy doc see OMStopMappingMode
     * @return AbstractActionBP
     */
    public static AbstractActionBP getInstance() {
        if (instance == null) {
            instance = new OMStopMappingModeBP();
        }
        return instance;
    }

}
