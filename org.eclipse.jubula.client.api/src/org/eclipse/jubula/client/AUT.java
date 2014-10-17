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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.tools.AUTIdentifier;

/**
 * @author BREDEX GmbH
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface AUT extends Remote {
    /**
     * @return the AUTIdentifier of this AUT
     */
    @NonNull AUTIdentifier getIdentifier();

    /**
     * @param cap
     *            the CAP to execute on the AUT
     * @param <T>
     *            the payload type
     * @param payload
     *            the additional payload for the execution
     * @return the result of the execution
     * @throws ExecutionException
     *             in case of remote execution problems
     */
    @NonNull <T> Result<T> execute(
        @NonNull CAP cap, 
        @Nullable T payload) 
        throws ExecutionException;
}