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

import java.util.List;

/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface ITestSuiteContPO extends IPersistentObject {

    /**
     * @return an unmodifiable list of testsuites
     */
    public abstract List<ITestSuitePO> getTestSuiteList();

    /**
     * @param ts testsuite to add
     */
    public abstract void addTestSuite(ITestSuitePO ts);

    /**
     * @param position position of testsuite to add in testsuiteList
     * @param ts testsuite to add
     */
    public abstract void addTestSuite(int position, ITestSuitePO ts);

    /**
     * @param ts testsuite to remove
     */
    public abstract void removeTestSuite(ITestSuitePO ts);
}