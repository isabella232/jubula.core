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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.TreeOpsBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.client.ui.rcp.actions.TransactionWrapper;
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

        editor.getEditorHelper().getClipboard().clearContents();
        final INodePO node = (INodePO) editor.getEditorHelper()
                .getEditSupport().getOriginal();
        if (node != null) {
            validateNode(node);
            IExecTestCasePO exec = performExtraction(newTestCaseName, node,
                    getSelection());
            try {
                editor.reOpenEditor(node);
                editor.getTreeViewer().setSelection(
                        new StructuredSelection(editor.getEntityManager().
                                find(exec.getClass(), exec.getId())));
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
    private IExecTestCasePO performExtraction(final String newTcName,
            final INodePO node, final IStructuredSelection selection) {

        final List<INodePO> modNodes = new ArrayList<INodePO>(
                selection.size());
        Iterator it = selection.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof INodePO) {
                modNodes.add((INodePO) next);
            }
        }
        if (modNodes.isEmpty()) {
            return null;
        }
        
        IExecTestCasePO exec = persistExtraction(node, newTcName, modNodes);
        DataEventDispatcher.getInstance().fireDataChangedListener(node,
                DataState.StructureModified, UpdateState.all);
        DataEventDispatcher.getInstance().fireDataChangedListener(
                exec, DataState.Added, UpdateState.all);
        ISpecTestCasePO newSpecTC = exec.getSpecTestCase();
        newSpecTC = GeneralStorage.getInstance().getMasterSession().find(
                newSpecTC.getClass(), newSpecTC.getId());
        DataEventDispatcher.getInstance().fireDataChangedListener(
                newSpecTC, DataState.Added, UpdateState.all);
        TestCaseBrowser tcb = MultipleTCBTracker.getInstance().getMainTCB();
        if (tcb != null) {
            tcb.getTreeViewer().setSelection(
                    new StructuredSelection(newSpecTC), true);
        }
        return exec;
    }

    /**
     * @param newTcName
     *            the name of the new SpecTestCase
     * @param ownerNode
     *            the edited {@link INodePO} from which to extract
     * @param modNodes
     *            nodes to move from the old tc to the new tc
     * @return result of extraction
     */
    private IExecTestCasePO persistExtraction(final INodePO ownerNode,
        final String newTcName, final List<INodePO> modNodes) {
        
        final ParamNameBPDecorator mapper = new ParamNameBPDecorator(
                ParamNameBP.getInstance());
        final IExecTestCasePO[] lol = new IExecTestCasePO[1];
        
        final List<IPersistentObject> toLock = new ArrayList<>(2);
        final IPersistentObject cont = GeneralStorage.getInstance().
                getProject().getSpecObjCont();
        toLock.add(cont);
        toLock.add(ownerNode);

        boolean succ = TransactionWrapper.executeOperation(new ITransaction() {

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToLock() {
                return toLock;
            }

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToRefresh() {
                return toLock;
            }

            /** {@inheritDoc} */
            public void run(EntityManager sess)
                    throws PMException {
                List<INodePO> nodesToRef = 
                        new ArrayList<INodePO>();
                getModNodesFromCurrentSession(sess, nodesToRef);
                lol[0] = TreeOpsBP
                        .extractTestCase(newTcName, ownerNode, nodesToRef,
                                sess, mapper);
                final ISpecTestCasePO newSpecTc = lol[0].getSpecTestCase();
                registerParamNamesToSave(newSpecTc, mapper);
                mapper.persist(sess, GeneralStorage.getInstance().getProject()
                        .getId());
                NativeSQLUtils.addNodeAFFECTS(sess, lol[0].getSpecTestCase(),
                        cont);
            }
            
            /**
             * Loads the moved nodes to the session
             * @param s session used for refactoring
             * @param nodesToRef nodes to refactor from current session
             */
            private void getModNodesFromCurrentSession(EntityManager s,
                    List<INodePO> nodesToRef) {
                if (modNodes.isEmpty()) {
                    return;
                }
                INodePO par = modNodes.get(0).getParentNode();
                par = s.find(par.getClass(), par.getId());
                // we have to set the children's parent node...
                par.getUnmodifiableNodeList();
                for (INodePO node : modNodes) {
                    INodePO object = s.find(node.getClass(), node.getId());
                    if (object != null) {
                        nodesToRef.add(object);
                    }
                }
            }
            
        });
        
        if (!succ) {
            return null;
        }
        
        mapper.updateStandardMapperAndCleanup(ownerNode.getParentProjectId());
        return lol[0];
    }
}
