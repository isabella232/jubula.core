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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.TestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;

/**
 * @author BREDEX GmbH
 * @created 27.06.2006
 */
public class NewTestCaseHandlerTCEditor extends AbstractNewHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        final TestCaseEditor tce = (TestCaseEditor)Plugin.getActiveEditor();
        if (!(tce.getTreeViewer().getSelection() 
                instanceof IStructuredSelection)) {
            return null;
        }

        tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
            public void run(IPersistentObject workingPo) {
                INodePO selectedNode = (INodePO)((IStructuredSelection)tce
                        .getTreeViewer().getSelection()).getFirstElement();
                final ISpecTestCasePO editorNode = (ISpecTestCasePO)workingPo;
                InputDialog dialog = new InputDialog(getActiveShell(),
                        Messages.NewTestCaseActionTCTitle,
                        InitialValueConstants.DEFAULT_TEST_CASE_NAME,
                        Messages.NewTestCaseActionTCMessage,
                        Messages.RenameActionTCLabel,
                        Messages.RenameActionTCError,
                        Messages.NewTestCaseActionDoubleTCName,
                        IconConstants.NEW_TC_DIALOG_STRING,
                        Messages.NewTestCaseActionTCShell, false);
                
                dialog.setHelpAvailable(true);
                dialog.create();
                Plugin.getHelpSystem().setHelp(dialog.getShell(),
                        ContextHelpIds.DIALOG_TC_ADD_NEW);
                DialogUtils.setWidgetNameForModalDialog(dialog);
                dialog.open();
                ISpecTestCasePO newSpecTC = null;
                if (Window.OK == dialog.getReturnCode()) {
                    String tcName = dialog.getName();
                    INodePO parent = ISpecObjContPO.TCB_ROOT_NODE;
                    try {
                        newSpecTC = TestCaseBP.createNewSpecTestCase(tcName,
                                parent, null);
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(
                                newSpecTC, DataState.Added, UpdateState.all);
                    } catch (PMException e) {
                        PMExceptionHandler.handlePMExceptionForMasterSession(e);
                    } catch (ProjectDeletedException e) {
                        PMExceptionHandler.handleProjectDeletedException();
                    }
                }
                if (newSpecTC != null) {
                    Integer index = null;
                    if (selectedNode instanceof IExecTestCasePO) {
                        index = getPositionToInsert(
                                editorNode, (IExecTestCasePO)selectedNode);
                    }

                    try {
                        ISpecTestCasePO workNewSpecTC = (ISpecTestCasePO) tce
                                .getEditorHelper().getEditSupport()
                                .createWorkVersion(newSpecTC);
                        IExecTestCasePO newExecTC = TestCaseBP
                                .addReferencedTestCase(tce.getEditorHelper()
                                        .getEditSupport(), editorNode,
                                        workNewSpecTC, index);

                        tce.getEditorHelper().setDirty(true);
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(
                                newExecTC, DataState.Added, UpdateState.all);
                    } catch (PMException e) {
                        PMExceptionHandler.handlePMExceptionForEditor(e, tce);
                    }
                }
            }
        });
        return null;
    }
    
    /**
     * @param workNode
     *            the workversion of the current specTC
     * @param selectedNode
     *            the currently selected node
     * @return the position to add
     */
    public static Integer getPositionToInsert(INodePO workNode,
            IExecTestCasePO selectedNode) {

        int positionToAdd = workNode.indexOf(selectedNode) + 1;
        if (Plugin.getDefault().getPreferenceStore()
                .getBoolean(Constants.NODE_INSERT_KEY)) {

            positionToAdd = workNode.getUnmodifiableNodeList().size() + 1;
        }
        return positionToAdd;
    }
}