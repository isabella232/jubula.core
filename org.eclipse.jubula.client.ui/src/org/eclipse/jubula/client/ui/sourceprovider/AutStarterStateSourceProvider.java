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
package org.eclipse.jubula.client.ui.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.communication.ConnectionException;
import org.eclipse.jubula.client.core.communication.ServerConnection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IServerConnectionListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the status of the connection between the
 * client and AUT-Agent.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class AutStarterStateSourceProvider extends AbstractJBSourceProvider 
        implements IServerConnectionListener {
    
    /** 
     * ID of variable that indicates whether the client is currently connected 
     * to an AutStarter
     */
    public static final String IS_AUT_STARTER_CONNECTED = 
        "org.eclipse.jubula.client.ui.variable.isAutStarterConnected"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public AutStarterStateSourceProvider() {
        DataEventDispatcher.getInstance().addServerConnectionListener(
                this, true);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher.getInstance().removeServerConnectionListener(this);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = new HashMap<String, Object>();
        boolean isConnectedToAutStarter = false;
        try {
            isConnectedToAutStarter = 
                ServerConnection.getInstance().isConnected();
        } catch (ConnectionException e) {
            // Not connected. Do nothing.
        }

        currentState.put(IS_AUT_STARTER_CONNECTED, 
                isConnectedToAutStarter);
        
        return currentState;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String [] {IS_AUT_STARTER_CONNECTED};
    }

    /**
     * {@inheritDoc}
     */
    public void handleServerConnStateChanged(ServerState state) {
        gdFireSourceChanged(
                ISources.WORKBENCH, 
                IS_AUT_STARTER_CONNECTED, state == ServerState.Connected);
    }
}
