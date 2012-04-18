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

import java.util.List;

/**
 * This class is a model for an AnalyzeResult. It is used to save the different
 * attributes of an AnalyzeResult, when the AnalyzeResult is registered when the plugin
 * starts.
 * @author volker
 *
 */
public class AnalyzeResult {

    /**
     * ResultType identifier for the Renderer selection in the ResultRendererHandler
     */
    private String m_rType = "";
    
    /** Analyze ID */
    private String m_analyzeID = "";
    
    /** CategoryID */
    private String m_catID = "";
    
    /** ContextType */
    private String m_context = "";
    
    /** The result-Object */
    private Object m_analyzeResult;
    
    /** The parameter list of the analyze */
    private List<AnalyzeParameter> m_parameterList;
    
    /** The additionalData of the executed Analyze*/
    private Object m_additionalData;

    /**
     * Constructor
     * @param resultType The ResultType
     * @param result The ResultValue
     * @param params The AnalyzeParameter list
     */
    public AnalyzeResult(String resultType, Object result,
            List<AnalyzeParameter> params) {
        setResultType(resultType);
        setResult(result);
        setParameterList(params);
    }
    /**
     * Constructor
     * @param resultType The ResultType
     * @param result The ResultValue
     * @param params The AnalyzeParameter list
     * @param workbenchPart The WorkbenchPart of the executed Analyze
     */
    public AnalyzeResult(String resultType, Object result,
            List<AnalyzeParameter> params, Object workbenchPart) {
        setResultType(resultType);
        setResult(result);
        setParameterList(params);
        setAdditionalData(workbenchPart);
    }
    /**
     * @return The resultType
     */
    public String getResultType() {
        return m_rType;
    }
    
    /**
     * @param resultType The resultType
     */
    public void setResultType(String resultType) {
        m_rType = resultType;
    }
    
    /**
     * @return ID The ID of the results Analyze
     */
    public String getId() {
        return m_analyzeID;
    }
    
    /**
     * @param id The ID of the results Analyze
     */
    public void setID(String id) {
        m_analyzeID = id;
    }
    
    /**
     * @return The resultValue of this analyze
     */
    public Object getResult() {
        return m_analyzeResult;
    }
    
    /**
     * @param result The resultValue of this Analyze 
     */
    private void setResult(Object result) {
        m_analyzeResult = result;
    }
    
    /**
     * @return The CategoryID
     */
    public String getCategoryID() {
        return m_catID;
    }
    
    /**
     * @param categoryID The CategoryID
     */
    public void setCategoryID(String categoryID) {
        m_catID = categoryID;
    }
    
    /**
     * @return The contextType
     */
    public String getContextType() {
        return m_context;
    }
    
    /**
     * @param contextType The contextType
     */
    public void setContextType(String contextType) {
        m_context = contextType;
    }
    
    /**
     * 
     * @return The list that includes the AnalyzeParameters. Can be null.
     */
    public List<AnalyzeParameter> getParameterList() {
        return m_parameterList;
    }
    
    /**
     * @param parameterList The List with AnalyzeParameters
     */
    public void setParameterList(List<AnalyzeParameter> parameterList) {
        this.m_parameterList = parameterList;
    }
    
    /**
     * @param part
     *             The given additionalData 
     */
    public void setAdditionalData(Object part) {
        this.m_additionalData = part;
    }
    /**
     * @return The additionalData of the executed Analyze
     */
    public Object getAdditionalData() {
        return m_additionalData;
    }
}
