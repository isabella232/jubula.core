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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.ExecTestCaseGUIPropertySource;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class ExecTestCaseGUI extends GuiNode implements IAdaptable {
    
    /** the counter of the maximum of CAPs */
    private int m_maxCAP = 0;

    /** Reference to the original SpecTestCase in the specification */
    private SpecTestCaseGUI m_originalSpecTestCase;
    
    /** Reference to the SpecTestCaseGUI */
    private SpecTestCaseGUI m_specTestCase;


    /** List of childrens */
    private List < ExecTestCaseGUI > m_childrenNodes = 
        new ArrayList < ExecTestCaseGUI > (0);
    
    /** The ExecTestCaseGUIPropertySource for properties view */
    private ExecTestCaseGUIPropertySource m_exTcPropSource;
    
    
    /**
     * Constructor.
     * @param name The name of the TestCase.
     */
    public ExecTestCaseGUI(String name) {
        super(name);
    }
    
    /**
     * Constructor.
     * @param name The name of the TestCase.
     * @param parent The parent.
     */
    public ExecTestCaseGUI(String name, GuiNode parent) {
        super(name, parent, null);
    }

    /**
     * Constructor.
     * @param name The name of the TestCase.
     * @param parent The parent.
     * @param testCase The SpecTestCasePO.
     */
    public ExecTestCaseGUI(String name, GuiNode parent, 
        SpecTestCaseGUI testCase) {
        
        super(name, parent, null);
        m_specTestCase = testCase;
    }
    
    
    /**
     * Constructor.
     * @param name The name of the TestCase.
     * @param parent The parent.
     * @param testCase The SpecTestCasePO.
     * @param content The "real" model object.
     */
    public ExecTestCaseGUI(String name, GuiNode parent, 
        SpecTestCaseGUI testCase, INodePO content) {
        
        this(name, parent, testCase, content, parent.getChildren().size());
    }
    
    /**
     * Constructor.
     * @param name The name of the TestCase.
     * @param parent The parent.
     * @param testCase The SpecTestCasePO.
     * @param content The "real" model object.
     * @param isEditable whether or not this GuiNode is editable
     */
    public ExecTestCaseGUI(String name, GuiNode parent, 
        SpecTestCaseGUI testCase, INodePO content, boolean isEditable) {
        
        this(name, parent, testCase, content, 
                parent.getChildren().size(), isEditable);
    }

    /**
     * Constructor.
     * @param name The name of the TestCase.
     * @param parent The parent.
     * @param testCase The SpecTestCasePO.
     * @param content The "real" model object.
     * @param pos position to insert the child in nodelist of parent
     */
    public ExecTestCaseGUI(String name, GuiNode parent, 
        SpecTestCaseGUI testCase, INodePO content, int pos) {
        
        super(name, parent, content, pos);
        m_specTestCase = testCase;
    }

    /**
     * Constructor.
     * @param name The name of the TestCase.
     * @param parent The parent.
     * @param testCase The SpecTestCasePO.
     * @param content The "real" model object.
     * @param pos position to insert the child in nodelist of parent
     * @param isEditable whether or not this GuiNode is editable
     */
    public ExecTestCaseGUI(String name, GuiNode parent, 
            SpecTestCaseGUI testCase, INodePO content, 
            int pos, boolean isEditable) {
        
        super(name, parent, content, pos, isEditable);
        m_specTestCase = testCase;
    }

    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     * @param pos the position to be inserted in parent.
     * @param isEditable whether or not this GuiNode is editable
     */
    public ExecTestCaseGUI(String name, GuiNode parent, INodePO content, 
        Integer pos, boolean isEditable) {
        
        super(name, parent, content, pos, isEditable);
    }

    /**
     * addTestCase
     * @param testCase TestCaseGUI
     */
    public void addTestCase(ExecTestCaseGUI testCase) {
        m_childrenNodes.add(testCase);
    }
 



    /**
     * For implementation of IAdaptable.
     * {@inheritDoc}
     * 
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_exTcPropSource == null) {
                m_exTcPropSource = new ExecTestCaseGUIPropertySource(this);
            }
            return m_exTcPropSource;
        }
        return null;
    }

    /**
     * Overrides GuiNode.getChildren().
     * Returns a <code>List</code> of children of a <code>GuiNode</code>.
     * @return the children.
     * {@inheritDoc}
     */
    public List<GuiNode> getChildren() {
        if (m_specTestCase != null) {
            return m_specTestCase.getChildren();
        }
        return Collections.emptyList();
    }
    


    /**
     * @return Returns the m_specTestCase.
     */
    public SpecTestCaseGUI getSpecTestCase() {
        return m_specTestCase;
    }
    
    /**
     * @return Returns the maximum of the CAPs.
     */
    public int getMaxCAP() {
        return m_maxCAP;
    }

    /**
     * @param maxcap the
     *            maximum of CAPs to set.
     */
    public void setMaxCAP(int maxcap) {
        m_maxCAP = maxcap;
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.TC_REF_IMAGE;
    }

    /**
     * {@inheritDoc}
     */
    public Image getCutImage() {
        return IconConstants.TC_REF_CUT_IMAGE;
    }
    
    /**
     * {@inheritDoc}
     */
    public Image getGeneratedImage() {
        return IconConstants.TC_REF_GENERATED_IMAGE;
    }

    /**
     * Only to use for initializing if the dependend SpecTestCase cannot 
     * be set via constructor!
     * @param specTestCase The specTestCase to set.
     */
    public void setSpecTestCase(SpecTestCaseGUI specTestCase) {
        Assert.verify(m_specTestCase == null, 
            Messages.DoNotChangeTheSpecTestCase);
        m_specTestCase = specTestCase;
    }
    
    
    /**
     * Gets the original SpectestCase in the Specification.
     * @return Returns the originalSpecTestCase.
     */
    public SpecTestCaseGUI getOriginalSpecTestCase() {
        return m_originalSpecTestCase;
    }
    
    /**
     * Sets the original SpectestCase in the Specification.
     * @param originalSpecTestCase The originalSpecTestCase to set.
     */
    public void setOriginalSpecTestCase(SpecTestCaseGUI originalSpecTestCase) {
        m_originalSpecTestCase = originalSpecTestCase;
    }
    
    /**
     * {@inheritDoc}
     */
    public void getInfoString(StringBuilder info) {
        IExecTestCasePO execTc = (IExecTestCasePO)getContent();
        Iterator iter = execTc.getParameterList().iterator();
        boolean parameterExist = false;
        if (iter.hasNext()) {
            parameterExist = true;
            info.append(GeneralLabelProvider.OPEN_BRACKED);
        }
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                IParamDescriptionPO descr = (IParamDescriptionPO)iter.next();
                info.append(descr.getName());
                if (iter.hasNext()) {
                    info.append(GeneralLabelProvider.SEPARATOR);
                }
            }
        }
        if (parameterExist) {
            info.append(GeneralLabelProvider.CLOSE_BRACKED);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        IExecTestCasePO exec = (IExecTestCasePO)getContent();
        if (exec.getSpecTestCase() == null) {
            return super.getName();
        }
        if (exec.getRealName() != null && exec.getSpecTestCase() != null
                && !StringUtils.isBlank(exec.getRealName())) {
            String name = exec.getRealName();
            if (Plugin.getDefault().getPreferenceStore().getBoolean(
                    Constants.SHOWORIGINALNAME_KEY)) {
                name += StringConstants.SPACE + StringConstants.LEFT_PARENTHESES
                        + exec.getSpecTestCase().getName() 
                        + StringConstants.RIGHT_PARENTHESES;
            }
            return name;
        }
        return StringConstants.LEFT_INEQUALITY_SING 
            + exec.getSpecTestCase().getName() 
            + StringConstants.RIGHT_INEQUALITY_SING;
    }
}