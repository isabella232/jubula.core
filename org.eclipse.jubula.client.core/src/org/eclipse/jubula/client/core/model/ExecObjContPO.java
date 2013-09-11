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
package org.eclipse.jubula.client.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.jubula.client.core.persistence.IExecPersistable;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * @author BREDEX GmbH
 * @created 14.10.2011
 */
@Entity
@Table(name = "EXEC_CONT")
public class ExecObjContPO extends WrapperPO implements IExecObjContPO {
    
    /**
     * <code>m_execObjList</code>list with all toplevel execTestCases and categories
     */
    private List<IExecPersistable> m_execObjList = 
            new ArrayList<IExecPersistable>();

    /** default constructor */
    ExecObjContPO() {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "ExecObjContPO"; //$NON-NLS-1$
    }

    /**
     * @return Returns the execObjList.
     */
    // FIXME zeb Persistence (JPA / EclipseLink): although this property is semantically a OneToMany, 
    //                      it must be execified as ManyToMany in order to avoid
    //                      the problem described on the following pages:
    //                      http://opensource.atlassian.com/projects/Persistence (JPA / EclipseLink)/browse/HHH-1268
    //                      http://stackoverflow.com/questions/4022509/constraint-violation-in-Persistence (JPA / EclipseLink)-unidirectional-onetomany-mapping-with-jointable
    //                      It's worth looking at this again after changing JPA providers,
    //                      as we may be able to change it back to a OneToMany.
    @ManyToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = NodePO.class)
    @OrderColumn(name = "IDX")
    @BatchFetch(value = BatchFetchType.JOIN)
    List<IExecPersistable> getHbmExecObjList() {
        return m_execObjList;
    }
    
    /**
     * @return unmodifiable ExecObjList
     */
    @Transient
    public List<IExecPersistable> getExecObjList() {
        for (IExecPersistable exec : m_execObjList) {
            exec.setParentNode(TSB_ROOT_NODE);
        }
        return Collections.unmodifiableList(getHbmExecObjList());
    }

    /**
     * @param execObjList The execObjList to set.
     */
    @SuppressWarnings("unused")
    private void setHbmExecObjList(List<IExecPersistable> execObjList) {
        m_execObjList = execObjList;
    }
    
    /**
     * @param execObj execObj to add
     */
    public void addExecObject(IExecPersistable execObj) {
        getHbmExecObjList().add(execObj);
        execObj.setParentNode(TSB_ROOT_NODE);
        execObj.setParentProjectId(getParentProjectId());
    }
    
    
    /**
     * @param execObj execObj to remove
     */
    public void removeExecObject(IExecPersistable execObj) {
        getHbmExecObjList().remove(execObj);
        execObj.setParentNode(null);
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (IExecPersistable exec : getExecObjList()) {
            exec.setParentProjectId(projectId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    // FIXME zeb If we don't explicitly declare this method and mark it as 
    //           @Transient, then EclipseLink assumes (due to the inheritance 
    //           of the method from the superclass) that the property 
    //           "parentProjectId" should be persisted. This causes an 
    //           exception when trying to access an instance of this class 
    //           from the database. Oddly enough, the exceptions do not occur 
    //           when running Jubula from the IDE. The exceptions only occur 
    //           in a deployed Jubula. Once this problem is resolved (for a 
    //           deployed Jubula), the workaround can be removed.
    public Long getParentProjectId() {
        return super.getParentProjectId();
    }
    
}
