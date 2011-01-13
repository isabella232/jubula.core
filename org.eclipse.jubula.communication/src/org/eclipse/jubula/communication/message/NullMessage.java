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
 * Null Message to check e.g. the Server connection.
 *
 * @author BREDEX GmbH
 * @created 02.02.2006
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
public class NullMessage extends Message {
    /**
     * Static version
     */
    private static final double VERSION = 1.0;
    
/* DOTNETDECLARE:BEGIN */

    /**
     * Transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

/* DOTNETDECLARE:END */

    /**
     * {@inheritDoc}
     * @return
     */
    public String getCommandClass() {
        return CommandConstants.NULL_COMMAND;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public double getVersion() {
        return m_version;
    }
}