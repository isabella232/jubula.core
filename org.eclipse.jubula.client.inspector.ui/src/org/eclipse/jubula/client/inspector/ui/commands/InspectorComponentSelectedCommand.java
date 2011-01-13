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
package org.eclipse.jubula.client.inspector.ui.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.communication.ConnectionException;
import org.eclipse.jubula.client.inspector.ui.model.InspectedComponent;
import org.eclipse.jubula.client.inspector.ui.provider.sourceprovider.InspectorStateProvider;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.InspectorComponentSelectedMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;


/**
 * @author BREDEX GmbH
 * @created Jun 10, 2009
 */
public class InspectorComponentSelectedCommand implements ICommand {

    /** the logger */
    private static Log log = 
        LogFactory.getLog(InspectorComponentSelectedCommand.class);
    
    /** the message */
    private InspectorComponentSelectedMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (InspectorComponentSelectedMessage)message;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        try {
            AUTConnection.getInstance().close();
        } catch (ConnectionException ce) {
            // Connection already closed. Do nothing.
            log.info("Attempted to close an uninitialized connection.", ce); //$NON-NLS-1$
        }
        IWorkbenchWindow activeWorkbenchWindow = 
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            IWorkbenchWindow [] windows = 
                PlatformUI.getWorkbench().getWorkbenchWindows();
            if (windows.length > 0) {
                activeWorkbenchWindow = windows[0];
                final Shell shell = activeWorkbenchWindow.getShell();
                shell.getDisplay().syncExec(new Runnable() {

                    public void run() {
                        shell.getShell().setMinimized(false);
                        shell.setActive();
                    }
                    
                });
            }
        }
        
        InspectedComponent.getInstance().setCompId(
                m_message.getComponentIdentifier());
        
        ISourceProviderService service = 
            (ISourceProviderService)PlatformUI.getWorkbench().getService(
                    ISourceProviderService.class);
        InspectorStateProvider sourceProvider = 
            (InspectorStateProvider)service.getSourceProvider(
                    InspectorStateProvider.IS_INSPECTOR_ACTIVE);
        sourceProvider.setInspectorActive(false);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}
