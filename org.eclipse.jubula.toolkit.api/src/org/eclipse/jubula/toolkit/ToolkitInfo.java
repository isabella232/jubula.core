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
package org.eclipse.jubula.toolkit;

import java.util.Map;

import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;

/**
 * Contains information about a toolkit and its components
 * @author BREDEX GmbH
 * @created 15.10.2014
 */
public interface ToolkitInfo {
    
    /**
     * @return a map containing the mappings from a component class
     * to the name of its tester class
     */
    public Map<ComponentClass, String> getTypeMapping();
    
    /**
     * @return the toolkit id
     */
    public String getToolkitID();
}
