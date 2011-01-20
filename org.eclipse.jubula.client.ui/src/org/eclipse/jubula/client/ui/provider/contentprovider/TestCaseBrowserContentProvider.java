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
package org.eclipse.jubula.client.ui.provider.contentprovider;

import org.eclipse.jubula.client.ui.model.GuiNode;

/**
 * @author BREDEX GmbH
 * @created 08.10.2004
 */
public class TestCaseBrowserContentProvider 
    extends AbstractTreeViewContentProvider {

    /**
     * {@inheritDoc}
     * @param parentElement Object
     * @return object array
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof GuiNode) {
            return ((GuiNode) parentElement).getChildren().toArray();
        }
        return new Object[0];
    }  
}