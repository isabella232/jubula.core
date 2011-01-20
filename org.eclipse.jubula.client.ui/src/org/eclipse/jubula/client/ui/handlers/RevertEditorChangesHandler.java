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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.editors.IJBEditor;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author BREDEX GmbH
 * @created 04.03.2008
 */
public class RevertEditorChangesHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        final IWorkbenchPart activePart = Plugin.getActivePart();
        if (activePart == null) {
            return null;
        }
        
        final IJBEditor editor = 
            (IJBEditor)activePart.getAdapter(IJBEditor.class);
        if (editor != null) {
            MessageDialog dialog = showConfirmDialog();
            if (dialog.getReturnCode() == Window.OK) {
                revertEditorChanges(editor);
            }
        }
        return null;
    }
    
    /**
     * Shows confirm dialog for this action and returns the dialog object
     * @return confirm dialog for this action
     */
    private MessageDialog showConfirmDialog() {
        MessageDialog dialog = new MessageDialog(Plugin.getShell(), 
            I18n.getString("RevertEditorChangesAction.shellTitle"), //$NON-NLS-1$
                null,
                I18n.getString("RevertEditorChangesAction.questionText"), //$NON-NLS-1$
                MessageDialog.QUESTION, new String[] {
                    I18n.getString("NewProjectDialog.MessageButton0"), //$NON-NLS-1$
                    I18n.getString("NewProjectDialog.MessageButton1") }, 0); //$NON-NLS-1$
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog;
    }

    /**
     * @param editor The editor in that the changes have to be reverted
     */
    private void revertEditorChanges(IJBEditor editor) {
        try {
            editor.reOpenEditor(
                    editor.getEditorHelper().getEditSupport().getOriginal());
        } catch (PMException e) {
            Utils.createMessageDialog(
                MessageIDs.E_REVERT_EDITOR_CHANGES_FAILED);
        }
    }
}
