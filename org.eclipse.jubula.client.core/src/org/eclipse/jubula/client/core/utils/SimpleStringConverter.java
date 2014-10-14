/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
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
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.LexerException;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.Parser;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.ParserException;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.parser.parameter.JubulaParameterLexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates tokens with our self defined sablecc lexer. There is only validation
 * if the String could be parsed into tokens. 
 * <br>
 * <b>This should only be used if you want to work with the tokens!</b>
 * 
 * @author BREDEX GmbH
 */
public class SimpleStringConverter extends ParamValueConverter {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(SimpleStringConverter.class);

    /**
     * actual state
     */
    private ConvValidationState m_actualState;

    /**
     * hint: the string could be null.
     * 
     * @param guiString
     *            to convert
     */
    public SimpleStringConverter(String guiString) {
        init(guiString);
    }

    /**
     * Returns the actual state
     * 
     * @return actualState
     */
    public ConvValidationState getState() {
        return m_actualState;
    }

    /**
     * @param guiString
     *            to convert
     */
    protected void init(String guiString) {
        setGuiString(guiString);
        createTokens();
    }

    /** create tokens from gui string */
    void createTokens() {
        Parser parser = new Parser(new JubulaParameterLexer(new PushbackReader(
                new StringReader(StringUtils.defaultString(getGuiString())))));
        ParsedParameter parsedParam = new ParsedParameter(true, null, null);
        try {
            parser.parse().apply(parsedParam);
            List<IParamValueToken> liste = parsedParam.getTokens();
            setTokens(liste);
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
     * Controls the error list.
     * 
     * @return The state of the errors
     */
    public ConvValidationState getErrorStatus() {
        ConvValidationState val = ConvValidationState.valid;
        List<TokenError> errors = getErrors();
        if (errors.isEmpty()) {
            return val;
        }
        for (TokenError error : errors) {
            if (error.getValidationState() == ConvValidationState.invalid) {
                return ConvValidationState.invalid;
            } else if (error.getValidationState() 
                    == ConvValidationState.undecided) {
                val = ConvValidationState.undecided;
            }
        }
        return val;
    }

    /**
     * No validation of the tokens is done
     */
    public void validateSingleTokens() {
        // this class only generates tokens
    }

    /**
     * @param stack
     *            ignored
     * @param locale
     *            ignored
     * @return <code>null</code>
     */
    public String getExecutionString(List<ExecObject> stack, Locale locale) {
        return null;
    }
}
