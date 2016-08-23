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

import java.util.Collection;
import java.util.List;

import javax.persistence.PersistenceException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTreeItemHandlerTCEditor 
        extends AbstractDeleteTreeItemHandler {
    /**
     * <code>log</code> logger for class
     */
    private static Logger log = LoggerFactory.getLogger(
            DeleteTreeItemHandlerTCEditor.class);
    
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
                if (node instanceof IExecTestCasePO) {
                    IExecTestCasePO po = (IExecTestCasePO) node;
                    Collection<ICompNamesPairPO> col = po.getCompNamesPairs();
                    for (ICompNamesPairPO iCompNamesPairPO : col) {
                        iCompNamesPairPO.getSecondName();
                    }
                    for (int i = po.getDataManager().getDataSetCount() - 1;
                            i >= 0; i--) {
                        po.getDataManager().removeDataSet(i);
                    }
                }
                if (node.getId() != null) {
                    editor.getEditorHelper().getEditSupport().getSession()
                            .remove(node);
                }
                createReuseEvents(editor, node);
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

    /**
     * changes the component names back to the first name so correct events are generated
     * @param editor the editor from which we need the EditSupport
     * @param node the node to generate the component name change events from
     */
    private static void createReuseEvents(AbstractJBEditor editor,
            INodePO node) {
        if (node instanceof IExecTestCasePO) {
            IWritableComponentNameMapper mapper =
                    editor.getEditorHelper().getEditSupport().getCompMapper();
            IWritableComponentNameCache cache = mapper.getCompNameCache();
            IExecTestCasePO exec = (IExecTestCasePO) node;
            Collection<ICompNamesPairPO> compNamesPairs =
                    exec.getCompNamesPairs();
            for (ICompNamesPairPO iCompNamesPairPO : compNamesPairs) {
                IComponentNamePO compName =
                        cache.getCompNamePo(iCompNamesPairPO.getFirstName());
                try {
                    new CompNamesBP().updateCompNamesPair(exec,
                            iCompNamesPairPO, compName.getName(), mapper);
                } catch (IncompatibleTypeException | PMException e) {
                    log.warn("error occured during update of component " //$NON-NLS-1$
                            + "names from deleted node", e); //$NON-NLS-1$
                }
            }
        }
    }
}
