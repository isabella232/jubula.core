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

import java.util.ConcurrentModificationException;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.businessprocess.CompletenessGuard;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ILanguageChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.osgi.util.NLS;


/**
 * @author BREDEX GmbH
 * @created 12.03.2007
 */
public class CompletenessBP implements 
    IDataChangedListener, IProjectLoadedListener, ILanguageChangedListener {
    
    /** this instance */
    private static CompletenessBP instance; 
    
    /**
     * private constructor
     */
    private CompletenessBP() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addDataChangedListener(this, false);
        ded.addLanguageChangedListener(this, false);
        ded.addProjectLoadedListener(this, false);
    }

    /**
     * @return the ComponentNamesList
     */
    public static CompletenessBP getInstance() {
        if (instance == null) {
            instance = new CompletenessBP();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public void handleDataChanged(
        IPersistentObject po, 
        DataState dataState, 
        UpdateState updateState) {
        
        // update only when global changes
        switch (updateState) {
            case onlyInEditor :
                return;
            case notInEditor :
            case all :
            default :
                break;
        }

        // skip updates in several cases
        switch (dataState) {
            case Renamed :
            case Deleted :
                return;
            case Added :
                if (!(po instanceof IProjectPO)
                    && !(po instanceof ITestSuitePO)) {
                    return;
                }
            default :
                break;
        }
        INodePO root = GeneralStorage.getInstance().getProject();
        
        // manipulating CategoryPOs does not affect completeness
        if (po instanceof ICategoryPO) {
            return;
        } else if (po instanceof ISpecTestCasePO) {
            // you could change parameters in a SpecTestCase used in TS
            if (dataState != DataState.StructureModified) {
                return;
            }
        } else if (po instanceof ITestSuitePO) {
            // check only changed test suite
            root = (INodePO)po;
        } else if (po instanceof IAUTMainPO || po instanceof IObjectMappingPO) {
            
            // only object-mapping has to be checked again
            CompletenessGuard.checkOM(root);
            fireCompletenessCheckFinished();
            return;
        }
        
        Locale wl = WorkingLanguageBP.getInstance().getWorkingLanguage();
        runCheck(false, root, wl);
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        /* At this time he data is not completely loaded from the database.
         * Since hibernate is not threadsafe the checks (which will load
         * all data) can't be run in a job, so checkProject must be called
         * with runInJob==false.
         */
        checkProject(false); 
    }

    /**
     * checks the project regardless of user preferences
     * @param runInJob if true submit the check to a Job
     */
    public void checkProject(boolean runInJob) {
        final INodePO root = GeneralStorage.getInstance().getProject();
        if (root != null) {
            final Locale wl = WorkingLanguageBP.getInstance()
                    .getWorkingLanguage();
            runCheck(runInJob, root, wl);
        }
    }
    
    /**
     * @param runInJob
     *            if true submit the check to a Job
     * @param root
     *            top node to start check
     * @param wl
     *            locale to use
     */
    private void runCheck(boolean runInJob, final INodePO root, 
        final Locale wl) {
        if (runInJob) {
            final String jobName = NLS.bind(Messages.UIJobRunCompletenessCheck,
                    new String[] { root.getName() });
            Job job = new Job(jobName) {
                @SuppressWarnings("synthetic-access")
                public IStatus run(IProgressMonitor monitor) {
                    IStatus result = Status.OK_STATUS;
                    monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                    try {
                        CompletenessGuard.checkAll(wl, root);
                        fireCompletenessCheckFinished();
                        monitor.done();
                    } catch (ConcurrentModificationException e) {
                        result = Status.CANCEL_STATUS;
                        monitor.setCanceled(true);
                        runCheck(true, root, wl);
                    }
                    monitor.done();
                    return result;
                }
            };
            job.schedule();
        } else {
            CompletenessGuard.checkAll(wl, root);
            fireCompletenessCheckFinished();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleLanguageChanged(Locale locale) {
        INodePO root = GeneralStorage.getInstance().getProject();
        Locale wl = WorkingLanguageBP.getInstance().getWorkingLanguage();
        CompletenessGuard.checkTD(wl, root);
        fireCompletenessCheckFinished();
    }
    
    /**
     * Notifies that the check is finished.
     */
    private void fireCompletenessCheckFinished() {
        DataEventDispatcher.getInstance().fireCompletenessCheckFinished();
    }
    
}
