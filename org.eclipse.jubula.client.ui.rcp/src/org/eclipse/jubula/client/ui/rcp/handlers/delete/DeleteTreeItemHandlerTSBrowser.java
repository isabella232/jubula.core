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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteCatHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteExecHandle;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestJobEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestSuiteEditor;
import org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTreeItemHandlerTSBrowser 
        extends AbstractDeleteTreeItemHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        
        if (activePart instanceof TestSuiteBrowser
                && currentSelection instanceof IStructuredSelection) {
            
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)currentSelection;

            try {
                if (confirmDelete(structuredSelection)) {
                    deleteExecItems(structuredSelection);
                }
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleGDProjectDeletedException();
            }
        }
        

        return null;
    }

    
    /**
     * Deletes the given selection.
     * 
     * @param selection
     *            the selection to delete.
     * @throws PMSaveException
     *             if the save operation failed
     * @throws PMAlreadyLockedException
     *             in case of locked item to delete
     * @throws PMException
     *             in case of failed rollback
     * @throws ProjectDeletedException
     *             if the project was deleted in another instance
     */
    private void deleteExecItems(IStructuredSelection selection) 
        throws PMSaveException, PMAlreadyLockedException, PMException, 
            ProjectDeletedException {
        IEditorReference[] editors = Plugin.getActivePage()
                .getEditorReferences();

        Iterator iter = selection.iterator();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>();
        List<INodePO> deletedNodes = new ArrayList<INodePO>();
        Object selElement = selection.getFirstElement();
        if (selElement instanceof ITestSuitePO
                || selElement instanceof ITestJobPO
                || selElement instanceof ICategoryPO) {
            while (iter.hasNext()) {
                INodePO execTS = (INodePO)iter.next();
                AbstractCmdHandle cmd = null;
                if (execTS instanceof ITestSuitePO) {
                    ITestSuitePO testSuite = (ITestSuitePO)execTS;
                    List<IRefTestSuitePO> refTs = NodePM
                        .getInternalRefTestSuites(
                            testSuite.getGuid(), 
                            testSuite.getParentProjectId());
                    if (refTs.size() > 0) {
                        ErrorHandlingUtil.createMessageDialog(
                                MessageIDs.I_REUSED_TS);
                        return;
                    }
                    
                    closeEditors(project, testSuite, editors);
                    deletedNodes.add(testSuite);
                    cmd = new DeleteExecHandle(testSuite);
                } else if (execTS instanceof ITestJobPO) {
                    ITestJobPO testjob = (ITestJobPO)execTS;
                    closeEditors(testjob, editors);
                    deletedNodes.add(testjob);
                    cmd = new DeleteExecHandle(testjob);
                } else if (execTS instanceof ICategoryPO) {
                    ICategoryPO category = (ICategoryPO)execTS;
                    deletedNodes.add(category);
                    cmd = new DeleteCatHandle(category);
                }
                cmds.add(cmd);
            }
        }
        MultipleNodePM.getInstance().executeCommands(cmds);

        // FIXME : we need a concept to execute just one gui update
        for (INodePO node : deletedNodes) {
            DataEventDispatcher.getInstance().fireDataChangedListener(node,
                    DataState.Deleted, UpdateState.all);
        }
    }

    /**
     * Closes all given IEditorReferences
     * 
     * @param testjob
     *            the Test Job
     * @param editors
     *            the IEditorReferences
     */
    private void closeEditors(ITestJobPO testjob, IEditorReference[] editors) {
        for (IEditorReference editor : editors) {
            if (editor.getPart(true) instanceof TestJobEditor) {
                TestJobEditor tjEditor = (TestJobEditor)editor.getPart(true);
                if (tjEditor.getEditorInput().getName().endsWith(
                        testjob.getName())) {
                    tjEditor.getEditorSite().getPage().closeEditor(tjEditor,
                            true);
                }
            }
        }
    }

    /**
     * Closes all given IEditorReferences
     * @param project the project
     * @param execTS the TestSuite
     * @param editors the IEditorReferences
     */
    private void closeEditors(IProjectPO project, 
        ITestSuitePO execTS, IEditorReference[] editors) {
        
        for (IEditorReference editor : editors) {
            if (editor.getPart(true) instanceof TestSuiteEditor) {
                TestSuiteEditor tsEditor = (TestSuiteEditor)editor
                    .getPart(true);
                if (tsEditor.getEditorInput().getName().endsWith(
                    execTS.getName())) {
    
                    tsEditor.getEditorSite().getPage().closeEditor(
                        tsEditor, true);
                }
            }
            if (editor.getPart(true) instanceof ObjectMappingMultiPageEditor) {
                int autCounter = 0;
                ObjectMappingMultiPageEditor omEditor = 
                    (ObjectMappingMultiPageEditor)editor.getPart(true);
                IAUTMainPO omAut = omEditor.getAut();
                List<ITestSuitePO> tsList = TestSuiteBP
                        .getListOfTestSuites(project);
                for (ITestSuitePO ts : tsList) {
                    if (ts.getAut() != null && ts.getAut().equals(omAut)) {
                        autCounter++;
                    }
                }
                if (execTS.getAut() != null && execTS.getAut().equals(omAut)) {
                    autCounter--;
                }
                if (autCounter == 0) {
                    omEditor.getEditorSite().getPage().closeEditor(omEditor, 
                        true);
                }
            }
        }
    }
    
}
