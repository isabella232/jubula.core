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
 * Interface for a Data Set, which is capable of storing and retrieving 
 * Test Data for given Parameters.
 * 
 * @author BREDEX GmbH
 * @created 20.12.2005
 *
 *
 *
 *
 */
public interface IListWrapperPO extends IPersistentObject {
    /**
     * @return Returns the list.
     */
    public abstract List<ITestDataPO> getList();

    /**
     * @return empty string
     */
    public abstract String getName();

    /**
     * @param column The column
     * @return The test data entry
     */
    public ITestDataPO getColumn(int column);

    /**
     * @return The number of columns
     */
    public int getColumnCount();

    /**
     * Adds a new test data.
     * 
     * @param testData
     *            The test data
     */
    public void addColumn(ITestDataPO testData);

    /**
     * Removes the column.
     * 
     * @param column
     *            The column index
     */
    public void removeColumn(int column);
}