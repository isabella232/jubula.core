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
    /**
     * 
     * @author BREDEX GmbH
     *
     */
public class TestSuiteRootGUI extends CategoryGUI {
    /**
     * 
     */
    private String m_mylynId = "00000000000000000000000000000001";
    
    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     */
    public TestSuiteRootGUI(String name, GuiNode parent, INodePO content) {
        super(name, parent, content);
    }
    /**
     * Constructor.
     * @param name the name.
     * @param parent the parent
     * @param content the content.
     * @param isEditable whether or not this GuiNode is editable
     */
    public TestSuiteRootGUI(String name, GuiNode parent, INodePO content,
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
    public TestSuiteRootGUI(String name, GuiNode parent, INodePO content,
            int pos) {
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
    public TestSuiteRootGUI(String name, GuiNode parent, INodePO content, 
            int pos,
            boolean isEditable) {
            super(name, parent, content, pos, isEditable);
    }
    /**
     * {@inheritDoc}
     */
    public String getMylynId() {
        return m_mylynId;
        
    }
    /**
     * 
     * @param id the id
     */
    public void setMylynId(String id) {
        this.m_mylynId = id;
    }
}
