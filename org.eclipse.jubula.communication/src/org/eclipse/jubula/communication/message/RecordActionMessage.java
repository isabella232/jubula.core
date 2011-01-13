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


/**
 * The message to send recorded CAP <br>
 * 
 * @author BREDEX GmbH
 * @created 21.05.2008
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
public class RecordActionMessage extends Message {

    /** static version */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;
        
    /**
    * The CAP message data.
    */
   /** @attribute System.Xml.Serialization.XmlElement("m__message") */
   public CAPRecordedMessage m_message;    

/* DOTNETDECLARE:END */
    
   /**
    * Default constructor.
    * Do nothing.
    */
    public RecordActionMessage() {
        // do nothing
    }
    
    /**
     * Creates a new instance with the passed CAP message data.
     * @param message The message data
     */
    public RecordActionMessage(CAPRecordedMessage message) {
        m_message = message;
    }
    
    /**
     * the message 
     * @return the message 
     */
    public CAPRecordedMessage getCAPRecordedMessage() {
        return m_message;
    }
    /**
     * Sets the CAP message data (required by Betwixt).
     * @param message The message data
     */
    public void setCAPRecordedMessage(CAPRecordedMessage message) {
        m_message = message;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.RECORD_ACTION_COMMAND;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }
}