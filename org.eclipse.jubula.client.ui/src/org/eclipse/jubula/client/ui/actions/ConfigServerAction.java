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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.dialogs.NewServerPortDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ServerManager;
import org.eclipse.jubula.client.ui.utils.ServerManager.Server;
import org.eclipse.swt.widgets.Display;



/**
 * @author BREDEX GmbH
 * @created 11.04.2006
 */
public class ConfigServerAction extends Action {
    
    /**
     * <code>m_serverName</code>name of server to configure
     */
    private String m_serverName = null;
    /**
     * <code>m_jre</code>jre of AUTConfig using m_serverName
     */
    private String m_jre;

    /**
     * @param server name of server to configure
     * @param jre corresponding jre for this server in AUTConfig
     */
    public ConfigServerAction(String server, String jre) {
        super(server + "..."); //$NON-NLS-1$
        m_serverName = server;
        m_jre = jre;
    }
    
    /**
     * {@inheritDoc}
     */
    public void run() {
        Display.getDefault().asyncExec(new Runnable() {
            @SuppressWarnings("synthetic-access")
            public void run() {
                ServerManager serverMgr = ServerManager.getInstance();
                Server server = new Server(m_serverName, Integer.valueOf(-1));
                NewServerPortDialog dialog = new NewServerPortDialog(
                    Plugin.getShell(), server);
                dialog.create();
                DialogUtils.setWidgetNameForModalDialog(dialog);
                dialog.open();
                if (dialog.getReturnCode() == Window.OK) {
                    m_serverName = dialog.getServer();
                    Server server1 = new Server(m_serverName, dialog.getPort());
                    serverMgr.addServer(server);                
                    serverMgr.addJRE(m_serverName, m_jre);
                    serverMgr.storeServerList();
                    // start the currently configured server
                    StartServerAction action = new StartServerAction(
                        server1, IAction.AS_CHECK_BOX);
                    action.setServer(server);
                    action.run();
                } else {
                    serverMgr.removeServer(server);
                }
            }
        });
    }

    /**
     * @return Returns the serverName.
     */
    public String getServerName() {
        return m_serverName;
    }

}
