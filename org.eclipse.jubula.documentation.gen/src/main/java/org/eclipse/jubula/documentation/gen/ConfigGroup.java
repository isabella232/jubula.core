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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * Represents a configuration group of the Tex generation. A group contains the
 * Tex template file name, the Tex output file name, the generator class name
 * and the generator specific properties.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 13203 $
 */
public class ConfigGroup {
    /**
     * <code>GENERATOR</code>
     */
    private static final String GENERATOR = "Generator"; //$NON-NLS-1$
    /**
     * <code>OUTPUT</code>
     */
    private static final String OUTPUT = "Output"; //$NON-NLS-1$
    /**
     * <code>TEMPLATE</code>
     */
    private static final String TEMPLATE = "Template"; //$NON-NLS-1$
    /**
     * The Tex template file name.
     */
    private String m_template;
    /**
     * The Tex output file name.
     */
    private String m_output;
    /**
     * The generator class name.
     */
    private String m_generatorClass;
    /**
     * The generator properties.
     */
    private Map<String, String> m_generatorProps =
        new HashMap<String, String>();
    
    /**
     * The name of this group.
     */
    private String m_name;
    
    /**
     * @param group
     *            The name of the group read from the properties file
     * @param conf
     *            The configuration source (the properties file)
     */
    public ConfigGroup(String group, Configuration conf) {
        Configuration subset = conf.subset(group);
        m_template = subset.getString(TEMPLATE);
        m_output = subset.getString(OUTPUT);
        m_generatorClass = subset.getString(GENERATOR);
        m_name = group;
        
        subset = subset.subset(GENERATOR);
        for (Iterator it = subset.getKeys(); it.hasNext();) {
            String key = (String)it.next();
            if (!StringConstants.EMPTY.equals(key)) {
                m_generatorProps.put(key, subset.getString(key));
            }
        }
    }

    /**
     * @return Returns the generator class name.
     */
    public String getGeneratorClass() {
        return m_generatorClass;
    }

    /**
     * @return Returns the output file name.
     */
    public String getOutput() {
        return m_output;
    }

    /**
     * @return Returns the template file name.
     */
    public String getTemplate() {
        return m_template;
    }
    /**
     * @param key
     *            The key
     * @return The value
     * @throws IllegalArgumentException
     *             If the key is invalid
     */
    public String getProp(String key) throws IllegalArgumentException {
        if (!m_generatorProps.containsKey(key)) {
            throw new IllegalArgumentException("This key is not defined: " //$NON-NLS-1$
                + key);
        }
        return m_generatorProps.get(key);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }
}
