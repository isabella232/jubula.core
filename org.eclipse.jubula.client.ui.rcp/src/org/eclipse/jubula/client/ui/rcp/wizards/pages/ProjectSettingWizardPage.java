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
package org.eclipse.jubula.client.ui.rcp.wizards.pages;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedProjectNameText;
import org.eclipse.jubula.client.ui.rcp.wizards.ProjectWizard;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;



/**
 * @author BREDEX GmbH
 * @created 18.05.2005
 */
public class ProjectSettingWizardPage extends WizardPage {
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;
    /** Toolkit that is selected by default */
    private static final String DEFAULT_TOOLKIT = "com.bredexsw.guidancer.ConcreteToolkitPlugin";   //$NON-NLS-1$
    /** the project that owns these properties */
    private IProjectPO m_project = null;
    /** new name for the project */
    private String m_newProjectName = null;
    /** reusability of the new project */
    private boolean m_isProjectReusable = false;
    /** protected status of the new project */
    private boolean m_isProjectProtected = false;
    /** the combo box for the project aut toolkit */
    private DirectCombo<String> m_autToolKitComboBox;
    /** the text field for the project name */
    private Text m_projectNameTextField;
    /** the checkbox for the project reusability */
    private Button m_projectReusabilityCheckbox;
    /** the checkbox for the project reusability */
    private Button m_projectProtectionCheckbox;
    /** the selectionListener */
    private final WidgetSelectionListener m_selectionListener = 
        new WidgetSelectionListener();
    /** the the modifyListener */
    private final WidgetModifyListener m_modifyListener = 
        new WidgetModifyListener();
   
    /**
     * @param pageName The name of the wizard page.
     * @param newProject The new project to create.
     */
    public ProjectSettingWizardPage(String pageName, IProjectPO newProject) {
        super(pageName);
        setPageComplete(false);
        m_project = newProject;  
        m_newProjectName = m_project.getName();
        m_isProjectReusable = m_project.getIsReusable();
        m_isProjectProtected = m_project.getIsProtected();
    }

    /**
     * @return the combo box for the project aut toolkit
     */
    public DirectCombo<String> getToolkitComboBox() {
        return m_autToolKitComboBox;
    }
    
    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL
            | SWT.H_SCROLL);
        Composite composite = createComposite(scroll, NUM_COLUMNS_1, 
            GridData.FILL, false);
        Composite projectNameComposite = createComposite(composite, 
            NUM_COLUMNS_2, GridData.FILL, false);
        createLabel(projectNameComposite, StringConstants.EMPTY);
        createLabel(projectNameComposite, StringConstants.EMPTY);
        createProjectNameField(projectNameComposite); 
        createLabel(projectNameComposite, StringConstants.EMPTY);
        createLabel(projectNameComposite, StringConstants.EMPTY);
        createIsReusableCheckbox(projectNameComposite);
        createIsProtectedCheckbox(projectNameComposite);
        createLabel(projectNameComposite, StringConstants.EMPTY);
        separator(projectNameComposite, NUM_COLUMNS_2); 
        createLabel(projectNameComposite, StringConstants.EMPTY);
        createAutToolKit(projectNameComposite);
        separator(projectNameComposite, NUM_COLUMNS_2); 
        createNextLabel(composite);
        addListener();
        setMessage(Messages.ProjectWizardNewProject, IMessageProvider.NONE);
        Plugin.getHelpSystem().setHelp(composite, ContextHelpIds
            .PROJECT_WIZARD);
        scroll.setContent(composite);
        scroll.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);                
        setControl(scroll);
        checkCompleteness(true);
    }

    /**
     * Creates the checkbox for project reusability.
     * 
     * @param composite the parent composite
     */
    private void createIsReusableCheckbox(Composite composite) {
        Composite leftComposite = createComposite(composite, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(composite, NUM_COLUMNS_1, 
            GridData.FILL, true);
        ControlDecorator.decorateInfo(createLabel(leftComposite, 
            Messages.ProjectPropertyPageIsReusable),
            "ControlDecorator.NewProjectIsReusable", false);  //$NON-NLS-1$
        m_projectReusabilityCheckbox = new Button(rightComposite, SWT.CHECK);
        m_projectReusabilityCheckbox.setSelection(false);
    }
    
    /**
     * Creates the checkbox for project protected.
     * 
     * @param composite the parent composite
     */
    private void createIsProtectedCheckbox(Composite composite) {
        Composite leftComposite = createComposite(composite, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(composite, NUM_COLUMNS_1, 
            GridData.FILL, true);
        ControlDecorator.decorateInfo(createLabel(leftComposite, 
            Messages.ProjectPropertyPageIsProtected),
            "ControlDecorator.NewProjectIsProtected", false);  //$NON-NLS-1$
        m_projectProtectionCheckbox = new Button(rightComposite, SWT.CHECK);
        m_projectProtectionCheckbox.setSelection(false);
    }

    /**
     * Creates a label.
     * @param composite the parent composite
     */
    private void createNextLabel(Composite composite) {
        Label nextLabel = new Label(composite, SWT.NONE);
        nextLabel.setText(Messages.ProjectSettingWizardPageClickNext);
        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.END;
        nextLabel.setLayoutData(data);
    }

    /**
     * @param parent the parent composite
     */
    private void createAutToolKit(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_2, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 4;
        leftComposite.setLayout(gridLayout);
        rightComposite.setLayout(gridLayout);
        ControlDecorator.decorateInfo(createLabel(leftComposite, 
                Messages.ProjectSettingWizardPageAutToolKitLabel), 
                 "ControlDecorator.NewProjectToolkit", false); //$NON-NLS-1$
        m_autToolKitComboBox = ControlFactory.createToolkitCombo(
            rightComposite); 
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(comboGridData, m_autToolKitComboBox);
        m_autToolKitComboBox.setLayoutData(comboGridData);
        String projectToolkit = m_project.getToolkit();
        if (projectToolkit != null && projectToolkit.length() > 0) {
            m_autToolKitComboBox.setSelectedObject(projectToolkit);
        } else {
            m_autToolKitComboBox.setSelectedObject(DEFAULT_TOOLKIT);
        }
    }

    /**
     * Creates a separator line.
     * @param composite The parent composite.
     * @param horSpan The horizonzal span.
     */
    private void separator(Composite composite, int horSpan) {
        createLabel(composite, StringConstants.EMPTY);
        Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sepData = new GridData();
        sepData.horizontalAlignment = GridData.FILL;
        sepData.horizontalSpan = horSpan;
        sep.setLayoutData(sepData);
        createLabel(composite, StringConstants.EMPTY);
    }

    /**
     * Creates the textfield for the project name.
     * @param parent The parent composite.
     */
    private void createProjectNameField(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_2, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        leftComposite.setLayout(gridLayout);
        rightComposite.setLayout(gridLayout);
        createLabel(leftComposite, 
                Messages.ProjectSettingWizardPageProjectName);
        m_projectNameTextField = new CheckedProjectNameText(rightComposite,
                SWT.BORDER);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        m_projectNameTextField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_projectNameTextField);
        m_projectNameTextField.setText(
                Messages.ProjectSettingWizardPageDefaultProjectName);
        m_projectNameTextField.setSelection(0, m_projectNameTextField.getText()
            .length());
    }
    
    /**
     * Creates a label for this page.
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    private Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER, 
            false, false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;
    }
    
    /**
     * Creates a new composite.
     * @param parent The parent composite.
     * @param numColumns the number of columns for this composite.
     * @param alignment The horizontalAlignment.
     * @param horizontalSpace The horizontalSpace.
     * @return The new composite.
     */
    private Composite createComposite(Composite parent, int numColumns, 
            int alignment, boolean horizontalSpace) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalAlignment = alignment;
        compositeData.grabExcessHorizontalSpace = horizontalSpace;
        composite.setLayoutData(compositeData);
        return composite;       
    }
    
    /**
     * Enables/Disables the next button.
     */
    private void confirmNextButton() {
        if (m_project != null) {
            m_newProjectName = m_projectNameTextField.getText();
            m_isProjectReusable = m_projectReusabilityCheckbox.getSelection();
            m_isProjectProtected = m_projectProtectionCheckbox.getSelection();
            m_project.setToolkit(m_autToolKitComboBox.getSelectedObject());
        }
    }
       
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        removeListener();
        super.dispose();
    }
    
    /**
     * Adds necessary listeners.
     */
    private void addListener() {
        m_projectNameTextField.addModifyListener(m_modifyListener);
        m_autToolKitComboBox.addSelectionListener(m_selectionListener);
        m_projectReusabilityCheckbox.addSelectionListener(m_selectionListener);
        m_projectProtectionCheckbox.addSelectionListener(m_selectionListener);
    }
    
    /**
     * Removes all listeners.
     */
    private void removeListener() {
        if (m_project != null) {
            m_projectNameTextField.removeModifyListener(m_modifyListener);
            m_projectReusabilityCheckbox.removeSelectionListener(
                m_selectionListener);
            m_projectProtectionCheckbox.removeSelectionListener(
                    m_selectionListener);
        }
    }
    
    /**
     * Sets the "Next>"-button true, if all fields are filled in correctly.
     * @param isAUTNameModified True, if the aut name was modified.
     */
    private void checkCompleteness(boolean isAUTNameModified) {
        if (modifyProjectNameFieldAction(isAUTNameModified)) {
            
            setPageComplete(true);
            confirmNextButton();
            return;
        }
        setPageComplete(false);
    }
    
    
    
    /** 
     * The action of the project name field.
     * @param isProjectNameModiyfied True, if the aut name was modified.
     * @return false, if the project name field contents an error:
     * the project name starts or end with a blank, or the field is empty
     */
    private boolean modifyProjectNameFieldAction(
        boolean isProjectNameModiyfied) {
        
        boolean isCorrect = true;
        String projectName = m_projectNameTextField.getText();
        int projectNameLength = projectName.length();
        if ((projectNameLength == 0) || (projectName.startsWith(" ")) //$NON-NLS-1$
            || (projectName.charAt(projectNameLength - 1) == ' ')) {
            
            isCorrect = false;
        }
        if (isCorrect) {
            setMessage(Messages.ProjectWizardNewProject, IMessageProvider.NONE);
            if (ProjectPM.doesProjectNameExist(projectName)
                && !m_project.getName().equals(projectName)) {
                
                setMessage(Messages.ProjectSettingWizardPageDoubleProjectName, 
                    IMessageProvider.ERROR); 
                isCorrect = false;
            }
        } else {
            if (projectNameLength == 0) {
                setMessage(Messages.ProjectWizardEmptyProject, 
                        IMessageProvider.ERROR);
            } else {
                setMessage(Messages.ProjectWizardNotValidProject, 
                        IMessageProvider.ERROR);
            }
        }
        return isCorrect;
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     * @created 10.02.2005
     */
    private class WidgetSelectionListener implements SelectionListener {
        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_autToolKitComboBox)) {
                handleToolkitCombo();
                return;
            } else if (o.equals(m_projectReusabilityCheckbox)) {
                boolean isReusable = m_projectReusabilityCheckbox
                    .getSelection();
                if (isReusable) {
                    m_projectProtectionCheckbox.setSelection(true);
                }
                checkCompleteness(false);
                return;
            } else if (o.equals(m_projectProtectionCheckbox)) {
                checkCompleteness(false);
                return;
            }
            Assert.notReached(Messages.EventActivatedByUnknownWidget 
                + StringConstants.COLON + StringConstants.SPACE 
                + StringConstants.APOSTROPHE + String.valueOf(o) 
                + StringConstants.APOSTROPHE);
        }

        
        
        
        
        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_autToolKitComboBox)) {
                handleToolkitCombo();
                return;
            }
            Assert.notReached(Messages.EventActivatedByUnknownWidget 
                    + StringConstants.COLON + StringConstants.SPACE 
                    + StringConstants.APOSTROPHE + String.valueOf(o) 
                    + StringConstants.APOSTROPHE);
        }


        /**
         * 
         */
        private void handleToolkitCombo() {
            m_project.setToolkit(m_autToolKitComboBox.getSelectedObject());
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    public ProjectWizard getWizard() {
        return (ProjectWizard)super.getWizard();
    }
    
    /**
     * This private inner class contains a new ModifyListener.
     * @author BREDEX GmbH
     * @created 23.05.2005
     */
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            Object o = e.getSource();
            if (o.equals(m_projectNameTextField)) {
                checkCompleteness(true);
                return;
            }           
        }       
    }
    
    /**
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem()
        .displayHelp(ContextHelpIds.PROJECT_WIZARD);
    }

    /**
     * @return the newProjectName
     */
    public String getNewProjectName() {
        return m_newProjectName;
    }

    /**
     * @return <code>true</code> if the new project is reusable. Otherwise
     *         <code>false</code>.
     */
    public boolean isProjectReusable() {
        return m_isProjectReusable;
    }
    
    /**
     * @return <code>true</code> if the new project is protected. Otherwise
     *         <code>false</code>.
     */
    public boolean isProjectProtected() {
        return m_isProjectProtected;
    }
}