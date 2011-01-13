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
 * Message that an AUT registration event has occurred.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
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
public class AutRegisteredMessage extends Message {

    /** version */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

    /** @attribute System.Xml.Serialization.XmlElement("m__autId") */
    public AutIdentifier m_autId;

    /** @attribute System.Xml.Serialization.XmlElement("m__registered") */
    public boolean m_registered;

/* DOTNETDECLARE:END */

    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public AutRegisteredMessage() {
        // Nothing to initialize
    }
    
    /**
     * Constructor
     * 
     * @param autId The ID of the AUT that caused the event.
     * @param registered <code>true</code> if the event was caused by a 
     *                   registration, or <code>false</code> if it was caused by
     *                   a deregistration.
     */
    public AutRegisteredMessage(AutIdentifier autId, boolean registered) {
        m_autId = autId;
        m_registered = registered;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.AUT_REGISTERED_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return VERSION;
    }

    /**
     * 
     * @return the ID for the AUT that caused the event.
     */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * 
     * @param autId The ID for the AUT.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }

    /**
     * 
     * @return <code>true</code> if the event was caused by a 
     *         registration, or <code>false</code> if it was caused by
     *         a deregistration.
     */
    public boolean isRegistered() {
        return m_registered;
    }

    /**
     * 
     * @param registered The AUT's registration status.
     */
    public void setRegistered(boolean registered) {
        m_registered = registered;
    }
}
