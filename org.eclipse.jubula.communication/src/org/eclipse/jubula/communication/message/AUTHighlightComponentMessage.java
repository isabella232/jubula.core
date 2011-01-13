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
import org.eclipse.jubula.tools.objects.IComponentIdentifier;

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

public class AUTHighlightComponentMessage extends Message {
    
    /** Static version */
    private static final double VERSION = 1.0;
    
/* DOTNETDECLARE:BEGIN */

    /** transmitted version of this message. */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

    /** component to highlight */
    /** @attribute System.Xml.Serialization.XmlElement("m__component") */
    public IComponentIdentifier m_component;

/* DOTNETDECLARE:END */
    
    /**
     * default constructor
     */
    public AUTHighlightComponentMessage() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.AUT_HIGHLIGHT_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

    /**
     * @return Returns the component.
     */
    public IComponentIdentifier getComponent() {
        return m_component;
    }

    /**
     * @param component The component to set.
     */
    public void setComponent(IComponentIdentifier component) {
        m_component = component;
    }
}