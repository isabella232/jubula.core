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
package org.eclipse.jubula.client.ui.wizards.pages;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.archive.businessprocess.FileStorageBP;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.ImportFileBP.IProjectImportInfoProvider;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.provider.GDControlDecorator;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.widgets.GDText;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created May 19, 2010
 */
public class ImportProjectsWizardPage extends WizardPage 
        implements IProjectImportInfoProvider {

    /** Bit set for importing all */
    public static final int IMPORT_ALL = 0;
    /** Bit set for importing testcases */
    public static final int IMPORT_TESTCASES = 1; 
    /** Bit set for importing AUT */
    public static final int IMPORT_AUTS = 2; 

    /** the logger */
    private static Log log = LogFactory.getLog(ImportProjectsWizardPage.class);

    /** number of colums in GridLayout */
    private static final int NUM_COLUMS = 6; 

    /** TextField for FilePath */
    private GDText m_fileToAdd;
    /** All files that will be imported */
    private List m_filesToImport;
    /** button to browse for file */
    private Button m_browseButton; 
    /** button to add the currently typed file to the import list */
    private Button m_addButton; 
    /** remove button */
    private Button m_removeButton;
    /** move up button */
    private Button m_moveUpButton;
    /** move down button */
    private Button m_moveDownButton;
    /** open project checkbox */
    private Button m_openProjectCheckbox;
    /** The status of m_openProjectCheckbox */
    private boolean m_isOpenProject;
    /** radio button */
    private Button m_radio1; 
    /** radio button */
    private Button m_radio2; 
    /** checkbox for elements selector */
    private Button m_elementsCheckBox1; 
    /** checkbox for elements selector */
    private Button m_elementsCheckBox2; 
    /** fileNames */
    private String [] m_fileNames; 
    /** importTarget: <p>0 = all; <p>1 = not all */
    private int m_importTarget; 
    /** what elements should be imported */
    private int m_selectedElements;
    /** Whether the page should include "import target" options */
    private boolean m_includeTargetOptions;
    
    /**
     * Constructor
     * 
     * @param pageName The name of the page.
     */
    public ImportProjectsWizardPage(String pageName) {
        this(pageName, true);
    }

    /**
     * Constructor
     * 
     * @param pageName The name of the page.
     * @param includeTargetOptions Whether the page should include 
     *                             "import target" options (open project 
     *                             immediately after import, import specific 
     *                             parts of the project(s), etc).
     */
    public ImportProjectsWizardPage(String pageName, 
            boolean includeTargetOptions) {
        
        super(pageName);
        m_includeTargetOptions = includeTargetOptions;
    }
    
    /**
     * @param fileName the user defined filename
     */
    protected void handleFile(String fileName) {
        if (fileName != null && fileName.length() > 0) {
            File file = new File(fileName);
            if (file.exists() && file.isFile() && file.canRead()) {
                m_addButton.setEnabled(true);
                setErrorMessage(null);
            } else {
                setErrorMessage(I18n.getString(
                        "ImportProjectDialog.invalidFile", //$NON-NLS-1$
                        new Object[] { fileName }));
                m_addButton.setEnabled(false);
            }
        } else {
            setErrorMessage(null);
            m_addButton.setEnabled(false);
        }
    }

    /**
     * @param parent parent composite
     */
    private void addFileButtonComposite(Composite parent) {
        GridData gridData;
        GridLayout layout;
        Composite fileButtonComposite = new Composite(parent, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        fileButtonComposite.setLayout(layout);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        fileButtonComposite.setLayoutData(gridData);
        
        addBrowseButton(fileButtonComposite);
        addAddButton(fileButtonComposite);
    }

    /**
     * Adds all checkboxes and radio buttons at the bottom of the dialog.
     * 
     * @param parent parent composite
     */
    private void addButtons(Composite parent) {
        GridData gridData;
        m_openProjectCheckbox = new Button(parent, SWT.CHECK);
        m_openProjectCheckbox.setSelection(true);
        m_isOpenProject = true;
        m_openProjectCheckbox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                m_isOpenProject = m_openProjectCheckbox.getSelection();
            }
            
            public void widgetDefaultSelected(SelectionEvent arg0) {
                m_isOpenProject = m_openProjectCheckbox.getSelection();
            }
        });
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = NUM_COLUMS;
        gridData.grabExcessHorizontalSpace = true;
        m_openProjectCheckbox.setLayoutData(gridData);
        m_openProjectCheckbox.setText(I18n.getString("ImportProjectDialog.openProjectCheckbox")); //$NON-NLS-1$)
        DialogUtils.setWidgetName(m_openProjectCheckbox, "openProjectCheckbox"); //$NON-NLS-1$
        addBlankLine(parent);

        m_radio1 = new Button(parent, SWT.RADIO);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = NUM_COLUMS;
        gridData.grabExcessHorizontalSpace = true;
        m_radio1.setLayoutData(gridData);
        m_radio1.setText(I18n.getString("ImportProjectDialog.radio1")); //$NON-NLS-1$)
        DialogUtils.setWidgetName(m_radio1, "radio1"); //$NON-NLS-1$
        
        m_radio2 = new Button(parent, SWT.RADIO);
        m_radio2.setLayoutData(gridData);
        m_radio2.setText(I18n.getString("ImportProjectDialog.radio2")); //$NON-NLS-1$)
        DialogUtils.setWidgetName(m_radio2, "radio2"); //$NON-NLS-1$
        Group elements = new Group(parent, SWT.SHADOW_IN);
        elements.setLayout(new GridLayout());
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = NUM_COLUMS;
        gridData.verticalSpan = 5;
        gridData.grabExcessHorizontalSpace = true;
        elements.setLayoutData(gridData);
        
        elements.setText(I18n.getString("ImportProjectDialog.elements")); //$NON-NLS-1$
        m_elementsCheckBox1 = new Button(elements, SWT.CHECK);
        m_elementsCheckBox1.setText(I18n.getString("ImportProjectDialog.elementsCheckBox1")); //$NON-NLS-1$
        DialogUtils.setWidgetName(m_elementsCheckBox1, "elementsCheckBox1"); //$NON-NLS-1$

        m_elementsCheckBox2 = new Button(elements, SWT.CHECK);
        m_elementsCheckBox2.setText(I18n.getString("ImportProjectDialog.elementsCheckBox2")); //$NON-NLS-1$
        m_elementsCheckBox2.setEnabled(false);
        DialogUtils.setWidgetName(m_elementsCheckBox2, "elementsCheckBox2"); //$NON-NLS-1$
    }

    /**
     * adds the list of projects to import
     * 
     * @param parent parent composite
     */
    private void addImportList(Composite parent) {
        GridData gridData;
        m_filesToImport = new List(parent, 
            SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        gridData = new GridData();
        gridData.horizontalSpan = 5;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        gridData.heightHint = Dialog.convertHeightInCharsToPixels(
            Layout.getFontMetrics(m_filesToImport), 6);

        m_filesToImport.setLayoutData(gridData);
        m_filesToImport.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                checkListButtonEnablement();
            }
            
        });
        m_filesToImport.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                if (e.character == SWT.DEL) {
                    removeIndicesFromList(
                        m_filesToImport.getSelectionIndices());
                }
            }
            
        });
        
        m_filesToImport.addListener(SWT.Show, new Listener() {

            public void handleEvent(Event event) {
                checkCompletness();
            }
            
        });
        
        DialogUtils.setWidgetName(m_filesToImport, "filesToImport"); //$NON-NLS-1$
    }

    /**
     * adds the buttons for the import projects list
     * 
     * @param parent parent composite
     */
    private void addListButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData;
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        composite.setLayoutData(gridData);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = true;
        composite.setLayout(layout);
        
        addUpButton(composite);
        addDownButton(composite);
        addRemoveButton(composite);
        
    }

    /**
     * @param parent parent composite
     */
    private void addRemoveButton(Composite parent) {
        GridData gridData;
        m_removeButton = new Button(parent, SWT.PUSH);
        m_removeButton.setImage(IconConstants.DELETE_IMAGE_DISABLED);
        m_removeButton.setToolTipText(
            I18n.getString("ImportProjectDialog.RemoveToolTip")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_removeButton.setLayoutData(gridData);
        m_removeButton.setEnabled(false);
        m_removeButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                removeIndicesFromList(m_filesToImport.getSelectionIndices());
            }
            
        });

        DialogUtils.setWidgetName(m_removeButton, "removeButton"); //$NON-NLS-1$
    }

    /**
     * @param parent parent composite
     */
    private void addDownButton(Composite parent) {
        GridData gridData;
        m_moveDownButton = new Button(parent, SWT.PUSH);
        m_moveDownButton.setImage(IconConstants.DOWN_ARROW_DIS_IMAGE);
        m_moveDownButton.setToolTipText(
            I18n.getString("ImportProjectDialog.MoveDownToolTip")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_moveDownButton.setLayoutData(gridData);
        m_moveDownButton.setEnabled(false);
        m_moveDownButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                int [] selectedIndices = m_filesToImport.getSelectionIndices();
                Arrays.sort(selectedIndices);
                int [] newSelectedIndices = new int [selectedIndices.length];
                int greatestIndex = m_filesToImport.getItemCount() - 1;
                if (selectedIndices.length > 0 
                    && selectedIndices[selectedIndices.length - 1] 
                                       < greatestIndex) {
                    
                    for (int i = 0; i < selectedIndices.length; i++) {
                        int index = selectedIndices[i];
                        int newIndex = index + 1;
                        String item = m_filesToImport.getItem(index);
                        m_filesToImport.remove(index);
                        m_filesToImport.add(item, newIndex);
                        newSelectedIndices[i] = newIndex;
                    }
                    m_filesToImport.setSelection(newSelectedIndices);
                }
            }
            
        });
        
        DialogUtils.setWidgetName(m_moveDownButton, "moveDownButton"); //$NON-NLS-1$
    }

    /**
     * @param parent parent composite
     */
    private void addUpButton(Composite parent) {
        GridData gridData;
        m_moveUpButton = new Button(parent, SWT.PUSH);
        m_moveUpButton.setImage(IconConstants.UP_ARROW_DIS_IMAGE);
        m_moveUpButton.setToolTipText(
            I18n.getString("ImportProjectDialog.MoveUpToolTip")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_moveUpButton.setLayoutData(gridData);
        m_moveUpButton.setEnabled(false);
        m_moveUpButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                int [] selectedIndices = m_filesToImport.getSelectionIndices();
                Arrays.sort(selectedIndices);
                int [] newSelectedIndices = new int [selectedIndices.length];
                if (selectedIndices.length > 0 && selectedIndices[0] > 0) {
                    for (int i = 0; i < selectedIndices.length; i++) {
                        int index = selectedIndices[i];
                        int newIndex = index - 1;
                        String item = m_filesToImport.getItem(index);
                        m_filesToImport.remove(index);
                        m_filesToImport.add(item, newIndex);
                        newSelectedIndices[i] = newIndex;
                    }
                    m_filesToImport.setSelection(newSelectedIndices);
                }
            }
            
        });
        
        DialogUtils.setWidgetName(m_moveUpButton, "moveUpButton"); //$NON-NLS-1$
    }

    /**
     * Adds the given file names to the list of files to import.
     * 
     * @param fileNames The file names to add to the list.
     */
    private void addFilesToList(String [] fileNames) {
        String [] items = m_filesToImport.getItems();
        for (String selectedFile : fileNames) {
            boolean isAlreadyInList = false;
            for (String curItem : items) {
                if (curItem.equals(selectedFile)) {
                    isAlreadyInList = true;
                    break;
                }
            }
            
            if (!isAlreadyInList) {
                m_filesToImport.add(selectedFile);
            }
        }

        checkCompletness();
    }
    
    /**
     * Removes the items at the given indices from the list of files to import.
     * 
     * @param indices The indices of the items to remove from the list.
     */
    private void removeIndicesFromList(int [] indices) {
        m_filesToImport.remove(indices);
        Event selectionEvent = new Event();
        selectionEvent.type = SWT.Selection;
        selectionEvent.widget = m_filesToImport;
        selectionEvent.display = m_filesToImport.getDisplay();
        m_filesToImport.notifyListeners(SWT.Selection, selectionEvent);
        checkCompletness();
    }
    
    /**
     * Inits the selection state of every button.
     */
    private void initButtons() {
        m_radio1.setSelection(true);
        m_elementsCheckBox1.setSelection(true);
        m_elementsCheckBox1.setEnabled(false);
        m_elementsCheckBox2.setVisible(false);
    }
    
    /**
     * checks if all is complete
     *
     */
    void checkCompletness() {
        // Update model values
        m_fileNames = m_filesToImport.getItems();
        if (m_includeTargetOptions) {
            m_importTarget = m_radio1.getSelection() 
                ? IMPORT_ALL : IMPORT_TESTCASES;
            if (m_elementsCheckBox1.getSelection()) {
                m_selectedElements = m_selectedElements | IMPORT_TESTCASES;
            }
            if (m_elementsCheckBox2.getSelection()) {
                m_selectedElements = m_selectedElements | IMPORT_AUTS;
            }
            m_isOpenProject = m_openProjectCheckbox.getSelection();
        } else {
            m_importTarget = FileStorageBP.IMPORT_ALL;
            m_isOpenProject = false;
        }


        // check completeness
        if (m_includeTargetOptions) {
            m_openProjectCheckbox.setEnabled(m_radio1.getSelection() 
                    && m_filesToImport.getItemCount() <= 1);
            if (!m_openProjectCheckbox.isEnabled()) {
                m_openProjectCheckbox.setSelection(false);
                m_isOpenProject = false;
            }
        }
        if (m_fileNames.length < 1) {
            setErrorMessage(I18n.getString("ImportProjectDialog.NoFilesToImport")); //$NON-NLS-1$
            setPageComplete(false);
        } else if (m_importTarget != FileStorageBP.IMPORT_ALL 
                && !isProjectOpen()) {
            setErrorMessage(I18n.getString("ImportProjectDialog.NoProjectError")); //$NON-NLS-1$
            setPageComplete(false);
        } else if (m_importTarget != FileStorageBP.IMPORT_ALL 
                && m_selectedElements == 0) {
            
            setErrorMessage(I18n.getString("ImportProjectDialog.NoSelectError")); //$NON-NLS-1$
            setPageComplete(false);
        } else {
            setErrorMessage(null);
            setPageComplete(true);
            if (m_includeTargetOptions) {
                m_radio1.setEnabled(true);
                m_radio2.setEnabled(true);
            }
        }
        handleFile(m_fileToAdd.getText());
    }
    
    /**
     * checks if a project is open and shows Error Message
     * @return isopen ?
     */
    private boolean isProjectOpen() {
        if (GeneralStorage.getInstance().getProject() == null) {
            return false;
        }
        return true;
    }

    /**
     * selection Listener for radio button
     * @author BREDEX GmbH
     */
    private class RadioSelectionListener extends SelectionAdapter {
        /**
         * event widget selected
         * @param e SelectionEvent
         */
        public void widgetSelected(SelectionEvent e) {
            checkCompletness();
            if (e.getSource() == m_radio1) {
                m_elementsCheckBox1.setEnabled(false);
                m_elementsCheckBox2.setEnabled(false);
            } else if ((e.getSource() == m_radio2) && isProjectOpen()) {
                m_elementsCheckBox1.setEnabled(true);
                m_elementsCheckBox2.setEnabled(false);
                // FIXME: remove the following lines, if AUT-Import is allowed -
                m_elementsCheckBox1.setSelection(true);
                // -------------------------------------------------------------
            }
        }        
    }
    
    /**
     * adds the browse button
     * @param composite parent composite
     */
    private void addBrowseButton(Composite composite) {
        GridData gridData;
        m_browseButton = new Button(composite, SWT.NONE);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_browseButton.setLayoutData(gridData);
        m_browseButton.setText(I18n.getString("ImportProjectDialog.browse")); //$NON-NLS-1$
        m_browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(Plugin.getShell(), 
                        SWT.OPEN | SWT.APPLICATION_MODAL | SWT.MULTI);
                String file;
                fileDialog.setText(I18n.getString("ImportProjectDialog.fileSelector")); //$NON-NLS-1$
                String[] extension = new String[1];
                extension[0] = "*.xml"; //$NON-NLS-1$
                fileDialog.setFilterExtensions(extension);
                fileDialog.setFilterPath(Utils.getLastDirPath());
                file = fileDialog.open();
                getShell().setFocus();
                if (file != null) {
                    String path = fileDialog.getFilterPath();
                    String [] fileNames = fileDialog.getFileNames();
                    String [] absFileNames = new String [fileNames.length];
                    for (int i = 0; i < fileNames.length; i++) {
                        try {
                            absFileNames[i] = 
                                new File(path, fileNames[i]).getCanonicalPath();
                        } catch (IOException ioe) {
                            log.error("Failed to find file: " //$//$NON-NLS-1$
                                + path + File.pathSeparator + fileNames[i], 
                                ioe);
                        }
                    }
                    addFilesToList(absFileNames);
                    Utils.storeLastDirPath(path);
                }
            }
        });
        DialogUtils.setWidgetName(m_browseButton, "browseButton"); //$NON-NLS-1$
    }

    /**
     * adds the add button
     * @param parent parent composite
     */
    private void addAddButton(Composite parent) {
        GridData gridData;
        m_addButton = new Button(parent, SWT.NONE);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_addButton.setLayoutData(gridData);
        m_addButton.setText(I18n.getString("ImportProjectDialog.add")); //$NON-NLS-1$
        m_addButton.setEnabled(false);
        m_addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                
                try {
                    addFilesToList(new String [] {
                        new File(m_fileToAdd.getText()).getCanonicalPath()
                    });
                } catch (IOException ioe) {
                    log.error("Failed to find file: " + m_fileToAdd.getText(), //$//$NON-NLS-1$
                        ioe);
                }
            }
            
        });
        DialogUtils.setWidgetName(m_addButton, "addButton"); //$NON-NLS-1$
    }

    /**
     * adds a blank Line 
     * @param composite parent composite
     */
    private void addBlankLine(Composite composite) {
        GridData gridData;
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = NUM_COLUMS;
        gridData.grabExcessHorizontalSpace = true;
        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public int getImportTarget() {
        return m_importTarget;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public String [] getFiles() {
        return m_fileNames;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public boolean getIsOpenProject() {
        return m_isOpenProject;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public int getSelectedElements() {
        return m_selectedElements;
    }
    
    /**
     * Sets the enablement for the button.
     * 
     * @param enabled <code>true</code> to enable the button, <code>false</code>
     *               to disable it.
     */
    private void enableUpButton(boolean enabled) {
        m_moveUpButton.setEnabled(enabled);
        if (enabled) {
            m_moveUpButton.setImage(IconConstants.UP_ARROW_IMAGE);
        } else {
            m_moveUpButton.setImage(IconConstants.UP_ARROW_DIS_IMAGE);
        }
    }

    /**
     * Sets the enablement for the button.
     * 
     * @param enabled <code>true</code> to enable the button, <code>false</code>
     *               to disable it.
     */
    private void enableDownButton(boolean enabled) {
        m_moveDownButton.setEnabled(enabled);
        if (enabled) {
            m_moveDownButton.setImage(IconConstants.DOWN_ARROW_IMAGE);
        } else {
            m_moveDownButton.setImage(IconConstants.DOWN_ARROW_DIS_IMAGE);
        }
    }
    
    /**
     * Sets the enablement for the button.
     * 
     * @param enabled <code>true</code> to enable the button, <code>false</code>
     *               to disable it.
     */
    private void enableRemoveButton(boolean enabled) {
        m_removeButton.setEnabled(enabled);
        if (enabled) {
            m_removeButton.setImage(IconConstants.DELETE_IMAGE);
        } else {
            m_removeButton.setImage(IconConstants.DELETE_IMAGE_DISABLED);
        }
    }
    
    /**
     * Checks the enablement criteria for list buttons and sets their 
     * enablement.
     *
     */
    private void checkListButtonEnablement() {
        if (m_filesToImport.getSelectionCount() > 0) {
            enableUpButton(true);
            enableDownButton(true);
            enableRemoveButton(true);
        } else {
            enableUpButton(false);
            enableDownButton(false);
            enableRemoveButton(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        final String title = I18n.getString("ImportProjectDialog.title"); //$NON-NLS-1$
        setTitle(title);
        setImageDescriptor(IconConstants.IMPORT_DIALOG_IMAGE_DESCRIPTOR);
        setMessage(I18n.getString("ImportProjectDialog.message")); //$NON-NLS-1$ 
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData;
        GridLayout layout = new GridLayout();
        layout.numColumns = NUM_COLUMS;
        layout.makeColumnsEqualWidth = true;
        composite.setLayout(layout);
        
        Label label = new Label(composite, SWT.NONE);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = NUM_COLUMS;
        gridData.grabExcessHorizontalSpace = true;
        label.setLayoutData(gridData);
        label.setText(I18n.getString("ImportProjectDialog.fileLabel")); //$NON-NLS-1$
        
        m_fileToAdd = new GDText(composite, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        m_fileToAdd.setLayoutData(gridData);
        m_fileToAdd.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String fileName = m_fileToAdd.getText();
                handleFile(fileName);
            }
        });
        DialogUtils.setWidgetName(m_fileToAdd, "fileToAdd"); //$NON-NLS-1$

        addFileButtonComposite(composite);
        addBlankLine(composite);

        Label listLabel = new Label(composite, SWT.NONE);
        listLabel.setText(I18n.getString("ImportProjectDialog.listLabel")); //$NON-NLS-1$
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = NUM_COLUMS;
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = SWT.LEFT;
        listLabel.setLayoutData(gridData);
        GDControlDecorator.decorateInfo(listLabel, 
                "GDControlDecorator.Import", false); //$NON-NLS-1$
        
        addImportList(composite);
        addListButtons(composite);

        if (m_includeTargetOptions) {
            addBlankLine(composite);
            
            addButtons(composite);
            RadioSelectionListener listener = new RadioSelectionListener();
            
            initButtons();
            
            addBlankLine(composite);
            m_radio1.addSelectionListener(listener);
            m_radio2.addSelectionListener(listener);
            m_elementsCheckBox1.addSelectionListener(listener);
            m_elementsCheckBox2.addSelectionListener(listener);
        }

        Plugin.getHelpSystem().setHelp(composite, ContextHelpIds
                .IMPORT_PROJECT_DIALOG);
        setPageComplete(false);
        setControl(composite);
    }
    
    /**
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(
                ContextHelpIds.IMPORT_PROJECT_DIALOG);
    }
}
