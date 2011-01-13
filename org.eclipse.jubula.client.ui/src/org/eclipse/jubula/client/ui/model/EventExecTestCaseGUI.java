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
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.EventExecTestCaseGUIPropertySource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author BREDEX GmbH
 * @created 04.04.2005
 */
public class EventExecTestCaseGUI extends ExecTestCaseGUI 
    implements IAdaptable {

    /** The ExecTestCaseGUIPropertySource for properties view */
    private EventExecTestCaseGUIPropertySource m_eventTcPropSource;

    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     */
    public EventExecTestCaseGUI(String name, SpecTestCaseGUI parent, 
        IEventExecTestCasePO content) {
        
        super(name, parent, null, content);
    }
    
    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     * @param isEditable whether or not this GuiNode is editable
     */
    public EventExecTestCaseGUI(String name, SpecTestCaseGUI parent, 
        IEventExecTestCasePO content, boolean isEditable) {
        
        super(name, parent, null, content, isEditable);
    }

    /**
     * For implementation of IAdaptable.
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_eventTcPropSource == null) {
                m_eventTcPropSource = 
                    new EventExecTestCaseGUIPropertySource(this);
            }
            return m_eventTcPropSource;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.EH_IMAGE;
    }
}