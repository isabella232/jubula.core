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
package org.eclipse.jubula.rc.common.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.communication.connection.ConnectionState;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.tools.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.exception.JBVersionException;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.jubula.tools.utils.TimeUtil;


/**
 * Registers an AUT ID with an Aut Agent.
 *
 * @author BREDEX GmbH
 * @created Dec 11, 2009
 */
public class AgentRegisterAut implements IRegisterAut {

    /** the logger */
    private static final Log LOG = LogFactory.getLog(AgentRegisterAut.class);

    /** the address of the Aut Agent with which to register */
    private InetSocketAddress m_agentAddr;
    
    /** the ID of the AUT to register */
    private AutIdentifier m_autIdentifier;
    
    /** connection to the Aut Agent */
    private Socket m_agentConn;

    /**
     * Constructor
     * 
     * @param agentAddr The address of the Aut Agent with which to register.
     * @param autIdentifier The ID of the AUT to register.
     */
    public AgentRegisterAut(InetSocketAddress agentAddr, 
            AutIdentifier autIdentifier) {
        m_agentAddr = agentAddr;
        m_autIdentifier = autIdentifier;
        m_agentConn = null;
    }

    /**
     * {@inheritDoc}
     */
    public void register() throws IOException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Registering AUT '"  //$NON-NLS-1$
                    + m_autIdentifier.getExecutableName() 
                    + "' with agent at "  //$NON-NLS-1$
                    + m_agentAddr.getHostName() + ":" + m_agentAddr.getPort()); //$NON-NLS-1$
        }
        m_agentConn = 
            new Socket(m_agentAddr.getAddress(), m_agentAddr.getPort());
        
        long waitForServer = 10000;
        long waitTime = 0;
        boolean success = false;
        InputStream inputStream = m_agentConn.getInputStream();
        final BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(inputStream));
        PrintStream outputStream = 
            new PrintStream(m_agentConn.getOutputStream());
        while (!success && (waitTime <= waitForServer)) {
            if (inputStream.available() > 0) {
                String line = inputReader.readLine();
                if (line != null) {
                    line = line.substring(0, 
                            line.indexOf(ConnectionState.SEPARATOR));
                }
                success = true;
                if (ConnectionState.CLIENT_TYPE_REQUEST.equals(line)) {
                    final String response = 
                        ConnectionState.CLIENT_TYPE_AUT;

                    outputStream.println(response);
                    outputStream.flush();
                } else {
                    if (LOG.isWarnEnabled()) {
                        StringBuffer errBuf = new StringBuffer();
                        errBuf.append("Received invalid request from server. Expected '") //$NON-NLS-1$
                            .append(ConnectionState.CLIENT_TYPE_REQUEST)
                            .append("' but received '").append(line) //$NON-NLS-1$
                            .append("'."); //$NON-NLS-1$
                        LOG.warn(errBuf.toString());
                    }
                }
            } else {
                TimeUtil.delay(TimingConstantsServer
                        .POLLING_DELAY_AUT_REGISTER);
                waitTime += 500;
            }
        }

        outputStream.println(m_autIdentifier.encode());
        outputStream.flush();

        // wait for communicator host name and port  
        String communicatorHostName = inputReader.readLine();
        String communicatorPort = inputReader.readLine();
        
        try {
            AUTServer.getInstance().initAutAgentCommunicator(
                    InetAddress.getByName(communicatorHostName), 
                    Integer.parseInt(communicatorPort));
        } catch (NumberFormatException nfe) {
            LOG.error("Error occurred while connecting to AUT Agent.", nfe); //$NON-NLS-1$
        } catch (SecurityException se) {
            LOG.error("Error occurred while connecting to AUT Agent.", se); //$NON-NLS-1$
        } catch (JBVersionException gdve) {
            LOG.error("Error occurred while connecting to AUT Agent.", gdve); //$NON-NLS-1$
        }

    }

}
