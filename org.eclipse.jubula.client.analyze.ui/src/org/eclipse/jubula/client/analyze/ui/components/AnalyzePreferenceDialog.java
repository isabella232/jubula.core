/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jubula.client.analyze.internal.AnalyzeParameter;
import org.eclipse.jubula.client.analyze.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class creates the AnalyzePreference dialog, which is shown, when an
 * Analyze with Parameters has been chosen. This PreferenceDialog is used to
 * adjust AnalyzeParameters
 * 
 * @author volker
 * 
 */
public class AnalyzePreferenceDialog extends Dialog {
    
    /** The ParameterDialog */
    private Dialog m_dialog;
    
    /** the cancel status */
    private boolean m_cancelStatus = true;
    
    /** The List includes the Text fields for the ParameterValues */
    private ArrayList<Text> m_textList;
    
    /**
     * the parameterValue. It is modified when the text is changed in the
     * modifyListener below
     */
    private String m_paramValue;
    
    /**
     * @param parentShell
     *            The ParentShell
     * @param parameterList
     *            The given List of AnalyzeParameters
     */
    public AnalyzePreferenceDialog(Shell parentShell,
            List<AnalyzeParameter> parameterList) {
        super(parentShell);
        createAnalyzePreferenceDialog(parameterList);
    }
    
    /**
     * @return m_dialog The Dialog
     */
    public Dialog getDialog() {
        return m_dialog;
    }

    /**
     * 
     * @param dialog The given Dialog
     */
    public void setDialog(Dialog dialog) {
        this.m_dialog = dialog;
    }
    
    /**
     * @return m_cancel The cancelStatus
     */
    public boolean getCancelStatus() {
        return m_cancelStatus;
    }

    /**
     * @param cancelStatus The given cancel-state
     */
    public void setCancelStatus(Boolean cancelStatus) {
        this.m_cancelStatus = cancelStatus;
    }

    /**
     * @return m_paramValue The Parameter Value
     */
    public String getParamValue() {
        return m_paramValue;
    }
 
    /**
     * @param paramValue The value of the AnalyzeParameter
     */
    public void setParamValue(String paramValue) {
        this.m_paramValue = paramValue;
    }

    /**
     * creates the AnalyzePreferenceDialog
     * @param parameterList
     *            The List of AnalyzeParameters
     * @return dialog The AnalyzePreferenceDialog
     */
    public Dialog createAnalyzePreferenceDialog(
            final List<AnalyzeParameter> parameterList) {
        if (parameterList.size() != 0) {

            // The AnalyzePreferenceDialog
            Dialog dialog = new Dialog(Display.getCurrent().getActiveShell()) {
                @Override
                protected Control createDialogArea(Composite parent) {
                    Composite composite = (Composite) super
                            .createDialogArea(parent);
                    createDialogContent(composite, parameterList);
                    return composite;
                }
                @Override
                protected Point getInitialSize() {
                    int height;
                    if (parameterList.size() <= 10) {
                        height = (parameterList.size() * 32) + 132;
                    } else {
                        height = 452;
                    }
                    return new Point(600, height);
                }
                @Override
                protected void okPressed() {
                    setCancelStatus(false);
                    getDialog().close();
                }
                @Override
                protected void cancelPressed() {
                    setCancelStatus(true);
                    getDialog().close();
                }
                @Override
                protected void createButtonsForButtonBar(Composite parent) {
                    ((GridLayout) parent.getLayout()).numColumns++;
                    Button defaults = new Button(parent, SWT.PUSH);
                    defaults.setData(parameterList);
                    defaults.setText(Messages.DefaultsButton);
                    defaults.addListener(SWT.Selection, new Listener() {
                        
                        public void handleEvent(Event event) {
                            restore();
                        }
                    });
                    createButton(parent, IDialogConstants.OK_ID,
                            IDialogConstants.OK_LABEL, true);
                    createButton(parent, IDialogConstants.CANCEL_ID,
                            IDialogConstants.CANCEL_LABEL, false);
                }
                @Override
                protected void configureShell(Shell newShell) {
                    super.configureShell(newShell);
                    newShell.setText(Messages.AnalyzePreferenceDialog);
                }
            };
            setDialog(dialog);
            dialog.open();
            return dialog;
        }
        setCancelStatus(false);
        return null;
    }
    
    /**
     * Creates the content of the AnalyzePreferenceDialog
     * 
     * @param composite
     *            The given Composite
     * @param parameterList
     *            The given parameterList with the AnalyzeParameters
     */
    private void createDialogContent(Composite composite,
            List<AnalyzeParameter> parameterList) {

        int width = 580;
        int height = (parameterList.size() * 28) + 40;
        composite.setLayout(new FillLayout());

        ScrolledComposite sc = new ScrolledComposite(composite, SWT.V_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(width, height + 1);

        Composite content = new Composite(sc, SWT.NONE);
        content.setLayout(new RowLayout());
        
        // has to be less than the setMinSize-height of the ScrolledComposite
        content.setSize(width, height);

        sc.setContent(content);

        // The HeaderLabel of this Dialog.
        Label header = new Label(content, SWT.NONE);
        header.setSize(600, 20);
        header.setText(Messages.AnalyzePreferenceDialogAdjustNote);

        // adds a Separator
        RowData sepData = new RowData(600, 10);
        Label sep = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        sep.setLayoutData(sepData);

        // fill the Composite with the AnalyzeParameters
        processParameters(content, parameterList);
    }

    /**
     * Gets the List with the TextObjects and resets their Texts
     * to the defaults values
     */
    private void restore() {
        
        for (int i = 0; i < getTextList().size(); i++) {
            Text t = getTextList().get(i);
            AnalyzeParameter ap = (AnalyzeParameter) t.getData();
            t.setText(ap.getDefaultValue());
        }
    }
    
    /**
     * Handles the dynamic content of the given Composite. Creates the
     * GridLayout with its components to display the parameters
     * 
     * @param cmp
     *            The given Composite
     * @param parameterList
     *            The given List of Parameters
     * @return c The Composite that which contains the Parameters
     */
    private Composite processParameters(Composite cmp,
            List<AnalyzeParameter> parameterList) {

        GridLayout grid = new GridLayout(4, false);

        Composite c = new Composite(cmp, SWT.NONE);
        c.setLayout(grid);
        
        ArrayList<Text> textList = new ArrayList<Text>();     
        setTextList(textList);
        
        for (int i = 0; i < parameterList.size(); i++) {
            AnalyzeParameter aParam = parameterList.get(i);
            
            GridData sepData = new GridData();
            sepData.widthHint = 10;
            GridData dataLeft = new GridData(400, SWT.DEFAULT);
            dataLeft.horizontalAlignment = SWT.CENTER;
            GridData dataMid = new GridData(10, SWT.DEFAULT);
            GridData dataRight = new GridData(120, SWT.DEFAULT);
            dataRight.grabExcessHorizontalSpace = false;
            
            // A Separator
            Label sepLab = new Label(c, SWT.NONE);
            sepLab.setLayoutData(sepData);
            // This Label shows the name of the AnalyzeParameter
            Label nameLabel = new Label(c, SWT.LEFT);
            nameLabel.setText(aParam.getName());
            nameLabel.setLayoutData(dataLeft);

            // This Label is used to show the ControlDecoration info-Image,
            // without using the the ControlDecoration,to show the 
            // AnalyzeParameter description as a ToolTip
            Label infoLabel = new Label(c, SWT.NONE);
            infoLabel.setLayoutData(dataMid);
            Image im = FieldDecorationRegistry
                    .getDefault()
                    .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
                    .getImage();
            infoLabel.setImage(im);
            infoLabel.setToolTipText(aParam.getDescription());
            // create the Text which is used to display the parameterValue
            Text text = new Text(c, SWT.BORDER);
            text.addListener(SWT.Verify, new Listener() {
                public void handleEvent(Event e) {
                    String enteredText = e.text;
                    char[] chars = new char[enteredText.length()];
                    enteredText.getChars(0, chars.length, chars, 0);
                    for (int i = 0; i < chars.length; i++) {
                        if (!('0' <= chars[i] && chars[i] <= '9')) {
                            e.doit = false;
                            return;
                        }
                    }
                }
            });
            text.setLayoutData(dataRight);
            text.setText(aParam.getValue());
            text.setTextLimit(20);
            text.setSize(70, 40);
            // set the AnalyzeParameter as the TextData
            text.setData(aParam);
            text.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    Text text = (Text) e.widget;
                    AnalyzeParameter param = (AnalyzeParameter) text.getData();
                    param.setModifiedValue(text.getText());
                }
            });
            // save the Text in an ArrayList
            getTextList().add(text);
        }
        return c;
    }
    
    /**
     * @return A List with the Text-Objects for the AnalyzeParameter values
     */
    public List<Text> getTextList() {
        return m_textList;
    }
    
    /**
     * @param textList The given List which includes the TextObjects
     */
    public void setTextList(List<Text> textList) {
        this.m_textList = (ArrayList<Text>) textList;
    }
}
