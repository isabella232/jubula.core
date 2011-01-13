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
package org.eclipse.jubula.client.ui.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.RefTestSuiteGUIPropertySource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public class RefTestSuiteGUI extends GuiNode implements IAdaptable {
    /** The RefTestSuiteGUIPropertySource for properties view */
    private RefTestSuiteGUIPropertySource m_rtsPropSource;

    /**
     * Constructs Referenced Test Suite and sets it into the given parent.
     * 
     * @param name
     *            the name
     * @param parent
     *            the parent
     * @param refTS
     *            the content
     */
    public RefTestSuiteGUI(String name, GuiNode parent, IRefTestSuitePO refTS) {
        this(name, parent, refTS, true);
    }

    /**
     * Constructs Referenced Test Suite and sets it into the given parent.
     * 
     * @param name
     *            the name
     * @param parentGUI
     *            the parent
     * @param refTS
     *            the content
     * @param isEditable
     *            wheter it is editable or not
     */
    public RefTestSuiteGUI(String name, GuiNode parentGUI,
            IRefTestSuitePO refTS, boolean isEditable) {
        super(name, parentGUI, refTS, isEditable);
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.TS_REF_IMAGE;
    }

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_rtsPropSource == null) {
                m_rtsPropSource = new RefTestSuiteGUIPropertySource(this);
            }
            return m_rtsPropSource;
        }
        return null;
    }

}
