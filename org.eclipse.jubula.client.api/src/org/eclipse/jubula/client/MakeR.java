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
package org.eclipse.jubula.client;

import java.io.InputStream;

import org.eclipse.jubula.client.internal.impl.AUTAgentImpl;
import org.eclipse.jubula.client.internal.impl.ObjectMappingImpl;

/** @author BREDEX GmbH */
public final class MakeR {
    /** Constructor */
    private MakeR() {
        // hide
    }

    /**
     * @param hostname
     *            the hosts name the remote AUT-Agent is running on
     * @param port
     *            the port number the remote AUT-Agent is running on
     * @return a new AUTAgent instance
     */
    public static AUTAgent createAUTAgent(final String hostname, int port) {
        return new AUTAgentImpl(hostname, port);
    }
    
    /**
     * @return a new Object Mapping instance
     * @param input the input stream containing the encoded object mapping
     */
    public static ObjectMapping createObjectMapping(InputStream input) {
        return new ObjectMappingImpl(input);
    }
}