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
package org.eclipse.jubula.client.core.businessprocess.compcheck;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.CompNameResult;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.LogicComponentNotManagedException;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.ConcreteComponent;


/**
 * @author BREDEX GmbH
 * @created 10.05.2005
 */
@SuppressWarnings("synthetic-access")
public final class CompletenessGuard {
    /**
     * Operation to set the CompleteSpecTc flag for a node
     */
    private static class CheckMissingTestCaseReferences 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTc = (IExecTestCasePO)node;
                boolean isMissingSpecTc = execTc.getSpecTestCase() == null;
                execTc.setCompletenessMissingTestCase(!isMissingSpecTc);
            }
            return !alreadyVisited;
        }
    }
    
    /**
     * Operation to reflect the activity status of a node
     */
    private static class InactiveNodesOperation 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (!node.isActive()) {
                Set<IProblem> problemsToRemove = 
                        new HashSet<IProblem>(node.getProblems());
                for (IProblem problem : problemsToRemove) {
                    node.removeProblem(problem);
                }
            }
            return true;
        }
    }

    /**
     * Updates the flags for the object mapping (complete or not?)
     * in all nodes down to the test step.
     */
    private static class CheckObjectMappingCompleteness 
            implements ITreeNodeOperation<INodePO> {
        /**
         * The business process that performs component name operations.
         */
        private final CompNamesBP m_compNamesBP = new CompNamesBP();
        /**
         * The AUT
         */
        private IAUTMainPO m_aut;
        
        /**
         * Operates on the test step by setting its object mapping flag. The
         * node that is responsible for a component name is also updated.
         * 
         * @param ctx
         *            The tree traverser context
         * @param cap
         *            The test step
         * @return <code>true</code> if the component name of the test step is
         *         mapped, <code>false</code> otherwise
         */
        private boolean handleCap(ITreeTraverserContext<INodePO> ctx, 
                ICapPO cap) {
            IObjectMappingPO objMap = m_aut.getObjMap();
            String compName = cap.getComponentName();
            IComponentNamePO compNamePo = 
                ComponentNamesBP.getInstance().getCompNamePo(compName);
            if (compNamePo != null) {
                compName = compNamePo.getGuid();
            }
            
            final Component metaComponentType = cap.getMetaComponentType();
            if (metaComponentType instanceof ConcreteComponent
                    && ((ConcreteComponent)metaComponentType)
                        .hasDefaultMapping()) {
                
                cap.setCompletenessObjectMapping(m_aut, true);
                return true;
            }
            
            if (compName != null && objMap != null) {
                IComponentIdentifier id = null;
                CompNameResult result = m_compNamesBP.findCompName(
                    ctx.getCurrentTreePath(), cap, cap.getComponentName(),
                    ComponentNamesBP.getInstance());
                try {
                    id = objMap.getTechnicalName(result.getCompName());
                } catch (LogicComponentNotManagedException e) {
                    // ok
                }
                if (id == null) {
                    result.getResponsibleNode()
                        .setCompletenessObjectMapping(m_aut, false);
                    if (result.getResponsibleNode() == cap) {
                        cap.setCompletenessObjectMapping(m_aut, false);
                    }
                } else {
                    cap.setCompletenessObjectMapping(m_aut, true);
                }
            }
            
            return cap.hasCompleteObjectMapping(m_aut);
        }
        /**
         * Sets the object mapping flag of the passed node to <code>true</code>
         * 
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (node instanceof ITestSuitePO) {
                ITestSuitePO ts = (ITestSuitePO)node;
                m_aut = ts.getAut();
            }
            if (m_aut != null) {
                node.setCompletenessObjectMapping(m_aut, true);
            }
            return true;
        }
        /**
         * Updates the object mapping flags of the passed node and its parent.
         * 
         * {@inheritDoc}
         */
        public void postOperate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {
            
            if (m_aut != null) {
                if (node instanceof ICapPO) {
                    handleCap(ctx, (ICapPO)node);
                }
            }
        }
    }
    
    /**
     * Operation to set the CompleteTestData flag at TDManager.
     * @author BREDEX GmbH
     * @created 10.10.2005
     */
    private static class CheckTestDataCompleteness 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** The Locale to check */
        private Locale m_locale;
        
        /**
         * @param loc The Locale to check
         */
        public CheckTestDataCompleteness(Locale loc) {
            m_locale = loc;
        }
        
        /**
         * Sets the CompleteTDFlag in all ParamNodePOs.
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (node instanceof IParamNodePO) {
                IParamNodePO paramNode = (IParamNodePO)node;
                if (node instanceof IExecTestCasePO) {
                    IExecTestCasePO execTc = (IExecTestCasePO)node;
                    if (execTc.getHasReferencedTD()) {
                        paramNode = execTc.getSpecTestCase();
                    }
                }
                
                if (paramNode != null) { 
                    // FIXME Andreas : If entering a new Parameter into a step, a parameter is
                    // created at parent TC. But there is no Row created. Guard should
                    // check if there is missing data or Row(0) have to be created

                    boolean isComplete = paramNode.isTestDataComplete(m_locale);
                    if (node instanceof IExecTestCasePO) {
                        CompletenessGuard.setCompFlagForTD((IParamNodePO) node,
                                m_locale, isComplete);
                    } else {
                        CompletenessGuard.setCompFlagForTD(paramNode, m_locale,
                                isComplete);
                    }
                }
            }
            return !alreadyVisited;
        }
    }
    
    /**
     * private constructor
     */
    private CompletenessGuard() {
    // nothing
    }

    /**
     * method to label the testdata completeness for a node
     * 
     * @param node
     *            node, for which the complete flag is to set
     * @param loc
     *            language, for which the complete flag is valid
     * @param hasCompleteTD
     *            logical value for completeness of testdata
     */
    public static void setCompFlagForTD(IParamNodePO node, Locale loc,
        boolean hasCompleteTD) {
        node.setCompletenessTestData(loc, hasCompleteTD);
    }

    /**
     * checks OM and TD Completness of all TS
     * @param loc the Locale to check
     * @param root INodePO
     */
    public static void checkAll(Locale loc, INodePO root) {
        // Iterate Execution tree
        final TreeTraverser traverser = new TreeTraverser(root);
        traverser.addOperation(new CheckObjectMappingCompleteness());
        traverser.addOperation(new CheckTestDataCompleteness(loc));
        traverser.addOperation(new CheckMissingTestCaseReferences());
        traverser.addOperation(new InactiveNodesOperation());
        traverser.traverse(true);
    }

    /**
     * checks TD Completness of all TS
     * @param loc the Locale to check
     * @param root
     *      INodePO
     */
    public static void checkTestData(Locale loc, INodePO root) {
        // Iterate Execution tree
        new TreeTraverser(root, new CheckTestDataCompleteness(loc))
                .traverse(true);
    }

    /**
     * checks OM Completness of all TS
     * @param root
     *      INodePO
     */
    public static void checkObjectMappings(INodePO root) {
        // Iterate Execution tree
        new TreeTraverser(root, new CheckObjectMappingCompleteness())
                .traverse(true);
    }
}
