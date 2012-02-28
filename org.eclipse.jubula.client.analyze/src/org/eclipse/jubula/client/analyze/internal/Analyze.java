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
 * This class is a model for an Analyze. It is used to save the different
 * attributes of an Analyze, when the Analyze is registered when the plugin
 * starts.
 * 
 * @author volker
 * 
 */
public class Analyze {
    /** Analyze ID */
    private String m_id;
    
    /** Analyze Name */
    private String m_name;
    
    /** Analyze Class */
    private String m_class;
    
    /** Analyze CategoryID */
    private String m_categoryID;
    
    /** Analyze ContextType */
    private String m_contextType;
    
    /** Analyze ResultType */
    private String m_resultType;
    
    /** The Instance of this Analyze */
    private Object m_executableExtension;
    
    /** Map containing the Parameters of this Analyze */
    private ArrayList<AnalyzeParameter> m_analyzeParameter;

    /**
     * @param id
     *            The Analyze-ID
     * @param name
     *            The Analyze-Name
     * @param clazz
     *            The Analyze-Class
     * @param catID
     *            The CategoryID
     * @param contextType
     *            The ContextType
     * @param resultType
     *            The ResultType
     */
    public Analyze(String id, String name, String clazz, String catID,
            String contextType, String resultType) {
        setID(id);
        setName(name);
        setClass(clazz);
        setCategoryID(catID);
        setContextType(contextType);
        setResultType(resultType);
    }

    ///////////////////////////
    //  Getters and Setters  //
    ///////////////////////////
    /**
     *  @return The Analyze-Instance
     */
    public Object getExecutableExtension() {
        return m_executableExtension;
    }

    /**
     * @param executableExtension
     *            The Analyze-Instance
     */
    public void setExecutableExtension(Object executableExtension) {
        this.m_executableExtension = executableExtension;
    }

    /**
     * @return The Analyze-ID String
     */
    public String getID() {
        return m_id;
    }

    /**
     * @param id
     *            The Analyze-ID
     */
    public void setID(String id) {
        this.m_id = id;
    }

    /**
     * @return The Analyze-Name String
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name
     *            The Analyze-Name
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * @return String Class
     */
    public String getAnalyzeClass() {
        return m_class;
    }

    /**
     * @param analyzeClass
     *            The Analyze-Class
     */
    public void setClass(String analyzeClass) {
        this.m_class = analyzeClass;
    }

    /**
     * @return The Category-ID String
     */
    public String getCategoryID() {
        return m_categoryID;
    }

    /**
     * @param categoryID
     *            The Category-ID
     */
    public void setCategoryID(String categoryID) {
        this.m_categoryID = categoryID;
    }

    /**
     * @return The ContextType String
     */
    public String getContextType() {
        return m_contextType;
    }

    /**
     * @param contextType
     *            The ContextType
     */
    public void setContextType(String contextType) {
        this.m_contextType = contextType;
    }

    /**
     * @return The resultType String
     */
    public String getResultType() {
        return m_resultType;
    }

    /**
     * @param resultType
     *            The resultType
     */
    public void setResultType(String resultType) {
        this.m_resultType = resultType;
    }

    /**
     * @param analyzeParameter
     *            A List which contains the AnalyzeParameters of this Analyze
     */
    public void setAnalyzeParameter(List<AnalyzeParameter> analyzeParameter) {
        this.m_analyzeParameter = 
                (ArrayList<AnalyzeParameter>) analyzeParameter;
    }

    /**
     * @return A List which contains the AnalyzeParameters of this Analyze
     */
    public List<AnalyzeParameter> getAnalyzeParameter() {
        return m_analyzeParameter;
    }
}
