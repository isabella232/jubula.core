/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.archive.schema.ExecCategory;
import org.eclipse.jubula.client.archive.schema.Project;
import org.eclipse.jubula.client.archive.schema.RefTestSuite;
import org.eclipse.jubula.client.archive.schema.TestJobs;
import org.eclipse.jubula.client.archive.schema.TestJobs.Testjobelement;
import org.eclipse.jubula.client.archive.schema.TestSuite;

/**
 * This converter deletes the name from {@link RefTestSuite} if it is the same
 * name as from the {@link TestSuite}. The converter is necessary since we have
 * copied the name from {@link TestSuite}s before we fixed
 * http://eclip.se/451113
 * 
 * @author BREDEX GmbH
 */
public class RefTSNameConverter extends AbstractXmlConverter {

    /** The highest meta data version number, which have to be converted. */
    private static final int HIGHEST_META_DATA_VERSION_NUMBER = 6;

    /** map of test Suites */
    private Map<String, TestSuite> m_testSuiteMap = 
            new HashMap<String, TestSuite>();
    /** list of test jobs */
    private List<TestJobs> m_testJobs = new ArrayList<TestJobs>();

    /**
     * {@inheritDoc}
     */
    protected boolean conversionIsNecessary(Project xml) {
        return xml.getMetaDataVersion() <= HIGHEST_META_DATA_VERSION_NUMBER;
    }

    /**
     * removes ref test suite names if they are the same as the name of the
     * specification test suite 
     * {@inheritDoc}
     */
    protected void convertImpl(Project xml) {
        List<ExecCategory> execList = xml.getExecCategoriesList();
        for (ExecCategory execCategory : execList) {
            getAllTestJobsAndSuites(execCategory.getCategoryList());
        }

        for (TestJobs jobs : m_testJobs) {
            for (Testjobelement element : jobs.getTestjobelementList()) {
                if (element instanceof RefTestSuite) {
                    RefTestSuite refTS = (RefTestSuite) element;
                    TestSuite ts = m_testSuiteMap.get(refTS.getTsGuid());
                    if (ts != null) {
                        if (StringUtils.equals(refTS.getName(), ts.getName())) {
                            refTS.setName(null);
                        }
                    }
                }
            }
        }

    }

    /**
     * searches recursive for all {@link TestJobs} and {@link TestSuite}s
     * an puts them in the corresponding list and map
     * @param execList the {@link ExecCategory} list
     */
    private void getAllTestJobsAndSuites(List<ExecCategory> execList) {
        if (execList.size() > 0) {
            for (ExecCategory execCategory : execList) {
                for (TestSuite ts : execCategory.getTestsuiteList()) {
                    m_testSuiteMap.put(ts.getGUID(), ts);
                }
                for (TestJobs job : execCategory.getTestjobList()) {
                    m_testJobs.add(job);
                }
                getAllTestJobsAndSuites(execCategory.getCategoryList());
            }
        }
    }
}
