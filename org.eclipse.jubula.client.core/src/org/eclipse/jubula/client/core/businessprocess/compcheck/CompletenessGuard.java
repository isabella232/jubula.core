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
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;

/**
 * @author BREDEX GmbH
 * @created 10.05.2005
 */
public final class CompletenessGuard {
    /**
     * Operation to set the CompleteSpecTc flag for a node
     */
    private static class CheckMissingTestCaseReferences extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTc = (IExecTestCasePO) node;
                boolean isMissingSpecTc = execTc.getSpecTestCase() == null;
                setCompletenessMissingTestCase(execTc, !isMissingSpecTc);
            }
            return !alreadyVisited;
        }
    }

    /**
     * Operation to reflect the activity status of a node
     */
    private static class InactiveNodesOperation extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (!node.isActive()) {
                Set<IProblem> problemsToRemove = new HashSet<IProblem>(
                        node.getProblems());
                for (IProblem problem : problemsToRemove) {
                    node.removeProblem(problem);
                }
            }
            return !alreadyVisited;
        }
    }

    /**
     * Operation to set the CompleteTestData flag at TDManager.
     * 
     * @author BREDEX GmbH
     * @created 10.10.2005
     */
    private static class CheckTestDataCompleteness extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /**
         * Sets the CompleteTDFlag in all ParamNodePOs. {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            checkLocalTestData(node);
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
     * checks OM and TD Completeness of all TS
     * 
     * @param root
     *            INodePO
     * @param monitor
     *            the progress monitor to use
     */
    public static void checkAll(INodePO root, IProgressMonitor monitor) {
        // Iterate Execution tree
        TreeTraverser traverser = new TreeTraverser(root);
        traverser.setMonitor(monitor);
        traverser.addOperation(new CheckTestDataCompleteness());
        traverser.addOperation(new CheckMissingTestCaseReferences());
        traverser.addOperation(new InactiveNodesOperation());
        traverser.traverse(true);
        
        CompCheck check = new CompCheck(TestSuiteBP.getListOfTestSuites());
        check.traverse();
        check.addProblems();
    }

    /**
     * checks TD Completeness of all TS
     * 
     * @param root
     *            INodePO
     */
    public static void checkTestData(INodePO root) {
        new TreeTraverser(root, new CheckTestDataCompleteness()).traverse(true);
    }

    /**
     * @param node
     *            the node
     * @param aut
     *            AUT, for which to set the omFlag
     * @param omFlag
     *            The omFlag to set.
     */
    private static void setCompletenessObjectMapping(INodePO node,
            IAUTMainPO aut, boolean omFlag) {
        setNodeProblem(node,
                ProblemFactory.createIncompleteObjectMappingProblem(aut),
                omFlag);
    }

    /**
     * @param node
     *            the node
     * @param problem
     *            the problem
     * @param deleteOrAdd
     *            true to delete; false to add problem to node
     */
    private static void setNodeProblem(INodePO node, IProblem problem,
            boolean deleteOrAdd) {
        if (deleteOrAdd) {
            node.removeProblem(problem);
        } else {
            node.addProblem(problem);
        }
    }

    /**
     * @param node
     *            the node
     * @param completeTCFlag
     *            true to delete; false to add problem to node
     */
    private static void setCompletenessMissingTestCase(INodePO node,
            boolean completeTCFlag) {
        setNodeProblem(node, ProblemFactory.MISSING_NODE, completeTCFlag);
    }

    /**
     * method to set the sumTdFlag for a given Locale
     * 
     * @param node
     *            the node
     * @param flag
     *            the state of sumTdFlag to set
     */
    public static void setCompletenessTestData(INodePO node, boolean flag) {
        setNodeProblem(node,
                ProblemFactory.createIncompleteTestDataProblem(node), flag);
    }

    /**
     * clear all test data related problems for the given node
     * 
     * @param node
     *            the node
     */
    private static void resetTestDataCompleteness(INodePO node) {
        Set<IProblem> toRemove = new HashSet<IProblem>();
        for (IProblem problem : node.getProblems()) {
            if (problem.getProblemType().equals(
                    ProblemType.REASON_TD_INCOMPLETE)) {
                toRemove.add(problem);
            }
        }
        for (IProblem problem : toRemove) {
            node.removeProblem(problem);
        }
    }

    /**
     * @param node
     *            the node to check
     */
    public static void checkLocalTestData(INodePO node) {
        INodePO possibleDataSourceNode = node;
        if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTc = (IExecTestCasePO) node;
            if (execTc.getHasReferencedTD()) {
                possibleDataSourceNode = execTc.getSpecTestCase();
            }
        }
        if (possibleDataSourceNode instanceof IParamNodePO) {
            IParamNodePO dataSourceNode = (IParamNodePO) possibleDataSourceNode;
            INodePO nodeToModify = null;
            if (!(node instanceof ISpecTestCasePO)) {
                nodeToModify = node;
            }
            if (nodeToModify != null)  {
                resetTestDataCompleteness(nodeToModify);
                setCompletenessTestData(nodeToModify,
                        dataSourceNode.isTestDataComplete());
                
            }
        }
    }
}
