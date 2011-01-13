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
 * @created 20.12.2005
 *
 *
 *
 *
 */
public interface ITestDataPO extends IPersistentObject {
  
    /**
     * @return the value
     */
    public abstract II18NStringPO getValue();

    /**
     * @param value The value to set.
     */
    public abstract void setValue(II18NStringPO value);

    /**
     * Overides Object.equals()
     * Compares this TestDataPO object to the given object to equality.
     * @param obj the object to compare.
     * @return true or false
     * {@inheritDoc}
     */
    public abstract boolean equals(Object obj);

    /**
     * @return empty string
     */
    public abstract String getName();

    /**
     * Creates a deep copy of this instance.
     * 
     * @return The new test data instance
     */
    public abstract ITestDataPO deepCopy();
}