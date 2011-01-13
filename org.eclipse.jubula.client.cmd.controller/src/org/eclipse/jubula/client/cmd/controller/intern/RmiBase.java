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

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.eclipse.jubula.client.cmd.controller.IClcServer;


/**
 * @author BREDEX GmbH
 * @created Oct 12, 2010
 */
public class RmiBase {
    
    /** port to be used by RMI registry */
    private int m_portNumber;
    /** RMI registry */
    private Registry m_registry;
    /** anchor for the service. BEWARE: objects for RMI may be garbage 
     * collected even while bound to the registry. TO avoid this the Stub
     * is stored in the RmiBase instance.
     */
    private IClcServer m_clcServerImpl;
    
    /**
     * Start the RMI registry and register the service instance
     * @param portNumber Port for the RMI registry
     * @param service Instance of a CommandLineClient service
     */
    public RmiBase(int portNumber, IClcServer service) {
        m_portNumber = portNumber;
        
        try {
            m_registry = LocateRegistry.createRegistry(m_portNumber);
            registerServices(service);
        } catch (RemoteException e) {
            throw new IllegalArgumentException("Can't create RMI registry on port "  //$NON-NLS-1$
                    + m_portNumber, e);
        } catch (AlreadyBoundException e) {
            throw new IllegalStateException("Can't bind service", e); //$NON-NLS-1$
        }
    }

    /**
     * register the CLC service with the RMI registry
     * @param service an Instance of a CLC service
     * @throws RemoteException see RMI
     * @throws AlreadyBoundException see RMI
     */
    private void registerServices(IClcServer service)
        throws RemoteException, AlreadyBoundException {
        m_clcServerImpl = service;
        IClcServer clcServerStub = 
            (IClcServer)UnicastRemoteObject.exportObject(m_clcServerImpl, 0);
        m_registry.bind(IClcServer.SERVICE_ID, clcServerStub);
    }

    /**
     * @return the registry
     */
    public Registry getRegistry() {
        return m_registry;
    }
    
}

