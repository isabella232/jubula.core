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
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Markus Tiede
 * @created Jul 25, 2011
 */
public class MatchComponentNamesPage extends WizardPage {
    /**
     * @param pageName
     *            the page name
     */
    public MatchComponentNamesPage(String pageName) {
        super(pageName, 
                Messages.ReplaceTCRWizard_matchComponentNames_title, null);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        setControl(new Composite(parent, SWT.NONE));
    }
    
    /**
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(
            ContextHelpIds.REFACTOR_REPLACE_MATCH_COMP_NAMES_WIZARD_PAGE);
    }
}
