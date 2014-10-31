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
package org.eclipse.jubula.qa.api;

import junit.framework.Assert;

import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.junit.Before;
import org.junit.Test;

/** @author BREDEX GmbH */
public class TestAutAgentConnection {
    /** AUT-Agent host name to use */
    public static final String AGENT_HOST = "localhost"; //$NON-NLS-1$
    /** AUT-Agent port to use */
    public static final int AGENT_PORT = 5051;
    /** AUT-Agent port to use */
    public static final int AGENT_PORT_SECOND = 50512;
    /** the AUT-Agent */
    private AUTAgent m_agent;
    
    /** the AUT-Agent */
    private AUTAgent m_agentSecond;

    
    /** prepare */
    @Before
    public void setUp() throws Exception {
        m_agent = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT);
        m_agentSecond = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT_SECOND);
    }

    /** switches between the two autagents 
     * @throws Exception */
    @Test
    public void autAgentSwitching() throws Exception {
        m_agent.connect();
        Assert.assertEquals(true, m_agent.isConnected());
        m_agent.disconnect();
        Assert.assertEquals(false, m_agent.isConnected());
        m_agent.connect();
        Assert.assertEquals(true, m_agent.isConnected());
    
    }
    
    /** switches between the two autagents 
     * @throws Exception */
    @Test
    public void autAgentDouble() throws Exception {
        m_agent.connect();
        Assert.assertEquals(true, m_agent.isConnected());
        m_agent.disconnect();
        m_agentSecond.connect();
        Assert.assertEquals(true, m_agentSecond.isConnected());
        Assert.assertEquals(false, m_agent.isConnected());
        m_agentSecond.disconnect();
    }
    
}