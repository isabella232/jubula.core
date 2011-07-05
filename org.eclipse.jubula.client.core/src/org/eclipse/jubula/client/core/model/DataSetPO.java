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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.jubula.tools.constants.StringConstants;

/**
 * class to manage the list with testdata, associated with a dataset number
 * 
 * @author BREDEX GmbH
 * @created 13.06.2005
 */
@Entity
@Table(name = "TEST_DATA_LIST")
class DataSetPO implements IDataSetPO {
    
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /**
     * <code>m_list</code> list with testdata
     */
    private List<ITestDataPO> m_list = new ArrayList<ITestDataPO>();
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * @param list list to manage from DataSetPO
     */
    DataSetPO(List<ITestDataPO> list) {
        if (list == null) {
            setList(new ArrayList<ITestDataPO>());
        } else {
            setList(list);
        }
        
        for (ITestDataPO td : list) {
            td.setParentProjectId(getParentProjectId());
        }
    }
    
    /**
     * constructor
     *
     */
    DataSetPO() {
        // only for Persistence (JPA / EclipseLink)    
    }

    /**
     * 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }
    
    /**
     *    
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }
    
    /**
     * @param projectId The (database) ID of the parent project.
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (ITestDataPO td : getList()) {
            td.setParentProjectId(projectId);
        }
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }
    
    /**
     * @param projectId The (database) ID of the parent project.
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * 
     * @return Returns the list.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = TestDataPO.class)
    @OrderColumn(name = "IDX_TEST_DATA_LIST")
    @JoinColumn(name = "FK_TEST_DATA_LIST")
    public List<ITestDataPO> getList() {
        return m_list;
    }
    /**
     * @param list The list to set.
     */
    void setList(List<ITestDataPO> list) {
        m_list = list;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {        
        return m_version;
    }

    /**
     * @param version version
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }
    
    /**
     * {@inheritDoc}
     * @return empty string
     */
    @Transient
    public String getName() {
        return StringConstants.EMPTY;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public ITestDataPO getColumn(int column) {
        return getList().get(column);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public int getColumnCount() {
        return getList().size();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addColumn(ITestDataPO testData) {
        getList().add(testData);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeColumn(int column) {
        if (column < getColumnCount()) {
            getList().remove(column);
        }
    }
    
}
