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
package org.eclipse.jubula.rc.common.agent;

import java.lang.instrument.Instrumentation;

import org.eclipse.jubula.rc.common.AutServerLauncher;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.tools.constants.AutConfigConstants;


/**
 * @author BREDEX GmbH
 * @created Mar 26, 2008
 */
public class GDAgent {

    /**
     * hidden constructor
     */
    private GDAgent() {
        super();
    }
    
    /**
     * Creates the arguments array for AutServer, <br>
     * saves the current ClassLoader, <br>
     * calls the main-method of the AUTServerLauncher, <br>
     * reactivates the saved ClassLoader.
     * @param agentArguments String agentArguments
     * @param instrumentation a java.lang.instrument.Instrumentation instance
     */
    public static void premain(String agentArguments,
            Instrumentation instrumentation) {  
        
        // create AutServer arguments
        String[] args = 
            new String[Constants.MIN_ARGS_REQUIRED];
        
        args[Constants.ARG_SERVERPORT] = System.getenv("AUT_SERVER_PORT"); //$NON-NLS-1$
        // placeholder
        args[Constants.ARG_AUTMAIN] = "AutMain"; //$NON-NLS-1$
        args[Constants.ARG_AUTSERVER_CLASSPATH] = 
            System.getenv("AUT_SERVER_CLASSPATH"); //$NON-NLS-1$
        args[Constants.ARG_AUTSERVER_NAME] = System.getenv("AUT_SERVER_NAME"); //$NON-NLS-1$

        // Aut Agent arguments
        args[Constants.ARG_REG_HOST] = 
            System.getenv(AutConfigConstants.AUT_AGENT_HOST);
        args[Constants.ARG_REG_PORT] = 
            System.getenv(AutConfigConstants.AUT_AGENT_PORT);
        args[Constants.ARG_AUT_NAME] = 
            System.getenv(AutConfigConstants.AUT_NAME);
        // true for agent is activated
        args[Constants.ARG_AGENT_SET] = "true"; //$NON-NLS-1$

        final ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        
        try {
            AutServerLauncher.main(args);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextClassLoader);
        }
    }
}
