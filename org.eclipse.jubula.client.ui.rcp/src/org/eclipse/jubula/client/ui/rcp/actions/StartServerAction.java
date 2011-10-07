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

import org.eclipse.jface.action.Action;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ConnectServerBP;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionGUIController;
import org.eclipse.jubula.client.ui.rcp.utils.ServerManager.Server;


/**
 * @author BREDEX GmbH
 * @created 07.04.2006
 */
public class StartServerAction extends Action {
    
    /**
     * <code>m_server</code> current server
     */
    private Server m_server = null;
   

    /**
     * @param server server  
     * @param style style 
     */
    public StartServerAction(Server server, int style) {

        // we add an additional @ char here, because of implemention of  
        // setText(String text) in Action.class. Everything behind the last
        // "@" is used as accelerator
        super(server.getName() + " : " + server.getPort().toString() + "@", style); //$NON-NLS-1$ //$NON-NLS-2$
        m_server = server;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        DataEventDispatcher.getInstance().fireServerConnectionChanged(
                ServerState.Connecting);
        TestExecutionGUIController.connectToServer(m_server);
        ConnectServerBP.getInstance().setCurrentServer(m_server);
    }

}
