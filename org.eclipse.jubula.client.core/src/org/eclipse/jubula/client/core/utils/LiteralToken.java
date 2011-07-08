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

import java.util.List;
import java.util.Locale;

import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.InvalidDataException;




/**
 * token to represent a string starts end ends with a single quote
 */
class LiteralToken implements IParamValueToken {

    /**
     * <code>m_value</code> string represents the token in the GUI
     */
    private String m_value = null;

    /**
     * index of first character of this token in the entire parameter value
     */
    private int m_startPos = -1;

    /**
     * index of last character of this token in the entire value
     */
    private int m_endPos = -1;

    /**
     * <code>m_errorKey</code>I18NKey for error message 
     * associated with result of invocation of validate()
     */
    private Integer m_errorKey;

    /**
     * @param s string represents the token
     * @param pos index of first character of token in entire string
     */
    public LiteralToken(String s, int pos) {
        m_value = s;
        m_startPos = pos;
    }

   
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#validate(org.eclipse.jubula.client.core.model.INodePO)
     */
    public ConvValidationState validate() {
        return ConvValidationState.notSet;
    }


    /**
     * validates, if this token must be internationalized
     * 
     * @return true, if the token needs consideration of locale
     */
    public boolean isI18Nrelevant() {
        return true;
    }

    /** {@inheritDoc}
     *  @see IParamValueToken#getErrorKey()
     */
    public Integer getErrorKey() {
        return m_errorKey;
    }

    /**
     * @return index of last character of tokenvalue in entire string
     */
    public int getEndIndex() {
        return m_endPos;
    }

    /**
     * @return index of first character of tokenvalue in entire string
     */
    public int getStartIndex() {
        return m_startPos;
    }


    /**
     * @return the current value for this token
     */
    public String getGuiString() {
        return m_value;
    }


    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getExecutionString(int, org.eclipse.jubula.client.core.utils.Traverser, java.util.Locale)
     */
    @SuppressWarnings("unused")
    public String getExecutionString(List<ExecObject> stack, 
        Locale locale) throws InvalidDataException {
        
        String execString = StringConstants.EMPTY;
        // remove quotes
        if (m_value != null && m_value.length() > 1) {
            execString = m_value.substring(1, m_value.length() - 1);
        }
        return execString;
    }


    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getModelString()
     */
    public String getModelString() {
        return m_value;
    }


    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#setErrorKey(java.lang.Integer)
     */
    public void setErrorKey(Integer errorKey) {
        m_errorKey = errorKey;        
    }
}
