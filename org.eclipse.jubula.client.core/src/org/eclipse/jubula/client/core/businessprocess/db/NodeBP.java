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
package org.eclipse.jubula.client.core.businessprocess.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created Mar 18, 2010
 */
public class NodeBP {
    /** the logger */
    private static final Log LOG = LogFactory.getLog(NodeBP.class);

    /**
     * 
     * Checks whether a given node is a descendant of the root traversal node.
     */
    private static final class IsSubNodeOperation 
            implements ITreeNodeOperation<INodePO> {

        /** whether the given node is a descendant of the root traversal node */
        private boolean m_isSubNode;
        
        /** the node to check */
        private INodePO m_node;
        
        /**
         * Constructor
         * 
         * @param node The node to check.
         */
        public IsSubNodeOperation(INodePO node) {
            m_node = node;
            m_isSubNode = false;
        }

        /**
         * 
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {

            if (node == m_node) {
                m_isSubNode = true;
            }
            
            return !m_isSubNode;
        }

        /**
         * 
         * {@inheritDoc}
         */
        public void postOperate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            // no-op
        }

        /**
         * 
         * @return <code>true</code> if the given node is a descendant of the
         *         root traversal node. Otherwise <code>false</code>.
         */
        public boolean isSubNode() {
            return m_isSubNode;
        }
    }

    /**
     * Utility class
     */
    protected NodeBP() {
    // do nothing
    }
    
    /**
     * @param editSupport holding the DB session for locking purposes
     * @param node node to lock
     * @throws PMObjectDeletedException
     * @throws PMDirtyVersionException
     * @throws PMAlreadyLockedException
     */
    protected static void lockPO(EditSupport editSupport, INodePO node)
        throws PMObjectDeletedException, PMDirtyVersionException,
            PMAlreadyLockedException {
        final EntityManager lockSession = editSupport.getSession();
        try {
            try {
                // make sure there is no old version
                // in the session cache
                lockSession.detach(node);
                lockSession.find(node.getClass(), node.getId());
            } catch (PersistenceException e) {
                PersistenceManager.handleDBExceptionForEditor(node, e,
                        editSupport);
            }
        } catch (PMDirtyVersionException e) { // NOPMD by al on 3/19/07 1:25 PM
            // ignore, we are not interested in version checking
        } catch (PMObjectDeletedException e) {
            // OK, this may happen, just forward to caller
            throw e;
        } catch (PMException e) {
            // Continue since we are just refreshing the cache
            LOG.fatal(Messages.StrayHibernateException + StringConstants.DOT
                    + StringConstants.DOT, e);
        }
        if (!LockManager.instance().lockPO(lockSession, node, false)) {
            throw new PMAlreadyLockedException(node,
                    Messages.OrginalTestcaseLocked + StringConstants.DOT,
                    MessageIDs.E_OBJECT_IN_USE);
        }
    }
    
    /**
     * @param type
     *            the type to search for
     * @return a list of nodes with the given type
     */
    public static List<? extends INodePO> 
    getAllNodesForGivenTypeInCurrentProject(Class type) {
        if (INodePO.class.isAssignableFrom(type)) {
            GeneralStorage gs = GeneralStorage.getInstance();
            return NodePM.computeListOfNodes(type, 
                    gs.getProject().getId(), gs.getMasterSession());
        }
        return ListUtils.EMPTY_LIST;
    }
    
    /**
     * 
     * @param node The node to check.
     * @return <code>true</code> if the given node can be modified within
     *         the active project. Otherwise <code>false</code>.
     */
    public static boolean isEditable(INodePO node) {
        Validate.notNull(node);
        IProjectPO activeProject = GeneralStorage.getInstance().getProject();
        if (activeProject == node) {
            return true;
        }
        return activeProject != null 
            && activeProject.getId().equals(node.getParentProjectId());
    }

    /**
     * 
     * @param node The node to check.
     * @return the Test Suite that (recursively) contains <code>node</code>, or
     *         <code>null</code> if <code>node</code> does not belong to any 
     *         Test Suite.
     */
    public static ITestSuitePO getOwningTestSuite(INodePO node) {
        IProjectPO activeProject = GeneralStorage.getInstance().getProject();
        if (activeProject != null) {
            IsSubNodeOperation op = new IsSubNodeOperation(node);
            for (ITestSuitePO testSuite 
                    : activeProject.getTestSuiteCont().getTestSuiteList()) {
                TreeTraverser traverser = new TreeTraverser(testSuite, op);
                traverser.traverse(true);
                if (op.isSubNode()) {
                    return testSuite;
                }
            }
        }
        
        return null;
    }
}
