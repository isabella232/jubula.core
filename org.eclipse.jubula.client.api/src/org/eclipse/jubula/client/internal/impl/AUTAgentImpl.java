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

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.Remote;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.BaseConnection.AlreadyConnectedException;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.message.GetRegisteredAutListMessage;
import org.eclipse.jubula.communication.internal.message.StartAUTServerMessage;
import org.eclipse.jubula.communication.internal.message.StopAUTServerMessage;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author BREDEX GmbH */
public class AUTAgentImpl implements AUTAgent {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTAgentImpl.class);
    /** the hosts name */
    private String m_hostname;
    /** the port */
    private String m_port;
    /** the connection to the AUT-Agent */
    private AutAgentConnection m_agent;

    /**
     * @param hostname
     *            the hosts name
     * @param iPort
     *            the port
     */
    public AUTAgentImpl(String hostname, int iPort) {
        Validate.notEmpty(hostname, "The hostname must not be empty."); //$NON-NLS-1$
        final String port = String.valueOf(iPort);
        String portNumberMessage = NetUtil.isPortNumberValid(port);
        Validate.isTrue(portNumberMessage == null, portNumberMessage);
        
        m_hostname = hostname;
        m_port = port;
    }

    /** {@inheritDoc} */
    public void connect() throws CommunicationException {
        if (!isConnected()) {
            try {
                AutAgentConnection.createInstance(m_hostname, m_port);
                m_agent = AutAgentConnection.getInstance();
                m_agent.run();
                if (!isConnected()) {
                    throw new CommunicationException(
                        new ConnectException(
                            "Could not connect to AUT-Agent: " //$NON-NLS-1$
                                + m_hostname + ":" + m_port)); //$NON-NLS-1$
                }
            } catch (ConnectionException e) {
                throw new CommunicationException(e);
            } catch (AlreadyConnectedException e) {
                throw new CommunicationException(e);
            } catch (JBVersionException e) {
                throw new CommunicationException(e);
            }
        } else {
            throw new IllegalStateException("AUT-Agent connection is already made"); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public void disconnect() {
        if (isConnected()) {
            m_agent.close();
        } else {
            throw new IllegalStateException("AUT-Agent connection is already disconnected"); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_agent != null ? m_agent.isConnected() : false;
    }

    /** {@inheritDoc} */
    public AUTIdentifier startAUT(
        @NonNull AUTConfiguration configuration)
        throws CommunicationException {
        Validate.notNull(configuration, "The configuration must not be null."); //$NON-NLS-1$
        checkConnected(this);
        
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

        try {
            m_agent.send(startAUTMessage);
            Object genericStartResponse = Synchronizer.instance()
                .exchange(null);
            if (genericStartResponse instanceof Integer) {
                int startResponse = (Integer) genericStartResponse;
                return handleResponse(startResponse);
            }
            log.error("Unexpected start response code received: " //$NON-NLS-1$
                + String.valueOf(genericStartResponse));
        } catch (NotConnectedException e) {
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            throw new CommunicationException(e);
        }
        
        return null;
    }

    /**
     * @param startResponse
     *            the AUT start response
     * @return the AUT or <code>null<code> if problem during start
     */
    private AutIdentifier handleResponse(int startResponse) 
        throws CommunicationException {
        if (startResponse == AUTStartResponse.OK) {
            Object autIdentifier;
            try {
                autIdentifier = Synchronizer.instance().exchange(null);
                if (autIdentifier instanceof AutIdentifier) {
                    return (AutIdentifier) autIdentifier;
                }
                log.error("Unexpected AUT identifier received: " //$NON-NLS-1$
                    + String.valueOf(autIdentifier));
            } catch (InterruptedException e) {
                throw new CommunicationException(e);
            }
        }

        return null;
    }
    

    /** {@inheritDoc} */
    public void stopAUT(
        @NonNull AUTIdentifier aut) 
        throws CommunicationException {
        Validate.notNull(aut, "The AUT-Identifier must not be null."); //$NON-NLS-1$
        checkConnected(this);
        
        try {
            m_agent.send(new StopAUTServerMessage((AutIdentifier)aut));
        } catch (NotConnectedException e) {
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            throw new CommunicationException(e);
        }
    }

    /** {@inheritDoc} */
    @NonNull
    public List<AUTIdentifier> getAllRegisteredAUTIdentifier()
        throws CommunicationException {
        checkConnected(this);
        
        try {
            m_agent.send(new GetRegisteredAutListMessage());
            Object arrayOfAutIdentifier = Synchronizer.instance()
                .exchange(null);
            if (arrayOfAutIdentifier instanceof AutIdentifier[]) {
                final List<AUTIdentifier> unmodifiableList = Collections
                    .unmodifiableList(Arrays
                        .asList((AUTIdentifier[]) arrayOfAutIdentifier));
                if (unmodifiableList != null) {
                    return unmodifiableList;
                }
            }

            log.error("Unexpected AUT identifiers received: " //$NON-NLS-1$
                + String.valueOf(arrayOfAutIdentifier));
        } catch (NotConnectedException e) {
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            throw new CommunicationException(e);
        }

        return new ArrayList<AUTIdentifier>(0);
    }

    /** {@inheritDoc} */
    @NonNull public AUT getAUT (
        @NonNull AUTIdentifier autID, 
        @NonNull ToolkitInfo information) 
        throws CommunicationException {
        Validate.notNull(autID, "The AUT-Identifier must not be null."); //$NON-NLS-1$
        Validate.notNull(information, "The toolkit information must not be null."); //$NON-NLS-1$
        checkConnected(this);
        
        return new AUTImpl((AutIdentifier) autID, information);
    }
    
    /**
     * @param side
     *            the side to check the connection state for
     */
    static void checkConnected(Remote side) {
        if (!side.isConnected()) {
            throw new IllegalStateException("There is currently no connection established to the remote side - call connect() first!"); //$NON-NLS-1$
        }
    }
}