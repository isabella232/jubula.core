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
package org.eclipse.jubula.client.ui.handlers.rename;

import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.dialogs.EnterTestDataManagerDialog;
import org.eclipse.jubula.client.ui.dialogs.RenameTestDataManagerDialog;
import org.eclipse.jubula.client.ui.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.handlers.AddNewTestDataManagerHandler;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jul 21, 2010
 */
public class RenameTestDataCubeInEditorHandler extends
        AbstractRenameTreeItemHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        if (activePart instanceof CentralTestDataEditor
                && currentSelection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)currentSelection;
            CentralTestDataEditor editor = (CentralTestDataEditor)activePart;
            if (editor.getEditorHelper().requestEditableState() 
                    != EditableState.OK) {
                return null;
            }

            Object firstElement = structuredSelection.getFirstElement();
            if (firstElement instanceof ITestDataCubePO) {
                ITestDataCubePO tdc = (ITestDataCubePO)firstElement;
                String newName = getNewTestDataCubeName(tdc, editor, event);
                if (newName != null) {
                    tdc.setName(newName);
                    editor.getEditorHelper().setDirty(true);
                    DataEventDispatcher ded = DataEventDispatcher.getInstance();
                    ded.fireDataChangedListener(tdc, 
                            DataState.Renamed, UpdateState.onlyInEditor);
                    ded.fireParamChangedListener();
                    ded.firePropertyChanged(false);
                }
            }
        }
        return null;
    }

    /**
     * @param tdc the cube to rename
     * @param editor
     *            the editor
     * @param event
     *            the ExecutionEvent
     * @return a new name for the data cube
     */
    private String getNewTestDataCubeName(ITestDataCubePO tdc,
            CentralTestDataEditor editor, ExecutionEvent event) {
        return openDialog(tdc.getName(), HandlerUtil.getActiveShell(event),
                AddNewTestDataManagerHandler.getSetOfUsedNames(editor));
    }

    /**
     * Opens the "Rename Test Data Set..." dialog.
     * @param initialName the initial name to display during rename
     * @param activeShell
     *            The parent Shell for the dialog.
     * @param setOfUsedNames
     *            a set of already used names
     * @return the name typed into the dialog, or <code>null</code> if the
     *         dialog was cancelled.
     */
    private String openDialog(String initialName, Shell activeShell,
            Set<String> setOfUsedNames) {
        EnterTestDataManagerDialog newNameDialog = 
            new RenameTestDataManagerDialog(
                initialName, activeShell, setOfUsedNames);
        newNameDialog.setHelpAvailable(true);
        newNameDialog.create();
        DialogUtils.setWidgetNameForModalDialog(newNameDialog);
        Plugin.getHelpSystem().setHelp(newNameDialog.getShell(),
                ContextHelpIds.NEW_TESTDATACUBE_NAME);
        if (newNameDialog.open() == Window.OK) {
            return newNameDialog.getName();
        }
        return null;
    }
}
