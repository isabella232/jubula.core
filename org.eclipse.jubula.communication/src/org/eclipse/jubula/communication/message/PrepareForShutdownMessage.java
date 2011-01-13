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
 * 
 */
public class PrepareForShutdownMessage extends Message {

    /**
     * Static version
     */
    private static final double VERSION = 1.0;

    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.PREPARE_FOR_SHUTDOWN_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return VERSION;
    }

}
