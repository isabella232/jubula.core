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
import java.util.ListIterator;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

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
     * <code>m_columns</code> list with testdata
     */
    private List<String> m_columns = new ArrayList<String>();
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * @param list list to manage from DataSetPO
     */
    DataSetPO(List<String> list) {
        if (list == null) {
            setColumns(new ArrayList<String>());
        } else {
            // this is a workaround for null/or empty Strings in the list
            for (ListIterator<String> iterator = list.listIterator(); iterator
                    .hasNext();) {
                String string = iterator.next();
                iterator.set(StringUtils.defaultIfEmpty(string,
                        StringConstants.UNICODE_NULL));
            }
            setColumns(list);
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Transient
    public String getName() {
        return StringConstants.EMPTY;
    }

    /** {@inheritDoc} */
    public String getValueAt(int column) {
        String value = getColumns().get(column);
        if (StringConstants.UNICODE_NULL.equals(value)) {
            return null;
        }
        return value;
    }

    /** {@inheritDoc} */
    public void setValueAt(int column, String value) {
        getColumns().set(column, StringUtils.defaultIfEmpty(value,
                StringConstants.UNICODE_NULL));
    }
    
    /** {@inheritDoc} */
    @Transient
    public int getColumnCount() {
        return getColumns().size();
    }

    /** {@inheritDoc} */
    public void addColumn(String value) {
        getColumns().add(StringUtils.defaultIfEmpty(value,
                StringConstants.UNICODE_NULL));
    }

    /** {@inheritDoc} */
    public void removeColumn(int column) {
        if (column < getColumnCount()) {
            getColumns().remove(column);
        }
    }

    /**
     * @return the columns
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TEST_DATA_VALUES")
    @Column(name = "DATA_VALUES", length = MAX_STRING_LENGTH)
    @OrderColumn(name = "IDX")
    @JoinColumn(name = "TEST_DATA_LIST_ID")
    @BatchFetch(value = BatchFetchType.JOIN)
    private List<String> getColumns() {
        return m_columns;
    }

    /**
     * @param columns the columns to set
     */
    void setColumns(List<String> columns) {
        m_columns = columns;
    }
    
    /** {@inheritDoc} */
    @Transient
    public List<String> getColumnsCopy() {
        List<String> list = new ArrayList<String>(getColumnCount());
        for (String string : getColumns()) {
            if (StringConstants.UNICODE_NULL.equals(string)) {
                list.add(null);
            } else {
                list.add(string);
            }
        }
        return list;
    }
}
