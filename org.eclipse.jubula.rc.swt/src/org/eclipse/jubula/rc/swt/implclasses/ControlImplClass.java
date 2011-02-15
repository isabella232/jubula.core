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
package org.eclipse.jubula.rc.swt.implclasses;

import org.eclipse.swt.widgets.Control;

/**
 * @author BREDEX GmbH
 * @created Feb 14, 2011
 */
public class ControlImplClass extends AbstractControlImplClass {
    /**
     * <code>m_graphicsComponent</code>
     */
    private Control m_graphicsComponent;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_graphicsComponent = (Control)graphicsComponent;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_graphicsComponent;
    }
}
