/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;

/**
 * A class supporting transactions with fully integrated Session and Exception handling
 * The user does not receive any special report on the error, but the causing Exception is
 * properly displayed.
 * @author BREDEX GmbH
 *
 */
public class TransactionSupport {
    
    /** Private constructor */
    private TransactionSupport() {
        // empty
    }
    
    /** Interface used for the transaction */
    public interface ITransaction {
        
        /**
         * Sets the set of objects to be locked - these objects do not have
         *     to be managed anywhere or can be managed by any session
         * @return the collection of objects
         */
        public Collection<? extends IPersistentObject> getToLock();

        /**
         * Sets the set of objects to be refreshed - these objects do not have
         *     to be managed anywhere or can be managed by any session
         * @return the collection of objects
         */
        public Collection<? extends IPersistentObject> getToRefresh();
        
        /**
         * Executes the operations
         * @param sess the session to use
         * @throws PMException
         * @throws ProjectDeletedException
         */
        public abstract void run(EntityManager sess) throws PMException,
            ProjectDeletedException;
    }
    
    /**
     * Executes the transaction
     * @param op the operation
     * @throws PersistenceException
     * @throws PMException
     * @throws ProjectDeletedException
     */
    public static void transact(ITransaction op)
            throws PMException, ProjectDeletedException, PersistenceException {
        EntityManager sess = null;
        Persistor per = Persistor.instance();
        boolean success = false;
        try {
            sess = per.openSession();
            EntityTransaction tx = per.getTransaction(sess);
            LockManager.instance().lockPOs(sess, op.getToLock(), true);
            op.run(sess);
            per.commitTransaction(sess, tx);
            success = true;
        } finally {
            per.dropSession(sess);
        }
        refreshMasterSession(op);
    }
    
    /**
     * Refreshes objects of the master session
     * @param op the objects - these don't need to be managed
     */
    private static void refreshMasterSession(ITransaction op)
        throws PMRefreshFailedException {
        Collection<? extends IPersistentObject> toRefresh = op.getToRefresh();
        if (toRefresh == null || toRefresh.isEmpty()) {
            return;
        }
        try {
            EntityManager master = GeneralStorage.getInstance().
                    getMasterSession();
            for (IPersistentObject po : toRefresh) {
                po = master.find(po.getClass(), po.getId());
                if (po != null) {
                    master.refresh(po);
                }
                if (po instanceof INodePO) {
                    // to set the parents of the children...
                    ((INodePO) po).getUnmodifiableNodeList();
                }
            }
        } catch (Exception e) {
            throw new PMRefreshFailedException(e);
        }
    }
    
}