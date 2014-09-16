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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDialogStatusListener;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.rcp.widgets.autconfig.AutConfigComponent;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.UnexpectedGenericTypeException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 10.02.2005
 */
public class AUTConfigPropertiesDialog extends TitleAreaDialog 
    implements IDialogStatusListener {
    /** The logger */
    private static Logger log = LoggerFactory.getLogger(
            AUTConfigPropertiesDialog.class);
    
    /** the name of the selected aut configuration */
    private IAUTConfigPO m_autConfig;
    
    /** The toolkit */
    private String m_toolkit = StringUtils.EMPTY;

    /** The name of the AUT that will be using this configuration */
    private String m_autName;
    
    /** validator for the AUT ID text field */
    private IValidator m_autIdValidator;
    
    /**
     * The contructor.
     * @param parentShell The shell.
     * @param autConfig The selected AUTConfiguration in the AUTPropertiesDialog.
     * @param toolkit the toolkit.
     * @param autName The name of the AUT that will be using this configuration.
     * @param autIdValidator The validator for the AUT ID text field.
     */
    public AUTConfigPropertiesDialog(Shell parentShell, 
            IAUTConfigPO autConfig, String toolkit, String autName, 
            IValidator autIdValidator) {
        
        super(parentShell);
        m_autConfig = autConfig;
        m_toolkit = toolkit;
        m_autName = autName;
        m_autIdValidator = autIdValidator;
    }

    /**
     * {@inheritDoc}
     */
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) { 
        DataEventDispatcher.getInstance().getDialogStatusListenerMgr()
            .addListener(this);
        // set help id
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds
                .AUT_CONFIG_PROP_DIALOG);
        setTitle(Messages.ProjectWizardAutSettings);
        setMessage(Messages.ProjectWizardAUTData);
        try {
            Composite autConfigComposite = 
                ToolkitSupportBP.getAutConfigComposite(
                    m_toolkit, parent, SWT.H_SCROLL | SWT.V_SCROLL,
                    m_autConfig.getConfigMap(), m_autName);
            autConfigComposite.setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));

            // FIXME zeb This is necessary at the moment because the creator 
            //           of AutConfigComponents is in the ToolkitSupport project, which 
            //           is not aware of model (PO) objects nor of databinding classes 
            //           (IValidator). This dependency issue should be resolved, and
            //           the validator should be set in the constructor, rather than
            //           in a separate setter method.
            if (autConfigComposite instanceof AutConfigComponent) {
                ((AutConfigComponent)autConfigComposite).setAutIdValidator(
                        m_autIdValidator);
            }
            
        } catch (ToolkitPluginException e) {
            log.error(Messages.NoAUTConfigPageForToolkit + StringConstants.COLON
                + StringConstants.SPACE
                + String.valueOf(m_toolkit), e);
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_NO_AUTCONFIG_DIALOG);
        }

        return parent;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void okPressed() {
        if (canExecFileGetUsed()) {
            NagDialog.runNagDialog(this.getShell(),
                    "InfoNagger.DefineSwingOrSwtExecutable", //$NON-NLS-1$
                    ContextHelpIds.AUT_CONFIG_SETTING_WIZARD_PAGE); 
        }
        super.okPressed();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean close() {
        DataEventDispatcher.getInstance().getDialogStatusListenerMgr()
            .removeListener(this);
        return super.close();
    }

    /**
     * @return true if Swing or SWT should be used and an executable file is declared
     */
    private boolean canExecFileGetUsed() {
        if (m_autConfig.getValue(AutConfigConstants.EXECUTABLE, null) != null) {
            if (m_toolkit.equals(CommandConstants.SWT_TOOLKIT)
                    || m_toolkit.equals(
                            CommandConstants.SWING_TOOLKIT)) {
                return true;
            }
        }
        // else
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccured(List< ? extends Object> params) {
        DialogStatusParameter parameter = (DialogStatusParameter)params.get(0);
        setMessage(parameter.getMessage(), parameter.getStatusType());
        Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(parameter.getButtonState());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void checkGenericListElementType(List< ? extends Object> params) 
        throws UnexpectedGenericTypeException {
            
        Validate.noNullElements(params);
        Class type = null;
        int index = 0;
        if (!(params.get(0) instanceof DialogStatusParameter)) {
            type = DialogStatusParameter.class;
        }
        if (type != null) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.GenericType)
                .append(StringConstants.SPACE)
                .append(type)
                .append(StringConstants.SPACE)
                .append(Messages.WasExpectedBut)
                .append(StringConstants.SPACE)
                .append(params.get(index).getClass())
                .append(StringConstants.SPACE)
                .append(Messages.WasFound)
                .append(StringConstants.DOT);
            throw new UnexpectedGenericTypeException(msg.toString(),
                    MessageIDs.E_UNEXPECTED_EXCEPTION);
        }
    }
}