/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.rename;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.SelectionUtils;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 
 * @author Zeb Ford-Reitz
 * @created Nov 06, 2011
 */
public class RenameTestDataCategoryInEditorHandler extends AbstractHandler {

    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ITestDataCategoryPO toRename = SelectionUtils.getFirstElement(
                HandlerUtil.getCurrentSelection(event),
                ITestDataCategoryPO.class);
        if (toRename == null) {
            return null;
        }

        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        
        if (activePart instanceof CentralTestDataEditor) {
            CentralTestDataEditor editor = (CentralTestDataEditor)activePart;
            InputDialog dialog = new InputDialog(Plugin.getShell(), 
                    Messages.RenameCategoryActionOMEditorTitle,
                    toRename.getName(), 
                    Messages.RenameCategoryActionOMEditorMessage,
                    Messages.RenameCategoryActionOMEditorLabel,
                    Messages.RenameCategoryActionOMEditorError1,
                    Messages.RenameCategoryActionOMEditorDoubleCatName,
                    IconConstants.RENAME_CAT_DIALOG_STRING, 
                    Messages.RenameCategoryActionOMEditorShell,
                    false);
            
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(), 
                ContextHelpIds.DIALOG_RENAME);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                if (editor.getEditorHelper().requestEditableState() 
                        != EditableState.OK) {
                    return null;
                }
                if (!toRename.getName().equals(dialog.getName())) {
                    toRename.setName(dialog.getName());
                    editor.getEditorHelper().setDirty(true);
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                        toRename, DataState.Renamed, UpdateState.onlyInEditor);
                }
            }
        }
        
        return null;
    }

}
