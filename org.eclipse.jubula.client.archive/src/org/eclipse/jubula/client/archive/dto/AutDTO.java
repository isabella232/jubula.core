/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class AutDTO {

    /** */
    private String m_id, m_name, m_guid, m_autToolkit;
    /** */
    private List<AutConfigDTO> m_configs = new ArrayList<AutConfigDTO>();
    /** */
    private ObjectMappingDTO m_objectMapping;
    /** */
    private boolean m_generateNames = false;
    /** */
    private List<String> m_autIds = new ArrayList<String>();
    
    /**
     * @return autId
     */
    @JsonProperty("id")
    public String getId() {
        return m_id;
    }
    
    /**
     * @param id 
     */
    public void setId(String id) {
        this.m_id = id;
    }
    
    /**
     * @return name 
     */
    @JsonProperty("name")
    public String getName() {
        return m_name;
    }
    
    /**
     * @param name 
     */
    public void setName(String name) {
        this.m_name = name;
    }
    
    /**
     * @return guid
     */
    @JsonProperty("guid")
    public String getGuid() {
        return m_guid;
    }
    
    /**
     * @param guid 
     */
    public void setGuid(String guid) {
        this.m_guid = guid;
    }
    
    /**
     * @return autToolkit
     */
    @JsonProperty("autToolkit")
    public String getAutToolkit() {
        return m_autToolkit;
    }
    
    /**
     * @param autToolkit 
     */
    public void setAutToolkit(String autToolkit) {
        this.m_autToolkit = autToolkit;
    }
    
    /**
     * @return configs
     */
    @JsonProperty("configs")
    public List<AutConfigDTO> getConfigs() {
        return m_configs;
    }
    
    /**
     * @param config 
     */
    public void addConfig(AutConfigDTO config) {
        this.m_configs.add(config);
    }
    
    /**
     * @return objectMapping
     */
    @JsonProperty("objectMapping")
    public ObjectMappingDTO getObjectMapping() {
        return m_objectMapping;
    }
    
    /**
     * @param objectMapping 
     */
    public void setObjectMapping(ObjectMappingDTO objectMapping) {
        this.m_objectMapping = objectMapping;
    }
    
    /**
     * @return generateNames
     */
    @JsonProperty("generateNames")
    public boolean isGenerateNames() {
        return m_generateNames;
    }
    
    /**
     * @param generateNames 
     */
    public void setGenerateNames(boolean generateNames) {
        this.m_generateNames = generateNames;
    }
    
    /**
     * @return autIds
     */
    @JsonProperty("autIds")
    public List<String> getAutIds() {
        return m_autIds;
    }
    
    /**
     * @param autId 
     */
    public void addAutId(String autId) {
        this.m_autIds.add(autId);
    }
}
