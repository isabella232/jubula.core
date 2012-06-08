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

import org.eclipse.jubula.communication.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created Jun 08, 2012
 */
public class SenddotnetAUTListOfSupportedComponentsMessage extends
    SendAUTListOfSupportedComponentsMessage {

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.DOTNET_SEND_COMPONENTS_COMMAND;
    }
}
