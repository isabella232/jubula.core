/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.utils.MonitoringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created September 25, 2013
 * 
 */
public class StartJavaFXAutServerCommand extends AbstractStartJavaAutServer {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(StartJavaFXAutServerCommand.class);

    /** separates the environment variables */
    private static final String ENV_SEPARATOR = "\n"; //$NON-NLS-1$

    /** the classpath of the Aut Server */
    private String m_autServerClasspath = "AutServerClasspath"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    protected String[] createCmdArray(String baseCmd, Map parameters) {
        List cmds = new Vector();
        cmds.add(baseCmd);

        StringBuffer autServerClasspath = new StringBuffer();
        createServerClasspath(autServerClasspath);

        List autAgentArgs = new ArrayList();
        autAgentArgs.add(String.valueOf(parameters
                .get(AutConfigConstants.AUT_AGENT_HOST)));
        autAgentArgs.add(String.valueOf(parameters
                .get(AutConfigConstants.AUT_AGENT_PORT)));
        autAgentArgs.add(String.valueOf(parameters
                .get(AutConfigConstants.AUT_NAME)));

        if (!isRunningFromExecutable(parameters)) {
            createAutServerLauncherClasspath(cmds, autServerClasspath,
                    parameters);
            createAutServerClasspath(autServerClasspath, cmds, parameters);
            cmds.addAll(autAgentArgs);
            // information for aut server that agent is not used
            cmds.add(CommandConstants.RC_COMMON_AGENT_INACTIVE);
        } else {
            String serverBasePath = createServerBasePath();
            autServerClasspath.append(PATH_SEPARATOR).append(serverBasePath)
                    .append(PATH_SEPARATOR).append(getRcBundleClassPath());
            m_autServerClasspath = autServerClasspath.toString();

        }
        cmds.addAll(createAutArguments(parameters));
        return (String[]) cmds.toArray(new String[cmds.size()]);
    }

    /**
     * {@inheritDoc}
     */
    protected String getServerClassName() {
        return CommandConstants.AUT_JAVAFX_SERVER;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected String getRcBundleId() {
        return CommandConstants.RC_JAVAFX_BUNDLE_ID;
    }

    @Override
    protected String[] createEnvArray(Map parameters, boolean isAgentSet) {
        
        if (isRunningFromExecutable(parameters) 
                || MonitoringUtil.shouldAndCanRunWithMonitoring(parameters)) {
            setEnv(parameters, m_autServerClasspath);
            boolean agentActive = true;
            return super.createEnvArray(parameters, agentActive);
        }       
          
        return super.createEnvArray(parameters, isAgentSet);
        
    }


    /**
     * 
     * @return the class path corresponding to the receiver's RC bundle.
     */
    protected String getRcBundleClassPath() {
        return AbstractStartToolkitAut.getClasspathForBundleId(getRcBundleId());
    }

    @Override
    protected String getMainClassFromManifest(Map parameters) {
        String jarFile = createAbsoluteJarPath(parameters);
        String attr = getAttributeFromManifest("main-class", jarFile); //$NON-NLS-1$
        /*
         * Replacing "/" with "." because, in the Manifest file of an
         * Application that was build with JavaFX-Ant-Tasks, the path to the
         * JavaFX loader class has slashes but for the Classloader we need the
         * qualified class name with dots.
         */
        return attr.replace("/", ".");  //$NON-NLS-1$//$NON-NLS-2$
    }
}