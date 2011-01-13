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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.CapGUIPropertySource;
import org.eclipse.jubula.client.ui.provider.labelprovider.GeneralGDLabelProvider;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * GUI representation of the CapPO class. This class is used to display the CAPs
 * in the trees. It consists the "real" CapPO.
 *
 * @author BREDEX GmbH
 * @created 14.10.2004
 */
public class CapGUI extends GuiNode implements IAdaptable {

    
    /** The CapGUIPropertySource for properties view */
    private CapGUIPropertySource m_cgps;
   
    
    /**
     * Constructor.
     * @param name The name.
     * @param parent The parent.
     * @param content The "real" model node.
     */
    public CapGUI(String name, SpecTestCaseGUI parent, INodePO content) {
        super(name, parent, content);
    }
    
    /**
     * Constructor.
     * @param name The name.
     * @param parent The parent.
     * @param content The "real" model node.
     * @param position the position to insert.
     */
    public CapGUI(String name, SpecTestCaseGUI parent, INodePO content, 
            int position) {
        
        super(name, parent, content, position);
    }
    
   
    /**
     * Constructor
     * 
     * @param name The name.
     * @param parent The parent.
     * @param content The "real" model node.
     * @param isEditable whether or not this GuiNode is editable
     */
    public CapGUI(String name, SpecTestCaseGUI parent, ICapPO content,
            boolean isEditable) {
        super(name, parent, content, isEditable);
    }

    /**
     * For implementation of IAdaptable.
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_cgps == null) {
                m_cgps = new CapGUIPropertySource(this);
            }
            return m_cgps;
        }
        return null;
    }
    
    /**
     * Overrides GuiNode.getChildren()
     * Note: A CAP does not have children!
     * @return an empty List.
     * {@inheritDoc}
     */
    public List<GuiNode> getChildren() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        if (getParentNode().getParentNode() instanceof EventExecTestCaseGUI) {
            return IconConstants.EH_CAP_IMAGE;
        }
        return IconConstants.CAP_IMAGE;
    }
    
    /**
     * {@inheritDoc}
     */
    public Image getCutImage() {
        return IconConstants.CAP_CUT_IMAGE;
    }
    
    /**
     * Overrides GuiNode.addNode()
     * Does nothing, a CAP does not have children!
     * @param child the node to be set as children.
     */
    public void addNode(GuiNode child) {
        // do nothing.
    }
    
    
    /**
     * Overrides GuiNode.removeNode()
     * Does nothing, a CAP does not have children!
     * @param child the node to be removed.
     */
    public void removeNode(GuiNode child) {
//      do nothing.
    }
    
    
    
    
    
    /**
     * {@inheritDoc}
     */
    public void getInfoString(StringBuilder info) {
        if (Plugin.getDefault().getPreferenceStore()
                .getBoolean(Constants.SHOWCAPINFO_KEY)) {
            info.append(GeneralGDLabelProvider.OPEN_BRACKED);
            final Map<String, String> map = StringHelper.getInstance().getMap();
            ICapPO cap = (ICapPO)getContent();
            IComponentNameMapper compMapper = 
                Plugin.getActiveCompMapper();
            info.append(I18n.getString("CapGUI.Type"));  //$NON-NLS-1$
            info.append(map.get(cap.getComponentType())).append(
                    GeneralGDLabelProvider.SEPARATOR);
            info.append(I18n.getString("CapGUI.Name"));  //$NON-NLS-1$
            String componentName = cap.getComponentName();
            if (compMapper != null) {
                componentName = 
                    compMapper.getCompNameCache().getName(componentName);
            } else {
                componentName = 
                    ComponentNamesBP.getInstance().getName(componentName);
            }
            if (componentName != null) {
                info.append(componentName);
            }
            info.append(GeneralGDLabelProvider.SEPARATOR);
            info.append(I18n.getString("CapGUI.Action"));  //$NON-NLS-1$
            info.append(map.get(cap.getActionName()));
            info.append(GeneralGDLabelProvider.CLOSE_BRACKED);
        }        
    }
}
