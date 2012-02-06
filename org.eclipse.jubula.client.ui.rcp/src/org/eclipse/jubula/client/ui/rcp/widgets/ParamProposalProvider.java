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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jubula.client.core.functions.FunctionDefinition;
import org.eclipse.jubula.client.core.functions.FunctionRegistry;
import org.eclipse.jubula.client.core.functions.ParameterDefinition;
import org.eclipse.jubula.client.core.functions.VarArgsDefinition;
import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.LexerException;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TBeginFunctionArgsToken;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TEndFunctionArgsToken;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.parser.parameter.FunctionLocator;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides proposals for parameter values based on parameter type and 
 * editing context.
 *
 * @author BREDEX GmbH
 * @created Apr 7, 2010
 */
public class ParamProposalProvider implements IContentProposalProvider {
    
    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(ParamProposalProvider.class);

    /** separator for Function arguments in content proposals */
    private static final String ARG_SEPARATOR = ","; //$NON-NLS-1$
    
    /** prefix for parameter reference content proposals */
    private static final String PARAMETER_PREFIX = "="; //$NON-NLS-1$
    
    /** the base name to use for vararg content proposals */
    private static final String BASE_VARARG_NAME = "varArg"; //$NON-NLS-1$
    
    /**
     * @author BREDEX GmbH
     * @created Feb 5, 2009
     */
    public static class ParamProposal implements IContentProposal {

        /** display value */
        private String m_displayValue;

        /** the content that will be inserted if this proposal is selected */
        private String m_content;
        
        /**
         * Constructor
         * 
         * @param content The content of the proposal.
         */
        public ParamProposal(String content) {
            this(content, null);
        }

        /**
         * Constructor
         * 
         * @param content The content of the proposal.
         * @param displayValue The label for the proposal.
         */
        public ParamProposal(String content, String displayValue) {
            m_content = content;
            m_displayValue = displayValue;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getContent() {
            return m_content;
        }

        /**
         * {@inheritDoc}
         */
        public int getCursorPosition() {
            return m_content.length();
        }

        /**
         * {@inheritDoc}
         */
        public String getDescription() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return m_displayValue;
        }

    }

    /** fixed values */
    private String[] m_valueSet;
    
    /** the node for which to provide proposals */
    private INodePO m_node;
    
    /** the param description for which to provide proposals */
    private IParamDescriptionPO m_paramDesc;
    
    /**
     * Constructor
     * 
     * @param valueSet Fixed values to propose (for example, "true" and "false"
     *                 for a boolean parameter).
     * @param node The node to use as a base for dynamically generating 
     *             proposals.
     * @param paramDesc The param description to use as a base for dynamically 
     *                  generating proposals.
     */
    public ParamProposalProvider(String[] valueSet, INodePO node, 
            IParamDescriptionPO paramDesc) {
        m_valueSet = valueSet;
        m_node = node;
        m_paramDesc = paramDesc;
    }
    /**
     * {@inheritDoc}
     */
    public IContentProposal[] getProposals(String contents, int position) {

        // handle content proposal for functions, if necessary
        String proposalSubstring = contents.substring(0, position);
        try {
            FunctionLocator locator = new FunctionLocator(proposalSubstring);
            String startingFunctionText = locator.getCurrentFunction();
            if (startingFunctionText != null) {
                // if the user is currently entering a function, only 
                // function-related content proposals are interesting, so just 
                // return from here
                return getProposalsForFunction(startingFunctionText);
            }
        } catch (LexerException e) {
            LOG.warn(NLS.bind(Messages.ParamProposal_ParsingError, 
                    proposalSubstring), e);
        } catch (IOException e) {
            LOG.warn(NLS.bind(Messages.ParamProposal_ParsingError, 
                    proposalSubstring), e);
        }
        
        
        if (position != contents.length()) { // no proposals when in text
            return new IContentProposal[0];
        }

        List<IContentProposal> proposals = 
            new ArrayList<IContentProposal>(20);

        
        
        // if there are predefined values offer them first
        if (m_valueSet != null) {
            proposals.addAll(getValueSetProposals(contents, position));
        }

        proposals.addAll(getParentParamProposals(contents));
        
        return proposals.toArray(new IContentProposal[proposals.size()]);
    }
 
    /**
     * Creates and returns content proposals based on the Parameters available
     * from the parent node.
     * 
     * @param contents The text for which to generate content proposals.
     * @return the proposals for the given arguments.
     */
    private Collection<IContentProposal> getParentParamProposals(
            String contents) {

        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        
        // find a SpecTestCase
        INodePO node = m_node;
        while ((node != null) && !(node instanceof ISpecTestCasePO)) {
            node = node.getParentNode();
        }
        ISpecTestCasePO paramNode = (ISpecTestCasePO)node;
        
        if (!(m_node instanceof ISpecTestCasePO)) {
            // add the parameter name as a suggestion
            if (m_paramDesc != null && paramNode != null) {
                if (!paramNode.isInterfaceLocked()
                        && !paramNode.getParamNames().contains(
                                m_paramDesc.getName())) {
                    String p = PARAMETER_PREFIX + m_paramDesc.getName();
                    p = StringUtils.replaceChars(p, ' ', '_');
                    p = StringUtils.replaceChars(p, '-', '_');
                    p = p.toUpperCase();
                    if (p.startsWith(contents)) {
                        proposals.add(new ParamProposal(
                                p.substring(contents.length()), p));
                    }
                }
            }

            if (paramNode != null) {
                List<IParamDescriptionPO> params = 
                    paramNode.getParameterList();
                for (IParamDescriptionPO param : params) {
                    if (param.getType().equals(m_paramDesc.getType())) {
                        String p = PARAMETER_PREFIX + param.getName();
                        if (p.startsWith(contents)) {
                            proposals.add(new ParamProposal(
                                    p.substring(contents.length()), p));
                        }
                    }
                }

            }
        }

        return proposals;
    }

    /**
     * 
     * @param contents The text for which to generate content proposals.
     * @param position The current position in the text for which to generate 
     *                 proposals.
     * @return the proposals for the given arguments.
     */
    private Collection<IContentProposal> getValueSetProposals(
            String contents, int position) {

        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        StringBuilder sb = new StringBuilder(contents);
        
        sb.delete(position, sb.length());
        sb.delete(0, 
            sb.lastIndexOf(TestDataConstants.COMBI_VALUE_SEPARATOR) + 1);
        for (String predefValue : m_valueSet) {
            if (predefValue.startsWith(sb.toString())) {
                proposals.add(new ParamProposal(
                        predefValue.substring(sb.length()), predefValue));
            } else if (predefValue.startsWith(contents)) {
                proposals.add(new ParamProposal(
                        predefValue.substring(
                                contents.length()), predefValue));
            }
        }

        return proposals;
    }

    /**
     * 
     * @param startingFunctionText The text for which to generate content 
     *                             proposals.
     * @return the proposals for the given arguments.
     */
    private IContentProposal[] getProposalsForFunction(
            String startingFunctionText) {

        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        for (FunctionDefinition function 
                : FunctionRegistry.getInstance().getAllFunctions()) {
            
            if (function.getName().startsWith(startingFunctionText)) {
                StringBuilder displayBuilder = new StringBuilder();
                
                displayBuilder.append(function.getName())
                    .append(new TBeginFunctionArgsToken().getText());

                ParameterDefinition[] parameters = function.getParameters();
                List<String> parameterNames = new ArrayList<String>();
                for (ParameterDefinition param : parameters) {
                    parameterNames.add(param.getName());
                }

                VarArgsDefinition varArgs = function.getVarArgs();
                if (varArgs != null) {
                    for (int i = 0; i < varArgs.getDefaultNumberOfArgs(); i++) {
                        StringBuilder varArgNameBuilder = 
                                new StringBuilder(BASE_VARARG_NAME);
                        varArgNameBuilder.append(i + 1);
                        parameterNames.add(varArgNameBuilder.toString());
                    }
                }
                
                displayBuilder.append(StringUtils.join(
                        parameterNames, ARG_SEPARATOR));
                
                displayBuilder.append(new TEndFunctionArgsToken().getText());
                String displayString = displayBuilder.toString();
                proposals.add(new ParamProposal(
                        displayString.substring(
                                startingFunctionText.length()),
                        displayString));
            }

        }

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }
    
}
