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
import org.eclipse.jubula.client.core.persistence.GeneralStorage;

/**
 * @author BREDEX GmbH
 * @created Oct 27, 2006
 */
public class SaveProjectAsBP extends AbstractActionBP {

    /**
     * <code>instance</code>single instance
     */
    private static SaveProjectAsBP instance = null;

    /**
     * <code>m_ProjectLoadedListener</code> listener for when a project is
     * loaded
     */
    private IProjectLoadedListener m_projectLoadedListener = 
        new IProjectLoadedListener() {
            /**
             * @param state the new state of the server connection
             */
            @SuppressWarnings("synthetic-access") 
            public void handleProjectLoaded() {
                setEnabledStatus();
            }

        };

    /**
     * <code>m_dataChangedListener</code> listener for when project data 
     * changes; specifically, we are listening in case the current project 
     * is deleted
     */
    private IDataChangedListener m_dataChangedListener = 
        new IDataChangedListener() {
            /**
             * @param state the new state of the server connection
             */
            @SuppressWarnings("synthetic-access") 
            public void handleDataChanged(IPersistentObject po, 
                               DataState dataState, 
                               UpdateState updateState) {
                setEnabledStatus();
            }
        };

    /**
     * private constructor
     */
    private SaveProjectAsBP() {
        DataEventDispatcher.getInstance()
            .addProjectLoadedListener(m_projectLoadedListener, true);
        DataEventDispatcher.getInstance()
            .addDataChangedListener(m_dataChangedListener, true);
        
        setEnabledStatus();
    }
    
    /**
     * @return the single instance
     */
    public static SaveProjectAsBP getInstance() {
        if (instance == null) {
            instance = new SaveProjectAsBP();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return !isCurrentProjectNull();
    }

    /**
     * @return true if the current project is null
     */    
    private boolean isCurrentProjectNull() {
        return (GeneralStorage.getInstance().getProject() == null);
    }

}
