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

import org.eclipse.jubula.client.core.model.IRefTestSuitePO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class RefTestSuiteDTO extends NodeDTO {

    /** */
    private String m_tsGuid, m_autId;

    
    /** needed because json mapping */
    public RefTestSuiteDTO() { }
    
    /**
     * @param node 
     */
    public RefTestSuiteDTO(IRefTestSuitePO node) {
        super(node);
        this.m_tsGuid = node.getTestSuiteGuid();
        this.m_autId = node.getTestSuiteAutID();
    }

    /**
     * @return tsGuid
     */
    @JsonProperty("tsGuid")
    public String getTsGuid() {
        return m_tsGuid;
    }

    /**
     * @param tsGuid 
     */
    public void setTsGuid(String tsGuid) {
        this.m_tsGuid = tsGuid;
    }

    /**
     * @return autId
     */
    @JsonProperty("autId")
    public String getAutId() {
        return m_autId;
    }

    /**
     * @param autId 
     */
    public void setAutId(String autId) {
        this.m_autId = autId;
    }
}
