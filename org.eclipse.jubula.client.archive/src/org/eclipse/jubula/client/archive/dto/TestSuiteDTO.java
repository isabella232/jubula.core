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
import org.eclipse.jubula.tools.internal.constants.StringConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class TestSuiteDTO extends NodeDTO {

    /** */
    private String m_selectedAut;
    /** */
    private String m_commandLineParameter = StringConstants.EMPTY;
    /** */
    private List<String> m_selectedAutConfigs = new ArrayList<String>();
    /** */
    private int m_stepDelay = 0;
    /** */
    private List<RefTestCaseDTO> m_usedTestcases =
            new ArrayList<RefTestCaseDTO>();
    /** */
    private List<EventHandlerDTO> m_eventHandlers =
            new ArrayList<EventHandlerDTO>();
    /** */
    private boolean m_relevant;
    
    
    /** needed because json mapping */
    public TestSuiteDTO() { }
    
    /**
     * @param node 
     */
    public TestSuiteDTO(INodePO node) {
        super(node);
    }

    /**
     * @return selectedAut
     */
    @JsonProperty("selectedAut")
    public String getSelectedAut() {
        return m_selectedAut;
    }

    /**
     * @param selectedAut 
     */
    public void setSelectedAut(String selectedAut) {
        this.m_selectedAut = selectedAut;
    }

    /**
     * @return commandLineParameter
     */
    @JsonProperty("commandLineParameter")
    public String getCommandLineParameter() {
        return m_commandLineParameter;
    }

    /**
     * @param commandLineParameter 
     */
    public void setCommandLineParameter(String commandLineParameter) {
        this.m_commandLineParameter = commandLineParameter;
    }

    /**
     * @return selectedAutConfigs
     */
    @JsonProperty("selectedAutConfigs")
    public List<String> getSelectedAutConfigs() {
        return m_selectedAutConfigs;
    }

    /**
     * @param selectedAutConfig 
     */
    public void addSelectedAutConfig(String selectedAutConfig) {
        this.m_selectedAutConfigs.add(selectedAutConfig);
    }

    /**
     * @return stepDelay
     */
    @JsonProperty("stepDelay")
    public int getStepDelay() {
        return m_stepDelay;
    }

    /**
     * @param stepDelay 
     */
    public void setStepDelay(int stepDelay) {
        this.m_stepDelay = stepDelay;
    }

    /**
     * @return usedTestcase
     */
    @JsonProperty("usedTestcase")
    public List<RefTestCaseDTO> getUsedTestcases() {
        return m_usedTestcases;
    }

    /**
     * @param usedTestcase 
     */
    public void addUsedTestcase(RefTestCaseDTO usedTestcase) {
        this.m_usedTestcases.add(usedTestcase);
    }

    /**
     * @return eventHandlers
     */
    @JsonProperty("eventHandlers")
    public List<EventHandlerDTO> getEventHandlers() {
        return m_eventHandlers;
    }

    /**
     * @param eventHandler 
     */
    public void addEventHandler(EventHandlerDTO eventHandler) {
        this.m_eventHandlers.add(eventHandler);
    }

    /**
     * @return relevant
     */
    @JsonProperty("relevant")
    public boolean isRelevant() {
        return m_relevant;
    }
    
    /**
     * @param relevant 
     */
    public void setRelevant(boolean relevant) {
        this.m_relevant = relevant;
    }
}
