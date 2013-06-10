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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.SearchResultPage;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.param.ChangeCtdsColumnUsageWizard;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.param.ExistingAndNewParameterData;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.param.TestCasesValidator;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBookView;

/**
 * Handler for context menu in a search result list to change CTDS column usage.
 * @author BREDEX GmbH
 */
public class SearchChangeCtdsColumnUsageHandler
        extends AbstractSelectionBasedHandler {

    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event) {
        SearchResultPage page = getSearchResultPage(event);
        if (page == null) {
            return null;
        }
        // create list of selected Test Cases
        @SuppressWarnings("unchecked")
        List<SearchResultElement<Long>> oldSelection =
                (List<SearchResultElement<Long>>) getSelection().toList();
        Set<ITestCasePO> testCases = new HashSet<ITestCasePO>();
        EntityManager session = GeneralStorage.getInstance().getMasterSession();
        for (SearchResultElement<Long> searchResult: oldSelection) {
            INodePO nodePO = session.find(NodeMaker.getNodePOClass(),
                    searchResult.getData());
            if (nodePO instanceof ITestCasePO) {
                testCases.add((ITestCasePO) nodePO);
            }
        }
        // validate the selected Test Cases
        TestCasesValidator validator = new TestCasesValidator(testCases);
        if (!validator.isReferencedDataCubeOk()) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_ALL_TCS_MUST_USE_SAME_CTDS);
            return null;
        }
        if (!validator.areAllTestCasesOk()) {
            if (MessageDialog.openConfirm(null,
                    Messages.ChangeCtdsColumnUsageActionDialog,
                    Messages.ChangeCtdsColumnUsageQuestionDeselect)) {
                selectValidTestCases(page,
                        oldSelection, validator.getValidTestCases());
            }
            return null;
        }
        // create for each parameter name a corresponding set of execution Test Cases
        ExistingAndNewParameterData paramData =
                new ExistingAndNewParameterData(validator.getValidTestCases());
        if (paramData.getAllParamDescriptions().length == 0) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_TCS_HAVE_NO_CHANGEABLE_PARAMETER_NAME);
            return null;
        }
        if (Plugin.getDefault().saveAllDirtyEditors()) {
            showWizardDialog(paramData);
        }
        return null;
    }

    /**
     * @param event The event.
     * @return The search result page from the given event.
     */
    private static SearchResultPage getSearchResultPage(ExecutionEvent event) {
        SearchResultPage resultPage = null;
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof PageBookView) {
            PageBookView pageView = (PageBookView) activePart;
            IPage currentPage = pageView.getCurrentPage();
            if (currentPage instanceof SearchResultPage) {
                resultPage = (SearchResultPage) currentPage;
            }
        }
        return resultPage;
    }

    /**
     * Select the Test Cases, which can be used for changing CTDS column usage.
     * @param page The search result page.
     * @param oldSelection The old selection.
     * @param testCases The list of valid Test Cases.
     */
    private static void selectValidTestCases(SearchResultPage page,
            List<SearchResultElement<Long>> oldSelection,
            List<ITestCasePO> testCases) {
        // create a new list for selection
        List<SearchResultElement<Long>> newSelection =
                new ArrayList<SearchResultElement<Long>>();
        for (SearchResultElement<Long> resultElement: oldSelection) {
            if (testCasesContainId(testCases, resultElement.getData())) {
                newSelection.add(resultElement);
            }
        }
        // set the new list for selection
        page.setSelection(new StructuredSelection(newSelection));
    }

    /**
     * @param testCases The list of valid Test Cases
     * @param id The persistence (JPA / EclipseLink) ID.
     * @return True, if the given list of Test Cases contains a Test Case
     *         with the given ID.
     */
    private static boolean testCasesContainId(
            List<ITestCasePO> testCases, Long id) {
        for (ITestCasePO tc: testCases) {
            if (id.equals(tc.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Show the dialog for changing CTDS column usage.
     * @param paramData The parameter names of the selected Test Cases.
     */
    private void showWizardDialog(
            ExistingAndNewParameterData paramData) {
        WizardDialog dialog;
        dialog = new WizardDialog(getActiveShell(),
                new ChangeCtdsColumnUsageWizard(paramData));
        dialog.setHelpAvailable(true); // show ? icon on left bottom
        dialog.open();
    }

}
