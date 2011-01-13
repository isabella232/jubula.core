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
package org.eclipse.jubula.client.ui.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;


/**
 * This class iterates in trees based on <code>GuiNodes</code>.
 *
 * @author BREDEX GmbH
 * @created 08.01.2005
 */
public class TreeIterator {

    /** Linear List of tree elements */
    private List<GuiNode> m_elements = new LinkedList<GuiNode>();
    /** Iterator to iterate over the element list */
    private Iterator<GuiNode> m_iter;
 
    /**
     * Constructor.
     * @param root the root of the tree.
     */
    public TreeIterator(GuiNode root) {
        m_elements.add(root);
        setElements(root);
        m_iter = m_elements.iterator();
    }
    
    /**
     * Constructor.
     * @param root the root of the tree.
     * @param filter An array of Class. 
     * Only objects of instance of the given classes will be iterated.
     */
    public TreeIterator(GuiNode root, Class[] filter) {
        this(root);
        filterElements(filter);
    }
    
    /**
     * @param filter the classes of the elements which are to be iterated.
     */
    private void filterElements(Class[] filter) {
        List<GuiNode> filteredElements = new ArrayList<GuiNode>(25);
        int filterLength = filter.length;
        for (int i = 0; i < filterLength; i++) {
            Class filteredClass = filter[i];
            for (GuiNode element : m_elements) {
                if (filteredClass.isInstance(element)) {
                    filteredElements.add(element);
                }
            }
        }
        m_elements = filteredElements;
        m_iter = m_elements.iterator();
    }

    /**
     * @return The next element in the iteration.
     */
    public GuiNode next() {
        return m_iter.next();
    }
    
    /**
     * Returns <tt>true</tt> if the iteration has more elements.
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return m_iter.hasNext();
    }
    
    /**
     * Creates the linear list of tree elements inclusive the root.
     * @param root a <code>GuiNode</code> value. The root of the tree.
     */
    protected void setElements(GuiNode root) {
        if (root == null) {
            return;
        }
        final List<GuiNode> nodes = root.getChildren();
        for (GuiNode nextNode : nodes) {
            if (nextNode != null) {
                m_elements.add(nextNode);
                if (nextNode instanceof ExecTestCaseGUI) {
                    final SpecTestCaseGUI specTestCase = ((ExecTestCaseGUI)
                            nextNode).getSpecTestCase();
                    if (specTestCase != null) {
                        m_elements.add(specTestCase);
                    }
                }
            }
            setElements(nextNode); 
        }
    }

    /**
     * @param node the nodePO
     * @return the equivalent GuiNode(s) of the given nodePO
     */
    public List<GuiNode> getGuiNodeOfNodePO(INodePO node) {
        List<GuiNode> guiNodeList = new ArrayList<GuiNode>(0);
        if (node != null) {
            for (GuiNode guiNode : m_elements) {
                if ((guiNode != null) && (node.equals(guiNode.getContent()))) {
                    guiNodeList.add(guiNode);
                }
            }
        }
        return guiNodeList;
    }

    /**
     * @return Returns the elements.
     */
    protected List<GuiNode> getElements() {
        return m_elements;
    }
}