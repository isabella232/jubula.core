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
package org.eclipse.jubula.client.core.utils;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;

/**
 * Token that represents a function call.
 */
public class FunctionToken extends AbstractParamValueToken 
    implements INestableParamValueToken {

    /** the tokens comprising the function arguments */
    private IParamValueToken[] m_argTokens;
    
    /** the string at the beginning of the function : ?&lt;name&gt;(*/
    private String m_prefix;

    /** the string at the end of the function : ) */
    private String m_suffix;
    
    /**
     * Constructor
     * 
     * @param s string represents the entire token
     * @param functionPrefix string represents the text at the beginning of the token
     * @param functionSuffix string represents the text at the end of the token
     * @param pos index of first character of token in entire string
     * @param desc param description belonging to currently edited parameter value
     * @param argTokens the tokens that comprise the arguments for the function
     */
    public FunctionToken(String s,
            String functionPrefix, String functionSuffix, 
            int pos, IParamDescriptionPO desc, 
            IParamValueToken[] argTokens) {

        super(s, pos, desc);
        m_argTokens = argTokens;
        m_prefix = functionPrefix;
        m_suffix = functionSuffix;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public ConvValidationState validate() {
        // FIXME implement
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getExecutionString(List<ExecObject> stack, Locale locale) {
        // FIXME implement
        return StringUtils.EMPTY;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getGuiString() {
        StringBuilder guiStringBuilder = new StringBuilder();
        guiStringBuilder.append(m_prefix);
        
        for (IParamValueToken nestedToken : getNestedTokens()) {
            guiStringBuilder.append(nestedToken.getGuiString());
        }
        
        guiStringBuilder.append(m_suffix);
        return guiStringBuilder.toString();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getModelString() {
        StringBuilder modelStringBuilder = new StringBuilder();
        modelStringBuilder.append(m_prefix);
        
        for (IParamValueToken nestedToken : getNestedTokens()) {
            modelStringBuilder.append(nestedToken.getModelString());
        }
        
        modelStringBuilder.append(m_suffix);
        return modelStringBuilder.toString();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public IParamValueToken[] getNestedTokens() {
        return m_argTokens;
    }

}
