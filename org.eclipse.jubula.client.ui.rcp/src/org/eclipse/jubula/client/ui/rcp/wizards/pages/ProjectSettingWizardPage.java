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

import java.awt.im.InputContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedProjectNameText;
import org.eclipse.jubula.client.ui.rcp.wizards.ProjectWizard;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.I18nEnumCombo;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.html.Browser;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 18.05.2005
 */
public class ProjectSettingWizardPage extends WizardPage {
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;
    /** Width of text fields */
    private static final int WIDTH_TF = 500;
    /** Toolkit that is selected by default */
    private static final String DEFAULT_TOOLKIT =
            CommandConstants.CONCRETE_TOOLKIT;
    /** main container */
    private Composite m_mainComposite;
    /** top container */
    private Composite m_topComposite;
    /** the project that owns these properties */
    private IProjectPO m_project = null;
    /** new name for the project */
    private String m_newProjectName = null;
    /** the combo box for the project toolkit */
    private DirectCombo<String> m_projectToolKitComboBox;
    /** the text field for the project name */
    private Text m_projectNameTextField;
    /** Aut executable label container */
    private Composite m_autExecutableLabel;
    /** Aut executable textField container */
    private Composite m_autExecutableValue;
    /** the text field for AUT executable command*/
    private Text m_autExecutableTextField;
    /** browse button for the executable */
    private Button m_execButton;
    /** Aut URL label container */
    private Composite m_autUrlLabel;
    /** Aut URL textField container */
    private Composite m_autUrlValue;
    /** the text field for AUT executable command*/
    private Text m_autUrlTextField;
    /** Aut toolkit label container */
    private Composite m_autToolkitLabel;
    /** Aut toolkit comboBox container*/
    private Composite m_autToolkitValue;
    /** the combo box for the aut toolkit */
    private DirectCombo<String> m_autToolKitComboBox;
    /** Browser label container */
    private Composite m_browserLabel;
    /** Browser comboBox container*/
    private Composite m_browserValue;
    /** the combo box for browser */
    private I18nEnumCombo<Browser> m_browserComboBox;
    /** Browser path label container */
    private Composite m_browserPathLabel;
    /** Browser path textField container */
    private Composite m_browserPathValue;
    /** the text field for browser path*/
    private Text m_browserPathTextField;
    /** browse button for the executable */
    private Button m_browserPathButton;
    /** the selectionListener */
    private final WidgetSelectionListener m_selectionListener = 
            new WidgetSelectionListener();
    /** the modifyListener */
    private final WidgetModifyListener m_modifyListener =
            new WidgetModifyListener();
    /** the new AUT to create */
    private IAUTMainPO m_autMain;
    /** the name of the selected aut configuration */
    private IAUTConfigPO m_autConfig;
   
    /**
     * @param pageName The name of the wizard page.
     * @param newProject The new project to create.
     * @param autMain 
     * @param autConfig 
     */
    public ProjectSettingWizardPage(String pageName, IProjectPO newProject,
            IAUTMainPO autMain, IAUTConfigPO autConfig) {
        super(pageName);
        setPageComplete(false);
        m_project = newProject;  
        m_newProjectName = m_project.getName();
        m_autMain = autMain;
        m_autConfig = autConfig;
    }
    
    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        ScrolledComposite scroll =
                new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        m_mainComposite = UIComponentHelper.createLayoutComposite(scroll);
        ((GridLayout)m_mainComposite.getLayout()).marginLeft = 10;
        m_topComposite = UIComponentHelper.createLayoutComposite(
                m_mainComposite, NUM_COLUMNS_2);
        UIComponentHelper.createSeparator(m_topComposite, NUM_COLUMNS_2);
        createProjectNameField(m_topComposite); 
        createProjectToolKit(m_topComposite);
        setMessage(Messages.ProjectWizardNewProject);
        Plugin.getHelpSystem().setHelp(m_mainComposite, ContextHelpIds
            .PROJECT_WIZARD);
        createAutToolKit();
        createExecutableCommandField();
        createBrowserCombo();
        createBrowserPathField();
        try {
            modifyProjectNameField();
            validation();
        } catch (DialogValidationException ex) {
            ex.errorMessageHandling();
        }
        
        createNextLabel(m_mainComposite);
        
        scroll.setContent(m_mainComposite);
        scroll.setMinSize(m_mainComposite.computeSize(SWT.DEFAULT,
                SWT.DEFAULT));
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        setControl(m_mainComposite);
        handleProjectToolkitCombo();
    }

    /**
     * Creates the textfield for the project name.
     * @param parent The parent composite.
     */
    private void createProjectNameField(Composite parent) {
        Composite leftComposite = UIComponentHelper.createLayoutComposite(
                parent);
        Composite rightComposite = UIComponentHelper.createLayoutComposite(
                parent);
        setMargin((GridLayout)leftComposite.getLayout());
        setMargin((GridLayout)rightComposite.getLayout());
        ((GridData)rightComposite.getLayoutData())
            .grabExcessHorizontalSpace = true;
        UIComponentHelper.createLabelWithText(leftComposite,
                Messages.ProjectSettingWizardPageProjectName);
        m_projectNameTextField = new CheckedProjectNameText(rightComposite,
                SWT.BORDER);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = SWT.LEFT;
        textGridData.widthHint = WIDTH_TF;
        m_projectNameTextField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_projectNameTextField);
        m_projectNameTextField.setText(
                Messages.ProjectSettingWizardPageDefaultProjectName);
        m_projectNameTextField.setSelection(0, m_projectNameTextField.getText()
            .length());
        m_projectNameTextField.addModifyListener(m_modifyListener);
    }

    /**
     * Creates the AUT executable command line.
     */
    private void createExecutableCommandField() {
        if (m_autExecutableLabel != null) {
            return;
        }
        m_autExecutableLabel = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        m_autExecutableValue = UIComponentHelper.createLayoutComposite(
                m_topComposite, NUM_COLUMNS_2);
        setMargin((GridLayout)m_autExecutableLabel.getLayout());
        setMargin((GridLayout)m_autExecutableValue.getLayout());
        ((GridData)m_autExecutableValue.getLayoutData())
            .grabExcessHorizontalSpace = true;
        UIComponentHelper.createLabelWithText(m_autExecutableLabel,
                Messages.ProjectSettingWizardAUTExecutableCommand);
        m_autExecutableTextField = new Text(m_autExecutableValue, SWT.BORDER);
        LayoutUtil.setMaxChar(m_autExecutableTextField);
        m_execButton = new Button(m_autExecutableValue, SWT.PUSH);
        m_execButton.setText(Messages.AUTConfigComponentBrowse);
        m_execButton.addSelectionListener(m_selectionListener);
        m_autExecutableTextField.addModifyListener(m_modifyListener);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = SWT.LEFT;
        textGridData.widthHint = WIDTH_TF - m_execButton
                .computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        m_autExecutableTextField.setLayoutData(textGridData);
        m_mainComposite.layout(true, true);
    }

    /**
     * Creates the text field for the AUT url.
     */
    private void createUrlField() {
        if (m_autUrlLabel != null) {
            return;
        }
        m_autUrlLabel = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        m_autUrlValue = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        setMargin((GridLayout)m_autUrlLabel.getLayout());
        setMargin((GridLayout)m_autUrlValue.getLayout());
        ((GridData)m_autUrlValue.getLayoutData())
            .grabExcessHorizontalSpace = true;
        UIComponentHelper.createLabelWithText(m_autUrlLabel,
                Messages.ProjectSettingWizardAUTUrl);
        m_autUrlTextField = new Text(m_autUrlValue, SWT.BORDER);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = SWT.LEFT;
        textGridData.widthHint = WIDTH_TF;
        m_autUrlTextField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_autUrlTextField);
        m_autUrlTextField.addModifyListener(m_modifyListener);
        m_mainComposite.layout(true, true);
    }

    /**
     * Creates the project toolkit line
     * @param parent the parent composite
     */
    private void createProjectToolKit(Composite parent) {
        Composite leftComposite = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        Composite rightComposite = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        setMargin((GridLayout)leftComposite.getLayout());
        setMargin((GridLayout)rightComposite.getLayout());
        ((GridLayout)leftComposite.getLayout()).marginRight = 10;
        ((GridData)rightComposite.getLayoutData())
            .grabExcessHorizontalSpace = true;
        Label label = UIComponentHelper.createLabelWithText(leftComposite,
                Messages.ProjectSettingWizardPageProjectToolKitLabel);
        ControlDecorator.createInfo(label,
                I18n.getString("ControlDecorator.NewProjectToolkit"), false); //$NON-NLS-1$
        m_projectToolKitComboBox = ControlFactory.createToolkitCombo(
            rightComposite);
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(comboGridData,
                m_projectToolKitComboBox);
        m_projectToolKitComboBox.setLayoutData(comboGridData);
        String projectToolkit = m_project.getToolkit();
        if (projectToolkit != null && projectToolkit.length() > 0) {
            m_projectToolKitComboBox.setSelectedObject(projectToolkit);
        } else {
            m_projectToolKitComboBox.setSelectedObject(DEFAULT_TOOLKIT);
        }
        m_projectToolKitComboBox.addModifyListener(m_modifyListener);
    }

    /** Creates the aut toolkit line */
    private void createAutToolKit() {
        if (m_autToolkitLabel != null) {
            return;
        }
        m_autToolkitLabel = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        m_autToolkitValue = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        setMargin((GridLayout)m_autToolkitLabel.getLayout());
        setMargin((GridLayout)m_autToolkitValue.getLayout());
        ((GridData)m_autToolkitValue.getLayoutData())
            .grabExcessHorizontalSpace = true;
        Label label = UIComponentHelper.createLabelWithText(m_autToolkitLabel,
                Messages.ProjectSettingWizardPageAutToolKitLabel);
        ControlDecorator.createInfo(label,
                I18n.getString("ControlDecorator.NewProjectAUTToolkit"), false); //$NON-NLS-1$
        try {
            m_autToolKitComboBox = ControlFactory.createAutToolkitCombo(
                    m_autToolkitValue, m_project, null, true);
        } catch (ToolkitPluginException e) {
            e.printStackTrace();
        } 
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(comboGridData, m_autToolKitComboBox);
        m_autToolKitComboBox.setLayoutData(comboGridData);
        m_autToolKitComboBox.addModifyListener(m_modifyListener);
        m_mainComposite.layout(true, true);
    }

    /** Creates the browser combo line */
    private void createBrowserCombo() {
        if (m_browserLabel != null) {
            return;
        }
        m_browserLabel = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        m_browserValue = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        setMargin((GridLayout)m_browserLabel.getLayout());
        setMargin((GridLayout)m_browserValue.getLayout());
        ((GridData)m_browserValue.getLayoutData())
            .grabExcessHorizontalSpace = true;
        UIComponentHelper.createLabelWithText(m_browserLabel,
                I18n.getString("WebAutConfigComponent.browser")); //$NON-NLS-1$
        m_browserComboBox = UIComponentHelper.createEnumCombo(
                m_browserValue, 2, "WebAutConfigComponent.Browser", //$NON-NLS-1$
                    Browser.class);
        m_browserComboBox.deselectAll();
        m_browserComboBox.clearSelection();
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(comboGridData, m_browserComboBox);
        m_browserComboBox.setLayoutData(comboGridData);
        m_browserComboBox.addModifyListener(m_modifyListener);
        m_mainComposite.layout(true, true);
    }

    /** Creates the browser path line */
    private void createBrowserPathField() {
        if (m_browserPathLabel != null) {
            return;
        }
        m_browserPathLabel = UIComponentHelper.createLayoutComposite(
                m_topComposite);
        m_browserPathValue = UIComponentHelper.createLayoutComposite(
                m_topComposite, NUM_COLUMNS_2);
        setMargin((GridLayout)m_browserPathLabel.getLayout());
        setMargin((GridLayout)m_browserPathValue.getLayout());
        ((GridData)m_browserPathValue.getLayoutData())
            .grabExcessHorizontalSpace = true;
        Label label = UIComponentHelper.createLabelWithText(m_browserPathLabel,
                I18n.getString("WebAutConfigComponent.browserPath")); //$NON-NLS-1$
        ControlDecorator.createInfo(label,
                I18n.getString("ControlDecorator.WebBrowserPath"), false); //$NON-NLS-1$
        m_browserPathTextField = new Text(m_browserPathValue, SWT.BORDER);
        m_browserPathTextField.addModifyListener(m_modifyListener);
        LayoutUtil.setMaxChar(m_browserPathTextField);
        m_browserPathButton = new Button(m_browserPathValue, SWT.PUSH);
        m_browserPathButton.setText(I18n.getString("AUTConfigComponent.browse")); //$NON-NLS-1$
        m_browserPathButton.addSelectionListener(m_selectionListener);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        textGridData.horizontalAlignment = SWT.LEFT;
        textGridData.widthHint = WIDTH_TF - m_browserPathButton
                .computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        m_browserPathTextField.setLayoutData(textGridData);
        m_mainComposite.layout(true, true);
        
    }
    
    /**
     * remove the aut toolkit line
     */
    private void removeAutToolKit() {
        if (m_autToolkitLabel != null) {
            m_autToolkitLabel.dispose();
            m_autToolkitLabel = null;
        }
        if (m_autToolkitValue != null) {
            m_autToolkitValue.dispose();
            m_autToolkitValue = null;
        }
        m_autToolKitComboBox = null;
        m_mainComposite.layout(true, true);
    }
    
    /**
     * remove the aut executable line
     */
    private void removeAutExecutable() {
        if (m_autExecutableLabel != null) {
            m_autExecutableLabel.dispose();
            m_autExecutableLabel = null;
        }
        if (m_autExecutableValue != null) {
            m_autExecutableValue.dispose();
            m_autExecutableValue = null;
        }
        m_autExecutableTextField = null;
        putConfigValue(AutConfigConstants.EXECUTABLE, StringConstants.EMPTY);
        putConfigValue(AutConfigConstants.WORKING_DIR, StringConstants.EMPTY);
        m_mainComposite.layout(true, true);
    }
    
    /**
     * remove the aut executable line
     */
    private void removeAutUrl() {
        if (m_autUrlLabel != null) {
            m_autUrlLabel.dispose();
            m_autUrlLabel = null;
        }
        if (m_autUrlValue != null) {
            m_autUrlValue.dispose();
            m_autUrlValue = null;
        }
        m_autUrlTextField = null;
        putConfigValue(AutConfigConstants.AUT_URL, StringConstants.EMPTY);
        m_mainComposite.layout(true, true);
    }
    
    /**
     * remove the aut browser combo line
     */
    private void removeBrowserCombo() {
        if (m_browserLabel != null) {
            m_browserLabel.dispose();
            m_browserLabel = null;
        }
        if (m_browserValue != null) {
            m_browserValue.dispose();
            m_browserValue = null;
        }
        m_browserComboBox = null;
        putConfigValue(AutConfigConstants.BROWSER, StringConstants.EMPTY);
        m_mainComposite.layout(true, true);
    }
    
    /**
     * remove the aut browser path line
     */
    private void removeBrowserPathField() {
        if (m_browserPathLabel != null) {
            m_browserPathLabel.dispose();
            m_browserPathLabel = null;
        }
        if (m_browserPathValue != null) {
            m_browserPathValue.dispose();
            m_browserPathValue = null;
        }
        m_browserPathTextField = null;
        putConfigValue(AutConfigConstants.BROWSER_PATH, StringConstants.EMPTY);
        m_mainComposite.layout(true, true);
    }
    
    /**
     * Creates the next label.
     * @param parent the parent composite
     */
    private void createNextLabel(Composite parent) {
        Label nextLabel = UIComponentHelper.createLabelWithText(parent,
                Messages.ProjectSettingWizardPageClickFinish);
        GridData data = new GridData();
        data.horizontalSpan = NUM_COLUMNS_2;
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.END;
        nextLabel.setLayoutData(data);
        m_mainComposite.layout(true, true);
    }
    
    /** Handle aut config*/
    private void handleAutConfig() {
        String projectToolkit = m_projectToolKitComboBox.getSelectedObject();
        String autToolkit = m_autToolKitComboBox == null ? null
                : m_autToolKitComboBox.getSelectedObject();
        boolean isPTConcret = projectToolkit.equals(DEFAULT_TOOLKIT);
        if (!isPTConcret || isPTConcret && autToolkit != null) {
            fillAutConfig();
        } else {
            cleanAutConfig();
        }
    }
    
    /** Clear the default aut configs */
    private void cleanAutConfig() {
        m_autMain.setName(StringConstants.EMPTY);
        putConfigValue(AutConfigConstants.AUT_CONFIG_NAME,
                StringConstants.EMPTY);
        putConfigValue(AutConfigConstants.AUT_ID, StringConstants.EMPTY);
        putConfigValue(AutConfigConstants.AUT_CONFIG_AUT_HOST_NAME,
                StringConstants.EMPTY);
    }
    
    /** Fill the default aut configs */
    private void fillAutConfig() {
        m_autMain.setName(m_newProjectName);
        String configName = NLS.bind(
                Messages.AUTConfigComponentDefaultAUTConfigName, 
                        new String [] {
                            m_newProjectName, 
                            EnvConstants.LOCALHOST_ALIAS
                        });
        putConfigValue(AutConfigConstants.AUT_CONFIG_NAME, configName);
        putConfigValue(AutConfigConstants.AUT_ID, m_newProjectName);
        putConfigValue(AutConfigConstants.AUT_CONFIG_AUT_HOST_NAME,
                EnvConstants.LOCALHOST_ALIAS);
    }
       
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        removeListener();
        super.dispose();
    }
    
    /**
     * Removes all listeners.
     */
    private void removeListener() {
        if (m_project != null) {
            m_projectNameTextField.removeModifyListener(m_modifyListener);
        }
    }
    
    /** 
     * The action of the project name field.
     * the project name starts or end with a blank, or the field is empty
     * @throws Exception 
     */
    private void modifyProjectNameField() throws DialogValidationException {
        boolean isCorrect = true;
        String projectName = m_projectNameTextField.getText();
        int projectNameLength = projectName.length();
        if ((projectNameLength == 0) || (projectName.startsWith(" ")) //$NON-NLS-1$
            || (projectName.charAt(projectNameLength - 1) == ' ')) {
            
            isCorrect = false;
        }
        if (isCorrect) {
            if (ProjectPM.doesProjectNameExist(projectName)
                && !m_project.getName().equals(projectName)) {
                
                throw new DialogValidationException(
                        Messages.ProjectSettingWizardPageDoubleProjectName);
            }
            m_newProjectName = projectName;
            handleAutConfig();
        } else {
            throw new DialogValidationException(projectNameLength == 0
                    ? Messages.ProjectWizardEmptyProject
                            : Messages.ProjectWizardNotValidProject);
        }
    }

    /**
     * Handles the file browser button event.
     * @param title The file dialog title.
     * @param text texField
     * @return directory
     */
    private String fileBrowser(String title, Text text) {
        FileDialog fileDialog = new FileDialog(getShell(),
                SWT.APPLICATION_MODAL | SWT.ON_TOP);
        fileDialog.setText(title);
        String directory;
        String filterPath = Utils.getLastDirPath();
        try {
            File path = new File(text.getText());
            if (path.exists()) {
                if (path.isDirectory()) {
                    filterPath = path.getCanonicalPath();
                } else {
                    filterPath = new File(path.getParent()).getCanonicalPath();
                }
            }
        } catch (IOException e) {
            // Just use the default filter path which is already set
        }
        fileDialog.setFilterPath(filterPath);
        directory = fileDialog.open();
        if (directory != null) {
            Utils.storeLastDirPath(fileDialog.getFilterPath());
        }
        return directory;
    }
    
    /** Handle the executable path button */
    private void handleExecButtonEvent() {
        String directory = fileBrowser(Messages
                .AUTConfigComponentSelectExecutable, m_autExecutableTextField);

        if (directory != null) {
            m_autExecutableTextField.setText(directory);
            setWorkingDirToExecFilePath(directory);
            validation();
        }
    }
    
    /** Handle the browser path button */
    private void handleBrowserPathButtonEvent() {
        String directory = fileBrowser(
                I18n.getString("WebAutConfigComponent.SelectBrowserPath"), //$NON-NLS-1$
                m_browserPathTextField);
        if (directory != null) {
            m_browserPathTextField.setText(directory);
            validation();
        }
    }

    /**
     * Writes the path of the executable file in the AUT Working Dir field.
     * @param file The dir path of the executable file as string.
     */
    private void setWorkingDirToExecFilePath(String file) {
        String execPath = StringConstants.EMPTY;
        File wd = new File(file);
        if (wd.isAbsolute() && wd.getParentFile() != null) {
            execPath = wd.getParentFile().getAbsolutePath();
        }
        putConfigValue(AutConfigConstants.WORKING_DIR, execPath);
    }

    /** Check and modify the executable path */
    private void modifyExecutableTextField() {
        String executable = StringConstants.EMPTY;
        if (m_autExecutableTextField != null
                && !m_autExecutableTextField.getText().isEmpty()) {
            executable =  m_autExecutableTextField.getText();
            try {
                File file = new File(executable);
                if (!file.exists()) {
                    warningMessage(NLS.bind(
                            Messages.AUTConfigComponentFileNotFound,
                            file.getCanonicalPath()));
                }
            } catch (IOException e) {
                //do nothing
            }
        }
        putConfigValue(AutConfigConstants.EXECUTABLE, executable);
        setWorkingDirToExecFilePath(executable);
    }
    
    /**
     * Check and modify the url
     * @throws DialogValidationException
     */
    private void modifyUrlTextField() throws DialogValidationException {
        String urlText = StringConstants.EMPTY;
        if (m_autUrlTextField != null
                && !m_autUrlTextField.getText().isEmpty()) {
            try {
                new URL(m_autUrlTextField.getText());
                urlText = m_autUrlTextField.getText();
            } catch (MalformedURLException e) {
                throw new DialogValidationException(I18n.getString(
                        "WebAutConfigComponent.wrongUrl") + StringConstants.NEWLINE + e.getMessage()); //$NON-NLS-1$
            }
        }
        putConfigValue(AutConfigConstants.AUT_URL, urlText);
    }
    
    /** Modify the browser */
    private void modifyBrowser() {
        final String browser = (m_browserComboBox == null || m_browserComboBox
                .getSelectedObject() == null ? StringConstants.EMPTY
                        : m_browserComboBox.getSelectedObject().toString());
        putConfigValue(AutConfigConstants.BROWSER, browser);
    }
    
    /** Check and modify the browser path */
    private void modifyBrowserPathTextField() {
        String txt = StringConstants.EMPTY;
        if (m_browserPathTextField != null
                && m_browserPathTextField.getText().length() > 0) {
            txt = m_browserPathTextField.getText();
            try {
                File file = new File(txt);
                if (!file.exists()) {
                    warningMessage(NLS.bind(
                            Messages.AUTConfigComponentFileNotFound,
                            file.getCanonicalPath()));
                }
            } catch (IOException e) {
                //do nothing
            }
        }
        putConfigValue(AutConfigConstants.BROWSER_PATH, txt);
    }
    
    
    /** set default message */
    private void noMessage() {
        setMessage(Messages.ProjectWizardNewProject);
    }

    /**
     * @param errorMessage the error message
     */
    private void errorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return;
        }
        setMessage(errorMessage, IMessageProvider.ERROR);
    }
    
    /**
     * @param warningMessage the warning message
     */
    private void warningMessage(String warningMessage) {
        if (warningMessage == null || warningMessage.isEmpty()) {
            return;
        }
        setMessage(warningMessage, IMessageProvider.WARNING);
    }
    
    /**
     * Checks validity of all fields.
     */
    protected void validation() {
        noMessage();
        modifyExecutableTextField();
        modifyBrowser();
        modifyBrowserPathTextField();
        setPageComplete(true);
    }
    
    /**
     * @param key key
     * @param value value
     * @return <code>true</code> if the configuration was changed.
     *          Otherwise <code>false</code>.  
     */
    private boolean putConfigValue(String key, String value) {
        String previousValue = StringUtils.defaultString(
                m_autConfig.getConfigMap().put(key, value));
        boolean wasEmpty = previousValue.length() == 0; 
        boolean isEmpty = StringUtils.defaultString(value).length() == 0;
        boolean areBothEmpty = wasEmpty && isEmpty;
        if (isEmpty) {
            m_autConfig.getConfigMap().remove(key);
        }
        return (!areBothEmpty) || !value.equals(previousValue);
    }
    
    /**
     * Handle the combobox of project toolkit
     */
    private void handleProjectToolkitCombo() {
        String toolkit = m_projectToolKitComboBox.getSelectedObject();
        m_project.setToolkit(toolkit);
        removeAutToolKit();
        putConfigValue(AutConfigConstants.KEYBOARD_LAYOUT,
                StringConstants.EMPTY);
        handleAutToolkitCombo(toolkit);
    }
    
    /**
     * Handle the combobox of aut toolkits
     * @param toolkit 
     */
    private void handleAutToolkitCombo(String toolkit) {
        m_autMain.setToolkit(toolkit);
        removeAutExecutable();
        removeAutUrl();
        removeBrowserCombo();
        removeBrowserPathField();
        if (toolkit == null) {
            return;
        }
        
        switch (toolkit) {
            case CommandConstants.RCP_TOOLKIT:
            case CommandConstants.SWT_TOOLKIT:
                keyboardLayout();
            case CommandConstants.SWING_TOOLKIT:
            case CommandConstants.JAVAFX_TOOLKIT:
                createExecutableCommandField();
                break;
            case CommandConstants.HTML_TOOLKIT:
                createUrlField();
                createBrowserCombo();
                break;
            default:
                m_autMain.setToolkit(null);
                createAutToolKit();
                break;
        }
        
        handleAutConfig();
    }
    
    /** Handle the combobox of browser */
    private void handleBrowserCombo() {
        if (m_browserComboBox == null
                || m_browserComboBox.getSelectedObject() == null) {
            return;
        }
        Browser browser = m_browserComboBox.getSelectedObject();
        if (browser.equals(Browser.InternetExplorer)) {
            removeBrowserPathField();
        } else {
            createBrowserPathField();
        }
    }
    
    /** Set the default keyboard layout on */
    private void keyboardLayout() {
        String local = InputContext.getInstance().getLocale().toString();  
        local = Arrays.asList(Languages.getInstance()
                .getKeyboardLayouts()).contains(local)
                ? local : Locale.getDefault().toString();
        putConfigValue(AutConfigConstants.KEYBOARD_LAYOUT, local);
    }
    
    /**
     * {@inheritDoc}
     */
    public ProjectWizard getWizard() {
        return (ProjectWizard)super.getWizard();
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     */
    private class WidgetSelectionListener implements SelectionListener {
        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_execButton)) {
                handleExecButtonEvent();
            } else if (o.equals(m_browserPathButton)) {
                handleBrowserPathButtonEvent();
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            //do nothing
        }
    }
    
    /**
     * This private inner class contains a new ModifyListener.
     * @author BREDEX GmbH
     */
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            Object o = e.getSource();
            try {
                if (o.equals(m_projectToolKitComboBox)) {
                    handleProjectToolkitCombo();
                } else if (o.equals(m_autToolKitComboBox)) {
                    handleAutToolkitCombo(m_autToolKitComboBox
                            .getSelectedObject());
                } else if (o.equals(m_browserComboBox)) {
                    handleBrowserCombo();
                } else if (o.equals(m_projectNameTextField)) {
                    modifyProjectNameField();
                    validation();
                } else if (o.equals(m_autUrlTextField)) {
                    modifyUrlTextField();
                    validation();
                } else if (o.equals(m_autExecutableTextField)
                        || o.equals(m_browserPathTextField)) {
                    validation();
                }
            } catch (DialogValidationException ex) {
                ex.errorMessageHandling();
            }
        }       
    }
    
    /**
     * Handles the error messages
     * @author BREDEX GmbH
     */
    protected class DialogValidationException extends Exception {
        /** @param message the error message */
        public DialogValidationException(String message) {
            super(message);
        }
        /** error handling*/
        public void errorMessageHandling() {
            setPageComplete(false);
            errorMessage(super.getMessage());
        }
    }
    
    /**
     * @param layout the GridLayout where the margin should be set on
     */
    private void setMargin(GridLayout layout) {
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 20;
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
     * @return the toolkit of project
     */
    public String getProjectToolkit() {
        return m_project.getToolkit();
    }

    /**
     * @return the toolkit of aut
     */
    public String getAutToolkit() {
        return m_autMain.getToolkit();
    }
}