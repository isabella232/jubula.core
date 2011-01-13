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

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.SpecTestCaseGUIPropertySource;
import org.eclipse.jubula.client.ui.provider.labelprovider.GeneralGDLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author BREDEX GmbH
 * @created 14.10.2004
 */
public class SpecTestCaseGUI extends GuiNode implements IAdaptable {

    /** PropertySource for Properties View */ 
    private SpecTestCaseGUIPropertySource m_stgps;
  
    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     */
    public SpecTestCaseGUI(String name, GuiNode parent, INodePO content) {
        super(name, parent, content);
    }
    
    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     * @param pos the position to be inserted in parent.
     */
    public SpecTestCaseGUI(String name, GuiNode parent, INodePO content, 
        Integer pos) {
        
        super(name, parent, content, pos);
    }

    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     * @param pos the position to be inserted in parent.
     * @param isEditable whether or not this GuiNode is editable
     */
    public SpecTestCaseGUI(String name, GuiNode parent, INodePO content, 
        Integer pos, boolean isEditable) {
        
        super(name, parent, content, pos, isEditable);
    }

    /**
     * For implementation of IAdaptable.
     * {@inheritDoc}
     * 
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_stgps == null) {
                m_stgps = new SpecTestCaseGUIPropertySource(this);
            }
            return m_stgps;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.TC_IMAGE;
    }

    /**
     * {@inheritDoc}
     */
    public Image getCutImage() {
        return IconConstants.TC_CUT_IMAGE;
    }
    
    /**
     * {@inheritDoc}
     */
    public Image getGeneratedImage() {
        return IconConstants.TC_GENERATED_IMAGE;
    }
    
    /**
     * {@inheritDoc}
     */
    public void getInfoString(StringBuilder info) {
        ISpecTestCasePO specTc = (ISpecTestCasePO)getContent();
        final Iterator<IParamDescriptionPO> iter = specTc.getParameterList()
            .iterator();
        boolean parameterExist = false;
        if (iter.hasNext()) {
            parameterExist = true;
            info.append(GeneralGDLabelProvider.OPEN_BRACKED);
        }
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                IParamDescriptionPO descr = iter.next();
                info.append(descr.getName());
                if (iter.hasNext()) {
                    info.append(GeneralGDLabelProvider.SEPARATOR);
                }
            }
        }
        if (parameterExist) {
            info.append(GeneralGDLabelProvider.CLOSE_BRACKED);
        }
    }
}