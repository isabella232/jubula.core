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
package org.eclipse.jubula.client.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 18.05.2005
 */
public class ProjectInfoWizardPage extends WizardPage {
    
    /** the label of the infoText */
    private Label m_infoText;

    /**
     * @param pageName The name of this wizard page.
     */
    public ProjectInfoWizardPage(String pageName) {
        super(pageName);
        setPageComplete(true);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        ScrolledComposite scroll = new ScrolledComposite(parent, 
            SWT.V_SCROLL | SWT.H_SCROLL);
        Composite composite = new Composite(scroll, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.marginHeight = Layout.SMALL_MARGIN_HEIGHT;
        compositeLayout.marginWidth = Layout.SMALL_MARGIN_WIDTH;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = false;
        composite.setLayoutData(compositeData);
        createInfoText(composite);
        scroll.setContent(composite);
        scroll.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        setControl(scroll);
    }
    
    /**
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(ContextHelpIds
            .PROJECT_PROPERTY_PAGE);
    }

    /**
     * Creates the info text of this wizard page.
     * @param composite The parent composite.
     */
    private void createInfoText(Composite composite) {
        m_infoText = new Label(composite, SWT.NONE);
        setInfoText();
    }
    /**
     * Sets the info text.
     */
    private void setInfoText() {
        m_infoText.setText(Messages.ProjectInfoWizardPageInfoText);
        m_infoText.redraw();
    }
}
