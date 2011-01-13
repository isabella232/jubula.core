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
public class RegisteredAutListMessage extends Message {

    /** version */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

    /** @attribute System.Xml.Serialization.XmlElement("m__autIds") */
    public AutIdentifier [] m_autIds;
    
/* DOTNETDECLARE:END */

    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public RegisteredAutListMessage() {
        // Nothing to initialize
    }

    /**
     * Constructor
     * 
     * @param autIds All AUTs that should be reported as "running".
     */
    public RegisteredAutListMessage(AutIdentifier [] autIds) {
        m_autIds = autIds;
    }
    
    /**
     * @return the autIds
     */
    public AutIdentifier[] getAutIds() {
        return m_autIds;
    }

    /**
     * @param autIds the autIds to set
     */
    public void setAutIds(AutIdentifier[] autIds) {
        m_autIds = autIds;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.REGISTERED_AUTS_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

}
