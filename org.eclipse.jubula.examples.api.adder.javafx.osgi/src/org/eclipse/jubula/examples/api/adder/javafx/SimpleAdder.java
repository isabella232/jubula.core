/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.api.adder.javafx;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.toolkit.base.components.GraphicsComponent;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.toolkit.javafx.JavafxComponents;
import org.eclipse.jubula.toolkit.javafx.config.JavaFXAUTConfiguration;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author BREDEX GmbH */
public class SimpleAdder {
    /** AUT-Agent host name to use */
    public static final String AGENT_HOST = "localhost"; //$NON-NLS-1$
    /** AUT-Agent port to use */
    public static final int AGENT_PORT = 60000;
    /** the value1 */
    private static TextInputComponent value1;
    /** the value2 */
    private static TextInputComponent value2;
    /** the button */
    private static GraphicsComponent button;
    /** the result */
    private static TextComponent result;
    /** the AUT-Agent */
    private AUTAgent m_agent;
    /** the AUT */
    private AUT m_aut;

    /** global prepare */
    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void loadObjectMapping() throws Exception {
        ComponentIdentifier<TextInputComponent> val1Id = OM.value1;
        ComponentIdentifier<TextInputComponent> val2Id = OM.value2;
        ComponentIdentifier<ButtonComponent> buttonId = OM.equalsButton;
        ComponentIdentifier<TextComponent> sumId = OM.resultField;

        value1 = JavafxComponents.createTextInputControl(val1Id);
        value2 = JavafxComponents.createTextInputControl(val2Id);
        button = JavafxComponents.createButton(buttonId);
        result = JavafxComponents.createLabel(sumId);
    }

    /** prepare */
    @Before
    public void setUp() throws Exception {
        /* Connecting to external Jubula AUT Agent which
        must be started manually BEFORE test execution! */
        m_agent = getAUTAgentInstance();
        m_agent.connect();

        final String autID = "SimpleAdder_javafx"; //$NON-NLS-1$
        AUTConfiguration config = new JavaFXAUTConfiguration(
                "api.aut.conf.simple.adder.javafx", //$NON-NLS-1$
                autID,
                "..\\jre\\bin\\java.exe", //$NON-NLS-1$
                "..\\examples\\", //$NON-NLS-1$ 
                new String[]{"-jar", "AUTs\\SimpleAdder\\javafx\\SimpleAdder.jar"} //$NON-NLS-1$ //$NON-NLS-2$
                );

        AUTIdentifier id = m_agent.startAUT(config);
        if (id != null) {
            m_aut = m_agent.getAUT(id, JavafxComponents.getToolkitInformation());
            m_aut.connect();
        } else {
            Assert.fail("AUT start has failed!"); //$NON-NLS-1$
        }
    }

    /** the actual test method */
    @Test(expected = CheckFailedException.class)
    public void testTestFirstSimpleAdderSteps() throws Exception {

        final int firstValue = 17;
        List<Result<String>> results = new ArrayList<Result<String>>();
        try {
            for (int i = 1; i < 5; i++) {
                results.add(m_aut.execute(
                        value1.replaceText(String.valueOf(firstValue)),
                        "Entering first value")); //$NON-NLS-1$
                results.add(m_aut.execute(
                        value2.replaceText(String.valueOf(i)),
                        "Entering second value")); //$NON-NLS-1$
                results.add(m_aut.execute(
                        button.click(1, InteractionMode.primary),
                        "Invoking calculation")); //$NON-NLS-1$
                results.add(m_aut.execute(result.checkText(
                        String.valueOf(firstValue + i), Operator.equals),
                        "Checking result")); //$NON-NLS-1$
            }
        } finally {
            Assert.assertTrue(results.size() == 15);
        }
    }

    /** cleanup */
    @After
    public void tearDown() throws Exception {
        m_aut.disconnect();
        m_agent.stopAUT(m_aut.getIdentifier());
        m_agent.disconnect();
    }

    /**
     * @return an AUT-Agent instance
     */
    protected AUTAgent getAUTAgentInstance() {
        return MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT);
    }
}