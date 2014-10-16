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

import java.net.URL;
import java.util.Locale;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.toolkit.base.components.GraphicsComponent;
import org.eclipse.jubula.toolkit.base.components.TextComponent;
import org.eclipse.jubula.toolkit.base.components.TextInputComponent;
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
    /** the AUT-Agent */
    private AUTAgent m_agent;
    /** the AUT */
    private AUT m_aut;
    /** the object mapping */
    private ObjectMapping m_om;

    /** prepare */
    @Before
    public void setUp() throws Exception {
        URL input = this.getClass().getClassLoader()
                .getResource("objectMapping_SimpleAdderRCP.properties"); //$NON-NLS-1$
        m_om = MakeR.createObjectMapping(input.openStream());

        m_agent = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT);
        m_agent.connect();
        
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
        
        m_aut = m_agent.getAUT(id);
        m_aut.setToolkitInformation(new SwtToolkitInfo());
        m_aut.connect();
    }

    /** the actual test method */
    @Test(expected = CheckFailedException.class)
    public void testAUT() throws Exception {
        ComponentIdentifier val1 = m_om.get("value1"); //$NON-NLS-1$
        ComponentIdentifier val2 = m_om.get("value2"); //$NON-NLS-1$
        ComponentIdentifier button = m_om.get("equalsButton"); //$NON-NLS-1$
        ComponentIdentifier sum = m_om.get("sum"); //$NON-NLS-1$

        TextInputComponent value1 = SwtComponentFactory.createText(val1);
        TextInputComponent value2 = SwtComponentFactory.createText(val2);
        GraphicsComponent equalsButton = SwtComponentFactory
            .createButton(button);
        
        TextComponent result = SwtComponentFactory.createTextComponent(sum);
        
        final int firstValue = 17;
        for (int i = 1; i < 5; i++) {
            m_aut.execute(value1.replaceText(String.valueOf(firstValue)));
            m_aut.execute(value2.replaceText(String.valueOf(i)));
            m_aut.execute(equalsButton.click(1, 1));
            m_aut.execute(result.checkText(String.valueOf(firstValue + i),
                Operator.equals));
        }
    }

    /** cleanup */
    @After
    public void tearDown() throws Exception {
        m_aut.disconnect();
        m_agent.stopAUT(m_aut.getIdentifier());
        m_agent.disconnect();
    }
}