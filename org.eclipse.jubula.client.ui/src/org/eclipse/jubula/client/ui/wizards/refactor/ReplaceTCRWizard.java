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
package org.eclipse.jubula.client.ui.wizards.refactor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.actions.AbstractNewTestCaseAction;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.handlers.delete.DeleteTreeItemHandlerTCEditor;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.wizards.refactor.pages.AdditionalInformationPage;
import org.eclipse.jubula.client.ui.wizards.refactor.pages.ChooseTestCasePage;
import org.eclipse.jubula.client.ui.wizards.refactor.pages.MatchComponentNamesPage;
import org.eclipse.jubula.client.ui.wizards.refactor.pages.MatchParameterPage;

/**
 * @author Markus Tiede
 * @created Jul 25, 2011
 */
public class ReplaceTCRWizard extends Wizard {
    /** ID for the "Choose" page */
    private static final String CHOOSE_PAGE_ID = "ReplaceTCRWizard.ChoosePageId"; //$NON-NLS-1$

    /** ID for the "MATCH_COMP_NAMES" page */
    private static final String MATCH_COMP_NAMES_PAGE_ID = "ReplaceTCRWizard.MatchCompNamesPageId"; //$NON-NLS-1$

    /** ID for the "MATCH_PARAMETER" page */
    private static final String MATCH_PARAMETER_PAGE_ID = "ReplaceTCRWizard.MatchParameterPageId"; //$NON-NLS-1$

    /** ID for the "ADD_INFORMATION" page */
    private static final String ADD_INFORMATION_PAGE_ID = "ReplaceTCRWizard.AdditionalInformationPageId"; //$NON-NLS-1$

    /**
     * <code>m_editor</code>
     */
    private final AbstractJBEditor m_editor;

    /**
     * <code>m_listOfExecsToReplace</code>
     */
    private final List<IExecTestCasePO> m_listOfExecsToReplace;

    /**
     * <code>m_parentTC</code>
     */
    private INodePO m_parentTC;

    /**
     * <code>m_choosePage</code>
     */
    private ChooseTestCasePage m_choosePage;

    /**
     * <code>m_matchCompNamePage</code>
     */
    private MatchComponentNamesPage m_matchCompNamePage;

    /**
     * <code>m_matchParamPage</code>
     */
    private MatchParameterPage m_matchParamPage;

    /**
     * <code>m_addInfoPage</code>
     */
    private AdditionalInformationPage m_addInfoPage;

    /**
     * <code>m_newExec</code>
     */
    private IExecTestCasePO m_newExec;

    /**
     * Constructor
     * 
     * @param editor
     *            the editor
     * @param listOfExecsToReplace
     *            the list of execs to replace by this refactor wizard
     */
    public ReplaceTCRWizard(AbstractJBEditor editor,
            List<IExecTestCasePO> listOfExecsToReplace) {
        setNeedsProgressMonitor(true);
        setWindowTitle(Messages.ReplaceTCRWizardTitle);
        m_editor = editor;
        m_listOfExecsToReplace = listOfExecsToReplace;
        m_parentTC = (INodePO)editor.getEditorHelper()
            .getEditSupport().getWorkVersion();
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        
        m_choosePage = 
            new ChooseTestCasePage(m_parentTC, CHOOSE_PAGE_ID);
        m_matchCompNamePage = 
            new MatchComponentNamesPage(MATCH_COMP_NAMES_PAGE_ID, m_editor, 
                    m_listOfExecsToReplace); 
        m_matchParamPage = 
            new MatchParameterPage(MATCH_PARAMETER_PAGE_ID);
        m_addInfoPage = 
            new AdditionalInformationPage(ADD_INFORMATION_PAGE_ID);
        
        
        addPage(m_choosePage);
        addPage(m_matchCompNamePage);
        addPage(m_matchParamPage);
        addPage(m_addInfoPage);
    }
    
    /**
     * {@inheritDoc}
     */
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof ChooseTestCasePage) {
//            TestCaseBP.handleFirstReference(m_editor.getEditorHelper()
//                    .getEditSupport(),
//                    m_choosePage.getChoosenTestCase(), false);
            ISpecTestCasePO specTC = m_choosePage.getChoosenTestCase();
            m_newExec = NodeMaker.createExecTestCasePO(specTC);
            if (!anyCompNamesToMatch(m_listOfExecsToReplace, m_newExec)) {
                return m_matchParamPage;
            }
            m_matchCompNamePage.setSelectedExecNode(m_newExec);
        }
        return super.getNextPage(page);
    }

    /**
     * @param listOfExecsToReplace
     *            the list of execs to replace
     * @param newExec
     *            the new exec
     * @return true if comp name match page has to be shown
     */
    private boolean anyCompNamesToMatch(
        List<IExecTestCasePO> listOfExecsToReplace, IExecTestCasePO newExec) {
        List<IExecTestCasePO> execsToCheck = new ArrayList<IExecTestCasePO>();
        execsToCheck.addAll(listOfExecsToReplace);
        execsToCheck.add(newExec);
        for (IExecTestCasePO exec : execsToCheck) {
            if (exec.getCompNamesPairs().size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        IExecTestCasePO placeToInsert = m_listOfExecsToReplace.get(0);
        ISpecTestCasePO specTcToInsert = m_choosePage.getChoosenTestCase();
        try {
            Integer index = AbstractNewTestCaseAction.getPositionToInsert(
                    m_parentTC, placeToInsert);
            JBEditorHelper eh = m_editor.getEditorHelper();
            IExecTestCasePO replacementTCReference = TestCaseBP
                    .addReferencedTestCase(m_parentTC, m_newExec, index);
            InteractionEventDispatcher.getDefault()
                    .fireProgammableSelectionEvent(
                            new StructuredSelection(specTcToInsert));
            eh.getEditSupport().lockWorkVersion();
            eh.setDirty(true);
            m_editor.setSelection(new StructuredSelection(
                    replacementTCReference));
        } catch (PMException e) {
            NodeEditorInput inp = (NodeEditorInput)m_editor
                    .getAdapter(NodeEditorInput.class);
            INodePO inpNode = inp.getNode();
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
            try {
                m_editor.reOpenEditor(inpNode);
            } catch (PMException e1) {
                PMExceptionHandler.handlePMExceptionForEditor(e1, m_editor);
            }
        }
        DeleteTreeItemHandlerTCEditor.deleteNodesFromEditor(
                m_listOfExecsToReplace, m_editor);
        return true;
    }

    /**
     * @return the replacement
     */
    public IExecTestCasePO getReplacement() {
        return NodeMaker.createExecTestCasePO(
                m_choosePage.getChoosenTestCase());
    }
}
