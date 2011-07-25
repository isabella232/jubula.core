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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.model.INodePO;
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
     * <code>m_editorNode</code>
     */
    private final INodePO m_editorNode;

    /**
     * Constructor
     * @param editorNode the editor node
     */
    public ReplaceTCRWizard(INodePO editorNode) {
        setNeedsProgressMonitor(true);
        setWindowTitle(Messages.ReplaceTCRWizardTitle);
        m_editorNode = editorNode;
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        addPage(new ChooseTestCasePage(m_editorNode, CHOOSE_PAGE_ID));
        addPage(new MatchComponentNamesPage(MATCH_COMP_NAMES_PAGE_ID));
        addPage(new MatchParameterPage(MATCH_PARAMETER_PAGE_ID));
        addPage(new AdditionalInformationPage(ADD_INFORMATION_PAGE_ID));
    }

    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        return false;
    }
}
