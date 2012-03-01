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

import java.util.ArrayList;
import java.util.List;

/**
 * An AnalyzeRun contains at least one Analyze. If there is more than one Analyze in an 
 * AnalyzeRun, they will be executed one after another.   
 * @author volker
 *
 */
public class AnalyzeRun {
    /**
     * This List represents an AnalyzeRun. It contains every Analyze that has to be executed
     */
    private ArrayList<Analyze> m_runList;
    /**
     * @param runList The given AnalyzeRun List
     */
    public void setAnalyzeRunMap(List<Analyze> runList) {
        this.m_runList = new ArrayList<Analyze>(runList);
    }
    /**
     * @return The AnalyzeRun represented as a List of Analyzes
     */
    public List<Analyze> getAnalyzeRunList() {
        return m_runList;
    }
    /**
     * This method creates the AnalyzeRun
     * @param analyzeList The given List contains the Analyzes to be run
     * @return The AnalyzeRun ArrayList
     * 
     */
    public List<Analyze> createAnalyzeRun(List<Analyze> analyzeList) {
        this.setAnalyzeRunMap(analyzeList);
        return m_runList;
    }
}
