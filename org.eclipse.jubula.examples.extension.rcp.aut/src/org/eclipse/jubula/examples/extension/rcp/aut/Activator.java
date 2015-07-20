package org.eclipse.jubula.examples.extension.rcp.aut;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the bundle
 */
public class Activator extends AbstractUIPlugin {

    /** the plugin */
    private static Activator plugin;
    
    /**
     * public activator
     */
    public Activator() {
        
    }
    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }
 
    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    
    /**
     * 
     * @return gets the activator
     */
    public static Activator getActivator() {
        return plugin;
    }
}
