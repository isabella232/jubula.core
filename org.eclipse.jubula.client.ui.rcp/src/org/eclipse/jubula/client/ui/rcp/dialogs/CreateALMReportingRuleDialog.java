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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.model.IALMReportingRulePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.utils.ReportRuleType;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 10.07.2014
 */
public class CreateALMReportingRuleDialog extends TitleAreaDialog {

    /** the reporting rule */
    private IALMReportingRulePO m_reportingRule;

    /** textField for the name of the rule */
    private Text m_nameText;

    /** textField for the field of the rule */
    private Text m_fieldText;

    /** textField for the value of the rule */
    private Text m_valueText;

    /**
     * The constructor.
     * 
     * @param parentShell
     *            The shell.
     * @param type
     *            The type of the rule.
     * @param reportingRule
     *            The selected AUTMain in the AUTPropertyPage.
     */
    public CreateALMReportingRuleDialog(Shell parentShell,
            IALMReportingRulePO reportingRule,
            ReportRuleType type) {

        super(parentShell);
        if (reportingRule == null) {
            m_reportingRule = PoMaker.createALMReportingRulePO(
                    StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                    type);
        } else {
            m_reportingRule = reportingRule;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.ProjectPropertyPageALMReportingRuleAddDialog);
        setMessage(Messages.
                ProjectPropertyPageALMReportingRuleAddDialogMessage);
        
        Composite composite = newComposite(parent, 2, SWT.FILL);
        newLabel(composite, Messages.ALMReportRuleName
                + StringConstants.COLON);
        m_nameText = new Text(composite, SWT.BORDER);
        newLabel(composite, Messages.ALMReportRuleField
                + StringConstants.COLON);
        m_fieldText = new Text(composite, SWT.BORDER);
        newLabel(composite, Messages.ALMReportRuleValue
                + StringConstants.COLON);
        m_valueText = new Text(composite, SWT.BORDER);
        
        GridData layoutData = new GridData(250, 20);
        m_nameText.setLayoutData(layoutData);
        m_fieldText.setLayoutData(layoutData);
        m_valueText.setLayoutData(layoutData);
        
        GridData compositeLayout = new GridData();
        compositeLayout.verticalIndent = 10;
        compositeLayout.grabExcessVerticalSpace = true;
        compositeLayout.grabExcessHorizontalSpace = true;
        composite.setLayoutData(compositeLayout);
        
        initFields();
        return composite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 275);
    }

    /**
     * Fills the textFields with the data of the model, if the edit button was
     * pressed in the AUTPropertyPage.
     */
    private void initFields() {
        m_nameText.setText(
                StringUtils.defaultString(m_reportingRule.getName()));
        m_fieldText.setText(
                StringUtils.defaultString(m_reportingRule.getFieldID()));
        m_valueText.setText(
                StringUtils.defaultString(m_reportingRule.getValue()));
    }

    /**
     * Creates a new composite.
     * 
     * @param parent The parent composite.
     * @param numColumns The number of columns for this composite.
     * @param verticalAlignment The vertical alignment of this composite.
     * @return The new composite.
     */
    private Composite newComposite(Composite parent, int numColumns, 
        int verticalAlignment) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = false;
        compositeData.verticalAlignment = verticalAlignment;
        composite.setLayoutData(compositeData);
        return composite;

    }

    /**
     * Creates a label for this page.
     * 
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    private Label newLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER,
            false, false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void okPressed() {
        m_reportingRule.setName(m_nameText.getText());
        m_reportingRule.setFieldID(m_fieldText.getText());
        m_reportingRule.setValue(m_valueText.getText());        
        super.okPressed();
    }    

    /**
     * returns the reporting rule of the dialogue
     * @return the reporting rule
     */
    public IALMReportingRulePO getRule() {
        return m_reportingRule;
    }
    
}