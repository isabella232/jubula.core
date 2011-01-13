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
package org.eclipse.jubula.client.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * This class creates a dialog, where you can choose which zip files have to be
 * attached to the support request mail.
 *
 * @author BREDEX GmbH
 * @created 18.04.2008
 */
public final class SupportRequestMailDialog extends TitleAreaDialog {
    /** number of columns in area parent */
    private static final int NUM_COLUMNS_AREA_PARENT = 1;
    /** number of columns in area */
    private static final int NUM_COLUMNS_AREA = 2;
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;
    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;
    
    /** the 'add configuration information' checkbox */
    private Button m_addConfigInfoCheckbox;
    /** the 'add aut starter log' label */
    private Label m_addAutStarterLogLabel;
    /** the 'add aut starter log' checkbox */
    private Button m_addAutStarterLogCheckbox;
    /** the 'add client log' label */
    private Label m_addClientLogLabel;
    /** the 'add client log' checkbox */
    private Button m_addClientLogCheckbox;
    /** the 'add projects' label */
    private Label m_addProjectsLabel;
    /** the 'add projects' checkbox */
    private Button m_addProjectsCheckbox;
    
    /** Is 'add aut starter log' checkbox to enable? */
    private final boolean m_enableAutStarterLogCheckbox;
    /** Is 'add client log' checkbox to enable? */
    private final boolean m_enableClientLogCheckbox;
    /** Is 'add projects' checkbox to enable? */
    private final boolean m_enableProjectsCheckbox;
    
    // ---------- default selection for checkboxes ------------
    /** Is 'add aut starter log' checkbox selected? */
    private boolean m_addAutStarterLogCheckboxSelected = true;
    /** Is 'add client log' checkbox selected? */
    private boolean m_addClientLogCheckboxSelected = true;
    /** Is 'add projects' checkbox selected? */
    private boolean m_addProjectsCheckboxSelected = false;
    /** Is 'add configuration information' checkbox selected? */
    private boolean m_addConfigInfoCheckboxSelected = true;
    
    /**
     * @param parentShell The parent shell
     * @param enableAutStarterLogCheckbox Indicates whether aut starter log
     *                                    checkbox has to be enabled
     * @param enableClientLogCheckbox Indicates whether client log checkbox 
     *                                has to be enabled
     * @param enableProjectsCheckbox Indicates whether projects checkbox 
     *                               has to be enabled
     */
    public SupportRequestMailDialog(Shell parentShell,
            boolean enableAutStarterLogCheckbox,
            boolean enableClientLogCheckbox,
            boolean enableProjectsCheckbox) {
        
        super(parentShell);
        
        m_enableAutStarterLogCheckbox = enableAutStarterLogCheckbox;
        m_enableClientLogCheckbox = enableClientLogCheckbox;
        m_enableProjectsCheckbox = enableProjectsCheckbox;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(I18n.getString("SupportRequestMailDialog.title")); //$NON-NLS-1$
        setMessage(I18n.getString("SupportRequestMailDialog.message")); //$NON-NLS-1$
        getShell().setText(I18n.getString("SupportRequestMailDialog.shellTitle")); //$NON-NLS-1$
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_AREA_PARENT;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        Plugin.createSeparator(parent);
        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_AREA;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        area.setLayoutData(gridData);
        createFields(area);
        Plugin.createSeparator(parent);
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds
                .SUPPORT_REQUEST_DIALOG);
        setHelpAvailable(true);
        return area;
    }
    
    /**
     * @param area The composite.
     * creates the editor widgets
     */
    private void createFields(Composite area) {
        // for 'add configuration information' 
        new Label(area, SWT.NONE).setText(
            I18n.getString("SupportRequestMailDialog.addConfigInfoLabel")); //$NON-NLS-1$
        m_addConfigInfoCheckbox = new Button(area, SWT.CHECK);
        m_addConfigInfoCheckbox.setSelection(m_addConfigInfoCheckboxSelected);

        // for 'add aut starter log' 
        m_addAutStarterLogLabel = new Label(area, SWT.NONE);
        m_addAutStarterLogLabel.setText(
            I18n.getString("SupportRequestMailDialog.addAutStarterLogLabel")); //$NON-NLS-1$
        m_addAutStarterLogCheckbox = new Button(area, SWT.CHECK);
        if (m_enableAutStarterLogCheckbox) { 
            m_addAutStarterLogCheckbox
                .setSelection(m_addAutStarterLogCheckboxSelected);
        } else {
            m_addAutStarterLogLabel.setEnabled(false);
            m_addAutStarterLogCheckbox.setEnabled(false);
        }
        
        // for 'add client log' 
        m_addClientLogLabel = new Label(area, SWT.NONE);
        m_addClientLogLabel.setText(
            I18n.getString("SupportRequestMailDialog.addClientLogLabel")); //$NON-NLS-1$
        m_addClientLogCheckbox = new Button(area, SWT.CHECK);
        if (m_enableClientLogCheckbox) { 
            m_addClientLogCheckbox.setSelection(m_addClientLogCheckboxSelected);
        } else {
            m_addClientLogLabel.setEnabled(false);
            m_addClientLogCheckbox.setEnabled(false);
        }
        
        // for 'add projects' 
        m_addProjectsLabel = new Label(area, SWT.NONE);
        m_addProjectsLabel.setText(
            I18n.getString("SupportRequestMailDialog.addProjectsLabel")); //$NON-NLS-1$
        m_addProjectsCheckbox = new Button(area, SWT.CHECK);
        if (m_enableProjectsCheckbox) { 
            m_addProjectsCheckbox.setSelection(m_addProjectsCheckboxSelected);
        } else {
            m_addProjectsLabel.setEnabled(false);
            m_addProjectsCheckbox.setEnabled(false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void okPressed() {
        m_addConfigInfoCheckboxSelected = 
            m_addConfigInfoCheckbox.getSelection();
        m_addAutStarterLogCheckboxSelected = 
            m_addAutStarterLogCheckbox.getSelection();
        m_addClientLogCheckboxSelected = m_addClientLogCheckbox.getSelection();
        m_addProjectsCheckboxSelected = m_addProjectsCheckbox.getSelection();
        
        super.okPressed();
    }

    /**
     * @return A boolean indicating whether the 'add aut starter log' checkbox
     *         was selected while leaving the dialog by pressing 'Ok'.
     */
    public boolean isAutStarterLogCheckboxSelected() {
        return m_addAutStarterLogCheckboxSelected;
    }

    /**
     * @return A boolean indicating whether the 'add client log' checkbox
     *         was selected while leaving the dialog by pressing 'Ok'.
     */
    public boolean isClientLogCheckboxSelected() {
        return m_addClientLogCheckboxSelected;
    }

    /**
     * @return A boolean indicating whether the 'add project' checkbox
     *         was selected while leaving the dialog by pressing 'Ok'.
     */
    public boolean isProjectsCheckboxSelected() {
        return m_addProjectsCheckboxSelected;
    }

    /**
     * @return A boolean indicating whether the 'configuration information' checkbox
     *         was selected while leaving the dialog by pressing 'Ok'.
     */
    public boolean isConfigInfoCheckboxSelected() {
        return m_addConfigInfoCheckboxSelected;
    }
}
    

