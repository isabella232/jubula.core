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

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
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
     * Set a filled grid layout with given columns at the given composite.
     * @param composite The composite.
     * @param column The number of columns.
     */
    private static void setGrid(Composite composite, int column) {
        composite.setLayout(new GridLayout(column, true));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Create the group including the table for matching the parameters
     * with combo boxes.
     * @param parent The parent.
     */
    private void createGroupForSelectingParameters(Composite parent) {
        Group groupSelection = new Group(parent, SWT.NONE);
        setGrid(groupSelection, 1);

        ScrolledComposite scroll = new ScrolledComposite(
                groupSelection, SWT.V_SCROLL | SWT.H_SCROLL);
        setGrid(scroll, 1);
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);

        Composite selectGrid = new Composite(scroll, SWT.NONE);
        setGrid(selectGrid, 2);

        createHeadLabel(selectGrid, Messages
                .ReplaceTCRWizard_matchParameterNames_newParameter);
        createHeadLabel(selectGrid, Messages
                .ReplaceTCRWizard_matchParameterNames_oldParameter);

        createTableOfParameters(selectGrid);

        scroll.setContent(selectGrid);
        scroll.setMinSize(selectGrid.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * Create the table of parameters showing the new parameters in the left
     * column and the combo boxes in the right column.
     * @param parent The parent composite with a grid layout of 2 columns.
     */
    private void createTableOfParameters(Composite parent) {
        List<IParamDescriptionPO> paramDescList = m_replaceExecTestCasesData
                .getOldSpecTestCase()
                .getParameterList();
        for (IParamDescriptionPO paramDesc: paramDescList) {
            createLabel(parent,
                    GeneralLabelProvider.getTextWithBrackets(paramDesc));
            ControlDecoration warning = addWarningDecorator(
                createLabel(parent, Messages
                    .ReplaceTCRWizard_matchParameterNames_warningNoSameType),
                    Messages
                    .ReplaceTCRWizard_matchParameterNames_warningNoSameTypeDesc
            );
        }
    }

    /**
     * @param composite The composite.
     * @param message The ID of the warning decoration.
     * @return The warning control decoration added to the given control.
     */
    private ControlDecoration addWarningDecorator(
            Control composite, String message) {
        GridData grid = new GridData(GridData.BEGINNING, GridData.CENTER,
                false , false, 1, 1);
        //grid.horizontalIndent = 10;
        composite.setLayoutData(grid);
        ControlDecoration warningDecoration = ControlDecorator.createWarning(
                composite, SWT.TRAIL, message);
        return warningDecoration;

    }

    /**
     * @param parent The parent.
     * @param text The text.
     */
    private void createHeadLabel(Composite parent, String text) {
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
     * Creates a label for this page.
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    private static Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        return label;
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
