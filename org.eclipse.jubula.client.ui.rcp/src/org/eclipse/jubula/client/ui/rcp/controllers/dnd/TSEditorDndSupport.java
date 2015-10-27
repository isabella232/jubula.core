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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Suite Editor.
 *
 * @author BREDEX GmbH
 * @created 27.03.2008
 */
public class TSEditorDndSupport {

    /**
     * Private constructor
     */
    private TSEditorDndSupport() {
        // Do nothing
    }

    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param toDrop The items that were dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @return <code>true</code> if the drop/paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean performDrop(AbstractTestCaseEditor targetEditor,
            IStructuredSelection toDrop, INodePO dropTarget, int dropPosition) {
    
        return TCEditorDndSupport.performDrop(targetEditor, toDrop, dropTarget, 
                dropPosition);
    }

    /**
     * Checks whether the nodes in the given selection can legally be moved
     * to the given target location.
     *
     * @param sourceViewer The viewer containing the dragged/cut item.
     * @param targetViewer The viewer to which the item is to be dropped/pasted.
     * @param selection The selection to check.
     * @param target The target location to check.
     * @param allowFromBrowser Whether items from the Test Case Browser are 
     *                         allowed to be dropped/pasted.
     * @return <code>true</code> if the move is legal. Otherwise, 
     *         <code>false</code>.
     */
    public static boolean validateDrop(Viewer sourceViewer, Viewer targetViewer,
            IStructuredSelection selection, 
            Object target, boolean allowFromBrowser) {
        
        if (selection == null || target == null) {
            return false;
        }

        if (sourceViewer != null && !sourceViewer.equals(targetViewer)) {
            boolean foundOne = false;
            for (TestCaseBrowser tcb : MultipleTCBTracker.getInstance()
                    .getOpenTCBs()) {
                if (sourceViewer.equals(tcb.getTreeViewer())) {
                    foundOne = true;
                }
            }
            if (!(allowFromBrowser && foundOne)) {
                return false;
            }
        }

        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object transferObj = iter.next();
            if (!(transferObj instanceof INodePO)) {
                return false;
            } 
            INodePO transferGUI = (INodePO)transferObj;
            if (!((transferGUI instanceof IExecTestCasePO
                    && sourceViewer == targetViewer)
                    || (transferGUI instanceof ICommentPO
                            && sourceViewer == targetViewer)
                    || (transferGUI instanceof ISpecTestCasePO))) {
                
                return false;
            }
        }
        return true;
    }

}
