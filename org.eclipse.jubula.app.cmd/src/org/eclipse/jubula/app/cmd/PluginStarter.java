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
package org.eclipse.jubula.app.cmd;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PluginStarter extends Plugin {
    /** plugin id */
    public static final String PLUGIN_ID = "org.eclipse.jubula.app.cmd"; //$NON-NLS-1$

    /** the shared instance*/
    private static PluginStarter plugin;

    /**
     * The constructor
     */
    public PluginStarter() {
        plugin = this;
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        try {
            super.start(context);
        } catch (IllegalArgumentException iae) {
            AbstractCmdlineClient.printConsoleError(iae.getMessage());
            throw iae;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static PluginStarter getDefault() {
        return plugin;
    }

}
