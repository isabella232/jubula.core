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
package org.eclipse.jubula.tools.objects;

import org.apache.commons.lang.Validate;

/**
 * Represents a pair of a graphics component class name and its corresponding
 * implementation class (or tester class) name. This pair is extracted from
 * the GuiDancer XML configuration file.
 *
 * {@inheritDoc}
 * 
 * @author BREDEX GmbH
 * @created 01.04.2005
 */
public class ComponentImplClassPair {
    /**
     * The component class name.
     */
    private String m_componentClassName;
    /**
     * The implementation class name.
     */
    private String m_implClassName;
    /**
     * Default constructor (required by Betwixt).
     */
    public ComponentImplClassPair() {
        // Nothing to be done.
    }
    /**
     * Creates a new instance.
     * 
     * @param componentClassName
     *            The graphics component class name.
     * @param implClassName
     *            The implementation class (tester class) class name.
     * @throws IllegalArgumentException
     *             If one of the passed arguments is <code>null</code>.
     */
    public ComponentImplClassPair(String componentClassName,
        String implClassName) throws IllegalArgumentException {
        Validate.notNull(componentClassName);
        Validate.notNull(implClassName);
        m_componentClassName = componentClassName;
        m_implClassName = implClassName;
    }
    /**
     * @return The graphics component class name.
     */
    public String getComponentClassName() {
        return m_componentClassName;
    }
    /**
     * @param componentClassName
     *            The graphics component class name (required by Betwixt).
     */
    public void setComponentClassName(String componentClassName) {
        m_componentClassName = componentClassName;
    }
    /**
     * @return The implementation class name.
     */
    public String getImplClassName() {
        return m_implClassName;
    }
    /**
     * @param implClassName The implementation class name (required by Betwixt).
     */
    public void setImplClassName(String implClassName) {
        m_implClassName = implClassName;
    }
}
