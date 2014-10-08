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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.message.GetRegisteredAutListMessage;
import org.eclipse.jubula.communication.internal.message.StartAUTServerMessage;
import org.eclipse.jubula.communication.internal.message.StopAUTServerMessage;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author BREDEX GmbH */
public class AUTAgentImpl implements AUTAgent {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTAgentImpl.class);
    
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
    public AutIdentifier startAUT(AUTConfiguration configuration)
        throws Exception {
        Map<String, String> autConfigMap = configuration.getLaunchInformation();

        // add relevant information for the AUT-Agent
        final Communicator communicator = m_agent.getCommunicator();
        autConfigMap.put(AutConfigConstants.AUT_AGENT_PORT,
            String.valueOf(communicator.getPort()));
        autConfigMap.put(AutConfigConstants.AUT_AGENT_HOST,
            communicator.getHostName());
        autConfigMap.put(AutConfigConstants.AUT_NAME,
            autConfigMap.get(AutConfigConstants.AUT_ID));

        String toolkitID = autConfigMap.get(ToolkitConstants.ATTR_TOOLKITID);
        StartAUTServerMessage startAUTMessage = new StartAUTServerMessage(
            autConfigMap, toolkitID);

        m_agent.send(startAUTMessage);
        
        Object genericStartResponse = Synchronizer.instance().exchange(null);
        if (genericStartResponse instanceof Integer) {
            int startResponse = (Integer) genericStartResponse;
            return handleResponse(startResponse);
        }
        log.error("Unexpected start response code received: " //$NON-NLS-1$
            + String.valueOf(genericStartResponse));
        return null;
    }

    /**
     * @param startResponse
     *            the AUT start response
     * @return the AUT or <code>null<code> if problem during start
     */
    private AutIdentifier handleResponse(int startResponse) 
        throws Exception {
        if (startResponse == AUTStartResponse.OK) {
            Object autIdentifier = Synchronizer.instance().exchange(null);
            if (autIdentifier instanceof AutIdentifier) {
                return (AutIdentifier) autIdentifier;
            }
            log.error("Unexpected AUT identifier received: " //$NON-NLS-1$
                + String.valueOf(autIdentifier));
        }

        return null;
    }
    

    /** {@inheritDoc} */
    public void stopAUT(final AutIdentifier aut) throws Exception {
        m_agent.send(new StopAUTServerMessage(aut));
    }

    /** {@inheritDoc} */
    public List<AutIdentifier> getAllRegisteredAUTIdentifier() 
        throws Exception {
        m_agent.send(new GetRegisteredAutListMessage());

        Object arrayOfAutIdentifier = Synchronizer.instance().exchange(null);
        if (arrayOfAutIdentifier instanceof AutIdentifier[]) {
            return Collections.unmodifiableList(Arrays
                .asList((AutIdentifier[]) arrayOfAutIdentifier));
        }
        log.error("Unexpected AUT identifiers received: " //$NON-NLS-1$
            + String.valueOf(arrayOfAutIdentifier));
        
        return null;
    }
}