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

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * The class <code>StartAutServerCommand</code> and the associated
 * <code>StartAutServerMessage</code> are used as examples for the intended
 * use of the communications layer in Jubula. Since changes are expected, this
 * documentation is inlined in the source code. Please reevaluate the Java doc
 * frequently for changes in this templates.
 *  
 * The message send from the client to the server to start the AUTServer. <br>
 * The response message is StartAUTServerStateMessage.
 *
 * @author BREDEX GmbH
 * @created 04.08.2004
 */

/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 *
 * @attribute System.Serializable()
 */
public class StartAUTServerMessage extends Message {
    /**
     * Static version
     */
    private static final double VERSION = 1.1;

    /* DOTNETDECLARE:BEGIN */
    
    /* see class comment for details on .NET attributes */
    /** where to find the JubulaClient, inetAdress conform */
    /** @attribute System.Xml.Serialization.XmlElement("m__guidancerClient") */
    public String m_client;
    
    /** port the GDClient listens to */
    /** @attribute System.Xml.Serialization.XmlElement("m__port") */
    public int m_port;
    
    /** Timeout for the AUTServer to wait for a confirmation for a sended event*/
    /** @attribute System.Xml.Serialization.XmlElement("m__eventConfirmTimeOut") */
    public long m_eventConfirmTimeOut;
    
    /**
     * Language to start AUT with
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__locale") */
    public Locale m_locale;

    /**  the actual autToolKit of the project as String */
    /** @attribute System.Xml.Serialization.XmlElement("m__autToolKit") */
    public String m_autToolKit;

    /** The Map with the aut configuration */
    /** @attribute System.Xml.Serialization.XmlElement("m__autConfiguration") */
    public Map m_autConfiguration = null;
    
    /** flag to indicate whether technical names should be generated */
    /** @attribute System.Xml.Serialization.XmlElement("m__generateNames") */
    private boolean m_generateNames;
    
    /* DOTNETDECLARE:END */
    
    
    
    /**
     * @deprecated
     * Default constructor for transportation layer. Don't use for normal programming.
     *
     */
    public StartAUTServerMessage() {
        super();
    }
    
    
    
    /**
     * Constructs a complete message. No null values are allowed as parameters.
     * 
     * @param host
     *            Own host address, i.e. the address the AUT server should
     *            connect to.
     * @param port
     *            Own port, i.e.the port the AUT server should connect to.
     * @param autConfig a Map<String, String> with the AutConfiguration
     * @param autToolKit
     *            the autToolKit of the actual project as string
     * @param generateNames set to true to enable name generation in server
     */
    public StartAUTServerMessage(String host, int port, Map autConfig, 
        String autToolKit, boolean generateNames) {
        
        super();
        Validate.notEmpty(host);
        Validate.isTrue(port > 0);
        
        setAutConfiguration(autConfig);
        setClient(host);
        setPort(port);
        setAutToolKit(autToolKit);
        setGenerateNames(generateNames);
    }
    
    /**
     * @param autToolKit the actual autToolKit of the project as String
     */
    private void setAutToolKit(String autToolKit) {
        m_autToolKit = autToolKit;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * Returns the name of the AUTStartMessage
     * @return a <code>String</code> value
     */
    public String getCommandClass() {
        return CommandConstants.START_AUT_SERVER_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * Returns the version of the AUTStartVersion.
     * @return a <code>double</code> value.
     */
    public double getVersion() {
        return VERSION;
    }

    /**
     * @return Returns the client.
     */
    public String getClient() {
        return m_client;
    }
    
    /**
     * @param client The client to set.
     */
    public void setClient(String client) {
        Validate.notEmpty(client);
        m_client = client;
    }
    
    /**
     * @return Returns the port.
     */
    public int getPort() {
        return m_port;
    }
    
    /**
     * @param port The port to set.
     */
    public void setPort(int port) {
        Validate.isTrue(port > 0);
        m_port = port;
    }
    
    /**
     * @return Locale
     */
    public Locale getLocale() {
        return m_locale;
    }

    /**
     * @param locale
     *      Locale
     */
    public void setLocale(Locale locale) {
        m_locale = locale;
    }
    
    

    /**
     * Gets the timeout for the AUTServer to wait for a confirmation for a sended event
     * @return Returns the eventConfirmTimeOut.
     */
    public long getEventConfirmTimeOut() {
        return m_eventConfirmTimeOut;
    }

    /**
     * Sets the timeout for the AUTServer to wait for a confirmation for a sended event
     * @param eventConfirmTimeOut The eventConfirmTimeOut to set.
     */
    public void setEventConfirmTimeOut(long eventConfirmTimeOut) {
        m_eventConfirmTimeOut = eventConfirmTimeOut;
    }

    /**
     * @return the actual autToolKit of the project as String
     */
    public String getAutToolKit() {
        return m_autToolKit;
    }



    /**
     * @return the autConfiguration
     */
    public Map getAutConfiguration() {
        return m_autConfiguration;
    }

    /**
     * @param autConfiguration the autConfiguration to set
     */
    public void setAutConfiguration(Map autConfiguration) {
        m_autConfiguration = autConfiguration;
    }

    /**
     * @param generateNames the generateNames to set
     */
    public void setGenerateNames(boolean generateNames) {
        m_generateNames = generateNames;
    }

    /**
     * @return the generateNames
     */
    public boolean isGenerateNames() {
        return m_generateNames;
    }
}