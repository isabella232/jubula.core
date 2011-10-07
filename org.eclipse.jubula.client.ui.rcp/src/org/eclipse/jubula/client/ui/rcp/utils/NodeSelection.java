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
package org.eclipse.jubula.client.ui.rcp.utils;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.INodePO;


/**
 * Object to store current tree selections (for restoring tree states).
 * @author BREDEX GmbH
 * @created 07.02.2007
 */
public class NodeSelection {

    /** a NodePO object */
    private INodePO m_node;
    /** a CapPO object */
    private ICapPO m_cap;
    
    /**
     * Constructor.
     * @param node the parent node
     * @param cap the cap (or <code>null</code>)
     */
    public NodeSelection(INodePO node, ICapPO cap) {
        m_node = node;
        m_cap = cap;
    }
    
    /**
     * @return the cap
     */
    public ICapPO getCap() {
        return m_cap;
    }
    
    /**
     * @return the node
     */
    public INodePO getNode() {
        return m_node;
    } 
}