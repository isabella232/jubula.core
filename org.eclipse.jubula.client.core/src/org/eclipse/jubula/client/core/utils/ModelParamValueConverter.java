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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.LexerException;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.Parser;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.ParserException;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITestDataPO;
import org.eclipse.jubula.client.core.parser.parameter.JubulaParameterLexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 31.10.2007
 */
public class ModelParamValueConverter extends ParamValueConverter {
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ModelParamValueConverter.class);
    
    /**
     * hint: the string could be null.
     * @param modelString to convert
     * @param currentNode node with parameter for this parameterValue
     * @param locale current used language
     * @param desc param description associated with current string (parameter value)
     */
    public ModelParamValueConverter(String modelString,
            IParameterInterfacePO currentNode, Locale locale,
            IParamDescriptionPO desc) {
        super(currentNode, locale, desc, new NullValidator());
        setModelString(modelString);
        createTokens();
    }
    
    /**
     * @param td testDataObject with model string to convert
     * @param currentNode node with parameter for this parameterValue
     * @param locale current used language
     * @param desc param description associated with current string (parameter value)
     */
    public ModelParamValueConverter(ITestDataPO td,
            IParameterInterfacePO currentNode, Locale locale, 
            IParamDescriptionPO desc) {
        this(td.getValue(locale), currentNode, locale, desc);
    }
    
    /**
     * @return gui representation of string
     */
    public String getGuiString() {
        if (super.getGuiString() == null) {
            StringBuilder builder = new StringBuilder();
            for (IParamValueToken token : getTokens()) {
                builder.append(token.getGuiString());
            }
            
            if (builder.length() > 0) {
                setGuiString(builder.toString());
            } else {
                // If no tokens were generated, then either the model string
                // could not be successfully parsed or the model string is 
                // empty. Either way, use the model string in
                // order to show the current (possibly unparseable) state of the
                // parameter string.
                return getModelString();
            }
        }
        return super.getGuiString();
    }
    
    /**
     * @{inheritDoc}
     */
    void createTokens() {
        Parser parser = new Parser(new JubulaParameterLexer(new PushbackReader(
                new StringReader(StringUtils.defaultString(
                        getModelString())))));
        ParsedParameter parsedParam = 
            new ParsedParameter(false, getCurrentNode(), getDesc());
        try {
            parser.parse().apply(parsedParam);
            setTokens(parsedParam.getTokens());
        } catch (LexerException e) {
            createErrors(e, getModelString());
        } catch (ParserException e) {
            createErrors(e, getModelString());
        } catch (IOException e) {
            LOG.error(Messages.ParameterParsingErrorOccurred, e);
            createErrors(e, getModelString());
        } catch (SemanticParsingException e) {
            createErrors(e, getModelString());
        }
    }

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.ParamValueConverter#validateSingleTokens()
     */
    void validateSingleTokens() {
        for (IParamValueToken token : getTokens()) {
            ConvValidationState state = token.validate();
            if (state == ConvValidationState.invalid 
                || state == ConvValidationState.undecided) {
                TokenError tokenError = 
                    new TokenError(getModelString(), 
                        token.getErrorKey(), state);
                addError(tokenError);
            }
        }
        
    }

    /**
     * @param guid of associated parameter description
     * @return flag, if a deletion was done.
     */
    public boolean removeReference(String guid) {
        boolean isRefRemoved = false;
        List<IParamValueToken> tokensCopy = 
            new ArrayList<IParamValueToken>(getAllTokens());
        for (IParamValueToken token : tokensCopy) {
            if (token instanceof RefToken) {
                RefToken refToken = (RefToken)token;
                String refGuid = 
                    RefToken.extractCore(refToken.getModelString());
                if (refGuid.equals(guid)) {
                    getTokens().remove(token);
                    isRefRemoved = true;
                }                
            }
        }
        if (isRefRemoved) {
            updateStrings();
        }
        return isRefRemoved;        
    }
    
    
    
    /**
     * replaces an old guid with a new guid in all available RefTokens, which
     * contain a modelstring containing the given old guid
     * @param map key: old Guid, value: new Guid
     */
    public void replaceGuidsInReferences(Map<String, String> map) {
        List<RefToken> refTokens = getRefTokens();
        for (RefToken refToken : refTokens) {
            String oldGuid = RefToken.extractCore(refToken.getModelString());
            if (map.containsKey(oldGuid)) {
                String newGuid = map.get(oldGuid);
                refToken.setModelString(
                    RefToken.replaceCore(newGuid, refToken.getModelString()));
            }
        }
        updateModelString();
    }
    
    /**
     * updates the modelString after substitution of guids in references
     */
    private void updateModelString() {
        StringBuilder builder = new StringBuilder();
        for (IParamValueToken token : getTokens()) {
            builder.append(token.getModelString());
        }
        setModelString(builder.toString());
    }

    /**
     * updates model- and guiString after deletion of reference token
     */
    private void updateStrings() {
        StringBuilder builder = new StringBuilder();
        for (IParamValueToken token : getTokens()) {
            builder.append(token.getModelString());
        }
        setModelString(builder.toString());
        setGuiString(null);
    }
}
