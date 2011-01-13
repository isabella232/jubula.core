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

import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.Plugin;

    /**
     * 
     * @author BREDEX GmbH
     *
     */
public class TestSuiteBrowserRootGUI extends TestSuiteGUI {
    /**
     * 
     */
    private String m_mylynId = "00000000000000000000000000000003";
    /**
     * construct TestSuiteGUI
     * @param name name of ExecTestSuite
     */
    public TestSuiteBrowserRootGUI(String name) {
        super(name);
    }
    
    /**
     * Constructs TestSuiteGUI and <b>sets it as the "REAL" TestSuite automatically!</b>
     * @param name name of ExecTestSuite
     * @param testSuite associated testsuite in model
     */
    public TestSuiteBrowserRootGUI(String name, ITestSuitePO testSuite) {
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
    public TestSuiteBrowserRootGUI(String name, ITestSuitePO testSuite, 
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
    public TestSuiteBrowserRootGUI(String name, GuiNode parent, 
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
    public TestSuiteBrowserRootGUI(String name, GuiNode parent, 
        ITestSuitePO testSuite, boolean isEditable) {
        
        super(name, parent, testSuite, isEditable);
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
