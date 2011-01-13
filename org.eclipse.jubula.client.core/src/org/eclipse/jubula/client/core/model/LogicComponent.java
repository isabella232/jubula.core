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
 * This class represents the logic name of a component. <br>
 * 
 * It comprises the name (defined by the user) and the component type.
 *
 * @author BREDEX GmbH
 * @created 14.10.2004
 */
public class LogicComponent {

    /** the name of the component */
    private String m_name;
    
    /** the type of the component */
    private String m_type;
    
    /**
     * public empty constructor
     */
    public LogicComponent() {
        super();
    }
    
    /**
     * public constructor with name and type as parameters, no checks are
     * performed for the parameters
     * 
     * @param name
     *            the user defined name of the logic compopnent
     * @param type
     *            the fully qualified type of the component, like
     *            javax.swing.JButton
     */
    public LogicComponent(String name, String type) {
        this();
        m_name = name;
        m_type = type;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * @return Returns the type.
     */
    public String getType() {
        return m_type;
    }
    
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        m_type = type;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof LogicComponent) {
            LogicComponent logic = (LogicComponent)obj;
            if (logic.getName().equals(getName())) {
//                && logic.getType().equals(getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (getName() != null) {
            return getName().hashCode();
        }
        return 0;
    }
    

}
