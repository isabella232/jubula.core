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
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.model.CapGUI;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.TestCaseBrowserRootGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.client.ui.views.TestCaseBrowser;
import org.eclipse.ui.IViewPart;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Case Editor.
 *
 * @author BREDEX GmbH
 * @created 25.03.2008
 */
public class TCEditorDndSupport {

    /**
     * Private constructor
     */
    private TCEditorDndSupport() {
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
            IStructuredSelection toDrop, GuiNode dropTarget, int dropPosition) {
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
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
            if (node instanceof CapGUI || node instanceof ExecTestCaseGUI) {
                GuiNode target = dropTarget;
                if (target != node
                    && (target instanceof CapGUI 
                        || target instanceof ExecTestCaseGUI)) {
                        
                    droppedNode = moveNode(node, target);
                }
            }
            if (node instanceof SpecTestCaseGUI) {
                GuiNode target = dropTarget;
                if (target != node) {
                    try {
                        if (target instanceof CapGUI 
                            || target instanceof ExecTestCaseGUI) {
                            dropOnCAPorExecTc(node, target, dropPosition);
                        } else if (target instanceof SpecTestCaseGUI) {
                            dropOnSpecTc(node, target);
                        } else if (node instanceof SpecTestCaseGUI 
                                && target instanceof TestSuiteGUI) {
                                
                            dropOnTestsuite((TestSuiteGUI)target, 
                                    (SpecTestCaseGUI)node);
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
     * @param allowFromBrowser Whether items from the Test Case Browser are 
     *                         allowed to be dropped/pasted.
     * @return <code>true</code> if the given information indicates that the
     *         drop/paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateDrop(Viewer sourceViewer, Viewer targetViewer,
            IStructuredSelection toDrop, GuiNode dropTarget, 
            boolean allowFromBrowser) {
        
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
            return false;
        }

        if (sourceViewer != null 
            && !sourceViewer.equals(targetViewer)) {
            
            if (getTCBrowser() != null) {
                if (!(allowFromBrowser 
                    && sourceViewer.equals(getTCBrowser().getTreeViewer()))) {
                    
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
            GuiNode parentNode = transferGUI.getParentNode();
            if (!(parentNode instanceof SpecTestCaseGUI)
                    && !(parentNode instanceof TestCaseBrowserRootGUI)
                    && !(parentNode instanceof CategoryGUI)) {
                return false;
            }
            if (!(transferGUI instanceof SpecTestCaseGUI) 
                    && transferGUI.getParentNode() 
                        != dropTarget.getParentNode()) {
                return false;
            }
            SpecTestCaseGUI specTcGUI;
            if (!(dropTarget instanceof SpecTestCaseGUI)) {
                specTcGUI = (SpecTestCaseGUI)dropTarget.getParentNode();
            } else {
                specTcGUI = (SpecTestCaseGUI)dropTarget;
            }
            if (!(transferGUI instanceof SpecTestCaseGUI)) {
                continue;
            }
            SpecTestCaseGUI childGUI = (SpecTestCaseGUI)transferGUI;
            if (childGUI.getContent().hasCircularDependences(
                specTcGUI.getContent())) {
                
                return false;
            }
        }
        return true;

    }

    /**
     * @return instance of TestCaseBrowser, or null.
     */
    static TestCaseBrowser getTCBrowser() {
        IViewPart viewPart = Plugin.getView(Constants.TC_BROWSER_ID);
        if (viewPart != null) {
            return (TestCaseBrowser)viewPart;
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
    private static void dropOnSpecTc(GuiNode node, GuiNode target)
        throws PMReadException, PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        getTCBrowser().addReferencedTestCase(
                (ISpecTestCasePO)node.getContent(), target.getContent(), 0);
    }
    
    /**
     * Drops the given TestCase on the given TestSuite.
     * The TestCase will be inserted at the end.
     * @param testSuiteGUI the TestSuite to drop on
     * @param testcaseGUI the TestCAse to drop
     * @throws PMReadException in case of persistance error
     * @throws PMAlreadyLockedException in case of persistance error
     * @throws PMDirtyVersionException in case of persistance error
     * @throws PMException in case of persistance error
     */
    private static void dropOnTestsuite(TestSuiteGUI testSuiteGUI, 
            SpecTestCaseGUI testcaseGUI) 
        throws PMReadException, PMAlreadyLockedException, 
        PMDirtyVersionException, PMException {
        
        if (getTCBrowser() != null) {
            getTCBrowser().addReferencedTestCase(
                    (ISpecTestCasePO)testcaseGUI.getContent(), 
                    testSuiteGUI.getContent(), 0);
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
    private static void dropOnCAPorExecTc(GuiNode node, GuiNode target,
            int location) throws PMReadException, PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        SpecTestCaseGUI specTcGUItoDrop = (SpecTestCaseGUI)node;
        GuiNode parentGUI = target.getParentNode();
        int position = parentGUI.indexOf(target);
        if (location != ViewerDropAdapter.LOCATION_BEFORE) {
            position++;
        }
        getTCBrowser().addReferencedTestCase(
                (ISpecTestCasePO)specTcGUItoDrop.getContent(),
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
            AbstractTestCaseEditor targetEditor) {
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
