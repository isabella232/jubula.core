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
package org.eclipse.jubula.client.ui.businessprocess;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerConnectionListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;

/**
 * @author BREDEX GmbH
 * @created Oct 27, 2006
 */
public class DisconnectServerBP extends AbstractActionBP {

    /**
     * <code>instance</code>single instance of ConnectServerBP
     */
    private static DisconnectServerBP instance = null;

    /** true if the client is currently connected to the server */
    private boolean m_isConnected;
    
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
                setEnabledStatus();
            }
        };

        
    /**
     * private constructor
     */
    private DisconnectServerBP() {
        m_isConnected = false;
            
        DataEventDispatcher.getInstance()
            .addServerConnectionListener(m_serverConnectionListener, true);
        
        setEnabledStatus();
    }

        
    /**
     * @return the single instance
     */
    public static DisconnectServerBP getInstance() {
        if (instance == null) {
            instance = new DisconnectServerBP();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return m_isConnected;
    }

}
