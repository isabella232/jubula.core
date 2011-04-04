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
package org.eclipse.jubula.client.ui.handlers;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.ParameterInterfaceBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.dialogs.AbstractEditParametersDialog;
import org.eclipse.jubula.client.ui.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.dialogs.EditParametersTDMDialog;
import org.eclipse.jubula.client.ui.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jul 14, 2010
 */
public class EditTDMParametersHandler extends AbstractEditParametersHandler {
    /**
     * @param event
     *            An event containing all the information about the current
     *            state of the application; must not be <code>null</code>.
     * @return the currently selected TestDataCube, or <code>null</code> if 
     *         no TestDataCube is currently selected.
     */
    protected final ITestDataCubePO getSelectedTestDataManager(
            ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)selection;
            Object selectedObject = structuredSelection.getFirstElement();
            if (selectedObject instanceof ITestDataCubePO) {
                return (ITestDataCubePO)selectedObject;
            }
        }
        
        return null;
    }
    
    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        AbstractJBEditor editor = getEditorInEditableState();
        if (editor != null) {
            ITestDataCubePO tdc = getSelectedTestDataManager(event);
            if (tdc != null) {
                final JBEditorHelper.EditableState state = editor
                        .getEditorHelper().getEditableState();
                final AbstractEditParametersDialog dialog = 
                    new EditParametersTDMDialog(Plugin.getShell(), tdc);
                dialog.create();
                DialogUtils.setWidgetNameForModalDialog(dialog);
                dialog.open();
                if (Window.OK == dialog.getReturnCode()) {
                    performChanges(editor, tdc, dialog);
                } else {
                    if (state == JBEditorHelper.EditableState.NotChecked) {
                        editor.getEditorHelper().resetEditableState();
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param editor
     *            the current editor
     * @param tdc
     *            the test data cube to perform the changes on
     * @param dialog
     *            the edit parameters dialog
     */
    private void performChanges(AbstractJBEditor editor, ITestDataCubePO tdc,
            AbstractEditParametersDialog dialog) {
        final List<Parameter> parameters = dialog.getParameters();
        boolean isModified = editParameters(tdc, parameters, editor
                .getEditorHelper().getEditSupport().getParamMapper(),
                new ParameterInterfaceBP());
        if (isModified) {
            editor.getEditorHelper().setDirty(true);
            DataEventDispatcher ded = DataEventDispatcher.getInstance();
            ded.fireParamChangedListener();
            ded.firePropertyChanged(false);
            ded.fireDataChangedListener(tdc, 
                    DataState.StructureModified, UpdateState.onlyInEditor);
        } else {
            editor.getEditorHelper().resetEditableState();
        }
    }
}
