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
package org.eclipse.jubula.client.ui.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog for entering the comment e.g for a test result summary.
 *
 * @author BREDEX GmbH
 * @created Aug 23, 2010
 */
public class EnterCommentDialog extends AbstractValidatedDialog {
    /** observable (bindable) value for comment title */
    private WritableValue m_commentTitle;
    
    /** observable (bindable) value for comment detail */
    private WritableValue m_commentDetail;

    /**
     * <code>m_initialTitle</code>
     */
    private String m_initialTitle = null;
    
    /**
     * <code>m_initialDetail</code>
     */
    private String m_initialDetail = null;
    
    /** 
     * the validator used for validation of value correctness 
     */
    private IValidator m_validator;

    /**
     * Constructor
     * 
     * @param parentShell
     *            The Shell to use as a parent for the dialog.
     * @param commentValidator
     *            The validator to use for the commentary values
     * @param title
     *            the initial comment title
     * @param detail
     *            the initial comment detail
     */
    public EnterCommentDialog(Shell parentShell, IValidator commentValidator,
        String title, String detail) {
        super(parentShell);
        m_validator = commentValidator;
        m_initialTitle = title;
        m_initialDetail = detail;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.EnterCommentDialogTitle);
        setMessage(Messages.EnterCommentDialogMessage);
        getShell().setText(Messages.EnterCommentDialogTitle);
        
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Composite area = new Composite(parent, SWT.BORDER);
        area.setLayoutData(gridData);
        area.setLayout(new GridLayout(2, false));

        createCommentTitleField(area);
        createCommentDetailField(area);
        
        return area;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Point getInitialSize() {
        Point shellSize = super.getInitialSize();
        return new Point(Math.max(
                convertHorizontalDLUsToPixels(450), shellSize.x),
                Math.max(convertVerticalDLUsToPixels(300),
                        shellSize.y));
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean isResizable() {
        return true;
    }

    /**
     * @param area the parent area
     */
    private void createCommentTitleField(Composite area) {
        GridData gridData;
        JBText commentTitleField = createCommentTitleText(area);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        Layout.addToolTipAndMaxWidth(gridData, commentTitleField);
        commentTitleField.setLayoutData(gridData);
        
        IObservableValue commentTitleFieldText = 
            SWTObservables.observeText(commentTitleField, SWT.Modify);
        m_commentTitle = WritableValue.withValueType(String.class);
        getValidationContext().bindValue(
                commentTitleFieldText,
                m_commentTitle,
                new UpdateValueStrategy().setAfterGetValidator(m_validator),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
        
        if (!StringUtils.isEmpty(m_initialTitle)) {
            m_commentTitle.setValue(m_initialTitle);
        }
        Layout.setMaxChar(commentTitleField, 4000);
        
        commentTitleField.selectAll();
    }
    
    /**
     * @param area the parent area
     */
    private void createCommentDetailField(Composite area) {
        GridData gridData;
        JBText commentDetailField = createCommentDetailText(area);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Layout.addToolTipAndMaxWidth(gridData, commentDetailField);
        commentDetailField.setLayoutData(gridData);
        
        IObservableValue commentDetailFieldText = 
            SWTObservables.observeText(commentDetailField, SWT.Modify);
        m_commentDetail = WritableValue.withValueType(String.class);
        
        getValidationContext().bindValue(
                commentDetailFieldText,
                m_commentDetail,
                new UpdateValueStrategy().setAfterGetValidator(m_validator),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
        
        if (!StringUtils.isEmpty(m_initialDetail)) {
            m_commentDetail.setValue(m_initialDetail);
        }
        Layout.setMaxChar(commentDetailField, 4000);
    }

    /**
     * @param area The parent for the created widgets.
     * @return the created text field.
     */
    private JBText createCommentTitleText(Composite area) {
        new Label(area, SWT.NONE).setText(
                Messages.EnterCommentDialogTitleLabel);
        return new JBText(area, SWT.SINGLE | SWT.BORDER);
    }
    
    /**
     * @param area The parent for the created widgets.
     * @return the created text field.
     */
    private JBText createCommentDetailText(Composite area) {
        new Label(area, SWT.NONE).setText(
                Messages.EnterCommentDialogDetailLabel);
        return new JBText(area, SWT.V_SCROLL | SWT.BORDER);
    }
    
    /**
     * This method must be called from the GUI thread.
     * 
     * @return the comment title
     */
    public String getCommentTitle() {
        return (String)m_commentTitle.getValue();
    }
    
    /**
     * This method must be called from the GUI thread.
     * 
     * @return the comment detail
     */
    public String getCommentDetail() {
        return (String)m_commentDetail.getValue();
    }
}
