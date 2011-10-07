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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerConnectionListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerPrefListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.utils.ServerManager;
import org.eclipse.jubula.client.ui.rcp.utils.ServerManager.Server;



/**
 * @author BREDEX GmbH
 * @created 07.04.2006
 * 
 */
public final class ConnectServerBP extends AbstractActionBP {
    /**
     * <code>instance</code>single instance of ConnectServerBP
     */
    private static ConnectServerBP instance = null;
    
    /**
     * <code>m_currentServer</code> current used server
     */
    private Server m_currentServer = null;

    /**
     * <code>m_serverFromPref</code> list with all configured servers from
     * preference store
     */
    private Set <Server> m_serverFromPref = 
        ServerManager.getInstance().getServers();

    /** true if the client is currently connected to the server */
    private boolean m_isConnected;
    
    /** true if the client is currently connecting to the server */
    private boolean m_isConnecting;
    
    /**
     * <code>m_serverConnectionListener</code> listener for modification of 
     * server connection state
     */
    private IServerConnectionListener m_serverConnectionListener = 
        new IServerConnectionListener() {
            /**
             * @param state the new state of the server connection
             */
            @SuppressWarnings("synthetic-access") 
            public void handleServerConnStateChanged(ServerState state) {
                m_isConnected = 
                    (state == DataEventDispatcher.ServerState.Connected);
                m_isConnecting = 
                    (state == DataEventDispatcher.ServerState.Connecting);
                setEnabledStatus();
            }
        };
    
    /**
     * <code>m_serverPrefListener</code> listener for modification of server preferences
     */
    private IServerPrefListener m_serverPrefListener = 
        new IServerPrefListener() {
            @SuppressWarnings("synthetic-access")     
            public void handlePrefServerChanged() {
                if (m_currentServer != null
                    && !ServerManager.getInstance().getServers()
                        .contains(m_currentServer)) {
                    m_currentServer = null;
                }
                
                setEnabledStatus();
            }
        };

    
    
    /**
     * private constructor
     */
    private ConnectServerBP() {
        m_isConnected = false;
        
        DataEventDispatcher.getInstance()
            .addServerConnectionListener(m_serverConnectionListener, true);
        DataEventDispatcher.getInstance()
            .addServerPrefListener(m_serverPrefListener, true);
        
        setEnabledStatus();
    }

    /**
     * @return the single instance
     */
    public static ConnectServerBP getInstance() {
        if (instance == null) {
            instance = new ConnectServerBP();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return !m_isConnected && !isServerListEmpty() && !m_isConnecting;
    }

    /**
     * @param server server to set
     */
    public void setCurrentServer(Server server) {
        m_currentServer = server;
        ServerManager.getInstance().setLastUsedServer(server);
        ServerManager.getInstance().storeServerList();
    }

    /**
     * @return Returns the currentServer.
     */
    public Server getCurrentServer() {
        return m_currentServer;
    }
    
    /**
     * @return the currently used server, either the last used server or the
     * single server is available in preference store or null!
     */
    public Server getWorkingServer() {
        Server currentServer = null;
        // in current session was eventually started a server
        final Server lastUsedServer = 
            ServerManager.getInstance().getLastUsedServer();
        if (m_currentServer != null) {
            currentServer = m_currentServer;
        // not yet started server in current session, but last used server
        // from last session saved in preference store
        } else if (lastUsedServer != null) {
            currentServer = lastUsedServer;
            // no last used server available in preference store, but exactly one
            // server from preference store (maybe the default server)
        } else if (ServerManager.getInstance().getServers().size() == 1) {
            currentServer =
                ServerManager.getInstance().getServers().iterator().next();  
        } 
        // no error dialog because a default server is ever available
        // see org.eclipse.jubula.client.ui.rcp.Plugin.getServerAndPort()*/
        return currentServer;
    }

    /**
     * @return list of all AUTConfigs without a corresponding entry for their
     * server in server preferences
     */
    public List<IAUTConfigPO> computeUnconfiguredServers() {
        List<IAUTConfigPO> unconfServers = new ArrayList<IAUTConfigPO>();
        Set<IAUTConfigPO> confs = new HashSet<IAUTConfigPO>();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            // get all AUTs from project
            Set<IAUTMainPO> auts = project.getAutMainList();
            // get all AUTConfigs of available AUTs
            for (IAUTMainPO aut : auts) {
                if (!aut.getAutConfigSet().isEmpty()) {
                    Iterator<IAUTConfigPO> it = aut.getAutConfigSet()
                            .iterator();
                    while (it.hasNext()) {
                        confs.add(it.next());
                    }
                }
            }
            for (IAUTConfigPO conf : confs) {
                if (isUnconfiguredServer(conf.getServer())) {
                    unconfServers.add(conf);
                }                
            }
        }
        return unconfServers;
    }

    /**
     * @param serverName name of server to validate
     * @return if servername is not contained in preferences
     */
    private boolean isUnconfiguredServer(String serverName) {
        for (Server server : m_serverFromPref) {
            if (server.getName().equals(serverName)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @return true if the list of servers is empty
     */
    private boolean isServerListEmpty() {
        return ServerManager.getInstance().getServers().isEmpty();
    }
    
}
