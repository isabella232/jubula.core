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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * @author BREDEX GmbH
 * @created 29.04.2008
 */
@Entity
@Table(name = "DOC_ATTR")
class DocAttributePO implements IDocAttributePO {

    /** hibernate OID */
    private transient Long m_id = null;

    /** hibernate version id */
    private transient Integer m_version = null;
    
    /** sub-attributes */
    private Map<IDocAttributeDescriptionPO, IDocAttributeListPO> 
    m_subAttributes = 
        new HashMap<IDocAttributeDescriptionPO, IDocAttributeListPO>();

    /** The value for the attribute */
    private String m_value;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unused")
    private DocAttributePO() {
        // For hibernate
    }

    /**
     * Constructor.
     * 
     * @param initialValue The initial value for this attribute.
     */
    DocAttributePO(String initialValue) {
        setValue(initialValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public String getValue() {
        return getHbmValue();
    }
    /**
     * {@inheritDoc}
     */
    public void setValue(String newValue) {
        setHbmValue(newValue);
    }

    /**
     * Hibernate getter.
     * 
     * @return the value for this attribute.
     */
    @Basic
    @Column(name = "VALUE")
    private String getHbmValue() {
        return m_value;
    }
    /**
     * Hibernate setter.
     * 
     * @param newValue The new value for the attribute.
     */
    private void setHbmValue(String newValue) {
        m_value = newValue;
    }

    /**
     * 
     * @return The map of documentation attributes.
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, 
                targetEntity = DocAttributeListPO.class)
    @JoinTable(name = "SUB_DOC_ATTR_ASSOC", 
               joinColumns = @JoinColumn(name = "PARENT"), 
               inverseJoinColumns = @JoinColumn(name = "ATTR"))
    @MapKeyJoinColumn(name = "ATTR_DESC")
    @MapKeyClass(value = DocAttributeDescriptionPO.class)
    private Map<IDocAttributeDescriptionPO, IDocAttributeListPO> 
    getDocAttributeMap() {
    
        return m_subAttributes;
    }
    /**
     * 
     * @param map The new map.
     */
    @SuppressWarnings("unused")
    private void setDocAttributeMap(
            Map<IDocAttributeDescriptionPO, IDocAttributeListPO> map) {
        
        m_subAttributes = map;
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
     * @return Long
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }
    /**
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }

    /**
     * {@inheritDoc}
     */
    public IDocAttributeListPO getDocAttributeList(
            IDocAttributeDescriptionPO attributeType) {

        return getDocAttributeMap().get(attributeType);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Set<IDocAttributeDescriptionPO> getDocAttributeTypes() {
        return Collections.unmodifiableSet(getDocAttributeMap().keySet());
    }

    /**
     * {@inheritDoc}
     */
    public void setDocAttributeList(IDocAttributeDescriptionPO attributeType,
            IDocAttributeListPO attribute) {

        getDocAttributeMap().put(attributeType, attribute);
    }
}
