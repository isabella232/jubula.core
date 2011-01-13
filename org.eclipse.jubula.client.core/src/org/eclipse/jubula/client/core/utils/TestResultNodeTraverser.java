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

import java.util.Iterator;

import org.eclipse.jubula.client.core.model.TestResultNode;


/**
 * Traverses trees containing test result nodes.
 *
 * @author BREDEX GmbH
 * @created Jun 2, 2010
 */
public class TestResultNodeTraverser 
        extends AbstractTreeTraverser<TestResultNode> {

    /**
     * Constructor
     * 
     * @param rootNode
     *            The node where the traversion starts
     * @param operation
     *            The operation to call on any node
     */
    public TestResultNodeTraverser(TestResultNode rootNode,
            ITreeNodeOperation<TestResultNode> operation) {
        super(rootNode, operation);
    }

    /**
     * {@inheritDoc}
     */
    protected Iterator<TestResultNode> getChildIterator(TestResultNode node) {
        return node.getResultNodeList().iterator();
    }

}
