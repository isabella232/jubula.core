/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.mobile.provider.ui;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.ui.rcp.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.rcp.widgets.autconfig.AutConfigComponent;
import org.eclipse.jubula.toolkit.mobile.provider.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Class to provide the mobile toolkits.
 * 
 * @author BREDEX GmbH
 * 
 */
public class MobileAutConfigComponent extends AutConfigComponent {
    /** gui component */
    private Text m_autHostTextField;
    /** gui component */
    private Text m_autPortTextField;
    /** the WidgetModifyListener */
    private WidgetModifyListener m_modifyListener;

    /**
     * Constructor
     * 
     * @param parent
     *            the parent
     * @param style
     *            the style
     * @param autConfig
     *            the AUT configuration
     * @param autName
     *            the AUTs name
     * @param platform
     *            the AUTs platform
     */
    public MobileAutConfigComponent(Composite parent, int style,
            Map<String, String> autConfig, String autName, String platform) {
        super(parent, style, autConfig, autName, false);
    }

    /**
     * This private inner class contains a new ModifyListener.
     * 
     * @author BREDEX GmbH
     */
    private class WidgetModifyListener implements ModifyListener {
        /** {@inheritDoc} */
        @SuppressWarnings("synthetic-access")
        public void modifyText(ModifyEvent e) {
            Object source = e.getSource();
            boolean checked = false;
            if (source.equals(m_autHostTextField)) {
                checked = true;
            } else if (source.equals(m_autPortTextField)) {
                checked = true;
            } else if (source.equals(getAUTAgentHostNameCombo())) {
                checkLocalhostServer();
                checked = true;
            }
            if (checked) {
                checkAll();
                return;
            }
            Assert.notReached("Event activated by unknown widget."); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    protected void populateBasicArea(Map<String, String> data) {
        super.populateBasicArea(data);

        if (!isDataNew(data)) {
            m_autHostTextField.setText(StringUtils.defaultString(data
                    .get(AutConfigConstants.AUT_HOST)));
            m_autPortTextField.setText(StringUtils.defaultString(data
                    .get(AutConfigConstants.AUT_HOST_PORT)));
        }
    }

    /**
     * 
     * @return the modifier listener.
     */
    @SuppressWarnings("synthetic-access")
    private WidgetModifyListener getModifyListener() {
        if (m_modifyListener == null) {
            m_modifyListener = new WidgetModifyListener();
        }

        return m_modifyListener;

    }

    /** {@inheritDoc} */
    protected void installListeners() {
        super.installListeners();
        WidgetModifyListener modifyListener = getModifyListener();

        getAUTAgentHostNameCombo().addModifyListener(modifyListener);
        m_autHostTextField.addModifyListener(modifyListener);
        m_autPortTextField.addModifyListener(modifyListener);
    }

    /** {@inheritDoc} */
    protected void deinstallListeners() {
        super.deinstallListeners();
        WidgetModifyListener modifyListener = getModifyListener();

        getAUTAgentHostNameCombo().removeModifyListener(modifyListener);
        m_autHostTextField.removeModifyListener(modifyListener);
        m_autPortTextField.removeModifyListener(modifyListener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void checkAll(java.util.List<DialogStatusParameter> paramList) {
        super.checkAll(paramList);
        addError(paramList, modifyAUTHostTextField());
        addError(paramList, modifyAUTPortTextField());
    }

    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns a
     *         status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyAUTHostTextField() {
        DialogStatusParameter error = null;
        String autHostname = m_autHostTextField.getText();
        if (autHostname.length() == 0) {
            error = createErrorStatus("The " + Messages.emptyHostname); //$NON-NLS-1$
        } else {
            putConfigValue(AutConfigConstants.AUT_HOST, autHostname);
        }

        return error;
    }

    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns a
     *         status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyAUTPortTextField() {
        DialogStatusParameter error = null;
        String autPortname = m_autPortTextField.getText();
        if (autPortname.length() == 0) {
            error = createErrorStatus("The " + Messages.emptyPort); //$NON-NLS-1$
        } else {
            putConfigValue(AutConfigConstants.AUT_HOST_PORT, autPortname);
        }

        return error;
    }

    /** {@inheritDoc} */
    protected void populateExpertArea(Map<String, String> data) {
        // this method is empty due to multiMode=false
    }

    /** {@inheritDoc} */
    protected void populateAdvancedArea(Map<String, String> data) {
        // this method is empty due to multiMode=false
    }

    /**
     * @param autHostTextField
     *            the hostTextField
     */
    protected void setAutHostTextField(Text autHostTextField) {
        m_autHostTextField = autHostTextField;
    }

    /**
     * @param autPortTextField
     *            the portTextField
     */
    protected void setAutPortTextField(Text autPortTextField) {
        m_autPortTextField = autPortTextField;
    }
}
