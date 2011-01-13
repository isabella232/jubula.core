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
package org.eclipse.jubula.client.ui.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.core.utils.PrefStoreHelper;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.widgets.CheckedRequiredText;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * @author BREDEX GmbH
 * @created 10.01.2005
 */
public class TestDataPreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage {
    /** 1 column */
    private static final int NUM_COLUMNS = 3;

    /** 10 horizontal spaces */
    private static final int HORIZONTAL_SPACING_10 = 10;

    /** 10 vertical spaces */
    private static final int VERTICAL_SPACING_10 = 10;

    /** margin height = 10 */
    private static final int MARGIN_HEIGHT_10 = 10;

    /** margin width = 10 */
    private static final int MARGIN_WIDTH_10 = 10;

    /** allowed length for textfield entry */
    private static final int REF_TF_LIMIT = 1;

    /** allowed length for textfield entry */
    private static final int VALSEP_TF_LIMIT = 1;

    /** allowed length for textfield entry */
    private static final int PATHSEP_TF_LIMIT = 1;

    /** allowed length for textfield entry */
    private static final int ESC_TF_LIMIT = 1;
    
    /** horizontal span */
    private static final int HORIZONTAL_SPAN = 2;

    /** textfield to define character for escape symbol */
    private CheckedRequiredText m_textEscape = null;
    
    /** textfield to define character for reference */
    private CheckedRequiredText m_textRef = null;

    /** textfield to define character for seperator values */
    private CheckedRequiredText m_textValueSep = null;

    /** textfield to define character for seperator path */
    private CheckedRequiredText m_textPathSep = null;

    /** textfield to define character for function */
    private CheckedRequiredText m_textFunc = null;
    
    /**
     * Default Constructor
     *  
     */
    public TestDataPreferencePage() { //
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
    }

    /**
     * Implement the user interface for the preference page. Returns a control
     * that should be used as the main control for the page.
     * <p>
     * User interface defined here supports the definition of preference
     * settings used by the management logic.
     * </p>
     * 
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        /** Add layer to parent widget */
        Composite composite = new Composite(parent, SWT.NONE);

        /** Define laout rules for widget placement */
        compositeGridData(composite);

        createEscapeTextfield(composite);
        createRefTextField(composite);
        createPathSepTextField(composite);
        createValueSepTextField(composite);
        /** return the widget used as the base for the user interface */
        Plugin.getHelpSystem()
                .setHelp(parent, ContextHelpIds.PREFPAGE_TESTDATA);
        KeyListener checker = new KeyListener() {
            public void keyPressed(KeyEvent e) {
            // nothing
            }

            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }

        };
        m_textEscape.addKeyListener(checker);
        m_textRef.addKeyListener(checker);
        m_textPathSep.addKeyListener(checker);
        m_textValueSep.addKeyListener(checker);
        return composite;
    }

    /**
     * checks completeness of dialog
     *
     */
    protected void checkCompleteness() {
        String esc = m_textEscape.getText();
        String ref = m_textRef.getText();
//        String func = Constants.EMPTY_STRING;
        String valSep = m_textValueSep.getText();
        String pathSep = m_textPathSep.getText();
        // distinctive check
        Set <String>list = new HashSet<String>();
        int numberOfDistinctOptions = 2;
        list.add(esc);
        list.add(ref);
//        list.add(func);
//        list.add(valSep);
//        list.add(pathSep);
        
        if (esc.length() == 0 || StringConstants.SPACE.equals(esc)) {
            setErrorMessage(I18n.getString("TestDataPreferencePage.EscEmpty")); //$NON-NLS-1$
            setValid(false);
            // character for labeling of references in testdata 
        } else if (ref.length() == 0 || StringConstants.SPACE.equals(ref)) {
            setErrorMessage(I18n.getString("TestDataPreferencePage.RefEmpty")); //$NON-NLS-1$
            setValid(false);
        } else if (valSep.length() == 0 
                || StringConstants.SPACE.equals(valSep)) {
            setErrorMessage(I18n.getString("TestDataPreferencePage.PathEmpty")); //$NON-NLS-1$
            setValid(false);
        } else if (pathSep.length() == 0 
                || StringConstants.SPACE.equals(pathSep)) {
            setErrorMessage(I18n.getString("TestDataPreferencePage.ValueEmpty")); //$NON-NLS-1$
            setValid(false);
        } else if (list.size() != numberOfDistinctOptions) {
            setErrorMessage(I18n.getString("TestDataPreferencePage.NotDisjunkt")); //$NON-NLS-1$
            setValid(false);
        } else {
            setErrorMessage(null);
            setMessage(null); 
            setValid(true);
        }
    }
    
    /**
     * @param composite The composite.
     */
    private void compositeGridData(Composite composite) {
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS;
        compositeLayout.horizontalSpacing = HORIZONTAL_SPACING_10;
        compositeLayout.verticalSpacing = VERTICAL_SPACING_10;
        compositeLayout.marginHeight = MARGIN_HEIGHT_10;
        compositeLayout.marginWidth = MARGIN_WIDTH_10;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(compositeData);
    }

    /**
     * @param parent parent of this textfield
     */
    private void createRefTextField(Composite parent) {
        Label labelRef = new Label(parent, SWT.NONE);
        labelRef.setText(I18n.getString("TestDataPreferencePage.CharForRef")); //$NON-NLS-1$
        m_textRef = new CheckedRequiredText(parent, SWT.SINGLE | SWT.BORDER);
        m_textRef.setText(getPreferenceStore()
                .getString(Constants.REFERENCE_CHAR_KEY));
        m_textRef.setTextLimit(REF_TF_LIMIT);
        GridData textDataRef = new GridData();
        textDataRef.widthHint = Dialog.convertWidthInCharsToPixels(
            Layout.getFontMetrics(parent), REF_TF_LIMIT + 1);
        textDataRef.horizontalSpan = HORIZONTAL_SPAN;
        m_textRef.setLayoutData(textDataRef);
        
    }

    /**
     * @param parent parent of this textfield
     */
    private void createPathSepTextField(Composite parent) {
        Label labelRef = new Label(parent, SWT.NONE);
        labelRef.setVisible(false);
        labelRef.setText(I18n.getString("TestDataPreferencePage.PathSepForRef")); //$NON-NLS-1$
        m_textPathSep = 
            new CheckedRequiredText(parent, SWT.SINGLE | SWT.BORDER);
        m_textPathSep.setText(getPreferenceStore()
                .getString(Constants.PATH_CHAR_KEY));
        m_textPathSep.setTextLimit(PATHSEP_TF_LIMIT);
        m_textPathSep.setVisible(false);
        GridData textDataRef = new GridData();
        textDataRef.widthHint = Dialog.convertWidthInCharsToPixels(
            Layout.getFontMetrics(parent), PATHSEP_TF_LIMIT + 1);
        textDataRef.horizontalSpan = HORIZONTAL_SPAN;
        m_textPathSep.setLayoutData(textDataRef);
        
    }

    /**
     * @param parent parent of this textfield
     */
    private void createValueSepTextField(Composite parent) {
        Label labelRef = new Label(parent, SWT.NONE);
        labelRef.setVisible(false);
        labelRef.setText(I18n.getString("TestDataPreferencePage.ValueSepForRef")); //$NON-NLS-1$
        m_textValueSep = 
            new CheckedRequiredText(parent, SWT.SINGLE | SWT.BORDER);
        m_textValueSep.setVisible(false);
        m_textValueSep.setText(getPreferenceStore()
                .getString(Constants.VALUE_CHAR_KEY));
        m_textValueSep.setTextLimit(VALSEP_TF_LIMIT);
        GridData textDataRef = new GridData();
        textDataRef.widthHint = Dialog.convertWidthInCharsToPixels(
            Layout.getFontMetrics(parent), VALSEP_TF_LIMIT + 1);
        textDataRef.horizontalSpan = HORIZONTAL_SPAN;
        m_textValueSep.setLayoutData(textDataRef);
    }

    /**
     * @param parent parent of this textfield
     */
    private void createEscapeTextfield(Composite parent) {
        Label labelEsc = new Label(parent, SWT.NONE);
        labelEsc.setText(I18n.getString("TestDataPreferencePage.CharForEsc")); //$NON-NLS-1$
        m_textEscape = new CheckedRequiredText(parent, SWT.SINGLE | SWT.BORDER);
        m_textEscape.setEnabled(false);
        m_textEscape.setText(getPreferenceStore()
                .getString(Constants.ESCAPE_CHAR_KEY));
        m_textEscape.setTextLimit(ESC_TF_LIMIT);
        GridData textDataEsc = new GridData();
        textDataEsc.widthHint = Dialog.convertWidthInCharsToPixels(
            Layout.getFontMetrics(parent), ESC_TF_LIMIT + 1);
        textDataEsc.horizontalSpan = HORIZONTAL_SPAN;
        m_textEscape.setLayoutData(textDataEsc);
    }

    /**
     * @param parent parent of this textfield
     */
    private void createFuncTextfield(Composite parent) {
        Label labelFunc = new Label(parent, SWT.NONE);
        labelFunc.setText(I18n.getString("TestDataPreferencePage.CharForFunc")); //$NON-NLS-1$
        m_textFunc = new CheckedRequiredText(parent, SWT.SINGLE | SWT.BORDER);
        m_textFunc.setText(getPreferenceStore()
                .getString(Constants.FUNCTION_CHAR_KEY));
        m_textFunc.setTextLimit(ESC_TF_LIMIT);
        GridData textDataFunc = new GridData();
        textDataFunc.widthHint = Dialog.convertWidthInCharsToPixels(
            Layout.getFontMetrics(parent), ESC_TF_LIMIT + 1);
        textDataFunc.horizontalSpan = HORIZONTAL_SPAN;
        m_textFunc.setLayoutData(textDataFunc);
    }

    /**
     * Initializes the preference page
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setDescription(I18n.getString("TestDataPreferencePage.description")); //$NON-NLS-1$
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void performDefaults() {
        m_textEscape.setText(getDefaultPrefs(Constants.ESCAPE_CHAR_KEY));
        m_textRef.setText(getDefaultPrefs(Constants.REFERENCE_CHAR_KEY));
        m_textPathSep.setText(getDefaultPrefs(Constants.PATH_CHAR_KEY));
        m_textValueSep.setText(getDefaultPrefs(Constants.VALUE_CHAR_KEY));
        checkCompleteness();
    }
    
    /**
     * @return default value
     * @param key
     *            preference key
     */
    private String getDefaultPrefs(String key) {
        return getPreferenceStore().getDefaultString(key);
    }

    /**
     * Method declared on IPreferencePage.
     * 
     * @return performOK
     */
    public boolean performOk() {
        // escape character for testdata
        String esc = m_textEscape.getText();
        String ref = m_textRef.getText();
        String func = StringConstants.EMPTY;
        String valSep = m_textValueSep.getText();
        String pathSep = m_textPathSep.getText();
        getPreferenceStore().setValue(Constants.REFERENCE_CHAR_KEY, ref);
        getPreferenceStore().setValue(Constants.ESCAPE_CHAR_KEY, esc);
        getPreferenceStore().setValue(Constants.FUNCTION_CHAR_KEY, func);
        getPreferenceStore().setValue(Constants.VALUE_CHAR_KEY, valSep);
        getPreferenceStore().setValue(Constants.PATH_CHAR_KEY, pathSep);

        PrefStoreHelper helper = PrefStoreHelper.getInstance();
        helper.setEscapeChar(esc);
        helper.setFunctionChar(func);
        helper.setReferenceChar(ref);
        helper.setValueChar(valSep);
        return super.performOk();
    }

    /**
     * Can be used to implement any special processing, such as notification, if
     * required. Logic to actually change preference values should be in the
     * <code>performOk</code> method as that method will also be triggered
     * when the Apply push button is selected.
     * <p>
     * If others are interested in tracking preference changes they can use the
     * <code>addPropertyChangeListener</code> method available for for an
     * <code>IPreferenceStore</code> or <code>Preferences</code>.
     * </p>
     * 
     * {@inheritDoc}
     * {@inheritDoc}
     * {@inheritDoc}
     * {@inheritDoc}
     */
    protected void performApply() {
        performOk();
    }

}
