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
package org.eclipse.jubula.client.ui.handlers.existing.testcase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.TestCaseTreeDialog;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author BREDEX GmbH
 * @created 18.02.2009
 */
public abstract class AbstractReferenceExistingTestCase 
    extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        final AbstractTestCaseEditor tce = 
            (AbstractTestCaseEditor)Plugin.getActiveEditor();
        if (tce.getEditorHelper().requestEditableState() 
                == JBEditorHelper.EditableState.OK) {
            final INodePO editorNode = (INodePO)tce.getEditorHelper()
                    .getEditSupport().getWorkVersion();
            if (!(tce.getTreeViewer().getSelection() 
                    instanceof IStructuredSelection)) {
                return null;
            }
            INodePO guiNode = (INodePO)((IStructuredSelection)tce
                    .getTreeViewer().getSelection()).getFirstElement();
            if (guiNode == null) { // check for existing selection
                return null;
            }
            final Integer index = editorNode.indexOf(guiNode);
            ISelectionListener listener = new ISelectionListener() {
                public void selectionChanged(IWorkbenchPart part,
                        ISelection selection) {
                    if (!(selection instanceof IStructuredSelection)) {
                        return;
                    }
                    List<Object> selectedElements = 
                        ((IStructuredSelection)selection).toList();
                    Collections.reverse(selectedElements);
                    Iterator iter = selectedElements.iterator();
                    List<IExecTestCasePO> addedElements = 
                        new ArrayList<IExecTestCasePO>();
                    try {
                        while (iter.hasNext()) {
                            ISpecTestCasePO specTcToInsert = 
                                (ISpecTestCasePO)iter.next();
                            try {
                                addedElements.add(
                                    TestCaseBP.addReferencedTestCase(
                                        tce.getEditorHelper().getEditSupport(),
                                        editorNode, specTcToInsert, index));
                            } catch (PMException e) {
                                NodeEditorInput inp = (NodeEditorInput)tce
                                        .getAdapter(NodeEditorInput.class);
                                INodePO inpNode = inp.getNode();
                                PMExceptionHandler
                                        .handlePMExceptionForMasterSession(e);
                                tce.reOpenEditor(inpNode);
                            }
                        }
                        tce.getEditorHelper().getEditSupport()
                                .lockWorkVersion();
                        tce.getEditorHelper().setDirty(true);
                        tce.setSelection(
                                new StructuredSelection(addedElements));
                    } catch (PMException e1) {
                        PMExceptionHandler.handlePMExceptionForEditor(e1, tce);
                    }
                }
            };
            ISpecTestCasePO specTC = null;
            if (editorNode instanceof ISpecTestCasePO) {
                specTC = (ISpecTestCasePO)editorNode;
            }
            TestCaseTreeDialog dialog = new TestCaseTreeDialog(Plugin
                    .getShell(), specTC, SWT.MULTI, 
                    TestCaseTreeDialog.TESTCASE);
            dialog.addSelectionListener(listener);
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(),
                    ContextHelpIds.TESTCASE_ADD_EXISTING);
            dialog.open();
            dialog.removeSelectionListener(listener);
        }
        return null;
    }
    
}
