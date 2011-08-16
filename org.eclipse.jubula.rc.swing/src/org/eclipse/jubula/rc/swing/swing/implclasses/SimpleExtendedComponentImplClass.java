/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.swing.implclasses;

import javax.swing.JComponent;

/**
 * Tester Class for components designated as Graphics Components via the 
 * Simple Component Extension mechanism.
 * 
 * @author BREDEX GmbH
 * @created 15.08.2011
 */
public class SimpleExtendedComponentImplClass extends AbstractSwingImplClass {

    /** the tested component */
    private JComponent m_component;
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_component = (JComponent)graphicsComponent;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_component;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected String getText() {
        return null;
    }

}
