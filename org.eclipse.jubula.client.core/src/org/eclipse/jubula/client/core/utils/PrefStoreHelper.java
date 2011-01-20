/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;


/**
 * Stores preferences from TestDataPreferencePage for a faster access (
 * => <b>enormous</b> performance improvement, while typing parameter values).
 * @author BREDEX GmbH
 * @created 02.02.2007
 */
public class PrefStoreHelper {
    
    /** the singleton instance */
    private static PrefStoreHelper instance = null;
    /** the user defined reference char */
    private String m_referenceChar;
    /** the user defined escape char */
    private String m_escapeChar; 
    /** the user defined function char */
    private String m_functionChar;
    /** the user defined value char */
    private String m_valueChar;
    /** the user defined variable char */
    private String m_variableChar;
    
    /**
     * utility constructor 
     */
    private PrefStoreHelper() {
        // for initialization
    }
    
    /**
     * @return the singleton instance of PrefStoreHelper
     * <b>(has to be called first time when starting Jubula)</b>
     */
    public static PrefStoreHelper getInstance() {
        if (instance == null) {
            instance = new PrefStoreHelper();
        }
        return instance;
    }

    /**
     * @return the escapeChar
     */
    public String getEscapeChar() {
        return m_escapeChar;
    }

    /**
     * @param escapeChar the escapeChar to set
     */
    public void setEscapeChar(String escapeChar) {
        m_escapeChar = escapeChar;
    }

    /**
     * @return the functionChar
     */
    public String getFunctionChar() {
        return m_functionChar;
    }

    /**
     * @param functionChar the functionChar to set
     */
    public void setFunctionChar(String functionChar) {
        m_functionChar = functionChar;
    }

    /**
     * @return the referenceChar
     */
    public String getReferenceChar() {
        return m_referenceChar;
    }

    /**
     * @param referenceChar the referenceChar to set
     */
    public void setReferenceChar(String referenceChar) {
        m_referenceChar = referenceChar;
    }

    /**
     * @return the valueChar
     */
    public String getValueChar() {
        return m_valueChar;
    }
    
    /**
     * @return the variableChar
     */
    public String getVariableChar() {
        return m_variableChar;
    }

    /**
     * @param valueChar the valueChar to set
     */
    public void setValueChar(String valueChar) {
        m_valueChar = valueChar;
    }

    /**
     * @param variableChar the variableChar to set
     */
    public void setVariableChar(String variableChar) {
        m_variableChar = variableChar;
    }
}