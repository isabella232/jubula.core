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

/**
 * Message sent to AUT Server to prepare for shutdown.
 * 
 * @author BREDEX GmbH
 * @created Mar 23, 2010
 */
public class PrepareForShutdownMessage extends Message {
    /** Static version */
    private static final double VERSION = 1.0;
    /**
     * indicates whether the AUT should be forced to quit or whether the AUT
     * will terminate by itself
     */
    private boolean m_force = true;
    /**
     * the additional delay to use when AUT terminates normally
     */
    private int m_additionalDelay;

    /**
     * necessary for deserialization
     */
    public PrepareForShutdownMessage() {
        // empty
    }
    
    /**
     * @param force
     *            indicates whether the AUT should be forced to quit or whether
     *            the AUT will terminate by itself
     * @param additionalDelay
     *            an additional delay to use if the AUT terminates non-forced 
     */
    public PrepareForShutdownMessage(boolean force, int additionalDelay) {
        setAdditionalDelay(additionalDelay);
        setForce(force);
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.PREPARE_FOR_SHUTDOWN_COMMAND;
    }

    /** {@inheritDoc} */
    public double getVersion() {
        return VERSION;
    }

    /**
     * @return the force
     */
    public boolean isForce() {
        return m_force;
    }

    /**
     * @param force the force to set
     */
    public void setForce(boolean force) {
        m_force = force;
    }

    /**
     * @return the additionalDelay
     */
    public int getAdditionalDelay() {
        return m_additionalDelay;
    }

    /**
     * @param additionalDelay the additionalDelay to set
     */
    public void setAdditionalDelay(int additionalDelay) {
        m_additionalDelay = additionalDelay;
    }
}
