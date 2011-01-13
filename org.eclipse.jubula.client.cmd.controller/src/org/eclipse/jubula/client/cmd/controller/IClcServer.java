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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author BREDEX GmbH
 * @created Oct 12, 2010
 */
public interface IClcServer extends Remote {
    /** name for the RMI REGISTRY */
    public static final String SERVICE_ID = "ClcService"; //$NON-NLS-1$
    
    /**
     * 
     * @param tsName Specify the name of the Test Suite which should be run
     * by the service
     * @param timeout the maximum allowed time for the test Suite in seconds
     * @param variables key/value data for GUIdancer variables
     * @return the result code of the test execution, 0 if everything is fine.
     * @throws RemoteException
     */
    public abstract int runTestSuite(String tsName, int timeout, 
            Map<String, String>variables) 
        throws RemoteException;
    
    /**
     * shuts the CLC server down
     * @throws RemoteException
     */
    public abstract void shutdown() throws RemoteException;

}