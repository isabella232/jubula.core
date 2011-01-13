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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created Sep 4, 2009
 */
public class BroadcastSender {
    
    /** the destination port number */
    private int m_port;
    /** the socket used during communication */
    private DatagramSocket m_socket;
    /** the destination broadcast network */
    private InetAddress m_network;
    /**
     * 
     * @param network provide the broadcast network to use in 
     * "aaa.bbb.ccc.ddd" notation
     * @param port specifies the port number to use
     */
    public BroadcastSender(String network, int port) 
        throws BroadcastInitException {
        try {
            m_port = port;
            m_socket = new DatagramSocket();
            m_network = InetAddress.getByName(network);
        } catch (SocketException e) {
            throw new BroadcastInitException("Can't open socket", e, //$NON-NLS-1$
                    MessageIDs.E_AUT_CONNECTION_INIT);
        } catch (UnknownHostException e) {
            throw new BroadcastInitException("Host network " + network //$NON-NLS-1$
                    + " unknown", e, MessageIDs.E_AUT_CONNECTION_INIT); //$NON-NLS-1$
        }        
    }
    
    /**
     * Broadcast data to the destination network and port of this instance.
     * @param data array of byte to send
     * @param length number of valid byte, nust be <= data.length
     * @throws BroadcastException if the send failed for any reason
     */
    public void send(byte[] data, int length) throws BroadcastException {
        try {
            DatagramPacket packet = 
                new DatagramPacket(data, length, m_network, m_port);
            m_socket.send(packet);
        } catch (IOException e) {
            throw new BroadcastException("send failed", e, //$NON-NLS-1$
                    MessageIDs.E_GENERAL_CONNECTION);
        }
    }

    /**
     * get rid of the resources
     */
    public void dispose() {
        if (m_socket != null) {
            m_socket.close();
            m_socket = null;
            m_network = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
}
