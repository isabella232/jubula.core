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
package org.eclipse.jubula.communication.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jubula.tools.jarutils.IVersion;
import org.eclipse.jubula.tools.utils.TimeUtil;


/**
 * A serversocket implementing the protocol for establishing a connection. <br>
 * 
 * The method send() sends a state code of the server. It's called immediately
 * after accept() by the AcceptingThread of the communicator.
 * 
 * @author BREDEX GmbH
 * @created 20.09.2004
 */
public class AutStarterSocket extends ServerSocket {

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AutStarterSocket.class);
    
    /**
     * @param port
     *            the portnumber to use, zero means any free port
     * @throws java.io.IOException
     *             from constructor of super class ServerSocket
     */
    public AutStarterSocket(int port) throws IOException {
        super(port);
    }
    
    /**
     * Write the state to the socket. The state is converted to a string and terminated by a new line
     * character. 
     * @param socket
     *            the socket created by accept.
     * @param state
     *            the state to send see ConnectionState
     * @throws IOException
     *             from getting (and writing to) the outputstream of the given
     *             socket
     */
    public static void send(Socket socket, int state) throws IOException {
        PrintStream outputStream = new PrintStream(socket.getOutputStream());
        final String status = state 
            + ConnectionState.SEPARATOR 
            + IVersion.JB_PROTOCOL_MAJOR_VERSION;
        if (log.isDebugEnabled()) {
            log.debug("sending state: " + String.valueOf(status)); //$NON-NLS-1$
        }
        outputStream.println(String.valueOf(status));
        outputStream.flush();
    }

    /**
     * Sends a request to the client using the given socket and returns the 
     * response received via the given reader.
     * 
     * @param socket The socket on which the communication will take place.
     * @param reader Reader for the given socket's input stream.
     * @param timeout Maximum time to wait (in milliseconds) for a response.
     * @return the response received from the client, or <code>null</code> if a 
     *         timeout occurs.
     * @throws IOException
     *             from getting (and writing to) the outputstream of the given
     *             socket
     */
    public static String requestClientType(Socket socket, 
            BufferedReader reader, long timeout) throws IOException {
        PrintStream outputStream = new PrintStream(socket.getOutputStream());
        final String request = ConnectionState.CLIENT_TYPE_REQUEST 
            + ConnectionState.SEPARATOR 
            + IVersion.JB_PROTOCOL_MAJOR_VERSION;

        log.debug("sending request: " + request); //$NON-NLS-1$

        outputStream.println(request);
        outputStream.flush();

        if (log.isDebugEnabled()) {
            log.debug("waiting for client type response using timeout: " //$NON-NLS-1$ 
                    + String.valueOf(timeout));
        }

        long waitTime = 0;
        while (waitTime <= timeout) {
            if (socket.getInputStream().available() > 0) {
                return reader.readLine();
                
            }
            TimeUtil.delay(500);
            waitTime += 500;
        }

        log.debug("no client type response received from client"); //$NON-NLS-1$
        return null;
    }
    
}
