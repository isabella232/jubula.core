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
public class TestJobDTO extends NodeDTO {

    /** */
    private List<RefTestSuiteDTO> m_refTestSuites =
            new ArrayList<RefTestSuiteDTO>();

    
    /** needed because json mapping */
    public TestJobDTO() { }
    
    /**
     * @param node 
     */
    public TestJobDTO(INodePO node) {
        super(node);
    }
    
    /**
     * @param rtsDTO 
     */
    public void addRefTestSuite(RefTestSuiteDTO rtsDTO) {
        m_refTestSuites.add(rtsDTO);
    }

    /**
     * @return refTestSuites
     */
    @JsonProperty("refTestSuites")
    public List<RefTestSuiteDTO> getRefTestSuites() {
        return m_refTestSuites;
    }
}
