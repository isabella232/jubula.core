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

import org.eclipse.jubula.communication.message.ActivateApplicationMessage;
import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created 10.02.2006
 */
public class ActivateSwtApplicationMessage extends ActivateApplicationMessage {
    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.ACTIVATE_SWT_APPLICATION_COMMAND;
    }
}