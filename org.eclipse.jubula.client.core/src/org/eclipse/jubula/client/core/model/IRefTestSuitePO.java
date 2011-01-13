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

/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public interface IRefTestSuitePO extends INodePO {
    /** access to hibernate property 
     * @return the property
     * */
    public String getTestSuiteGuid();

    /** access to hibernate property 
     * @param testSuiteGuid GUID if the referenced TS */
    public void setTestSuiteGuid(String testSuiteGuid);

    /** access to hibernate property
    * @return the property
    */
    public String getTestSuiteAutID();

    /** access to hibernate property 
     * @param testSuiteAutID ID of the used AUT */
    public void setTestSuiteAutID(String testSuiteAutID);
}
