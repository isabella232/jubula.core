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
 * The response of a <code>ChangeAUTModeMessage</code>. <br>
 * 
 * Contains the new mode of the AUTServer.
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
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
public class AUTHighlightComponentResponseMessage extends Message {
    /**
     * Static version
     */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

    /**
     * set when component is highlighted by server
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__verified") */
    public boolean m_verified = false;
 
/* DOTNETDECLARE:END */
   
    /**
     * default constructor
     */
    public AUTHighlightComponentResponseMessage() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.HIGHLIGHT_COMPONENT_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

    /**
     * @return Returns the verified.
     */
    public boolean isVerified() {
        return m_verified;
    }

    /**
     * @param verified The verified to set.
     */
    public void setVerified(boolean verified) {
        m_verified = verified;
    }
}
