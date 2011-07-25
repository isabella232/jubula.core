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
package org.eclipse.jubula.client.ui.wizards.refactor.pages;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.widgets.TestCaseTreeComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Markus Tiede
 * @created Jul 25, 2011
 */
public class ChooseTestCasePage extends WizardPage {
    /**
     * <code>m_parentTestCase</code>
     */
    private INodePO m_parentTestCase;
    /**
     * @param pageId
     *            the page id
     * @param parentTestCase
     *            the parent test case
     */
    public ChooseTestCasePage(INodePO parentTestCase, String pageId) {
        super(pageId, Messages.ReplaceTCRWizard_choosePage_title, null);
        m_parentTestCase = parentTestCase;
        setPageComplete(false);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        final TestCaseTreeComposite tctc = new TestCaseTreeComposite(parent,
                SWT.SINGLE, m_parentTestCase);
        tctc.getTreeViewer().addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        setPageComplete(tctc.hasValidSelection());
                    }
                });
        setControl(tctc);
    }
    
    /**
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(
            ContextHelpIds.REFACTOR_REPLACE_CHOOSE_TEST_CASE_WIZARD_PAGE);
    }
}
