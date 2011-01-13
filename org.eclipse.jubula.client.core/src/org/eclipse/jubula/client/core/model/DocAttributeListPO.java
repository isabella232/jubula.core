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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * @author BREDEX GmbH
 * @created 29.04.2008
 */
@Entity
@Table(name = "DOC_ATTR_LIST_PO")
class DocAttributeListPO implements IDocAttributeListPO {

    /** hibernate OID */
    private transient Long m_id = null;

    /** hibernate version id */
    private transient Integer m_version = null;
    
    /** all attributes in this list */
    private List<IDocAttributePO> m_attributeList = 
        new ArrayList<IDocAttributePO>();
    
    /**
     * Constructor
     */
    DocAttributeListPO() {
        // nothing to initialize
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @ManyToMany(cascade = CascadeType.ALL, 
                fetch = FetchType.LAZY, 
                targetEntity = DocAttributePO.class)
    @OrderColumn(name = "IDX")
    @JoinColumn(name = "FK_DOC_ATTR_LIST")
    public List<IDocAttributePO> getAttributes() {
        return m_attributeList;
    }

    /**
     * 
     * @param attributes The attributes to set.
     */
    @SuppressWarnings("unused")
    private void setAttributes(List<IDocAttributePO> attributes) {
        m_attributeList = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public void addAttribute(IDocAttributePO toAdd) {
        m_attributeList.add(toAdd);
    }

    /**
     * {@inheritDoc}
     */
    public void removeAttribute(IDocAttributePO toRemove) {
        m_attributeList.remove(toRemove);
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
}
