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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.widgets.ComponentNamesTableComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Markus Tiede
 * @created Jul 25, 2011
 */
public class MatchComponentNamesPage extends WizardPage {
    /**
     * <code>m_editor</code>
     */
    private final AbstractJBEditor m_editor;
    /**
     * <code>cntc</code>
     */
    private ComponentNamesTableComposite m_cntc;

    /**
     * @param pageName
     *            the page name
     * @param editor
     *            the current editor
     */
    public MatchComponentNamesPage(String pageName, AbstractJBEditor editor) {
        super(pageName, Messages.ReplaceTCRWizard_matchComponentNames_title,
                null);
        m_editor = editor;
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        m_cntc = new ComponentNamesTableComposite(parent, SWT.NONE);
        setControl(new ComponentNamesTableComposite(parent, SWT.NONE));
        m_cntc.setSelectedExecNodeOwner(m_editor);
        if (m_editor.getEditorInput() instanceof ITestSuitePO) {
            m_cntc.disablePropagation();
        }
    }

    /**
     * @param replacement the replacing exec node
     */
    public void setSelectedExecNode(IExecTestCasePO replacement) {
        m_cntc.setSelectedExecNode(replacement);
    }

    /**
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(
            ContextHelpIds.REFACTOR_REPLACE_MATCH_COMP_NAMES_WIZARD_PAGE);
    }
}
