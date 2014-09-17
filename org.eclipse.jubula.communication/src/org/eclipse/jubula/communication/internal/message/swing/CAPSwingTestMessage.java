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
package org.eclipse.jubula.communication.internal.message.swing;

import org.eclipse.jubula.communication.internal.message.CAPTestMessage;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * This class sends a component-action-param-triple to the AutSwingServer.
 * @author BREDEX GmbH
 * @created 09.05.2006
 */
public class CAPSwingTestMessage extends CAPTestMessage {
    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public CAPSwingTestMessage() {
        // Nothing to be done
    }
    
    /**
     * Creates a new instance with the passed CAP message data. The data are
     * sent to the AUT server to execute a test step.
     * @param messageCap The message data
     */
    public CAPSwingTestMessage(MessageCap messageCap) {
        super(messageCap);
    }
   
    /**
     * @return the command class
     */
    public String getCommandClass() {
        return CommandConstants.SWING_CAP_TEST_COMMAND;
    }
}