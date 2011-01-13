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
package org.eclipse.jubula.rc.common.components;

import org.apache.commons.lang.Validate;


/**
 * @author BREDEX GmbH
 * @created 11.05.2006
 */
public abstract class AUTComponent {
    /** Component from the AUT. This may be null if no actual component
     * was used, i.e. the ID was generated for inheretance checking.
     */
    private Object m_component = null;

    /** the name of the compID */
    private String m_name;
    
    /**
     * create an instance from a Swing component. This constructor is used when
     * working with real instances instead of mere class descriptions.
     * <p>
     * Swing: use Component as parameter
     * <p>
     * SWT: use Widget as parameter
     * @param component Base for identification
     */
    public AUTComponent(Object component) {
        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$
        m_component = component;
    }

    /**
     * @return Returns the component.
     * you have to implement:
     * <p>Swing: Component getComponent()
     * <p>SWT  : Widget getComponent()
     */
    protected Object getComp() {
        return m_component;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof AUTComponent)) {
            return false;
        }
        if (obj == this) {
            return true; // a case of identity
        }
        AUTComponent o = (AUTComponent)obj;
        return m_component.equals(o.m_component);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return m_component.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("ComponentID: "); //$NON-NLS-1$
        sb.append(m_component.getClass().getName());
        sb.append(", CL: "); //$NON-NLS-1$
        sb.append(m_component.getClass().getClassLoader());
        return  sb.toString();
    }

    /**
     * @return the compID name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name the compID name to set
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /** set the aut component 
     * @param comp the comp to set
     */
    protected void setComp(Object comp) { 
        m_component = comp;
    }
}