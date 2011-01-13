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
package org.eclipse.jubula.client.core.utils;

import org.eclipse.jubula.client.core.model.TestResultNode;

/**
 * @author BREDEX GmbH
 * @created Jun 2, 2010
 */
public class FindNextTestResultErrorOperation 
        implements ITreeNodeOperation<TestResultNode> {

    /** the node that was found */
    private TestResultNode m_foundNode = null;

    /** the node after which to start looking for error nodes */
    private TestResultNode m_startingNode;

    /** 
     * flag indicating whether the traversal has already passed the 
     * initial node 
     */
    private boolean m_hasPassedStartingNode = false;
    
    /**
     * Constructor
     * 
     * @param startingNode 
     *              The node after which to start looking for error nodes.
     */
    public FindNextTestResultErrorOperation(TestResultNode startingNode) {
        m_startingNode = startingNode;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean operate(ITreeTraverserContext<TestResultNode> ctx,
        TestResultNode parent, TestResultNode node, boolean alreadyVisited) {

        if (m_hasPassedStartingNode && node.getStatus() == TestResultNode.ERROR
                && node != ctx.getRootNode()
                && m_foundNode == null) {
            m_foundNode = node;
        }
        
        if (node == m_startingNode) {
            m_hasPassedStartingNode = true;
        }

        return m_foundNode != null;
    }

    /**
     * {@inheritDoc}
     */
    public void postOperate(ITreeTraverserContext<TestResultNode> ctx,
        TestResultNode parent, TestResultNode node, boolean alreadyVisited) {

        // Do nothing
    }

    /**
     * 
     * @return the node that was found, or <code>null</code> if no such node was
     *         found.
     */
    public TestResultNode getFoundNode() {
        return m_foundNode;
    }
}
