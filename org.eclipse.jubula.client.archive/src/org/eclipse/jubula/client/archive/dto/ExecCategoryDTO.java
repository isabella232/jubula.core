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

import org.eclipse.jubula.client.core.model.INodePO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class ExecCategoryDTO extends NodeDTO {

    /** it could contain testsuite or testjob or category */
    private List<NodeDTO> m_nodes =
            new ArrayList<NodeDTO>();

    
    /** needed because JSON mapping */
    public ExecCategoryDTO() { }
    
    /**
     * @param node 
     */
    public ExecCategoryDTO(INodePO node) {
        super(node);
    }
    
    /**
     * @param node 
     */
    public void addNode(NodeDTO node) {
        if (!(node instanceof TestSuiteDTO
                || node instanceof TestJobDTO
                || node instanceof ExecCategoryDTO)) {
            
            throw new IllegalArgumentException();
        }
        this.m_nodes.add(node);
    }
    
    /**
     * @return nodes
     */
    @JsonProperty("nodes")
    public List<NodeDTO> getNodes() {
        return m_nodes;
    }
}
