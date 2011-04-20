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

import org.eclipse.jubula.communication.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * This message transfers all Jubula components of type
 * {@link org.eclipse.jubula.tools.xml.businessmodell.Component} and
 * subclasses. the components will be registered in the AUTSwtServer by executing
 * <code>SendAUTListOfSupportedComponentsCommand</code>.
 * @author BREDEX GmbH
 * @created 09.05.2006
 */
public class SendSwtAUTListOfSupportedComponentsMessage 
    extends SendAUTListOfSupportedComponentsMessage {

    /** empty constructor for serialization */
    public SendSwtAUTListOfSupportedComponentsMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SWT_SEND_COMPONENTS_COMMAND;
    }
}