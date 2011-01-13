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
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class EndTestExecutionResponseMessage extends Message {
    /**
     * Default constructor.
     */
    public EndTestExecutionResponseMessage() {
        // empty
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.END_TESTEXECUTION_RESPONSE_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return 1.0;
    }
}
