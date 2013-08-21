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
package org.eclipse.jubula.client.ui.rcp.properties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jubula.client.alm.mylyn.core.utils.ALMAccess;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.CompletenessBP;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedIntText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedProjectNameText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedURLText;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is the class for the test data property page of a project.
 *
 * @author BREDEX GmbH
 * @created 08.02.2005
 */
public class ProjectGeneralPropertyPage extends AbstractProjectPropertyPage {
    /** the default width */
    private static final int DEFAULT_CONTROL_WIDTH = 500;

    /**
     * @author BREDEX GmbH
     * @created Aug 21, 2007
     */
    public interface IOkListener {
        /**
         * The OK button has been pressed.
         */
        public void okPressed() throws PMException;
    }

    /**
     * @author BREDEX GmbH
     */
    private class ConnectionTestListener implements SelectionListener {
        /** {@inheritDoc} */
        public void widgetSelected(SelectionEvent e) {
            IStatus connectionStatus = ALMAccess
                    .testConnection(m_almRepoCombo.getSelectedObject());
            if (connectionStatus.isOK()) {
                m_connectionTest.setImage(IconConstants.STEP_OK_IMAGE);
                setErrorMessage(null);
            } else {
                m_connectionTest.setImage(IconConstants.ERROR_IMAGE);
                setErrorMessage(connectionStatus.getMessage());
            }
        }
        
        /** {@inheritDoc} */
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }
    
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;

    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(ProjectGeneralPropertyPage.class);
    
    /** the m_text field for the project name */
    private CheckedText m_projectNameTextField;
    /** the m_isReusable checkbox for if the project is reusable */
    private Button m_isReusableCheckbox;
    /** the m_isProtected checkbox for if the project is protected */
    private Button m_isProtectedCheckbox;
    /** the StateController */
    private final WidgetModifyListener m_modifyListener = 
        new WidgetModifyListener();
    /** the StateController */
    private final ToolkitComboSelectionListener m_toolkitComboListener = 
        new ToolkitComboSelectionListener();
    /** the Combo to select the toolkit */
    private DirectCombo<String> m_projectToolkitCombo;
    /** the Combo to select the connected ALM system */
    private DirectCombo<String> m_almRepoCombo;
    /** the button to test the connection with */ 
    private Button m_connectionTest;
    /** the new project name */
    private String m_newProjectName;
    /**  Checkbox to decide if testresults should be deleted after specified days */
    private Button m_cleanTestresults = null;
    
    /**
     * Checkbox to decide if a comment should be automatically posted to the ALM
     * in case of a succeeded test
     */
    private Button m_reportOnSuccess = null;
    /** the success comment */
    private Text m_successComment;
    /** the success comment */
    private Text m_failureComment;
    
    /**
     * Checkbox to decide if a comment should be automatically posted to the ALM
     * in case of a failed test
     */
    private Button m_reportOnFailure = null;    
    
    /**  textfield to specify days after which testresults should be deleted after from database */
    private CheckedIntText m_cleanResultDays = null; 
    
    /** set of listeners to be informed when ok has been pressed */
    private Set<IOkListener> m_okListenerList = new HashSet<IOkListener>();
    /**
     * the projects description text field
     */
    private Text m_projectDescriptionTextField;
    
    /**
     * the dashboards URL text field
     */
    private CheckedText m_dashboardURL;
    
    /**
     * the original / unmodified project properties
     */
    private IProjectPropertiesPO m_origProjectProps;
    /**
     * the old project name
     */
    private String m_oldProjectName;
    
    /**
     * @param es
     *            the editSupport
     */
    public ProjectGeneralPropertyPage(EditSupport es) {
        super(es);
        m_oldProjectName = getProject().getName();
        m_origProjectProps = ((IProjectPropertiesPO) es.getOriginal());
    }

    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        Composite composite = createComposite(parent, NUM_COLUMNS_1,
            GridData.FILL, false);
        Composite projectNameComposite = createComposite(composite,
            NUM_COLUMNS_2, GridData.FILL, false);
        noDefaultAndApplyButton();       

        createEmptyLabel(projectNameComposite);
        createEmptyLabel(projectNameComposite);
        
        createProjectNameField(projectNameComposite);
        createProjectDescrField(projectNameComposite);
        createProjectVersionInfo(projectNameComposite);
        createProjectGuidInfo(projectNameComposite);
        
        createEmptyLabel(projectNameComposite);
        separator(projectNameComposite, NUM_COLUMNS_2); 
        createEmptyLabel(projectNameComposite);

        createAutToolKit(projectNameComposite);
        separator(projectNameComposite, NUM_COLUMNS_2);
        createEmptyLabel(projectNameComposite);
        
        createIsReusable(projectNameComposite);
        createIsProtected(projectNameComposite);
        
        separator(projectNameComposite, NUM_COLUMNS_2);
        createEmptyLabel(projectNameComposite);
        
        createCleanTestResults(projectNameComposite);
        
        separator(projectNameComposite, NUM_COLUMNS_2);
        createEmptyLabel(projectNameComposite);
        
        createALMrepositoryChooser(projectNameComposite);
        createReportOnSuccess(projectNameComposite);
        createReportOnFailure(projectNameComposite);
        createDashboardURL(projectNameComposite);
        
        Composite innerComposite = new Composite(composite, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        innerComposite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalSpan = NUM_COLUMNS_2;
        compositeData.horizontalAlignment = GridData.FILL;
        compositeData.grabExcessHorizontalSpace = true;
        innerComposite.setLayoutData(compositeData);

        addListener();
        Plugin.getHelpSystem().setHelp(parent,
            ContextHelpIds.PROJECT_PROPERTY_PAGE);
        return composite;
    }
    
    /**
     * @param parent the parent to use
     */
    private void createDashboardURL(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.FILL, true);
        createLabel(leftComposite, 
                Messages.ProjectPropertyPageDasboardURLLabel);
        m_dashboardURL = new CheckedURLText(rightComposite, SWT.BORDER);
        m_dashboardURL.setText(StringUtils
                .defaultString(m_origProjectProps.getDashboardURL()));
        m_dashboardURL.validate();
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = false;
        textGridData.widthHint = DEFAULT_CONTROL_WIDTH;
        m_dashboardURL.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_dashboardURL,
                IPersistentObject.MAX_STRING_LENGTH);
    }
    
    /**
     * @param parent the parent to use
     */
    private void createReportOnFailure(Composite parent) {
        m_reportOnFailure = new Button(parent, SWT.CHECK);
        m_reportOnFailure.setText(Messages
                .ProjectPropertyPageReportOnFailureLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = false;
        m_reportOnFailure.setLayoutData(gridData);
        boolean reportOnFailure = m_origProjectProps.getIsReportOnFailure();
        m_reportOnFailure.setSelection(reportOnFailure);
        m_reportOnFailure.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                enableFailureCommentTextfield();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing here
            }
        });
        m_failureComment = new Text(parent, SWT.BORDER);
        m_failureComment.setText(StringUtils
                .defaultString(m_origProjectProps.getFailureComment()));
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = false;
        textGridData.widthHint = DEFAULT_CONTROL_WIDTH;
        m_failureComment.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_failureComment,
                IPersistentObject.MAX_STRING_LENGTH);
        ControlDecorator.createInfo(m_failureComment,
                Messages.ProjectPropertyPageReportOnFailureInfo, false);
        enableFailureCommentTextfield();
    }
    
    /**
     * @param parent the parent to use
     */
    private void createReportOnSuccess(Composite parent) {
        m_reportOnSuccess = new Button(parent, SWT.CHECK);
        m_reportOnSuccess.setText(Messages
                .ProjectPropertyPageReportOnSuccessLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = false;
        m_reportOnSuccess.setLayoutData(gridData);
        boolean reportOnSuccess = m_origProjectProps.getIsReportOnSuccess();
        m_reportOnSuccess.setSelection(reportOnSuccess);
        m_reportOnSuccess.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                enableSuccessCommentTextfield();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing here
            }
        });
        m_successComment = new Text(parent, SWT.BORDER);
        m_successComment.setText(StringUtils
                .defaultString(m_origProjectProps.getSuccessComment()));
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = false;
        textGridData.widthHint = DEFAULT_CONTROL_WIDTH;
        m_successComment.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_successComment,
                IPersistentObject.MAX_STRING_LENGTH);
        ControlDecorator.createInfo(m_successComment,
                Messages.ProjectPropertyPageReportOnSuccessInfo, false);
        enableSuccessCommentTextfield();
    }

    /**
     * @param parent the parent to use
     */
    private void createALMrepositoryChooser(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, 3,
                GridData.FILL, true);
        createLabel(leftComposite,
                Messages.ProjectPropertyPageALMRepositoryLabel);
        String configuredRepo = m_origProjectProps
                .getALMRepositoryName();
        m_almRepoCombo = ControlFactory
                .createALMRepositoryCombo(rightComposite, configuredRepo);
        m_almRepoCombo.setSelectedObject(configuredRepo);
        m_almRepoCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                m_connectionTest.setImage(IconConstants.STEP_TESTING_IMAGE);
                setErrorMessage(null);
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        m_almRepoCombo.setLayoutData(textGridData);
        
        m_connectionTest = new Button(rightComposite, SWT.PUSH);
        m_connectionTest.setText(Messages.ProjectPropertyPageALMConnectionTest);
        m_connectionTest.setImage(IconConstants.STEP_TESTING_IMAGE);
        m_connectionTest.addSelectionListener(new ConnectionTestListener());
    }

    /**
     * @param parent the parent composite
     */
    private void createEmptyLabel(Composite parent) {
        createLabel(parent, StringConstants.EMPTY);
    }

    /**
     * @param parent the parent composite
     */
    private void createProjectVersionInfo(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, 3, 
            GridData.FILL, true);
        
        createLabel(leftComposite, 
            Messages.ProjectPropertyPageProjectVersion);
        createLabel(rightComposite, m_origProjectProps.getMajorNumber() 
                + StringConstants.DOT + m_origProjectProps.getMinorNumber());
        Label l = createLabel(rightComposite, StringConstants.EMPTY);
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        l.setLayoutData(layoutData);

    }

    /**
     * @param parent the parent composite
     */
    private void createProjectGuidInfo(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, 3, 
            GridData.FILL, true);
        
        ControlDecorator.decorateInfo(createLabel(leftComposite, 
            Messages.ProjectPropertyPageProjectGuid), 
            "ControlDecorator.ProjectPropertiesGUID", false); //$NON-NLS-1$
        
        Label projectGuid = new Label(rightComposite, SWT.NONE);
        projectGuid.setText(getProject().getGuid());
        Label l = createLabel(rightComposite, StringConstants.EMPTY);
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        l.setLayoutData(layoutData);

    }

    /**
     * @param parent
     *            the parent composite
     */
    private void createIsReusable(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_2,
                GridData.FILL, true);
        ControlDecorator.decorateInfo(createLabel(leftComposite, 
                Messages.ProjectPropertyPageIsReusable),
                "ControlDecorator.NewProjectIsReusable", false); //$NON-NLS-1$
        m_isReusableCheckbox = new Button(rightComposite, SWT.CHECK);

        m_isReusableCheckbox.setSelection(m_origProjectProps.getIsReusable());
        m_isReusableCheckbox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                boolean isReusable = m_isReusableCheckbox.getSelection();
                if (isReusable) {
                    m_isProtectedCheckbox.setSelection(true);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }
    
    /**
     * @param parent
     *            the parent composite
     */
    private void createIsProtected(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_2,
                GridData.FILL, true);
        ControlDecorator.decorateInfo(createLabel(leftComposite, 
                Messages.ProjectPropertyPageIsProtected),
                "ControlDecorator.NewProjectIsProtected", false); //$NON-NLS-1$
        m_isProtectedCheckbox = new Button(rightComposite, SWT.CHECK);

        m_isProtectedCheckbox.setSelection(m_origProjectProps.getIsProtected());

    }

    /**
     * Creates a new composite.
     * @param parent The parent composite.
     * @param numColumns the number of columns for this composite.
     * @param alignment The horizontalAlignment (grabExcess).
     * @param horizontalSpace The horizontalSpace.
     * @return The new composite.
     */
    private Composite createComposite(Composite parent, int numColumns, 
            int alignment, boolean horizontalSpace) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalAlignment = alignment;
        compositeData.grabExcessHorizontalSpace = horizontalSpace;
        composite.setLayoutData(compositeData);
        return composite;       
    }
    
    /**
     * Creates a label for this page.
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    private Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER, 
            false , false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;
    }
    
    /**
     * Creates a separator line.
     * @param composite The parent composite.
     * @param horSpan The horizontal span.
     */
    private void separator(Composite composite, int horSpan) {
        createLabel(composite, StringConstants.EMPTY);
        Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sepData = new GridData();
        sepData.horizontalAlignment = GridData.FILL;
        sepData.horizontalSpan = horSpan;
        sep.setLayoutData(sepData);
        createLabel(composite, StringConstants.EMPTY);
    }

    /**
     * Creates the textfield for the project name.
     * 
     * @param parent
     *            The parent composite.
     */
    private void createProjectNameField(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);
        createLabel(leftComposite, Messages.ProjectPropertyPageProjectName);
        m_projectNameTextField = new CheckedProjectNameText(rightComposite, 
            SWT.BORDER);
        m_projectNameTextField.setText(getProject().getName());
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        LayoutUtil.addToolTipAndMaxWidth(textGridData, m_projectNameTextField);
        m_projectNameTextField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_projectNameTextField);
    }
    
    /**
     * Creates the textfield for the project description.
     * 
     * @param parent
     *            The parent composite.
     */
    private void createProjectDescrField(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);
        createLabel(leftComposite, Messages.ProjectPropertyPageProjectDescr);
        m_projectDescriptionTextField = new Text(rightComposite, SWT.BORDER);
        m_projectDescriptionTextField.setText(StringUtils
                .defaultString(getProject().getComment()));
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        LayoutUtil.addToolTipAndMaxWidth(
                textGridData, m_projectDescriptionTextField);
        m_projectDescriptionTextField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_projectDescriptionTextField,
                IPersistentObject.MAX_STRING_LENGTH);
    }
    
    /**
     * @param parent the parent composite
     */
    private void createAutToolKit(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_2, 
            GridData.FILL, true);
        createLabel(leftComposite, Messages.ProjectPropertyPageAutToolKitLabel);
        m_projectToolkitCombo = ControlFactory
                .createProjectToolkitCombo(rightComposite);
        m_projectToolkitCombo.setSelectedObject(getProject().getToolkit());
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        m_projectToolkitCombo.setLayoutData(textGridData);
    }
    
    /**
     * reflect the enablement of checkbox to the corresponding textfield
     */
    protected void enableCleanResultDaysTextfield() {
        m_cleanResultDays.setEnabled(m_cleanTestresults.getSelection());
    }
    
    /**
     * reflect the enablement of checkbox to the corresponding textfield 
     */
    protected void enableSuccessCommentTextfield() {
        m_successComment.setEnabled(m_reportOnSuccess.getSelection());
    }
    
    /**
     * reflect the enablement of checkbox to the corresponding textfield 
     */
    protected void enableFailureCommentTextfield() {
        m_failureComment.setEnabled(m_reportOnFailure.getSelection());
    }
    
    /**
     * @param parent The parent <code>Composite</code>
     */
    private void createCleanTestResults(Composite parent) {
        m_cleanTestresults = new Button(parent, SWT.CHECK);
        m_cleanTestresults.setText(Messages
                .TestResultViewPreferencePageCleanResults);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_cleanTestresults.setLayoutData(gridData);
        int testResultCleanupInterval = getProject()
                .getTestResultCleanupInterval();
        m_cleanTestresults.setSelection(testResultCleanupInterval
                != IProjectPO.NO_CLEANUP);
        m_cleanTestresults.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                enableCleanResultDaysTextfield();
                checkCompleteness();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing here
            }
        });
        m_cleanResultDays = new CheckedIntText(
                parent, SWT.BORDER, false, 1, Integer.MAX_VALUE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        gridData.widthHint = 80;
        m_cleanResultDays.setLayoutData(gridData);
        if (testResultCleanupInterval > 0) {
            m_cleanResultDays
                    .setText(String.valueOf(testResultCleanupInterval));
        }
        m_cleanResultDays.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }
            
        });
        enableCleanResultDaysTextfield();
        ControlDecorator.decorateInfo(m_cleanResultDays,  
                "TestResultViewPreferencePage.cleanResultsInfo", false); //$NON-NLS-1$
    }
    
    /**
     * Checks if Preference Page is complete and valid
     */
    protected void checkCompleteness() {
        if (m_cleanResultDays.isEnabled() 
                && m_cleanResultDays.getValue() <= 0) {
            setErrorMessage(Messages
                    .TestResultViewPreferencePageCleanResultDaysEmpty);
            setValid(false);
            return;
        }
        setErrorMessage(null);
        setValid(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performOk() {
        try {
            if (!m_oldProjectName.equals(m_newProjectName)) {
                if (ProjectPM.doesProjectNameExist(m_newProjectName)) {

                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.E_PROJECTNAME_ALREADY_EXISTS,
                            new Object[] { m_newProjectName }, null);
                    return false;
                }
            }
            IProjectPO project = getProject();
            if (m_isReusableCheckbox != null) {
                project.setIsReusable(m_isReusableCheckbox.getSelection());
            }
            if (m_isProtectedCheckbox != null) {
                project.setIsProtected(m_isProtectedCheckbox.getSelection());
            }
            if (m_projectDescriptionTextField != null) {
                project.setComment(m_projectDescriptionTextField.getText());
            }
            storeAutoTestResultCleanup();
            storeALMData();
            if (!m_oldProjectName.equals(m_newProjectName)) {
                ProjectNameBP.getInstance().setName(
                        getEditSupport().getSession(), project.getGuid(),
                        m_newProjectName);
            }
            fireOkPressed();
            Set<IReusedProjectPO> origReused = 
                ((IProjectPropertiesPO)getEditSupport().getOriginal())
                    .getUsedProjects();
            Set<IReusedProjectPO> newReused = new HashSet<IReusedProjectPO>(
                ((IProjectPropertiesPO)getEditSupport().getWorkVersion())
                    .getUsedProjects());
            newReused.removeAll(origReused);
            getEditSupport().saveWorkVersion();
            refreshProject();
            DataEventDispatcher ded = DataEventDispatcher.getInstance();
            ded.fireProjectStateChanged(ProjectState.prop_modified);
            for (IReusedProjectPO reused : newReused) {
                try {
                    IProjectPO reusedProject = 
                        ProjectPM.loadReusedProject(reused);
                    if (reusedProject != null) { 
                        // incomplete database, see https://bxapps.bredex.de/bugzilla/show_bug.cgi?id=854
                        ComponentNamesBP.getInstance().refreshNames(
                                reusedProject.getId());
                    }
                } catch (JBException e) {
                    // Could not refresh Component Name information for 
                    // reused project. Log the exception.
                    log.error(Messages
                            .ErrorWhileRetrievingReusedProjectInformation, e);
                }
            }
            // FIXME zeb This updates the Test Case Browser. Once we have separate
            //           EditSupports for each property page, then we can use 
            //           "real" ReusedProjectPOs instead of a placeholder.
            ded.fireDataChangedListener(
                    PoMaker.createReusedProjectPO("1", 1, 1), //$NON-NLS-1$
                    DataState.ReuseChanged, UpdateState.notInEditor);
            ded.fireDataChangedListener(GeneralStorage.getInstance()
                    .getProject(), DataState.Renamed, UpdateState.notInEditor);
            CompletenessBP.getInstance().completeProjectCheck();
        } catch (PMException e) {
            ErrorHandlingUtil.createMessageDialog(e, null, null);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        } catch (IncompatibleTypeException ite) {
            ErrorHandlingUtil.createMessageDialog(
                    ite, ite.getErrorMessageParams(), null);
        }
        return true;
    }

    /**
     * store all ALM related data
     */
    private void storeALMData() {
        IProjectPropertiesPO props = getProject().getProjectProperties();
        if (m_almRepoCombo != null) {
            props.setALMRepositoryName(m_almRepoCombo.getText());
        }
        if (m_reportOnFailure != null) {
            props.setIsReportOnFailure(m_reportOnFailure.getSelection());
        }
        if (m_failureComment != null) {
            props.setFailureComment(m_failureComment.getText().trim());
        }
        if (m_reportOnSuccess != null) {
            props.setIsReportOnSuccess(m_reportOnSuccess.getSelection());
        }
        if (m_successComment != null) {
            props.setSuccessComment(m_successComment.getText().trim());
        }
        if (m_dashboardURL != null) {
            props.setDashboardURL(m_dashboardURL.getText().trim());
        }
    }

    /**
     * store preferences made for auto test result cleanup
     */
    private void storeAutoTestResultCleanup() {
        if (m_cleanResultDays != null) {
            if (m_cleanTestresults != null) {
                boolean autoClean = m_cleanTestresults.getSelection();
                if (autoClean) {
                    getProject().setTestResultCleanupInterval(
                            Integer.valueOf(m_cleanResultDays.getText()));
                } else {
                    getProject().setTestResultCleanupInterval(
                            IProjectPO.NO_CLEANUP);
                }
            }
        }
    }

    /**
     * Notify listeners that OK was pressed.
     */
    private void fireOkPressed() throws PMException {
        for (IOkListener listener : m_okListenerList) {
            listener.okPressed();
        }
    }

    /**
     * Refreshes the project.
     */
    private void refreshProject() throws ProjectDeletedException {
        GeneralStorage storage = GeneralStorage.getInstance();
        try {
            storage.getMasterSession().refresh(storage.getProject());
        } catch (EntityNotFoundException enfe) {
            // Occurs if any Object Mapping information has been deleted while
            // the Project Properties were being edited.
            // Refresh the entire master session to ensure that AUT settings
            // and Object Mappings are in sync
            storage.reloadMasterSession(new NullProgressMonitor());
        }
    }

    /**
     * Adds necessary listeners.
     */
    private void addListener() {
        m_projectNameTextField.addModifyListener(m_modifyListener);
        m_projectToolkitCombo.addSelectionListener(m_toolkitComboListener);
    }
    
    /** 
     * The action of the project name field.
     * @param isProjectNameVerified True, if the project name was verified.
     * @return false, if the project name field contents an error:
     * the project name starts or end with a blank, or the field is empty
     */
    boolean modifyProjectNameFieldAction(
        boolean isProjectNameVerified) {
        
        boolean isCorrect = true;
        String projectName = m_projectNameTextField.getText();
        int projectNameLength = projectName.length();
        super.getShell().setText(Messages.ProjectPropertyPageShellTitle 
                + projectName);
        if ((projectNameLength == 0) || (projectName
                .startsWith(StringConstants.SPACE))
            || (projectName.charAt(projectNameLength - 1) == ' ')) {
            
            isCorrect = false;
        }
        if (isCorrect) {
            setErrorMessage(null);
            setMessage(Messages.PropertiesActionPage1, NONE);
            setValid(true);
            if (isProjectNameVerified) {
                m_newProjectName = projectName;
            }
            if (ProjectPM.doesProjectNameExist(projectName)
                && !m_oldProjectName.equals(projectName)) {
                    
                setErrorMessage(Messages
                        .ProjectSettingWizardPageDoubleProjectName); 
                isCorrect = false;
                setValid(false);
            }
        } else {
            if (projectNameLength == 0) {
                setErrorMessage(Messages.ProjectWizardEmptyProject);
                setValid(false);
            } else {
                setErrorMessage(Messages.ProjectWizardNotValidProject);
                setValid(false);
            }
        }
        return isCorrect;
    }
    
    
    /**
     * This private inner class contains a new ModifyListener.
     * @author BREDEX GmbH
     * @created 11.07.2005
     */
    @SuppressWarnings("synthetic-access")
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            Object o = e.getSource();
            if (o.equals(m_projectNameTextField)) {
                modifyProjectNameFieldAction(true);
                return;
            }
        }       
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     * @created 10.02.2005
     */
    @SuppressWarnings("synthetic-access")
    private class ToolkitComboSelectionListener implements SelectionListener {

        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_projectToolkitCombo)) {
                handleAutToolkitSelection();
                return;
            }
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                    + StringConstants.DOT);
        }

        /**
         * Handles the selection of the autToolkitCombo
         */
        private void handleAutToolkitSelection() {
            final String newToolkit = m_projectToolkitCombo
                .getSelectedObject();
            final IProjectPO project = getProject();
            project.setToolkit(newToolkit);
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                    + StringConstants.DOT);
        }        
    }

    /**
     * @param toAdd The listener to add. 
     */
    public void addOkListener(IOkListener toAdd) {
        m_okListenerList.add(toAdd);
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(boolean visible) {
        refreshAutToolkitCombo();
        super.setVisible(visible);
    }

    /**
     * Refreshes the m_autToolkitCombo.
     */
    private void refreshAutToolkitCombo() {
        final Composite parent = m_projectToolkitCombo.getParent();
        final DirectCombo<String> tmpCombo = ControlFactory
                .createProjectToolkitCombo(parent);
        m_projectToolkitCombo.setItems(tmpCombo.getValues(), Arrays.asList(
            tmpCombo.getItems()));
        tmpCombo.dispose();
        m_projectToolkitCombo.setSelectedObject(getProject().getToolkit());
    }
    
}