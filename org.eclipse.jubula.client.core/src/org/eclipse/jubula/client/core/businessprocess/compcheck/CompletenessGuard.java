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
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.LogicComponentNotManagedException;
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
    private static class MissingTestCaseOperation 
            implements ITreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTc = (IExecTestCasePO)node;
                boolean isMissingSpecTc = execTc.getSpecTestCase() == null;
                execTc.setSumSpecTcFlag(!isMissingSpecTc);
                execTc.setCompleteSpecTcFlag(!isMissingSpecTc);
            } else {
                node.setSumSpecTcFlag(true);
            }
            return !alreadyVisited;
        }

        /** {@inheritDoc} */
        public void postOperate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (parent instanceof ICategoryPO) {
                parent.setSumSpecTcFlag(true);
            } else if (parent != null && parent.getSumSpecTcFlag()) {
                parent.setSumSpecTcFlag(node.getSumSpecTcFlag());
            }
        }
    }
    
    /**
     * Operation to reflect the activity status of a node
     */
    private static class InactiveNodesOperation 
            implements ITreeNodeOperation<INodePO> {

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

        /** {@inheritDoc} */
        public void postOperate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            // currently empty
        }
    }

    /**
     * Updates the flags for the object mapping (complete or not?)
     * in all nodes down to the test step.
     */
    private static class CompleteObjectMappingOperation 
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
         * The flag of complete object mapping
         */
        private boolean m_sumOMFlag = true;
        
        /**
         * Operates on the test step by setting its object mapping flag. The
         * node that is responsible for a component name is also updated.
         * 
         * {@inheritDoc}
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
                
                cap.setCompleteOMFlag(m_aut, true);
                return true;
            }
            
            if (compName != null && objMap != null) {
                IComponentIdentifier id = null;
                CompNameResult result = m_compNamesBP.findCompName(
                    ctx.getCurrentTreePath(), cap, cap.getComponentName(),
                    ComponentNamesBP.getInstance());
                try {
                    id = objMap.getTechnicalName(result.getCompName());
                } catch (LogicComponentNotManagedException e) { // NOPMD by al on 3/19/07 1:21 PM
                    // ok
                }
                if (id == null) {
                    result.getResponsibleNode().setSumOMFlag(m_aut, false);
                    if (result.getResponsibleNode() == cap) {
                        cap.setCompleteOMFlag(m_aut, false);
                    }
                } else {
                    cap.setCompleteOMFlag(m_aut, true);
                }
            }
            
            return cap.getCompleteOMFlag(m_aut);
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
                node.setSumOMFlag(m_aut, true);
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
                    m_sumOMFlag = handleCap(ctx, (ICapPO)node);
                } else if (node instanceof ICategoryPO 
                        || node instanceof ITestJobPO) {
                    node.setSumOMFlag(m_aut, true);
                // Only set the OM flag if it's possible that the node could 
                // actually *have* incomplete mappings (i.e. if a node is not a 
                // Test Step and has no children, then it can't have mappings 
                // and therefore can't have incomplete mappings). Without this 
                // check, an empty Test Case could be marked as OM Incomplete if
                // a "nearby" Test Case is OM Incomplete.
                } else if (node.getNodeListSize() > 0) {  
                    m_sumOMFlag = node.getSumOMFlag(m_aut) && m_sumOMFlag;
                    node.setSumOMFlag(m_aut, m_sumOMFlag);
                }
            }
        }
    }
    
    /**
     * Operation to set the CompleteTestData flag at TDManager.
     * @author BREDEX GmbH
     * @created 10.10.2005
     */
    private static class CompleteTestDataOperation 
            implements ITreeNodeOperation<INodePO> {

        /** The Locale to check */
        private Locale m_locale;
        
        /**
         * @param loc The Locale to check
         */
        public CompleteTestDataOperation(Locale loc) {
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

        /** {@inheritDoc} */
        public void postOperate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {
            // no op
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
        updateTdMap(loc, hasCompleteTD, node);
    }

    /**
     * @param loc
     *            language, for which the complete flag is modified
     * @param hasCompleteTD
     *            state of completeFlag for node
     * @param node
     *            node, which has the modified completeTDFlag
     */
    private static void updateTdMap(Locale loc, boolean hasCompleteTD,
        IParamNodePO node) {
        node.setCompleteTdFlag(loc, hasCompleteTD);
        node.setSumTdFlag(loc, hasCompleteTD);
    }

    /**
     * checks OM and TD Completness of all TS
     * @param loc the Locale to check
     * @param root INodePO
     */
    public static void checkAll(Locale loc, INodePO root) {
        // Iterate Execution tree
        final TreeTraverser traverser = new TreeTraverser(root);
        traverser.addOperation(new CompleteObjectMappingOperation());
        traverser.addOperation(new CompleteTestDataOperation(loc));
        traverser.addOperation(new MissingTestCaseOperation());
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
        new TreeTraverser(root, new CompleteTestDataOperation(loc))
                .traverse(true);
    }

    /**
     * checks OM Completness of all TS
     * @param root
     *      INodePO
     */
    public static void checkObjectMappings(INodePO root) {
        // Iterate Execution tree
        new TreeTraverser(root, new CompleteObjectMappingOperation())
                .traverse(true);
    }
}
