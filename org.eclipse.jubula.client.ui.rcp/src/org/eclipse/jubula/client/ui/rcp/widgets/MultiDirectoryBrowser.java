/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.List;

/**
 * Composite with a multi-directory browser list, where directory pathes can be added,
 * edited, removed.
 *
 * @author BREDEX GmbH
 * @created Dec 3, 2015
 */
@SuppressWarnings("synthetic-access")
public class MultiDirectoryBrowser extends Composite {
    
    /**
     * Listener for the directory selection changes.
     *
     * @author BREDEX GmbH
     * @created Dec 3, 2015
     */
    public interface IMultiDirectorySelectionListener {
        /**
         * Handle a change, when a directory is added, edited, or removed
         * @param keyName name of the configuration
         * @param value value, which contains the selected directories.
         */
        public void selectedDirectoriesChanged(String keyName, String value);
    }

    /** layout for buttons */
    public static final GridData BUTTON_LAYOUT;

    static {
        BUTTON_LAYOUT = new GridData();
        BUTTON_LAYOUT.horizontalAlignment = GridData.FILL;
        BUTTON_LAYOUT.grabExcessHorizontalSpace = true;
    }

    /** gui component to display the selected directories. */
    private List m_directoryList;

    /** gui component to add directory. */
    private Button m_addElementButton;

    /** gui component to edit selected directory. */
    private Button m_editElementButton;

    /** gui component to remove selected directory. */
    private Button m_removeElementButton;

    /** list listeners */
    private Set<IMultiDirectorySelectionListener> m_listeners = 
        new HashSet<IMultiDirectorySelectionListener>();
    
    /**
     * Multi-directory browser with a list and add, edit, remove buttons.
     * 
     * @param parent
     *            The parent composite
     * @param attrId
     *            monitoring constant id
     * @param configurationValue
     *            value, which contains the current configuration
     */
    public MultiDirectoryBrowser(Composite parent, String attrId,
            String configurationValue) {
        super(parent, SWT.NONE);
       
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.heightHint = 1;
        this.setLayoutData(layoutData);
        
        m_directoryList = new List(
                parent, LayoutUtil.MULTI_TEXT_STYLE | SWT.MULTI);
        m_directoryList.setData(MonitoringConstants.MONITORING_KEY,
                attrId);
        
        GridData textGridData = new GridData();
        textGridData.horizontalAlignment = GridData.FILL;
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.heightHint = Dialog.convertHeightInCharsToPixels(
                LayoutUtil.getFontMetrics(m_directoryList), 2) + 10;
        LayoutUtil.addToolTipAndMaxWidth(textGridData,
                m_directoryList);
        m_directoryList.setLayoutData(textGridData);

        initDirectoryList(configurationValue);
        
        m_directoryList
                .addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        checkDirectoryActionButtons();
                        m_directoryList.setFocus();
                    }
                });

        Composite dirctoryActionComposite = UIComponentHelper
                .createLayoutComposite(parent, 3);

        initActionButtons(dirctoryActionComposite);
        checkDirectoryActionButtons();
    }

    /**
     * Initialize the add, edit, remove buttons.
     * @param dirctoryActionComposite parent composite
     */
    private void initActionButtons(Composite dirctoryActionComposite) {
        m_addElementButton = new Button(dirctoryActionComposite,
                SWT.PUSH);
        m_editElementButton = new Button(dirctoryActionComposite,
                SWT.PUSH);
        m_removeElementButton = new Button(dirctoryActionComposite,
                SWT.PUSH);

        m_addElementButton.setText(Messages.AUTConfigComponentElement);
        m_addElementButton.setLayoutData(BUTTON_LAYOUT);

        m_addElementButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handleSelection(false);
                checkDirectoryActionButtons();
            }
        });
        m_editElementButton.setText(Messages.AUTConfigComponentEdit);
        m_editElementButton.setLayoutData(BUTTON_LAYOUT);
        m_editElementButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleSelection(true);
                checkDirectoryActionButtons();
            }
        });

        m_removeElementButton.setText(Messages.AUTConfigComponentRemove);
        m_removeElementButton.setLayoutData(BUTTON_LAYOUT);
        m_removeElementButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleDirectoryRemove();
                checkDirectoryActionButtons();
            }

        });
    }

    /**
     * Handle directory remove action, when a directory should be removed.
     */
    private void handleDirectoryRemove() {
        int selectionIndex = m_directoryList.getSelectionIndex();
        m_directoryList
                .remove(m_directoryList.getSelection()[0]);
        if (m_directoryList.getItemCount() >= selectionIndex) {
            m_directoryList.select(selectionIndex - 1);
        }
        if (m_directoryList.getItemCount() == 1) {
            m_directoryList.select(0);
        }
        if (m_directoryList.getSelectionCount() == 0) {
            m_directoryList.select(0);
        }
        
        updateStoredValues();
    }

    /**
     * Init directory list with previously saved configuration data
     * 
     * @param configurationValue contains saved directory pathes
     */
    private void initDirectoryList(String configurationValue) {
        m_directoryList.removeAll();
        if (!StringUtils.isEmpty(configurationValue)) {
            String[] directories = configurationValue
                    .split(StringConstants.SEMICOLON);
            for (int i = 0; i < directories.length; i++) {
                m_directoryList.add(directories[i]);
            }
        }
    }

    /**
     * Handle selection of directory
     * 
     * @param isEditButtonPressed
     *            if true, this is modifying a current path, if false, it is
     *            addition for a new path
     */
    private void handleSelection(boolean isEditButtonPressed) {
        int maxLength = IPersistentObject.MAX_STRING_LENGTH
                - getDirectoriesLength();
        if (maxLength < 1) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_TOO_LONG_CLASSPATH,
                    new Object[] { IPersistentObject.MAX_STRING_LENGTH }, null);
            return;
        }

        String oldDirectoryPath = StringConstants.EMPTY;
        if (isEditButtonPressed) {
            oldDirectoryPath = m_directoryList.getSelection()[0];
        }

        DirectoryDialog directoryDialog = new DirectoryDialog(getShell(),
                SWT.APPLICATION_MODAL | SWT.ON_TOP);

        String directory = null;
        directoryDialog.setMessage(Messages.AUTConfigComponentSelectDir);
        String filterPath = Utils.getLastDirPath();
        if (oldDirectoryPath != null) {
            File path = new File(oldDirectoryPath);
            if (path.exists()) {
                try {
                    filterPath = path.getCanonicalPath();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }

        directoryDialog.setFilterPath(filterPath);
        directory = directoryDialog.open();

        if (directory != null) {
            if (isEditButtonPressed) {
                m_directoryList.remove(oldDirectoryPath);
                m_directoryList.add(directory);
            } else {
                m_directoryList.add(directory);
            }
            
            updateStoredValues();
        }
    }

    /**
     * invoke the listeners, whiches are responsible to persist changes.
     */
    private void updateStoredValues() {
        fireListChanged(
                String.valueOf(m_directoryList
                        .getData(MonitoringConstants.MONITORING_KEY)),
                getDirectoryPathes());
    }

    /**
     * returns the length of the concatenated directory names
     * 
     * @return int
     */
    private int getDirectoriesLength() {
        String directories = StringConstants.EMPTY;
        for (int i = 0; i < m_directoryList.getItemCount(); i++) {
            directories = directories.concat(
                    m_directoryList.getItem(i) + StringConstants.SEMICOLON);
        }
        return directories.length();
    }

    /**
     * check and set the enabled state of directory action buttons
     */
    private void checkDirectoryActionButtons() {
        if (m_directoryList.getItemCount() == 0) {
            m_removeElementButton.setEnabled(false);
            m_editElementButton.setEnabled(false);
            return;
        }
        if (m_directoryList.getSelectionCount() > 0) {
            m_removeElementButton.setEnabled(true);
            m_editElementButton.setEnabled(true);
        } else {
            m_removeElementButton.setEnabled(false);
            m_editElementButton.setEnabled(false);
        }
    }

    /**
     * get the multiple directory list in concatenated format, separated by semicolon
     * 
     * @return directory list concatenated into one string, separated
     *         by semicolon
     */
    private String getDirectoryPathes() {

        String directoryList = StringConstants.EMPTY;

        for (int i = 0; i < m_directoryList.getItemCount(); i++) {

            directoryList = directoryList.concat(
                    m_directoryList.getItem(i) + StringConstants.SEMICOLON);
        }
        if (!StringConstants.EMPTY.equals(directoryList)) {
            // cut off the last semicolon
            directoryList = directoryList.substring(0,
                    directoryList.length() - 1);
        }

        return directoryList;
    }

    /**
     * Informs all listeners that the directory list has been modified.
     * 
     * @param key
     *            name of the configuration
     * @param value
     *            value, which contains the selected directories.
     */
    private void fireListChanged(String key, String value) {
        for (IMultiDirectorySelectionListener listener : m_listeners) {
            listener.selectedDirectoriesChanged(key, value);
        }
    }
    
    /**
     * 
     * @param listener
     *            The listener to add.
     */
    public void addListModifiedListener(
            IMultiDirectorySelectionListener listener) {
        m_listeners.add(listener);
    }
}
