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

/** @author BREDEX GmbH */
public interface Remote {
    /**
     * connect to the remote side
     */
    void connect() throws Exception;

    /**
     * disconnect from the remote side
     */
    void disconnect();

    /**
     * @return whether a connection to the remote side is currently established
     */
    boolean isConnected();
}
