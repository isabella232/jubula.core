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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.GuiNodeBP;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created Aug 11, 2010
 */
public abstract class AbstractShowSpecificationHandler extends AbstractHandler {
    /**
     * Shows the SpecTS in the Test Suite Browser
     * 
     * @param node
     *            the node to show
     * @param toStart
     *            the guinode to start searching for
     * @param viewId
     *            the viewId to show the specification in
     */
    protected void showSpecGUINode(INodePO node, GuiNode toStart, 
        String viewId) {
        IViewPart view;
        GuiNode specGuiNode = GuiNodeBP.recursivlyfindNode(node, toStart);
        if (specGuiNode != null) {
            if (!Utils.openPerspective(Constants.SPEC_PERSPECTIVE)) {
                return;
            }
            if (!PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().getPerspective().getId().equals(
                            Constants.SPEC_PERSPECTIVE)) {
                Utils.createMessageDialog(// show error must be in SpecPers
                        MessageIDs.I_NO_PERSPECTIVE_CHANGE);
                return;
            }
            view = Plugin.showView(viewId, null, IWorkbenchPage.VIEW_ACTIVATE);
            ITreeViewerContainer specView = (ITreeViewerContainer)view;
            InteractionEventDispatcher.getDefault().
                fireProgammableSelectionEvent(
                        new StructuredSelection(specGuiNode));
            specView.getTreeViewer().refresh();
            specView.getTreeViewer().reveal(specGuiNode);
            specView.getTreeViewer().getTree().update();
            view.setFocus();
            specView.getTreeViewer().expandToLevel(specGuiNode, 0);
            specView.getTreeViewer().setSelection(
                    new StructuredSelection(specGuiNode), true);
        }
    }
}
