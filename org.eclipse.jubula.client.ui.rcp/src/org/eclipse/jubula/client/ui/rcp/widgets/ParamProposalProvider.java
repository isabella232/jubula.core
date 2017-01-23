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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;


/**
 * Provides proposals for parameter values based on parameter type and 
 * editing context.
 *
 * @author BREDEX GmbH
 * @created Apr 7, 2010
 */
public class ParamProposalProvider implements IContentProposalProvider {
    
    /** prefix for parameter reference content proposals */
    private static final String PARAMETER_PREFIX = "="; //$NON-NLS-1$
    
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

    /** delegate provider for Function proposals */
    private FunctionProposalProvider m_functionProposalProvider =
            new FunctionProposalProvider();
    
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

        IContentProposal[] functionProposals = 
                m_functionProposalProvider.getProposals(contents, position);
        if (!ArrayUtils.isEmpty(functionProposals)) {
            return functionProposals;
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
        if (node == null) {
            return proposals;
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
                    String parType = m_paramDesc.getType();
                    if ("java.lang.String".equals(parType) //$NON-NLS-1$
                            || param.getType().equals(parType)) {
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

}
