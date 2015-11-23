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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class ComponentNameDTO {

    /** */
    private String m_compType, m_guid, m_creationContext, m_refGuid, m_compName;

    /**
     * @return compType
     */
    @JsonProperty("compType")
    public String getCompType() {
        return m_compType;
    }

    /**
     * @param compType 
     */
    public void setCompType(String compType) {
        this.m_compType = compType;
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
     * @return creationContext
     */
    @JsonProperty("creationContext")
    public String getCreationContext() {
        return m_creationContext;
    }

    /**
     * @param creationContext 
     */
    public void setCreationContext(String creationContext) {
        this.m_creationContext = creationContext;
    }

    /**
     * @return refGuid
     */
    @JsonProperty("refGuid")
    public String getRefGuid() {
        return m_refGuid;
    }

    /**
     * @param refGuid 
     */
    public void setRefGuid(String refGuid) {
        this.m_refGuid = refGuid;
    }

    /**
     * @return compName
     */
    @JsonProperty("compName")
    public String getCompName() {
        return m_compName;
    }

    /**
     * @param compName 
     */
    public void setCompName(String compName) {
        this.m_compName = compName;
    }
}
