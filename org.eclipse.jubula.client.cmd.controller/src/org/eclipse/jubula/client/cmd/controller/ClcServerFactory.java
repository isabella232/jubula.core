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
package org.eclipse.jubula.client.cmd.controller;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.cmd.controller.intern.RmiClient;


/**
 * @author BREDEX GmbH
 * @created Oct 12, 2010
 */
public abstract class ClcServerFactory {
    
    /** service cache */
    private static Map<Integer, IClcServer> servers = 
        new HashMap<Integer, IClcServer>();

    /**
     * hide
     */
    private ClcServerFactory() {
        // hide
    }

    /**
     * Get a service stub from a specific registry. The stub is cached
     * and will be delivered on subsequent calls without asking the registry.
     * 
     * @param rmiPort RMI registry connection port
     * @return an instance of an IClcServer stub
     */
    public static IClcServer getServer(int rmiPort) {
        IClcServer server = servers.get(rmiPort);
        if (server == null) {
            RmiClient con = new RmiClient(rmiPort);
            server = con.getService();
            servers.put(rmiPort, server);
        }
        return server;
    }

}
