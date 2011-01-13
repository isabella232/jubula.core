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
package org.eclipse.jubula.client.cmd.controller.intern;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.eclipse.jubula.client.cmd.controller.IClcServer;


/**
 * @author BREDEX GmbH
 * @created Oct 12, 2010
 */
public class RmiClient {
    /** port to be used by RMI registry */
    private int m_portNumber;

    /** RMI registry */
    private Registry m_registry;
    /** cache for the service */
    private IClcServer m_clcService;

    /**
     * Find the service by asking the RMI registry on port portNumber
     * @param portNumber port of the RMI registry
     */
    public RmiClient(int portNumber) {
        m_portNumber = portNumber;

        try {
            m_registry = LocateRegistry.getRegistry(m_portNumber);
            findServices();
        } catch (RemoteException e) {
            throw new IllegalArgumentException(
                    "Can't connect to RMI registry on port " + m_portNumber, e); //$NON-NLS-1$
        } catch (NotBoundException e) {
            throw new IllegalStateException("Can't bind service", e); //$NON-NLS-1$
        }
    }

    /**
     * lookup the CLC service 
     * @throws RemoteException see RMI
     * @throws NotBoundException see RMI
     */
    private void findServices() throws RemoteException, NotBoundException {
        m_clcService = (IClcServer)m_registry.lookup(IClcServer.SERVICE_ID);
        assert (m_clcService != null);
    }

    /**
     * @return the CLC service
     */
    public IClcServer getService() {
        return m_clcService;
    }
}
