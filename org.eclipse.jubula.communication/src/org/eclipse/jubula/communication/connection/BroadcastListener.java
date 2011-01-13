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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created Sep 4, 2009
 */
public class BroadcastListener extends Thread {

    /**
     * listener interface
     */
    public static interface IBroadcastListener {
        /**
         * called when data arrived on the socket
         * @param data the comple datagram
         */
        public void dataArrived(DatagramPacket data);
    }
    
    /** the destination port number */
    private int m_port;
    /** the socket used during communication */
    private MulticastSocket m_socket;
    /** the destination broadcast network */
    private InetAddress m_network;
    /** are we done yet? */
    private boolean m_done = false;
    /** listener */
    private List m_broadcastListener;
    
    /**
    /**
     * 
     * @param network provide the broadcast network to use in 
     * "aaa.bbb.ccc.ddd" notation
     * @param port specifies the port number to use
     */
    public BroadcastListener(String network, int port)
        throws BroadcastInitException {
        try {
            m_port = port;
            m_socket = new MulticastSocket(m_port);
            m_network = InetAddress.getByName(network);
            m_socket.joinGroup(m_network);
            m_broadcastListener = new ArrayList(10);
        } catch (SocketException e) {
            throw new BroadcastInitException("Can't open socket", e, //$NON-NLS-1$
                    MessageIDs.E_AUT_CONNECTION_INIT);
        } catch (UnknownHostException e) {
            throw new BroadcastInitException("Host network " + network //$NON-NLS-1$
                    + " unknown", e, MessageIDs.E_AUT_CONNECTION_INIT); //$NON-NLS-1$
        } catch (IOException e) {
            throw new BroadcastInitException("Host network " + network //$NON-NLS-1$
                    + " can't be joined", e, MessageIDs.E_AUT_CONNECTION_INIT); //$NON-NLS-1$
        }

    }

    /**
     * tries to get some data from the socket
     * @return a Datagram which contains the data and its origin 
     * @throws BroadcastException in cas of receiver error
     */
    private DatagramPacket readData() throws BroadcastException {
        try {
            byte[] buffer = new byte[65000];
            DatagramPacket received = new DatagramPacket(buffer, buffer.length);
            m_socket.receive(received);
            return received;
        } catch (IOException e) {
            throw new BroadcastException("receive failed", e, //$NON-NLS-1$
                    MessageIDs.E_GENERAL_CONNECTION);
        }
    }
    
    /**
     * get rid of the resources
     */
    public void dispose() throws BroadcastException {
        try {
            m_done  = true;
            if (m_socket != null) {
                synchronized (m_socket) {
                    m_socket.leaveGroup(m_network);
                    m_socket.close();
                    m_network = null;
                    m_broadcastListener.clear();
                    m_broadcastListener = null;
                }
                m_socket = null;
            }
        } catch (IOException e) {
            throw new BroadcastException("dispose failed", e, //$NON-NLS-1$
                    MessageIDs.E_GENERAL_CONNECTION);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {        
        while (!m_done) {
            try {
                DatagramPacket dg = readData();
                fireBroadcastListener(dg);
            } catch (BroadcastException e) {
                try {
                    dispose();
                } catch (BroadcastException e1) {
                    // nothing can be done here
                }
            }
        }
    }
    
    /**
     * standard listener stuff
     * @param l Listener
     */
    public void addBroadcastListener(IBroadcastListener l) {
        m_broadcastListener.add(l);
    }
    
    /**
     * standard listener stuff
     * @param l Listener
     */
    public void removeBroadcastListener(IBroadcastListener l) {
        m_broadcastListener.remove(l);
    }
    
    /**
     * standard listener stuff
     * @param data data for the listener
     */
    private void fireBroadcastListener(DatagramPacket data) {
        for (Iterator it = m_broadcastListener.iterator(); it.hasNext();) {
            IBroadcastListener l = (IBroadcastListener)it.next();
            try {
                l.dataArrived(data);
            } catch (Throwable t) {
                // ignore any problems
            }
        }
    }
}
