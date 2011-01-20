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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.widgets.CheckedIntText;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
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


/**
 * @author BREDEX GmbH
 * @created Jul 3, 2007
 */
public class VersionDialog extends TitleAreaDialog {
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;
    /** number of columns = 4 */
    private static final int NUM_COLUMNS_4 = 4;
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
    /** the major version textfield */
    private JBText m_majorVersionField;
    /** the minor version textfield */
    private JBText m_minorVersionField;
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
    /** maximum length of input */
    private int m_length = 255;

    /**
     * @param parentShell The parent shell.
     * @param title The name of the title.
     * @param greatestMajor The major number of the highest version for the current project.
     * @param greatestMinor The minor number of the highest version for the current project.
     * @param message The message.
     * @param majorLabel The label of the major version number textfield.
     * @param minorLabel The label of the minor version number textfield.
     * @param wrongVersionError The wrongNameError message.
     * @param doubleVersionError The doubleNameError message.
     * @param image The image of the dialog.
     * @param shell The name of the shell.
     */
    public VersionDialog(Shell parentShell, String title,
            Integer greatestMajor, Integer greatestMinor, String message, 
            String majorLabel, String minorLabel, String wrongVersionError, 
            String doubleVersionError, String image, String shell) {

        this(parentShell, title, greatestMajor, greatestMinor, message, 
            majorLabel, minorLabel, wrongVersionError, doubleVersionError, 
            image, shell, 255);
    }

    /**
     * @param parentShell The parent shell.
     * @param title The name of the title.
     * @param greatestMajor The major number of the highest version for the current project.
     * @param greatestMinor The minor number of the highest version for the current project.
     * @param message The message.
     * @param majorLabel The label of the major version number textfield.
     * @param minorLabel The label of the minor version number textfield.
     * @param wrongNameError The wrongNameError message.
     * @param doubleNameError The doubleNameError message.
     * @param image The image of the dialog.
     * @param shell The name of the shell.
     * @param maxLength Maximum Length of input
     */
    private VersionDialog(Shell parentShell, String title,
            Integer greatestMajor, Integer greatestMinor, String message, 
            String majorLabel, String minorLabel, String wrongNameError, 
            String doubleNameError, String image, String shell, int maxLength) {

        super(parentShell);
        m_greatestMajor = greatestMajor;
        m_greatestMinor = greatestMinor + 1;
        m_message = message;
        m_majorLabel = majorLabel;
        m_minorLabel = minorLabel;
        m_wrongVersionError = wrongNameError;
        m_doubleVersionError = doubleNameError;
        m_image = image;
        m_shell = shell;
        m_title = title;
        m_length = maxLength;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(m_title);
        setTitleImage(Plugin.getImage(m_image));
        getShell().setText(m_shell);
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        Plugin.createSeparator(parent);
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
        Plugin.createSeparator(parent);
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
        new Label(area, SWT.NONE).setText(m_majorLabel);
        m_majorVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            false, 0, Integer.MAX_VALUE);
        GridData gridData = newGridData();
        Layout.addToolTipAndMaxWidth(gridData, m_majorVersionField);
        m_majorVersionField.setLayoutData(gridData);
        m_majorVersionField.setText(String.valueOf(m_greatestMajor));
        Layout.setMaxChar(m_majorVersionField, m_length);
        
        m_majorVersionField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (getButton(IDialogConstants.OK_ID) != null) {
                    modifyVersionFieldAction();
                }
            }
        });

        new Label(area, SWT.NONE).setText(m_minorLabel);
        m_minorVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            false, 0, Integer.MAX_VALUE);
        gridData = newGridData();
        Layout.addToolTipAndMaxWidth(gridData, m_minorVersionField);
        m_minorVersionField.setLayoutData(gridData);
        m_minorVersionField.setText(String.valueOf(m_greatestMinor));
        Layout.setMaxChar(m_minorVersionField, m_length);
        
        m_minorVersionField.addModifyListener(new ModifyListener() {
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
        dialog.setText(I18n.getString("InputDialog.selectJRE"));  //$NON-NLS-1$
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
        boolean isCorrect = false;
        try {
            int maj = Integer.parseInt(m_majorVersionField.getText());
            int min = Integer.parseInt(m_minorVersionField.getText());
            isCorrect = ((maj > 0 && min >= 0) || (maj >= 0 && min > 0));
        } catch (NumberFormatException nfe) {
            // Do nothing, the input is not correct and isCorrect remains false
        }

        if (isCorrect) {
            enableOKButton();
            if (!isInputAllowed()) {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
                setErrorMessage(m_doubleVersionError);
                isCorrect = false;
            }
        } else if (m_majorVersionField.getText().trim().length() == 0
            || m_minorVersionField.getText().trim().length() == 0) {
            
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            setErrorMessage(
                I18n.getString("VersionDialog.emptyField", true)); //$NON-NLS-1$
            isCorrect = false;
        } else {
            
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            setErrorMessage(m_wrongVersionError);
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
            m_majorVersionNumber = Integer.parseInt(
                m_majorVersionField.getText());
            m_minorVersionNumber = Integer.parseInt(
                m_minorVersionField.getText());
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
     * @return Returns the major version number.
     */
    public Integer getMajorVersionNumber() {
        return m_majorVersionNumber;
    }

    /**
     * @return Returns the minor version number.
     */
    public Integer getMinorVersionNumber() {
        return m_minorVersionNumber;
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
            value = Integer.parseInt(m_minorVersionField.getText());
        } catch (NumberFormatException nfe) {
            // FIXME zeb Handle error
        }
        return value;
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
