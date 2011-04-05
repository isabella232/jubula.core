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

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;


/**
 * This class iterates in trees based on <code>INodePO</code>s.
 *
 * @author BREDEX GmbH
 * @created 08.01.2005
 */
public class TreeIterator {

    /** Linear List of tree elements */
    private List<INodePO> m_elements = new LinkedList<INodePO>();
    /** Iterator to iterate over the element list */
    private Iterator<INodePO> m_iter;
 
    /**
     * Constructor.
     * @param root the root of the tree.
     */
    public TreeIterator(INodePO root) {
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
    public TreeIterator(INodePO root, Class[] filter) {
        this(root);
        filterElements(filter);
    }
    
    /**
     * @param filter the classes of the elements which are to be iterated.
     */
    private void filterElements(Class[] filter) {
        List<INodePO> filteredElements = new ArrayList<INodePO>(25);
        int filterLength = filter.length;
        for (int i = 0; i < filterLength; i++) {
            Class filteredClass = filter[i];
            for (INodePO element : m_elements) {
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
    public INodePO next() {
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
    protected void setElements(INodePO root) {
        if (root == null) {
            return;
        }
        final List<INodePO> nodes = root.getUnmodifiableNodeList();
        for (INodePO nextNode : nodes) {
            if (nextNode != null) {
                m_elements.add(nextNode);
                if (nextNode instanceof IExecTestCasePO) {
                    final ISpecTestCasePO specTestCase = ((IExecTestCasePO)
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
     * @return Returns the elements.
     */
    protected List<INodePO> getElements() {
        return m_elements;
    }
}