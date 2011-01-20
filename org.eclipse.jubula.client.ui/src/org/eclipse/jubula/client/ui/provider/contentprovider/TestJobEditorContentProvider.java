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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.ui.model.GuiNode;


/**
 * @author BREDEX GmbH
 * @created Mar 17, 2010
 */
public class TestJobEditorContentProvider extends
        AbstractTreeViewContentProvider {

    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        GuiNode data = (GuiNode)parentElement;
        List<GuiNode> list = new ArrayList<GuiNode>(data.getChildren());
        return list.toArray();
    }
}
