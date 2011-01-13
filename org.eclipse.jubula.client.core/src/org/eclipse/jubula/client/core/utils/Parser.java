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
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;

import de.susebox.jtopas.Flags;
import de.susebox.jtopas.StandardTokenizer;
import de.susebox.jtopas.StandardTokenizerProperties;
import de.susebox.jtopas.StringSource;
import de.susebox.jtopas.Token;
import de.susebox.jtopas.Tokenizer;
import de.susebox.jtopas.TokenizerException;
import de.susebox.jtopas.TokenizerProperties;

/**
 * @author BREDEX GmbH
 * @created 17.08.2007
 */
public class Parser {

    /** symbol for reference */
    public static final char REF_SYMBOL = 
        TestDataConstants.REFERENCE_CHAR_DEFAULT;
    
    /** The reference regex */
    public static final String REF_REGEX = "[a-zA-Z_0-9]{1,}"; //$NON-NLS-1$
    
    /** symbol for variables */
    public static final char VAR_SYMBOL = 
        TestDataConstants.VARIABLE_CHAR_DEFAULT;

    /** symbol for opening curly brace */
    public static final char OPENING_BRACE_SYMBOL = '{';
    
    /** symbol for closing curly brace */
    public static final char CLOSING_BRACE_SYMBOL = '}';
    
    /** symbol for single quote */
    public static final char COMMENT_SYMBOL = 
        TestDataConstants.COMMENT_SYMBOL;
    
    /** escape symbol */
    public static final char ESCAPE_SYMBOL = '\\';

    /**
     * 
     *
     * @author BREDEX GmbH
     * @created 20.08.2007
     */
    static class ParserException extends Exception {
        /** value to parse */
        private String m_input = StringConstants.EMPTY;
        
        /** token to create */
        private String m_token = StringConstants.EMPTY;
        
        /** index of input in entire string */
        private int m_startPos = -1;
        
        /** length of input */
        private int m_length = -1;

        /** id for error message */
        private Integer m_messageId = null;
        /**
         * @param message for exception
         * @param cause reason for exception
         * @param messageId for I18N-Key of error message
         */
        public ParserException(String message, Throwable cause, 
            Integer messageId) {
            super(message, cause);
            m_messageId = messageId;
        }
        
        /**
         * @param message for exception
         * @param input parsed string
         * @param token created token
         * @param messageId for I18N-Key of error message
         */
        public ParserException(String message, String input, String token, 
            Integer messageId) {
            this(message, input, token, -1, -1, messageId);
        }
        
        /**
         * @param message for exception
         * @param input parsed string
         * @param token created token
         * @param startPos index of first character of input in entire string
         * @param length of input
         * @param messageId for I18N-Key of error message
         */
        public ParserException(String message, String input, String token,
            int startPos, int length, Integer messageId) {
            super(message);
            m_input = input;
            m_token = token;
            m_startPos = startPos;
            m_length = length;
            m_messageId = messageId;
        }

        /**
         * @return the input
         */
        public String getInput() {
            return m_input;
        }

        /**
         * @return the length
         */
        public int getLength() {
            return m_length;
        }

        /**
         * @return the startPos
         */
        public int getStartPos() {
            return m_startPos;
        }

        /**
         * @return the token
         */
        public String getToken() {
            return m_token;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            StringBuilder res = new StringBuilder(getMessage());
            res.append("Input="); //$NON-NLS-1$
            res.append(getInput());
            res.append(", token="); //$NON-NLS-1$
            res.append(getToken());
            res.append("\nstart@"); //$NON-NLS-1$
            res.append(getStartPos());
            res.append(", length="); //$NON-NLS-1$
            res.append(getLength());
            return res.toString();
        }

        /**
         * @return Returns the messageId.
         */
        Integer getMessageId() {
            return m_messageId;
        }
    }

   /** token created from Parser */
    private interface ICreateToken {
        /**
         * @param s string to create our own token
         * @return our representation of a paramValueToken
         * @throws ParserException
         */
        IParamValueToken create(String s) throws ParserException;
        
        /**
         * @param pos start index of string, representing this token
         */
        void setStartPos(int pos);
    }

    /**
     * token for special tokens without using of curly braces,
     * e.g. "=REF" or "$VAR"
     */
    private class CreateSpecialToken implements ICreateToken {
        
        /** object to manage tokens */
        private Tokenizer m_tokenizer;

        /** token createds the ParamValueToken */
        private ICreateToken m_creator;
        
        /** start index of token */
        private int m_startPos;

        
        /**
         * @param tokenizer object to manage tokens
         * @param creator first created token
         */
        public CreateSpecialToken(Tokenizer tokenizer, ICreateToken creator) {
            m_tokenizer = tokenizer;
            m_creator = creator;
        }

        /**
         * {@inheritDoc}
         */
        public IParamValueToken create(String s) throws ParserException {
            if (!m_tokenizer.hasMoreToken()) {
                throw new ParserException("Premature end of input", //$NON-NLS-1$
                    m_input, s, MessageIDs.E_ONE_CHAR_PARSE_ERROR); 
            }
            try {
                Token val = m_tokenizer.nextToken();
                if (val.getType() == Token.EOF) {
                    throw new ParserException("Premature end of input", //$NON-NLS-1$
                        m_input, s, MessageIDs.E_ONE_CHAR_PARSE_ERROR);
                }
                String str = val.getImage();
                int nameEnd = findNameEnd(str);
                if (nameEnd == 0) {
                    throw new ParserException("Malformed name", m_input, s, //$NON-NLS-1$
                        MessageIDs.E_PARSE_NAME_ERROR); 
                }
                String nameStr = str.substring(0, nameEnd);
                int valPos = str.length() - nameEnd;
                if (valPos > 0) {
                    m_tokenizer.setReadPositionRelative(-valPos);
                }
                return m_creator.create(s + nameStr);
            } catch (TokenizerException e) {
                throw new ParserException("Tokenizer error", e, //$NON-NLS-1$
                    MessageIDs.E_GENERAL_PARSE_ERROR); 
            }
        }
        
        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#setStartPos(int)
         */
        public void setStartPos(int pos) {
            m_startPos = pos;
            
        }

        /**
         * @param str string to validate
         * @return end position of variable or reference name
         */
        private int findNameEnd(String str) {
            int len = str.length();
            for (int i = 0; i < len; ++i) {
                char ch = str.charAt(i);
                if (!(Character.isLetterOrDigit(ch) || ch == '_')) {
                    return i;
                }
            }
            return len;
        }

        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#getStartPos()
         */
        public int getStartPos() {
            return m_startPos;
        }

       
    }

    /**
     * token class for references
     */
    private class CreateRefToken implements ICreateToken {
        
        /** start index of token */
        private int m_startPos;

        /**
         * {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#create(java.lang.String)
         */
        public IParamValueToken create(String s) throws ParserException {
            validateToken(s, REF_SYMBOL);
            return new RefToken(s, m_areTokensGuiBased, m_startPos, 
                m_currentNode, m_desc); 
        }

       

        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#setStartPos(int)
         */
        public void setStartPos(int pos) {
            m_startPos = pos;
        }
    }

    /** token class for variables */
    private class CreateVarToken implements ICreateToken {
        
        /** start index of token */
        private int m_startPos;

        /**
         * {@inheritDoc}
         */
        public IParamValueToken create(String s) throws ParserException {
            validateToken(s, VAR_SYMBOL);
            return new VariableToken(s, m_startPos, m_desc);
        }


        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#setStartPos(int)
         */
        public void setStartPos(int pos) {
            m_startPos = pos;
        }
    }

    /** token class for simple values */
    private class CreateValueToken implements ICreateToken {
        
        /** start index of token */
        private int m_startPos;

        /**
         * {@inheritDoc}
         */
        public IParamValueToken create(String s) {
            return new SimpleValueToken(s, m_startPos, m_desc);
        }
        

        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#setStartPos(int)
         */
        public void setStartPos(int pos) {
            m_startPos = pos;
        }
    }

    /** token class for using strings with single quotes
     *  a string contains only two single quotes without a value between (''), 
     *  means, that the user not set a value
     *  in terms of completeness check is the parameter complete
     * 
     */
    
    private class CreateLiteralToken implements ICreateToken {
        
        /** start index of token */
        private int m_startPos;

        /**
         * {@inheritDoc}
         */
        public IParamValueToken create(String s) throws ParserException {
            if (s.length() < 2 || !(s.charAt(0) == COMMENT_SYMBOL)
                || !(s.charAt(s.length() - 1) == COMMENT_SYMBOL)) {
                throw new ParserException("Quote mismatch",  //$NON-NLS-1$
                    m_input, s, MessageIDs.E_INCOMPL_QUOTES);
            }
            return new LiteralToken(s, m_startPos);
        }
        
        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#setStartPos(int)
         */
        public void setStartPos(int pos) {
            m_startPos = pos;
            
        }
        
        
    }
    /** token class to handle escape symbols */
    private class CreateEscapeToken implements ICreateToken {
        
        /** start index of token */
        private int m_startPos;

        /**
         * {@inheritDoc}
         */
        public IParamValueToken create(String s) throws ParserException {
            if (!(s.charAt(0) == ESCAPE_SYMBOL) || s.length() != 2) {
                throw new ParserException("Unsupported escape sequence", //$NON-NLS-1$
                    m_input, s, MessageIDs.E_GENERAL_PARSE_ERROR);
            }
            return new CreateValueToken().create(s);
        }
        
        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#setStartPos(int)
         */
        public void setStartPos(int pos) {
            m_startPos = pos;
            
        }

        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.core.utils.Parser.ICreateToken#getStartPos()
         */
        public int getStartPos() {
            return m_startPos;
        }
    }
    
    /** string to tokenize */
    private String m_input;
    
    /** tokens created from tokenizer */
    private List<IParamValueToken> m_tokens;

    /** node using parameter with input as parameter value */
    private IParameterInterfacePO m_currentNode;

    /** flag for differentiation between token creation based on string in gui- or
     * model representation
     */
    private boolean m_areTokensGuiBased = true;

    /** desc parameter description associated with currently parsed string */
    private IParamDescriptionPO m_desc;
    
    /**
     * use this constructor to parse a string in gui representation
     * @param s string to parse
     * @param isGuiString flag for distinction between guiString and modelString
     * @param paramNode paramNode using parameter with input as parameter value
     * @param desc parameter description associated with currently parsed string
     * @throws ParserException in case of any syntax error
     */
    public Parser(String s, boolean isGuiString, 
            IParameterInterfacePO paramNode, IParamDescriptionPO desc) 
        throws ParserException {
        if (s == null) {
            m_input = StringConstants.EMPTY;
        } else {
            m_input = s;
        }
        m_areTokensGuiBased = isGuiString;
        m_currentNode = paramNode;
        m_desc = desc;
        m_tokens = new ArrayList<IParamValueToken>();
        tokenize(m_input);
    }


    /**
     * @param s string to parse
     */
    private void tokenize(String s) throws ParserException {
        Tokenizer tokenizer = createTokenizer(s);
        Token token;

        while (tokenizer.hasMoreToken()) {
            try {
                token = tokenizer.nextToken();
            } catch (TokenizerException e) {
                throw new ParserException("Tokenizer error", e, //$NON-NLS-1$
                    MessageIDs.E_GENERAL_PARSE_ERROR);
            }
            if (token.getType() != Token.EOF) {
                ICreateToken creator = (ICreateToken)token.getCompanion();
                if (creator == null) {
                    // Text token can't have a companion, therefor one must
                    // create one.
                    creator = new CreateValueToken();
                }
                creator.setStartPos(token.getStartPosition());
                try {
                    m_tokens.add(creator.create(token.getImage()));
                } catch (ParserException e) {
                    throw new ParserException(e.getMessage(), e.getInput(),
                        e.getToken(), token.getStartPosition(), 
                        token.getLength(), e.getMessageId());
                }
            }
        }
    }

    /**
     * @param s string to parse
     * @return tokenizer
     */
    private Tokenizer createTokenizer(String s) {
        TokenizerProperties props = new StandardTokenizerProperties();
        Tokenizer tokenizer = new StandardTokenizer();

        props.setParseFlags(Flags.F_CASE | Flags.F_RETURN_BLOCK_COMMENTS
            | Flags.F_RETURN_WHITESPACES | Flags.F_SINGLE_LINE_STRING);
        props.setSeparators(null);
        props
            .addSpecialSequence(escSeq(ESCAPE_SYMBOL), new CreateEscapeToken());
        props.addSpecialSequence(escSeq(REF_SYMBOL), new CreateEscapeToken());
        props.addSpecialSequence(escSeq(VAR_SYMBOL), new CreateEscapeToken());
        props.addSpecialSequence(escSeq(OPENING_BRACE_SYMBOL),
            new CreateEscapeToken());
        props.addSpecialSequence(escSeq(CLOSING_BRACE_SYMBOL),
            new CreateEscapeToken());
        props.addSpecialSequence(escSeq(COMMENT_SYMBOL),
            new CreateEscapeToken());

        props.addSpecialSequence(String.valueOf(REF_SYMBOL),
            new CreateSpecialToken(tokenizer,
                new CreateRefToken()));
        props.addBlockComment(charSeq(REF_SYMBOL, OPENING_BRACE_SYMBOL),
            String.valueOf(CLOSING_BRACE_SYMBOL), 
            new CreateRefToken());

        props.addSpecialSequence(String.valueOf(VAR_SYMBOL),
            new CreateSpecialToken(tokenizer,
                new CreateVarToken()));
        props.addBlockComment(charSeq(VAR_SYMBOL, OPENING_BRACE_SYMBOL),
            String.valueOf(CLOSING_BRACE_SYMBOL), 
            new CreateVarToken());

        props.addString(String.valueOf(COMMENT_SYMBOL),
            String.valueOf(COMMENT_SYMBOL), 
            String.valueOf(ESCAPE_SYMBOL), 
            new CreateLiteralToken());
        tokenizer.setTokenizerProperties(props);
        tokenizer.setSource(new StringSource(s));
        return tokenizer;
    }

    /**
     * @param ch character to escape
     * @return escape sequence for character
     */
    private static String escSeq(char ch) {
        return String.valueOf(ESCAPE_SYMBOL) + String.valueOf(ch);
    }
    
    /**
     * @param chars characters to concatenate
     * @return concatenated characters
     */
    private String charSeq(char... chars) {
        StringBuilder sb = new StringBuilder(chars.length);
        for (char c : chars) {
            sb.append(c);
        }
        return sb.toString();
    }
    

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(m_input);
        sb.append(" tokenizes as\n"); //$NON-NLS-1$
        for (IParamValueToken token : m_tokens) {
            sb.append("\t"); //$NON-NLS-1$
            sb.append(token.getGuiString());
            sb.append("\n"); //$NON-NLS-1$
        }
        return sb.toString();
    }

    /**
     * @return Returns the tokens.
     */
    List<IParamValueToken> getTokens() {
        return m_tokens;
    }


    /**
     * @param symbol start character for string to validate (e.g. "=" for a reference name) 
     * @param s string to validate
     * @throws ParserException in case of any syntax error in string
     */
    private void validateToken(String s, char symbol) throws ParserException {
        StringBuilder builder = new StringBuilder(s);
        if (!(!(s.length() == 0) && s.charAt(0) == symbol)) {
            throw new ParserException("Internal error", m_input, s, //$NON-NLS-1$
                MessageIDs.E_GENERAL_PARSE_ERROR);
        }
        builder.deleteCharAt(0);
        if (s.length() > 1 && s.charAt(1) == OPENING_BRACE_SYMBOL) {
            builder.deleteCharAt(0);
            if (s.length() > 2) {
                if (s.charAt(s.length() - 1) != CLOSING_BRACE_SYMBOL) {
                    parseName(s, builder);
                    throw new ParserException("Missing closing brace",  //$NON-NLS-1$
                        m_input, s, MessageIDs.E_MISSING_CLOSING_BRACE);
                } 
                builder.deleteCharAt(builder.length() - 1);
                if (builder.toString().length() == 0) {
                    throw new ParserException("Missing content between braces.", //$NON-NLS-1$
                        m_input, s, MessageIDs.E_MISSING_CONTENT);
                }
            }
        }
        if (builder.toString().length() == 0) {
            throw new ParserException("Missing closing brace",  //$NON-NLS-1$
                m_input, s, MessageIDs.E_MISSING_CLOSING_BRACE);
        }
        parseName(s, builder);
    }
    
    /**
     * @param s complete string
     * @param builder builder with string for pure name
     * @throws ParserException in case of parser error
     */
    private void parseName(String s, StringBuilder builder) 
        throws ParserException {
        final String wordRegex = REF_REGEX;
        if (!Pattern.matches(wordRegex, builder.toString())) {
            throw new ParserException("Name contains unsupported characters", //$NON-NLS-1$
                m_input, s, MessageIDs.E_PARSE_NAME_ERROR);
        }
    }
}
