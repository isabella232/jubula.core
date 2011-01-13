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
 * class to manage top level objects of Test Job Browser
 * 
 * @author BREDEX GmbH
 * @created 02.12.2005
 */
@Entity
@Table(name = "TEST_JOB_CONT")
public class TestJobContPO extends WrapperPO implements ITestJobContPO {

    /**
     * <code>m_TestJobList</code> list of all TestJobs
     */
    private List<ITestJobPO> m_testJobList = new ArrayList<ITestJobPO>();

    /** default constructor */
    TestJobContPO() {
    // nothing
    }

    /**
     * 
     * @return Returns the topLevelNodeList.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = TestJobPO.class)
    @OrderColumn(name = "IDX")
    List<ITestJobPO> getHbmTestJobList() {
        return m_testJobList;
    }

    /**
     * @return an unmodifiable list of TestJobs
     */
    @Transient
    public List<ITestJobPO> getTestJobList() {
        return Collections.unmodifiableList(m_testJobList);
    }

    /**
     * @param testJobList
     *            The TestJobList to set.
     */
    void setHbmTestJobList(List<ITestJobPO> testJobList) {
        m_testJobList = testJobList;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return "TestJobContPO"; //$NON-NLS-1$
    }

    /**
     * @param ts
     *            TestJob to add
     */
    public void addTestJob(ITestJobPO ts) {
        addTestJob(-1, ts);
    }

    /**
     * @param position
     *            position of TestJob to add in TestJobList
     * @param ts
     *            TestJob to add
     */
    public void addTestJob(int position, ITestJobPO ts) {
        if (position < 0 || position > getHbmTestJobList().size()) {
            getHbmTestJobList().add(ts);
        } else {
            getHbmTestJobList().add(position, ts);
        }
        ts.setParentProjectId(getParentProjectId());
    }

    /**
     * @param ts
     *            TestJob to remove
     */
    public void removeTestJob(ITestJobPO ts) {
        getHbmTestJobList().remove(ts);
        ts.setParentNode(null);
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (ITestJobPO ts : getHbmTestJobList()) {
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
    //           when running GUIdancer from the IDE. The exceptions only occur 
    //           in a deployed GUIdancer. Once this problem is resolved (for a 
    //           deployed GUIdancer), the workaround can be removed.
    public Long getParentProjectId() {
        return super.getParentProjectId();
    }
    
}
