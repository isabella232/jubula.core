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

import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * @author BREDEX GmbH
 * @created 02.12.2005
 */
@Entity
@Table(name = "SPEC_CONT")
public class SpecObjContPO extends WrapperPO implements ISpecObjContPO {
    
    /**
     * <code>m_specObjList</code>list with all toplevel specTestCases and categories
     */
    private List<ISpecPersistable> m_specObjList = 
            new ArrayList<ISpecPersistable>();

    /** default constructor */
    SpecObjContPO() {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "SpecObjContPO"; //$NON-NLS-1$
    }

    /**
     * @return Returns the specObjList.
     */
    // FIXME zeb Persistence (JPA / EclipseLink): although this property is semantically a OneToMany, 
    //                      it must be specified as ManyToMany in order to avoid
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
    List<ISpecPersistable> getHbmSpecObjList() {
        return m_specObjList;
    }
    
    /**
     * @return unmodifiable SpecObjList
     */
    @Transient
    public List<ISpecPersistable> getSpecObjList() {
        for (ISpecPersistable spec : m_specObjList) {
            spec.setParentNode(TCB_ROOT_NODE);
        }
        return Collections.unmodifiableList(getHbmSpecObjList());
    }

    /**
     * @param specObjList The specObjList to set.
     */
    @SuppressWarnings("unused")
    private void setHbmSpecObjList(List<ISpecPersistable> specObjList) {
        m_specObjList = specObjList;
    }
    
    /**
     * @param specObj specObj to add
     */
    public void addSpecObject(ISpecPersistable specObj) {
        getHbmSpecObjList().add(specObj);
        specObj.setParentNode(TCB_ROOT_NODE);
        specObj.setParentProjectId(getParentProjectId());
    }
    
    /**
     * @param specObj specObj to remove
     */
    public void removeSpecObject(ISpecPersistable specObj) {
        getHbmSpecObjList().remove(specObj);
        specObj.setParentNode(null);
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (ISpecPersistable spec : getSpecObjList()) {
            spec.setParentProjectId(projectId);
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
