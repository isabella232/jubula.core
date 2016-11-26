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
package org.eclipse.jubula.client.ui.rcp.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;

/**
 * Adds a node to the DB and the master session
 * @author BREDEX GmbH
 */
public class NodeAdder {
    
    /** Constructor */
    private NodeAdder() {
        // empty
    }
    
    /**
     * Adds a newly created node to the DB and into the master session
     * 
     * @param created the created node
     * @param parent the parent node which is TCB / TSB root or Category
     */
    public static void addNode(final INodePO created, final INodePO parent) {
        final List<IPersistentObject> toLock = new ArrayList<>();
        IProjectPO pr = GeneralStorage.getInstance().getProject();
        final IPersistentObject par;
        if (parent == ISpecObjContPO.TCB_ROOT_NODE) {
            par = pr.getSpecObjCont();
        } else if (parent == IExecObjContPO.TSB_ROOT_NODE) {
            par = pr.getExecObjCont();
        } else {
            par = parent;
        }
        toLock.add(par);
        
        TransactionWrapper.executeOperation(new ITransaction() {
            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToLock() {
                return toLock;
            }

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToRefresh() {
                toLock.add(created);
                return toLock;
            }

            /** {@inheritDoc} */
            public void run(EntityManager sess) {
                sess.persist(created);
                NativeSQLUtils.addNodeAFFECTS(sess, created, par);
            }
        });
    }
}
