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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.FindDialog;
import org.eclipse.jubula.client.ui.rcp.views.TestResultTreeView;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 */
public class SearchTreeAction extends AbstractAction {

    /** The handle to this Action */
    private static IAction handleAction;
    
    /** the dialog to rename an selected item */
    private FindDialog<?> m_dialog;

    /**
     * {@inheritDoc}
     */
    public void init(IAction action) {
        handleAction = action;
        handleAction.setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) {
        IWorkbenchPart part = Plugin.getActivePart();
        if (part instanceof ITreeViewerContainer) {
            if (m_dialog == null || m_dialog.isDisposed()) {
                if (part instanceof TestResultTreeView) {
                    m_dialog = new FindDialog<TestResultNode>(
                            Plugin.getShell(), (ITreeViewerContainer) part);
                } else {
                    m_dialog = new FindDialog<INodePO>(Plugin.getShell(), 
                            (ITreeViewerContainer) part);
                }
            }
            m_dialog.open();
        }
    }
    
    /**
     * @return Returns the handleAction.
     */
    public static IAction getAction() {
        return handleAction;
    }
    
    /**
     * {@inheritDoc}
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);
        IWorkbenchPart part = Plugin.getActivePart();
        action.setEnabled(part instanceof ITreeViewerContainer);
        if (m_dialog != null && !m_dialog.isDisposed()
                && part instanceof ITreeViewerContainer) {
            m_dialog.setTreeViewContainer((ITreeViewerContainer) part);
        }

    }
    
}