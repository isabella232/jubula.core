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
package org.eclipse.jubula.rc.swt.implclasses;

import org.eclipse.swt.widgets.Control;

/**
 * Tester Class for components designated as Graphics Components via the 
 * Simple Component Extension mechanism.
 * 
 * @author BREDEX GmbH
 * @created 15.08.2011
 */
public class SimpleExtendedComponentImplClass extends AbstractControlImplClass {

    /** the tested component */
    private Control m_control;

    /**
     * 
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_control = (Control)graphicsComponent;
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
    public Control getComponent() {
        return m_control;
    }

}
