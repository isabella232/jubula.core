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
package org.eclipse.jubula.client.ui.businessprocess;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.ui.controllers.TreeIterator;


/**
 * BP utility class for GUINodes.
 *
 * @author BREDEX GmbH
 * @created 03.03.2006
 */
public class GuiNodeBP {

    /**
     * Default utility constructor.
     */
    private GuiNodeBP() {
        // do nothing
    }
    
    /**
     * Gets the TestSuite of the given node
     * @param node the node
     * @return the TestSuite of the given node or null if no TestSuite found
     */
    public static ITestSuitePO getTestSuiteOfNode(INodePO node) {
        INodePO parent = node;
        while (!(parent instanceof ITestSuitePO) && parent != null) {
            parent = parent.getParentNode();
        }
        return (ITestSuitePO)parent;
    }

    /**
     * sets the selection in the given TreeViewer to the given node and also
     * gives the given tree viewer focus
     * 
     * @param node
     *            the node to select
     * @param tv
     *            the TreeViewer
     */
    public static void setSelectionAndFocusToNode(INodePO node, TreeViewer tv) {
        TreeIterator it = new TreeIterator((INodePO)tv.getInput());
        List<INodePO> guiNodeList = it.getGuiNodeOfNodePO(node);
        tv.getTree().setFocus();
        tv.setSelection(new StructuredSelection(guiNodeList), true);
    }
    

    /**
     * @param structuredSel
     *            find the referenced specification test case for the given
     *            structured selection
     * @return a valid ISpecTestCasePO or <code>null</code> if no reference
     *         could be found
     */
    public static ISpecTestCasePO getSpecTC(
        IStructuredSelection structuredSel) {
        IExecTestCasePO execTc = null;
        if (structuredSel.getFirstElement() instanceof IExecTestCasePO) {
            execTc = (IExecTestCasePO)structuredSel.getFirstElement();
        } else if (structuredSel.getFirstElement() instanceof TestResultNode) {
            TestResultNode trNode = (TestResultNode)structuredSel
                    .getFirstElement();
            INodePO nodePO = trNode.getNode();
            while (!(Hibernator.isPoSubclass(nodePO, IExecTestCasePO.class))) {
                trNode = trNode.getParent();
                if (trNode == null) {
                    return null;
                }
                nodePO = trNode.getNode();
            }
            execTc = (IExecTestCasePO)nodePO;
        }
        if (execTc != null) {
            return execTc.getSpecTestCase();
        }
        return null;
    }
    
}