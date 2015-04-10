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
package org.eclipse.jubula.launch.java.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jubula.launch.AutLaunchConfigurationConstants;
import org.eclipse.jubula.launch.java.ui.i18n.Messages;
import org.eclipse.jubula.launch.ui.tab.AutLaunchConfigurationTab;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launch Configuration tab for launching an Eclipse RCP application with 
 * support for automated testing (as an AUT).
 * 
 * @author BREDEX GmbH
 * @created 20.07.2011
 */
public class JavaAutLaunchConfigurationTab extends AutLaunchConfigurationTab {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(JavaAutLaunchConfigurationTab.class);
    
    /** 
     * label for toolkit of AUT
     */
    private Label m_toolkitLabel;
    
    /** 
     * combo box for toolkit of AUT
     */
    private Combo m_toolkitChoice;
    

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        composite.setLayout(new GridLayout(1, false));
        super.createControl(composite);
        Composite additionalComposite = new Composite(composite, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
                additionalComposite);
        additionalComposite.setLayout(new GridLayout(2, false));

        m_toolkitLabel = new Label(additionalComposite, SWT.NONE);
        m_toolkitLabel.setText(
                Messages.AutLaunchConfigurationTab_ToolkitChoice_label);
        
        m_toolkitChoice = new Combo(additionalComposite, SWT.DROP_DOWN);
        try {
            m_toolkitChoice.add(ToolkitSupportBP.getToolkitDescriptor(
                    CommandConstants.SWING_TOOLKIT).getName());
            m_toolkitChoice.add(ToolkitSupportBP.getToolkitDescriptor(
                    CommandConstants.JAVAFX_TOOLKIT).getName());
        } catch (ToolkitPluginException e) {
            LOG.error("Error while resolving toolkit names", e); //$NON-NLS-1$
        }
        ModifyListener listener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };
        m_toolkitChoice.addModifyListener(listener);
        setControl(composite);
    }
    
    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        super.initializeFrom(configuration);
        try {
            m_toolkitChoice.setText(
                    configuration.getAttribute(
                        AutLaunchConfigurationConstants.AUT_TOOLKIT, 
                        AutLaunchConfigurationConstants.
                            AUT_TOOLKIT_DEFAULT_VALUE));
        } catch (CoreException ce) {
            LOG.error("An error occurred while initializing toolkit choice box.", ce); //$NON-NLS-1$        }
        }
    }
    
    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        configuration.setAttribute(
                AutLaunchConfigurationConstants.AUT_TOOLKIT, 
                m_toolkitChoice.getText());
    }
    
    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        super.setDefaults(configuration);
        configuration.setAttribute(
                AutLaunchConfigurationConstants.AUT_TOOLKIT, 
                AutLaunchConfigurationConstants.AUT_TOOLKIT_DEFAULT_VALUE);
    }
}
