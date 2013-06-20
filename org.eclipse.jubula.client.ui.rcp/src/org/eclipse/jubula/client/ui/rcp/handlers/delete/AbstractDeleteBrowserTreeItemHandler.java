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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;


/**
 * Superclass of all DeleteTreeItem Handlers
 *
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public abstract class AbstractDeleteBrowserTreeItemHandler 
    extends AbstractDeleteTreeItemHandler {

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IStructuredSelection structuredSelection = getSelection();
        if (confirmDelete(structuredSelection)) {
            deleteSelection(structuredSelection);
        }
        return null;
    }
    
    /**
     * Deletes all selected items unless an error occurs.
     * 
     * @param selection The selected items to delete.
     */
    private void deleteSelection(IStructuredSelection selection) {
        // cleanup set for entries, that are children of other contained nodes
        Set <INodePO> set = new HashSet<INodePO>(selection.toList());
        Set <INodePO> topNodesToDelete = new HashSet<INodePO>();
        for (INodePO node : set) {
            if (!containsParent(set, node)) {
                topNodesToDelete.add(node);
            }
        }

        // determine all nodes, which have to be deleted
        // (children of selected nodes which are CategoryPO or SpecTestCasePO
        List <INodePO> nodesToDelete = new ArrayList<INodePO>();
        for (INodePO node : topNodesToDelete) {
            MultipleNodePM.collectAffectedNodes(nodesToDelete, node);
        }
        
        // reverse List, to get the right order of deletion (child first)
        Collections.reverse(nodesToDelete);
        
        List<AbstractCmdHandle> cmds = getDeleteCommands(nodesToDelete);
        if (!cmds.isEmpty()) {
            try {
                MultipleNodePM.getInstance().executeCommands(cmds);
                
                List<DataChangedEvent> eventList = 
                        new ArrayList<DataChangedEvent>();
                for (INodePO node : topNodesToDelete) {
                    eventList.add(new DataChangedEvent(node, DataState.Deleted,
                            UpdateState.all));
                }
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        eventList.toArray(new DataChangedEvent[0]));
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleProjectDeletedException();
            }
        }
    }

    /**
     * @param nodesToDelete the nodes to delete
     * @return a list of abstract cmd handles for node deletion
     */
    protected abstract List<AbstractCmdHandle> getDeleteCommands(
            List<INodePO> nodesToDelete);
    
    /**
     * checks if a set contains any parent node of a  specified node
     * @param set
     *      Set<INodePO> set
     * @param node
     *      UI node
     * @return
     *      true if any parent is already in set
     */
    private boolean containsParent(Set<INodePO> set, INodePO node) {
        INodePO parent = node.getParentNode();
        while (parent != null) {
            if (set.contains(parent)) {
                return true;
            }
            parent = parent.getParentNode();
        }
        return false;
    }
}