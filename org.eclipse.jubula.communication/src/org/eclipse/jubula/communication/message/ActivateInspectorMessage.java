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
 * This message is send from JubulaClient to AUTServer in order to start the
 * Inspector. <br>
 * 
 * @author BREDEX GmbH
 * @created 10.06.2009
 */
public class ActivateInspectorMessage extends Message {
    /** Static version */
    private static final double VERSION = 1.0;

    /**
     * <code>true</code> if the inspector should be started, <code>false</code>
     * if the inspector should be stopped.
     */
    private boolean m_startInspector;

    /**
     * Default constructor. Do nothing (required by Betwixt).
     */
    public ActivateInspectorMessage() {
        // Nothing to be done
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.ACTIVATE_INSPECTOR_COMMAND;
    }

    /** {@inheritDoc} */
    public double getVersion() {
        return VERSION;
    }
}