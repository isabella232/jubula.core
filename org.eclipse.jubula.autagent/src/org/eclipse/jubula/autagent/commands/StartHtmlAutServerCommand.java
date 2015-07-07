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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;


/**
 * @author BREDEX GmbH
 * @created 01.09.2009
 */
public class StartHtmlAutServerCommand extends AbstractStartPseudoJavaAUT {
    /**
     * <code>DEFAULT_AUT_ID_ATTRIBUTE_NAME</code>
     */
    private static final String DEFAULT_AUT_ID_ATTRIBUTE_NAME = "id"; //$NON-NLS-1$

    /** 
     * mapping from browser type (String) to corresponding 
     * Selenium browser command (String) 
     */
    private static final Map<String, String> BROWSER_TO_CMD_MAP = 
        new HashMap<String, String>();
    
    static {
        BROWSER_TO_CMD_MAP.put("Firefox", "*firefox"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("InternetExplorer", "*iexplore"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("Safari", "*safari"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("Chrome", "*googlechrome"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * {@inheritDoc}
     */
    protected String[] createCmdArray(String baseCmd, Map parameters) {
        Vector<String> commands = new Vector<String>();
        commands.add(baseCmd);

        addDebugParams(commands, false);
        
        StringBuilder serverClasspath = new StringBuilder();
        String [] bundlesToAddToClasspath = getBundlesForClasspath();
            
        for (String bundleId : bundlesToAddToClasspath) {
            serverClasspath.append(
                    AbstractStartToolkitAut.getClasspathForBundleId(bundleId));
            serverClasspath.append(PATH_SEPARATOR);
        }
        
        commands.add("-classpath"); //$NON-NLS-1$
        commands.add(serverClasspath.toString());

        commands.add("com.bredexsw.jubula.rc.html.WebAUTServer"); //$NON-NLS-1$
        
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
        
        // additional parameters
        Object idAttribute = parameters.get(AutConfigConstants.WEB_ID_TAG);
        if (idAttribute != null) {
            commands.add(String.valueOf(idAttribute));
        } else {
            commands.add(DEFAULT_AUT_ID_ATTRIBUTE_NAME);
        }
        
        Object singleWindowMode = 
                parameters.get(AutConfigConstants.SINGLE_WINDOW_MODE);
        if (singleWindowMode != null) {
            commands.add(String.valueOf(singleWindowMode));
        } else {
            commands.add(String.valueOf(true));
        }



        return commands.toArray(new String[commands.size()]);
    }

    /**
     * @return the bundles to add to the classpath
     */
    protected String[] getBundlesForClasspath() {
        return new String[] { CommandConstants.RC_HTML_BUNDLE_ID,
                              CommandConstants.TOOLS_BUNDLE_ID,
                              CommandConstants.COMMUNICATION_BUNDLE_ID,
                              CommandConstants.RC_COMMON_BUNDLE_ID,
                              CommandConstants.TOOLKIT_API_BUNDLE_ID,
                              CommandConstants.RC_BREDEX_COMMON_BUNDLE_ID,
                              CommandConstants.SLF4J_JCL_BUNDLE_ID,
                              CommandConstants.ORG_SLF4J_JUL_BUNDLE_ID,
                              CommandConstants.SLF4J_API_BUNDLE_ID,
                              CommandConstants.LOGBACK_CLASSIC_BUNDLE_ID,
                              CommandConstants.LOGBACK_CORE_BUNDLE_ID,
                              CommandConstants.LOGBACK_SLF4J_BUNDLE_ID,
                              CommandConstants.COMMONS_LANG_BUNDLE_ID,
                              CommandConstants.APACHE_ORO_BUNDLE_ID,
                              CommandConstants.COMMONS_COLLECTIONS_BUNDLE_ID };
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


    @Override
    public String getRcBundleId() {
        return CommandConstants.RC_HTML_BUNDLE_ID;
    }
}
