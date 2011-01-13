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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created 14.10.2004
 */
public class TestCaseBrowserRootGUI extends GuiNode {
    
    /** the counter of the maximum of testCases */
    private int m_maxTestCase = 0;
 
   
    /**
     * construct TestSuiteGUI
     * @param name String
     */
    public TestCaseBrowserRootGUI(String name) {
        super(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public INodePO getContent() {
        if (super.getContent() == null) {
            setContent(GeneralStorage.getInstance().getProject());
        }
        return super.getContent();
    }

 

    /**
     * {@inheritDoc}
     * @return super.getInternalName()
     */
    public String getName() {
        return getInternalName();
    }



    /**
     * {@inheritDoc}
     * @param name
     */
    public void setName(String name) {
        setInternalName(name);
    }



    /**
     * returns the list of TS GUIs.
     * @return the List.
     */
    public List getTestSuiteList() {
        return Collections.unmodifiableList(getChildren().subList(0, 1));
    }
    
    /**
     * Removes all TestSuites.
     */
    public void removeAllTestSuites() {
        removeAllNodes();
    }

    
    /**
     * @return Returns the maximum of the testCases.
     */
    public int getMaxTestCase() {
        return m_maxTestCase;
    }

    /**
     * @param maxTestCase the
     *            maximum of testCases to set.
     */
    public void setMaxTestCase(int maxTestCase) {
        m_maxTestCase = maxTestCase;
    }
    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.ROOT_IMAGE;
    }

    
    /**
     * @param specTcPO the SpecTestCasePO to search for.
     * @param actualNode
     *      acutal node in tree(null = this)
     * @return the depending SpecTestCaseGUI or null.
     */
    public SpecTestCaseGUI getSpecTestCaseGuiByPO(GuiNode actualNode, 
            ISpecTestCasePO specTcPO) {
        GuiNode node = actualNode;
        Iterator iter;
        if (node == null) {
            iter = getChildren().iterator();
        } else {
            iter = node.getChildren().iterator();
        }

        SpecTestCaseGUI returnVal = null;
        // FIXME Andreas : should check if method works well
        while (iter.hasNext()) {
            GuiNode iterNode = (GuiNode) iter.next();
            if (iterNode instanceof SpecTestCaseGUI) {
                SpecTestCaseGUI specTcGUI = (SpecTestCaseGUI) iterNode;
                if (specTcGUI.getContent() == specTcPO) {
                    return specTcGUI;
                }
            } else if (iterNode instanceof CategoryGUI
                && iterNode.getChildren().size() > 0) {

                returnVal = getSpecTestCaseGuiByPO(iterNode, specTcPO);
                if (returnVal != null) {
                    return returnVal;
                }
            }
        }
        return returnVal;
    } 
}