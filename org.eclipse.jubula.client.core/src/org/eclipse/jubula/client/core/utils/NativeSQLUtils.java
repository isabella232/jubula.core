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
package org.eclipse.jubula.client.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IExecPersistable;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;

/**
 * Class helping handling native SQL queries
 * Native SQL queries ignore our internal locks, so before executing these,
 *    the caller is responsible for acquiring proper locks for any possibly affected nodes
 * The versions of the objects are properly increased in the DB
 * @author BREDEX GmbH
 *
 */
public class NativeSQLUtils {
    
    /** Maximum number of elements in a DB Query list - 1000 for Oracle... */
    private static final int MAXLGT = 990;
    
    /** Exception message */
    private static final String FAIL = "Operation failed due to database error."; //$NON-NLS-1$ 
    
    /** private constructor */
    private NativeSQLUtils() {
        // private constructor
    }
    
    /**
     * Returns a list of Ids in the collection
     * @param objects the objects - should not be empty
     * @return the list
     */
    public static String getIdList(
            Collection<? extends IPersistentObject> objects) {
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("Collection should not be empty."); //$NON-NLS-1$
        }
        StringBuilder str = new StringBuilder("("); //$NON-NLS-1$
        for (IPersistentObject per : objects) {
            str.append(per.getId());
            str.append(","); //$NON-NLS-1$
        }
        str.deleteCharAt(str.length() - 1);
        str.append(")"); //$NON-NLS-1$
        return str.toString();
    }
    
    /**
     * Returns a list of lists of ids in the collection, each list is limited in size
     * @param objects the objects
     * @return the list of lists
     */
    public static List<String> getSlicedIdLists(
            Collection<? extends IPersistentObject> objects) {
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("Collection should not be empty."); //$NON-NLS-1$
        }
        StringBuilder str = null;
        int num = 0;
        List<String> result = new ArrayList<>();
        
        for (IPersistentObject per : objects) {
            if (num == 0) {
                str = new StringBuilder("("); //$NON-NLS-1$
            }
            str.append(per.getId());
            str.append(","); //$NON-NLS-1$
            num++;
            if (num == MAXLGT) {
                str.deleteCharAt(str.length() - 1);
                str.append(")"); //$NON-NLS-1$
                result.add(str.toString());
                num = 0;
            }
        }
        if (num != 0) {
            str.deleteCharAt(str.length() - 1);
            str.append(")"); //$NON-NLS-1$
            result.add(str.toString());
        }
        return result;
    }

    /**
     * Deletes a collection of independent top nodes from either the TC or TS Browsers
     *    The nodes have to be managed by the master session, and will be chaged
     * @param sess the session
     * @param nodes the nodes
     * @param monitor the progress monitor or null
     */
    public static void deleteFromTCTSTreeAFFECTS(EntityManager sess,
            Collection<INodePO> nodes, IProgressMonitor monitor) {
        
        for (INodePO node : nodes) {
            node.goingToBeDeleted(sess);
            removeNodeAFFECTS(sess, node);
            if (monitor != null) {
                monitor.worked(1);
            }
        }
        Query q;
        for (String smallList : getSlicedIdLists(nodes)) {
            q = sess.createNativeQuery("delete from NODE where ID in " + smallList); //$NON-NLS-1$
            q.executeUpdate();
        }
    }
    
    /**
     * Removes a node from its parent
     * This operation changes both the DB and the memory data.
     * @param sess the session
     * @param node the node
     */
    public static void removeNodeAFFECTS(EntityManager sess, INodePO node) {
        Query q1, q2;
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        int pos = -1;
        INodePO par = node.getParentNode();
        if (par == ISpecObjContPO.TCB_ROOT_NODE) {
            pos = proj.getSpecObjCont().getSpecObjList().indexOf(node);
            q1 = sess.createNativeQuery("delete from SPEC_CONT_NODE where SPECOBJCONTPO_ID = ?1 and HBMSPECOBJLIST_ID = ?2 and IDX = ?3"); //$NON-NLS-1$
            q1.setParameter(1, proj.getSpecObjCont().getId());
            q2 = sess.createNativeQuery("update SPEC_CONT_NODE set IDX = IDX - 1 where SPECOBJCONTPO_ID = ?1 and IDX > ?2"); //$NON-NLS-1$
            q2.setParameter(1, proj.getSpecObjCont().getId());
        } else if (par == IExecObjContPO.TSB_ROOT_NODE) {
            pos = proj.getExecObjCont().getExecObjList().indexOf(node);
            q1 = sess.createNativeQuery("delete from EXEC_CONT_NODE where EXECOBJCONTPO_ID = ?1 and HBMEXECOBJLIST_ID = ?2 and IDX = ?3"); //$NON-NLS-1$
            q1.setParameter(1, proj.getExecObjCont().getId());
            q2 = sess.createNativeQuery("update EXEC_CONT_NODE set IDX = IDX - 1 where EXECOBJCONTPO_ID = ?1 and IDX > ?2"); //$NON-NLS-1$
            q2.setParameter(1, proj.getExecObjCont().getId());
        } else {
            pos = par.indexOf(node);
            q1 = sess.createNativeQuery("update NODE set PARENT = null, IDX = null where ID = ?1"); //$NON-NLS-1$
            q1.setParameter(1, node.getId()).executeUpdate();
            q1 = null;
            q2 = sess.createNativeQuery("update NODE set IDX = IDX - 1 where PARENT = ?1 and IDX > ?2"); //$NON-NLS-1$
            q2.setParameter(1, par.getId());
        }
        // removing from a regular node parent requires a very different query which is already executed
        if (q1 != null) {
            q1.setParameter(2, node.getId()).setParameter(3, pos);
            int res = q1.executeUpdate();
            if (res != 1) {
                throw new PersistenceException(FAIL);
            }
        }
        q2.setParameter(2, pos).executeUpdate();
        if (par == ISpecObjContPO.TCB_ROOT_NODE) {
            proj.getSpecObjCont().removeSpecObject((ISpecPersistable) node);
        } else if (par == IExecObjContPO.TSB_ROOT_NODE) {
            proj.getExecObjCont().removeExecObject((IExecPersistable) node);
        } else {
            node.getParentNode().removeNode(node);
        }
    }
    
    /**
     * Adds a node to another to the end of the child list
     * The parent node can be the TCB or TSB root node - in this case the master
     *      session's Spec(Exec)ObjCont is affected
     * This operation changes both the DB and the memory data.
     * @param sess the session
     * @param toAdd the node to add
     * @param par the target
     */
    public static void addNodeAFFECTS(EntityManager sess, INodePO toAdd,
            IPersistentObject par) {
        Query q1;
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        int pos = -1;
        if (par instanceof ISpecObjContPO) {
            pos = ((ISpecObjContPO) par).getSpecObjList().size();
            q1 = sess.createNativeQuery("insert into SPEC_CONT_NODE (SPECOBJCONTPO_ID, HBMSPECOBJLIST_ID, IDX) values (?1, ?2, ?3)"); //$NON-NLS-1$
        } else if (par instanceof IExecObjContPO) {
            pos = ((IExecObjContPO) par).getExecObjList().size();
            q1 = sess.createNativeQuery("insert into EXEC_CONT_NODE (EXECOBJCONTPO_ID, HBMEXECOBJLIST_ID, IDX) values (?1, ?2, ?3)"); //$NON-NLS-1$
        } else {
            pos = ((INodePO) par).getNodeListSize();
            q1 = sess.createNativeQuery("update NODE set PARENT = ?1, IDX = ?3 where ID = ?2"); //$NON-NLS-1$
        }
        q1.setParameter(1, par.getId()).setParameter(2, toAdd.getId());
        int res = q1.setParameter(3, pos).executeUpdate();
        if (res != 1) {
            throw new PersistenceException(FAIL);
        }
        if (par instanceof ISpecObjContPO) {
            ((ISpecObjContPO) par).addSpecObject((ISpecPersistable) toAdd);
        } else if (par instanceof IExecObjContPO) {
            ((IExecObjContPO) par).addExecObject((IExecPersistable) toAdd);
        } else {
            ((INodePO) par).addNode(toAdd);
        }
    }
    
    /**
     * Moves a node from somewhere to somewhere else
     * This operation changes both the DB and the memory data.
     * @param sess the session
     * @param toMove node to move
     * @param target the target
     */
    public static void moveNode(EntityManager sess, INodePO toMove,
            IPersistentObject target) {
        removeNodeAFFECTS(sess, toMove);
        addNodeAFFECTS(sess, toMove, target);
    }
}
