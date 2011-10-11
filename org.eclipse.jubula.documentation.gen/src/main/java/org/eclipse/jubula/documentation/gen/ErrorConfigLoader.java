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

/**
 * Loads the configuration for the Tex generation from the properties file. It
 * is expected that the properties reside in
 * <code>resources/texgen.properties</code>.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 5529 $
 */
public class ErrorConfigLoader {
    /**
     * <code>TEX_GEN_GROUP</code>
     */
    private static final String TEX_GEN_GROUP = "TexGen.ErrorGroup"; //$NON-NLS-1$
    /**
     * <code>RESOURCES_TEXGEN_PROPERTIES</code>
     */
    private static final String RESOURCES_TEXGEN_PROPERTIES =
        "resources/texgen.properties"; //$NON-NLS-1$
    /**
     * The list of Tex generation groups.
     */
    private List<ConfigGroup> m_groups = new ArrayList<ConfigGroup>();
    /**
     * The constructor.
     */
    public ErrorConfigLoader() {
        Configuration conf;
        try {
            conf = new PropertiesConfiguration(RESOURCES_TEXGEN_PROPERTIES);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        String[] groups = conf.getStringArray(TEX_GEN_GROUP);
        for (int i = 0; i < groups.length; i++) {
            String group = groups[i];
            m_groups.add(new ConfigGroup(group, conf));
        }
    }
    /**
     * @return Returns the Tex generation groups.
     */
    public List<ConfigGroup> getGroups() {
        return m_groups;
    }
}
