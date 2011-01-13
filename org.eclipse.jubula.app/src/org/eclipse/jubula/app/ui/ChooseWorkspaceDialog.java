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
package org.eclipse.jubula.app.ui;

/**
 * @author BREDEX GmbH
 * @created 23.03.2005
 */
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.app.core.WorkSpaceData;
import org.eclipse.jubula.app.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * A dialog that prompts for a directory to use as a workspace.
 * @author BREDEX GmbH
 * @created 18.12.2006
 */
public class ChooseWorkspaceDialog extends TitleAreaDialog {
    /** the workspace launch data */
    private WorkSpaceData m_launchData;
    /** the actual selected workspace */
    private String m_currentSelection;

    /**
     * Create a modal dialog on the arugment shell, using and updating the
     * argument data object.
     * @param launchData The workbench luanch data.
     * @param parentShell The parent shell.
     */
    public ChooseWorkspaceDialog(Shell parentShell, WorkSpaceData launchData) {
        super(parentShell);
        this.m_launchData = launchData;
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (above
     * the button bar).
     * <p>
     * The <code>Dialog</code> implementation of this framework method creates
     * and returns a new <code>Composite</code> with no margins and spacing.
     * </p>
     * 
     * @param parent The parent composite to contain the dialog area.
     * @return The dialog area control.
     */
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        setTitle(Messages.ChooseWorkspaceDialogDialogMessage);
        setMessage(Messages.ChooseWorkspaceDialogMessageArea);
        super.setShellStyle(SWT.ON_TOP | SWT.SYSTEM_MODAL);
        setShellStyle(SWT.ON_TOP | SWT.SYSTEM_MODAL);
        createWorkspaceBrowseRow(composite);
        return composite;
    }

    /**
     * Configures the given shell in preparation for opening this window in it.
     * <p>
     * The default implementation of this framework method sets the shell's
     * image and gives it a grid layout. Subclasses may extend or reimplement.
     * </p>
     * 
     * @param shell The shell.
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.ChooseWorkspaceDialogDialogText);
    }

    /**
     * Notifies that the ok button of this dialog has been pressed.
     * <p>
     * The <code>Dialog</code> implementation of this framework method sets
     * this dialog's return code to <code>Window.OK</code> and closes the
     * dialog. Subclasses may override.
     * </p>
     */
    protected void okPressed() {
        m_launchData.workspaceSelected(m_currentSelection);
        super.okPressed();
    }

    /**
     * Notifies that the cancel button of this dialog has been pressed.
     * <p>
     * The <code>Dialog</code> implementation of this framework method sets
     * this dialog's return code to <code>Window.CANCEL</code> and closes the
     * dialog. Subclasses may override if desired.
     * </p>
     */
    protected void cancelPressed() {
        m_currentSelection = null;
        m_launchData.workspaceSelected(m_currentSelection);
        super.cancelPressed();
    }
    
    /**
     * The main area of the dialog is just a row with the current selection
     * information and a drop-down of the most recently used workspaces.
     * @param parent The parent composite.
     */
    private void createWorkspaceBrowseRow(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants
            .VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants
            .HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants
            .VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(
            IDialogConstants.HORIZONTAL_SPACING);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));
        panel.setFont(parent.getFont());

        Label label = new Label(panel, SWT.NONE);
        label.setText(Messages.ChooseWorkspaceDialogWorkspace);

        final Combo text = new Combo(panel, SWT.BORDER | SWT.LEAD
            | SWT.DROP_DOWN);
        text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
            | GridData.FILL_HORIZONTAL));
        setInitialTextValues(text);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                m_currentSelection = text.getText();
                m_launchData.workspaceSelected(m_currentSelection);
            }
        });
        text.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                m_currentSelection = text.getText();
                m_launchData.workspaceSelected(m_currentSelection);
            }
        });

        Button browseButton = new Button(panel, SWT.PUSH);
        browseButton.setText(Messages.ChooseWorkspaceDialogBrowse);
        setButtonLayoutData(browseButton);
        GridData data = (GridData)browseButton.getLayoutData();
        data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        browseButton.setLayoutData(data);
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), 
                    SWT.ON_TOP | SWT.SYSTEM_MODAL);
                dialog.setText(Messages.ChooseWorkspaceDialogSelectDir);
                dialog.setMessage(Messages.ChooseWorkspaceDialogMessaageDir);
                dialog.setFilterPath(m_currentSelection);
                String dir = dialog.open();
                if (dir != null) {
                    text.setText(dir);
                    m_launchData.workspaceSelected(dir);
                }
            }
        });
    }

    /**
     * @param text The comboBox.
     */
    private void setInitialTextValues(Combo text) {
        String[] recentWorkspaces = m_launchData.getRecentWorkspaces();
        for (int i = 0; i < recentWorkspaces.length; ++i) {
            if (recentWorkspaces[i] != null) {
                text.add(recentWorkspaces[i]);
            }
        }
        m_currentSelection = text.getItemCount() > 0 
            && m_launchData.getSelection() != null
            ? m_launchData.getSelection() : m_launchData.getInitialDefault();
        text.setText(m_currentSelection);
    }
}
