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
import org.junit.Test;

/** @author BREDEX GmbH */
public class TestAUTAgentConnection {
    /** AUT-Agent host name to use */
    public static final String AGENT_HOST = "g8.dev.bredex.local"; //$NON-NLS-1$
    /** AUT-Agent port to use */
    public static final int AGENT_PORT = 11022;

    /** the actual test method */
    @Test
    public void testConnectingAndDisconnecting() throws Exception {
        final String stillConnectedMessage = "Still connected to AUT-Agent via API"; //$NON-NLS-1$
        final String noConnectionMessage = "No connection to AUT-Agent via API"; //$NON-NLS-1$

        AUTAgent agent = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT);
        Assert.assertNotNull(agent);
        
        agent.connect();
        Assert.assertTrue(noConnectionMessage, agent.isConnected());

        agent.disconnect();
        Assert.assertFalse(stillConnectedMessage, agent.isConnected());

        agent.connect();
        Assert.assertTrue(noConnectionMessage, agent.isConnected());

        agent.disconnect();
        Assert.assertFalse(stillConnectedMessage, agent.isConnected());
    }
}