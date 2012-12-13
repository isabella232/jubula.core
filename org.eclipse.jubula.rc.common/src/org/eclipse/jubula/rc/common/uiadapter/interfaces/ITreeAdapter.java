/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.uiadapter.interfaces;

import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;

/**
 * Interface for all necessary methods for testing trees.
 * 
 * @author BREDEX GmbH
 */
public interface ITreeAdapter extends IWidgetAdapter {

    /**
     * Gets the root node(s) of the tree.
     * This could be either a single node or multiple nodes
     * @return The root node(s).
     */ 
    public Object getRootNode();
    
    /**
     * Gets the TreeOperationContext which is created through an
     * toolkit specific implementation.
     * @return the TreeOperationContext for the tree
     */
    public AbstractTreeOperationContext getContext();
    
    /**
     * 
     * @return The visibility of the Root Node
     */
    public boolean isRootVisible();
}
