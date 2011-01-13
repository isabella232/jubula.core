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

import java.util.Set;

/**
 * Handles mapping Component Names between memory and persistence layer.
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2009
 */
public interface IComponentNameMapper {


    /**
     * Finds all Component Types for which the Component Name with GUID 
     * <code>compNameGuid</code> is used. Examples of Component Types include
     * "Graphics Component" and "Table".
     * 
     * @param compNameGuid The GUID of the of the Component Name for which to
     *                     perform the check.
     * @return all Component Types for which the Component Name with the given
     *         GUID is used.
     */
    public Set<String> getUsedTypes(String compNameGuid);

    /**
     * 
     * @return the cache for Component Names.
     */
    public IComponentNameCache getCompNameCache();

}
