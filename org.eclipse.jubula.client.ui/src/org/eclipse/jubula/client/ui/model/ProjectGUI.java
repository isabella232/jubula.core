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
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.ProjectGUIPropertySource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author BREDEX GmbH
 * @created 02.12.2005
 */
public class ProjectGUI extends GuiNode implements IAdaptable {

    /** The propertySource for the Properties View */
    private IPropertySource m_propSource;
    
    
    /**
     * @param name name
     */
    public ProjectGUI(String name) {
        this(name, null, null, null);
    }

    /**
     * @param name name
     * @param parent parent
     * @param content content
     */
    public ProjectGUI(String name, GuiNode parent, INodePO content) {
        this(name, parent, content, null);
    }

    /**
     * @param name name
     * @param parent parent
     * @param content content
     * @param pos pos
     */
    public ProjectGUI(String name, GuiNode parent, INodePO content, 
        Integer pos) {
        
        super(name, parent, content, pos);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public Image getImage() {
        return IconConstants.PROJECT_IMAGE;
    }

    /**
     * For implementation of IAdaptable.
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_propSource == null) {
                m_propSource = new ProjectGUIPropertySource(this);
            }
            return m_propSource;
        }
        return null;
    }

}
