/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.internal.impl;

import java.util.Map;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.internal.AUTConnection;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;

/** @author BREDEX GmbH */
public class AUTImpl implements AUT {
    /** the AUT identifier */
    private AutIdentifier m_autID;
    /** the instance */
    private AUTConnection m_instance;
    /** the typeMapping */
    private Map<ComponentClass, String>  m_typeMapping;

    /**
     * Constructor
     * 
     * @param autID
     *            the identifier to use for connection
     */
    public AUTImpl(AutIdentifier autID) {
        m_autID = autID;
    }

    /** {@inheritDoc} */
    public void connect() throws Exception {
        final Map<ComponentClass, String> typeMapping = getTypeMapping();
        Assert.verify(typeMapping != null);
        m_instance = AUTConnection.getInstance();
        m_instance.connectToAut(m_autID, typeMapping);
    }

    /** {@inheritDoc} */
    public void disconnect() throws Exception {
        m_instance.close();
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_instance != null ? m_instance.isConnected() : false;
    }


    /** {@inheritDoc} */
    public AutIdentifier getIdentifier() {
        return m_autID;
    }

    /**
     * @return the typeMapping
     */
    public Map<ComponentClass, String>  getTypeMapping() {
        return m_typeMapping;
    }

    /**
     * @param typeMapping the typeMapping to set
     */
    public void setTypeMapping(Map<?, ?> typeMapping) {
        m_typeMapping = (Map<ComponentClass, String>) typeMapping;
    }
}