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
package org.eclipse.jubula.client;

import java.net.URL;

import org.eclipse.jubula.tools.ComponentIdentifier;

/**
 * Utility class for loading object mapping associations
 * 
 * @author BREDEX GmbH
 * @created Oct 13, 2014
 */
public interface OM {
    /**
     * Initializes the object mapping associations
     * 
     * @param resourceURL
     *            the URL to the resource properties file
     */
    public void init(URL resourceURL);

    /**
     * Returns the component identifier for a component name from the cache
     * 
     * @param compName
     *            the component name
     * @return the component identifier or <code>null</code> if no identifier
     *         was found
     */
    public ComponentIdentifier get(String compName);
}
