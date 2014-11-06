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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jubula.client.internal.impl.AUTAgentImpl;
import org.eclipse.jubula.client.internal.impl.ObjectMappingImpl;

/**
 * @author BREDEX GmbH
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
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
     * @return a new AUT-Agent instance - note: currently the underlying
     *         implementation only supports <b>ONE</b> connection at a time to a
     *         remote running AUT-Agent; multiple connections may only be
     *         established sequentially!
     */
    @NonNull
    public static AUTAgent createAUTAgent(@NonNull String hostname, int port) {
        return new AUTAgentImpl(hostname, port);
    }

    /**
     * @return a new Object Mapping instance
     * @param input
     *            an input stream providing the exported object mapping
     *            properties
     */
    @NonNull
    public static ObjectMapping createObjectMapping(
        @NonNull InputStream input) {
        return new ObjectMappingImpl(input);
    }
}