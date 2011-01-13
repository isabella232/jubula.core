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
package org.eclipse.jubula.rc.common.implclasses.tree;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;


/**
 * @author BREDEX GmbH
 * @created 19.01.2006
 */
public class BoundsTreeNodeOperation extends AbstractTreeNodeOperation {
    
    /** The bounds of the node */
    private Rectangle m_bounds = null;

    /**
     * Constructor
     */
    public BoundsTreeNodeOperation() {
        // Empty constructor
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean operate(Object node) throws StepExecutionException {
        if (getContext().isVisible(node)) {
            Rectangle nodeBounds = getContext().getNodeBounds(node);
            m_bounds = nodeBounds;
        }
        return true;
    }
    
    /**
     * Getter for the tree node bounds.
     * @return bounds of the node
     */
    public Rectangle getBounds() {
        return m_bounds;
    }
}