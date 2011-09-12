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
package org.eclipse.jubula.client.ui.actions;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionTransfer;
import org.eclipse.jubula.client.ui.controllers.dnd.objectmapping.OMEditorDndSupport;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.editors.ObjectMappingMultiPageEditor;


/**
 * Implementation of the Paste action within the Object Mapping Editor.
 *
 * @author BREDEX GmbH
 * @created 20.03.2008
 */
public class PasteTreeItemActionOMEditor extends AbstractPasteTreeItemAction {

    /**
     * {@inheritDoc}
     */
    public void run() {
        ObjectMappingMultiPageEditor ome = 
            (ObjectMappingMultiPageEditor)Plugin.getActiveEditor();
        LocalSelectionClipboardTransfer transfer = 
            LocalSelectionClipboardTransfer.getInstance();
        
        if (!(ome.getEditorHelper().getClipboard().getContents(transfer)
                instanceof IStructuredSelection)) {
            return;
        }
        IStructuredSelection pasteSelection = 
            (IStructuredSelection)ome.getEditorHelper().getClipboard()
                .getContents(transfer);
        if (pasteSelection != null) {
            // Paste will always occur at the most recently selected node.
            ISelection sel = 
                ome.getSite().getSelectionProvider().getSelection();
            if (!(sel instanceof IStructuredSelection)) {
                return;
            }
            IStructuredSelection selection = 
                (IStructuredSelection)sel;
            Object [] selArray = selection.toArray();
            Object target = selArray[selArray.length - 1];
            
            if (ome.getEditorHelper().requestEditableState() 
                    != EditableState.OK) {
                return;
            }

            if (transfer.containsOnlyType(IObjectMappingAssoziationPO.class)) {
                // Use logic for dropping associations
                List<IObjectMappingAssoziationPO> toMove = 
                    transfer.getSelection().toList();
                if (target instanceof IObjectMappingCategoryPO) {
                    OMEditorDndSupport.checkAndMoveAssociations(
                            toMove, (IObjectMappingCategoryPO)target, ome);
                }
            } else if (transfer.containsOnlyType(
                    IObjectMappingCategoryPO.class)) {
                // Use logic for dropping categories
                if (target instanceof IObjectMappingCategoryPO) {
                    IObjectMappingCategoryPO targetCategory = 
                        (IObjectMappingCategoryPO)target;
                    List<IObjectMappingCategoryPO> toMove = 
                        transfer.getSelection().toList();
                    if (!OMEditorDndSupport.isMergeIfNeeded(
                            toMove, targetCategory)) {
                        return;
                    }
                    OMEditorDndSupport.checkAndMoveCategories(
                            toMove, targetCategory, ome);
                }
            } else if (transfer.containsOnlyType(IComponentNamePO.class)) {
                // Use logic for dropping Component Names
                List<IComponentNamePO> toMove = 
                    transfer.getSelection().toList();

                if (target instanceof IObjectMappingAssoziationPO) {
                    OMEditorDndSupport.checkTypeCompatibilityAndMove(
                            toMove, (IObjectMappingAssoziationPO)target, ome);
                } else if (target instanceof IObjectMappingCategoryPO) {
                    OMEditorDndSupport.checkTypeCompatibilityAndMove(
                            toMove, (IObjectMappingCategoryPO)target, ome);
                }
            }

            handlePostPaste(ome, transfer, target);
        }
    }

    /**
     * Called after the paste operation is finished.
     * 
     * @param ome The editor in which the paste took place.
     * @param transfer The transfer used for the paste.
     * @param target The target of the paste.
     */
    private void handlePostPaste(ObjectMappingMultiPageEditor ome,
            LocalSelectionClipboardTransfer transfer, Object target) {
        ome.getEditorHelper().getClipboard().clearContents();
        transfer.setSelection(null, null);
        
        // Update enablement manually because these is no selection
        // change to update the enablement automatically.
        setEnabled(false);
        
        ome.getTreeViewer().setSelection(
                new StructuredSelection(target));
        LocalSelectionTransfer.getInstance().setSelection(null);
        ome.getEditorHelper().setDirty(true);
    }

}
