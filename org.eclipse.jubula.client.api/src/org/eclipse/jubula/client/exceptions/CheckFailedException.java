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
package org.eclipse.jubula.client.exceptions;

/** @author BREDEX GmbH */
public class CheckFailedException extends ExecutionException {

    /** the actualValue */
    private String m_actualValue;

    /**
     * Constructor
     * 
     * @param actualValue
     *            the actual value
     * @param message
     *            the message
     */
    public CheckFailedException(String message, String actualValue) {
        super(message);
        setActualValue(actualValue);
    }

    /**
     * @return the actualValue
     */
    public String getActualValue() {
        return m_actualValue;
    }

    /**
     * @param actualValue
     *            the actualValue to set
     */
    private void setActualValue(String actualValue) {
        m_actualValue = actualValue;
    }
}