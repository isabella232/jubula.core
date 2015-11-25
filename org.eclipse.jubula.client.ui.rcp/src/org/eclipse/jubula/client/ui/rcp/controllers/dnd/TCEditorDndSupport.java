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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.RefToken;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Case Editor.
 *
 * @author BREDEX GmbH
 * @created 25.03.2008
 */
public class TCEditorDndSupport extends AbstractEditorDndSupport {

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
            IStructuredSelection toDrop, INodePO dropTarget, int dropPosition) {
        
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Object> selectedElements = toDrop.toList();
        Collections.reverse(selectedElements);
        Iterator iter = selectedElements.iterator();
        while (iter.hasNext()) {
            INodePO droppedNode = null;
            Object obj = iter.next();
            if (!(obj instanceof INodePO)) {
                return false;
            }
            INodePO node = (INodePO)obj;
            if (node instanceof ICapPO || node instanceof IExecTestCasePO
                    || node instanceof ICommentPO) {
                INodePO target = dropTarget;
                if (target != node
                    && (target instanceof ICapPO 
                        || target instanceof IExecTestCasePO
                        || target instanceof ICommentPO)) {
                        
                    droppedNode = moveNode(node, target);
                }
            }
            if (node instanceof ISpecTestCasePO) {
                if (performDrop(targetEditor, dropTarget, 
                        dropPosition, (ISpecTestCasePO)node) == null) {
                    return false;
                }
            }
            postDropAction(droppedNode, targetEditor);
        }
        return true;
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
        
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Object> selectedElements = toDrop.toList();
        
        ISpecTestCasePO targetNode;
        if (dropTarget instanceof ISpecTestCasePO) {
            targetNode = (ISpecTestCasePO)dropTarget;
        } else {
            targetNode = (ISpecTestCasePO)dropTarget.getParentNode();
        }
        
        boolean isContainsEctOrCap = false;
        for (Object obj : selectedElements.toArray()) {
            if (!(obj instanceof INodePO)) {
                return false;
            }

            if (obj instanceof IEventExecTestCasePO) {
                
                copyPasteEventExecTestCase(targetEditor,
                        (IEventExecTestCasePO)obj, targetNode, project);
            } else if (obj instanceof IExecTestCasePO
                    || obj instanceof ICapPO) {
                
                IParamNodePO paramNode = (IParamNodePO)obj;
                isContainsEctOrCap = true;
                if (!(targetNode.equals(paramNode.getParentNode())
                        || checkParentParameters(targetNode, paramNode, null,
                                project, false))) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (isContainsEctOrCap) {
            int position = targetNode.indexOf(dropTarget);
            for (Object obj : selectedElements.toArray()) {
                position++;
                
                if (obj instanceof IExecTestCasePO) {
                    
                    copyPasteExecTestCase(targetEditor, (IExecTestCasePO)obj,
                            targetNode, position, project);
                } else if (obj instanceof ICapPO) {
                    
                    copyPasteCap(targetEditor, (ICapPO)obj, targetNode,
                            position, project);
                }
            }
        }
        return true;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param origEvent The item that was dragged/cut.
     * @param targetNode target parent node
     * @param project currently project
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean copyPasteEventExecTestCase(
            AbstractTestCaseEditor targetEditor,
            IEventExecTestCasePO origEvent, ISpecTestCasePO targetNode,
            IProjectPO project) {
        
        if (targetNode.getEventExecTcMap()
                .containsKey(origEvent.getEventType())) {
            
            boolean status = MessageDialog.openQuestion(null,
                    Messages.DoubleEventTypeTitle,
                    NLS.bind(Messages
                            .TestCaseEditorDoubleEventTypeErrorDetailOverwrite,
                             new Object[]{targetNode.getName(), 
                                I18n.getString(origEvent.getEventType())}));
            if (status) {
                targetNode.getEventExecTcMap()
                    .remove(origEvent.getEventType());
            } else {
                return false;
            }
        }
        
        final EditSupport editSupport = targetEditor.getEditorHelper()
                .getEditSupport();
        ParamNameBPDecorator pMapper = targetEditor.getEditorHelper()
                .getEditSupport().getParamMapper();
        if (targetNode.equals(origEvent.getParentNode())
                || checkParentParameters(targetNode, origEvent, pMapper,
                        project, false)) {
            
            try {
                IEventExecTestCasePO newEvent = NodeMaker
                        .createEventExecTestCasePO(origEvent
                        .getSpecTestCase(), targetNode);
                fillExec(origEvent, newEvent);
                checkParentParameters(targetNode, newEvent, pMapper, project,
                        true);
                TestCaseBP.addEventHandler(editSupport, targetNode, newEvent);
                targetEditor.getEditorHelper().setDirty(true);

                DataEventDispatcher.getInstance()
                    .fireDataChangedListener(newEvent,
                        DataState.Added, UpdateState.onlyInEditor);
            } catch (InvalidDataException e) {
                // no log entry, because it is a use case!
                ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_DOUBLE_EVENT, null, 
                    new String[]{NLS.bind(
                            Messages.TestCaseEditorDoubleEventTypeErrorDetail,
                            new Object[]{targetNode.getName(), 
                                I18n.getString(origEvent.getEventType())})});
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            }
        } else {
            return false;
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
     * @param targetNode target parent node
     * @param project currently project
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean copyPasteExecTestCase(
        AbstractTestCaseEditor targetEditor, IExecTestCasePO execTestCase,
        ISpecTestCasePO targetNode, int dropPosition, IProjectPO project) {
    
        IExecTestCasePO newExecTestCase = NodeMaker
                .createExecTestCasePO(execTestCase.getSpecTestCase());
        fillExec(execTestCase, newExecTestCase);
        ParamNameBPDecorator pMapper = targetEditor.getEditorHelper()
                .getEditSupport().getParamMapper();
        checkParentParameters(targetNode, newExecTestCase, pMapper, project,
                true);
        TestCaseBP.addReferencedTestCase(targetNode, newExecTestCase,
                dropPosition);
        targetEditor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance()
            .fireDataChangedListener(newExecTestCase,
                DataState.Added, UpdateState.onlyInEditor);
        postDropAction(newExecTestCase, targetEditor);
        
        return true;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param cap The item that was dragged/cut.
     * @param targetNode target parent node
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @param project currently project
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean copyPasteCap(
            AbstractTestCaseEditor targetEditor, ICapPO cap,
            ISpecTestCasePO targetNode, int dropPosition, IProjectPO project) {
        
        ParamNameBPDecorator pMapper = targetEditor.getEditorHelper()
                .getEditSupport().getParamMapper();
        
        ICapPO newCap = CapBP.createCapWithDefaultParams(cap.getName(),
                cap.getComponentName(), cap.getComponentType(),
                cap.getActionName());
        fillCap(cap, newCap);
        newCap.setParentNode(targetNode);
        targetNode.addNode(dropPosition, newCap);
        checkParentParameters(targetNode, newCap, pMapper, project, true);
        targetEditor.getTreeViewer().expandToLevel(targetNode, 1);
        targetEditor.handleParamChanged();
        DataEventDispatcher.getInstance().fireParamChangedListener();
        postDropAction(newCap, targetEditor);
        
        return true;
    }

    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param toDrop The item that was dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @return the new IExecTestCasePO object if the drop/paste was successful. 
     *         Otherwise <code>null</code>.
     */
    private static IExecTestCasePO performDrop(
        AbstractTestCaseEditor targetEditor, INodePO dropTarget,
        int dropPosition, ISpecTestCasePO toDrop) {
        INodePO target = dropTarget;
        if (target != toDrop) {
            EditSupport editSupport = 
                targetEditor.getEditorHelper().getEditSupport();
            try {
                if (target instanceof ICapPO 
                    || target instanceof IExecTestCasePO) {
                    return dropOnCAPorExecTc(editSupport, toDrop, target,
                            dropPosition);
                } else if (target instanceof ISpecTestCasePO) {
                    return dropOnSpecTc(editSupport, toDrop, target);
                } else if (target instanceof ITestSuitePO) {
                    return dropOnTestsuite(editSupport,
                            (ITestSuitePO)target, toDrop);
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
            }
        }
        
        return null;
    }
    
    /**
     * Check the parameter of the new parent node. If possible it generate
     * the needed parameters and return a modified execTestCase
     * else it drop up an error message and return null. 
     *  
     * @param targetNode target node
     * @param paramNode original exec test case node
     * @param pMapper ParamNameBPDecorator
     * @param project currently project
     * @param create if <code>true</code> the parameter will be created
     * @return <code>false</code> if target parent node has a different parameter type
     *          with same name than the new parameter node .Otherwise <code>true</code>.
     */
    
    private static boolean checkParentParameters(
            ISpecTestCasePO targetNode, IParamNodePO paramNode,
            ParamNameBPDecorator pMapper, IProjectPO project, boolean create) {
        
        List<Locale> langs = project.getLangHelper().getLanguageList();
        for (Locale lang : langs) {
            for (Iterator<TDCell> it = paramNode
                    .getParamReferencesIterator(lang); it.hasNext();) {
                TDCell cell = it.next();
                String guid = paramNode.getDataManager()
                        .getUniqueIds().get(cell.getCol());
                IParamDescriptionPO childDesc = paramNode
                        .getParameterForUniqueId(guid);
                // The childDesc can be null if the parameter has been
                // removed in another session and not yet updated in the 
                // current editor session.
                if (childDesc != null) {
                    ModelParamValueConverter conv = 
                            new ModelParamValueConverter(cell.getTestData(),
                                    paramNode, lang, childDesc);
                    List<RefToken> refTokens = conv.getRefTokens();
                    for (RefToken refToken : refTokens) {
                        String oldGUID = RefToken.extractCore(refToken
                                .getModelString());
                        String paramName = ParamNameBP.getInstance().getName(
                                oldGUID, childDesc.getParentProjectId());

                        @SuppressWarnings("unchecked")
                        Map<String, String> oldToNewGuids = new HashedMap();
                        IParamDescriptionPO parentParamDescr = targetNode
                                .getParameterForName(paramName);
                        
                        if (parentParamDescr == null) {
                            if (create) {
                                targetNode.addParameter(childDesc.getType(),
                                        paramName, pMapper);
                                parentParamDescr = targetNode
                                        .getParameterForName(paramName);
                            }
                        } else if (!parentParamDescr.getType()
                                .equals(childDesc.getType())) {
                            MessageDialog.openInformation(null,
                                    Messages.ParameterConfligtDetectedTitle,
                                    NLS.bind(Messages.ParameterConfligtDetected,
                                    new Object[] {parentParamDescr.getName(),
                                            targetNode.getName()}));
                            return false;
                        }
                        
                        if (create) {
                            if (parentParamDescr != null) {
                                String newGuid = parentParamDescr.getUniqueId();
                                oldToNewGuids.put(oldGUID, newGuid);
                            }
                            // update TestDataPO of child with GUID for reference
                            conv.replaceGuidsInReferences(oldToNewGuids);
                            cell.getTestData().setValue(lang,
                                    conv.getModelString(), project);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 
     * @param toDrop The items that were copy.
     * @param dropTarget The paste target.
     * @return <code>true</code> if the given information indicates that the
     *         paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateCopy(IStructuredSelection toDrop,
            INodePO dropTarget) {
        
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
            return false;
        }
        
        for (Object obj : toDrop.toArray()) {
            if (!(obj instanceof INodePO)) {
                return false;
            } 
            INodePO transferGUI = (INodePO)obj;
            INodePO parentNode = transferGUI.getParentNode();
            if (parentNode.equals(obj)) {
                return false;
            }
            if (transferGUI instanceof ICapPO) {
                continue;
            }
            ISpecTestCasePO specTcGUI;
            if (!(dropTarget instanceof ISpecTestCasePO)) {
                specTcGUI = (ISpecTestCasePO)dropTarget.getParentNode();
            } else {
                specTcGUI = (ISpecTestCasePO)dropTarget;
            }
            if (specTcGUI.equals(parentNode)) {
                continue;
            }
            ISpecTestCasePO childGUI;
            if (transferGUI instanceof ICapPO) {
                continue;
            } else if (transferGUI instanceof ISpecTestCasePO) {
                childGUI = (ISpecTestCasePO)transferGUI;
            } else if (transferGUI instanceof IExecTestCasePO) {
                childGUI = ((IExecTestCasePO)transferGUI).getSpecTestCase();
            } else {
                return false;
            }
            if (childGUI.hasCircularDependences(specTcGUI)) {
                return false;
            }
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
            IStructuredSelection toDrop, INodePO dropTarget, 
            boolean allowFromBrowser) {
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
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

        Iterator iter = toDrop.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (!(obj instanceof INodePO)) {
                return false;
            } 
            INodePO transferGUI = (INodePO)obj;
            INodePO parentNode = transferGUI.getParentNode();
            if (!(parentNode instanceof ISpecTestCasePO)
                    && !(parentNode instanceof IProjectPO)
                    && !(parentNode instanceof ICategoryPO)) {
                return false;
            }
            if (!(transferGUI instanceof ISpecTestCasePO) 
                    && transferGUI.getParentNode() 
                        != dropTarget.getParentNode()) {
                return false;
            }
            ISpecTestCasePO specTcGUI;
            if (!(dropTarget instanceof ISpecTestCasePO)) {
                specTcGUI = (ISpecTestCasePO)dropTarget.getParentNode();
            } else {
                specTcGUI = (ISpecTestCasePO)dropTarget;
            }
            if (!(transferGUI instanceof ISpecTestCasePO)) {
                continue;
            }
            ISpecTestCasePO childGUI = (ISpecTestCasePO)transferGUI;
            if (childGUI.hasCircularDependences(specTcGUI)) {
                
                return false;
            }
        }
        return true;
    }

    /**
     * @param editSupport The EditSupport in which to perform the action.
     * @param node the node to be dropped.
     * @param target the target node.
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     * 
     * @return the new execTestCaseNode
     */
    private static IExecTestCasePO dropOnSpecTc(EditSupport editSupport, 
            INodePO node, INodePO target)
        throws PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        return TestCaseBP.addReferencedTestCase(editSupport, 
                target, (ISpecTestCasePO)node, 0);
    }
    
    /**
     * Drops the given TestCase on the given TestSuite.
     * The TestCase will be inserted at the end.
     * @param editSupport The EditSupport in which to perform the action.
     * @param testSuite the TestSuite to drop on
     * @param testcase the TestCAse to drop
     * @throws PMAlreadyLockedException in case of persistence error
     * @throws PMDirtyVersionException in case of persistence error
     * @throws PMException in case of persistence error
     * 
     * @return the new execTestCaseNode
     */
    private static IExecTestCasePO dropOnTestsuite(EditSupport editSupport, 
            ITestSuitePO testSuite, ISpecTestCasePO testcase) 
        throws PMAlreadyLockedException, 
        PMDirtyVersionException, PMException {
        
        return TestCaseBP.addReferencedTestCase(editSupport, testSuite, 
                testcase, 0);
    }

    /**
     * @param editSupport The EditSupport in which to perform the action.
     * @param node the node to be dropped
     * @param target the target node.
     * @param location One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     * 
     * @return the new execTestCaseNode
     */
    private static IExecTestCasePO dropOnCAPorExecTc(EditSupport editSupport, 
            INodePO node, INodePO target,
            int location) throws PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        ISpecTestCasePO specTcGUItoDrop = (ISpecTestCasePO)node;
        INodePO parentGUI = target.getParentNode();
        int position = parentGUI.indexOf(target);
        if (location != ViewerDropAdapter.LOCATION_BEFORE) {
            position++;
        }
        return TestCaseBP.addReferencedTestCase(editSupport, parentGUI, 
                specTcGUItoDrop, position);
    }
}
