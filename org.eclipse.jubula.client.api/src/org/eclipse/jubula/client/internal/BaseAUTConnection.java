/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.internal;

import java.io.IOException;

import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author BREDEX GmbH */
public class BaseAUTConnection extends BaseConnection {
    /**
     * the timeout used for establishing a connection to a running AUT
     */
    public static final int CONNECT_TO_AUT_TIMEOUT = 10000;
    
    /** the logger */
    static final Logger LOG = LoggerFactory.getLogger(BaseAUTConnection.class);

    /** 
     * The ID of the running AUT with which a connection is currently 
     * established. 
     */
    private AutIdentifier m_connectedAutId;

    /**
     * Constructor
     * 
     * @throws ConnectionException
     *             containing a detailed message why the connection could not
     *             initialized
     */
    public BaseAUTConnection() throws ConnectionException {
        try {
            // create a communicator on any free port
            Communicator communicator = new Communicator(0, this.getClass()
                    .getClassLoader());
            communicator.setIsServerSocketClosable(false);
            setCommunicator(communicator);
        } catch (IOException ioe) {
            handleInitError(ioe);
        } catch (SecurityException se) {
            handleInitError(se);
        }
    }
    
    /**
     * handles the fatal errors occurs during initialization
     * 
     * @param throwable
     *            the occurred exception
     * @throws ConnectionException
     *             a ConnectionException containing a detailed message
     */
    private void handleInitError(Throwable throwable)
        throws ConnectionException {
        String message = "Initialisation of AUTConnection failed: "; //$NON-NLS-1$
        LOG.error(message, throwable);
        throw new ConnectionException(message + throwable.getMessage(), 
            MessageIDs.E_AUT_CONNECTION_INIT);
    }

    /**
     * 
     * @return the ID of the currently connected AUT, or <code>null</code> if 
     *         there is currently no connection to an AUT.
     */
    public AutIdentifier getConnectedAutId() {
        return m_connectedAutId;
    }
    
    /**
     * Disconnects from the currently connected Running AUT. If no connection
     * currently exists, this method is a no-op.
     */
    protected void disconnectFromAut() {
        setConnectedAutId(null);
    }

    /**
     * @param connectedAutId the connectedAutId to set
     */
    protected void setConnectedAutId(AutIdentifier connectedAutId) {
        m_connectedAutId = connectedAutId;
    }
    
    /**
     * Resets this singleton: Closes the communicator
     * removes the listeners.<br>
     * <b>Note: </b><br>
     * This method is used by the Restart-AUT-Action only to avoid errors while
     * reconnecting with the AUTServer.<br>
     * This is necessary because the disconnect from the AUTServer is implemented
     * badly which will be corrected in a future version!
     */
    public synchronized void reset() {
        Communicator communicator = getCommunicator();
        if (communicator != null) {
            communicator.setIsServerSocketClosable(true);
            communicator.interruptAllTimeouts();
            communicator.clearListeners();
            communicator.close();
        }
    }
}