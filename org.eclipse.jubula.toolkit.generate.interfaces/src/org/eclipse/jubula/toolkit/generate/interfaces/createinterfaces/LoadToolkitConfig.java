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
package org.eclipse.jubula.toolkit.generate.interfaces.createinterfaces;

import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jubula.tools.utils.generator.ToolkitConfig;

/**
 * @author BREDEX GmbH
 * @created Dec 1, 2009
 */
public class LoadToolkitConfig {
    /**
     * <code>PROPERTY_FILE</code>
     */
    private static final String PROPERTY_FILE = "toolkit.properties"; //$NON-NLS-1$

    /**
     * <code>TOOLKIT_NAMES</code>
     */
    private static final String TOOLKIT_NAMES = "GenInterfaces.ToolkitSupport.toolkitnames"; //$NON-NLS-1$

    /**
     * <code>BASEDIR</code>
     */
    private static final String BASEDIR = "GenInterfaces.ToolkitSupport.basedir"; //$NON-NLS-1$

    /**
     * <code>XML_PATH</code>
     */
    private static final String XML_PATH = "GenInterfaces.ToolkitSupport.xmlpath"; //$NON-NLS-1$

    /**
     * <code>RESOURCE_BUNDLE_PATH</code>
     */
    private static final String RESOURCE_BUNDLE_PATH = "GenInterfaces.ToolkitSupport.resourcebundlepath"; //$NON-NLS-1$

    /**
     * <code>RESOURCE_BUNDLE_FQN</code>
     */
    private static final String RESOURCE_BUNDLE_FQN = "GenInterfaces.ToolkitSupport.resourcebundlefqn"; //$NON-NLS-1$

    /**
     * <code>OUTPUTDIR</code>
     */
    private static final String OUTPUTDIR = "GenInterfaces.ToolkitSupport.outputdir"; //$NON-NLS-1$

    /**
     * loadConfig
     * 
     * @return toolkitconfig
     */
    public ToolkitConfig loadConfig() {
        Configuration conf;
        try {
            URL prop = ClassLoader.getSystemResource(PROPERTY_FILE);
            conf = new PropertiesConfiguration(prop);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        return new ToolkitConfig(conf.getString(BASEDIR), conf
                .getString(XML_PATH), conf.getString(RESOURCE_BUNDLE_PATH),
                conf.getString(RESOURCE_BUNDLE_FQN), conf.getString(OUTPUTDIR),
                conf.getList(TOOLKIT_NAMES));
    }
}
