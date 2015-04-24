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
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.launch.AutLaunchConfigurationConstants;
import org.eclipse.jubula.launch.ui.i18n.Messages;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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
     * label for toolkit of AUT
     */
    private Label m_toolkitLabel;
    
    /** 
     * combo box for toolkit of AUT
     */
    private Combo m_toolkitChoice;
    
    /**
     * 
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        Image infoImage = 
            FieldDecorationRegistry.getDefault().getFieldDecoration(
                    FieldDecorationRegistry.DEC_INFORMATION).getImage();
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
        composite.setLayout(new GridLayout(2, false));

        m_autIdLabel = new Label(composite, SWT.NONE);
        m_autIdLabel.setText(
                Messages.AutLaunchConfigurationTab_AutIdTextField_label);

        m_autIdText = new Text(composite, SWT.BORDER);

        ControlDecoration autIdLabelDecoration = 
            new ControlDecoration(m_autIdText, SWT.LEFT | SWT.TOP);
        autIdLabelDecoration.setDescriptionText(
                Messages.AutLaunchConfigurationTab_AutIdTextField_info);
        autIdLabelDecoration.setImage(infoImage);
        autIdLabelDecoration.setMarginWidth(2);
        autIdLabelDecoration.setShowOnlyOnFocus(false);

        GridDataFactory.fillDefaults().grab(true, false)
            .indent(autIdLabelDecoration.getImage().getBounds().x 
                    + (autIdLabelDecoration.getMarginWidth() * 2), 0)
            .applyTo(m_autIdText);

        ModifyListener listener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };
        m_autIdText.addModifyListener(listener);
        
        
        m_toolkitLabel = new Label(composite, SWT.NONE);
        m_toolkitLabel.setText(
                Messages.AutLaunchConfigurationTab_ToolkitChoice_label);
        
        m_toolkitChoice = new Combo(composite, SWT.DROP_DOWN);
        try {
            m_toolkitChoice.add(ToolkitSupportBP.getToolkitDescriptor(
                    CommandConstants.SWING_TOOLKIT).getName());
            m_toolkitChoice.add(ToolkitSupportBP.getToolkitDescriptor(
                    CommandConstants.JAVAFX_TOOLKIT).getName());
        } catch (ToolkitPluginException e) {
            LOG.error("Error while resolving toolkit names", e); //$NON-NLS-1$
        }
        m_toolkitChoice.addModifyListener(listener);
        
        setControl(composite);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(
                AutLaunchConfigurationConstants.AUT_ID_KEY, 
                AutLaunchConfigurationConstants.AUT_ID_DEFAULT_VALUE);
        configuration.setAttribute(
                AutLaunchConfigurationConstants.AUT_TOOLKIT, 
                AutLaunchConfigurationConstants.AUT_TOOLKIT_DEFAULT_VALUE);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            m_autIdText.setText(
                    configuration.getAttribute(
                        AutLaunchConfigurationConstants.AUT_ID_KEY, 
                        AutLaunchConfigurationConstants.AUT_ID_DEFAULT_VALUE));
            m_toolkitChoice.setText(
                    configuration.getAttribute(
                        AutLaunchConfigurationConstants.AUT_TOOLKIT, 
                        AutLaunchConfigurationConstants.
                            AUT_TOOLKIT_DEFAULT_VALUE));
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
                AutLaunchConfigurationConstants.AUT_ID_KEY, 
                m_autIdText.getText());
        configuration.setAttribute(
                AutLaunchConfigurationConstants.AUT_TOOLKIT, 
                m_toolkitChoice.getText());
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getName() {
        return Messages.AutLaunchConfigurationTab_name;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Image getImage() {
        return IconConstants.TS_VAL_IMAGE;
    }

}
