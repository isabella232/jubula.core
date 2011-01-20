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
package org.eclipse.jubula.client.ui.actions;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.DisconnectServerBP;
import org.eclipse.jubula.client.ui.controllers.TestExecutionGUIController;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.widgets.Event;


/**
 * 
 *
 * @author BREDEX GmbH
 * @created 11.05.2005
 *
 */
public class DisconnectAction extends AbstractAction {

    /** The eclipse job manager */
    private IJobManager m_jobManager = Job.getJobManager();
    /** The job family String */
    private String m_jobFamily = I18n.getString("Client.CollectingInformation"); //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     *      org.eclipse.swt.widgets.Event)
     */
    public void runWithEvent(IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        if (isJobRunning()) {
            MessageDialog dialog = getConfirmDialog();
            if (dialog.getReturnCode() != Window.OK) {
                return;
            }
            m_jobManager.cancel(m_jobFamily);
        }
        TestExecutionGUIController.disconnectFromServer();
    }

    /**
     * @return the ActionBP object associated with this Action
     */
    protected AbstractActionBP getActionBP() {
        return DisconnectServerBP.getInstance();
    }    
    /**
     * Checks whether a monitoring job is running or not.
     * @return true if jobs are running, or false if no monitoring job is running
     */
    private boolean isJobRunning() {
        
        Job[] jobs = m_jobManager.find(m_jobFamily);        
        if (jobs.length > 0) {
            return true;
        }
        return false;
        
    }
    /**
     * @return a confirm Dialog, if monitoring job is still running.
     */
    private MessageDialog getConfirmDialog() {      
                
        MessageDialog dialog = new MessageDialog(Plugin.getShell(), 
            I18n.getString("Client.DisconnectFromServerTitle"), //$NON-NLS-1$
                null,
                I18n.getString("Client.DisconnectFromServerMessage"), //$NON-NLS-1$
                MessageDialog.QUESTION, new String[] {
                    I18n.getString("NewProjectDialog.MessageButton0"), //$NON-NLS-1$
                    I18n.getString("NewProjectDialog.MessageButton1") }, 0); //$NON-NLS-1$
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog;
    }
}