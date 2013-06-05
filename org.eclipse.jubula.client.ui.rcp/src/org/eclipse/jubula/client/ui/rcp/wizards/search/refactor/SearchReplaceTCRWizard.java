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
package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.pages.ChooseTestCasePage;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.ComponentNameMappingWizardPage;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.ParameterNamesMatchingWizardPage;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.ReplaceExecTestCaseData;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.messagehandling.MessageInfo;
import org.eclipse.ui.PlatformUI;

/**
 * This wizard is used for replacing Test Cases from items in a search result
 * 
 * @author BREDEX GmbH
 * 
 */
public class SearchReplaceTCRWizard extends Wizard {
    /** ID for the "Choose" page */
    private static final String CHOOSE_PAGE_ID = "ReplaceTCRWizard.ChoosePageId"; //$NON-NLS-1$
    /** ID for the "Component Mapping" page */
    private static final String COMPONENT_MAPPING_PAGE_ID = "ReplaceTCRWizard.ComponentMappingPageId"; //$NON-NLS-1$
    /** ID for the "Parameter Matching" page */
    private static final String PARAMETER_MATCHING_PAGE_ID = "ReplaceTCRWizard.ParameterMatchingPageId"; //$NON-NLS-1$

    /**
     * Operation for replacing the test cases
     * 
     * @author BREDEX GmbH
     */
    private class ReplaceTestCaseOperation implements IRunnableWithProgress {

        /** CompNamesBP to get AllCompNamePairs from an execTC*/
        private CompNamesBP m_compNamesBP = new CompNamesBP();
        
        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {
            monitor.beginTask(Messages.ReplaceTestCasesActionDialog,
                    IProgressMonitor.UNKNOWN);
            EntityManager session = GeneralStorage.getInstance()
                    .getMasterSession();
            try {
                Persistor.instance().lockPOSet(session,
                        m_replaceExecTestCaseData.getOldExecTestCases());
                Persistor.instance().lockPO(session,
                        m_replaceExecTestCaseData.getNewSpecTestCase());
                List<MultipleNodePM.AbstractCmdHandle> commands = 
                        new ArrayList<MultipleNodePM.AbstractCmdHandle>();
                for (IExecTestCasePO exec: m_replaceExecTestCaseData
                        .getOldExecTestCases()) {
                    INodePO parent = exec.getParentNode();
                    int index = parent.indexOf(exec);
                    
                    IExecTestCasePO newExec;
                    if (IEventExecTestCasePO.class.isAssignableFrom(
                            exec.getClass())) {
                        IEventExecTestCasePO newEventExec = NodeMaker
                            .createEventExecTestCasePO(
                                m_replaceExecTestCaseData.getNewSpecTestCase(),
                                parent);
                        IEventExecTestCasePO oldEventExec = 
                                (IEventExecTestCasePO) exec;
                        newEventExec.setEventType(oldEventExec.getEventType());
                        newEventExec.setReentryProp(oldEventExec
                                .getReentryProp());
                        newEventExec
                                .setMaxRetries(oldEventExec.getMaxRetries());
                        newExec = newEventExec;
                    } else {
                        newExec = NodeMaker.createExecTestCasePO(
                                m_replaceExecTestCaseData.getNewSpecTestCase());
                    }

                    newExec.setComment(exec.getComment());
                    newExec.setActive(exec.isActive());
                    newExec.setParentProjectId(exec.getParentProjectId());
                    if (exec.getSpecTestCase().getName() != exec.getName()) {
                        newExec.setName(exec.getName());
                    }
                    addNewCompNamePairs(exec, newExec);
                    
                    commands.add(new MultipleNodePM.DeleteExecTCHandle(exec));
                    commands.add(new MultipleNodePM.AddExecTCHandle(parent,
                            newExec, index));
                }
                MessageInfo errorMessageInfo = MultipleNodePM.getInstance()
                        .executeCommands(commands, session);
                
                // Since a lot of changes are done fire the project is "reloaded"
                DataEventDispatcher.getInstance().fireProjectLoadedListener(
                        monitor);
                if ((errorMessageInfo != null)) {
                    ErrorHandlingUtil.createMessageDialog(
                            errorMessageInfo.getMessageId(),
                            errorMessageInfo.getParams(), null);
                }
            } catch (JBException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            } finally {
                LockManager.instance().unlockPOs(session);
            }
            monitor.done();
        }

        /**
         * Looks in the<code>m_matchedCompNameGuidMap</code> if there is a
         * matching for the new ExecTestCase and add new CompNamePairs if
         * necessary
         * 
         * @param oldExec
         *            the old ExecTestCase
         * @param newExec
         *            the newly created ExecTestCase
         * @return the <code>newExec</code>
         */
        private IExecTestCasePO addNewCompNamePairs(IExecTestCasePO oldExec,
                IExecTestCasePO newExec) {
            // Get all new compNamePairs to get the component names
            Collection<ICompNamesPairPO> compNamePairs = 
                    m_compNamesBP.getAllCompNamesPairs(newExec);
            for (ICompNamesPairPO newPseudoPairs : compNamePairs) {
                if (m_matchedCompNameGuidMap.containsKey(newPseudoPairs
                        .getFirstName())) {
                    String oldCompName = m_matchedCompNameGuidMap
                            .get(newPseudoPairs.getFirstName());
                    Collection<ICompNamesPairPO> oldCompNamePairs = 
                            m_compNamesBP.getAllCompNamesPairs(oldExec);
                    
                    // Get the corresponding new Name of the old CompNamePair
                    for (Iterator iterator = oldCompNamePairs.iterator();
                            iterator.hasNext();) {
                        ICompNamesPairPO oldPair = (ICompNamesPairPO) iterator
                                .next();
                        if (oldPair.getFirstName().equals(oldCompName)) {
                            ICompNamesPairPO newPair = PoMaker
                                    .createCompNamesPairPO(
                                            newPseudoPairs.getFirstName(),
                                            oldPair.getSecondName(),
                                            oldPair.getType());
                            newPair.setPropagated(oldPair.isPropagated());
                            newExec.addCompNamesPair(newPair);
                            break;
                        }

                    }
                }
            }
            return newExec;
        }

    }

    /**
     * <code>m_setOfExecsToReplace</code>
     */
    private final ReplaceExecTestCaseData m_replaceExecTestCaseData;

    /**
     * Map for matching guid's for component names 
     */
    private Map<String, String> m_matchedCompNameGuidMap;
    
    /**
     * <code>m_choosePage</code>
     */
    private ChooseTestCasePage m_choosePage;

    /**
     * Component Names matching page ID
     */
    private ComponentNameMappingWizardPage m_componentNamesPage;

    /**
     * Parameter Matching page ID
     */
    private ParameterNamesMatchingWizardPage m_parameterMatchingPage;

    /**
     * Constructor for the wizard page
     * 
     * @param execsToReplace
     *            set of ExecTC in which the SpecTC and other information should
     *            be changed
     */
    public SearchReplaceTCRWizard(Set<IExecTestCasePO> execsToReplace) {
        m_replaceExecTestCaseData = new ReplaceExecTestCaseData(execsToReplace);
        setWindowTitle(Messages.ReplaceTCRWizardTitle);
    }

    /** {@inheritDoc} */
    public boolean performFinish() {
        // This is needed if Finish was pressed on the first page
        m_replaceExecTestCaseData.setNewSpecTestCase(
                m_choosePage.getChoosenTestCase());
        m_matchedCompNameGuidMap = m_componentNamesPage.getCompMatching();
        try {
            PlatformUI.getWorkbench().getProgressService()
                    .run(true, false, new ReplaceTestCaseOperation());
        } catch (InvocationTargetException e) {
            // Already handled;
        } catch (InterruptedException e) {
            // Already handled
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        ISpecTestCasePO spec = m_replaceExecTestCaseData
                .getOldSpecTestCase();
        m_choosePage = new ChooseTestCasePage(spec, CHOOSE_PAGE_ID);
        m_choosePage.setDescription(
                Messages.replaceTCRWizard_choosePage_multi_description);
        m_choosePage.setContextHelpId(ContextHelpIds
                .SEARCH_REFACTOR_REPLACE_EXECUTION_TEST_CASE_WIZARD);
        m_componentNamesPage = new ComponentNameMappingWizardPage(
                COMPONENT_MAPPING_PAGE_ID,
                m_replaceExecTestCaseData.getOldExecTestCases());
        m_componentNamesPage.setDescription(Messages
                .ReplaceTCRWizard_matchComponentNames_multi_description);
        m_parameterMatchingPage = new ParameterNamesMatchingWizardPage(
                PARAMETER_MATCHING_PAGE_ID, m_replaceExecTestCaseData);
        addPage(m_choosePage);
        addPage(m_componentNamesPage);
        addPage(m_parameterMatchingPage);
        
    }

    /**
     * {@inheritDoc}
     */
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof ChooseTestCasePage) {
            m_replaceExecTestCaseData.setNewSpecTestCase(
                    m_choosePage.getChoosenTestCase());
            // FIXME RB: wizard pages should update data it self
            m_componentNamesPage.setNewSpec(
                    m_replaceExecTestCaseData.getNewSpecTestCase());
        }
        IWizardPage nextPage = super.getNextPage(page);
        return nextPage;
    }

}
