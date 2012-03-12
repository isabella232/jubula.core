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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ConnectAutAgentBP;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionGUIController;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager.AutAgent;
import org.eclipse.jubula.tools.constants.DebugConstants;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

/**
 * @created 02.03.2012
 */
public class AUTAgentConnectHandler extends AbstractHandler {
    /** ID of command parameter for AUT Agent name to connect */
    public static final String AUT_AGENT_NAME_TO_CONNECT = "org.eclipse.jubula.client.ui.rcp.commands.ConnectToAUTAgentCommand.parameter.name"; //$NON-NLS-1$
    /** ID of command parameter for AUT Agent port to connect */
    public static final String AUT_AGENT_PORT_TO_CONNECT = "org.eclipse.jubula.client.ui.rcp.commands.ConnectToAUTAgentCommand.parameter.port"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * 
     * @throws ExecutionException
     */
    public Object executeImpl(ExecutionEvent event) throws ExecutionException {
        String currentState = event.getParameter(RadioState.PARAMETER_ID);
        try {
            String name = event.getParameter(AUT_AGENT_NAME_TO_CONNECT);
            String port = event.getParameter(AUT_AGENT_PORT_TO_CONNECT);
            Integer portNo = null;

            if (currentState != null) {
                if (HandlerUtil.matchesRadioState(event)) {
                    return null;
                }
                if (port != null && name != null) {
                    portNo = Integer.parseInt(event
                            .getParameter(AUT_AGENT_PORT_TO_CONNECT));
                }
            } else {
                AutAgent fallbackAgent = ConnectAutAgentBP.getInstance()
                        .getWorkingAutAgent();
                if (fallbackAgent != null) {
                    name = fallbackAgent.getName();
                    portNo = fallbackAgent.getPort();
                } else {
                    return null;
                }
            }

            AutAgent autAgent = new AutAgent(name, portNo);
            DataEventDispatcher.getInstance().fireAutAgentConnectionChanged(
                    ServerState.Connecting);
            TestExecutionGUIController.connectToAutAgent(autAgent);
            ConnectAutAgentBP.getInstance().setCurrentAutAgent(autAgent);
            HandlerUtil.updateRadioState(event.getCommand(), currentState);
        } catch (Exception e) {
            throw new ExecutionException(DebugConstants.ERROR, e);
        }
        return null;
    }
}