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

import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.swt.graphics.Image;


/**
 * GUI node for a Reused Project
 *
 * @author BREDEX GmbH
 * @created Oct 16, 2007
 */
public class ReusedProjectGUI extends CategoryGUI {

    /** version information for the represented project */
    private String m_versionString;
    
    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     * @param pos the position to insert into parent.
     * @param versionString version information for the given project.
     */
    public ReusedProjectGUI(String name, GuiNode parent, 
            IProjectPO content, int pos, String versionString) {
        
        super(name, parent, content, pos, false);
        m_versionString = versionString;
    }

    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     * @param versionString version information for the given project.
     */
    public ReusedProjectGUI(String name, GuiNode parent, 
            IProjectPO content, String versionString) {
        
        super(name, parent, content, false);
        m_versionString = versionString;
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        if (getContent() != null) {
            return super.getImage();
        }
        
        // If the project is missing
        return IconConstants.MISSING_PROJECT_IMAGE;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getName() {
        StringBuilder name = new StringBuilder(super.getName());
        name.append(m_versionString);
        
        return name.toString();
    }
}
