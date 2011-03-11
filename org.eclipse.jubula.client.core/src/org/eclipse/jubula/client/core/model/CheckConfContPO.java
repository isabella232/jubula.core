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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author BREDEX GmbH
 * @created Nov 17, 2010
 */
@Entity
@Table(name = "CHECK_CONF_CONT")
class CheckConfContPO extends WrapperPO implements ICheckConfContPO {
    
    /** is teststyle enabled? */
    private boolean m_enabled = true;

    /** map of the checkconf and the checkid */
    private Map<String, CheckConfPO> m_confMap = 
        new HashMap<String, CheckConfPO>();

    /**
     * {@inheritDoc}
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "CHECK_CONF_CONT_MAP")
    @MapKeyColumn(name = "CHECK_CONF_KEY", nullable = false, 
                  table = "CHECK_CONF_CONT_MAP")
    public Map<String, CheckConfPO> getConfMap() {
        return m_confMap;
    }
    
    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "ENABLED")
    public boolean getEnabled() {
        return m_enabled;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
    }
    

    /**
     * @param confMap
     *            the confMap to set
     */
    public void setConfMap(Map<String, CheckConfPO> confMap) {
        m_confMap = confMap;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return toString();
    }

    /**
     * {@inheritDoc}
     */
    public void addCheckConf(String chkId, ICheckConfPO cfg) {
        m_confMap.put(chkId, (CheckConfPO)cfg);
    }

    /**
     * {@inheritDoc}
     */
    public CheckConfPO getCheckConf(String chkId) {
        return m_confMap.get(chkId);
    }

    /**
     * {@inheritDoc}
     */
    public ICheckConfPO createCheckConf() {
        return new CheckConfPO();
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
