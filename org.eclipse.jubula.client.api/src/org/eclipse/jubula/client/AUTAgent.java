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

import java.util.List;

import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;

/** @author BREDEX GmbH */
public interface AUTAgent extends Remote {
    /**
     * start an AUT
     * 
     * @param configuration
     *            an AUT configuration to launch the AUT
     * @return a reference to the running AUT
     */
    AUTIdentifier startAUT(AUTConfiguration configuration) throws Exception;

    /**
     * stop an AUT
     * 
     * @param aut
     *            a reference to the AUT to stop
     */
    void stopAUT(AUTIdentifier aut) throws Exception;
    
    /**
     * @return an unmodifiable list of currently known / registered AUT IDs
     * @throws Exception
     *             in case of a communication problem
     */
    List<AUTIdentifier> getAllRegisteredAUTIdentifier() throws Exception;
    
    /**
     * @param autID
     *            the autID to get an AUT for
     * @param information
     *            the information about the toolkit
     * @return an AUT
     */
    AUT getAUT(AUTIdentifier autID, ToolkitInfo information);
}