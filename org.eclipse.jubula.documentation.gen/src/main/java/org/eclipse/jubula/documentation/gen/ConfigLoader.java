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
package org.eclipse.jubula.documentation.gen;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jubula.tools.utils.generator.ToolkitConfig;


/**
 * Loads the configuration for the Tex generation from the properties file. It
 * is expected that the properties reside in
 * <code>resources/texgen.properties</code>.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 12986 $
 */
public class ConfigLoader {
    /**
     * <code>TEX_GEN_GROUPS</code>
     */
    private static final String TEX_GEN_GROUPS = "TexGen.Groups"; //$NON-NLS-1$
    /**
     * <code>TOOLKIT_LOCATIONS</code>
     */
    private static final String TOOLKIT_NAMES = 
        "TexGen.ToolkitSupport.toolkitnames"; //$NON-NLS-1$
    /**
     * <code>RESOURCES_TEXGEN_PROPERTIES</code>
     */
    private static final String RESOURCES_TEXGEN_PROPERTIES =
        "resources/texgen.properties"; //$NON-NLS-1$
    /**
     * <code>XML_FILENAME</code>
     */
    private static final String XML_PATH = 
        "TexGen.ToolkitSupport.xmlpath"; //$NON-NLS-1$
    /**
     * <code>RESOURCE_BUNDLE_FILENAME</code>
     */
    private static final String RESOURCE_BUNDLE_PATH = 
        "TexGen.ToolkitSupport.resourcebundlepath"; //$NON-NLS-1$
    /**
     * <code>RESOURCE_BUNDLE_FQN</code>
     */
    private static final String RESOURCE_BUNDLE_FQN = 
        "TexGen.ToolkitSupport.resourcebundlefqn"; //$NON-NLS-1$
    /**
     * <code>BASEDIR</code>
     */
    private static final String BASEDIR = "TexGen.ToolkitSupport.basedir"; //$NON-NLS-1$
    /**
     * <code>OUTPUTDIR</code>
     */
    private static final String OUTPUTDIR = "TexGen.ToolkitSupport.outputdir"; //$NON-NLS-1$
    
    /**
     * <code>instance</code> the singleton instance
     */
    private static ConfigLoader instance = null;
    /**
     * The list of Tex generation groups.
     */
    private List<ConfigGroup> m_groups = new ArrayList<ConfigGroup>();
    /**
     * toolkit filename and path information
     */
    private ToolkitConfig m_toolkitConfig;
    /**
     * The constructor.
     */
    private ConfigLoader() {
        Configuration conf;
        try {
            conf = new PropertiesConfiguration(RESOURCES_TEXGEN_PROPERTIES);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        String[] groups = conf.getStringArray(TEX_GEN_GROUPS);
        for (int i = 0; i < groups.length; i++) {
            String group = groups[i];
            m_groups.add(new ConfigGroup(group, conf));
        }
        m_toolkitConfig = new ToolkitConfig(
                conf.getString(BASEDIR),
                conf.getString(XML_PATH),
                conf.getString(RESOURCE_BUNDLE_PATH),
                conf.getString(RESOURCE_BUNDLE_FQN),
                conf.getString(OUTPUTDIR),
                conf.getList(TOOLKIT_NAMES)
        );
    }
    
    /**
     * @return the singleton instance
     */
    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }
    /**
     * @return Returns the Tex generation groups.
     */
    public List<ConfigGroup> getGroups() {
        return m_groups;
    }
    
    /**
     * 
     * @return a list of toolkit plugin names
     */
    public ToolkitConfig getToolkitConfig() {
        return m_toolkitConfig;
    }
}
