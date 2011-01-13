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
 * message to send recorded Action name and information if CAP is recorded.
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
public class ShowObservInfoMessage extends Message {
    
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
    
    /** additional Message */
    /** @attribute System.Xml.Serialization.XmlElement("m__extraMsg") */
    public String m_extraMsg;    

/* DOTNETDECLARE:END */
    
    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public ShowObservInfoMessage() {
        // Nothing to be done
    }
    
    /**
     * constructor
     * @param extraMsg additional Message / Info
     */
    public ShowObservInfoMessage(String extraMsg) {
        m_extraMsg = extraMsg;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.SHOW_OBSERVE_INFO_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }
       
    /**
     * @return additional Message / Info
     */
    public String getExtraMessage() {
        return m_extraMsg;
    }
    
    /**
     * @param extraMsg additional Message / Info
     */
    public void setExtraMessage(String extraMsg) {
        m_extraMsg = extraMsg;
    }
}