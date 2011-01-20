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

/**
 * class to manage top level objects of Test Suite Browser
 *
 * @author BREDEX GmbH
 * @created 02.12.2005
 *
 *
 *
 *
 */
@Entity
@Table(name = "SUITE_CONT")
public class TestSuiteContPO extends WrapperPO implements ITestSuiteContPO {
    
    /**
     * <code>m_testSuiteList</code> list of all testsuites
     */
    private List<ITestSuitePO> m_testSuiteList = new ArrayList<ITestSuitePO>();

    /** default constructor */
    TestSuiteContPO() {
        // nothing
    }
    
    /**
     * 
     * @return Returns the topLevelNodeList.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = TestSuitePO.class)
    @OrderColumn(name = "IDX")
    List<ITestSuitePO> getHbmTestSuiteList() {
        return m_testSuiteList;
    }
    
    /**
     * @return an unmodifiable list of testsuites
     */
    @Transient
    public List<ITestSuitePO> getTestSuiteList() {
        return Collections.unmodifiableList(m_testSuiteList);
    }

    /**
     * @param testSuiteList The testSuiteList to set.
     */
    void setHbmTestSuiteList(List<ITestSuitePO> testSuiteList) {
        m_testSuiteList = testSuiteList;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "TestSuiteContPO"; //$NON-NLS-1$
    }

    /**
     * @param ts testsuite to add
     */
    public void addTestSuite(ITestSuitePO ts) {
        addTestSuite(-1, ts);
    }
    
    /**
     * @param position position of testsuite to add in testsuiteList
     * @param ts testsuite to add
     */
    public void addTestSuite(int position, ITestSuitePO ts) {
        if (position < 0 || position > getHbmTestSuiteList().size()) {
            getHbmTestSuiteList().add(ts);
        } else {
            getHbmTestSuiteList().add(position, ts);
        }
        ts.setParentProjectId(getParentProjectId());
    }
    
    
    /**
     * @param ts testsuite to remove
     */
    public void removeTestSuite(ITestSuitePO ts) {
        getHbmTestSuiteList().remove(ts);
        ts.setParentNode(null);
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (ITestSuitePO ts : getHbmTestSuiteList()) {
            ts.setParentProjectId(projectId);
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
