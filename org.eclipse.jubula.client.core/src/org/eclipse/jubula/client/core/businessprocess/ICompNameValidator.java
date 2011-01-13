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
package org.eclipse.jubula.client.core.businessprocess;

/**
 * Validates use of Component Names with regard to component type.
 *
 * @author BREDEX GmbH
 * @created Jan 30, 2009
 */
public interface ICompNameValidator {

    /**
     * Validates whether the Component Name with name 
     * <code>checkableName</code> can be used for component type 
     * <code>type</code>.
     * 
     * @param type The type to use for validation.
     * @param checkableName The name of the Component Name to use for 
     *                      validation.
     * @return a <code>String</code> representing an error message if the
     *         Component Name is invalid. Otherwise, <code>null</code>.
     */
    public String isValid(String type, String checkableName);
}
