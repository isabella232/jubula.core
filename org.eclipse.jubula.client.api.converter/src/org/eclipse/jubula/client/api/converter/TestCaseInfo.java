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
package org.eclipse.jubula.client.api.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.ITestCasePO;

/**
 *  Information for Creating a Java Class corresponding to a Test Case
 *  @created 28.10.2014
 */
public class TestCaseInfo {
    
    /** The class name of the test case */
    private String m_className;

    /** The base path of the package */
    private String m_packageBasePath;
    
    /** The test case */
    private ITestCasePO m_testCase;
    
    /**
     * @param className the class name
     * @param testCase the test case
     * @param packageBasePath the base path of the package
     */
    public TestCaseInfo (String className, ITestCasePO testCase,
            String packageBasePath) {
        m_className = StringUtils.substringBeforeLast(className, ".java"); //$NON-NLS-1$
        m_testCase = testCase;
        m_packageBasePath = packageBasePath;
    }
    
    /**
     * @return The class name of the test case
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * @return The base path of the package
     */
    public String getPackageBasePath() {
        return m_packageBasePath;
    }
    
    /**
     * @return The test case
     */
    public ITestCasePO getTestCase() {
        return m_testCase;
    }
}
