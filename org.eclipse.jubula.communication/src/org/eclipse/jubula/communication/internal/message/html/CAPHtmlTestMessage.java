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
package org.eclipse.jubula.communication.internal.message.html;

import org.eclipse.jubula.communication.internal.message.CAPTestMessage;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created Nov 24, 2009
 */
public class CAPHtmlTestMessage extends CAPTestMessage {
    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public CAPHtmlTestMessage() {
        // Nothing to be done
    }
    
    /**
     * Creates a new instance with the passed CAP message data. The data are
     * sent to the AUT server to execute a test step.
     * @param messageCap The message data
     */
    public CAPHtmlTestMessage(MessageCap messageCap) {
        super(messageCap);
    }
   
    /**
     * @return the command class
     */
    public String getCommandClass() {
        return CommandConstants.HTML_CAP_TEST_COMMAND;
    }
}