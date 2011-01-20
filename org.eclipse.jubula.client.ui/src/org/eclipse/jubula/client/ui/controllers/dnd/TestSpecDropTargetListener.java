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
package org.eclipse.jubula.client.ui.controllers.dnd;

import java.util.List;

import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.TestCaseBrowserRootGUI;
import org.eclipse.jubula.client.ui.views.TestCaseBrowser;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;



/**
 * @author BREDEX GmbH
 * @created 06.07.2005
 */
public class TestSpecDropTargetListener extends ViewerDropAdapter {

    /**
     * @param view the depending view.
     */
    public TestSpecDropTargetListener(TestCaseBrowser view) {
        super(view.getTreeViewer());
        boolean scrollExpand = Plugin.getDefault().getPreferenceStore().
            getBoolean(Constants.TREEAUTOSCROLL_KEY);
        setScrollExpandEnabled(scrollExpand);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean performDrop(Object data) {
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        GuiNode target = (GuiNode)getCurrentTarget();
        List <GuiNode> nodesToBeMoved = transfer.getSelection().toList();
        try {
            TCBrowserDndSupport.moveNodes(nodesToBeMoved, target);
            return true;
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        } finally {
            LocalSelectionTransfer.getInstance().setSelection(null);
        }
        
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
        if (LocalSelectionTransfer.getInstance().getSelection() == null) {
            return false;
        }
        if (getCurrentLocation() == LOCATION_BEFORE
            || getCurrentLocation() == LOCATION_AFTER) {
            return false;
        }
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        if (transfer.getSource() != null 
            && !transfer.getSource().equals(getViewer())) {
            return false;
        }
        if (target instanceof TestCaseBrowserRootGUI 
                && getCurrentLocation() == LOCATION_BEFORE) {
                
            return false;
        }

        return TCBrowserDndSupport.canMove(transfer.getSelection(), target);
    }

    /**
     * no expand of SpecTestCaseGUI nodes
     * {@inheritDoc}
     */
    public void dragOver(DropTargetEvent event) {
        super.dragOver(event);
        if (event.item != null
            && event.item.getData() instanceof SpecTestCaseGUI) {
            event.feedback &= ~DND.FEEDBACK_EXPAND;
        }
        if (getCurrentLocation() == LOCATION_BEFORE
            || getCurrentLocation() == LOCATION_AFTER) {
            event.feedback &= ~DND.FEEDBACK_EXPAND;
        }
    }
}