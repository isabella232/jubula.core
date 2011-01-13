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
import org.eclipse.jubula.client.ui.views.TestCaseBrowser;
import org.eclipse.swt.dnd.Transfer;


/**
 * Implementation of the Cut action within the Test Case Browser.
 *
 * @author BREDEX GmbH
 * @created 18.03.2008
 */
public class CutTreeItemActionTCBrowser extends AbstractCutTreeItemAction {

    /**
     * {@inheritDoc}
     */
    public void run() {
        
        TestCaseBrowser tstv = (TestCaseBrowser)Plugin.getActiveView();
        if (!(tstv.getSelection() instanceof IStructuredSelection)) {
            return;
        }
        IStructuredSelection selection = (IStructuredSelection)tstv
            .getSelection();
        LocalSelectionClipboardTransfer transfer = 
            LocalSelectionClipboardTransfer.getInstance(); 
        tstv.getClipboard().setContents(
                new Object [] {selection}, new Transfer [] {transfer});
        transfer.setSelection(selection, tstv.getTreeViewer());
        
    }

}
