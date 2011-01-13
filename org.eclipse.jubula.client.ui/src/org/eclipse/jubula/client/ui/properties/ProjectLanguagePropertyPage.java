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
package org.eclipse.jubula.client.ui.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.ListElementChooserComposite;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;


/**
 * This is the class for the test data property page of a project.
 *
 * @author BREDEX GmbH
 * @created 08.02.2005
 */
public class ProjectLanguagePropertyPage extends AbstractProjectPropertyPage {

    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;
    /** the list field for the available languages */ 
    private List m_availableLangList;
    /** the list field for the project languages */ 
    private List m_projectLangList;
    /** the button to add a language from the upper field into the bottom field */
    private Button m_downButton;
    /** the button to delete a language from the bottom field */
    private Button m_upButton;
    /** the combo box for the project default language */
    private DirectCombo <Locale> m_defaultLangComboBox;
    /** the GDStateController */
    private final GuiDancerSelectionListener m_selectionListener = 
        new GuiDancerSelectionListener();
    /** the composite with 2 ListBoxes */
    private ListElementChooserComposite m_chooseLists;
    /** the button to add all languages from the upper field into the bottom field */
    private Button m_allDownButton;
    /** the button to delete all languages from the bottom field */
    private Button m_allUpButton;
    /** all project languages at the time this page was instantiated */
    private String[] m_initialLanguages;
    /**
     * @param es the editSupport
     */
    public ProjectLanguagePropertyPage(EditSupport es) {
        super(es);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        Composite composite = createComposite(parent, NUM_COLUMNS_1,
            GridData.FILL, GridData.FILL, true, true);
        Composite projectNameComposite = createComposite(composite,
            NUM_COLUMNS_2, GridData.FILL, GridData.FILL, true, true);

        noDefaultAndApplyButton();       
        String str = getProject().getName();
        super.getShell().setText(I18n.getString(
            "ProjectPropertyPage.shellTitle") + str); //$NON-NLS-1$

        createLabel(projectNameComposite, StringConstants.EMPTY);
        createLabel(projectNameComposite, StringConstants.EMPTY);
        createLabel(composite, I18n.getString("ProjectSettingWizardPage.SelectLanguagesOfTD"));       //$NON-NLS-1$
        Composite innerComposite = new Composite(composite, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        innerComposite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalSpan = NUM_COLUMNS_2;
        compositeData.horizontalAlignment = GridData.FILL;
        compositeData.grabExcessHorizontalSpace = true;
        innerComposite.setLayoutData(compositeData);
        createLanguageLists(innerComposite);
        getObjects();
        m_initialLanguages = m_projectLangList.getItems();

        separator(composite, NUM_COLUMNS_1);
        createLanguageCombo(createComposite(composite, NUM_COLUMNS_2,
            GridData.FILL, GridData.FILL, false, false));
        resizeLists();
        initFields();
        addListener();
        Plugin.getHelpSystem().setHelp(parent,
            ContextHelpIds.PROJECT_PROPERTY_PAGE);
        return composite;
    }

    /**
     * @param innerComposite the parent composite
     */
    private void createLanguageLists(Composite innerComposite) {
        Languages langUtil = Languages.getInstance();
        java.util.List leftList = Utils.getAvailableLanguages();
        java.util.List<String> rightList = new ArrayList<String>();
        Iterator<Locale> iter = 
            getProject().getLangHelper().getLangListIterator();
        while (iter.hasNext()) {
            Locale l = iter.next();
            if (langUtil.getDisplayString(l) != null) {
                rightList.add(langUtil.getDisplayString(l));
            }
        } 
        m_chooseLists = new ProjectLanguageChooserComposite(innerComposite, 
                leftList, rightList);
    }
  
    /**
     * Resizes the two ListBoxes.
     */
    private void resizeLists() {
        ((GridData)m_projectLangList.getLayoutData()).widthHint = Dialog
            .convertHeightInCharsToPixels(Layout
                .getFontMetrics(m_projectLangList), 15);
        ((GridData)m_availableLangList.getLayoutData()).widthHint = Dialog
            .convertHeightInCharsToPixels(Layout
                .getFontMetrics(m_projectLangList), 15);
    }
    
    /**
     * Creates a new composite.
     * @param parent The parent composite.
     * @param numColumns the number of columns for this composite.
     * @param horizontalAlignment The horizontal alignment.
     * @param verticalAlignment The vertical alignment.
     * @param grabHorizontalSpace Whether the composite should grab extra
     *                            horizontal space in the layout.
     * @param grabVerticalSpace Whether the composite should grab extra
     *                            vertical space in the layout.
     * @return The new composite.
     */
    private Composite createComposite(Composite parent, int numColumns, 
            int horizontalAlignment, int verticalAlignment,
            boolean grabHorizontalSpace, boolean grabVerticalSpace) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        composite.setLayoutData(new GridData(
            horizontalAlignment, verticalAlignment, 
            grabHorizontalSpace, grabVerticalSpace));
        return composite;       
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
        if (getProject().getDefaultLanguage() != null && !StringConstants.EMPTY
                .equals(getProject().getDefaultLanguage())) {
            m_defaultLangComboBox.setSelectedObject(getProject().
                getDefaultLanguage());
            
        }
    }
    
    /**
     * Creates the combobox for the project default language.
     * @param parent The parent composite.
     */
    private void createLanguageCombo(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, GridData.FILL, true, true);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, GridData.FILL, true, true);
        createLabel(leftComposite, I18n.getString("ProjectPropertyPage.languageLabel")); //$NON-NLS-1$
        m_defaultLangComboBox = new DirectCombo<Locale>(rightComposite, 
                SWT.READ_ONLY, new ArrayList<Locale>(), new ArrayList<String>(),
                false, true);
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
     * fills the defaultLanguage Combobox
     * only call this after manipulating project languages is finished
     *
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
     * Handles the selectionEvent of the Down Button.
     */
    void handleDownButtonEvent() {
        fillDefaultLanguageComboBox();
        enableLangCombo();
    }
    
    /**
     * Handles the selectionEvent of the Down Button.
     */
    void handleAllDownButtonEvent() {
        fillDefaultLanguageComboBox();
        enableLangCombo();
    }

    /**
     * checks, if any AUT uses the given language
     * @param lang language to validate
     * @return flag, if given language is used of any AUT
     */
    boolean isLangUsedInAUT(String lang) {
        Set<IAUTMainPO> autSet = getProject().getAutMainList();
        for (IAUTMainPO aut : autSet) {
            java.util.List<Locale> locList = 
                aut.getLangHelper().getLanguageList();
            for (Locale loc : locList) {
                String displName = 
                    Languages.getInstance().getDisplayString(loc);
                if (lang.equals(displName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks, if the given language is currently used in the project
     * @param lang language to validate
     * @return true if given language is currently used in the project
     */
    boolean isLangUsedInProject(String lang) {

        for (int j = 0; j < m_initialLanguages.length; j++) {
            if (lang.equals(m_initialLanguages[j])) {
                return true;
            }
        }

        return false;
    }
 
    /**
     * Dis-/Enables the ComboBox.
     */
    void enableLangCombo() {
        if (m_projectLangList.getItemCount() == 0
                || m_defaultLangComboBox.getItemCount() == 0) {        
            m_defaultLangComboBox.setEnabled(false);
            setErrorMessage(I18n.getString("ProjectPropertyPage.noProjectLanguage"));  //$NON-NLS-1$
            setValid(false);
        } else {
            if (StringConstants.EMPTY.equals(m_defaultLangComboBox.
                getText())) {
                setErrorMessage(I18n.getString("ProjectPropertyPage.noProjectLanguage"));  //$NON-NLS-1$
                setValid(false);
                m_defaultLangComboBox.setEnabled(true);
                return;
            }
            m_defaultLangComboBox.setEnabled(true);
            setErrorMessage(null);
            setMessage(I18n.getString("PropertiesAction.page1"), NONE); //$NON-NLS-1$
            setValid(true);
            getProject().setDefaultLanguage(m_defaultLangComboBox.
                getSelectedObject());
        }
    }
        
    /**
     * {@inheritDoc}
     */
    public boolean performOk() {
        return true;
    }


    /**
     * Updates the list fields and the combo box.
     */
    void updateLanguages() {
        getProject().setDefaultLanguage(m_defaultLangComboBox.
            getSelectedObject());
        getProject().getLangHelper().clearLangList();
        for (int i = 0; i < m_projectLangList.getItemCount(); i++) {
            getProject().getLangHelper().addLanguageToList(
                Languages.getInstance().getLocale(
                    m_projectLangList.getItem(i)));
        }
    }
   
    /**
     * Adds necessary listeners.
     */
    private void addListener() {
        m_projectLangList.addSelectionListener(m_selectionListener);
        m_availableLangList.addSelectionListener(m_selectionListener);
        m_upButton.addSelectionListener(m_selectionListener);
        m_downButton.addSelectionListener(m_selectionListener);
        m_allUpButton.addSelectionListener(m_selectionListener);
        m_allDownButton.addSelectionListener(m_selectionListener);
        m_defaultLangComboBox.addSelectionListener(m_selectionListener);
    }
    
    /**
     * @author BREDEX GmbH
     * @created Dec 20, 2006
     */
    private final class ProjectLanguageChooserComposite 
            extends ListElementChooserComposite {
        
        /**
         * Constructor
         * @param innerComposite The inner composite
         * @param leftList The left list.
         * @param rightList The right list.
         */
        private ProjectLanguageChooserComposite(Composite innerComposite, 
                java.util.List leftList, java.util.List<String> rightList) {
            super(innerComposite,  I18n.getString("ProjectPropertyPage.upperLabel"),  //$NON-NLS-1$
                    leftList, I18n.getString("ProjectSettingWizardPage.bottomLabel"),  //$NON-NLS-1$
                    rightList, 15, 
                    new Image[] { IconConstants.RIGHT_ARROW_IMAGE, 
                                  IconConstants.DOUBLE_RIGHT_ARROW_IMAGE, 
                                  IconConstants.LEFT_ARROW_IMAGE, 
                                  IconConstants.DOUBLE_LEFT_ARROW_IMAGE }, 
                    new Image[] { IconConstants.RIGHT_ARROW_DIS_IMAGE, 
                                  IconConstants.DOUBLE_RIGHT_ARROW_DIS_IMAGE, 
                                  IconConstants.LEFT_ARROW_DIS_IMAGE, 
                                  IconConstants.DOUBLE_LEFT_ARROW_DIS_IMAGE }, 
                    new String[] { 
                        I18n.getString("ProjectPropertyPage.downToolTip"), //$NON-NLS-1$
                        I18n.getString("ProjectSettingWizardPage.allDownToolTip"), //$NON-NLS-1$
                        I18n.getString("ProjectPropertyPage.upToolTip"), //$NON-NLS-1$
                        I18n.getString("ProjectSettingWizardPage.allUpToolTip")  //$NON-NLS-1$
                    }, ListElementChooserComposite.VERTICAL);
        }

        /**
         * {@inheritDoc}
         */
        protected void handleAllTwoToOneButtonEvent() {
            String[] selection = m_chooseLists.getListTwo().getItems();
            for (int i = 0; i < selection.length; i++) {
                if (isLangUsedInAUT(selection[i])) {
                    Utils.createMessageDialog(MessageIDs.E_DELETE_PROJECT_LANG);
                    return;
                }
            }

            for (int i = 0; i < selection.length; i++) {
                if (isLangUsedInProject(selection[i])) {
                    Dialog dialog = Utils.createMessageDialog(
                        MessageIDs.Q_REMOVE_PROJECT_LANGUAGES);
                    if (!(Window.OK == dialog.getReturnCode())) {
                        return;
                    }
                    break;
                }
            }
            
            enableLangCombo();
            super.handleAllTwoToOneButtonEvent();
            /* has to be filled after updating language list */
            fillDefaultLanguageComboBox();
        }

        /**
         * {@inheritDoc}
         */
        protected void handleSelectionTwoToOneButtonEvent() {
            String[] selection = m_chooseLists.getListTwo().getSelection();
            for (int i = 0; i < selection.length; i++) {
                if (isLangUsedInAUT(selection[i])) {
                    Utils.createMessageDialog(
                            MessageIDs.E_DELETE_PROJECT_LANG);
                    return;
                }
            }

            for (int i = 0; i < selection.length; i++) {
                if (isLangUsedInProject(selection[i])) {
                    Dialog dialog = Utils.createMessageDialog(
                        MessageIDs.Q_REMOVE_PROJECT_LANGUAGES);
                    if (!(Window.OK == dialog.getReturnCode())) {
                        return;
                    }
                    break;
                }
            }
            
            enableLangCombo();
            super.handleSelectionTwoToOneButtonEvent();
            /* has to be filled after updating language list */
            fillDefaultLanguageComboBox();
        }
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
                updateLanguages();
                return;
            } else if (o.equals(m_upButton) || o.equals(m_allUpButton)) {
                updateLanguages();
                enableLangCombo();
                return;
            } else if (o.equals(m_allDownButton)) {
                handleAllDownButtonEvent();
                updateLanguages();
                return;
            } else if (o.equals(m_defaultLangComboBox)) {
                enableLangCombo();
                return;
            } else if (o.equals(m_availableLangList)) {
                enableLangCombo();
                return;
            } else if (o.equals(m_projectLangList)) {
                enableLangCombo();
                return;
            } 
            Assert.notReached("Event activated by unknown widget."); //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_availableLangList)) {
                handleDownButtonEvent();
                updateLanguages();
                return;
            } else if (o.equals(m_projectLangList)) {
                updateLanguages();
                return;
            } 
            Assert.notReached("Event activated by unknown widget."); //$NON-NLS-1$
        }        
    }
}