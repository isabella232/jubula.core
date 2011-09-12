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
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.ui.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 */
public class StopObjectMappingModeHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        if (TestExecution.getInstance().getConnectedAut() == null) {
            String message = Messages.OMStopMappingModeActionError1;
            Utils.createMessageDialog(new JBException(message,
                    MessageIDs.E_UNEXPECTED_EXCEPTION), null,
                    new String[] { message });
        } else {
            TestExecutionContributor.getInstance().getClientTest()
                    .resetToTesting();
            DataEventDispatcher.getInstance().fireOMStateChanged(
                    OMState.notRunning);
        }
        return null;
    }
}