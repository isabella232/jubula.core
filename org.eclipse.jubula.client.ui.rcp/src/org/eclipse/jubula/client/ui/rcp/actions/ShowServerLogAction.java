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
package org.eclipse.jubula.client.ui.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jubula.client.ui.rcp.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ShowServerLogBP;
import org.eclipse.jubula.client.ui.rcp.editors.ISimpleEditorInput;
import org.eclipse.jubula.client.ui.rcp.editors.ServerLogInput;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.communication.message.ServerLogResponseMessage;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;


/**
 * @author BREDEX GmbH
 * @created Feb 8, 2007
 */
public class ShowServerLogAction extends AbstractAction {

    /** The window associated with this action */
    private IWorkbenchWindow m_window;
    
    /** single instance of the ServerLogInput */
    private ISimpleEditorInput m_serverLogInput = null;

    /**
     * {@inheritDoc}
     */
    public void init(IWorkbenchWindow window) {
        m_window = window;
    }

    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        
        ServerLogResponseMessage response = 
            ShowServerLogBP.getInstance().requestServerLog();

        if (response != null) {
            int status = response.getStatus();
            if (status == ServerLogResponseMessage.OK) {
                IWorkbenchPage currentPage = m_window.getActivePage();
                if (currentPage != null) {
                    if (m_serverLogInput != null 
                        && currentPage.findEditor(m_serverLogInput) != null) {
                        currentPage.closeEditor(
                            currentPage.findEditor(m_serverLogInput), false);
                    }
                    
                    m_serverLogInput = 
                        new ServerLogInput(response.getServerLog());
                    try {
                        currentPage.openEditor(m_serverLogInput, 
                            "org.eclipse.jubula.client.ui.rcp.editors.LogViewer"); //$NON-NLS-1$
                    } catch (PartInitException e) {
                        ErrorHandlingUtil.createMessageDialog(
                                MessageIDs.E_CANNOT_OPEN_EDITOR);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return ShowServerLogBP.getInstance();
    }

}
