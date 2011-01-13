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
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.TestJobGUIPropertySource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public class TestJobGUI extends GuiNode implements IAdaptable {
    /** The TestJobGUIPropertySource for properties view */
    private TestJobGUIPropertySource m_tjPropSource;

    /**
     * Constructs Test Job and sets it into the given parent.
     * 
     * @param name
     *            the name
     * @param parent
     *            the parent
     * @param testJob
     *            the content
     */
    public TestJobGUI(String name, GuiNode parent, ITestJobPO testJob) {
        this(name, parent, testJob, true);
    }

    /**
     * Constructs Test Job.
     * 
     * @param name
     *            the name
     * @param testJob
     *            the content
     * @param isEditable
     *            wheter it is editable or not
     */
    public TestJobGUI(String name, ITestJobPO testJob, boolean isEditable) {
        this(name, null, testJob, isEditable);
    }

    /**
     * Constructs Test Job and sets it into the given parent.
     * 
     * @param name
     *            the name
     * @param parentGUI
     *            the parent
     * @param testJob
     *            the content
     * @param isEditable
     *            wheter it is editable or not
     */
    public TestJobGUI(String name, GuiNode parentGUI, ITestJobPO testJob,
            boolean isEditable) {
        super(name, parentGUI, testJob, isEditable);
    }

    /**
     * @param name
     *            the name
     */
    public TestJobGUI(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.TJ_IMAGE;
    }

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_tjPropSource == null) {
                m_tjPropSource = new TestJobGUIPropertySource(this);
            }
            return m_tjPropSource;
        }
        return null;
    }

}
