/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Wizard page for matching the parameter names.
 *
 * @author BREDEX GmbH
 */
public class ParameterNamesMatchingWizardPage extends WizardPage {

    /** The data for replacing execution Test Cases. */
    private final ReplaceExecTestCaseData m_replaceExecTestCasesData;

    /** The last selected new specification Test Case. */
    private ISpecTestCasePO m_lastNewSpecTestCase;

    /** The scrolled composite containing the parameter names. */
    private ScrolledComposite m_scroll;

    /** GridLayout to show the new and old parameter names. */
    private Composite m_selectGrid;

    /** An array of combo boxes for the old names. */
    private List<ParameterNameCombo> m_oldNameCombos =
            new ArrayList<ParameterNameCombo>();

    /**
     * @param pageName
     *            The name of the page.
     * @param replaceExecTestCasesData The data for replacing execution Test Cases.
     */
    public ParameterNamesMatchingWizardPage(String pageName,
            ReplaceExecTestCaseData replaceExecTestCasesData) {
        super(pageName, Messages.ReplaceTCRWizard_matchParameterNames_title,
                null);
        m_replaceExecTestCasesData = replaceExecTestCasesData;
        setDescription(Messages
                .ReplaceTCRWizard_matchParameterNames_multi_description);
        setPageComplete(false);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        // create a main composite with one column
        Composite composite = new Composite(parent, SWT.NONE);
        setGrid(composite, 1);
        setControl(composite);
        // create a group for selecting the parameters
        createGroupForSelectingParameters(composite);
    }

    /**
     * Create the group including the table for matching the parameters
     * with combo boxes.
     * @param parent The parent.
     */
    private void createGroupForSelectingParameters(Composite parent) {
        Group groupSelection = new Group(parent, SWT.NONE);
        setGrid(groupSelection, 1);

        m_scroll = new ScrolledComposite(
                groupSelection, SWT.V_SCROLL | SWT.H_SCROLL);
        setGrid(m_scroll, 1);
        m_scroll.setExpandHorizontal(true);
        m_scroll.setExpandVertical(true);

        m_selectGrid = new Composite(m_scroll, SWT.NONE);
        m_scroll.setContent(m_selectGrid);
        setGrid(m_selectGrid, 2);

    }

    /**
     * Create the table of parameters showing the new parameters in the left
     * column and the combo boxes in the right column.
     * @param parent The parent composite with a grid layout of 2 columns.
     */
    private void createTableOfParameters(Composite parent) {
        if (m_lastNewSpecTestCase == m_replaceExecTestCasesData
                .getNewSpecTestCase()) {
            return; // no new specification Test Case has been selected
        }
        m_lastNewSpecTestCase = m_replaceExecTestCasesData
                .getNewSpecTestCase();
        // remove the previously shown parameter names
        for (Control child: m_selectGrid.getChildren()) {
            child.dispose();
        }
        // create head row
        createHeadLabel(m_selectGrid, Messages
                .ReplaceTCRWizard_matchParameterNames_newParameter);
        createHeadLabel(m_selectGrid, Messages
                .ReplaceTCRWizard_matchParameterNames_oldParameter);
        // fill the rows with the new parameter names
        List<IParamDescriptionPO> paramDescList = m_replaceExecTestCasesData
                .getNewSpecTestCase()
                .getParameterList();
        m_oldNameCombos.clear();
        for (IParamDescriptionPO paramDesc: paramDescList) {
            createLabel(parent,
                    GeneralLabelProvider.getTextWithBrackets(paramDesc));
            List<String> oldNames = m_replaceExecTestCasesData
                    .getOldParameterNamesByType(paramDesc);
            if (oldNames.size() == 0) {
                Label label = createLabel(parent, Messages
                    .ReplaceTCRWizard_matchParameterNames_warningNoSameType);
                ControlDecorator.addWarningDecorator(
                    label,
                    Messages
                    .ReplaceTCRWizard_matchParameterNames_warningNoSameTypeDesc
                );
                m_oldNameCombos.add(null); // remember no matching with null
            } else {
                ParameterNameCombo combo = new ParameterNameCombo(
                        parent, oldNames, m_replaceExecTestCasesData
                            .getOldSpecTestCase()
                            .getName());
                m_oldNameCombos.add(combo);
            }
        }
        m_scroll.setMinSize(m_selectGrid.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        m_selectGrid.layout(true);
    }

    /**
     * Set a filled grid layout with given columns at the given composite.
     * @param composite The composite.
     * @param column The number of columns.
     */
    private static void setGrid(Composite composite, int column) {
        composite.setLayout(new GridLayout(column, true));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Create a label with the given text in a bold black font and white background
     * added to the given parent.
     * @param parent The parent.
     * @param text The text.
     */
    private static void createHeadLabel(Composite parent, String text) {
        StyledText styledText = new StyledText(parent,
                SWT.READ_ONLY | SWT.WRAP | SWT.CENTER);
        styledText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        styledText.setEnabled(false);
        styledText.setText(text);
        styledText.setStyleRange(new StyleRange(0, text.length(),
                null,
                //parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND),
                null,
                SWT.BOLD));
    }

    /**
     * @param text The label text to set.
     * @param parent The composite.
     * @return A new label with the given text added to the given parent.
     */
    private static Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        return label;
    }

    @Override
    public void setVisible(boolean isVisible) {
        if (isVisible) {
            // add the selectable parameter names
            createTableOfParameters(m_selectGrid);
        }
        super.setVisible(isVisible);
    }

    /**
     * Show help contend attached to wizard after selecting the ? icon,
     * or pressing F1 on Windows / Shift+F1 on Linux / Help on MAC.
     * {@inheritDoc}
     */
    public void performHelp() {
        Plugin.getHelpSystem().displayHelp(ContextHelpIds
                .SEARCH_REFACTOR_REPLACE_EXECUTION_TEST_CASE_WIZARD);
    }

}
