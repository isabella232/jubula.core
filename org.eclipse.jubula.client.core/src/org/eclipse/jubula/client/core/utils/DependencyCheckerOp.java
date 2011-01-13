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

import org.eclipse.jubula.client.core.model.INodePO;

/**
 * checks for nodes if they have dependencies with a certain parent
 * 
 *
 * @author BREDEX GmbH
 * @created 26.09.2005
 */
public class DependencyCheckerOp implements ITreeNodeOperation<INodePO> {

    /**
     * dependency finder implementation
     */
    private DependencyFinderOp m_dependencyFinder;

    /**
     * contructor
     * @param node
     *      INodePO
     */
    public DependencyCheckerOp(INodePO node) {
        m_dependencyFinder = new DependencyFinderOp(node);
    }

    /**
     * {@inheritDoc}
     *      org.eclipse.jubula.client.core.model.NodePO,
     *      org.eclipse.jubula.client.core.model.NodePO)
     * @param ctx
     *            ITreeTraverserContext
     * @param parent
     *            INodePO
     * @param node
     *            INodePO
     */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent, 
        INodePO node, boolean alreadyVisited) {

        if (hasDependency()) {
            ctx.setContinued(false);
            return true;
        }

        m_dependencyFinder.operate(ctx, parent, node, alreadyVisited);
        
        return true;
    }

    /**
     * {@inheritDoc}
     *      org.eclipse.jubula.client.core.model.NodePO,
     *      org.eclipse.jubula.client.core.model.NodePO)
     */
    public void postOperate(
        ITreeTraverserContext<INodePO> ctx, 
        INodePO parent, 
        INodePO node, boolean alreadyVisited) {
    }

    /**
     * returns true if any dependency found
     * @return
     *      boolean
     */
    public boolean hasDependency() {
        return !m_dependencyFinder.getDependentNodes().isEmpty();
    }


}
