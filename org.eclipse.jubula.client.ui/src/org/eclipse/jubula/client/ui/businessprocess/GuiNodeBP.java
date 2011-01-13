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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.controllers.SpecRefreshTreeIterator;
import org.eclipse.jubula.client.ui.controllers.TreeIterator;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.client.ui.views.TreeBuilder;


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
     * @param root root object of tree, where node is to add
     * @param po associated po object for GUINode to add
     * @param pos location of insertion
     * @param monitor The progress monitor for this potentially long-running 
     *                operation.
     * @return GuiNode the newest created GuiNode.
     */
    public static GuiNode addGUINode(GuiNode root, INodePO po, Integer pos, 
        IProgressMonitor monitor) {
        INodePO parentPO = po.getParentNode();
        TreeIterator ti = new TreeIterator(root);
        List <GuiNode> guiNodeList = ti.getGuiNodeOfNodePO(parentPO);
        GuiNode newestNode = null;
        for (GuiNode parentGui : guiNodeList) {
            newestNode =  TreeBuilder
                .buildSubTree(po, parentGui, true, pos, monitor);
        }
        return newestNode;
    }
    
    /**
     * @param root root object of tree, where node is to add
     * @param po associated po object for GUINode to add
     * @param pos location of insertion
     * @param monitor The progress monitor for this potentially long-running 
     *                operation.
     * @return GuiNode the newest created GuiNode.
     */
    public static GuiNode addGUINodeInTestSuiteBrowser
    (GuiNode root, INodePO po, Integer pos, IProgressMonitor monitor) {
        return TreeBuilder.buildSubTree(po, root, true, pos, monitor);
    }
    
    /**
     * Deletes all occurrences of the given InodePO in the given tree.
     * @param root the root of the tree
     * @param po the po which GUI representation to delete
     * @return true if one ore more nodes were deleted, 
     * false if no node was deleted.
     */
    public static boolean deleteGuiNode(GuiNode root, INodePO po) {
        boolean deleted = false;
        TreeIterator ti = new TreeIterator(root);
        for (GuiNode nodeGUI : ti.getGuiNodeOfNodePO(po)) {
            nodeGUI.getParentNode().removeNode(nodeGUI);
            deleted = true;
        }
        return deleted;
    }
    
    /**
     * Gets the TestSuite of the given node
     * @param node the node
     * @return the TestSuite of the given node or null if no TestSuite found
     */
    public static TestSuiteGUI getTestSuiteOfNode(GuiNode node) {
        GuiNode parent = node;
        while (!(parent instanceof TestSuiteGUI) && parent != null) {
            parent = parent.getParentNode();
        }
        return (TestSuiteGUI)parent;
    }

    /**
     * @param root root object of tree, where node is to rebuild
     * @param po associated po object for GUINode to rebuild
     */
    public static void rebuildEditorGuiNode(GuiNode root, INodePO po) {
        TreeIterator ti = new SpecRefreshTreeIterator(root);
        List <GuiNode> guiNodeList = ti.getGuiNodeOfNodePO(po);
        for (GuiNode guiNode : guiNodeList) {
            GuiNode parentGuiNode = guiNode.getParentNode();
            parentGuiNode.removeNode(guiNode);   
            
            // rebuilding for TestSuite editor
            if (po instanceof ITestSuitePO) {
                TreeBuilder.buildTestSuiteEditorTree((ITestSuitePO)po, 
                    parentGuiNode);
            } else if (po instanceof ITestJobPO) {
                TreeBuilder.buildTestJobEditorTree((ITestJobPO)po, 
                        parentGuiNode);
            } else if (po instanceof ISpecTestCasePO) {
                TreeBuilder.buildTestCaseEditorTopTree((ISpecTestCasePO)po, 
                    parentGuiNode);
            }
        }
        // update location of reuses of SpecTestCases
        if (Hibernator.isPoSubclass(po, ISpecTestCasePO.class)) {
            ISpecTestCasePO specTc = (ISpecTestCasePO)po;
            List <IExecTestCasePO> execTestCases = 
                NodePM.getInternalExecTestCases(
                    specTc.getGuid(), 
                    specTc.getParentProjectId());
            if (execTestCases.size() > 0) {
                for (IExecTestCasePO execTc : execTestCases) {
                    List <GuiNode> guiNodes = 
                        ti.getGuiNodeOfNodePO(execTc);
                    for (GuiNode guiNode : guiNodes) {
                        GuiNode parentGuiNode = guiNode.getParentNode();
                        Integer pos = parentGuiNode.indexOf(guiNode);
                        parentGuiNode.removeNode(guiNode);            
                        TreeBuilder.buildSubTree(
                            execTc, parentGuiNode, true, pos, 
                            new NullProgressMonitor());
                    }                    
                }
            }
        }
    }
    
    /**
     * @param root root object of tree, where node is to rebuild
     * @param po associated po object for GUINode to rebuild
     * @return List
     *      new or modified top level GuiNodes
     */
    public static Set<GuiNode> rebuildBrowserGuiNode(GuiNode root, 
        INodePO po) {
        TreeIterator ti = new SpecRefreshTreeIterator(root);
        List <GuiNode> guiNodeList = ti.getGuiNodeOfNodePO(po);
        Set <GuiNode> newGuiNodes = new HashSet<GuiNode>();
        for (GuiNode guiNode : guiNodeList) {
            GuiNode parentGuiNode = guiNode.getParentNode();
            if (parentGuiNode == null) {
                parentGuiNode = root;
            }
            parentGuiNode.removeNode(guiNode);   
            GuiNode newNode = TreeBuilder
                .buildSubTree(po, parentGuiNode, true, null, 
                new NullProgressMonitor());
            if (newNode.getParentNode() != null) {
                newGuiNodes.add(newNode.getParentNode());
            } else {
                newGuiNodes.add(newNode);
            }
        }
        // update location of reuses of SpecTestCases
        if (Hibernator.isPoSubclass(po, ISpecTestCasePO.class)) {
            ISpecTestCasePO specTc = (ISpecTestCasePO)po;
            List <IExecTestCasePO> execTestCases = 
                NodePM.getInternalExecTestCases(
                    specTc.getGuid(), 
                    specTc.getParentProjectId());
            if (execTestCases.size() > 0) {
                for (IExecTestCasePO execTc : execTestCases) {
                    List <GuiNode> guiNodes = 
                        ti.getGuiNodeOfNodePO(execTc);
                    for (GuiNode guiNode : guiNodes) {
                        GuiNode parentGuiNode = guiNode.getParentNode();
                        Integer pos = parentGuiNode.indexOf(guiNode);
                        parentGuiNode.removeNode(guiNode);            
                        GuiNode newNode = TreeBuilder.buildSubTree(
                            execTc, parentGuiNode, true, pos, 
                            new NullProgressMonitor());
                        if (newNode.getParentNode() != null) {
                            newGuiNodes.add(newNode.getParentNode());
                        } else {
                            newGuiNodes.add(newNode);
                        }
                    }                    
                }
            }
        }
        return newGuiNodes;
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
        TreeIterator it = new TreeIterator((GuiNode)tv.getInput());
        List<GuiNode> guiNodeList = it.getGuiNodeOfNodePO(node);
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
        if (structuredSel.getFirstElement() instanceof ExecTestCaseGUI) {
            ExecTestCaseGUI node = (ExecTestCaseGUI)structuredSel
                    .getFirstElement();
            execTc = (IExecTestCasePO)node.getContent();

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
    
    /**
     * recursivly find the guinode for the given {@link INodePO}
     * 
     * @param node
     *            the node you are looking for
     * @param current
     *            the current GuiNode
     * @return GuiNode
     */
    public static GuiNode recursivlyfindNode(INodePO node, GuiNode current) {
        if (current == null) {
            // GuiNode not initialized, probably the TC browser is hidden
            // or a reused Project is missing
            return null;
        }
        if (ObjectUtils.equals(current.getContent(), node)) {
            return current;
        }
        for (GuiNode nextNode : current.getChildren()) {
            GuiNode result = recursivlyfindNode(node, nextNode);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    /**
     * @param node - id
     * @return GuiNode
     */
    public static GuiNode getGuiNodeForNodePO(INodePO node) {
        GuiNode rootGUI = Plugin.getDefault().getTestCaseBrowserRootGUI();
        GuiNode returnNode = GuiNodeBP.recursivlyfindNode(node, rootGUI);
        if (returnNode != null) {
            return returnNode;
        }
        rootGUI = Plugin.getDefault().getTestSuiteBrowserRootGUI();
        returnNode = GuiNodeBP.recursivlyfindNode(node, rootGUI);
        if (returnNode != null) {
            return returnNode;
        }
        return null;
    }
}