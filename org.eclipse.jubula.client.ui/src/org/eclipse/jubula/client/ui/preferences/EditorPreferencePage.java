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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * @author BREDEX GmbH
 * @created 10.01.2005
 */
public class EditorPreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage {
    /** 1 column */
    private static final int NUM_COLUMNS = 1;

    /** 10 horizontal spaces */
    private static final int HORIZONTAL_SPACING_10 = 10;

    /** 10 vertical spaces */
    private static final int VERTICAL_SPACING_10 = 10;

    /** margin height = 10 */
    private static final int MARGIN_HEIGHT_10 = 10;

    /** margin width = 10 */
    private static final int MARGIN_WIDTH_10 = 10;

    /** button for insert node in testCaseEditor */
    private Button m_nodeInsertButton;
    /** button for add node in testCaseEditor */
    private Button m_nodeAddButton;

    /** The preferece store to hold the existing preference values. */
    private IPreferenceStore m_store = Plugin.getDefault()
            .getPreferenceStore();

    /**
     * Default Constructor
     *  
     */
    public EditorPreferencePage() { //
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

        /** Define layout rules for widget placement */
        compositeGridData(composite);
        createInsertNodeAfterSelectedNodeButton(composite);

        /** return the widget used as the base for the user interface */
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.EDITOR_PREF_PAGE);
        return composite;
    }

    /**
     * @param composite the parent composite
     */
    private void createInsertNodeAfterSelectedNodeButton(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.PrefPageBasicNodeInsertionGroup);
        RowLayout layout = new RowLayout();
        layout.type = SWT.VERTICAL;
        group.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        Label label = new Label(group, SWT.NONE);
        label.setText(Messages.EditorPreferencePageAddPositionText);
        new Label(group, SWT.NONE);
        m_nodeAddButton = new Button(group, SWT.RADIO);
        m_nodeAddButton.setText(Messages.PrefPageBasicAddNewNode);
        m_nodeAddButton.setSelection(Plugin.getDefault()
                .getPluginPreferences().getBoolean(Constants.NODE_INSERT_KEY));
        m_nodeInsertButton = new Button(group, SWT.RADIO);
        m_nodeInsertButton.setText(Messages.PrefPageBasicInsertNewNode);
        m_nodeInsertButton.setSelection(!m_nodeAddButton.getSelection());
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
     * Initializes the preference page
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setDescription(Messages.EditorPreferencePageDescription);
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void performDefaults() {
        m_nodeInsertButton.setSelection(m_store
                .getDefaultBoolean(Constants.NODE_INSERT_KEY));
        m_nodeAddButton.setSelection(!m_store
                .getDefaultBoolean(Constants.NODE_INSERT_KEY));
    }

    /**
     * Method declared on IPreferencePage. 
     * 
     * @return performOK
     */
    public boolean performOk() {
        Plugin.getDefault().getPreferenceStore().setValue(
                Constants.NODE_INSERT_KEY, m_nodeAddButton.getSelection());
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
     */
    protected void performApply() {
        super.performApply();
    }

}
