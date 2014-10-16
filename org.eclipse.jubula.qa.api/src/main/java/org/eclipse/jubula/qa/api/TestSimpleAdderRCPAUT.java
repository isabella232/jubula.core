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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.toolkit.base.components.TextComponent;
import org.eclipse.jubula.toolkit.base.components.TextInputComponent;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.MenuBarComponent;
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
    /** the value1 */
    private TextInputComponent m_value1;
    /** the value2 */
    private TextInputComponent m_value2;
    /** the button */
    private ButtonComponent m_button;
    /** the result */
    private TextComponent m_result;

    /** prepare */
    @Before
    public void setUp() throws Exception {
        URL input = this.getClass().getClassLoader()
                .getResource("objectMapping_SimpleAdderRCP.properties"); //$NON-NLS-1$
        ObjectMapping om = MakeR.createObjectMapping(input.openStream());

        ComponentIdentifier val1Id = om.get("value1"); //$NON-NLS-1$
        ComponentIdentifier val2Id = om.get("value2"); //$NON-NLS-1$
        ComponentIdentifier buttonId = om.get("equalsButton"); //$NON-NLS-1$
        ComponentIdentifier sumId = om.get("sum"); //$NON-NLS-1$

        m_value1 = SwtComponentFactory.createText(val1Id);
        m_value2 = SwtComponentFactory.createText(val2Id);
        m_button = SwtComponentFactory.createButton(buttonId);
        m_result = SwtComponentFactory.createTextComponent(sumId);
        
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
        
        m_aut = m_agent.getAUT(id, new SwtToolkitInfo());
        m_aut.connect();
    }

    /** the actual test method */
    @Test(expected = CheckFailedException.class)
    public void testTestFirstSimpleAdderSteps() throws Exception {
        
        final int firstValue = 17;
        List<Result> results = new ArrayList<Result>();
        try {
            for (int i = 1; i < 5; i++) {
                exec(m_value1.replaceText(String.valueOf(firstValue)), results);
                exec(m_value2.replaceText(String.valueOf(i)), results);
                exec(m_button.click(1, 1), results);
                exec(m_result.checkText(String.valueOf(firstValue + i),
                    Operator.equals), results);
            }
        } finally {
            Assert.assertTrue(results.size() == 15);
        }
    }
    
    /** the actual test method */
    @Test
    public void testMenubar() throws Exception {
        MenuBarComponent menu = SwtComponentFactory.createMenu();
        exec(menu.checkEnablementOfEntryByIndexpath("1", true));
        
    }
    
    /**
     * @param cap
     *            the cap
     * @param r
     *            the result
     */
    private void exec(CAP cap, List<Result> r) {
        Result execute = m_aut.execute(cap);
        if (r != null) {
            r.add(execute);
        }
    }
    
    /**
     * @param cap
     *            the cap
     */
    private void exec(CAP cap) {
        exec(cap, null);
    }

    /** cleanup */
    @After
    public void tearDown() throws Exception {
        m_aut.disconnect();
        m_agent.stopAUT(m_aut.getIdentifier());
        m_agent.disconnect();
    }
}