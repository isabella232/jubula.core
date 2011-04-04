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
package org.eclipse.jubula.client.ui.handlers.delete;

import java.util.Iterator;

import javax.persistence.PersistenceException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
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
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        
        if (activePart instanceof AbstractJBEditor
                && currentSelection instanceof IStructuredSelection) {

            AbstractJBEditor tce = (AbstractJBEditor)activePart;
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)currentSelection;
            if (confirmDelete(structuredSelection)) {
                if (tce.getEditorHelper().requestEditableState() 
                        != JBEditorHelper.EditableState.OK) {
                    return null;
                }
                for (Iterator<INodePO> it = structuredSelection.iterator(); 
                        it.hasNext();) {
                    
                    INodePO node = it.next();
                    try {
                        node.getParentNode().removeNode(node);
                        if (node.getId() != null) {
                            tce.getEditorHelper().getEditSupport()
                                .getSession().remove(node);
                        }
                        tce.getEditorHelper().setDirty(true);
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(node, DataState.Deleted, 
                                UpdateState.onlyInEditor);
                    } catch (PersistenceException e) {
                        try {
                            PersistenceManager.handleDBExceptionForEditor(
                                node, e, 
                                tce.getEditorHelper().getEditSupport());
                        } catch (PMException pme) {
                            PMExceptionHandler
                                .handlePMExceptionForMasterSession(pme);
                            break;
                        }
                        break;
                    }
                }
            }

        }

        return null;
    }
   
}
