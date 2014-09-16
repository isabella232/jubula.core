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
package org.eclipse.jubula.tools.internal.xml.businessmodell;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the technical component class and it's properties
 * 
 * @author BREDEX GmbH
 * @created Nov 12, 2009
 * 
 */
public class ComponentClass {
    /** the component class name */
    private String m_name;

    /** the optional properties for each component class */
    private List m_properties = new ArrayList();

    /** default constructor used by xstream */
    public ComponentClass() {
        // default
    }
    
    /** @param componentClass the component class name */
    public ComponentClass(String componentClass) {
        setName(componentClass);
    }

    /** @param name the name to set */
    public void setName(String name) {
        m_name = name;
    }

    /** @return the name */
    public String getName() {
        return m_name;
    }

    /** @param properties the properties to set */
    public void setProperties(List properties) {
        m_properties = properties;
    }

    /** @return the properties */
    public List getProperties() {
        return m_properties;
    }
}