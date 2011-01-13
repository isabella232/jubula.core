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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.utils.Parser.ParserException;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;




/**
 * @author BREDEX GmbH
 * @created 16.08.2007
 */
public abstract class ParamValueConverter {

    /** string in gui representation */
    private String m_guiString = null;

    /**
     * <code>m_modelString</code> string in model representation
     */
    private String m_modelString = null;

    /**
     * list of tokens for current string
     * 
     */
    private List<IParamValueToken> m_tokens = 
        new ArrayList<IParamValueToken>();

    /**
     * <code>m_errors</code>errors, detected in tokens
     */
    private List<TokenError> m_errors = new ArrayList<TokenError>(1);

    /**
     * <code>m_currentNode</code>node contains the parameter with this parameter value
     */
    private IParameterInterfacePO m_currentNode;

    /** currently used language */
    private Locale m_locale;

    /** param description associated with current gui- or model string */
    private IParamDescriptionPO m_desc;
    
    /** validator for special validations */
    private IParamValueValidator m_validator;
   
    /**
     * describes the state of a single token
     */
    public enum ConvValidationState {
        /**token has syntax or semantical errors */ 
        invalid,
        /** unknown state */
        notSet,
        /** currently invalid, but could be valid later */
        undecided,
        /** token is free of syntax or semantical errors */
        valid
    }

    
    /**
     * @param currentNode node with parameter for this parameterValue
     * @param locale current used language
     * @param desc param description associated with current string (parameter value)
     * @param validator to use for special validations
     */
    public ParamValueConverter(IParameterInterfacePO currentNode, Locale locale,
        IParamDescriptionPO desc, IParamValueValidator validator)  {
        Validate.notNull(currentNode, "Node for given parameter value must not be null."); //$NON-NLS-1$
        m_currentNode = currentNode;
        m_locale = locale;
        m_desc = desc;
        m_validator = validator;
    }
    
    /**
     * default constructor
     */
    protected ParamValueConverter() {
        // do nothing
    }
    
    /**
     * @return list of reference names containing in s
     */
    public List<String> getNamesForReferences() {
        List<String> paramNames = new ArrayList<String>();
        for (IParamValueToken token : m_tokens) {
            if (token instanceof RefToken) {
                RefToken refToken = (RefToken)token;
                paramNames.add(RefToken.extractCore(refToken.getGuiString()));
            }
        }
        return paramNames;
    }
    
    /**
     * @return list of variables contained in current string
     */
    public List<String> getVariables() {
        List<String> variables = new ArrayList<String>();
        for (IParamValueToken token : m_tokens) {
            if (token instanceof VariableToken) {
                variables.add(((VariableToken)token).getGuiString());
            }
        }
        return variables;
    }
    
    
    /**
     * @return true, if string contains at least one reference
     */
    public boolean containsReferences() {
        for (IParamValueToken token : m_tokens) {
            if (token instanceof RefToken) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return true, if string contains only simple values
     */
    public boolean containsOnlySimpleValues() {
        for (IParamValueToken token : m_tokens) {
            if (!(token instanceof SimpleValueToken)) {
                return false;
            }
        }
        return true;
    }
   
    /**
     * @param stack current execution stack
     * @param locale currently used language for testexecution
     * @return string for testexecution
     * @throws InvalidDataException in case of any problem to resolve the token
     */
    public String getExecutionString(List<ExecObject> stack, 
        Locale locale) throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        for (IParamValueToken token : m_tokens) {
            builder.append(token.getExecutionString(
                new ArrayList<ExecObject>(stack), locale));
        }
        return builder.toString();
    }

    /**
     * parses the GUI string and separates it in single tokens
     * 
     */
    abstract void createTokens();

    /** calls the validation for each token */
    abstract void validateSingleTokens();

    /**
     * @return Returns the tokens.
     */
    protected List<IParamValueToken> getTokens() {
        return m_tokens;
    }
    
    

    /**
     * @param tokens The tokens to set.
     */
    protected void setTokens(List<IParamValueToken> tokens) {
        m_tokens = tokens;
    }

    /**
     * @return Returns the currentNode.
     */
    public IParameterInterfacePO getCurrentNode() {
        return m_currentNode;
    }

    /**
     * @return Returns the guiString.
     */
    public String getGuiString() {
        return m_guiString;
    }

    /**
     * @param guiString The guiString to set.
     */
    protected void setGuiString(String guiString) {
        m_guiString = guiString;
    }
    
    /**
     * @return Returns the modelString.
     */
    public String getModelString() {
        return m_modelString;
    }
    
    /**
     * @return an unmodifiable list of contained RefTokens
     */
    public List<RefToken> getRefTokens() {
        List <RefToken> refTokens = new ArrayList<RefToken>();
        for (IParamValueToken token : getTokens()) {
            if (token instanceof RefToken) {
                refTokens.add((RefToken)token);
            }
        }
        return Collections.unmodifiableList(refTokens);
    }

    /**
     * @param modelString The modelString to set.
     */
    protected void setModelString(String modelString) {
        m_modelString = modelString;
    }

    /**
     * @return Returns the locale.
     */
    Locale getLocale() {
        return m_locale;
    }

    /**
     * @return Returns the desc.
     */
    public IParamDescriptionPO getDesc() {
        return m_desc;
    }
    
    /**
     * @param desc The desc to set.
     */
    protected void setDesc(IParamDescriptionPO desc) {
        m_desc = desc;
    }

    /**
     * @return list of detected errors in tokens
     */
    public List <TokenError> getErrors() {
        validateSingleTokens();
        return m_errors;
    }
    
    /**
     * @return if currently converted and validated string contains errors
     */
    public boolean containsErrors() {
        return !(m_errors.isEmpty());
    }
    
    /**
     * @param error error to add to error list
     */
    protected void addError(TokenError error) {
        m_errors.add(error);
    }

    /**
     * @param errors The errors to set.
     */
    protected void setErrors(List<TokenError> errors) {
        m_errors = errors;
    }

    /**
     * @param locale The locale to set.
     */
    protected void setLocale(Locale locale) {
        m_locale = locale;
    }

    /**
     * @param currentNode The currentNode to set.
     */
    void setCurrentNode(IParamNodePO currentNode) {
        m_currentNode = currentNode;
    }
    
    /**
     * @param e occured ParserException
     */
    protected void createErrors(ParserException e) {
        List<Integer> undecidedMsgs = new ArrayList<Integer>();
        undecidedMsgs.add(MessageIDs.E_ONE_CHAR_PARSE_ERROR);
        undecidedMsgs.add(MessageIDs.E_MISSING_CLOSING_BRACE);
        
        if (undecidedMsgs.contains(e.getMessageId())) {
            addError(new TokenError(e.getInput(), e.getStartPos(), 
                e.getMessageId(), ConvValidationState.undecided));
        } else {
            addError(new TokenError(e.getInput(), e.getStartPos(), 
                e.getMessageId(), ConvValidationState.invalid));
        }
    }

    /**
     * @return Returns the validator.
     */
    IParamValueValidator getValidator() {
        return m_validator;
    }

    /**
     * creates appropriate TokenError for given ConverterValidationState
     * @param state computed validation state
     * @param token validated token
     */
    protected void createTokenError(ConvValidationState state, 
        IParamValueToken token) {
        if (state == ConvValidationState.invalid 
            || state == ConvValidationState.undecided) {
            TokenError tokenError = 
                new TokenError(getGuiString(), token.getStartIndex(), 
                    token.getErrorKey(), state);
            addError(tokenError);
        }
    }
   
}
