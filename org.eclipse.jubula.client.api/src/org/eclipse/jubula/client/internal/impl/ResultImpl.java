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

import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.communication.CAP;

/**
 * @author BREDEX GmbH
 * @param <T>
 *            the payload type
 */
public class ResultImpl<T> implements Result<T> {
    /** the payload */
    private T m_payload;
    /** the cap */
    private CAP m_cap;

    /**
     * Constructor
     * 
     * @param cap
     *            the CAP
     * @param payload
     *            the payload to use
     */
    public ResultImpl(CAP cap, T payload) {
        m_cap = cap;
        m_payload = payload;
    }

    /** {@inheritDoc} */
    public T getPayload() {
        return m_payload;
    }

    /** {@inheritDoc} */
    public CAP getCAP() {
        return m_cap;
    }
}