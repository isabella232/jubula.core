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
package org.eclipse.jubula.autagent.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;


/**
 * @author BREDEX GmbH
 * @created 01.09.2009
 */
public class StartHtmlAutServerCommand extends AbstractStartToolkitAut {
    
    /** parameter map entry */
    private static final String WEB_ID_TAG = "WEB_ID_TAG"; //$NON-NLS-1$

    /**
     * <code>LIB_DIR</code>
     */
    private static final String LIB_DIR = FILE_SEPARATOR
            + "lib" + FILE_SEPARATOR; //$NON-NLS-1$

    /**
     * <code>MAIN_JAR</code>
     */
    private static final String MAIN_JAR = "org.eclipse.jubula.rc.html.jar"; //$NON-NLS-1$

    /** 
     * mapping from browser type (String) to corresponding 
     * Selenium browser command (String) 
     */
    private static final Map BROWSER_TO_CMD_MAP = new HashMap();
    
    static {
        BROWSER_TO_CMD_MAP.put("Firefox", "*firefox"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("InternetExplorer", "*iexplore"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("Safari", "*safari"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /** the logger */
    private static final Log LOG = LogFactory
            .getLog(StartHtmlAutServerCommand.class);

    /**
     * {@inheritDoc}
     */
    protected String createBaseCmd(Map parameters) throws IOException {
        String jre = System.getProperty("java.home") + FILE_SEPARATOR//$NON-NLS-1$ 
                + "bin" + FILE_SEPARATOR + "java"; //$NON-NLS-1$ //$NON-NLS-2$
        
        if (EnvironmentUtils.isWindowsOS()) {
            jre += ".exe"; //$NON-NLS-1$
        }
        
        if (jre != null && jre.length() > 0) {
            File exe = new File(jre);
            if (exe.isFile() && exe.exists()) {
                return exe.getCanonicalPath();
            }
            String errorMsg = jre + " does not point to a valid executable."; //$NON-NLS-1$
            LOG.error(errorMsg);
            throw new FileNotFoundException(errorMsg);
        }
        return jre;
    }

    /**
     * {@inheritDoc}
     */
    protected String[] createCmdArray(String baseCmd, Map parameters) {
        Vector<String> commands = new Vector<String>();
        commands.add(baseCmd);

        if (BXDEBUG != null) {
            commands.add("-Xdebug"); //$NON-NLS-1$
            commands.add("-agentlib:jdwp=transport=dt_socket,address="  //$NON-NLS-1$
                    + BXDEBUG + ",server=y,suspend=y"); //$NON-NLS-1$
        }
        File serverDir = new File("."); //$NON-NLS-1$
        commands.add("-jar"); //$NON-NLS-1$
        StringBuffer cmd = new StringBuffer(LIB_DIR + MAIN_JAR);
        cmd.insert(0, serverDir.getAbsolutePath());
        commands.add(cmd.toString());

        // connection parameters
        commands.add(String.valueOf(
                AutStarter.getInstance().getAutCommunicator().getLocalPort()));
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_ARGUMENTS)));
        commands.add(getBrowserString(
                parameters.get(AutConfigConstants.BROWSER_PATH), 
                parameters.get(AutConfigConstants.BROWSER)));

        // place holder
        commands.add("AUT"); //$NON-NLS-1$
        
        // registration parameters
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_AGENT_HOST)));
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_AGENT_PORT)));
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_ID)));
        
        final String idTag = (String)parameters.get(WEB_ID_TAG);
        if (!StringUtils.isEmpty(idTag)) {
            commands.add("/idtag:" + idTag); //$NON-NLS-1$
        }
        
        return commands.toArray(new String[commands.size()]);
    }
    
    /**
     * 
     * @param browserPath The path to the browser to start, or 
     *                    <code>null</code> if the default path for the given
     *                    browser type should be used.
     * @param browserType The browser type to start 
     *                    (ex. Firefox, Internet Explorer, Safari).
     * @return the command to use when starting Selenium in order to start the
     *         desired browser from the desired path.
     */
    private String getBrowserString(Object browserPath, Object browserType) {
        String browserString;
        
        Object browser = BROWSER_TO_CMD_MAP.get(browserType);
        if (browser == null) {
            throw new IllegalArgumentException(
                    "Unsupported browser type: " + browserType); //$NON-NLS-1$
        }
        browserString = String.valueOf(browser);

        if (browserPath != null) {
            browserString += " " + String.valueOf(browserPath); //$NON-NLS-1$
        }
              
        return browserString;
    }
}
