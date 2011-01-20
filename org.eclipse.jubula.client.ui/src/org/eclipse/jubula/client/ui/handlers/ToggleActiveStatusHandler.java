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
package org.eclipse.jubula.client.ui.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created May 11, 2010
 */
public class ToggleActiveStatusHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

        if (activePart instanceof AbstractJBEditor
                && currentSelection instanceof IStructuredSelection) {
            AbstractJBEditor tce = (AbstractJBEditor)activePart;
            if (tce.getEditorHelper().requestEditableState() 
                    != JBEditorHelper.EditableState.OK) {
                return null;
            }
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)currentSelection;
            for (Iterator<GuiNode> it = structuredSelection.iterator(); it
                    .hasNext();) {
                INodePO node = it.next().getContent();
                node.setActive(!node.isActive());
                tce.getEditorHelper().setDirty(true);
            }
            DataEventDispatcher.getInstance().firePropertyChanged(false);
        }
        return null;
    }
}
