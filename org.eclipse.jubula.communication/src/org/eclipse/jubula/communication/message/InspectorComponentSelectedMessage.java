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
 * This message is send from the AUTServer to the GUIDancer Client after 
 * the user has selected a component to inspect. <br>
 * 
 * There is no response message.
 * 
 * @author BREDEX GmbH
 * @created 10.06.2009
 */

/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 * @attribute System.Serializable()
 */
public class InspectorComponentSelectedMessage extends Message {
    /** static version */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

    // the data of this message BEGIN
    /**
     * the identifier of the component. 
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__componentIdentifier") */
    public IComponentIdentifier m_componentIdentifier;
    // the data of this message END

/* DOTNETDECLARE:END */

    /**
     * default constructor
     */
    public InspectorComponentSelectedMessage() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.INSPECTOR_COMPONENT_SELECTED_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

    /**
     * @return Returns the componentIdentifier.
     */
    public IComponentIdentifier getComponentIdentifier() {
        return m_componentIdentifier;
    }
    
    /**
     * @param componentIdentifier The componentIdentifier to set.
     */
    public void setComponentIdentifier(
            IComponentIdentifier componentIdentifier) {
        m_componentIdentifier = componentIdentifier;
    }
}
