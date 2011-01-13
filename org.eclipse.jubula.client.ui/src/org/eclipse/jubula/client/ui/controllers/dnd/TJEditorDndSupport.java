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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamCheckBP.SpecTcParamRefCheck;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.editors.GDEditorHelper;
import org.eclipse.jubula.client.ui.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.editors.TestJobEditor;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.RefTestSuiteGUI;
import org.eclipse.jubula.client.ui.model.TestJobGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.client.ui.views.TestSuiteBrowser;
import org.eclipse.ui.IViewPart;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Job Editor.
 *
 * @author BREDEX GmbH
 * @created Mar 17, 2010
 */
public class TJEditorDndSupport {

    /**
     * Private constructor
     */
    private TJEditorDndSupport() {
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
    public static boolean performDrop(TestJobEditor targetEditor,
            IStructuredSelection toDrop, GuiNode dropTarget, int dropPosition) {
        if (targetEditor.getEditorHelper().requestEditableState() 
                != GDEditorHelper.EditableState.OK) {
            return false;
        }
        List<Object> selectedElements = toDrop.toList();
        Collections.reverse(selectedElements);
        Iterator iter = selectedElements.iterator();
        while (iter.hasNext()) {
            GuiNode droppedNode = null;
            Object obj = iter.next();
            if (!(obj instanceof GuiNode)) {
                return false;
            }
            GuiNode node = (GuiNode)obj;
            if (node instanceof RefTestSuiteGUI) {
                GuiNode target = dropTarget;
                if (target != node
                    && (target instanceof RefTestSuiteGUI)) {
                    droppedNode = moveNode(node, target);
                }
            }
            if (node instanceof TestSuiteGUI) {
                GuiNode target = dropTarget;
                if (target != node) {
                    try {
                        if (target instanceof RefTestSuiteGUI) {
                            dropOnRefTS(node, target, dropPosition);
                        } else if (target instanceof TestJobGUI) {
                            dropOnTJ(node, target);
                        } 
                    } catch (PMException e) {
                        NodeEditorInput inp = (NodeEditorInput)targetEditor.
                            getAdapter(NodeEditorInput.class);
                        INodePO inpNode = inp.getNode();
                        PMExceptionHandler.handlePMExceptionForMasterSession(e);

                        // If an object was already locked, *and* the locked 
                        // object is not the editor Test Case, *and* the editor 
                        // is dirty, then we do *not* want to revert all 
                        // editor changes.
                        // The additional test as to whether the the editor is 
                        // marked as dirty is important because, due to the 
                        // requestEditableState() call earlier in this method, 
                        // the editor TC is locked (even though the editor 
                        // isn't dirty). Reopening the editor removes this lock.
                        if (!(e instanceof PMAlreadyLockedException
                                && ((PMAlreadyLockedException)e)
                                    .getLockedObject() != null
                                && !((PMAlreadyLockedException)e)
                                    .getLockedObject().equals(inpNode))
                            || !targetEditor.isDirty()) {
                            
                            try {
                                targetEditor.reOpenEditor(inpNode);
                            } catch (PMException e1) {
                                PMExceptionHandler.handlePMExceptionForEditor(e,
                                        targetEditor);
                            }
                        }
                        return false;
                    }
                }
            }
            postDropAction(droppedNode, targetEditor);
        }
        return true;
    }

    /**
     * 
     * @param sourceViewer The viewer containing the dragged/cut item.
     * @param targetViewer The viewer to which the item is to be dropped/pasted.
     * @param toDrop The items that were dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param allowFromBrowser Whether items from the Test Case Suite are 
     *                         allowed to be dropped/pasted.
     * @return <code>true</code> if the given information indicates that the
     *         drop/paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateDrop(Viewer sourceViewer,
            Viewer targetViewer, IStructuredSelection toDrop,
            GuiNode dropTarget, boolean allowFromBrowser) {
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
            return false;
        }
        if (sourceViewer != null && !sourceViewer.equals(targetViewer)) {
            if (getTSBrowser() != null) {
                if (!(allowFromBrowser && sourceViewer.equals(getTSBrowser()
                        .getTreeViewer()))) {
                    return false;
                }
            } else {
                return false;
            }
        }
        Iterator iter = toDrop.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (!(obj instanceof GuiNode)) {
                return false;
            }
            GuiNode transferGUI = (GuiNode)obj;
            if (!(transferGUI instanceof TestSuiteGUI 
                    || transferGUI instanceof RefTestSuiteGUI)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return instance of TestSuiteBrowser, or null.
     */
    static TestSuiteBrowser getTSBrowser() {
        IViewPart viewPart = Plugin.getView(Constants.TS_BROWSER_ID);
        if (viewPart != null) {
            return (TestSuiteBrowser)viewPart;
        }
        return null;
    }

    /**
     * @param node the node to be dropped.
     * @param target the target node.
     * @throws PMReadException in case of db read error
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     */
    private static void dropOnTJ(GuiNode node, GuiNode target)
        throws PMReadException, PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        if (getTSBrowser() != null) {
            getTSBrowser().addReferencedTestSuite(
                    (ITestSuitePO)node.getContent(), target.getContent(), 0);
        }
    }

    /**
     * @param node the node to be dropped
     * @param target the target node.
     * @param location One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @throws PMReadException in case of db read error
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     */
    private static void dropOnRefTS(GuiNode node, GuiNode target, 
            int location) throws PMReadException, PMAlreadyLockedException, 
            PMDirtyVersionException, PMException {
        TestSuiteGUI tsGUItoDrop = (TestSuiteGUI)node;
        GuiNode parentGUI = target.getParentNode();
        int position = parentGUI.indexOf(target);
        if (location != ViewerDropAdapter.LOCATION_BEFORE) {
            position++;
        }
        getTSBrowser().addReferencedTestSuite(
                (ITestSuitePO)tsGUItoDrop.getContent(),
                parentGUI.getContent(), position);
    }

    /**
     * @param node the node to be moved.
     * @param target the target node.
     * @return the dropped GuiNode.
     */
    private static GuiNode moveNode(GuiNode node, GuiNode target) {
        int actualPos = node.getParentNode().getChildren().indexOf(node);
        int newPos = target.getParentNode().indexOf(target);
        node.getParentNode().moveNode(actualPos, newPos);
        // the real model
        node.getParentNode().getContent()
            .removeNode(node.getContent());
        target.getParentNode().getContent()
            .addNode(newPos, node.getContent());
        return node;
    }

    /**
     * Executes actions after the drop.
     * @param node the dropped node. 
     * @param targetEditor The editor to which the item has been dropped/pasted.
     */
    private static void postDropAction(GuiNode node, 
            TestJobEditor targetEditor) {
        targetEditor.setFocus();
        if (node != null) {
            targetEditor.getTreeViewer().setSelection(
                    new StructuredSelection(node));            
        }
        targetEditor.getTreeViewer().refresh();
        targetEditor.getEditorHelper().setDirty(
                SpecTcParamRefCheck.editorShouldBeDirty());
        SpecTcParamRefCheck.setEditorShouldBeDirty(true);
        LocalSelectionTransfer.getInstance().setSelection(null);
    }
}
