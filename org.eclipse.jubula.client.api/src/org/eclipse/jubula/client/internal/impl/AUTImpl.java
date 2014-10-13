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

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.internal.AUTConnection;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/** @author BREDEX GmbH */
public class AUTImpl implements AUT {
    /** the AUT identifier */
    private AutIdentifier m_autID;
    /** the instance */
    private AUTConnection m_instance;

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
        m_instance = AUTConnection.getInstance();
        m_instance.connectToAut(m_autID);
    }

    /** {@inheritDoc} */
    public void disconnect() throws Exception {
        m_instance.close();
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_instance != null ? m_instance.isConnected() : false;
    }
}