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
package org.eclipse.jubula.client.ui.rcp.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.utils.ToolkitUtils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.constants.ToolkitConstants;
import org.eclipse.jubula.tools.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.xml.businessmodell.ToolkitPluginDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**
 * This class creates specific Controls
 *
 * @author BREDEX GmbH
 * @created 05.06.2007
 */
public class ControlFactory {

    /**
     * Private utility constructor
     */
    private ControlFactory() {
        // nothing
    }
    
    /**
     * Creates a Combo with all independent toolkit entries.
     * @param parent the parent of the Combo
     * @return a Combo with all independent toolkit entries.
     */
    @SuppressWarnings("unchecked")
    public static DirectCombo<String> createToolkitCombo(Composite parent) {
        
        final List<ToolkitPluginDescriptor> descriptors = 
            ComponentBuilder.getInstance().getCompSystem()
                .getIndependentToolkitPluginDescriptors(false);
        List<String> values = new ArrayList<String>();
        List<String> displayValues = new ArrayList<String>();
        for (ToolkitPluginDescriptor desc : descriptors) {
            values.add(desc.getToolkitID());
            displayValues.add(desc.getName());
        }

        return new DirectCombo<String>(
            parent, SWT.READ_ONLY, values, displayValues, false, 
                true);
    }
    
    /**
     * Creates a Combo for AUT-Toolkit depending on the Project-Toolkit.
     * @param parent the parent of the Combo
     * @param project the depending project.
     * @param currentValue the value to select by default. If this value is not 
     *                     found within the toolkit plugins, it will be added to
     *                     the combo box anyway, but it will not be 
     *                     internationalized. If this parameter is 
     *                     <code>null</code>, it will be ignored.
     * @return a Combo with toolkits depending on the Project-Toolkit.
     * @throws ToolkitPluginException if the toolkit for the given project 
     *         cannot be found.
     */
    public static DirectCombo<String> createAutToolkitCombo(Composite parent, 
        IProjectPO project, String currentValue) throws ToolkitPluginException {
        
        final List<ToolkitPluginDescriptor> toolkits = getAutToolkits(project);

        List<String> values = new ArrayList<String>();
        List<String> displayValues = new ArrayList<String>();
        for (ToolkitPluginDescriptor desc : toolkits) {
            values.add(desc.getToolkitID());
            displayValues.add(desc.getName());
        }

        if (currentValue != null && currentValue.trim().length() != 0
            && !values.contains(currentValue)) {
            
            values.add(currentValue);
            displayValues.add(currentValue);
        }

        return new DirectCombo<String>(parent, SWT.READ_ONLY, 
            values, displayValues, false, true);
        
    }
    
    /**
     * Creates a Combo for Project-Toolkit depending on the current 
     * Project-Toolkit and underlying AUT-Toolkits.
     * @param parent the parent of the Combo
     * @return a Combo with toolkits depending on the Project-Toolkit.
     */
    public static DirectCombo<String> createProjectToolkitCombo(
        Composite parent) {

        final IProjectPO project = GeneralStorage.getInstance().getProject();
        final List<ToolkitPluginDescriptor> toolkits = 
            UsedToolkitBP.getInstance().getAllowedProjectToolkits(project);

        final List<String> values = new ArrayList<String>();
        final List<String> displayValues = new ArrayList<String>();
        for (ToolkitPluginDescriptor desc : toolkits) {
            values.add(desc.getToolkitID());
            displayValues.add(desc.getName());
        }

        final String currentValue = project.getToolkit();
        if (currentValue != null && currentValue.trim().length() != 0
            && !values.contains(currentValue)) {
            
            values.add(currentValue);
            final String tkName = ToolkitUtils.getToolkitName(currentValue);
            displayValues.add(tkName);
        }
        
        return new DirectCombo<String>(parent, SWT.NONE, values,
            displayValues, false, true);
    }

    /**
     * Creates a Combo for AUT-Toolkit depending on the current AUT-Toolkit.
     * This method should be called only as a backup, if the Project-Toolkit
     * is unavailable.
     * @param parent the parent of the Combo
     * @param aut the depending AUT.
     * @return a Combo with toolkits depending on the current AUT-Toolkit.
     */
    public static DirectCombo<String> createAutToolkitCombo(Composite parent, 
        IAUTMainPO aut) {

        final ToolkitPluginDescriptor toolkit = 
            ComponentBuilder.getInstance().getCompSystem()
                .getToolkitPluginDescriptor(aut.getToolkit());
        final List<String> values = new ArrayList<String>();
        final List<String> displayValues = new ArrayList<String>();
        if (toolkit != null) {
            values.add(toolkit.getToolkitID());
            displayValues.add(toolkit.getName());
            
        } else {
            String autToolkitId = aut.getToolkit();
            if (autToolkitId != null && autToolkitId.trim().length() != 0) {
                values.add(autToolkitId);
                displayValues.add(autToolkitId);
            }
        }        
        return new DirectCombo<String>(parent, SWT.READ_ONLY, 
            values, displayValues, false, true);
    }

    /**
     * Gets the List of Toolkits for a AUT depending of the Project-Toolkit.
     * @param project the depending project.
     * @return the List of Toolkits for a AUT depending of the Project-Toolkit.
     * @throws ToolkitPluginException if the toolkit for the given project 
     *         cannot be found.
     */
    @SuppressWarnings("unchecked")
    public static List<ToolkitPluginDescriptor> getAutToolkits(
        IProjectPO project) throws ToolkitPluginException {
        
        CompSystem compSys = ComponentBuilder.getInstance().getCompSystem();
        final String projToolkit = project.getToolkit();
        if (projToolkit == null) {
            return compSys.getIndependentToolkitPluginDescriptors(
                    ToolkitConstants.LEVEL_TOOLKIT);
        }
        final String level = ToolkitSupportBP.getToolkitLevel(projToolkit);
        if (ToolkitConstants.LEVEL_TOOLKIT.equals(level)) {
            final List<ToolkitPluginDescriptor> toolkitList = 
                new ArrayList<ToolkitPluginDescriptor>(1);
            toolkitList.add(compSys.getToolkitPluginDescriptor(projToolkit));
            
            for (Object descObj 
                    : compSys.getIndependentToolkitPluginDescriptors(
                            ToolkitConstants.LEVEL_TOOLKIT)) {
                
                ToolkitPluginDescriptor desc = (ToolkitPluginDescriptor)descObj;
                
                if (ToolkitUtils.doesToolkitInclude(
                        desc.getToolkitID(), projToolkit)) {
                    toolkitList.add(desc);
                }
            }
            
            return toolkitList;
        }

        return compSys.getIndependentToolkitPluginDescriptors(
            ToolkitConstants.LEVEL_TOOLKIT);
    }
    
    
}
