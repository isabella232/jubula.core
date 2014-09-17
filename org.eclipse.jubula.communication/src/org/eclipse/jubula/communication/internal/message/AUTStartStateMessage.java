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
package org.eclipse.jubula.communication.internal.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

/**
 * The message to send all supported and currently instantiated components of
 * the AUT. <br>
 * 
 * @author BREDEX GmbH
 * @created 05.10.2004
 */
public class AUTStartStateMessage extends Message {
    // the data of this message BEGIN
    /**
     * the list of component identifiers of all supported and instantiated
     * components of the AUT.
     */
    private List m_compIds;

    // the data of this message END

    /** empty constructor for serialization */
    public AUTStartStateMessage() {
        m_compIds = new ArrayList();
    }

    /**
     * public constructor
     * 
     * @param compIds
     *            component identifier to set. If null, the list will be
     *            cleared.
     */
    public AUTStartStateMessage(List compIds) {
        m_compIds = compIds;
    }

    /**
     * the compIds as array
     * 
     * @return an array of ComponentIdentifier, may be empty but never null
     */
    public IComponentIdentifier[] getCompIdsArray() {
        return (IComponentIdentifier[]) m_compIds
                .toArray(new IComponentIdentifier[m_compIds.size()]);
    }

    /**
     * @return The component identifiers {@link ComponentIdentifier} as a list
     */
    public List getCompIds() {
        return Collections.unmodifiableList(m_compIds);
    }

    /**
     * add a componentIdentifier to the list. Used for serializing this message.
     * 
     * @param compId
     *            the component identifier to add, null values are ignored
     */
    public void addCompIds(IComponentIdentifier compId) {
        if (compId != null) {
            m_compIds.add(compId);
        }
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.AUT_STARTED_COMMAND;
    }
}