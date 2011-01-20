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
package org.eclipse.jubula.client.ui.sourceprovider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ILanguageChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectPropertiesModifyListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerConnectionListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.businessprocess.StartAutBP;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the availability/startability of AUTs.
 *  1. available AUTs and AUT Configurations within the active project.
 *  2. available AUTs for the current working language.
 *  3. available AUT Configurations for the current (most recently connected)
 *     AutStarter.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class AvailableAutSourceProvider extends AbstractJBSourceProvider 
        implements ILanguageChangedListener, IServerConnectionListener, 
                   IProjectPropertiesModifyListener, 
                   IProjectLoadedListener, IDataChangedListener {

    /** 
     * ID of variable that indicates whether at least one AUT is 
     * currently available/startable
     */
    public static final String IS_AUT_AVAILABLE = 
        "org.eclipse.jubula.client.ui.variable.isAutAvailable"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public AvailableAutSourceProvider() {
        DataEventDispatcher dispatcher = DataEventDispatcher.getInstance();
        dispatcher.addLanguageChangedListener(this, true);
        dispatcher.addServerConnectionListener(this, true);
        dispatcher.addProjectPropertiesModifyListener(this, true);
        dispatcher.addProjectLoadedListener(this, true);
        dispatcher.addDataChangedListener(this, true);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher dispatcher = DataEventDispatcher.getInstance();
        dispatcher.removeLanguageChangedListener(this);
        dispatcher.removeServerConnectionListener(this);
        dispatcher.removeProjectPropertiesModifyListener(this);
        dispatcher.removeProjectLoadedListener(this);
        dispatcher.removeDataChangedListener(this);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(IS_AUT_AVAILABLE, isAutAvailable());
        return values;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String [] {IS_AUT_AVAILABLE};
    }

    /**
     * {@inheritDoc}
     */
    public void handleLanguageChanged(Locale locale) {
        fireSourceChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleServerConnStateChanged(ServerState state) {
        fireSourceChanged();
    }

    /**
     * Fires a source changed event for <code>IS_AUT_AVAILABLE</code>.
     */
    private void fireSourceChanged() {
        final String jobName = I18n.getString("UIJob.resolvingStartableAuts"); //$NON-NLS-1$
        Job resolveStartableAUTs = new Job(jobName) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                gdFireSourceChanged(ISources.WORKBENCH, IS_AUT_AVAILABLE,
                        isAutAvailable());
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        JobUtils.executeJob(resolveStartableAUTs, null);
    }
    
    /**
     * 
     * @return <code>true</code> if at least one AUT is currently 
     *         available/startable. Otherwise <code>false</code>. 
     */
    private boolean isAutAvailable() {
        return !StartAutBP.getInstance().getAllAUTs().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectPropsChanged() {
        fireSourceChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        fireSourceChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleDataChanged(IPersistentObject po, DataState dataState,
            UpdateState updateState) {

        if (po instanceof IProjectPO && dataState == DataState.Deleted) {
            fireSourceChanged();
        }
    }
}
