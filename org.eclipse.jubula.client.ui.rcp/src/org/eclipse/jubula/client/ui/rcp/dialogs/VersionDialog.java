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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedIntText;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;



/**
 * @author BREDEX GmbH
 * @created Jul 3, 2007
 */
public class VersionDialog extends TitleAreaDialog {
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;
    /** number of columns = 4 */
    private static final int NUM_COLUMNS_4 = 10;
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;
    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;
    /** horizontal span = 3 */
    private static final int HORIZONTAL_SPAN = 3;
    /** int value of major version field */
    private Integer m_majorVersionNumber = null;
    /** int value of minor version field */
    private Integer m_minorVersionNumber = null;
    /** int value of micro version field */
    private Integer m_microVersionNumber = null;
    /** String value of qualifier version field */
    private String m_versionQualifier = null;
    /** the major version textfield */
    private Text m_majorVersionField;
    /** the minor version textfield */
    private Text m_minorVersionField;
    /** the micro version textfield */
    private Text m_microVersionField;
    /** the qualifier version textfield */
    private Text m_versionQualifierField;
    /** the message depends on the object that is selected */
    private String m_message = StringConstants.EMPTY;
    /** the errormessage depends on the object that is selected */
    private String m_wrongVersionError = StringConstants.EMPTY;
    /** the m_doubleNameError depends on the object that is selected */
    private String m_doubleVersionError = StringConstants.EMPTY;
    /** the major label depends on the object that is selected */
    private String m_majorLabel = StringConstants.EMPTY;
    /** the minor label depends on the object that is selected */
    private String m_minorLabel = StringConstants.EMPTY;
    /** the image depends on the object that is selected */
    private String m_image = StringConstants.EMPTY;
    /** the shell depends on the object that is selected */
    private String m_shell = StringConstants.EMPTY;
    /** the title depends on the object that is selected */
    private String m_title = StringConstants.EMPTY;
    /** the major number of the highest version */
    private Integer m_greatestMajor = null;
    /** the minor number of the highest version */
    private Integer m_greatestMinor = null;
    /** the micro number of the highest version */
    private Integer m_greatestMicro = null;
    /** the qualifier of the highest version */
    private String m_greatestQualifier = null;
    /** maximum length of input */
    private int m_length = 255;

    /**
     * @param parentShell The parent shell.
     * @param title The name of the title.
     * @param greatestVersion is the greatest existing version
     * @param message The message.
     * @param majorLabel The label of the major version number textfield.
     * @param minorLabel The label of the minor version number textfield.
     * @param wrongNameError The wrongNameError message.
     * @param doubleNameError The doubleNameError message.
     * @param image The image of the dialog.
     * @param shell The name of the shell.
     */
    public VersionDialog(Shell parentShell, String title,
            ProjectVersion greatestVersion, String message, 
            String majorLabel, String minorLabel, String wrongNameError, 
            String doubleNameError, String image, String shell) {

        super(parentShell);
        setNewVersion(greatestVersion);
        m_message = message;
        m_majorLabel = majorLabel;
        m_minorLabel = minorLabel;
        m_wrongVersionError = wrongNameError;
        m_doubleVersionError = doubleNameError;
        m_image = image;
        m_shell = shell;
        m_title = title;
        m_length = 255;
    }
    
    /**
     * 
     * @param greatestVersion the greatest existing projectVersion
     */
    private void setNewVersion(ProjectVersion greatestVersion) {
        m_greatestMajor = greatestVersion.getMajorNumber();
        m_greatestMinor = greatestVersion.getMinorNumber();
        m_greatestMicro = greatestVersion.getMicroNumber();
        m_greatestQualifier = greatestVersion.getVersionQualifier();
        if (m_greatestMajor != null && m_greatestMinor == null) {
            m_greatestMajor += 1;
        }
        if (m_greatestMinor != null && m_greatestMicro == null) {
            m_greatestMinor += 1;
        }
        if (m_greatestMicro != null) {
            m_greatestMicro += 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(m_title);
        setTitleImage(IconConstants.getImage(m_image));
        getShell().setText(m_shell);
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        LayoutUtil.createSeparator(parent);
        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_4;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        area.setLayoutData(gridData);
        createVersionFields(area);
        createAdditionalComponents(area);
        LayoutUtil.createSeparator(parent);
        return area;
    }
    
    /**
     * Sets the shell style bits. This method has no effect after the shell iscreated.
     * <p>
     * The shell style bits are used by the framework method
     * <code>createShell</code> when creating this window's shell.
     * </p>
     * @param style the new shell style bits
     */
    public void setStyle(int style) {
        setShellStyle(style);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void initializeBounds() {
        super.initializeBounds();
        modifyVersionFieldAction();
        setMessage(m_message);
    }

    /**
     * @param area The composite. creates the text field to edit the TestSuite name.
     */
    private void createVersionFields(Composite area) {
        new Label(area, SWT.NONE).setText(Messages.CreateNewProjectVersionActionVersionNumbers);
        m_majorVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            true, 0, Integer.MAX_VALUE);
        GridData gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_majorVersionField);
        m_majorVersionField.setLayoutData(gridData);
        m_majorVersionField.setText(m_greatestMajor != null ? String
                .valueOf(m_greatestMajor) : StringConstants.EMPTY);
        LayoutUtil.setMaxChar(m_majorVersionField, m_length);
        m_majorVersionField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (getButton(IDialogConstants.OK_ID) != null) {
                    modifyVersionFieldAction();
                }
            }
        });
        
//        new Label(area, SWT.NONE).setText(m_minorLabel);
        m_minorVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            true, 0, Integer.MAX_VALUE);
        gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_minorVersionField);
        m_minorVersionField.setLayoutData(gridData);
        m_minorVersionField.setText(m_greatestMinor != null ? String
                .valueOf(m_greatestMinor) : StringConstants.EMPTY);
        LayoutUtil.setMaxChar(m_minorVersionField, m_length);
        m_minorVersionField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (getButton(IDialogConstants.OK_ID) != null) {
                    modifyVersionFieldAction();
                }
            }
        });
        
//        new Label(area, SWT.NONE).setText(
//                Messages.CreateNewProjectVersionActionMicroLabel);
        m_microVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            true, 0, Integer.MAX_VALUE);
        gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_microVersionField);
        m_microVersionField.setLayoutData(gridData);
        m_microVersionField.setText(m_greatestMicro != null ? String
                .valueOf(m_greatestMicro) : StringConstants.EMPTY);
        LayoutUtil.setMaxChar(m_microVersionField, m_length);
        m_microVersionField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (getButton(IDialogConstants.OK_ID) != null) {
                    modifyVersionFieldAction();
                }
            }
        });
        
        new Label(area, SWT.NONE).setText(
                Messages.CreateNewProjectVersionActionQualifierLabel);
        m_versionQualifierField = new Text(area, SWT.SINGLE | SWT.BORDER);
        gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_versionQualifierField);
        m_versionQualifierField.setLayoutData(gridData);
        m_versionQualifierField.setText(StringUtils.defaultIfBlank(
                m_greatestQualifier, StringConstants.EMPTY));
        LayoutUtil.setMaxChar(m_versionQualifierField, m_length);
        m_versionQualifierField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (getButton(IDialogConstants.OK_ID) != null) {
                    modifyVersionFieldAction();
                }
            }
        });
        
        

    }
    
    /**
     * Handles the event of the button.
     */
    void handleButtonEvent() {
        FileDialog dialog = new FileDialog(Plugin.getShell(),
            SWT.APPLICATION_MODAL);
        dialog.setFilterPath(Utils.getLastDirPath());
        dialog.setText(Messages.InputDialogSelectJRE);
        String path = dialog.open();
        if (path != null) {
            Utils.storeLastDirPath(dialog.getFilterPath());
            m_majorVersionField.setText(path);
        }
    }

    /**
     * the action of a version field
     * @return false, if one of the fields contains an error
     */
    boolean modifyVersionFieldAction() {        
        boolean isCorrect = checkIfVersionsAreCorrect();
        checkAndModifyEnablementOfFields();
        if (isCorrect) {
            enableOKButton();
            if (!isInputAllowed()) {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
                setErrorMessage(m_doubleVersionError);
                isCorrect = false;
            }
        } else {
            
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            setErrorMessage(m_wrongVersionError);
        }
        return isCorrect;
    }
    /**
     * Enables and disables version number fields so that the number sequence is correct
     */
    private void checkAndModifyEnablementOfFields() {
        if (StringUtils.isBlank(m_majorVersionField.getText())) {
            m_minorVersionField.setEnabled(false);
            m_microVersionField.setEnabled(false);
        } else {
            m_minorVersionField.setEnabled(true);
        }
        if (StringUtils.isBlank(m_minorVersionField.getText())
                || !m_minorVersionField.isEnabled()) {
            m_microVersionField.setEnabled(false);
        } else {
            m_microVersionField.setEnabled(true);
        }

    }

    /**
     * Checks for some conditions which are not correct
     * @return true if everything is okay
     */
    private boolean checkIfVersionsAreCorrect() {
        boolean isCorrect = false;
        try {
            isCorrect = (StringUtils.isNotBlank(m_majorVersionField.getText())
            || StringUtils.isNotBlank(m_versionQualifierField.getText()));
            if (isCorrect && StringUtils.isNotBlank(
                    m_minorVersionField.getText())) {
                isCorrect = StringUtils.isNotBlank(
                        m_majorVersionField.getText());
            }
            if (isCorrect && StringUtils.isNotBlank(
                    m_microVersionField.getText())) {
                isCorrect = StringUtils.isNotBlank(
                        m_minorVersionField.getText())
                        && StringUtils.isNotBlank(
                                m_majorVersionField.getText());
            }

        } catch (NumberFormatException nfe) {
            // Do nothing, the input is not correct and isCorrect remains false
        }
        return isCorrect;
    }

    /**
     * @return False, if the input name already exists.
     */
    protected boolean isInputAllowed() {
        return true;
    }

    /**
     * enables the OK button and makes a non-error title message
     */
    private void enableOKButton() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        setErrorMessage(null);
    }

    /**
     * This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        if (!modifyVersionFieldAction()) {
            return;
        }
        try {
            if (m_majorVersionField.getText().length() > 0) {
                m_majorVersionNumber = Integer.parseInt(m_majorVersionField
                        .getText());
            }
            if (m_minorVersionField.getText().length() > 0) {
                m_minorVersionNumber = Integer.parseInt(m_minorVersionField
                        .getText());
            }
            if (m_microVersionField.getText().length() > 0) {
                m_microVersionNumber = Integer.parseInt(m_microVersionField
                        .getText());
            }
            if (m_versionQualifierField.getText().length() > 0) {
                m_versionQualifier = m_versionQualifierField.getText();
            }
            setReturnCode(OK);
        } catch (NumberFormatException nfe) {
            setReturnCode(CANCEL);
        }
        close();
    }

    /**
     * Creates a new GridData.
     * @return grid data
     */
    private GridData newGridData() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = HORIZONTAL_SPAN;
        return gridData;
    }

    /**
     * @return a projectVersion generated from the version numbers
     */
    public ProjectVersion getProjectVersion() {
        return new ProjectVersion(m_majorVersionNumber, m_minorVersionNumber,
                m_microVersionNumber, m_versionQualifier);
    }

    /**
     * @return Returns the text of the input field.
     */
    public Integer getMajorFieldValue() {
        Integer value = null;
        try {
            value = Integer.parseInt(m_majorVersionField.getText());
        } catch (NumberFormatException nfe) {
            // FIXME zeb Handle error
        }
        return value;
    }

    /**
     * @return Returns the text of the input field.
     */
    public Integer getMinorFieldValue() {
        Integer value = null;
        try {
            if (m_minorVersionField.isEnabled()) {
                value = Integer.parseInt(m_minorVersionField.getText());
            }
        } catch (NumberFormatException nfe) {
            // FIXME zeb Handle error
        }
        return value;
    }
    
    /**
     * @return Returns the text of the input field.
     */
    public Integer getMicroFieldValue() {
        Integer value = null;
        try {
            if (m_microVersionField.isEnabled()) {                
                value = Integer.parseInt(m_microVersionField.getText());
            }
        } catch (NumberFormatException nfe) {
            // Nothing to handle
        }
        return value;
    }
    
    /**
     * @return Returns the text of the input field.
     */
    public String getQualifierFieldValue() {
        if (StringUtils.isBlank(m_versionQualifierField.getText())) {
            return null;
        }
        return StringUtils.trim(m_versionQualifierField.getText());
    }

    /**
     * @param text the text to set in the input field
     */
    protected void setInputFieldText(String text) {
        m_majorVersionField.setText(text);
    }
    
    /**
     * Subclasses can add new guiComponents to the given layout.
     * @param parent the parent composite
     */
    protected void createAdditionalComponents(Composite parent) {
        parent.setEnabled(true); // placeholder
    }

}
