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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;


/**
 * Loads the configuration for the API generation from the properties file. It
 * is expected that the properties reside in
 * <code>resources/apigen.properties</code>.
 * 
 * @author BREDEX GmbH
 * @created 11.09.2014
 */
public class NameLoader {
    /**
     * <code>RESOURCES_APIGEN_PROPERTIES</code>
     */
    private static final String RESOURCES_NAMEMAPPINGS_PROPERTIES =
        "resources/nameMappings.properties"; //$NON-NLS-1$
    
    /** package base path */
    private static final String PACKAGE_BASE_PATH =
        "org.eclipse.jubula.toolkit."; //$NON-NLS-1$
    
    /** specific path for interfaces */
    private static final String PACKAGE_SPECIFIC_INTERFACE =
        ".widget"; //$NON-NLS-1$
    
    /** specific path for implementation classes */
    private static final String PACKAGE_SPECIFIC_IMPLCLASS =
        ".internal.impl"; //$NON-NLS-1$
    
    /**
     * <code>instance</code> the singleton instance
     */
    private static NameLoader instance = null;
    
    /** the mapping properties */
    private Properties m_mappingProperties;
    

    /**
     * The constructor.
     */
    private NameLoader() {
        try {
            URL resourceURL = NameLoader.class.getClassLoader()
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
    public static NameLoader getInstance() {
        if (instance == null) {
            instance = new NameLoader();
        }
        return instance;
    }
    
    /**
     * Checks in the name mappings property file whether there is a mapping for a
     * given string and returns it and if not, returns the original string
     * @param name original name
     * @return the name which should be used in api
     */
    public String translateFromCompSystem(String name) {
        String mapEntry = m_mappingProperties.getProperty(name);
        if (mapEntry != null) {
            return mapEntry;
        }
        String desiredName = CompSystemI18n.getString(name);
        desiredName = desiredName.replace(
                StringConstants.MINUS, StringConstants.SPACE)
                    .replace(
                StringConstants.LEFT_PARENTHESES, StringConstants.SPACE)
                    .replace(
                StringConstants.RIGHT_PARENTHESES, StringConstants.SPACE)
                    .replace(
                StringConstants.SLASH, StringConstants.SPACE);
        desiredName = WordUtils.capitalizeFully(desiredName);
        desiredName = StringUtils.deleteWhitespace(desiredName);
        desiredName = WordUtils.uncapitalize(desiredName);
        return desiredName;
    }
    
    /**
     * Checks in the name mappings property file whether there is a mapping for a
     * given string and returns it and if not, returns the original string
     * @param name original name
     * @return the name which should be used in api
     */
    public String getFromNameMappings(String name) {
        String mapEntry = m_mappingProperties.getProperty(name);
        if (mapEntry != null) {
            return mapEntry;
        }
        return name;
    }
    
    /**
     * @param component the component
     * @param toolkitName the toolkit name
     * @param generateInterface whether an interface should be generated
     * @return the name extension of the api package name for the component
     */
    public String getPackageName(Component component,
            String toolkitName, boolean generateInterface) {
        StringBuilder packageName = new StringBuilder(
                PACKAGE_BASE_PATH + toolkitName);
        if (generateInterface) {
            packageName.append(PACKAGE_SPECIFIC_INTERFACE);
        } else {
            packageName.append(PACKAGE_SPECIFIC_IMPLCLASS);
        }
        return packageName.toString();
    }
    
    /**
     * @param name the class name in comp system
     * @return the name of the class in the api
     */
    public String getClassName(String name) {
        String mapEntry = m_mappingProperties.getProperty(name);
        if (mapEntry != null) {
            return mapEntry;
        }
        String desiredName = CompSystemI18n.getString(name);
        desiredName = desiredName
                .replace(StringConstants.SLASH, StringConstants.SPACE);
        if (desiredName.startsWith(StringConstants.LEFT_PARENTHESES)) {
            desiredName = StringUtils.substringAfter(
                    desiredName, StringConstants.RIGHT_PARENTHESES);
        }
        desiredName = desiredName
                .replace(StringConstants.LEFT_PARENTHESES,
                        StringConstants.SPACE)
                .replace(StringConstants.RIGHT_PARENTHESES,
                        StringConstants.SPACE);
        desiredName = WordUtils.capitalizeFully(desiredName);
        desiredName = StringUtils.deleteWhitespace(desiredName);
        return desiredName;
    }

    /**
     * Returns the toolkit name
     * @param toolkitDesriptor toolkit descriptor
     * @return the toolkit name
     */
    public String getToolkitName(ToolkitDescriptor toolkitDesriptor) {
        return toolkitDesriptor.getName().toLowerCase();
    }
    
    /**
     * modifies a string such that it fits into api naming patterns
     * @param string the string
     * @return the adjusted string
     */
    public String executeExceptions(String string) {
        return string.replace("abstract", "base").replace("gef", "rcp.gef") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                .replace("ios", "mobile.ios").replace("winApps", "win.apps"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }
}
