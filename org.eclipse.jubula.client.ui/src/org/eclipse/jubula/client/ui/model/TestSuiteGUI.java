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
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.TestSuiteGUIPropertySource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class TestSuiteGUI extends GuiNode implements IAdaptable {
   
    
    /** The TestSuiteGUIPropertySource for properties view */
    private TestSuiteGUIPropertySource m_exTsPropSource;    
    
    /**
     * construct TestSuiteGUI
     * @param name name of ExecTestSuite
     */
    public TestSuiteGUI(String name) {
        super(name);
    }
    
    /**
     * Constructs TestSuiteGUI and <b>sets it as the "REAL" TestSuite automatically!</b>
     * @param name name of ExecTestSuite
     * @param testSuite associated testsuite in model
     */
    public TestSuiteGUI(String name, ITestSuitePO testSuite) {
        super(name, 
            Plugin.getDefault().getTestSuiteBrowserRootGUI().getChildren()
                .get(0), testSuite); 
    }
    
    /**
     * Constructs TestSuiteGUI and <b>sets it as the "REAL" TestSuite automatically!</b>
     * @param name name of ExecTestSuite
     * @param testSuite associated testsuite in model
     * @param isEditable whether or not this GuiNode is editable
     */
    public TestSuiteGUI(String name, ITestSuitePO testSuite, 
            boolean isEditable) {
        
        super(name, 
            Plugin.getDefault().getTestSuiteBrowserRootGUI().getChildren()
                .get(0), testSuite, isEditable); 
    }

    /**
     * Constructs TestSuite and sets it into the given parent.
     * @param name the name
     * @param parent the parent
     * @param testSuite the content
     */
    public TestSuiteGUI(String name, GuiNode parent, 
        ITestSuitePO testSuite) {
        
        super(name, parent, testSuite);
    }
    
    /**
     * Constructs TestSuite and sets it into the given parent.
     * @param name the name
     * @param parent the parent
     * @param testSuite the content
     * @param isEditable whether or not this GuiNode is editable
     */
    public TestSuiteGUI(String name, GuiNode parent, 
        ITestSuitePO testSuite, boolean isEditable) {
        
        super(name, parent, testSuite, isEditable);
    }

    /**
     * Removes all TestSuites.
     */
    public void removeAllTestSuites() {
        if (getParentNode() == null) {
            removeAllNodes();
        }
    }

    /**
     * Removes all ExecTestCases of the TestSuite
     */
    public void removeAllExecTestCases() {
        removeAllNodes();
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        if (getContent() != null) {
            ((ITestSuitePO)getContent()).setEditable(true);
        }
        return IconConstants.TS_IMAGE;
    }
    
    /**
     * returns the disabled image
     * {@inheritDoc}
     * @return the disabled image
     */
    public Image getDisabledImage() {
        if (getContent() != null) {
            ((ITestSuitePO)getContent()).setEditable(false);
        }
        return IconConstants.TS_DISABLED_IMAGE;
    }
    
    /**
     * @param node the node whose TestSuite is to find.
     * @return the TestSuiteGUI for the given GuiNode 
     * or null if no TestSuite was found.
     */
    public static TestSuiteGUI getTestSuiteForNode(GuiNode node) {
        GuiNode tmpNode = node;
        while (tmpNode != null && !(tmpNode instanceof TestSuiteGUI)) {
            tmpNode = tmpNode.getParentNode();
        }
        return (TestSuiteGUI)tmpNode;
    }

    /**
     * {@inheritDoc}
     * @param adapter
     * @return
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (m_exTsPropSource == null) {
                m_exTsPropSource = new TestSuiteGUIPropertySource(this);
            }
            return m_exTsPropSource;
        }
        return null;
    }
}