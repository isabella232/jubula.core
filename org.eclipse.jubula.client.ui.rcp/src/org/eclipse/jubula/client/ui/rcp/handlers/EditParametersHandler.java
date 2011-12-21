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

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.rcp.dialogs.EditParametersDialog;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.utils.DialogUtils;


/**
 * @author BREDEX GmbH
 * @created Oct 29, 2007
 */
public class EditParametersHandler extends AbstractEditParametersHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        final AbstractJBEditor editor = getEditorInEditableState();
        if (editor != null) {
            final JBEditorHelper.EditableState state = editor.getEditorHelper()
                    .getEditableState();
            final ISpecTestCasePO workTC = (ISpecTestCasePO)editor
                    .getEditorHelper().getEditSupport().getWorkVersion();
            final EditParametersDialog dialog = new EditParametersDialog(
                    getActiveShell(), workTC);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            dialog.open();
            if (Window.OK == dialog.getReturnCode()) {
                performChanges(editor, state, workTC, dialog);
            } else {
                if (state == JBEditorHelper.EditableState.NotChecked) {
                    workTC.setIsReused(null);
                    editor.getEditorHelper().resetEditableState();
                }
            }
        }
        return null;
    }

    /**
     * Performs the changes done in the EditParametersDialog
     * @param editor the TestCaseEditor
     * @param state the EditableState
     * @param workTC the working ISpecTestCasePO
     * @param dialog the EditParametersDialog
     */
    private static void performChanges(AbstractJBEditor editor, 
        JBEditorHelper.EditableState state, ISpecTestCasePO workTC, 
        EditParametersDialog dialog) {
        
        final List<Parameter> parameters = dialog.getParameters();
        final boolean isInterfaceLocked = dialog.isInterfaceLocked();
        boolean isModified = editParameters(workTC, parameters, 
            isInterfaceLocked, 
            editor.getEditorHelper().getEditSupport().getParamMapper(),
            new TestCaseParamBP());
        if (isModified) {
            editor.getEditorHelper().setDirty(true);
            DataEventDispatcher.getInstance()
                .fireParamChangedListener();
            DataEventDispatcher.getInstance().firePropertyChanged(false);
        } else {
            if (state == JBEditorHelper.EditableState.NotChecked) {
                workTC.setIsReused(null);
                editor.getEditorHelper().resetEditableState();
            }  
        }
    }
}
