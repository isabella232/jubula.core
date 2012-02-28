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
package org.eclipse.jubula.client.analyze;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jubula.client.analyze.constants.AnalyzeConstants;
import org.osgi.framework.BundleContext;



/**
 * @author Volker Hotzan
 */
public class Activator extends Plugin {
    
    /** The plug-in ID */
    public static final String PLUGIN_ID = 
        AnalyzeConstants.PLUGIN_ID; 
    
    /** The shared instance */
    private static Activator plugin;
    
    /** Constructor */
    public Activator() {
    }
    
    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        ExtensionRegistry.getInstance().start();
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }

    /** @return plugin */
    public static Activator getDefault() {
        return plugin;
    }
}
