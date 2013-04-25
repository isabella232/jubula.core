/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.SearchReplaceTCRWizard;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.DialogUtils.SizeType;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public class SearchReplaceTestCaseHandler extends
        AbstractSelectionBasedHandler {

    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event)
        throws ExecutionException {
        IStructuredSelection selectedObject = getSelection();
        List selectionList = selectedObject.toList();
        Set<IExecTestCasePO> execList = new LinkedHashSet<IExecTestCasePO>();
        EntityManager session = GeneralStorage.getInstance().getMasterSession();
        ISpecTestCasePO firstSpec = null;
        try {

            for (Iterator iterator = selectionList.iterator(); iterator
                    .hasNext();) {
                Object object = (Object) iterator.next();
                SearchResultElement<Long> searchResult = 
                        (SearchResultElement<Long>) object;
                INodePO nodePO = session.find(NodeMaker.getNodePOClass(),
                        searchResult.getData());
                IExecTestCasePO exec = (IExecTestCasePO) nodePO;
                execList.add(exec);
                ISpecTestCasePO spec = exec.getSpecTestCase();
                if (firstSpec == null) {
                    firstSpec = spec;
                }
                if (!firstSpec.equals(spec)) {
                    ErrorHandlingUtil
                        .createMessageDialog(MessageIDs.I_NOT_SAME_SPEC);
                    return null;
                }
            }
        } catch (Exception e) {
            ErrorHandlingUtil.createMessageDialog(MessageIDs.I_NOT_SAME_SPEC);
            return null;
        }
        if (Plugin.getDefault().anyDirtyStar()) {
            if (Plugin.getDefault().showSaveEditorDialog()) {
                showWizardDialog(execList, firstSpec);
            }
        }

        return null;
    }

    /**
     * 
     * @param execList Set of all exec testcases which should be replaced
     * @param firstSpec the spec testcase of the execs
     */
    private void showWizardDialog(Set<IExecTestCasePO> execList,
            ISpecTestCasePO firstSpec) {
        WizardDialog dialog;
        dialog = new WizardDialog(getActiveShell(), new SearchReplaceTCRWizard(
                firstSpec, execList)) {
            /** {@inheritDoc} */
            protected void configureShell(Shell newShell) {
                super.configureShell(newShell);
                DialogUtils.adjustShellSizeRelativeToClientSize(newShell, .6f,
                        .6f, SizeType.SIZE);
            }
        };
        dialog.setHelpAvailable(true);
        dialog.open();
    }

}
