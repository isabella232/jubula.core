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
 * This message is send from the AUTServer to the JubulaClient in OBJECT_MAPPING
 * mode after the user has signaled to select the high lighted component. <br>
 * There is no response message.
 * 
 * @author BREDEX GmbH
 * @created 25.08.2004
 */
public class ObjectMappedMessage extends Message {
    /** static version */
    public static final double VERSION = 1.0;

    // the data of this message BEGIN
    /** the identifier of the components */
    private IComponentIdentifier[] m_componentIdentifiers;

    // the data of this message END

    /** default constructor */
    public ObjectMappedMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.OBJECT_MAPPED_COMMAND;
    }

    /** {@inheritDoc} */
    public double getVersion() {
        return VERSION;
    }

    /** @return the componentIdentifiers */
    public IComponentIdentifier[] getComponentIdentifiers() {
        return m_componentIdentifiers;
    }

    /**
     * @param componentIdentifiers
     *            the componentIdentifiers to set.
     */
    public void setComponentIdentifiers(IComponentIdentifier[] 
        componentIdentifiers) {
        m_componentIdentifiers = componentIdentifiers;
    }
    
    /**
     * @param componentIdentifier
     *            the componentIdentifier to set.
     */
    public void setComponentIdentifier(
            IComponentIdentifier componentIdentifier) {
        m_componentIdentifiers = new IComponentIdentifier[] { 
            componentIdentifier };
    }
}