/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.api.gen.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;


/**
 * Loads the configuration for the API generation from the properties file. It
 * is expected that the properties reside in
 * <code>resources/apigen.properties</code>.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 12986 $
 */
public class NameMappingLoader {
    /**
     * <code>RESOURCES_APIGEN_PROPERTIES</code>
     */
    private static final String RESOURCES_NAMEMAPPINGS_PROPERTIES =
        "resources/nameMappings.properties"; //$NON-NLS-1$
    /**
     * <code>instance</code> the singleton instance
     */
    private static NameMappingLoader instance = null;
    /**
     * the mapping properties
     */
    private Properties m_mappingProperties;

    /**
     * The constructor.
     */
    private NameMappingLoader() {
        try {
            URL resourceURL = NameMappingLoader.class.getClassLoader()
                .getResource(RESOURCES_NAMEMAPPINGS_PROPERTIES);
            
            m_mappingProperties = new Properties();
            m_mappingProperties.load(resourceURL.openStream());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * @return the singleton instance
     */
    public static NameMappingLoader getInstance() {
        if (instance == null) {
            instance = new NameMappingLoader();
        }
        return instance;
    }
    
    /**
     * Checks in the name mappings property file whether there is a mapping for a
     * given string and returns it and if not, returns the original string
     * @param name original name
     * @return the name which should be used in api
     */
    public String getDesiredName(String name) {
        String mapEntry = m_mappingProperties.getProperty(name);
        if (mapEntry != null) {
            return mapEntry;
        }
        String desiredName = name.replace("abstract", "base");
        return desiredName;
    }
}
