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

import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;

/** @author BREDEX GmbH */
public interface AUT extends Remote {
    /**
     * @return the identifier of this AUT
     */
    AUTIdentifier getIdentifier();

    /**
     * @param information
     *            the information about the AUTs toolkit
     */
    void setToolkitInformation(ToolkitInfo information);

    /**
     * @param cap
     *            the CAP to execute on the AUT
     * @throws ExecutionException
     *             in case of remote execution problems
     */
    void execute(CAP cap) throws ExecutionException;
}