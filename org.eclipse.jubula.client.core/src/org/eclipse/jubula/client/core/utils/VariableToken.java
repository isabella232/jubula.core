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

import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 14.08.2007
 */
class VariableToken implements IParamValueToken {
    
    /** Constant for a Variable as a data type of test data */
    private static final String VARIABLE = "guidancer.datatype.Variable"; //$NON-NLS-1$

    /**
     * <code>m_value</code> string represents the token in the GUI
     */
    private String m_value = null;

    /**
     * represents the actual name of the variable (without the additional 
     * pre- and post-fix information contained in <code>m_value</code>).
     */
    private String m_variableName = null;

    /**
     * index of first character of this token in the entire parameter value
     */
    private int m_startPos = 0;
    
    /**
     * <code>m_errorKey</code>I18NKey for error message 
     * associated with result of invocation of validate()
     */
    private Integer m_errorKey = null;

    /** param description belonging to currently edited parameter value */
    private IParamDescriptionPO m_desc;

    /**
     * @param s string represents the token
     * @param pos index of first character of token in entire string
     * @param variableName The name of the variable represented by the token
     *                     (without the pre- and post-fix text included in 
     *                     <code>s</code>).
     * @param desc param description belonging to currently edited parameter value
     */
    public VariableToken(String s, int pos, String variableName, 
            IParamDescriptionPO desc) {
        m_value = s;
        m_variableName = variableName;
        m_startPos = pos;
        m_desc = desc;
    }

    /**
     * only runtime validation possible
     * {@inheritDoc}
     * @see IParamValueToken#validate(INodePO)
     */
    public ConvValidationState validate() {
        ConvValidationState state = ConvValidationState.notSet;
        if (VARIABLE.equals(m_desc.getType())) {
            state = ConvValidationState.invalid;
            setErrorKey(MessageIDs.E_INVALID_VAR_NAME);
        }
        return state;
    }


    /**
     * {@inheritDoc}
     * @see IParamValueToken#isI18Nrelevant()
     */
    public boolean isI18Nrelevant() {
        return false;
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
        String  resolvedVar = TestExecution.getInstance()
            .getVariableStore().getValue(m_variableName);
        if (resolvedVar == null) {
            throw new InvalidDataException(Messages.VariableWithName 
                + StringConstants.SPACE + m_value 
                + StringConstants.SPACE + Messages.IsNotResolvable, 
                MessageIDs.E_UNRESOLV_VAR_ERROR);
        }
        return resolvedVar;
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
