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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.Lexer;
import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.LexerException;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.Parser;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.ParserException;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 31.10.2007
 */
public class GuiParamValueConverter extends ParamValueConverter {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(GuiParamValueConverter.class);
    
    /**
     * hint: the string could be null.
     * @param guiString to convert
     * @param currentNode node with parameter for this parameterValue
     * @param locale current used language
     * @param desc param description associated with current string (parameter value)
     * @param validator to use for special validations
     */
    public GuiParamValueConverter(String guiString,
            IParameterInterfacePO currentNode, Locale locale,
            IParamDescriptionPO desc, IParamValueValidator validator) {
        super(currentNode, locale, desc, validator);
        init(guiString);
    }
    
    /**
     * default constructor
     */
    protected GuiParamValueConverter() {
        // do nothing
    }

    /**
     * @param guiString to convert
     */
    protected void init(String guiString) {
        setGuiString(guiString);
        createTokens();
    }
    
    /** create tokens from gui string */
    void createTokens() {
        Parser parser = new Parser(new Lexer(new PushbackReader(
                new StringReader(StringUtils.defaultString(getGuiString())))));
        ParsedParameter parsedParam = 
            new ParsedParameter(true, getCurrentNode(), getDesc());
        try {
            parser.parse().apply(parsedParam);
            setTokens(parsedParam.getTokens());
        } catch (LexerException e) {
            createErrors(e, getGuiString());
        } catch (ParserException e) {
            createErrors(e, getGuiString());
        } catch (IOException e) {
            LOG.error(Messages.ParameterParsingErrorOccurred, e);
            createErrors(e, getGuiString());
        } catch (SemanticParsingException e) {
            createErrors(e, getGuiString());
        }
    }
    
    /**
     * 
     */
    void validateSingleTokens() {
        // validates each token
        for (IParamValueToken token : getTokens()) {
            ConvValidationState state = token.validate();
            createTokenError(state, token);
        }
        // validates whole expression
        if (!containsErrors()) {
            ConvValidationState state = 
                getValidator().validateInput(getTokens());
            for (IParamValueToken token : getTokens()) {
                Integer errorKey = token.getErrorKey();
                if (errorKey != null && state == ConvValidationState.invalid) {
                    createTokenError(state, token);
                    return;
                } else if (state == ConvValidationState.undecided) {
                    createTokenError(state, token);
                }
            } 
        }
    }
    

    /**
     * @param parent parent
     * @return list of parameter names to add
     */
    public Set<String> getParametersToAdd(ISpecTestCasePO parent) {
        List<String> newRefs = getNamesForReferences();
        // remove multiple entries
        Set<String> refs = new HashSet<String>(newRefs);
        if (!refs.isEmpty()) {
            for (IParamDescriptionPO desc : parent.getParameterList()) {
                refs.remove(desc.getName());
            }
        }
        return refs;
    }
    
    /**
     * @return model representation of string
     */
    public String getModelString() {
        // replace reference names with GUIDs
        if (super.getModelString() == null) {
            StringBuilder builder = new StringBuilder();
            for (IParamValueToken token : getTokens()) {
                String modelString = token.getModelString();
                if (modelString == null) {
                    return null;
                }
                builder.append(token.getModelString());
            }
            setModelString(builder.toString());
        }
        return super.getModelString();
    }
}
