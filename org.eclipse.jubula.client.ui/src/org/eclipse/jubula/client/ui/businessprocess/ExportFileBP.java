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
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;

/**
 * @author BREDEX GmbH
 * @created Nov 8, 2006
 */
public class ExportFileBP extends AbstractActionBP {

    /** single instance */
    private static AbstractActionBP instance = null;

    /** is the current project null? */
    private boolean m_isProjectNull;
    
    /**
     * {@inheritDoc}
     */
    private IProjectLoadedListener m_projectLoadedListener = 
        new IProjectLoadedListener() {
            
            /**
             * {@inheritDoc}
             */
            @SuppressWarnings("synthetic-access")
            public void handleProjectLoaded() {
                m_isProjectNull = 
                    GeneralStorage.getInstance().getProject() == null;
                setEnabledStatus();
            }

        };

    /**
     * <code>m_currentProjDeletedListener</code>listener for deletion of current project
     */
    private IDataChangedListener m_currentProjDeletedListener =
        new IDataChangedListener() {
        
            @SuppressWarnings("synthetic-access") 
            public void handleDataChanged(IPersistentObject po, 
                DataState dataState, 
                UpdateState updateState) {
                if (updateState == UpdateState.onlyInEditor) {
                    return;
                }
                if (dataState == DataState.Deleted && po instanceof IProjectPO
                    && GeneralStorage.getInstance().getProject() == null) {
                    m_isProjectNull = true;
                    setEnabledStatus();
                }
            }
        };

    /**
     * private constructor
     */
    private ExportFileBP() {
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.addProjectLoadedListener(m_projectLoadedListener, true);
        dispatch.addDataChangedListener(m_currentProjDeletedListener, true);
        m_isProjectNull = true;
    }
    
    /**
     * @return single instance
     */
    public static AbstractActionBP getInstance() {
        if (instance == null) {
            instance = new ExportFileBP();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return !m_isProjectNull;
    }

}
