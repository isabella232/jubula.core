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
package org.eclipse.jubula.client.ui.rcp.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.swt.dnd.Transfer;


/**
 * Implementation of the Cut action within the Test Case Editor.
 *
 * @author BREDEX GmbH
 * @created 20.03.2008
 */
public class CutTreeItemActionJBEditor extends AbstractCutTreeItemAction {

    /**
     * {@inheritDoc}
     */
    public void run() {
        AbstractJBEditor jbe = (AbstractJBEditor) Plugin.getActiveEditor();
        ISelection selection = jbe.getSelection();
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        IStructuredSelection strucSelection = (IStructuredSelection) selection;
        LocalSelectionClipboardTransfer transfer = 
                LocalSelectionClipboardTransfer.getInstance();
        jbe.getEditorHelper().getClipboard()
                .setContents(new Object[] { strucSelection },
                        new Transfer[] { transfer });
        transfer.setSelection(strucSelection, jbe.getTreeViewer(), true);
    }
}
