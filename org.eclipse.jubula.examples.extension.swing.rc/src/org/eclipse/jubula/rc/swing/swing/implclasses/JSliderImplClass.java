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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import javax.swing.JComponent;
import javax.swing.JSlider;

/**
 * Tester Class for the AutStarter. This class realizes the technical access to
 * provide testability for new component type: JSlider. By implementing the
 * abstract class "AbstractSwingImplClass" you only have to implement a few
 * methods to enable testability of your new component on the
 * "Graphics Component"-level. That means all actions which are available for
 * the Jubula "Graphics Component" should work for your new component.
 * 
 * @author BREDEX GmbH
 * 
 */
public class JSliderImplClass extends AbstractSwingImplClass {
    /** the managed component */
    private JSlider m_component;

    /**
     * getter for the new supported component
     * 
     * @return the component
     */
    public JComponent getComponent() {
        return m_component;
    }

    /**
     * as JSlider is no component which by default displays any kind of text,
     * this method should return null
     * 
     * @return null in case of a non-textual component
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     * setter for the new supported component
     * 
     * @param graphicalObject
     *            the ui object to set
     */
    public void setComponent(Object graphicalObject) {
        m_component = (JSlider)graphicalObject;
    }

    /**
     * new since 3.2
     * @return the descriptive text of the component
     */
    protected String getText() {
        return null;
    }
}
