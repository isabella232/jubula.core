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
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.jubula.tools.i18n.I18n;


/**
 * @author BREDEX GmbH
 * @created Jun 28, 2010
 */
@Entity
@Table(name = "CUBE_CONT")
public class TestDataCubeContPO extends WrapperPO implements
        ITestDataCubeContPO {
    /**
     * <code>m_testDataCubes</code>
     */
    private List<IParameterInterfacePO> m_testDataCubes = 
        new ArrayList<IParameterInterfacePO>();

    /** default constructor */
    TestDataCubeContPO() {
        // nothing
    }

    /**
     * 
     * @return Returns the topLevelNodeList.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = TestDataCubePO.class, 
               orphanRemoval = true)
    @OrderColumn(name = "IDX")
    List<IParameterInterfacePO> getHbmTestDataCubeList() {
        return m_testDataCubes;
    }

    /**
     * @return an unmodifiable list of test data cubes
     */
    @Transient
    public List<IParameterInterfacePO> getTestDataCubeList() {
        return Collections.unmodifiableList(getHbmTestDataCubeList());
    }

    /**
     * @param tdcl
     *            test data cube list The test data cube list to set.
     */
    void setHbmTestDataCubeList(List<IParameterInterfacePO> tdcl) {
        m_testDataCubes = tdcl;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return I18n.getString("CentralTestDataEditor.name"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void addTestDataCube(IParameterInterfacePO tdc) {
        getHbmTestDataCubeList().add(tdc);
        tdc.setParentProjectId(getParentProjectId());
    }

    /**
     * {@inheritDoc}
     */
    public void removeTestDataCube(IParameterInterfacePO tdc) {
        getHbmTestDataCubeList().remove(tdc);
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
    //           when running GUIdancer from the IDE. The exceptions only occur 
    //           in a deployed GUIdancer. Once this problem is resolved (for a 
    //           deployed GUIdancer), the workaround can be removed.
    public Long getParentProjectId() {
        return super.getParentProjectId();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (IParameterInterfacePO tdc : getHbmTestDataCubeList()) {
            tdc.setParentProjectId(projectId);
        }
    }
}
