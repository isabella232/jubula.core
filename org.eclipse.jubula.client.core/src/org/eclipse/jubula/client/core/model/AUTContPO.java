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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author BREDEX GmbH
 * @created Jun 11, 2007
 */
@Entity
@Table(name = "AUT_CONT")
class AUTContPO extends WrapperPO implements IAUTContPO {
    /**
     * <code>DEFAULT_NUMBER_OF_AUTS</code> the default number of AUTs to hold
     */
    public static final int DEFAULT_NUMBER_OF_AUTS = 2;
    
    /** the list of AUTs, that belong to a project */ 
    private Set<IAUTMainPO> m_autMainList = 
        new HashSet<IAUTMainPO>(DEFAULT_NUMBER_OF_AUTS);

    /**
     * Persistence (JPA / EclipseLink) constructor
     */
    AUTContPO() {
        // only for Persistence (JPA / EclipseLink)
    }
    
    /**
     * 
     * @return Returns the autMainList.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = AUTMainPO.class)
    @JoinColumn(name = "FK_AUT_CONT")
    public Set<IAUTMainPO> getAutMainList() {
        return m_autMainList;
    }
    /**
     * only for Persistence (JPA / EclipseLink)
     * @param autMainSet The autMainList to set.
     */
    void setAutMainList(Set<IAUTMainPO> autMainSet) {
        m_autMainList = autMainSet;
    }
    /**
     * Adds an AUT to a project.
     * @param aut The AUT to add.
     */
    public void addAUTMain(IAUTMainPO aut) {
        getAutMainList().add(aut);
        aut.setParentProjectId(getParentProjectId());
    }
    /**
     * Removes an AUT from this container.
     * @param aut The AUT to remove.
     */
    public void removeAUTMain(IAUTMainPO aut) {
        getAutMainList().remove(aut);
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (IAUTMainPO aut : getAutMainList()) {
            aut.setParentProjectId(projectId);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "AUTContPO"; //$NON-NLS-1$
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
    
    /** {@inheritDoc} */
    @Override
    @Id
    @GeneratedValue
    // FIXME : workaround as described in http://bugs.eclipse.org/411284#c0
    public Long getId() {
        return super.getId();
    }
}