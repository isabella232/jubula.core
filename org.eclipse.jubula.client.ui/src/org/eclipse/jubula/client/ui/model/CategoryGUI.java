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

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.swt.graphics.Image;


/**
 * GuiNode for a category.
 *
 * @author BREDEX GmbH
 * @created 06.06.2005
 */
public class CategoryGUI extends GuiNode {

    
    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     */
    public CategoryGUI(String name, GuiNode parent, INodePO content) {
        super(name, parent, content);
    }
    
    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     * @param isEditable whether or not this GuiNode is editable
     */
    public CategoryGUI(String name, GuiNode parent, INodePO content,
        boolean isEditable) {
        
        super(name, parent, content, isEditable);
    }

    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     * @param pos the position to insert into parent.
     */
    public CategoryGUI(String name, GuiNode parent, INodePO content, int pos) {
        super(name, parent, content, pos);
    }
    
    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     * @param pos the position to insert into parent.
     * @param isEditable whether or not this GuiNode is editable
     */
    public CategoryGUI(String name, GuiNode parent, INodePO content, int pos,
        boolean isEditable) {
        super(name, parent, content, pos, isEditable);
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.CATEGORY_IMAGE;
    }

    /**
     * {@inheritDoc}
     */
    public Image getCutImage() {
        return IconConstants.CATEGORY_CUT_IMAGE;
    }
    
    /**
     * {@inheritDoc}
     */
    public Image getGeneratedImage() {
        return IconConstants.CATEGORY_GENERATED_IMAGE;
    }
}