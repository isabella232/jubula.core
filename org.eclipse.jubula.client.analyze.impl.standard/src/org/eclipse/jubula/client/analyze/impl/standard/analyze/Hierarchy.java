package org.eclipse.jubula.client.analyze.impl.standard.analyze;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.analyze.definition.IAnalyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeParameter;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;

/**
 * @author volker
 * 
 */
public class Hierarchy implements IAnalyze {

//
//    /**
//     * contains The TopNodes of an empty chain
//     */
//    private Map<String, ISpecTestCasePO> m_chainTopNodes = 
//            new HashMap<String, ISpecTestCasePO>();
//    
//    /**
//     * @return The Map that contains the SpecTestCasePOs that are TopNodes of an empty chain 
//     */
//    private Map<String, ISpecTestCasePO> getChainTopNodes() {
//        return this.m_chainTopNodes;
//    }
//    
//    private ISpecTestCasePO m_parent;
//    
//    private ISpecTestCasePO getParent() {
//        return this.m_parent;
//    }
//    
//    private void setParent(ISpecTestCasePO parent) {
//        this.m_parent = parent;
//    }

    /**
     * @author volker
     * 
     */
    static class NodeOperation extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** local cache */
        private List<String> m_singleUseNodeGUID = new ArrayList<String>();
        
        /**
         * @return The Map that contains the singleUsedNodeGUIDs
         */
        private List<String> getSingelUsedNodeGUIDs() {
            return this.m_singleUseNodeGUID;
        }

        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {

            if (node instanceof IExecTestCasePO) {
                ISpecTestCasePO spec = ((IExecTestCasePO) node)
                        .getSpecTestCase();

                if ((!getSingelUsedNodeGUIDs().contains(spec.getGuid()))
                        && (NodePM.getInternalExecTestCases(spec.getGuid(),
                                (spec.getParentProjectId()))).size() == 1
                        && ((spec.getUnmodifiableNodeList().size() <= 1))) {

                    getSingelUsedNodeGUIDs().add(spec.getGuid());
                    return !alreadyVisited;
                }
            } else if (node instanceof ISpecTestCasePO) {

                ISpecTestCasePO spec = (ISpecTestCasePO) node;

                if ((!getSingelUsedNodeGUIDs().contains(spec.getGuid()))
                        && (NodePM.getInternalExecTestCases(spec.getGuid(),
                                (spec.getParentProjectId()))).size() == 1
                        && ((spec.getUnmodifiableNodeList().size() <= 1))) {

                    getSingelUsedNodeGUIDs().add(spec.getGuid());
                    return !alreadyVisited;
                }
            }
            return !alreadyVisited;
        }
    }

    /**
     * {@inheritDoc}
     */
    public AnalyzeResult execute(Object obj2analyze, IProgressMonitor monitor,
            String resultType, List<AnalyzeParameter> param, 
            String analyzeName) {
        int workAmount = 1;
        // get the number of nodes from the NodePersistenceManager to have a
        // representative workAmount value for the ProgressMonitor
        if (obj2analyze instanceof IProjectPO) {
            workAmount = (int) NodePM.getNumNodes(((IProjectPO) obj2analyze)
                    .getId(), GeneralStorage.getInstance().getMasterSession());
        } else if (obj2analyze instanceof INodePO) {
            workAmount = (int) NodePM.getNumNodes(
                    ((INodePO) obj2analyze).getParentProjectId(),
                    GeneralStorage.getInstance().getMasterSession());
        }
        monitor.beginTask("", workAmount);
        monitor.subTask(analyzeName);

        NodeOperation nodeOp = new NodeOperation();
        traverse(obj2analyze, nodeOp);

        System.out.println(nodeOp.getSingelUsedNodeGUIDs().size());
        return null;
    }

    /**
     * Creates a new instance of the TreeTraverser, adds the given Operation and
     * traverses
     * 
     * @param obj2analyze
     *            The given Selection
     * @param nodeOp
     *            The given NodeOperation
     */
    private void traverse(Object obj2analyze, NodeOperation nodeOp) {

        if (obj2analyze instanceof INodePO) {
            INodePO root = (INodePO) obj2analyze;

            TreeTraverser tt = new TreeTraverser(root, nodeOp, true, true) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        }
    }

}
