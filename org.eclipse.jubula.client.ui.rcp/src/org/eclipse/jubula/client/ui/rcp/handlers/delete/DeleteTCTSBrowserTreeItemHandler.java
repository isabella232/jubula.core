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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.actions.TransactionWrapper;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;


/**
 * Superclass of all DeleteTreeItem Handlers
 *
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTCTSBrowserTreeItemHandler 
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
     * Creates a String with the locations of use of the given ISpecTestCasePO.
     * @param specTcPO a SpecTestCasePO
     * @param reusesSet
     *      List <IExecTestCasePO>
     * @param  nodesToDelete
     *      List<INodePO>
     * @return a String
     */
    private static Object[] createLocOfUseArray(ISpecTestCasePO specTcPO,
        List <IExecTestCasePO> reusesSet, Collection<INodePO> nodesToDelete) {
        Set < String > locations = new TreeSet < String > ();
        for (IExecTestCasePO node : reusesSet) {
            INodePO parent = null;
            parent = node.getSpecAncestor();
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
    
    /**
     * Decides whether we can delete the nodes
     * @param offspring the offspring collection
     * @return whether we can delete
     */
    private boolean canDelete(Collection<INodePO> offspring) {
        for (INodePO node : offspring) {
            closeOpenEditor(node);
            if (node instanceof ISpecTestCasePO) {
                ISpecTestCasePO specTcPO = (ISpecTestCasePO)node;
                List<IExecTestCasePO> execTestCases;
                execTestCases = NodePM.getInternalExecTestCases(
                    specTcPO.getGuid(), specTcPO.getParentProjectId());
                if (!MultipleNodePM.allExecsFromList(
                        offspring, execTestCases)) {
                    ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.I_REUSED_SPEC_TCS, 
                        createLocOfUseArray(specTcPO, execTestCases,
                            offspring), null);
                    return false;
                }
            } else if (node instanceof ITestSuitePO) {
                ITestSuitePO testSuite = (ITestSuitePO) node;
                List<IRefTestSuitePO> refTs = NodePM.getInternalRefTestSuites(
                        testSuite.getGuid(), testSuite.getParentProjectId());
                if (refTs.size() > 0) {
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.I_REUSED_TS);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deletes all selected items unless an error occurs.
     * 
     * @param selection The selected items to delete.
     */
    private void deleteSelection(final IStructuredSelection selection) {
        // cleanup set for entries, that are children of other contained nodes
        Set<INodePO> set = new HashSet<INodePO>(selection.toList());
        final Set<INodePO> topNodes = new HashSet<INodePO>();
        for (INodePO node : set) {
            if (!containsParent(set, node)) {
                topNodes.add(node);
            }
        }
        if (topNodes.isEmpty()) {
            return;
        }
        Collection<INodePO> toDelete = NodeBP.getOffspringCollection(
                topNodes);
        if (!canDelete(toDelete)) {
            return;
        }
        final Collection<IPersistentObject> toRefresh = new HashSet<>();
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        if (NodeBP.isTC((INodePO) selection.getFirstElement())) {
            toRefresh.add(proj.getSpecObjCont());
        } else {
            toRefresh.add(proj.getExecObjCont());
        }
        for (INodePO node : topNodes) {
            INodePO par = node.getParentNode();
            if (!par.equals(ISpecObjContPO.TCB_ROOT_NODE) 
                    && !(par.equals(IExecObjContPO.TSB_ROOT_NODE))) {
                toRefresh.add(par);
            }
        }
        final Collection<IPersistentObject> toLock = new ArrayList<>();
        toLock.addAll(toDelete);
        toLock.addAll(toRefresh);

        boolean succ = TransactionWrapper.executeOperation(new ITransaction() {

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToLock() {
                return toLock;
            }
            
            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToRefresh() {
                return toRefresh;
            }

            /** {@inheritDoc} */
            public void run(EntityManager sess) {
                NativeSQLUtils.deleteFromTCTSTreeAFFECTS(sess, topNodes);
            }

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToMerge() {
                return null;
            }
        
        });
        if (!succ) {
            return;
        }
        List<DataChangedEvent> eventList = new ArrayList<DataChangedEvent>();
        for (INodePO node : topNodes) {
            eventList.add(new DataChangedEvent(node, DataState.Deleted,
                    UpdateState.all));
        }
        CompNameManager.getInstance().countUsage();
        DataEventDispatcher.getInstance().fireDataChangedListener(
                eventList.toArray(new DataChangedEvent[0]));
    }

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