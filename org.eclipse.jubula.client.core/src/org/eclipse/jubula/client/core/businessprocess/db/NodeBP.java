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

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import javax.persistence.PersistenceException;


/**
 * @author BREDEX GmbH
 * @created Mar 18, 2010
 */
public class NodeBP {
    /** the logger */
    private static final Log LOG = LogFactory.getLog(NodeBP.class);

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
            LOG.fatal("Stray hibernate exception on evict/load, continueing..", //$NON-NLS-1$
                    e);
        }
        if (!LockManager.instance().lockPO(lockSession, node, false)) {
            throw new PMAlreadyLockedException(node,
                    "Original testcase already locked in db.", //$NON-NLS-1$
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
}
