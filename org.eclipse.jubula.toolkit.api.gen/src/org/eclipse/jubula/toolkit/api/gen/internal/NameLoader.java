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
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
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
        ".widgets"; //$NON-NLS-1$
    
    /** specific path for implementation classes */
    private static final String PACKAGE_SPECIFIC_IMPLCLASS =
        ".internal.impl"; //$NON-NLS-1$

    /** specific path for implementation classes */
    private static final String FACTORY_NAME_EXTENSION =
        "ComponentFactory"; //$NON-NLS-1$
    
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
     * Translates a comp system name (from e.g. an action or a parameter) 
     * to how it shall be used in api
     * @param name original name
     * @return the name which should be used in api
     */
    public String translateFromCompSystem(String name) {
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
     * given parameter name and returns it and if not, returns the original type
     * @param type the type of the parameter
     * @param name the name of the parameter
     * @return the name which should be used in api
     */
    public String findEnumForParameter(String type, String name) {
        String mapEntry = m_mappingProperties.getProperty(name);
        if (mapEntry != null) {
            return mapEntry;
        }
        mapEntry = m_mappingProperties.getProperty(type);
        if (mapEntry != null) {
            return mapEntry;
        }
        return type;
    }
    
    /**
     * @param toolkitName the toolkit name
     * @return the name extension of the api package name for the component
     */
    public String getClassPackageName(String toolkitName) {
        return getToolkitPackageName(toolkitName) + PACKAGE_SPECIFIC_IMPLCLASS;
    }
    
    /**
     * @param toolkitName the toolkit name
     * @return the name extension of the api package name for the component
     */
    public String getInterfacePackageName(String toolkitName) {
        return getToolkitPackageName(toolkitName) + PACKAGE_SPECIFIC_INTERFACE;
    }
    
    /**
     * @param toolkitName the toolkit name
     * @return the toolkit package base name
     */
    public String getToolkitPackageName(String toolkitName) {
        return PACKAGE_BASE_PATH + toolkitName;
    }
    
    /**
     * Translates a component name from the comp system to how it shall be used in the api
     * @param name the class name in comp system
     * @return the name of the class in the api
     */
    public String getClassName(String name) {
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
        return string.replace("abstract", "base"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the name for a component factory for a toolkit
     * @param toolkitName the toolkit name
     * @return the name for a component factory for a toolkit
     */
    public String getFactoryName(String toolkitName) {
        String tkName = WordUtils.capitalizeFully(toolkitName);
        return tkName + FACTORY_NAME_EXTENSION;
    }
    
    /**
     * Returns the name of factory of the toolkit above in toolkit hierarchy
     * <code>null</code> if there is no toolkit above in toolkit hierarchy
     * @param tkDescriptor toolkit descriptor
     * @param compsystem the comp system
     * @return the name of factory of the toolkit above in toolkit hierarchy
     */
    public String getSuperFactoryName(ToolkitDescriptor tkDescriptor,
            CompSystem compsystem) {
        String name = null;
        String includes = tkDescriptor.getIncludes();
        String depends = tkDescriptor.getDepends();
        String superToolkit = !includes.equals("null") ? includes : depends; //$NON-NLS-1$
        if (!"null".equals(superToolkit) && !StringConstants.EMPTY.equals(superToolkit)) { //$NON-NLS-1$
            superToolkit = compsystem.getToolkitDescriptor(superToolkit)
                    .getName();            
            name = getToolkitPackageName(superToolkit) + StringConstants.DOT
                    + getFactoryName(superToolkit);
            name = executeExceptions(name);
        }
        return name;
    }
}
