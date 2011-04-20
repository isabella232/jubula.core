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
package org.eclipse.jubula.communication.message;

import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * Message sent to an AUT Server containing information necessary to establish a
 * connection to a waiting client.
 * 
 * @author BREDEX GmbH
 * @created Mar 22, 2010
 * 
 */
public class ConnectToClientMessage extends Message {

    /** Static version */
    public static final double VERSION = 1.0;

    /** the host name at which the client is listening */
    private String m_clientHostName;

    /** the port number on which the client is listening */
    private int m_clientPort;

    /**
     * Default constructor for transportation layer. Don't use for normal
     * programming.
     * 
     * @deprecated
     */
    public ConnectToClientMessage() {
        super();
    }

    /**
     * Constructor
     * 
     * @param clientHostName
     *            The host name at which the client is listening.
     * @param clientPort
     *            The port number on which the client is listening.
     */
    public ConnectToClientMessage(String clientHostName, int clientPort) {
        m_clientHostName = clientHostName;
        m_clientPort = clientPort;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.CONNECT_TO_CLIENT_COMMAND;
    }

    /** {@inheritDoc} */
    public double getVersion() {
        return VERSION;
    }

    /** @return the host name at which the client is listening. */
    public String getClientHostName() {
        return m_clientHostName;
    }

    /** @return the port nubmer on which the client is listening. */
    public int getClientPort() {
        return m_clientPort;
    }
}