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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.actions.OpenProjectAction;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.ServerManager;
import org.eclipse.jubula.client.ui.utils.ServerManager.Server;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 01.08.2005
 */
@SuppressWarnings("synthetic-access")
public class NewServerPortDialog extends TitleAreaDialog {

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
    
    /** The message m_text */
    private String m_message = Messages.NewServerPortDialogMessage;
    
    /** the sever name combobox */
    private Combo m_serverCombo;
    /** the port m_text field */
    private IntegerFieldEditor m_portText;
    /** the ManageJre button */
    private Button m_jreButton = null;
    /** the selected server */
    private Server m_server;
    /** selection listener */
    private WidgetSelectionListener m_selectionListener = 
        new WidgetSelectionListener();
    /** the actual serverList */
    private ServerManager m_serverMgr;

    /**
     * @param parentShell The parent shell.
     * @param server server to display
     */
    public NewServerPortDialog(Shell parentShell, Server server) {
        super(parentShell);
        if (m_server == null) {
            m_server = new Server(StringConstants.EMPTY, (-1));
        } else {
            m_server = server;
        }
        m_serverMgr = ServerManager.getInstance();
    }
    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(Messages.NewServerPortDialogTitle);
        setTitleImage(IconConstants.SERVER_PORT_DIALOG_IMAGE);
        getShell().setText(Messages.NewServerPortDialogShellTitle);
        
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
        
        createServerCombo(area);
        fillServerCombo();
        createPortField(area);
        
        addListener();
        modifyServerNameFieldAction();
        
        Plugin.createSeparator(parent);
        
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.SERVER_PORT_ID);
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
     * @param area The parent composite. Creates the combobox to select the server name.
     */
    private void createServerCombo(Composite area) {
        new Label(area, SWT.NONE).setLayoutData(new GridData(GridData.FILL, 
            GridData.CENTER, false, false, HORIZONTAL_SPAN + 1, 1));
        new Label(area, SWT.NONE).setText(
                Messages.NewServerPortDialogServerLabel);
        m_serverCombo = new Combo(area, SWT.SINGLE | SWT.BORDER);
        GridData gridData = newGridData();
        Layout.addToolTipAndMaxWidth(gridData, m_serverCombo);
        m_serverCombo.setLayoutData(gridData);
        m_jreButton = new Button(area, SWT.PUSH);
        m_jreButton.setText(Messages.NewServerPortDialogManageJRE);
        m_jreButton.setLayoutData(buttonLayoutData());
    }
    
    /**
     * @param area The composite. Creates the m_text field to enter the port number.
     */
    private void createPortField(Composite area) {
        newLabel(area, NLS.bind(Messages.NewServerPortDialogPortLabel,
                new Object[] {
                    Constants.MIN_PORT_NUMBER, Constants.MAX_PORT_NUMBER}));
        m_portText = new IntegerFieldEditor(StringConstants.EMPTY,
                NLS.bind(Messages.NewServerPortDialogPortLabel,
                        new Object[] { Constants.MIN_PORT_NUMBER,
                            Constants.MAX_PORT_NUMBER }), area,
                (StringConstants.EMPTY + Constants.MAX_PORT_NUMBER).length()) {

            /**
             * {@inheritDoc}
             * @param parent
             */
            protected void createControl(Composite parent) {
                GridLayout layout = new GridLayout();
                layout.numColumns = NUM_COLUMNS_4;
                layout.marginWidth = 0;
                layout.marginHeight = 0;
                parent.setLayout(layout);
                doFillIntoGrid(parent, layout.numColumns);
            }

            /**
             * {@inheritDoc}
             */
            protected void doFillIntoGrid(Composite parent, int numColumns) {
                getLabelControl(parent).dispose();
                Text textField = getTextControl(parent);
                GridData gd = new GridData();
                GC gc = new GC(textField);
                try {
                    Point extent = gc.textExtent("X"); //$NON-NLS-1$
                    gd.widthHint = (StringConstants.EMPTY
                            + Constants.MAX_PORT_NUMBER).length() * extent.x;
                } finally {
                    gc.dispose();
                }
                textField.setLayoutData(gd);
            }
        };
        m_portText.setValidRange(Constants.MIN_PORT_NUMBER, 
                Constants.MAX_PORT_NUMBER);
        if (Integer.valueOf(-1).equals(m_server.getPort())) {
            m_portText.setStringValue(StringConstants.EMPTY);
        } else {
            m_portText.setStringValue(m_server.getPort().toString());
        }
    }

    /**
     * Creates a label for this page.
     * @param text The label text to set.
     * @param parent The composite.
     */
    private void newLabel(Composite parent, String text) {
        new Label(parent, SWT.NONE).setLayoutData(new GridData(GridData.FILL, 
            GridData.CENTER, false, false, HORIZONTAL_SPAN + 1, 1));
        new Label(parent, SWT.NONE).setText(text);
    }

    /** 
     * @return A GridData for the buttons. 
     */
    private GridData buttonLayoutData() {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        return gridData;
    }

    /**
     * The action of the integer field editors.
     * 
     * @param e The VerifyEvent.
     */
    void verifyIntegerFieldAction(VerifyEvent e) {
        e.doit = "0123456789" //$NON-NLS-1$
            .indexOf(e.text) >= 0;
        checkCompleteness(e.doit);
    }
    
    /** 
     * The action of the server name field.
     * @return false, if the server name field contents an error:
     * the project name starts or end with a blank, or the field is empty
     */
    boolean modifyServerNameFieldAction() {
        boolean isCorrect = true;
        int serverNameLength = m_serverCombo.getText().length();
        if ((serverNameLength == 0)
                || (m_serverCombo.getText().startsWith(" ")) //$NON-NLS-1$
                || (m_serverCombo.getText().charAt(
                        serverNameLength - 1) == ' ')) {
            isCorrect = false;
        }
        enableOKButton(isCorrect);
        if (isCorrect) {
            setMessage(Messages.ServerPreferencePageTitle);
            setErrorMessage(null);
            checkCompleteness(false);
        } else {
            if (serverNameLength == 0) {
                setErrorMessage(Messages.ServerPreferencePageEmptyServerName);
            } else {
                setErrorMessage(Messages.ServerPreferencePageWrongServerName);
            }
        }
        return isCorrect;
    }
    
    /**
     * Sets the "Next>"-button true, if all fields are filled in correctly.
     * @param isPortFieldVerified true, if the port field was verified.
     */
    void checkCompleteness(boolean isPortFieldVerified) {
        if (!m_portText.isValid()) {
            setErrorMessage(NLS.bind(Messages.NewServerPortDialogPortError,
                new Object[] {Constants.MIN_PORT_NUMBER,
                    Constants.MAX_PORT_NUMBER}));
            enableOKButton(false);
            return;
        }
        setErrorMessage(null);
        enableOKButton(true);
        if (isPortFieldVerified) {
            modifyServerNameFieldAction();
        }
    }

    /**
     * handles the click event for manage JRE Button
     */
    private void handleJreButtonEvent() {
        String serverName = m_serverCombo.getText();
        if (serverName != null && !serverName.equals(StringConstants.EMPTY)) {
            // copy current jreList
            List <String> jreList = 
                new ArrayList<String>(m_serverMgr.getJREs(serverName));
            ComboBoxDialog dialog = new ComboBoxDialog(Plugin.getShell(),
                jreList, Messages.GDProblemViewMessage,
                Messages.GDProblemViewTitle,
                IconConstants.PROJECT_DIALOG_IMAGE,
                Messages.GDProblemViewShellTitle,
                Messages.GDProblemViewLabel);
            Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.JRE_CHOOSE_DIALOG);
            dialog.setHelpAvailable(true);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                if (dialog.getSelectionIndex() == 0) { // create
                    CommandHelper.executeCommand(
                            CommandIDs.NEW_PROJECT_COMMAND_ID);
                } else if (dialog.getSelectionIndex() == 1) { // import
                    CommandHelper.executeCommand(
                            CommandIDs.IMPORT_PROJECT_COMMAND_ID);
                } else if (dialog.getSelectionIndex() == 2) { // open
                    OpenProjectAction.getAction().runWithEvent(new Event());
                }
            }
        }
    }

    /**
     * enables the OK button
     * @param enabled True, if the ok button should be enabled.
     */
    private void enableOKButton(boolean enabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(enabled);
        }
    }

    /**
     * This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        m_server.setName(m_serverCombo.getText());
        m_server.setPort(Integer.valueOf(m_portText.getStringValue()));
        setReturnCode(OK);
        close();
    }

    /**
     * Creates a new GridData.
     * @return grid data
     */
    GridData newGridData() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = HORIZONTAL_SPAN - 2;
        return gridData;
    }

    /**
     * Fills the combo box with all available server names
     */
    private void fillServerCombo() {
        m_serverCombo.removeAll();
        for (Server server : m_serverMgr.getServers()) {
            m_serverCombo.add(server.getName());
        }
    }
    /**
     * @return Returns the server.
     */
    public String getServer() {
        if (Constants.LOCALHOST2.equals(m_server.getName())) { 
            m_server.setName(Constants.LOCALHOST1); 
        } 
        return m_server.getName().toLowerCase();
    }
    
    /**
     * Adds listeners.
     */
    private void addListener() {
        m_jreButton.addSelectionListener(m_selectionListener);
        m_serverCombo.addSelectionListener(m_selectionListener);
        m_serverCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyServerNameFieldAction();
            }            
        });
        m_portText.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                checkCompleteness(true);
            }
        });
    }
    
    
    /**
     * @author BREDEX GmbH
     * @created 08.12.2005
     */
    private class WidgetSelectionListener implements SelectionListener {
        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_serverCombo)) {
                checkCompleteness(false);
                return;
            } else if (o.equals(m_jreButton)) {
                handleJreButtonEvent();
                return;
            }
            Assert.notReached(Messages.EventActivatedUnknownWidget
                + StringConstants.LEFT_PARENTHESES + o 
                + StringConstants.RIGHT_PARENTHESES + StringConstants.DOT);    
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            Object o = e.getSource();
            Assert.notReached(Messages.EventActivatedUnknownWidget
                    + StringConstants.LEFT_PARENTHESES + o 
                    + StringConstants.RIGHT_PARENTHESES + StringConstants.DOT);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, 
            boolean defaultButton) {
        
        Button button = super.createButton(parent, id, label, defaultButton);
        if (button.getData().equals(Integer.valueOf(0))) {
            button.setEnabled(false);
        }
        modifyServerNameFieldAction();
        return button;
    }
    /**
     * @return Returns the port.
     */
    public Integer getPort() {
        return m_server.getPort();
    }
}