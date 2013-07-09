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
package org.eclipse.jubula.autagent.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.jubula.communication.connection.RestartAutProtocol;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Restarts an AUT that was started with autrun.
 *
 * @author BREDEX GmbH
 * @created Mar 26, 2010
 */
public class RestartAutAutRun implements IRestartAutHandler {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(RestartAutAutRun.class);
    
    /** the ID of the started AUT */
    private AutIdentifier m_autId;

    /** the socket used for communicating with autrun */
    private Socket m_autrunSocket;
    
    /** reader used for communicating with autrun */
    private BufferedReader m_socketReader;
    
    /**
     * Constructor
     * 
     * @param autId The ID of the started AUT.
     * @param socket The socket used for communicating with autrun.
     * @param reader Reader for the given socket.
     */
    public RestartAutAutRun(AutIdentifier autId, Socket socket,
            BufferedReader reader) {

        m_autId = autId;
        m_autrunSocket = socket;
        m_socketReader = reader;
    }

    /**
     * {@inheritDoc}
     */
    public void restartAut(AutAgent agent, int timeout) {
        try {
            PrintWriter writer = new PrintWriter(
                    m_autrunSocket.getOutputStream(), true);

            writer.println(RestartAutProtocol.REQ_PREPARE_FOR_RESTART);
            m_socketReader.readLine();

            agent.stopAut(m_autId, timeout);
            
            writer.println(RestartAutProtocol.REQ_RESTART);
            
        } catch (IOException e) {
            LOG.error("Error occurred while restarting AUT.", e); //$NON-NLS-1$
        }
        
    }

}
