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

import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.constants.StringConstants;


/**
 * The message send from server to the client, transmitting the state of the start process of the AUTServer. <br>
 * OK: AUTServer is starting
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
public class StartAUTServerStateMessage extends Message {
    /** server start succesfull */
    public static final int OK = 0;
    
    /** reasons why an AUTServer could not started */
    /** general error */
    public static final int ERROR = 1;
    
    /** execution of the ICommand StartAUTServer failed */
    public static final int EXECUTION = ERROR + 1; //2

    /** an io error occurs */
    public static final int IO = EXECUTION + 1; //3

    /** the data in the message are not correct */
    public static final int DATA = IO + 1; //4

    /** security violation during starting the AUTServer */
    public static final int SECURITY = DATA + 1; //5

    /** no main class found in the jar */
    public static final int AUT_MAIN_NOT_FOUND_IN_JAR = SECURITY + 1; //6
    
    /** main class is not distinct */
    public static final int AUT_MAIN_NOT_DISTINCT_IN_JAR = 
        AUT_MAIN_NOT_FOUND_IN_JAR + 1; //7
    
    /** no main class transmitted and classpath does not consists of a jar */
    public static final int NO_JAR_AS_CLASSPATH =  //8
        AUT_MAIN_NOT_DISTINCT_IN_JAR + 1;

    /** scanning the jar for a main class failed (IOException) */
    public static final int SCANNING_JAR_FAILED =  //9
        NO_JAR_AS_CLASSPATH + 1;
    
    /**
     * Communication error (AUTServer exit codes UKNOWN_GuiDancerCLIENT and
     * COMUNICATION_ERROR
     */
    public static final int COMMUNICATION =  //10
        SCANNING_JAR_FAILED + 1;

    /**
     * invalid or unsufficient arguments (AUTServer exit codes INVALID_ARG,
     * INVALID_NUMBER_OF_ARGS)
     */
    public static final int INVALID_ARGUMENTS = //11
        COMMUNICATION + 1;
    
    /**
     * server class could not be instantiated for the given toolkit 
     * (AUTServer exit code EXIT_AUT_SERVER_INSTANTIATION)
     */
    public static final int NO_SERVER_CLASS = //12
        INVALID_ARGUMENTS + 1;

    /** the dotNet Framework is not properly installed */
    public static final int DOTNET_INSTALL_INVALID = NO_SERVER_CLASS + 1; //13

    /**
     * the JDK version used by the AUT is probably older than 1.5, 
     * -javaagent is unknown 
     */
    public static final int JDK_INVALID = DOTNET_INSTALL_INVALID + 1; //14
    
    /** the highest constant in this class, change this if you add constants. */
    private static final int MAX_CONSTANT = 15;
    
    /** the constant used, when no reason is set */
    private static final int UNKNOWN = MAX_CONSTANT;
    
    /** static version */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /** the reason */
    /** @attribute System.Xml.Serialization.XmlElement("m__reason") */
    public int m_reason;

    /** a short textual description */
    /** @attribute System.Xml.Serialization.XmlElement("m__description") */
    public String m_description; 

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

/* DOTNETDECLARE:END */

    /**
     * empty default constructor
     */
    public StartAUTServerStateMessage() {
        super();
        m_reason = UNKNOWN;
        m_description = StringConstants.EMPTY;
    }

    /**
     * public constructor
     * @param reason the reason why the AUTServer could not started, 
     * see constants
     */
    public StartAUTServerStateMessage(int reason) {
        this();
        m_reason = reason;
    }

    
    /**
     * public constructor
     * @param reason the reason why the AUTServer could not started, 
     * see constants
     * @param description a short textual description
     */
    public StartAUTServerStateMessage(int reason, String description) {
        this(reason);
        m_description = description;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.START_AUT_SERVER_STATE_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

    /**
     * @return Returns the reason.
     */
    public int getReason() {
        return m_reason;
    }
    
    /**
     * @param reason The reason to set.
     */
    public void setReason(int reason) {
        m_reason = reason;
    }
    
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return m_description;
    }
    
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        m_description = description;
    }
}
