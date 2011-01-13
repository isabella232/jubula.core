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
package org.eclipse.jubula.rc.rcp.accessor.plugin;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.rcp.accessor.Startup;
import org.eclipse.jubula.rc.rcp.gef.inspector.GefInspectorListenerAppender;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.constants.RcpAccessorConstants;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class RcpAccessorPlugin extends AbstractUIPlugin implements IStartup {

    /** the logger */
    private static Log log = LogFactory.getLog(RcpAccessorPlugin.class);

    
    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        final Properties envVars = 
            EnvironmentUtils.getProcessEnvironment();
        // unload plugin, if expected property is not set
        if (getValue(AutConfigConstants.AUT_AGENT_HOST, envVars) == null) {
            stop(context);
            return;
        }
        Thread t = new Thread() {
            public void run() {
                try {
                    while (getBundle().getState() != Bundle.ACTIVE) {
                        sleep(50);
                    }
                    
                    Display display = null;
                    while (display == null) {
                        try {
                            display = getWorkbench().getDisplay();
                        } catch (IllegalStateException ise) {
                            // Workbench not available. Wait and try again.
                            try {
                                sleep(100);
                            } catch (InterruptedException ie) {
                                // Do nothing
                            }
                        }
                    }
                    
                    ((SwtAUTServer)AUTServer.getInstance(CommandConstants
                            .AUT_SWT_SERVER)).setDisplay(display);
                    
                    AUTServer.getInstance().setAutStarterPort(getValue(
                            RcpAccessorConstants.SERVER_PORT, envVars));
                    AUTServer.getInstance().setAutAgentHost(getValue(
                            AutConfigConstants.AUT_AGENT_HOST, envVars));
                    AUTServer.getInstance().setAutAgentPort(getValue(
                            AutConfigConstants.AUT_AGENT_PORT, envVars));
                    AUTServer.getInstance().setAutName(getValue(
                            AutConfigConstants.AUT_NAME, envVars));
                    // init autServer
                    AUTServer.getInstance().start(true);

                    // add listener to AUT
                    AUTServer.getInstance().addToolKitEventListenerToAUT();

                    // add Inspector listener appender for GEF, if available
                    if (Platform.getBundle(Startup.GEF_BUNDLE_ID) != null) {
                        AUTServer.getInstance().addInspectorListenerAppender(
                                new GefInspectorListenerAppender());
                    }

                } catch (InterruptedException e) {
                    log.error(e);
                }
            }

        };
        t.start();
    }
    
    /**
     * Returns the value for a given property. First, <code>envVars</code> 
     * is checked for the given property. If this
     * property cannot be found there, the 
     * Java System Properties will be checked. If the property is not 
     * found there, <code>null</code> will be returned.
     * 
     * @param envVars The first source to check for the given property.
     * @param propName The name of the property for which to find the value.
     * @return The value for the given property name, or <code>null</code> if
     *         given property name cannot be found.
     */
    private String getValue(String propName, Properties envVars) {
        String value = 
            envVars.getProperty(propName);
        if (value == null) {
            value = System.getProperty(propName);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void earlyStartup() {
        // do nothing, but don't delete it
    }
}