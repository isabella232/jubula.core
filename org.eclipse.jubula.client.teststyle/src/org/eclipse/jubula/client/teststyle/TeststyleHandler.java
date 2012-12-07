/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.compcheck.ProblemPropagator;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.CheckCont;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;
import org.eclipse.jubula.client.teststyle.gui.TeststyleProblemAdder;
import org.eclipse.jubula.client.teststyle.gui.decoration.DecoratorHandler;
import org.eclipse.jubula.client.teststyle.problems.ProblemCont;


/**
 * Initialize and handles the teststyles and its definitions.
 * 
 * @author marcell
 * 
 */
public final class TeststyleHandler implements IDataChangedListener,
        IProjectLoadedListener, IProjectStateListener {

    /** singleton */
    private static TeststyleHandler instance;

    /**
     * Private constructor of the singleton
     */
    private TeststyleHandler() {
        DataEventDispatcher.getInstance().addProjectLoadedListener(this, false);
        addToListener();
    }

    /**
     * @return the singleton instance
     */
    public static TeststyleHandler getInstance() {
        if (instance == null) {
            instance = new TeststyleHandler();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        if (GeneralStorage.getInstance().getProject() == null) {
            return; // if there is no project, don't proceed
        }
        ExtensionHelper.initCheckConfiguration();
        checkEverything();
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        if (!isEnabled()) {
            return;
        }
        for (DataChangedEvent e : events) {
            handleChangedPo(e.getPo(), e.getDataState(), e.getUpdateState());
        }
        refresh();
        addTeststyleProblems();
        ProblemPropagator.getInstance().propagate();
    }
    
    /**
     * @param po changed persistent object
     * @param dataState kind of modification
     * @param updateState determines the parts to update
     */
    private void handleChangedPo(IPersistentObject po, DataState dataState,
        UpdateState updateState) {
        // FIXME mbs Need a event for closing a project
        // Clean up first
        ProblemCont.getInstance().remove(po);
        switch (dataState) {
            case Renamed: // fall through
            case Added: // fall through
            case StructureModified: // fall through
            case ReuseChanged:
                check(po);
                break;
            case Deleted: // fall through
            default:
                break;
        }
        // always check the project after each change
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            ProblemCont.getInstance().remove(project);
            check(project);
        }
        if (po instanceof ISpecTestCasePO || po instanceof ITestSuitePO
                || po instanceof ITestJobPO) {
            INodePO node = (INodePO)po;
            Iterator<INodePO> iter = node.getNodeListIterator();
            while (iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof IParamNodePO) {
                    IParamNodePO paramNode = (IParamNodePO)next;
                    handleChangedPo(paramNode, dataState, updateState);
                }
            }
            if (node instanceof ISpecTestCasePO) {
                ISpecTestCasePO specTc = (ISpecTestCasePO)node;
                Collection<IEventExecTestCasePO> c = specTc
                        .getAllEventEventExecTC();
                for (IEventExecTestCasePO eh : c) {
                    handleChangedPo(eh, dataState, updateState);
                }
            }
        }
    }

    /**
     * This method checks the Object obj with every check in the contexts of
     * this check for violation and decorates it approriatly.
     * 
     * @param obj
     *            The object that should be checked.
     */
    public void check(Object obj) {        
        // gather all checks for this
        BaseContext context = BaseContext.getFor(obj.getClass());
        List<BaseCheck> checks = CheckCont.getChecksFor(context);
        
        // Test the object!
        for (BaseCheck check : checks) {
            if (check.isActive(context) && check.hasError(obj)) {
                if (obj instanceof ITestDataCubePO) {
                    ProblemCont.getInstance().add(
                            ((ITestDataCubePO)obj).getId(), check);
                } else {
                    ProblemCont.getInstance().add(obj, check);
                }
            }
        }
    }

    /**
     * Checks every element with checks of the project for CheckStyle errors.
     */
    public void checkEverything() {
        // Clean up
        ProblemCont.getInstance().clear();
        
        if (isEnabled()) {
            // Check'em all!
            for (BaseContext context : CheckCont.getContexts()) {
                for (Object obj : context.getAll()) {
                    check(obj);
                }  
            }
        }
        
        refresh();
        addTeststyleProblems();
        ProblemPropagator.getInstance().propagate();
    }

    /** */
    private void addTeststyleProblems() {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        final ITreeNodeOperation<INodePO> op = new TeststyleProblemAdder();
        final TreeTraverser traverser = new TreeTraverser(project, op);
        traverser.traverse(true);
    }

    /**
     * Adds the handler to the important listeners.
     */
    public void addToListener() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addDataChangedListener(this, true);
        ded.addProjectStateListener(this);
    }

    /**
     * Removes the handler to the important listeners.
     */
    public void removeFromListener() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeDataChangedListener(this);
        ded.removeProjectStateListener(this);
    }

    /**
     * Starts the whole routine (adds the handler and initialize the extensions
     * with its definitions)
     */
    public void start() {
        // Fill the CheckCont with initChecks
        ExtensionHelper.initChecks();

        // And add the handler to the listener, so that we can use events.
        if (isEnabled()) {
            addToListener();
        }
    }

    /**
     * Stops the whole checkstyle routine.
     */
    public void stop() {
        if (isEnabled()) {
            removeFromListener();
        }
        ProblemCont.getInstance().clear();
    }

    /**
     * Refreshes the decoratos so that they start decorating the available 
     * resources again.
     */
    public void refresh() {
        DecoratorHandler.refresh();
    }
    
    /**
     * @return Is teststyle enabled for this project?
     */
    public boolean isEnabled() {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project == null) {
            return false;
        }
        return project.getProjectProperties().getCheckConfCont().getEnabled();
    }

    /** {@inheritDoc} */
    public void handleProjectStateChanged(ProjectState state) {
        switch (state) {
            case prop_modified:
                checkEverything();
                break;
            case closed:
                ProblemCont.getInstance().clear();
                break;
            case opened:
            default:
                break;
        }
    }
}
