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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.List;

import javax.persistence.PersistenceException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTreeItemHandlerTCEditor 
        extends AbstractDeleteTreeItemHandler {
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked") 
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        
        if (activePart instanceof AbstractJBEditor) {
            final AbstractJBEditor tce = (AbstractJBEditor)activePart;
            tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
                public void run(IPersistentObject workingPo) {
                    IStructuredSelection structuredSelection = getSelection();
                    if (confirmDelete(structuredSelection)) {
                        deleteNodesFromEditor(
                                structuredSelection.toList(), tce);
                    }
                }
            });

        }
        return null;
    }
    
    /**
     * @param nodes
     *            the nodes to delete
     * @param editor
     *            the editor to perfrom the deletion for
     */
    public static void deleteNodesFromEditor(List<? extends INodePO> nodes,
            AbstractJBEditor editor) {
        editor.getEditorHelper().getClipboard().clearContents();
        for (INodePO node : nodes) {
            try {
                node.getParentNode().removeNode(node);
                if (node.getId() != null) {
                    editor.getEditorHelper().getEditSupport().getSession()
                            .remove(node);
                }
                editor.getEditorHelper().setDirty(true);
                DataEventDispatcher.getInstance().fireDataChangedListener(node,
                        DataState.Deleted, UpdateState.onlyInEditor);
            } catch (PersistenceException e) {
                try {
                    PersistenceManager.handleDBExceptionForEditor(node, e,
                            editor.getEditorHelper().getEditSupport());
                } catch (PMException pme) {
                    PMExceptionHandler.handlePMExceptionForMasterSession(pme);
                }
            }
        }
    }
}
