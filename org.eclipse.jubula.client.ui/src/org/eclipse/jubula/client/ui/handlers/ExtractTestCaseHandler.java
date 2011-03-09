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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.TreeOpsBP;
import org.eclipse.jubula.client.core.businessprocess.TreeOpsBP.TreeOpFailedException;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.HibernateUtil;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.core.persistence.TransactionSupport;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransactAction;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;



/**
 * Handler for extract Test Case Command
 *
 * @author BREDEX GmbH
 * @created 27.04.2009
 */
public class ExtractTestCaseHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof AbstractTestCaseEditor) {
            final AbstractTestCaseEditor editor = 
                (AbstractTestCaseEditor)activePart;
            if (editor.isDirty()) {
                Dialog editorDirtyDlg = Utils.createMessageDialog(
                        MessageIDs.Q_SAVE_AND_EXTRACT);
                if (editorDirtyDlg.getReturnCode() != Window.OK) {
                    return null;
                }
                editor.doSave(new NullProgressMonitor());
            }
            String extractedTCName = getNewName(editor);
            InputDialog dialog = new InputDialog(Plugin.getShell(), 
                        Messages.NewTestCaseActionTCTitle,
                        extractedTCName,
                        Messages.NewTestCaseActionTCMessage,
                        Messages.RenameActionTCLabel,
                        Messages.RenameActionTCError,
                        Messages.NewTestCaseActionDoubleTCName,
                        IconConstants.NEW_TC_DIALOG_STRING,
                        Messages.NewTestCaseActionTCShell,
                        false);
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(), 
                    ContextHelpIds.DIALOG_TESTCASE_EXTRACT);
            dialog.open();
            int retCode = dialog.getReturnCode();
            String newTcName = dialog.getName();
            dialog.close();
            if (retCode != Window.OK) {       
                return null;
            }
            final INodePO node = 
                (INodePO)editor.getEditorHelper()
                    .getEditSupport().getOriginal();
            if (node != null) {
                if (editor.getTreeViewer().getSelection() 
                        instanceof IStructuredSelection) {
                    validateNode(node);
                    final IStructuredSelection selection = 
                        (IStructuredSelection)editor.getTreeViewer()
                            .getSelection();
                    ExtractionReturn extractRet = performExtraction(newTcName,
                            node, selection);
                    if (extractRet.getErrorMessage() != null) {
                        Utils.createMessageDialog(new JBException(
                                extractRet.getErrorMessage(), 
                                MessageIDs.E_UNEXPECTED_EXCEPTION), null, 
                                new String[]{
                                    extractRet.getErrorMessage()});
                    }
                    try {
                        editor.reOpenEditor(node);
                    } catch (PMException e) {
                        PMExceptionHandler.handlePMExceptionForEditor(e, 
                                editor);
                    }
                }
            } else {
                Utils.createMessageDialog((new JBException(Messages
                    .EditorWillBeClosed, MessageIDs.E_DELETED_TC)), null, null);
                try {
                    GeneralStorage.getInstance().reloadMasterSession(
                            new NullProgressMonitor());
                } catch (ProjectDeletedException e) {
                    PMExceptionHandler.handleGDProjectDeletedException();
                }
            }
        }
        return null;
    }
    
    /**
     * @param editor
     *            the current editor
     * @return the new extracted test case name
     */
    private String getNewName(AbstractTestCaseEditor editor) {
        String newName = InitialValueConstants.DEFAULT_TEST_CASE_NAME;
        final IStructuredSelection cs = (IStructuredSelection)editor
                .getTreeViewer().getSelection();
        if (cs.size() == 1) {
            Object e = cs.getFirstElement();
            if (e instanceof ExecTestCaseGUI) {
                IExecTestCasePO exec = ((IExecTestCasePO)((ExecTestCaseGUI)e)
                        .getContent());
                if (exec != null) {
                    String execName = exec.getName();
                    if (!StringUtils.isBlank(execName)) {
                        newName = execName;
                    }
                }
            }
        }
        return newName;
    }

    /**
     * @author BREDEX GmbH
     * @created 12.09.2005
     */
    private static class ExtractionReturn {
        
        
        /** The error message */
        private String m_errorMessage = null;
        
        /** the new ExecTestCasePO */  
        private IExecTestCasePO m_execTestCasePO = null;
        

        /**
         * @return Returns the errorMessage.
         */
        public String getErrorMessage() {
            return m_errorMessage;
        }

        /**
         * @return Returns the execTestCasePO.
         */
        public IExecTestCasePO getExecTestCasePO() {
            return m_execTestCasePO;
        }

        /**
         * @param errorMessage The errorMessage to set.
         */
        public void setErrorMessage(String errorMessage) {
            m_errorMessage = errorMessage;
        }

        /**
         * @param execTestCasePO The execTestCasePO to set.
         */
        public void setExecTestCasePO(IExecTestCasePO execTestCasePO) {
            m_execTestCasePO = execTestCasePO;
        }
        
    }

    /**
     * @param node the {@link INodePO} on which the extraction is to be performed.
     */
    private void validateNode(INodePO node) {
        Assert.verify(node instanceof ISpecTestCasePO 
            || node instanceof ITestSuitePO, 
            Messages.ExtractTestCaseOperateISpecTestCasePO);
    }

    /**
     * performs the extraction
     * @param newTcName the name of the new SpecTestCase
     * @param node the edited {@link INodePO} from which to extract
     * @param selection the nodes to be extracted
     * @return an error message or null.
     */
    private ExtractionReturn performExtraction(final String newTcName,
        final INodePO node, final IStructuredSelection selection) {
        
        final List <IParamNodePO> modNodes = 
            new ArrayList<IParamNodePO>(selection.size());
        Iterator it = selection.iterator();
        while (it.hasNext()) {
            GuiNode selNode = (GuiNode)it.next();
            modNodes.add((IParamNodePO)selNode.getContent());
        }
        try {
            ExtractionReturn extractionRet = 
                persistExtraction(node, newTcName, modNodes);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                node, DataState.StructureModified, UpdateState.all);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                extractionRet.getExecTestCasePO(), DataState.Added, 
                UpdateState.all);
            final ISpecTestCasePO newSpecTc = 
                extractionRet.getExecTestCasePO().getSpecTestCase();
            DataEventDispatcher.getInstance().fireDataChangedListener(
                newSpecTc, DataState.Added, UpdateState.all);
            return extractionRet;
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        }
        return new ExtractionReturn();
    }

    /**
     * @param newTcName
     *            the name of the new SpecTestCase
     * @param ownerNode
     *            the edited {@link INodePO} from which to extract
     * @param modNodes
     *            nodes to move from the old tc to the new tc
     * @return result of extraction
     * @throws PMException
     *             in case of persistence error
     * @throws ProjectDeletedException
     *             in case of deleted project
     */
    private ExtractionReturn persistExtraction(
        final INodePO ownerNode, final String newTcName, 
        final List<IParamNodePO> modNodes) 
        throws PMException, ProjectDeletedException {
        
        final ExtractionReturn extractionRet = new ExtractionReturn();
        final ParamNameBPDecorator mapper = new ParamNameBPDecorator(
            ParamNameBP.getInstance());
        final ITransactAction op = new ITransactAction() {
            public void run(EntityManager s) throws PMException, 
                ProjectDeletedException {               
                IPersistentObject obj = null;
                try {
                    Hibernator.instance().refreshPO(s, ownerNode, 
                        LockModeType.PESSIMISTIC_WRITE);
                    List<IParamNodePO> nodesToRef = 
                        new ArrayList<IParamNodePO>();
                    getModNodesFromCurrentSession(s, nodesToRef);
                    final IExecTestCasePO newExecTc = TreeOpsBP.extractTestCase(
                        newTcName, ownerNode, nodesToRef, s, mapper);
                    extractionRet.setExecTestCasePO(newExecTc);
                    obj = newExecTc;
                    s.persist(newExecTc);
                    final ISpecTestCasePO newSpecTc = newExecTc
                        .getSpecTestCase();
                    registerParamNamesToSave(newSpecTc, mapper);
                    obj = newSpecTc;
                    s.merge(newSpecTc);
                    mapper.persist(s, 
                        GeneralStorage.getInstance().getProject().getId());
                } catch (TreeOpFailedException e) {
                    Utils.createMessageDialog(e, null, null);
                } catch (PersistenceException e) {
                    PersistenceManager.handleDBExceptionForMasterSession(
                        obj, e);
                }
            }

            /**
             * @param s session used for refactoring (master session)
             * @param nodesToRef nodes to refactor from current session
             */
            private void getModNodesFromCurrentSession(EntityManager s, 
                List<IParamNodePO> nodesToRef) {
                for (IParamNodePO node : modNodes) {
                    IParamNodePO object = (IParamNodePO)s.
                        find(HibernateUtil.getClass(node), node.getId());
                    if (object != null) {
                        nodesToRef.add(object);
                    }
                }
            }
            
        };
        final EntityManager s = GeneralStorage.getInstance().getMasterSession();
        new TransactionSupport(s).transact(op);
        mapper.updateStandardMapperAndCleanup(ownerNode.getParentProjectId());
        return extractionRet;
    }

    /**
     * @param newSpecTc new created specTestCase (after extraction)
     * @param mapper mapper to use for resolving of param names in this context
     */
    protected void registerParamNamesToSave(ISpecTestCasePO newSpecTc, 
        ParamNameBPDecorator mapper) {
        for (IParamDescriptionPO desc : newSpecTc.getParameterList()) {
            mapper.registerParamDescriptions((ITcParamDescriptionPO)desc);
        }
    }
}
