/*******************************************************************************
 * Copyright (c) 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Markus Tiede
 */
public abstract class AbstractNewHandler extends AbstractHandler {

    /**
     * @param event the execution event
     * @return the parent node to create the new node at
     */
    protected INodePO getParentNode(ExecutionEvent event) {
        INodePO parentNode = null;
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection sselection = (IStructuredSelection) selection;
            
            if (!sselection.isEmpty()) {
                Object selectedNode = sselection.getFirstElement();
                if (selectedNode instanceof INodePO) {
                    parentNode = (INodePO) selectedNode;
                    while (!(parentNode instanceof ICategoryPO 
                            || parentNode == null)) {
                        parentNode = parentNode.getParentNode();
                    }
                } else if (selectedNode instanceof IExecObjContPO) {
                    parentNode = IExecObjContPO.TSB_ROOT_NODE;
                } else if (selectedNode instanceof ISpecObjContPO) {
                    parentNode = ISpecObjContPO.TCB_ROOT_NODE;
                }
            }
        }
        if (parentNode == null) {
            IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
            if (activePart instanceof TestSuiteBrowser) {
                parentNode = IExecObjContPO.TSB_ROOT_NODE;
            } else {
                parentNode = ISpecObjContPO.TCB_ROOT_NODE;
            }
        }
        return parentNode;
    }
}
