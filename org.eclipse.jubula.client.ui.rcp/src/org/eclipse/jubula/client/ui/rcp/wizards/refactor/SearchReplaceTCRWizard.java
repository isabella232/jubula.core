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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor;

import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.pages.ChooseTestCasePage;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public class SearchReplaceTCRWizard extends Wizard {
    /** ID for the "Choose" page */
    private static final String CHOOSE_PAGE_ID = "ReplaceTCRWizard.ChoosePageId"; //$NON-NLS-1$
    
    /**
     * <code>m_listOfExecsToReplace</code>
     */
    private final Set<IExecTestCasePO> m_listOfExecsToReplace;
    /**
     * <code>m_choosePage</code>
     */
    private ISpecTestCasePO m_specOfAllExec;
    /**
     * <code>m_choosePage</code>
     */
    private ChooseTestCasePage m_choosePage;

    /**
     * <code>m_newExec</code>
     */
    private IExecTestCasePO m_newExec;
    
    /**
     * Constructor for the wizard page
     * 
     * @param specOfAllExec
     *            the spec testcase from all the execs
     * @param execsToReplace
     *            set of execs which should be replace with a new exec
     */
    public SearchReplaceTCRWizard(ISpecTestCasePO specOfAllExec,
            Set<IExecTestCasePO> execsToReplace) {
        m_specOfAllExec = specOfAllExec;
        m_listOfExecsToReplace = execsToReplace;



    }
    
    /** {@inheritDoc}    */
    public boolean performFinish() {
        EntityManager session = GeneralStorage.getInstance().getMasterSession();
        try {
            Persistor.instance().lockPOSet(session, m_listOfExecsToReplace);
            Persistor.instance().lockPO(session, m_specOfAllExec);
            
            
        } catch (PMAlreadyLockedException e) {
            PMExceptionHandler.handlePMAlreadyLockedException(e, null);
            e.printStackTrace();
        } catch (PMDirtyVersionException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
            e.printStackTrace();
        } catch (PMObjectDeletedException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
            e.printStackTrace();
        }
        return false;
    }
    /** {@inheritDoc}    */
    public boolean performCancel() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        
        m_choosePage = 
            new ChooseTestCasePage(m_specOfAllExec, CHOOSE_PAGE_ID);
        addPage(m_choosePage);

    }
    
    /**
     * {@inheritDoc}
     */
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof ChooseTestCasePage) {
            ISpecTestCasePO specTC = m_choosePage.getChoosenTestCase();
            m_newExec = NodeMaker.createExecTestCasePO(specTC);
        }
        return super.getNextPage(page);
    }
}
