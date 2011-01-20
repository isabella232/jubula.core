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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.actions.OMSetCategoryToMapInto;
import org.eclipse.jubula.client.ui.businessprocess.OMEditorBP;
import org.eclipse.jubula.client.ui.businessprocess.OMStopMappingModeBP;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 29.05.2006
 */
public class RenameCategoryHandlerOMEditor 
        extends AbstractRenameTreeItemHandler {
    
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (editor instanceof ObjectMappingMultiPageEditor
                && sel instanceof IStructuredSelection) {
            
            dialogPopUp((ObjectMappingMultiPageEditor)editor, 
                    (IStructuredSelection)sel);
        }

        return null;
    }

    /**
     * Opens the dialog for renaming a node.
     * 
     * @param editor The current editor.
     * @param sel The current selection.
     * 
     */
    protected void dialogPopUp(ObjectMappingMultiPageEditor editor, 
            IStructuredSelection sel) {
        IObjectMappingCategoryPO category = 
            (IObjectMappingCategoryPO)sel.getFirstElement();
        IObjectMappingCategoryPO categoryToMapIn = 
            editor.getOmEditorBP().getCategoryToCreateIn();
        InputDialog dialog = createDialog(category, editor.getOmEditorBP());
        if (dialog != null) {
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(), 
                ContextHelpIds.DIALOG_RENAME);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                if (editor.getEditorHelper().requestEditableState() 
                        != EditableState.OK) {
                    return;
                }
                if (!category.getName().equals(dialog.getName())) {
                    category.setName(dialog.getName());
                    editor.getEditorHelper().setDirty(true);
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                        category, DataState.Renamed, UpdateState.onlyInEditor);
                    if (categoryToMapIn != null 
                        && OMStopMappingModeBP.getInstance().isEnabled()) {
                        
                        OMSetCategoryToMapInto action = 
                            new OMSetCategoryToMapInto();
                        action.runWithEvent(null, null);
                        action.dispose();
                    }
                }
            }
        }
    }
    
    /**
     * @param category the corresponding object mapping category.
     * @param editorBp The business process to use for checking the existence
     *                 of a category.
     * @return a new InputDialog.
     */
    private InputDialog createDialog(final IObjectMappingCategoryPO category, 
            final OMEditorBP editorBp) {
        
        return new InputDialog(Plugin.getShell(), 
                I18n.getString("RenameCategoryActionOMEditor.Title"), //$NON-NLS-1$
                category.getName(), 
                I18n.getString("RenameCategoryActionOMEditor.Message"),  //$NON-NLS-1$
                I18n.getString("RenameCategoryActionOMEditor.Label"), //$NON-NLS-1$
                I18n.getString("RenameCategoryActionOMEditor.Error1"), //$NON-NLS-1$
                I18n.getString("RenameCategoryActionOMEditor.doubleCatName"), //$NON-NLS-1$
                IconConstants.RENAME_CAT_DIALOG_STRING, 
                I18n.getString("RenameCategoryActionOMEditor.Shell"), //$NON-NLS-1$
                false) {
                /**
                 * @return False, if the input name already exists.
                 */
                protected boolean isInputAllowed() {
                    return (!editorBp.existCategory(
                            category.getParent(), getInputFieldText())
                        || category.getName().equals(getInputFieldText()));
                }
            };
    }
}