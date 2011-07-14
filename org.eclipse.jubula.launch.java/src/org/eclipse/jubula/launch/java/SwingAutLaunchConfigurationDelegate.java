/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.launch.java;

import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jubula.autagent.commands.IStartAut;
import org.eclipse.jubula.autagent.commands.StartSwingAutServerCommand;
import org.eclipse.jubula.client.autagent.handlers.ConnectToEmbeddedAutAgentHandler;
import org.eclipse.jubula.client.core.communication.ConnectionException;
import org.eclipse.jubula.client.core.communication.ServerConnection;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.launch.AutLaunchConfigurationConstants;
import org.eclipse.jubula.launch.java.i18n.Messages;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launch delegate for starting Java / Swing AUTs.
 * 
 * @author BREDEX GmbH
 * @created 13.07.2011
 */
public class SwingAutLaunchConfigurationDelegate extends JavaLaunchDelegate {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(SwingAutLaunchConfigurationDelegate.class);
    
    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException {
        
        String autMainType = verifyMainTypeName(configuration);
        String autArgs = getProgramArguments(configuration);
        String autId = configuration.getAttribute(
                AutLaunchConfigurationConstants.AUT_ID_KEY, 
                configuration.getName());
        if (StringUtils.isEmpty(autId)) {
            autId = configuration.getName();
        }
        
        ILaunchConfigurationWorkingCopy workingCopy = 
            configuration.getWorkingCopy();
        workingCopy.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
                CommandConstants.AUT_SERVER_LAUNCHER);
        

        InetSocketAddress agentAddr = verifyConnectedAgentAddress();

        String [] args = {
            Integer.toString(agentAddr.getPort()), autMainType, 
            StringUtils.join(
                    new StartSwingAutServerCommand().getLaunchClasspath(), 
                    IStartAut.PATH_SEPARATOR), 
            CommandConstants.AUT_SWING_SERVER, agentAddr.getHostName(),
            Integer.toString(agentAddr.getPort()), autId, 
            CommandConstants.RC_COMMON_AGENT_INACTIVE, autArgs
        };
        
        workingCopy.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, 
                StringUtils.join(args, " ")); //$NON-NLS-1$
        
        super.launch(workingCopy, ILaunchManager.DEBUG_MODE, launch, monitor);
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration)
        throws CoreException {
        
        String[] rcClasspath = 
            new StartSwingAutServerCommand().getLaunchClasspath();
        String[] autClasspath = super.getClasspath(configuration);
        String[] combinedClasspath = 
            new String [rcClasspath.length + autClasspath.length];
        for (int i = 0; i < autClasspath.length; i++) {
            combinedClasspath[i] = autClasspath[i];
        }
        
        for (int i = 0; i < rcClasspath.length; i++) {
            combinedClasspath[i + autClasspath.length] = rcClasspath[i];
        }

        return combinedClasspath;
    }

    /**
     * Checks that there is an active connection to an AUT Agent. If not,
     * attempts to connect to the embedded AUT Agent (starting it first, 
     * if necessary). 
     * 
     * @return the address for the currently (at end of method execution) 
     *         connected AUT Agent. Guaranteed not to be <code>null</code>.
     * @throws CoreException if there is no connection to an AUT Agent and no
     *                       connection could be established.
     */
    private InetSocketAddress verifyConnectedAgentAddress() 
        throws CoreException {
        
        InetSocketAddress addr = getConnectedAgentAddress();
        
        if (addr != null) {
            return addr;
        }

        LOG.info("Not connected to an AUT Agent. Connecting to embedded AUT Agent."); //$NON-NLS-1$
        IHandlerService handlerServce = 
            (IHandlerService)PlatformUI.getWorkbench()
                .getService(IHandlerService.class);
        try {
            handlerServce.executeCommand(
                ConnectToEmbeddedAutAgentHandler
                    .CONNECT_TO_EMBEDDED_AGENT_CMD_ID, null);
        } catch (CommandException e) {
            LOG.error("Error occurred while trying to connect to embedded AUT Agent.", e); //$NON-NLS-1$
        }

        addr = getConnectedAgentAddress();
        
        if (addr == null) {
            throw new CoreException(new Status(
                    IStatus.ERROR, Activator.PLUGIN_ID, 
                    Messages.LaunchAutError_NoAgentConnection));
        }
        
        return addr;
    }
    
    /**
     * 
     * @return the address for the currently connected AUT Agent, or 
     *         <code>null</code> if there is no connection to an AUT Agent.
     */
    private InetSocketAddress getConnectedAgentAddress() {
        try {
            Communicator agentCommunicator = 
                ServerConnection.getInstance().getCommunicator();
            if (agentCommunicator.getConnection() != null) {
                return new InetSocketAddress(
                        agentCommunicator.getHostName(), 
                        agentCommunicator.getPort());
            }
        } catch (ConnectionException e) {
            // No connection exists. We can safely ignore this, fall through,
            // and return null to indicate that there is no active connection
            // to an AUT Agent.
        }
        
        return null;
    }
}
