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

import java.util.Locale;
import java.util.Map;

import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * The class <code>StartAutServerCommand</code> and the associated
 * <code>StartAutServerMessage</code> are used as examples for the intended use
 * of the communications layer in Jubula. Since changes are expected, this
 * documentation is inlined in the source code. Please reevaluate the Java doc
 * frequently for changes in this templates. The message send from the client to
 * the server to start the AUTServer. <br>
 * The response message is StartAUTServerStateMessage.
 * 
 * @author BREDEX GmbH
 * @created 04.08.2004
 */
public class StartAUTServerMessage extends Message {
    /** Language to start AUT with */
    private Locale m_locale;

    /** the actual autToolKit of the project as String */
    private String m_autToolKit;

    /** The Map with the AUT configuration */
    private Map m_autConfiguration = null;

    /** flag to indicate whether technical names should be generated */
    private boolean m_generateNames;

    /**
     * @deprecated Default constructor for transportation layer. Don't use for
     *             normal programming.
     */
    public StartAUTServerMessage() {
        super();
    }

    /**
     * Constructs a complete message. No null values are allowed as parameters.
     * 
     * @param autConfig
     *            a Map<String, String> with the AutConfiguration
     * @param autToolKit
     *            the autToolKit of the actual project as string
     * @param generateNames
     *            set to true to enable name generation in server
     */
    public StartAUTServerMessage(Map autConfig, String autToolKit,
        boolean generateNames) {
        super();

        setAutConfiguration(autConfig);
        setAutToolKit(autToolKit);
        setGenerateNames(generateNames);
    }

    /**
     * @param autToolKit
     *            the actual autToolKit of the project as String
     */
    private void setAutToolKit(String autToolKit) {
        m_autToolKit = autToolKit;
    }

    /**
     * Returns the name of the AUTStartMessage
     * 
     * @return a <code>String</code> value {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.START_AUT_SERVER_COMMAND;
    }

    /** @return Locale */
    public Locale getLocale() {
        return m_locale;
    }

    /** @param locale */
    public void setLocale(Locale locale) {
        m_locale = locale;
    }

    /** @return the actual autToolKit of the project as String */
    public String getAutToolKit() {
        return m_autToolKit;
    }

    /** @return the autConfiguration */
    public Map getAutConfiguration() {
        return m_autConfiguration;
    }

    /**
     * @param autConfiguration
     *            the autConfiguration to set
     */
    public void setAutConfiguration(Map autConfiguration) {
        m_autConfiguration = autConfiguration;
    }

    /**
     * @param generateNames
     *            the generateNames to set
     */
    public void setGenerateNames(boolean generateNames) {
        m_generateNames = generateNames;
    }

    /** @return the generateNames */
    public boolean isGenerateNames() {
        return m_generateNames;
    }
}