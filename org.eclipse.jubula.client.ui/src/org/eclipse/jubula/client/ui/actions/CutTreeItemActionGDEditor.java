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
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.editors.AbstractGDEditor;
import org.eclipse.swt.dnd.Transfer;


/**
 * Implementation of the Cut action within the Test Case Editor.
 *
 * @author BREDEX GmbH
 * @created 20.03.2008
 */
public class CutTreeItemActionGDEditor extends AbstractCutTreeItemAction {

    /**
     * {@inheritDoc}
     */
    public void run() {
        AbstractGDEditor tce = (AbstractGDEditor)Plugin.getActiveEditor();
        if (!(tce.getSelection() instanceof IStructuredSelection)) {
            return;
        }
        IStructuredSelection selection = 
            (IStructuredSelection)tce.getSelection();
        LocalSelectionClipboardTransfer transfer = 
            LocalSelectionClipboardTransfer.getInstance(); 
        tce.getEditorHelper().getClipboard().setContents(
                new Object [] {selection}, new Transfer [] {transfer});
        transfer.setSelection(selection, tce.getTreeViewer());
    }
}
