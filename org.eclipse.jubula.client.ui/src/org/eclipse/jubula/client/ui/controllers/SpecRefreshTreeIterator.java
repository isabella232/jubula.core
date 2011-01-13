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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.client.ui.model.GuiNode;


/**
 * @author BREDEX GmbH
 * @created 28.07.2006
 */
public class SpecRefreshTreeIterator extends TreeIterator {
    
    /**
     * Constructor.
     * @param root the root of the tree.
     */
    public SpecRefreshTreeIterator(GuiNode root) {
        super(root);
    }
    
    /**
     * {@inheritDoc}
     * @param root
     */
    protected void setElements(GuiNode root) {
        if (root == null) {
            return;
        }
        List nodes = root.getChildren();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            GuiNode nextNode = (GuiNode)iter.next();
            if (nextNode != null) {
                getElements().add(nextNode);
            }
            setElements(nextNode); 
        }
    }

}
