/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.message;

import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.registration.AutIdentifier;

/**
 * @author BREDEX GmbH
 * @created Mar 25, 2010
 * 
 */
public class RestartAutMessage extends Message {

    /**
     * Static version
     */
    private static final double VERSION = 1.0;

    /** the ID of the Running AUT to restart */
    private AutIdentifier m_autId;
    
    /**
     * Constructor for use in framework methods. Do not use for normal 
     * programming.
     * 
     * @deprecated
     */
    public RestartAutMessage() {
        // Nothing to initialize
    }
    
    /**
     * Constructor
     * 
     * @param autId The ID of the Running AUT to restart.
     */
    public RestartAutMessage(AutIdentifier autId) {
        m_autId = autId;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.RESTART_AUT_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return VERSION;
    }

    /**
     * @return the ID of the Running AUT to restart.
     */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * @param autId The ID of the Running AUT to restart.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }

    
}
