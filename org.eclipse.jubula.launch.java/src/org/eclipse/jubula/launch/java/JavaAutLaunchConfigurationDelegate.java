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
import org.eclipse.jubula.autagent.commands.AbstractStartJavaAutServer;
import org.eclipse.jubula.autagent.commands.IStartAut;
import org.eclipse.jubula.autagent.commands.StartJavaFXAutServerCommand;
import org.eclipse.jubula.autagent.commands.StartSwingAutServerCommand;
import org.eclipse.jubula.launch.Activator;
import org.eclipse.jubula.launch.AutLaunchUtils;
import org.eclipse.jubula.launch.i18n.Messages;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * Launch delegate for starting JavaFX / Swing AUTs.
 * 
 * @author BREDEX GmbH
 * @created 13.07.2011
 */
public class JavaAutLaunchConfigurationDelegate extends JavaLaunchDelegate {

    /** AUT toolkit */
    private String m_toolkit;
    
    /** aut server start command */
    private AbstractStartJavaAutServer m_startAutServerCommand;
    
    /** aut server */
    private String m_autServer;
    
    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException {
        
        String autMainType = verifyMainTypeName(configuration);
        String autArgs = getProgramArguments(configuration);
        String autId = AutLaunchUtils.getAutId(configuration);
        m_toolkit = AutLaunchUtils.getToolkit(configuration);
        
        ILaunchConfigurationWorkingCopy workingCopy = 
            configuration.getWorkingCopy();
        workingCopy.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
                CommandConstants.AUT_SERVER_LAUNCHER);
        

        InetSocketAddress agentAddr = 
            AutLaunchUtils.verifyConnectedAgentAddress();

        try {
            m_startAutServerCommand = determineStartCommand();
            m_autServer = determineAutServer();
        } catch (ToolkitPluginException e) {
            throw new CoreException(new Status(
                    IStatus.ERROR, Activator.PLUGIN_ID, 
                    Messages.GetToolkitFromLaunchConfigError));
        }
        
        String [] args = {
            Integer.toString(agentAddr.getPort()), autMainType, 
            StringUtils.join(
                    m_startAutServerCommand.getLaunchClasspath(), 
                    IStartAut.PATH_SEPARATOR), 
            m_autServer, agentAddr.getHostName(),
            Integer.toString(agentAddr.getPort()), autId, 
            CommandConstants.RC_COMMON_AGENT_INACTIVE, autArgs
        };
        
        workingCopy.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, 
                StringUtils.join(args, " ")); //$NON-NLS-1$
        
        super.launch(workingCopy, ILaunchManager.DEBUG_MODE, launch, monitor);
    }

    /**
     * @return the aut server start command
     * @throws ToolkitPluginException 
     */
    private AbstractStartJavaAutServer determineStartCommand()
            throws ToolkitPluginException {
        AbstractStartJavaAutServer startAutServerCommand = null;
        if (ToolkitSupportBP.getToolkitDescriptor(
                CommandConstants.SWING_TOOLKIT).getName()
                    .equals(m_toolkit)) {
            startAutServerCommand = new StartSwingAutServerCommand();
        } else if (ToolkitSupportBP.getToolkitDescriptor(
                CommandConstants.JAVAFX_TOOLKIT).getName()
                    .equals(m_toolkit)) {
            startAutServerCommand = new StartJavaFXAutServerCommand();
        }
        return startAutServerCommand;
    }
    
    /**
     * @return the aut server
     * @throws ToolkitPluginException 
     */
    private String determineAutServer() throws ToolkitPluginException {
        String autServer = null;
        if (ToolkitSupportBP.getToolkitDescriptor(
                CommandConstants.SWING_TOOLKIT).getName()
                    .equals(m_toolkit)) {
            autServer = CommandConstants.AUT_SWING_SERVER;
        } else if (ToolkitSupportBP.getToolkitDescriptor(
                CommandConstants.JAVAFX_TOOLKIT).getName()
                    .equals(m_toolkit)) {
            autServer = CommandConstants.AUT_JAVAFX_SERVER;
        }
        return autServer;
    }
    


    @Override
    public String[] getClasspath(ILaunchConfiguration configuration)
            throws CoreException {
        String[] rcClasspath = m_startAutServerCommand.getLaunchClasspath();
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

}
