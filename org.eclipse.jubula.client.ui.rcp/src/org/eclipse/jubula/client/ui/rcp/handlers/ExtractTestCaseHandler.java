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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.TreeOpsBP;
import org.eclipse.jubula.client.core.businessprocess.TreeOpsBP.TreeOpFailedException;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.TransactionSupport;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransactAction;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for extract Test Case Command
 * 
 * @author BREDEX GmbH
 * @created 27.04.2009
 */
public class ExtractTestCaseHandler extends AbstractRefactorHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        final AbstractTestCaseEditor editor = (AbstractTestCaseEditor) 
                HandlerUtil.getActivePart(event);
        String newTestCaseName = getNewTestCaseName(event);
        if (newTestCaseName == null) {
            return null;
        }
        
        final INodePO node = (INodePO) editor.getEditorHelper()
                .getEditSupport().getOriginal();
        if (node != null) {
            validateNode(node);
            final IStructuredSelection selection = getSelection();
            ExtractionReturn extractRet = performExtraction(
                    newTestCaseName, node, selection);
            if (extractRet.getErrorMessage() != null) {
                ErrorHandlingUtil.createMessageDialog(new JBException(
                        extractRet.getErrorMessage(),
                        MessageIDs.E_UNEXPECTED_EXCEPTION), null,
                        new String[] { extractRet.getErrorMessage() });
            }
            try {
                editor.reOpenEditor(node);
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForEditor(e, editor);
            }
        } else {
            ErrorHandlingUtil.createMessageDialog((new JBException(
                    Messages.EditorWillBeClosed, MessageIDs.E_DELETED_TC)),
                    null, null);
            try {
                GeneralStorage.getInstance().reloadMasterSession(
                        new NullProgressMonitor());
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleProjectDeletedException();
            }
        }

        return null;
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
         * @param execTestCasePO
         *            The execTestCasePO to set.
         */
        public void setExecTestCasePO(IExecTestCasePO execTestCasePO) {
            m_execTestCasePO = execTestCasePO;
        }

    }

    /**
     * @param node
     *            the {@link INodePO} on which the extraction is to be
     *            performed.
     */
    private void validateNode(INodePO node) {
        Assert.verify(node instanceof ISpecTestCasePO
                || node instanceof ITestSuitePO,
                Messages.ExtractTestCaseOperateISpecTestCasePO);
    }

    /**
     * performs the extraction
     * 
     * @param newTcName
     *            the name of the new SpecTestCase
     * @param node
     *            the edited {@link INodePO} from which to extract
     * @param selection
     *            the nodes to be extracted
     * @return an error message or null.
     */
    private ExtractionReturn performExtraction(final String newTcName,
            final INodePO node, final IStructuredSelection selection) {

        final List<IParamNodePO> modNodes = new ArrayList<IParamNodePO>(
                selection.size());
        Iterator it = selection.iterator();
        while (it.hasNext()) {
            modNodes.add((IParamNodePO) it.next());
        }
        try {
            ExtractionReturn extractionRet = persistExtraction(node, newTcName,
                    modNodes);
            DataEventDispatcher.getInstance().fireDataChangedListener(node,
                    DataState.StructureModified, UpdateState.all);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    extractionRet.getExecTestCasePO(), DataState.Added,
                    UpdateState.all);
            final ISpecTestCasePO newSpecTc = extractionRet.getExecTestCasePO()
                    .getSpecTestCase();
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    newSpecTc, DataState.Added, UpdateState.all);
            TestCaseBrowser tcb = MultipleTCBTracker.getInstance().getMainTCB();
            if (tcb != null) {
                tcb.getTreeViewer().setSelection(
                        new StructuredSelection(newSpecTc), true);
            }
            return extractionRet;
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
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
    private ExtractionReturn persistExtraction(final INodePO ownerNode,
        final String newTcName, final List<IParamNodePO> modNodes)
        throws PMException, ProjectDeletedException {

        final ExtractionReturn extractionRet = new ExtractionReturn();
        final ParamNameBPDecorator mapper = new ParamNameBPDecorator(
                ParamNameBP.getInstance());
        final ITransactAction op = new ITransactAction() {
            public void run(EntityManager s) throws PMException,
                    ProjectDeletedException {
                IPersistentObject obj = null;
                try {
                    Persistor.instance().refreshPO(s, ownerNode,
                            LockModeType.PESSIMISTIC_WRITE);
                    List<IParamNodePO> nodesToRef = 
                            new ArrayList<IParamNodePO>();
                    getModNodesFromCurrentSession(s, nodesToRef);
                    final IExecTestCasePO newExecTc = TreeOpsBP
                            .extractTestCase(newTcName, ownerNode, nodesToRef,
                                    s, mapper);
                    extractionRet.setExecTestCasePO(newExecTc);
                    obj = newExecTc;
                    s.persist(newExecTc);
                    final ISpecTestCasePO newSpecTc = newExecTc
                            .getSpecTestCase();
                    registerParamNamesToSave(newSpecTc, mapper);
                    obj = newSpecTc;
                    s.merge(newSpecTc);
                    mapper.persist(s, GeneralStorage.getInstance().getProject()
                            .getId());
                } catch (TreeOpFailedException e) {
                    ErrorHandlingUtil.createMessageDialog(e, null, null);
                } catch (PersistenceException e) {
                    PersistenceManager
                            .handleDBExceptionForMasterSession(obj, e);
                }
            }

            /**
             * @param s
             *            session used for refactoring (master session)
             * @param nodesToRef
             *            nodes to refactor from current session
             */
            private void getModNodesFromCurrentSession(EntityManager s,
                    List<IParamNodePO> nodesToRef) {
                for (IParamNodePO node : modNodes) {
                    IParamNodePO object = s.find(node.getClass(), node.getId());
                    if (object != null) {
                        nodesToRef.add(object);
                    }
                }
            }

        };
        new TransactionSupport().transact(op);
        mapper.updateStandardMapperAndCleanup(ownerNode.getParentProjectId());
        return extractionRet;
    }
}
