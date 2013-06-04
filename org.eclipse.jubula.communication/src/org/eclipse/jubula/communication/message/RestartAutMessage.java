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
 */
public class RestartAutMessage extends Message {
    /** the ID of the Running AUT to restart */
    private AutIdentifier m_autId;
    
    /**
     * flag indicating whether the AUT should be closed or not
     */
    private boolean m_forceAUTTermination = true;

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
     * @param autId
     *            The ID of the Running AUT to restart.
     * @param forceAUTTermination
     *            flag indicating whether the AUT should be forced to quit
     */
    public RestartAutMessage(AutIdentifier autId, boolean forceAUTTermination) {
        m_autId = autId;
        m_forceAUTTermination = forceAUTTermination;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.RESTART_AUT_COMMAND;
    }

    /** @return the ID of the Running AUT to restart. */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * @param autId
     *            The ID of the Running AUT to restart.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }

    /**
     * @return the forceAUTTermination
     */
    public boolean isForceAUTTermination() {
        return m_forceAUTTermination;
    }

    /**
     * @param forceAUTTermination the forceAUTTermination to set
     */
    public void setForceAUTTermination(boolean forceAUTTermination) {
        m_forceAUTTermination = forceAUTTermination;
    }
}