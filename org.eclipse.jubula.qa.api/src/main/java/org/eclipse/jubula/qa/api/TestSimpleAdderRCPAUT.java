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

import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.OM;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.toolkit.rcp.config.RCPAUTConfiguration;
import org.eclipse.jubula.toolkit.swt.SwtComponentFactory;
import org.eclipse.jubula.toolkit.swt.SwtToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** @author BREDEX GmbH */
public class TestSimpleAdderRCPAUT {
    /** AUT-Agent host name to use */
    public static final String AGENT_HOST = "g8.dev.bredex.local"; //$NON-NLS-1$
    /** AUT-Agent port to use */
    public static final int AGENT_PORT = 11022;
    /** the ERR_STILL_CONNECTED */
    private static final String ERR_STILL_CONNECTED = "Still connected to AUT-Agent via API"; //$NON-NLS-1$
    /** the ERR_NOT_CONNECTED */
    private static final String ERR_NOT_CONNECTED = "No connection to AUT-Agent via API"; //$NON-NLS-1$
    /** the AUT-Agent */
    private AUTAgent m_agent;
    /** the AUT */
    private AUT m_aut;
    /** the object mapping */
    private OM m_om;

    /** prepare */
    @Before
    public void setUp() throws Exception {
        m_om = MakeR.createOM();
        m_om.init(this.getClass().getClassLoader()
            .getResource("objectMapping_SimpleAdderRCP.properties")); //$NON-NLS-1$

        
        m_agent = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT);
        Assert.assertNotNull(m_agent);
        
        m_agent.connect();
        Assert.assertTrue(ERR_NOT_CONNECTED, m_agent.isConnected());
        
        final String autID = "SimpleAdder";  //$NON-NLS-1$
        AUTConfiguration config = new RCPAUTConfiguration(
            "api.aut.conf.simple.adder.rcp",  //$NON-NLS-1$
            autID,
            "SimpleAdder.exe", //$NON-NLS-1$
            "k:\\guidancer\\Workspace\\hu_snapshot\\current\\platforms\\win32.win32.x86\\examples\\AUTs\\SimpleAdder\\rcp\\win32\\win32\\x86\\", //$NON-NLS-1$ 
            new String[]{
                "-clean" , //$NON-NLS-1$
                "-configuration", //$NON-NLS-1$
                "@none", //$NON-NLS-1$
                "-data", //$NON-NLS-1$
                "@none"}, //$NON-NLS-1$
            Locale.getDefault(), 
            Locale.getDefault());
        
        AUTIdentifier id = m_agent.startAUT(config);
        
        // this e.g. might not be the case if AUT-Agent is running in
        // non-lenient mode and multiple AUTs with the same ID are supposed to
        // start
        Assert.assertEquals(id.getID(), autID);
        
        List<AUTIdentifier> allRegisteredAUTIdentifier = m_agent
            .getAllRegisteredAUTIdentifier();
        
        Assert.assertTrue("Expected ID not found", //$NON-NLS-1$
            allRegisteredAUTIdentifier.contains(id)); 
        
        m_aut = m_agent.getAUT(id);
        
        Assert.assertTrue(!m_aut.isConnected());
        
        ToolkitInfo tkInfo = new SwtToolkitInfo();
        m_aut.setTypeMapping(tkInfo.getTypeMapping());
        m_aut.connect();
    }

    /** the actual test method */
    @Test(expected = CheckFailedException.class)
    public void testAUT() throws Exception {
        Assert.assertTrue(m_aut.isConnected());
        ComponentIdentifier buttonIdentifier = m_om.get("equalsButton"); //$NON-NLS-1$

        ButtonComponent buttonComponent = SwtComponentFactory
            .createButton(buttonIdentifier);
        
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            long startTime = System.currentTimeMillis();
            m_aut.execute(buttonComponent.click(1, 1));
            System.out.println("CAP took: "  //$NON-NLS-1$
                + (System.currentTimeMillis() - startTime));
        }

        try {
            m_aut.execute(buttonComponent.checkEnablement(true));
            m_aut.execute(buttonComponent.checkText("abc", Operator.equals)); //$NON-NLS-1$
        } catch (CheckFailedException e) {
            System.out.println(e.getActualValue());
            throw e;
        }
    }

    /** cleanup */
    @After
    public void tearDown() throws Exception {
        m_aut.disconnect();
        Assert.assertTrue(!m_aut.isConnected());
       
        m_agent.stopAUT(m_aut.getIdentifier());
        
        m_agent.disconnect();
        Assert.assertFalse(ERR_STILL_CONNECTED, m_agent.isConnected());
    }
}