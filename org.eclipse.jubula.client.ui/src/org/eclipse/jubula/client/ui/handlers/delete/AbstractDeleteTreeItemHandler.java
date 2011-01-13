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
package org.eclipse.jubula.client.ui.handlers.delete;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.model.GuiNode;


/**
 * Superclass of all DeleteTreeItem Handlers
 *
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public abstract class AbstractDeleteTreeItemHandler extends AbstractHandler {

    /**
     * Pops up a "confirmDelete" dialog.
     * 
     * @param sel
     *            The actual selection.
     * @return True, if "yes" was clicked, false otherwise
     */
    protected boolean confirmDelete(IStructuredSelection sel) {
        List<String> itemNames = new ArrayList<String>();
        for (Object obj : sel.toList()) {
            if (obj instanceof GuiNode) {
                itemNames.add(((GuiNode)obj).getName());
            } else {
                String name = getName(obj);
                if (!StringUtils.isBlank(name)) {
                    itemNames.add(name);
                }
            }
        }

        return DeleteHandlerHelper.confirmDelete(itemNames);
    }

    /**
     * Subclasses may override to provide name for given object
     * 
     * @param obj
     *            the object to get the name for
     * @return may return null if no name available; otherwise the name to
     *         display for delete operation
     */
    protected String getName(Object obj) {
        return null;
    }

    /**
     * Closes the editor for the given Node
     * @param node the node of the editor to be closed.
     */
    protected static void closeOpenEditor(IPersistentObject node) {
        DeleteHandlerHelper.closeOpenEditor(node);
    }
    
    
}