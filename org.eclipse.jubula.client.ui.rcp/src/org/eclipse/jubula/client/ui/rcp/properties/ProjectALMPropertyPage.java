/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.alm.mylyn.core.utils.ALMAccess;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedURLText;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;


/**
 * This is the class for the test data property page of a project.
 *
 * @author BREDEX GmbH
 * @created 08.02.2005
 */
public class ProjectALMPropertyPage extends AbstractProjectPropertyPage {
    /**
     * @author BREDEX GmbH
     */
    private class ConnectionTestListener implements SelectionListener {
        /** {@inheritDoc} */
        public void widgetSelected(SelectionEvent e) {
            String selectedObject = m_almRepoCombo.getSelectedObject();
            IStatus connectionStatus = ALMAccess.testConnection(selectedObject);
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
    
    
    /**
     * @author BREDEX GmbH
     */
    private class DataUpdateListener implements ModifyListener {
        /** {@inheritDoc} */
        public void modifyText(ModifyEvent e) {
            updateALMData();
        }
    }
    
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;

    /** the Combo to select the connected ALM system */
    private DirectCombo<String> m_almRepoCombo;
    /** the button to test the connection with */ 
    private Button m_connectionTest;
    
    /**
     * Checkbox to decide if a comment should be automatically posted to the ALM
     * in case of a succeeded test
     */
    private Button m_reportOnSuccess = null;
    /** listener to keep the data in sync */
    private ModifyListener m_dataUpdater = new DataUpdateListener();
    
    /**
     * Checkbox to decide if a comment should be automatically posted to the ALM
     * in case of a failed test
     */
    private Button m_reportOnFailure = null;    
    
    /**
     * the dashboards URL text field
     */
    private CheckedText m_dashboardURL;
    
    /**
     * the original / unmodified project properties
     */
    private IProjectPropertiesPO m_origProjectProps;
    
    /**
     * @param es
     *            the editSupport
     */
    public ProjectALMPropertyPage(EditSupport es) {
        super(es);
        m_origProjectProps = ((IProjectPropertiesPO) es.getOriginal());
    }

    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        Composite composite = createComposite(parent, NUM_COLUMNS_1,
                GridData.FILL, false);

        createALMPageDescription(composite);
        
        Composite main = createComposite(composite, NUM_COLUMNS_2,
                GridData.FILL, false);
        noDefaultAndApplyButton();       

        createEmptyLabel(main);
        createEmptyLabel(main);
        
        createALMrepositoryChooser(main);
        createReportOnSuccess(main);
        createReportOnFailure(main);
        createDashboardURL(main);
        
        Event event = new Event();
        event.type = SWT.Selection;
        event.widget = m_almRepoCombo;
        m_almRepoCombo.notifyListeners(SWT.Selection, event);
        
        Plugin.getHelpSystem().setHelp(parent,
            ContextHelpIds.PROJECT_ALM_PROPERTY_PAGE);
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
        GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
        textGridData.grabExcessHorizontalSpace = true;
        m_dashboardURL.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_dashboardURL,
                IPersistentObject.MAX_STRING_LENGTH);
        m_dashboardURL.addModifyListener(m_dataUpdater);
    }
    
    /**
     * @param parent the parent to use
     */
    private void createReportOnFailure(Composite parent) {
        createEmptyLabel(parent);
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
                updateALMData();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }
    
    /**
     * @param parent the parent to use
     */
    private void createReportOnSuccess(Composite parent) {
        Label infoLabel = createLabel(parent, 
                Messages.ProjectPropertyPageReportOptionsLabel);
        ControlDecorator.createInfo(infoLabel,
                Messages.ProjectPropertyPageReportOptionsDecoration, false);
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
                updateALMData();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * @param parent the parent to use
     */
    private void createALMPageDescription(Composite parent) {
        createEmptyLabel(parent);
        
        Composite composite = createComposite(parent, 1,
                GridData.FILL, true);
        createLabel(composite, Messages.ProjectPropertyPageALMLabel);
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
        m_almRepoCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                m_connectionTest.setImage(IconConstants.STEP_TESTING_IMAGE);
                if (m_almRepoCombo.getSelectedObject() == null) {
                    m_reportOnFailure.setEnabled(false);
                    m_reportOnSuccess.setEnabled(false);
                    m_dashboardURL.setEnabled(false);
                } else {
                    m_reportOnFailure.setEnabled(true);
                    m_reportOnSuccess.setEnabled(true);
                    m_dashboardURL.setEnabled(true);
                }
                setErrorMessage(null);
                updateALMData();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        m_almRepoCombo.setSelectedObject(configuredRepo);
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
     * update the data
     */
    private void updateALMData() {
        IProjectPropertiesPO props = getProject().getProjectProperties();
        if (m_almRepoCombo != null) {
            props.setALMRepositoryName(m_almRepoCombo.getText());
        }
        if (m_reportOnFailure != null) {
            props.setIsReportOnFailure(m_reportOnFailure.getSelection());
        }
        if (m_reportOnSuccess != null) {
            props.setIsReportOnSuccess(m_reportOnSuccess.getSelection());
        }
        if (m_dashboardURL != null) {
            props.setDashboardURL(m_dashboardURL.getText().trim());
        }
    }
}