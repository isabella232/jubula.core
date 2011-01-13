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
package org.eclipse.jubula.communication.message.html;

import org.eclipse.jubula.communication.message.CAPTestMessage;
import org.eclipse.jubula.communication.message.MessageCap;
import org.eclipse.jubula.tools.constants.CommandConstants;


/**
 * @author BREDEX GmbH
 * @created Nov 24, 2009
 * 
 * 
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 *
 * @attribute System.Serializable()
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