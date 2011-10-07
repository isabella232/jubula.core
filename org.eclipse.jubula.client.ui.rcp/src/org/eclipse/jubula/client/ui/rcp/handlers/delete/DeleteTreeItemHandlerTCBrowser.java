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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteCatHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteEvHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteTCHandle;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTreeItemHandlerTCBrowser 
        extends AbstractDeleteTreeItemHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        if (activePart instanceof TestCaseBrowser
                && currentSelection instanceof IStructuredSelection) {
            
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)currentSelection;
            if (confirmDelete(structuredSelection)) {
                deleteSelection(structuredSelection);
            }
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
            collectNodesToDelete(nodesToDelete, node);
        }
        
        // reverse List, to get the right order of deletion (child first)
        Collections.reverse(nodesToDelete);
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>();

        // check for all ISpecTestCases if they were reused somewhere, 
        // outside of the selected nodes, if not
        // create command for deletion
        ParamNameBPDecorator dec = 
            new ParamNameBPDecorator(ParamNameBP.getInstance());
        for (INodePO node : nodesToDelete) {
            closeOpenEditor(node);
            if (node instanceof ISpecTestCasePO) {
                ISpecTestCasePO specTcPO = (ISpecTestCasePO)node;
                List<IExecTestCasePO> execTestCases;
                execTestCases = NodePM.getInternalExecTestCases(
                    specTcPO.getGuid(), specTcPO.getParentProjectId());
                if (!allExecsFromList(nodesToDelete, execTestCases)) {
                    ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.I_REUSED_SPEC_TCS, 
                        createLocOfUseArray(specTcPO, execTestCases,
                            nodesToDelete), null);
                    return;
                }
                dec.clearAllNames();
                cmds.add(new DeleteTCHandle(specTcPO, dec));
            }
            if (node instanceof IEventExecTestCasePO) {
                cmds.add(new DeleteEvHandle((IEventExecTestCasePO)node));
            }
            if (node instanceof ICategoryPO) {
                cmds.add(new DeleteCatHandle((ICategoryPO)node));
            }
        }
        try {
            MultipleNodePM.getInstance().executeCommands(cmds, dec);

            // FIXME : we need a concept to execute just one gui update.
            // we will just update top level nodes for now 
            for (INodePO node : topNodesToDelete) {
                // notify listeners
                DataEventDispatcher.getInstance().
                    fireDataChangedListener(node, 
                        DataState.Deleted, UpdateState.all);
            }
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        }
    }

   

    /**
     * Checks if all ExecTestCases are used in SpecTestCases, that are going
     * to be deleted, too
     * 
     * @param nodesToDelete
     *      List<INodePO>
     * @param execTestCases
     *      List<IExecTestCasePO>
     * @return
     *      true, if no conflict exists. That means, there are no IExecTestCasePO 
     *      or all located in other nodes, which are going to be deleted
     */
    private boolean allExecsFromList(List<INodePO> nodesToDelete, 
        List<IExecTestCasePO> execTestCases) {
        if (execTestCases.isEmpty()) {
            return true;
        }
        for (IExecTestCasePO execTc : execTestCases) {
            INodePO parent;
            if (execTc instanceof IEventExecTestCasePO) {
                parent = ((IEventExecTestCasePO) execTc).getParentNode();
            } else {
                parent = execTc.getParentNode();
            }
            if (!nodesToDelete.contains(parent)) {
                return false;
            }
        }
        return true;
    }

    /**
     * collects all nodes of a specified node, that are instance of ICategoryPO
     * or ISpecTestCasePO
     * 
     * @param nodesToDelete
     *      Set<INodePO>
     * @param node
     *      INodePO
     */     
    @SuppressWarnings("unchecked") 
    private void collectNodesToDelete(List<INodePO> nodesToDelete, 
        INodePO node) {
        nodesToDelete.add(node);
        if (node instanceof ICategoryPO) {
            Iterator iter = node.getNodeListIterator();
            while (iter.hasNext()) {
                collectNodesToDelete(nodesToDelete, (INodePO)iter.next());
            }
        } else if (node instanceof ISpecTestCasePO) {
            ISpecTestCasePO specTcPO = (ISpecTestCasePO)node;
            if (!specTcPO.getAllEventEventExecTC().isEmpty()) {
                nodesToDelete.addAll(specTcPO.getAllEventEventExecTC());
            }
        }
    }

    /**
     * checks if a set contains any parent node of a  specified node
     * @param set
     *      Set<INodePO> set
     * @param node
     *      GuiNode
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

    /**
     * Creates a String with the locations of use of the given ISpecTestCasePO.
     * @param specTcPO a SpecTestCasePO
     * @param reusesSet
     *      List <IExecTestCasePO>
     * @param  nodesToDelete
     *      List<INodePO>
     * @return a String
     */
    private static Object[] createLocOfUseArray(ISpecTestCasePO specTcPO,
        List <IExecTestCasePO> reusesSet, List<INodePO> nodesToDelete) {
        Set < String > locations = new TreeSet < String > ();
        for (IExecTestCasePO node : reusesSet) {
            INodePO parent = null;
            if (Persistor.isPoSubclass(node, IEventExecTestCasePO.class)) {
                parent = ((IEventExecTestCasePO) node).getParentNode();
            } else {
                parent = node.getParentNode();
            }
            if (parent != null && !nodesToDelete.contains(parent)) {
                locations.add(Constants.BULLET + parent.getName() 
                        + StringConstants.NEWLINE);
            }
        }
        String list = StringConstants.EMPTY;
        for (String string : locations) {
            list += string;       
        }
        return new Object[] {specTcPO.getName(), locations.size(), list};
    }
}