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
package org.eclipse.jubula.client.core.model;

import java.util.Map;
import java.util.Set;



/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface IAUTConfigPO extends IPersistentObject, Comparable {
    
    /**
     * max length of classpath allowed
     */
    public static final int MAX_CLASSPATH_LENGTH = 4000;

    /** Activation method for window at TS start */
    public static enum ActivationMethod {
        /** No activation */
        NONE, 
        /** Click on titlebar */
        TITLEBAR, 
        /** Click in corner */
        NE, 
        /** Click in corner */
        NW, 
        /** Click in corner */
        SE, 
        /** Click in corner */
        SW, 
        /** click in the center of the window */
        CENTER
    }
    
    /** Browser for Html-Test */
    public static enum Browser {
        /** InternetExplorer */
        InternetExplorer, 
        /** Firefox */
        Firefox, 
        /** Safari */
        Safari
    }
    
    /**
     * Gets a value of this AutConfig.
     * Keys are defined in {@link IAutConfigKeys}.
     * @param key an AutConfigKey enum.
     * @param defaultValue a defaut value to return if the given key is unknown.
     * @return the value of the given key.
     */
    public String getValue(String key, String defaultValue);
    
    /**
     * Sets the given value with the given key.
     * The Keys are defined in {@link IAutConfigKeys}.
     * @param key an AutConfigKey enum.
     * @param value the value to set.
     */
    public void setValue(String key, String value);

    /**
     * @return Returns the GUID.
     */
    public abstract String getGuid();

    /**
     * Convenience method for getValue(IAutConfigPO.SERVER_KEY, "")
     * @return The server
     */
    public abstract String getServer();

    /**
     * @return a Set of all keys of the AutConfig.
     */
    public Set<String> getAutConfigKeys();
    
    /**
     * @return the Map<String, String> of the aut configuration
     */
    public Map<String, String> getConfigMap();
   
    /**
     * @param config the Map<String, String> of the aut configuration
     */
    public void setConfigMap(Map<String, String> config);
}