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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.utils.DBSchemaPropertyCreator;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.widgets.GDText;
import org.eclipse.jubula.tools.constants.SwtAUTHierarchyConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * Creates a pop up dialog to enter username and password for database connection.
 * @author BREDEX GmbH
 * @created 18.08.2005
 */
public class DBLoginDialog extends TitleAreaDialog {
    
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;

    /** number of columns = 2 */
    private static final int NUM_COLUMNS_3 = 3;

    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;

    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;

    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;

    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;

    /** horizontal span = 2 */
    private static final int HORIZONTAL_SPAN = 2;
    
    /** key for the username property for Hibernate */
    private static final String HIBERNATE_CONNECTION_USERNAME = 
        "javax.persistence.jdbc.user"; //$NON-NLS-1$
    
    /** <code>HIBERNATE_CONNECTION_PASSWORD</code> */
    private static final String HIBERNATE_CONNECTION_PASSWORD = 
        "javax.persistence.jdbc.password"; //$NON-NLS-1$ 
    
    /** The message m_text */
    private String m_message = I18n.getString("DBLoginDialog.Message");   //$NON-NLS-1$
    
    /** the username m_text field */
    private GDText m_userText;
    /** the username label */
    private Label m_userLabel;
    /** the password m_text field */
    private GDText m_pwdText;
    /** the password label */
    private Label m_pwdLabel;
    /** the schema combobox */
    private Combo m_schemaCbx;
    
    /** the username */
    private String m_user;
    /** the password */
    private String m_pwd;
    /** the schema name */
    private String m_schemaName;
    
    /** true, if selected db is embedded db */
    private boolean m_isEmbeddedOrNoSelection = false; 

    /**
     * @param parentShell The parent Shell.
     */
    public DBLoginDialog(Shell parentShell) {
        super(parentShell);
    }
    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(I18n.getString("DBLoginDialog.Title"));   //$NON-NLS-1$
        setTitleImage(IconConstants.DB_LOGIN_DIALOG_IMAGE);
        getShell().setText(I18n.getString("DBLoginDialog.Shell"));   //$NON-NLS-1$
        
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);

        Plugin.createSeparator(parent);

        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_3;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        area.setLayoutData(gridData);

        createSchemaCombobox(area);
        createUserTextField(area);
        createPasswordTextField(area);
        fillSchemaCombobox();
        
        setUserAndPwdVisible(!m_isEmbeddedOrNoSelection);
        
        Plugin.createSeparator(parent);
        
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.DB_LOGIN_ID);
        setHelpAvailable(true);
        
        return area;
    }

    /**
     * {@inheritDoc}
     */
    public int open() {
        setMessage(m_message);
        return super.open();
    }
    
    /**
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, 
        boolean defaultButton) {
        
        Button button = 
            super.createButton(parent, id, label, defaultButton);
        if (m_userText.getText().length() == 0
                || m_schemaCbx.getSelectionIndex() == -1
                || m_schemaCbx.getItemCount() == 0) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        return button;
    }
    
    /**
     * Creates the Textfield to select the user name.
     * @param area The parent composite. 
     */
    private void createUserTextField(Composite area) {
        new Label(area, SWT.NONE).setLayoutData(new GridData(GridData.FILL, 
            GridData.CENTER, false, false, HORIZONTAL_SPAN + 1, 1));
        m_userLabel = new Label(area, SWT.NONE);
        m_userLabel.setText(I18n.getString("DBLoginDialog.userLabel"));    //$NON-NLS-1$
        m_userLabel.setData(SwtAUTHierarchyConstants.WIDGET_NAME, "DBLoginDialog.userLabel"); //$NON-NLS-1$
        m_userText = new GDText(area, SWT.BORDER);
        m_userText.setData(SwtAUTHierarchyConstants.WIDGET_NAME, "DBLoginDialog.userTxf"); //$NON-NLS-1$
        GridData gridData = newGridData();
        Layout.addToolTipAndMaxWidth(gridData, m_userText);
        m_userText.setLayoutData(gridData);
        Layout.setMaxChar(m_userText);
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        m_userText.setText(store.getString(Constants.USER_KEY));
        m_userText.selectAll();
        m_userText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (modifyUsernameFieldAction()) {
                    modifyPasswordFieldAction();
                }
            }            
        });
    }
    
    /**
     * Creates the m_text field to enter the password.
     * @param area The composite. 
     */
    private void createPasswordTextField(Composite area) {
        new Label(area, SWT.NONE).setLayoutData(new GridData(GridData.FILL, 
            GridData.CENTER, false, false, HORIZONTAL_SPAN + 1, 1));
        m_pwdLabel = new Label(area, SWT.NONE);
        m_pwdLabel.setText(I18n.getString("DBLoginDialog.pwdLabel")); //$NON-NLS-1$
        m_pwdLabel.setData(SwtAUTHierarchyConstants.WIDGET_NAME, "DBLoginDialog.pwdLabel"); //$NON-NLS-1$
        m_pwdText = new GDText(area, SWT.PASSWORD | SWT.BORDER);
        m_pwdText.setData(SwtAUTHierarchyConstants.WIDGET_NAME, "DBLoginDialog.pwdTxf"); //$NON-NLS-1$
        GridData gridData = newGridData();
        Layout.addToolTipAndMaxWidth(gridData, m_pwdText);
        m_pwdText.setLayoutData(gridData);
        Layout.setMaxChar(m_pwdText);
        if (!StringUtils.isEmpty(m_userText.getText())) {
            m_pwdText.setFocus();
        }
        m_pwdText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (modifyPasswordFieldAction()) {
                    modifyUsernameFieldAction();
                }
            }            
        });
    }
    
    /**
     * Creates the Combobox to select the Database Schema.
     * @param area The parent composite. 
     */
    private void createSchemaCombobox(Composite area) {
        new Label(area, SWT.NONE).setLayoutData(new GridData(GridData.FILL, 
            GridData.CENTER, false, false, HORIZONTAL_SPAN + 1, 1));
        new Label(area, SWT.NONE).setText(I18n.getString("DBLoginDialog.ConnectionLabel"));    //$NON-NLS-1$
        m_schemaCbx = new Combo(area, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData gridData = newGridData();
        m_schemaCbx.setLayoutData(gridData);
        
    }
    
    /**
     * Fills the Combobox to select the Database Schema.
     */
    private void fillSchemaCombobox() {
        final Map<String, Properties> schemaMap = 
            DBSchemaPropertyCreator.getSchemaMap();
        
        String[] schemas = new String[schemaMap.size()];
        Set keyset = schemaMap.keySet();
        Iterator it = keyset.iterator();
        int i = 0;
        while (it.hasNext()) {
            schemas[i] = it.next().toString();
            i++;
        }
        
        m_schemaCbx.setItems(schemas);       
        
        if (schemas.length == 1) {
            m_schemaCbx.select(0);
        } else {
            IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
            int idx = m_schemaCbx.indexOf(store.getString(
                    Constants.SCHEMA_KEY));
            if (idx != -1) {
                m_schemaCbx.select(idx);
            }
        }        
        
        //check if a schema is selected
        selectSchemaCbxAction();
        //if db is embedded hide textfields
        isEmbeddedDbOrNoSchemaSelected(schemaMap);
        
        m_schemaCbx.addSelectionListener(new SelectionListener() {            
            public void widgetSelected(SelectionEvent e) {
                selectSchemaCbxAction();
                isEmbeddedDbOrNoSchemaSelected(schemaMap);
                setUserAndPwdVisible(!m_isEmbeddedOrNoSelection);
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing here
            }
        });
    }
    
    /**
     * verify if selected schema is embedded or no schema is selected
     * @param schemMap schema map
     */
    private void isEmbeddedDbOrNoSchemaSelected(
            Map<String, Properties> schemMap) {
        //if no item is selected, hide user and password field
        if (m_schemaCbx.getSelectionIndex() == -1) {
            m_isEmbeddedOrNoSelection = true;
            return;
        }
        Properties currentProps = schemMap.get(
                m_schemaCbx.getItem(m_schemaCbx.getSelectionIndex()));
        if (currentProps.containsKey(HIBERNATE_CONNECTION_USERNAME)
                && currentProps.containsKey(
                        HIBERNATE_CONNECTION_PASSWORD)) {
            m_isEmbeddedOrNoSelection = true;
            String user = currentProps.getProperty(
                    HIBERNATE_CONNECTION_USERNAME);
            String pwd = currentProps.getProperty(
                    HIBERNATE_CONNECTION_PASSWORD);
            Hibernator.setUser(user);
            Hibernator.setPw(pwd);
            m_userText.setText(user);
            m_pwdText.setText(pwd);
            enableOKButton(true);            
        } else {
            IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
            m_userText.setText(store.getString(Constants.USER_KEY));
            m_isEmbeddedOrNoSelection = false;
        }
    }
    
    /** 
     * set visible state of username and pwd 
     * @param visible true if user and pw should be visible, false otherwise
     */
    private void setUserAndPwdVisible(boolean visible) {
        m_userText.setVisible(visible);
        m_userLabel.setVisible(visible);
        m_pwdText.setVisible(visible);
        m_pwdLabel.setVisible(visible);
    }
    
    /** 
     * The action of the user name field.
     * @return false, if the user name field contents an error:
     * the user name starts or end with a blank, or the field is empty
     */
    boolean modifyUsernameFieldAction() {
        m_userText.clearSelection();
        boolean isCorrect = true;
        int serverNameLength = m_userText.getText().length();
        if ((serverNameLength == 0)
                || (m_userText.getText().startsWith(" ")) || //$NON-NLS-1$
            (m_userText.getText().charAt(
                serverNameLength - 1) == ' ')) {
            isCorrect = false;
        }
        if (isCorrect) {
            setErrorMessage(null);
        } else {
            if (serverNameLength == 0) {
                setErrorMessage(I18n.getString("DBLoginDialog.emptyUser")); //$NON-NLS-1$
            } else {
                setErrorMessage(I18n.getString("DBLoginDialog.wrongUser"));  //$NON-NLS-1$
            }
        }
        enableOKButton(isCorrect);
        return isCorrect;
    }
    
    /** 
     * The action of the password name field.
     * @return false, if the password name field contents an error:
     * the field is empty
     */
    boolean modifyPasswordFieldAction() {
        boolean isCorrect = true;
        if ((m_pwdText.getText().startsWith(" ")) //$NON-NLS-1$
            || (m_pwdText.getText().endsWith(" "))) { //$NON-NLS-1$
            
            isCorrect = false;
        }
        if (isCorrect) {
            setMessage(m_message); 
        } else {
            setErrorMessage(I18n.getString("DBLoginDialog.wrongPwd")); //$NON-NLS-1$ 
        }
        enableOKButton(isCorrect);
        return isCorrect;
    }
    
    /** 
     * show warning if no scheme selected or avaliable and disable ok button.
     */
    private void selectSchemaCbxAction() {
        boolean isCorrect = true;
        if (m_schemaCbx.getItemCount() == 0) {
            setErrorMessage(I18n.getString("DBLoginDialog.noSchemaAvailable")); //$NON-NLS-1$
            isCorrect = false;
        } else if (m_schemaCbx.getSelectionIndex() == -1) {
            setErrorMessage(I18n.getString("DBLoginDialog.noSchemaSelected")); //$NON-NLS-1$
            isCorrect = false;
        } else {
            setErrorMessage(null); 
        }
        enableOKButton(isCorrect);
    }

    /**
     * enables the OK button
     * @param enabled True, if the ok button should be enabled.
     */
    void enableOKButton(boolean enabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(enabled);
        }
    }

    /**
     * This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        m_user = m_userText.getText(); 
        m_pwd = m_pwdText.getText();
        m_schemaName = m_schemaCbx.getItem(m_schemaCbx.getSelectionIndex());
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        store.setValue(Constants.USER_KEY, m_user);
        store.setValue(Constants.SCHEMA_KEY, m_schemaName);
        setReturnCode(OK);
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
     * @return Returns username.
     */
    public String getUser() {
        return m_user;
    }
    
    /**
     * @return Returns the password.
     */
    public String getPwd() {
        return m_pwd;
    }
    
    /**
     * @return Returns the schema name.
     */
    public String getSchemaName() {
        return m_schemaName;
    }
    
    /**
     * @return true, if selected database if embedded db.
     */
    public boolean isEmbeddedDb() {
        return m_isEmbeddedOrNoSelection;
    }
}