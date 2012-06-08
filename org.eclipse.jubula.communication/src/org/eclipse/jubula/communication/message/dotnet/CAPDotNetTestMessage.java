/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.message.dotnet;

import org.eclipse.jubula.communication.message.CAPTestMessage;
import org.eclipse.jubula.communication.message.MessageCap;
import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created Jun 08, 2012
 */
public class CAPDotNetTestMessage extends CAPTestMessage {
    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public CAPDotNetTestMessage() {
        // Nothing to be done
    }
    
    /**
     * Creates a new instance with the passed CAP message data. The data are
     * sent to the AUT server to execute a test step.
     * @param messageCap The message data
     */
    public CAPDotNetTestMessage(MessageCap messageCap) {
        super(messageCap);
    }
   
    /**
     * @return the command class
     */
    public String getCommandClass() {
        return CommandConstants.DOTNET_CAP_TEST_COMMAND;
    }
}