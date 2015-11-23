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
public class CompNamesDTO {
    
    /** */
    private boolean m_propagated;
    /** */
    private String m_originalName, m_newName, m_type;
    
    /**
     * @return propagated
     */
    @JsonProperty("propagated")
    public boolean isPropagated() {
        return m_propagated;
    }
    
    /**
     * @param propagated 
     */
    public void setPropagated(boolean propagated) {
        this.m_propagated = propagated;
    }

    /**
     * @return originalName
     */
    @JsonProperty("originalName")
    public String getOriginalName() {
        return m_originalName;
    }
    
    /**
     * @param originalName 
     */
    public void setOriginalName(String originalName) {
        this.m_originalName = originalName;
    }
    
    /**
     * @return newName
     */
    @JsonProperty("newName")
    public String getNewName() {
        return m_newName;
    }
    
    /**
     * @param newName 
     */
    public void setNewName(String newName) {
        this.m_newName = newName;
    }

    /**
     * @return type
     */
    @JsonProperty("type")
    public String getType() {
        return m_type;
    }
    
    /**
     * @param type 
     */
    public void setType(String type) {
        this.m_type = type;
    }
}
