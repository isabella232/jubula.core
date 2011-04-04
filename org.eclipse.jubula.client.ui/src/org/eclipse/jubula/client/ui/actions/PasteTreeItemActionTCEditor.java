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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.controllers.dnd.TCEditorDndSupport;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;


/**
 * Implementation of the Paste action within the Object Mapping Editor.
 *
 * @author BREDEX GmbH
 * @created 20.03.2008
 */
public class PasteTreeItemActionTCEditor extends AbstractPasteTreeItemAction {

    /**
     * {@inheritDoc}
     */
    public void run() {
        AbstractTestCaseEditor tce = 
            (AbstractTestCaseEditor)Plugin.getActiveEditor();
        LocalSelectionClipboardTransfer transfer = 
            LocalSelectionClipboardTransfer.getInstance();
        
        if (!(tce.getEditorHelper().getClipboard().getContents(transfer)
                instanceof IStructuredSelection)) {
            return;
        }
        IStructuredSelection pasteSelection = 
            (IStructuredSelection)tce.getEditorHelper()
                .getClipboard().getContents(transfer);
        if (pasteSelection != null 
                && tce.getSelection() instanceof IStructuredSelection) {

            // Paste will always occur at the most recently selected node.
            IStructuredSelection selection = 
                (IStructuredSelection)tce.getSelection();
            Object [] selArray = selection.toArray();
            INodePO target = (INodePO)selArray[selArray.length - 1];
            
            if (TCEditorDndSupport.performDrop(tce, pasteSelection, target, 
                    ViewerDropAdapter.LOCATION_ON)) {

                tce.getEditorHelper().getClipboard().clearContents();
                transfer.setSelection(null, null);

            }

        }
        
    }

}
