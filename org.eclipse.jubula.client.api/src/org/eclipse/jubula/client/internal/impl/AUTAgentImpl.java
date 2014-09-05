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
package org.eclipse.jubula.client.internal.impl;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.launch.AUTConfiguration;

/** @author BREDEX GmbH */
public class AUTAgentImpl implements AUTAgent {
    /** the hosts name */
    private String m_hostname;
    /** the port */
    private int m_port;
    /** the connection to the AUT-Agent */
    private AutAgentConnection m_agent;

    /**
     * @param hostname
     *            the hosts name
     * @param port
     *            the port
     */
    public AUTAgentImpl(String hostname, int port) {
        m_hostname = hostname;
        m_port = port;
    }

    /** {@inheritDoc} */
    public void connect() throws Exception {
        AutAgentConnection.createInstance(m_hostname, String.valueOf(m_port));
        m_agent = AutAgentConnection.getInstance();
        m_agent.run();
    }

    /** {@inheritDoc} */
    public void disconnect() {
        if (m_agent != null) {
            m_agent.close();
        }
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_agent != null ? m_agent.isConnected() : false;
    }

    /** {@inheritDoc} */
    public AUT startAUT(AUTConfiguration configuration) throws Exception {
        // currently empty
        return null;
    }

    /** {@inheritDoc} */
    public void stopAUT(AUT aut) throws Exception {
        // currently empty
    }
}