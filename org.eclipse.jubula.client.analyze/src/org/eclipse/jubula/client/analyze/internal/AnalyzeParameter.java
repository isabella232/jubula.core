/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.internal;

import org.apache.commons.lang.StringUtils;
/**
 * This class is a model for an AnalyzeParameter. It is used to save the
 * different attributes of an AnalyzeParameter, when the AnalyzeParameter is
 * registered when the plugin starts.
 * 
 * @author volker
 * 
 */
public class AnalyzeParameter {
    
    /** The AnalyzeParameter-ID */
    private String m_id;
    
    /** The AnalyzeParameter-defaultValue */
    private String m_defaultValue;
    
    /** The AnalyzeParameter-modifiedValue */
    private String m_modifiedValue;
    
    /** The AnalyzeParameter-name */
    private String m_name;
    
    /** The AnalyzeParameter-description */
    private String m_description;

    /**
     * 
     * @param id
     *            The AnalyzeParameter-ID
     * @param defaultValue
     *            The AnalyzeParameter-DefaultVaule
     * @param name
     *            The AnalyzeParameter-name
     * @param description
     *            The AnalyzeParameter-description
     * @param modifiedValue
     *            The AnalyzeParameter-modified value
     */
    public AnalyzeParameter(String id, String defaultValue, String name,
            String description, String modifiedValue) {
        setID(id);
        setDefaultValue(defaultValue);
        setName(name);
        setDescription(description);
        setModifiedValue(modifiedValue);
    }

    /**
     * @return m_name The name of the AnalyzeParameter
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name
     *            The name of the AnalyzeParameter
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * @return m_description The description of the AnalyzeParameter
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description
     *            The description of the AnalyzeParameterDescription
     */
    public void setDescription(String description) {
        this.m_description = description;
    }

    /**
     * @return The id of the AnalyzeParameter 
     */
    public String getID() {
        return m_id;
    }

    /**
     * @param id
     *            The id of the AnalyzeParameter
     */
    public void setID(String id) {
        this.m_id = id;
    }

    /**
     * @return String The defaultValue of the AnalyzeParameter
     */
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue
     *            The defaultValue of the AnalyzeParameter
     */
    public void setDefaultValue(String defaultValue) {
        this.m_defaultValue = defaultValue;
    }
    
    /**
     * @return The modifiedValue of the AnalyzeParameter
     */
    public String getModifiedValue() {
        return m_modifiedValue;
    }

    /**
     * @param modifiedValue
     *            The modifiedValue of the AnalyzeParameter
     */
    public void setModifiedValue(String modifiedValue) {
        this.m_modifiedValue = modifiedValue;
    }
    /**
     * @return The defaultValue if there is no modified Value.
     */
    public String getValue() {
        if (StringUtils.isEmpty(m_modifiedValue)) {
            return m_defaultValue;
        } else {
            return m_modifiedValue;
        }
    }
}
