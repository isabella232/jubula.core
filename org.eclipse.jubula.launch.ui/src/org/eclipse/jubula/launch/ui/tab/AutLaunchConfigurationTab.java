/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.launch.ui.tab;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jubula.launch.AutLaunchConfigurationConstants;
import org.eclipse.jubula.launch.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launch Configuration tab for launching an application with support for 
 * automated testing (as an AUT).
 * 
 * @author BREDEX GmbH
 * @created 20.04.2011
 */
public class AutLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AutLaunchConfigurationTab.class);
    
    /** 
     * text field for AUT ID
     * 
     * @see AutLaunchConfigurationConstants#AUT_ID_KEY
     */
    private Text m_autIdText;

    /** 
     * label for AUT ID
     * 
     * @see AutLaunchConfigurationConstants#AUT_ID_KEY
     */
    private Label m_autIdLabel;

    /** 
     * checkbox for IS_ACTIVE
     * 
     * @see AutLaunchConfigurationConstants#ACTIVE_KEY
     */
    private Button m_activateTestSupportCheckbox;

    /**
     * 
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        createVerticalSpacer(parent, 1);
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        composite.setLayout(new GridLayout(2, false));
        m_activateTestSupportCheckbox = createCheckButton(
                composite, 
                Messages.AutLaunchConfigurationTab_ActiveCheckbox_label);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(
                m_activateTestSupportCheckbox);
        
        m_autIdLabel = new Label(composite, SWT.NONE);
        m_autIdLabel.setText(
                Messages.AutLaunchConfigurationTab_AutIdTextField_label);
        
        m_autIdText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(m_autIdText);

        m_activateTestSupportCheckbox.addSelectionListener(
                new SelectionAdapter() {
            
                    public void widgetSelected(SelectionEvent e) {
                        boolean enable = 
                            m_activateTestSupportCheckbox.getSelection();
                        m_autIdText.setEnabled(enable);
                        m_autIdLabel.setEnabled(enable);
                    }
                    
                });
        
        setControl(composite);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(
                AutLaunchConfigurationConstants.ACTIVE_KEY, 
                AutLaunchConfigurationConstants.ACTIVE_DEFAULT_VALUE);
        configuration.setAttribute(
                AutLaunchConfigurationConstants.AUT_ID_KEY, 
                AutLaunchConfigurationConstants.AUT_ID_DEFAULT_VALUE);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            boolean isActive = configuration.getAttribute(
                    AutLaunchConfigurationConstants.ACTIVE_KEY, 
                    AutLaunchConfigurationConstants.ACTIVE_DEFAULT_VALUE);
            m_activateTestSupportCheckbox.setSelection(isActive);
            m_autIdLabel.setEnabled(isActive);
            m_autIdText.setEnabled(isActive);
        } catch (CoreException ce) {
            LOG.error("An error occurred while initializing 'active' checkbox.", ce); //$NON-NLS-1$
        }

        try {
            m_autIdText.setText(
                    configuration.getAttribute(
                        AutLaunchConfigurationConstants.AUT_ID_KEY, 
                        AutLaunchConfigurationConstants.AUT_ID_DEFAULT_VALUE));
        } catch (CoreException ce) {
            LOG.error("An error occurred while initializing AUT ID text field.", ce); //$NON-NLS-1$
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(
                AutLaunchConfigurationConstants.ACTIVE_KEY, 
                m_activateTestSupportCheckbox.getSelection());
        configuration.setAttribute(
                AutLaunchConfigurationConstants.AUT_ID_KEY, 
                m_autIdText.getText());
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getName() {
        return Messages.AutLaunchConfigurationTab_name;
    }

}
