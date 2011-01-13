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


/**
 * This class sends a component-action-param-triple to the server.
 *
 * @author BREDEX GmbH
 * @created 27.08.2004
 */

/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 * 
 * @attribute System.Serializable()
 * */
public abstract class CAPTestMessage extends Message {
    
    /** Static version */
    private static final double VERSION = 1.0;
    
/* DOTNETDECLARE:BEGIN */

    /** Transmitted version of this message. */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;
    
    /** The CAP message data. */
    /** @attribute System.Xml.Serialization.XmlElement("m__messageCap") */
    public MessageCap m_messageCap;
    
    /**
     * message is used for 2 purposes
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__requestAnswer") */
    public boolean m_requestAnswer = true;

/* DOTNETDECLARE:END */
    
    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public CAPTestMessage() {
        // Nothing to be done
    }
    
    /**
     * Creates a new instance with the passed CAP message data. The data are
     * sent to the AUT server to execute a test step.
     * @param messageCap The message data
     */
    public CAPTestMessage(MessageCap messageCap) {
        m_messageCap = messageCap;
    }
    
    /**
     * Gets the CAP message data.
     * @return The message data.
     */
    public MessageCap getMessageCap() {
        return m_messageCap;
    }
    
    /**
     * Sets the CAP message data (required by Betwixt).
     * @param messageCap The message data
     */
    public void setMessageCap(MessageCap messageCap) {
        m_messageCap = messageCap;
    }

    /**
     * @return the command class
     */
    public abstract String getCommandClass();

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }
    
    /**
     * @return Returns the requestAnswer.
     */
    public boolean isRequestAnswer() {
        return m_requestAnswer;
    }
    
    /**
     * @param requestAnswer The requestAnswer to set.
     */
    public void setRequestAnswer(boolean requestAnswer) {
        m_requestAnswer = requestAnswer;
    }
}