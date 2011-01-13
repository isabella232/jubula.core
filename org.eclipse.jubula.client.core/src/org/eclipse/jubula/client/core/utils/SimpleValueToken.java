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
import java.util.regex.Pattern;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 14.08.2007
 */
class SimpleValueToken implements IParamValueToken {
    
   

    /** Constant for a Variable as a data type of test data */
    private static final String VARIABLE = "guidancer.datatype.Variable"; //$NON-NLS-1$

    /**
     * <code>m_value</code> string represents the token in the GUI
     */
    private String m_value = null;

    /**
     * index of first character of this token in the entire parameter value
     */
    private int m_startPos = 0;
    
    /**
     * <code>m_errorKey</code>I18NKey for error message 
     * associated with result of invocation of validate()
     */
    private Integer m_errorKey;

    /** param description belonging to currently edited parameter value */
    private IParamDescriptionPO m_desc;

    /**
     * @param s string represents the token
     * @param pos index of first character of token in entire string
     * @param desc param description belonging to currently edited parameter value
     */
    public SimpleValueToken(String s, int pos, IParamDescriptionPO desc) {
        m_value = s;
        m_startPos = pos;
        m_desc = desc;
    }


    /**
     * {@inheritDoc}
     * @see IParamValueToken#validate(INodePO)
     */
    public ConvValidationState validate() {
        ConvValidationState state = ConvValidationState.notSet;
        if (VARIABLE.equals(m_desc.getType())) {
            final String wordRegex = "[0-9a-z_A-Z]{1,}"; //$NON-NLS-1$
            if (Pattern.matches(wordRegex, m_value)) {
                state = ConvValidationState.valid;                
            } else {
                state = ConvValidationState.invalid;
                setErrorKey(MessageIDs.E_PARSE_NAME_ERROR);
            }
        }
        return state;
    }
    
    
    

    /**
     * {@inheritDoc}
     * @see IParamValueToken#isI18Nrelevant()
     */
    public boolean isI18Nrelevant() {
        return true;
    }

    /**
     * {@inheritDoc}
     * @see IParamValueToken#getErrorKey()
     */
    public Integer getErrorKey() {
        return m_errorKey;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getEndIndex()
     */
    public int getEndIndex() {
        return m_startPos + m_value.length();
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getStartIndex()
     */
    public int getStartIndex() {
        return m_startPos;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getValue()
     */
    public String getGuiString() {
        return m_value;
    }

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getExecutionString(int, org.eclipse.jubula.client.core.utils.Traverser, java.util.Locale)
     */
    public String getExecutionString(List<ExecObject> stack, Locale locale) 
        throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        do {
            char c = m_value.charAt(index);
            if (c == '\\') {
                if (index + 1 < m_value.length()) {
                    index++;
                    c = m_value.charAt(index);
                    char[] validChars = {'\\', '=', '{', '}', '$', '\''};
                    boolean isValid = false;
                    for (char validChar : validChars) {
                        if (validChar == c) {
                            builder.append(c);
                            isValid = true; 
                            index++;
                            break;
                        }   
                    }
                    if (!isValid) {
                        throw new InvalidDataException(
                            "Invalid character " + c + " after backslash in " + m_value, //$NON-NLS-1$ //$NON-NLS-2$
                            MessageIDs.E_SYNTAX_ERROR);
                    }            
                } else {
                    throw new InvalidDataException(
                        "Not allowed to set a single backslash in " + m_value, //$NON-NLS-1$
                        MessageIDs.E_SYNTAX_ERROR);
                }            
            } else {
                builder.append(c);
                index++;
            }
        } while (index < m_value.length());
        return builder.toString();
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getModelString()
     */
    public String getModelString() {
        return m_value;
    }

    /**
     * @param errorKey The errorKey to set.
     */
    public void setErrorKey(Integer errorKey) {
        m_errorKey = errorKey;
    }
}
