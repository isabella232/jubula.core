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

import org.eclipse.jubula.client.launch.AUTConfiguration;

/** @author BREDEX GmbH */
public interface AUTAgent extends Remote {
    /**
     * start an AUT
     * 
     * @param configuration
     *            an AUT configuration to launch the AUT
     * @return a reference to the running AUT
     */
    AUT startAUT(AUTConfiguration configuration) throws Exception;

    /**
     * stop an AUT
     * 
     * @param aut
     *            a reference to the AUT to stop
     */
    void stopAUT(AUT aut) throws Exception;
}
