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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.businessprocess.TestDataBP;
import org.eclipse.jubula.tools.constants.StringConstants;


/**
 * utility class for management of values or references 
 *
 * @author BREDEX GmbH
 * @created 08.12.2004
 */
@Entity
@Table(name = "TEST_DATA")
class TestDataPO implements ITestDataPO {
    /** hibernate OID */
    private transient Long m_id = null;
    
    /**
     * <code>m_value</code>internationalized value for a parameter
     */
    private II18NStringPO m_value = null;
    
    /** hibernate version id */
    private transient Integer m_version = null;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * constructor for TestDataPO with value
     * @param value value for a parameter
     */
    TestDataPO(II18NStringPO value) {
        setValue(value);
    }
    
    /**
     *  constructor only for hibernate
     */
    TestDataPO() {
        // only for hibernate
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
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        if (getValue() != null) {
            m_value.setParentProjectId(projectId);
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
     * 
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }
    

    /**
     * 
     * @return I18NStringPO
     */
    @OneToOne(cascade = CascadeType.ALL, 
              fetch = FetchType.EAGER, 
              targetEntity = I18NStringPO.class)
    @JoinColumn(name = "FK_I18N_STR")
    public II18NStringPO getValue() {
        return m_value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(II18NStringPO value) {
        m_value = value;
        if (value != null) {
            setParentProjectId(value.getParentProjectId());
        }
    }
    
    
   /**
    * Overides Object.equals()
    * Compares this TestDataPO object to the given object to equality.
    * @param obj the object to compare.
    * @return true or false
    * {@inheritDoc}
    */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TestDataPO || obj instanceof ITestDataPO) {
            ITestDataPO testData = (ITestDataPO) obj;
            return new EqualsBuilder()
                .append(getValue(), testData.getValue()).isEquals();
        }
        return false;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(getValue()).toHashCode();
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
     * Creates a deep copy of this instance.
     * 
     * @return The new test data instance
     */
    public ITestDataPO deepCopy() {
        ITestDataPO td = TestDataBP.instance().createEmptyTestData();
        if (getValue() != null) {
            td.setValue(getValue().deepCopy());
        }
        return td;
    }
    
}
