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
package org.eclipse.jubula.communication.message.swt;

import org.eclipse.jubula.communication.message.CAPTestMessage;
import org.eclipse.jubula.communication.message.MessageCap;
import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * This class sends a component-action-param-triple to the AutSwtServer.
 * @author BREDEX GmbH
 * @created 09.05.2006
 */
public class CAPSwtTestMessage extends CAPTestMessage {
    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public CAPSwtTestMessage() {
        // Nothing to be done
    }
    
    /**
     * Creates a new instance with the passed CAP message data. The data are
     * sent to the AUT server to execute a test step.
     * @param messageCap The message data
     */
    public CAPSwtTestMessage(MessageCap messageCap) {
        super(messageCap);
    }
   
    /** @return the command class */
    public String getCommandClass() {
        return CommandConstants.SWT_CAP_TEST_COMMAND;
    }
}