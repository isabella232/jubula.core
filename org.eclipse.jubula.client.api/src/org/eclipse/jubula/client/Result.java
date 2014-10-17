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
import org.eclipse.jubula.communication.CAP;

/**
 * Representing the result of a remotely executed CAP
 * 
 * @author BREDEX GmbH
 * @param <T>
 *            the payload type
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface Result<T> {
    /**
     * @return the payload
     */
    @Nullable T getPayload();
    
    /**
     * @return the CAP
     */
    @NonNull CAP getCAP();
    
    /**
     * @return wheter the CAP has been executed successfully or not
     */
    boolean isOK();
}