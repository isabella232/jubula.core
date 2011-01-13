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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;



/**
 * The message to send all supported and currently instantiated components of
 * the AUT. <br>
 * 
 * @author BREDEX GmbH
 * @created 05.10.2004
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
public class AUTStartStateMessage extends Message {

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
     * the list of component identifiers of all supported and instantiated
     * components of the AUT.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__compIds") */
    public List m_compIds;

/* DOTNETDECLARE:END */
    
    // the data of this message END
    
    /**
     * empty constructor for serialisation
     */
    public AUTStartStateMessage() {
        m_compIds = new ArrayList();
    }
    
    /**
     * public constructor
     * 
     * @param compIds
     *            component identifier to set. if null, the list will be
     *            cleared.
     */
    public AUTStartStateMessage(List compIds) {
        m_compIds = compIds;
    }
    
    /**
     * the compIds as array 
     * @return an array of ComponentIdentifier, may be empty but never null
     */
    public IComponentIdentifier[] getCompIdsArray() {
        return (IComponentIdentifier[])m_compIds.toArray(
                new IComponentIdentifier[m_compIds.size()]);
    }
    /**
     * @return The component identifiers {@link ComponentIdentifier} as a list
     */
    public List getCompIds() {
        return Collections.unmodifiableList(m_compIds);
    }
    
    /**
     * add a componentIdentifier to the list. Used for serialising this message.
     * 
     * @param compId
     *            the component identifier to add, null values are ignored
     */
    public void addCompIds(IComponentIdentifier compId) {
        if (compId != null) {
            m_compIds.add(compId);
        }
    }
     
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.AUT_STARTED_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }
}
