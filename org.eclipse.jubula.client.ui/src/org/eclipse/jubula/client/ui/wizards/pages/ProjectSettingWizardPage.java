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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.factory.ControlFactory;
import org.eclipse.jubula.client.ui.provider.GDControlDecorator;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.widgets.CheckedProjectNameText;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.GDText;
import org.eclipse.jubula.client.ui.widgets.ListElementChooserComposite;
import org.eclipse.jubula.client.ui.wizards.ProjectWizard;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
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
    private static final String DEFAULT_TOOLKIT = 
        "com.bredexsw.guidancer.ConcreteToolkitPlugin";   //$NON-NLS-1$
    /** number of columns = 3 */
    /** the project that owns these properties */
    private IProjectPO m_project = null;
    /** new name for the project */
    private String m_newProjectName = null;
    /** reusability of the new project */
    private boolean m_isProjectReusable = false;
    /** protected status of the new project */
    private boolean m_isProjectProtected = false;
    /** the list field for the available languages */ 
    private List m_availableLangList;
    /** the list field for the project languages */ 
    private List m_projectLangList;
    /** the list of the project languages, to save the project languages */
    private java.util.List<Locale> m_oldLocaleList = new ArrayList<Locale>();
    /** the button to add a language from the upper field into the bottom field */
    private Button m_downButton;
    /** the button to delete a language from the bottom field */
    private Button m_upButton;
    /** the button to add all languages from the upper field into the bottom field */
    private Button m_allDownButton;
    /** the button to delete all languages from the bottom field */
    private Button m_allUpButton;
    /** the combo box for the project default language */
    private DirectCombo <Locale> m_defaultLangComboBox;
    /** the combo box for the project aut toolkit */
    private DirectCombo<String> m_autToolKitComboBox;
    /** the text field for the project name */
    private GDText m_projectNameTextField;
    /** the checkbox for the project reusability */
    private Button m_projectReusabilityCheckbox;
    /** the checkbox for the project reusability */
    private Button m_projectProtectionCheckbox;
    /** the selectionListener */
    private final GuiDancerSelectionListener m_selectionListener = 
        new GuiDancerSelectionListener();
    /** the composite with 2 ListBoxes */
    private ListElementChooserComposite m_chooseLists;
    /** the the modifyListener */
    private final GuiDancerModifyListener m_modifyListener = 
        new GuiDancerModifyListener();
   
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
        separator(composite, NUM_COLUMNS_1);  
        GDControlDecorator.decorateInfo(createLabel(composite, 
                I18n.getString("ProjectSettingWizardPage.SelectLanguagesOfTD")),  //$NON-NLS-1$
                 "GDControlDecorator.NewProjectProjectLanguage", //$NON-NLS-1$ 
                false); 
        Composite innerComposite = new Composite(composite, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        innerComposite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalSpan = NUM_COLUMNS_2;
        compositeData.horizontalAlignment = GridData.CENTER;
        compositeData.grabExcessHorizontalSpace = true;
        innerComposite.setLayoutData(compositeData);
        java.util.List<String> availableLanguages = 
            Utils.getAvailableLanguages();
        java.util.List<String> usedLanguages = new ArrayList<String>();
        String userLanguage = 
            Languages.getInstance().getDisplayString(Locale.getDefault());
        if (userLanguage != null && userLanguage.length() != 0) {
            availableLanguages.remove(userLanguage);
            usedLanguages.add(userLanguage);
        }
        m_chooseLists = createLanguageChooser(innerComposite, availableLanguages
                , usedLanguages);
        getObjects();
        separator(composite, NUM_COLUMNS_1); 
        createLanguageCombo(createComposite(composite, NUM_COLUMNS_2, 
            GridData.FILL, false));
        createNextLabel(composite);
        initFields();
        addListener();
        enableLangCombo();
        setMessage(I18n.getString("ProjectWizard.newProject"), IMessageProvider.NONE); //$NON-NLS-1$
        resizeLists();
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
     * @param innerComposite the inner composite
     * @param availableLanguages the available languages
     * @param usedLanguages the used languages
     * @return the list element chooser
     */
    private ListElementChooserComposite createLanguageChooser(
            Composite innerComposite,
            java.util.List<String> availableLanguages,
            java.util.List<String> usedLanguages) {
        return new ListElementChooserComposite(
                innerComposite, I18n.getString("ProjectPropertyPage.upperLabel"), //$NON-NLS-1$
                availableLanguages, I18n.getString("ProjectSettingWizardPage.bottomLabel"), //$NON-NLS-1$
                usedLanguages, 15, 
                new Image[]{IconConstants.RIGHT_ARROW_IMAGE,
                    IconConstants.DOUBLE_RIGHT_ARROW_IMAGE,
                    IconConstants.LEFT_ARROW_IMAGE,
                    IconConstants.DOUBLE_LEFT_ARROW_IMAGE}, 
                new Image[] { IconConstants.RIGHT_ARROW_DIS_IMAGE, 
                    IconConstants.DOUBLE_RIGHT_ARROW_DIS_IMAGE, 
                    IconConstants.LEFT_ARROW_DIS_IMAGE, 
                    IconConstants.DOUBLE_LEFT_ARROW_DIS_IMAGE },
                new String[]{I18n.getString("ProjectPropertyPage.downToolTip"), //$NON-NLS-1$
                    I18n.getString("ProjectSettingWizardPage.allDownToolTip"),  //$NON-NLS-1$
                    I18n.getString("ProjectPropertyPage.upToolTip"), //$NON-NLS-1$
                    I18n.getString("ProjectSettingWizardPage.allUpToolTip")},  //$NON-NLS-1$
                ListElementChooserComposite.VERTICAL);
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
        GDControlDecorator.decorateInfo(createLabel(leftComposite, 
            I18n.getString("ProjectPropertyPage.isReusable")),   //$NON-NLS-1$
            "GDControlDecorator.NewProjectIsReusable", false);  //$NON-NLS-1$
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
        GDControlDecorator.decorateInfo(createLabel(leftComposite, 
            I18n.getString("ProjectPropertyPage.isProtected")),   //$NON-NLS-1$
            "GDControlDecorator.NewProjectIsProtected", false);  //$NON-NLS-1$
        m_projectProtectionCheckbox = new Button(rightComposite, SWT.CHECK);
        m_projectProtectionCheckbox.setSelection(false);
    }

    /**
     * Creates a label.
     * @param composite the parent composite
     */
    private void createNextLabel(Composite composite) {
        Label nextLabel = new Label(composite, SWT.NONE);
        nextLabel.setText(I18n.getString("ProjectSettingWizardPage.clickNext")); //$NON-NLS-1$
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
        GDControlDecorator.decorateInfo(createLabel(leftComposite, 
                I18n.getString("ProjectSettingWizardPage.autToolKitLabel")), 
                 "GDControlDecorator.NewProjectToolkit", false); //$NON-NLS-1$
        m_autToolKitComboBox = ControlFactory.createToolkitCombo(
            rightComposite); 
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        Layout.addToolTipAndMaxWidth(comboGridData, m_autToolKitComboBox);
        m_autToolKitComboBox.setLayoutData(comboGridData);
        String projectToolkit = m_project.getToolkit();
        if (projectToolkit != null && projectToolkit.length() > 0) {
            m_autToolKitComboBox.setSelectedObject(projectToolkit);
        } else {
            m_autToolKitComboBox.setSelectedObject(DEFAULT_TOOLKIT);
        }
    }

    /**
     * Resizes the two ListBoxes.
     */
    private void resizeLists() {
        ((GridData)m_projectLangList.getLayoutData()).widthHint = 
            Dialog.convertHeightInCharsToPixels(
                Layout.getFontMetrics(m_projectLangList), 15);
        ((GridData)m_availableLangList.getLayoutData()).widthHint = 
            Dialog.convertHeightInCharsToPixels(
                Layout.getFontMetrics(m_availableLangList), 15);
    }

    /**
     * fills the defaultLanguage Combobox
     * only call this after manipulating project languages is finished
     */
    private void fillDefaultLanguageComboBox() {
        Locale selected = m_defaultLangComboBox.getSelectedObject();
        java.util.List <Locale> localeObjects = new ArrayList<Locale>();
        java.util.List <String> localeStrings = new ArrayList<String>();
        for (String language : m_projectLangList.getItems()) {
            localeStrings.add(language);
            localeObjects.add(Languages.getInstance().getLocale(language));
        }
        m_defaultLangComboBox.setItems(localeObjects, localeStrings);
        m_defaultLangComboBox.setSelectedObject(selected);
    }

    /**
     * Gets the listBoxes / Buttons from the ListElementChooserComposite composite
     */
    private void getObjects() {
        m_availableLangList = m_chooseLists.getListOne();
        m_projectLangList = m_chooseLists.getListTwo();
        m_upButton = m_chooseLists.getSelectionTwoToOneButton();
        m_downButton = m_chooseLists.getSelectionOneToTwoButton();
        m_allUpButton = m_chooseLists.getAllTwoToOneButton();
        m_allDownButton = m_chooseLists.getAllOneToTwoButton();
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
     * Inits all swt field in this page.
     */
    private void initFields() {
        m_oldLocaleList.addAll(m_project.getLangHelper().getLanguageList());
        if (m_project.getDefaultLanguage() != null
            && !StringConstants.EMPTY.equals(m_project.getDefaultLanguage())) {
            m_defaultLangComboBox.setSelectedObject(m_project.
                getDefaultLanguage());
        }
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
        createLabel(leftComposite, I18n.getString("ProjectSettingWizardPage.ProjectName")); //$NON-NLS-1$
        m_projectNameTextField = new CheckedProjectNameText(rightComposite,
                SWT.BORDER);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        m_projectNameTextField.setLayoutData(textGridData);
        Layout.setMaxChar(m_projectNameTextField);
        m_projectNameTextField.setText(I18n.getString("ProjectSettingWizardPage.defaultProjectName")); //$NON-NLS-1$
        m_projectNameTextField.setSelection(0, m_projectNameTextField.getText()
            .length());
    }
    
    /**
     * Creates the combobox for the project default language.
     * @param parent The parent composite.
     */
    private void createLanguageCombo(Composite parent) {
        Composite leftComposite = createComposite(parent, 3, 
            GridData.BEGINNING, false);
        Composite middleComposite = createComposite(parent, NUM_COLUMNS_2, 
            GridData.FILL, true);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
                GridData.END, true);
        GDControlDecorator.decorateInfo(createLabel(leftComposite, 
                I18n.getString("ProjectSettingWizardPage.languageLabel")), 
                "GDControlDecorator.NewProjectDefaultLanguage", 
                false); //$NON-NLS-1$
        m_defaultLangComboBox = new DirectCombo<Locale>(middleComposite, 
                SWT.READ_ONLY, new ArrayList<Locale>(), 
                new ArrayList<String>(), false, true);
        createLabel(rightComposite, StringConstants.EMPTY);
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        comboGridData.horizontalAlignment = GridData.FILL;
        Layout.addToolTipAndMaxWidth(comboGridData, m_defaultLangComboBox);
        m_defaultLangComboBox.setLayoutData(comboGridData);
        /* has to be filled after updating language list,
         * ok here, because lists are initialized first */
        fillDefaultLanguageComboBox();
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
            false , false, 1, 1);
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
     * @return The union of all languages of the actual project
     */
    private String[] getLanguageList() {
        Languages languages = Languages.getInstance();
        Set<Locale> allLangs = new HashSet<Locale>(); 
        for (int i = 0; i < m_project.getLangHelper().getLanguageList().size(); 
            i++) {
            
            java.util.List<Locale> langList = m_project.getLangHelper().
                getLanguageList();
            for (int j = 0; j < langList.size(); j++) {
                allLangs.add(langList.get(j));
            }            
        }   
        languages.setProjectLanguages(allLangs);
        String[] unionLanguages = new String[allLangs.size()];  
        Iterator<Locale> iter = allLangs.iterator();
        int i = 0;
        while (iter.hasNext()) {
            unionLanguages[i] = 
                Languages.getInstance().getDisplayString(iter.next());
            i++;
        }
        Arrays.sort(unionLanguages);
        return unionLanguages;
    }
    
    /**
     * Updates the combo box items of the language combo box
     */
    public void updateLangCombo() {
        if (!m_defaultLangComboBox.isDisposed()) {
            m_projectLangList.setItems(getLanguageList());
            fillDefaultLanguageComboBox();
        }
    }
   
    /**
     * Handles the selectionEvent of the Down Button.
     */
    private void handleDownButtonEvent() {
        fillDefaultLanguageComboBox();
        enableLangCombo();
    }
    
    /**
     * Handles the selectionEvent of the Down Button.
     */
    private void handleAllDownButtonEvent() {
        fillDefaultLanguageComboBox();
        enableLangCombo();
    }
    
    /**
     * Handles the selectionEvent of the Down Button.
     */
    private void handleAllUpButtonEvent() {
        ((AUTSettingWizardPage)getNextPage()).clearLanguageLists();
        fillDefaultLanguageComboBox();
        enableLangCombo();
    }
    
    /**
     * Handles the selectionEvent of the UP Button.
     */
    private void handleUpButtonEvent() {
        fillDefaultLanguageComboBox();
        enableLangCombo();
    }
        
    /**
     * Dis-/Enables the ComboBox.
     */
    private void enableLangCombo() {
        if (m_projectLangList.getItemCount() == 0
                || m_defaultLangComboBox.getItemCount() == 0) {  
            
            m_defaultLangComboBox.setEnabled(false);
            setMessage(I18n.getString("ProjectPropertyPage.noProjectLanguage"),  //$NON-NLS-1$
                IMessageProvider.ERROR); 
        } else {
            if (StringConstants.EMPTY.equals(m_defaultLangComboBox
                .getText())) {
                
                setMessage(I18n.getString(
                    "ProjectPropertyPage.noProjectLanguage"),  //$NON-NLS-1$
                    IMessageProvider.ERROR); 
                m_defaultLangComboBox.setEnabled(true);
                return;
            }
            m_defaultLangComboBox.setEnabled(true);
            setMessage(I18n.getString("ProjectWizard.newProject"), //$NON-NLS-1$
                IMessageProvider.NONE);
            modifyProjectNameFieldAction(false);
        }
    }

    /**
     * Enables/Disables the next button.
     */
    private void confirmNextButton() {
        if (m_project != null) {
            m_project.setDefaultLanguage(m_defaultLangComboBox.
                getSelectedObject());
            m_project.getLangHelper().clearLangList();
            for (int i = 0; i < m_projectLangList.getItemCount(); i++) {
                m_project.getLangHelper().addLanguageToList(
                    Languages.getInstance().getLocale(
                        m_projectLangList.getItem(i)));
            }
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
        m_projectLangList.addSelectionListener(m_selectionListener);
        m_availableLangList.addSelectionListener(m_selectionListener);
        m_projectNameTextField.addModifyListener(m_modifyListener);
        m_upButton.addSelectionListener(m_selectionListener);
        m_downButton.addSelectionListener(m_selectionListener);
        m_allUpButton.addSelectionListener(m_selectionListener);
        m_allDownButton.addSelectionListener(m_selectionListener);
        m_defaultLangComboBox.addSelectionListener(m_selectionListener);
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
            m_downButton.removeSelectionListener(m_selectionListener);
            m_upButton.removeSelectionListener(m_selectionListener);
            m_allDownButton.removeSelectionListener(m_selectionListener);
            m_allUpButton.removeSelectionListener(m_selectionListener);
            m_availableLangList.removeSelectionListener(m_selectionListener);
            m_projectLangList.removeSelectionListener(m_selectionListener);
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
        if (modifyProjectNameFieldAction(isAUTNameModified) 
                && m_projectLangList.getItemCount() > 0
                && !m_defaultLangComboBox.getText()
                    .equals(StringConstants.EMPTY)) {
            
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
            setMessage(I18n.getString("ProjectWizard.newProject"), IMessageProvider.NONE); //$NON-NLS-1$
            if (isProjectNameModiyfied) {
                enableLangCombo();
            }
            if (ProjectPM.doesProjectNameExist(projectName)
                && !m_project.getName().equals(projectName)) {
                
                setMessage(I18n.getString("ProjectSettingWizardPage.doubleProjectName"), IMessageProvider.ERROR); //$NON-NLS-1$ 
                isCorrect = false;
            }
        } else {
            if (projectNameLength == 0) {
                setMessage(I18n.getString("ProjectWizard.emptyProject"), IMessageProvider.ERROR); //$NON-NLS-1$
            } else {
                setMessage(I18n.getString("ProjectWizard.notValidProject"), IMessageProvider.ERROR); //$NON-NLS-1$
            }
        }
        return isCorrect;
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     * @created 10.02.2005
     */
    private class GuiDancerSelectionListener implements SelectionListener {
        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_downButton)) {
                handleDownButtonEvent();
                checkCompleteness(true);
                return;
            } else if (o.equals(m_upButton)) {
                handleUpButtonEvent();
                checkCompleteness(true);
                return;
            } else if (o.equals(m_allUpButton)) {
                handleAllUpButtonEvent();
                checkCompleteness(true);
                return;
            } else if (o.equals(m_allDownButton)) {
                handleAllDownButtonEvent();
                checkCompleteness(true);
                return;
            } else if (o.equals(m_defaultLangComboBox)) {
                handleDefaultLangCombo();
                return;
            } else if (o.equals(m_availableLangList)) {
                enableLangCombo();
                return;
            } else if (o.equals(m_projectLangList)) {
                enableLangCombo();
                return;
            } else if (o.equals(m_autToolKitComboBox)) {
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
            Assert.notReached("Event activated by unknown widget: " + //$NON-NLS-1$
                    "'" + String.valueOf(o) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        
        
        
        
        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_availableLangList)) {
                handleDownButtonEvent();
                checkCompleteness(true);
                return;
            } else if (o.equals(m_projectLangList)) {
                handleUpButtonEvent();
                checkCompleteness(true);
                return;
            } else if (o.equals(m_autToolKitComboBox)) {
                handleToolkitCombo();
                return;
            } else if (o.equals(m_defaultLangComboBox)) {
                handleDefaultLangCombo();
                return;
            }
            Assert.notReached("Event activated by unknown widget: " + //$NON-NLS-1$
                    "'" + String.valueOf(o) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }


        /**
         * 
         */
        private void handleDefaultLangCombo() {
            enableLangCombo();
            checkCompleteness(false);
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
    private class GuiDancerModifyListener implements ModifyListener {
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
     * @return Returns the AVAILABLE_LANG_LIST.
     */
    public List getAvailableLangList() {
        return m_availableLangList;
    }
    
    /**
     * @return Returns the projectLangList.
     */
    public List getProjectLangList() {
        return m_projectLangList;
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