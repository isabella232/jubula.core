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
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.osgi.util.NLS;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Suite Editor.
 *
 * @author BREDEX GmbH
 * @created 27.03.2008
 */
public class TSEditorDndSupport extends AbstractEditorDndSupport {

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
     * 
     * @param targetEditor The editor to which the item is to be pasted.
     * @param toDrop The items that were copy.
     * @param dropTarget The paste target.
     *                     indicate the drop position relative to the drop
     *                     target.
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean copyPaste(AbstractTestCaseEditor targetEditor,
            IStructuredSelection toDrop, INodePO dropTarget) {
        
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Object> selectedElements = toDrop.toList();
        
        ITestSuitePO targetNode;
        if (dropTarget instanceof ITestSuitePO) {
            targetNode = (ITestSuitePO)dropTarget;
        } else {
            targetNode = (ITestSuitePO)dropTarget.getParentNode();
        }
        int position = targetNode.indexOf(dropTarget);
        boolean needRefParamMessage = true;
        boolean needPropCompNameMessage = true;
        for (Object obj : selectedElements.toArray()) {
            position++;
            
            if (!(obj instanceof IParamNodePO)) {
                return false;
            }
            boolean modifyRefParam = !checkRefParam((IParamNodePO)obj,
                    false);
            if (needRefParamMessage && modifyRefParam) {
                needRefParamMessage = false;
                MessageDialog.openInformation(null,
                        Messages.NotUseReferenceParameterTitle,
                        NLS.bind(Messages.NotUseReferenceParameter,
                            new Object[]{((IParamNodePO)obj).getName()}));
            }
            boolean modifyPropCompName =
                    !checkCompName((IParamNodePO)obj, false);
            if (needPropCompNameMessage && modifyPropCompName) {
                MessageDialog.openInformation(null,
                        Messages.NotUsePropagatedComponentNameTitle,
                        NLS.bind(Messages.NotUsePropagatedComponentName,
                                new Object[]{((IParamNodePO)obj).getName()}));
                needPropCompNameMessage = false;
            }
            
            if (obj instanceof IExecTestCasePO) {
                
                copyPasteExecTestCase(targetEditor, (IExecTestCasePO)obj,
                        targetNode, position, modifyRefParam,
                        modifyPropCompName);
            } else if (obj instanceof ICapPO) {
                
                copyPasteCap(targetEditor, (ICapPO)obj, targetNode,
                        position, modifyRefParam, modifyPropCompName);
            }
        }
        return true;
    }
    
    /**
     * 
     * @param paramNode original exec test case node
     * @param modifiy if <code>true</code> then delete all references parameter.
     * @return <code>false</code> if paramNodePO contain references parameter
     *              and the modify is false. Otherwise return <code>true</code>. 
     */
    private static boolean checkRefParam(
            IParamNodePO paramNode, boolean modifiy) {
        
        Iterator<TDCell> it = paramNode.getParamReferencesIterator();
        while (it.hasNext()) {
            if (modifiy) {
                it.next().setTestData(""); //$NON-NLS-1$
            } else {
                return false;
            }
        }
        return true;
    }
        
    /**
     * 
     * @param paramNodePO original exec test case node
     * @param modifiy if true then set all component name propagation false.
     * @return <code>false</code> if paramNodePO contain propagated component name
     *              and the modify is false. Otherwise return <code>true</code>. 
     */
    private static boolean checkCompName(
            IParamNodePO paramNodePO, boolean modifiy) {
        
        if (paramNodePO instanceof IExecTestCasePO) {
            for (ICompNamesPairPO origPair : ((IExecTestCasePO)paramNodePO)
                    .getCompNamesPairs()) {
                if (origPair.isPropagated()) {
                    if (modifiy) {
                        origPair.setPropagated(false);
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param execTestCase The item that was dragged/cut.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @param modifyRefParam <code>true</code> if necessary to the reference parameters
     *              delete from the new exec test node.
     * @param modifyPropCompName <code>true</code> if necessary to set component names
     *              propagation to false.
     * @param targetNode target parent node
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean copyPasteExecTestCase(
            AbstractTestCaseEditor targetEditor, IExecTestCasePO execTestCase,
            ITestSuitePO targetNode, int dropPosition, boolean modifyRefParam,
            boolean modifyPropCompName) {
        
        ISpecTestCasePO specTestCase = execTestCase.getSpecTestCase();
        IExecTestCasePO newExecTestCase = NodeMaker.createExecTestCasePO(
                specTestCase);
        fillExec(execTestCase, newExecTestCase);
        if (modifyRefParam) {
            checkRefParam(newExecTestCase, true);
        }
        if (modifyPropCompName) {
            checkCompName(newExecTestCase, true);
        }
        TestCaseBP.addReferencedTestCase(targetNode, newExecTestCase,
                dropPosition);
        targetEditor.getEditorHelper().setDirty(true);
        postDropAction(newExecTestCase, targetEditor);
        
        return true;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param cap The item that was dragged/cut.
     * @param targetNode The target parent node.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @param modifyRefParam <code>true</code> if necessary to the reference parameters
     *              delete from the new exec test node.
     * @param modifyPropCompName <code>true</code> if necessary to set component names
     *              propagation to false.
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean copyPasteCap(
            AbstractTestCaseEditor targetEditor, ICapPO cap,
            ITestSuitePO targetNode, int dropPosition, boolean modifyRefParam,
            boolean modifyPropCompName) {
        
        ICapPO newCap = CapBP.createCapWithDefaultParams(cap.getName(),
                cap.getComponentName(), cap.getComponentType(),
                cap.getActionName());
        fillCap(cap, newCap);
        if (modifyRefParam) {
            checkRefParam(newCap, true);
        }
        if (modifyPropCompName) {
            checkCompName(newCap, true);
        }
        newCap.setParentNode(targetNode);
        targetNode.addNode(dropPosition, newCap);
        targetEditor.getTreeViewer().expandToLevel(targetNode, 1);
        targetEditor.handleParamChanged();
        DataEventDispatcher.getInstance().fireParamChangedListener();
        postDropAction(newCap, targetEditor);
        
        return true;
    }

    /**
     * Checks whether the nodes in the given selection can legally be copied
     * to the given target location.
     *
     * @param toDrop The selection to check.
     * @param target The target location to check.
     * @return <code>true</code> if the move is legal. Otherwise, 
     *         <code>false</code>.
     */
    public static boolean validateCopy(IStructuredSelection toDrop,
            INodePO target) {
        return validateCopy(toDrop, target, IExecTestCasePO.class,
                IEventExecTestCasePO.class);
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
