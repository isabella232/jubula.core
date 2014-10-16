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

import org.eclipse.jubula.communication.CAP;

/**
 * @author BREDEX GmbH
 * @param <T>
 *            the payload type
 */
public interface Result<T> {
    /**
     * @return the payload
     */
    T getPayload();
    
    /**
     * @return the CAP
     */
    CAP getCAP();
}