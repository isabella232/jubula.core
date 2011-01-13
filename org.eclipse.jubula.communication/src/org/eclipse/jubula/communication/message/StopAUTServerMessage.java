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
import org.eclipse.jubula.tools.registration.AutIdentifier;

/**
 * The message send from the client to the server to stop the AUTServer. <br>
 * The response message is StopAUTServerStateMessage.
 *
 * @author BREDEX GmbH
 * @created 18.12.2007
 */

/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 *
 * @attribute System.Serializable()
 * 
 */
public class StopAUTServerMessage extends Message {

    /**
     * Static version
     */
    private static final double VERSION = 1.1;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

    /** @attribute System.Xml.Serialization.XmlElement("m__autId") */
    public AutIdentifier m_autId;
    
/* DOTNETDECLARE:END */

    /**
     * Default constructor. Not for use in standard development.
     * Do nothing (required by Betwixt).
     * @deprecated
     */
    public StopAUTServerMessage() {
        // Nothing to initialize
    }
    
    /**
     * Constructor
     * 
     * @param autId The ID of the AUT to stop.
     */
    public StopAUTServerMessage(AutIdentifier autId) {
        m_autId = autId;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.STOP_AUT_SERVER_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

    /**
     * 
     * @return the ID of the AUT to stop.
     */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * 
     * @param autId The ID for the AUT to stop.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }

}