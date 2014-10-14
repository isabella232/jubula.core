package org.eclipse.jubula.client;

import java.net.URL;

import org.eclipse.jubula.tools.ComponentIdentifier;

/**
 * Utility class for loading object mapping associations
 * @author BREDEX GmbH
 * @created Oct 13, 2014
 */
public interface OM {
    
    /**
     * Initializes the object mapping associations
     * @param resourceURL the URL to the resource properties file
     */
    public void init(URL resourceURL);
    
    /**
     * Returns the component identifier for a component name from the cache
     * 
     * @param compName
     *            the component name
     * @return the component identifier or <code>null</code> if no identifier
     *         was found
     */
    public ComponentIdentifier get(String compName);
}
