package org.eclipse.jubula.client.analyze.impl.standard.analyze;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jubula.client.analyze.definition.IAnalyze;
import org.eclipse.jubula.client.analyze.impl.standard.i18n.Messages;
import org.eclipse.jubula.client.analyze.internal.AnalyzeParameter;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.helper.PortableNodeInformationHelper;
import org.eclipse.jubula.client.analyze.internal.helper.ProjectContextHelper;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
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
 * 
 * @author volker
 * 
 */
public class Chain implements IAnalyze {
    /**
     * Contains the found chains
     */
    private List<Object> m_chains = new ArrayList<Object>();

    /**
     * Contains every chainParent
     */
    private List<ISpecTestCasePO> m_chainParents;

    /**
     * the currently parent SpecTestCase
     */
    private ISpecTestCasePO m_parentSpecTC;

    /**
     * the given ProgressMonitor
     */
    private IProgressMonitor m_monitor;

    /**
     * represents a chain
     */
    private List<String> m_chain;

    /**
     * contains The lists that represent the found chains
     */
    private List<List<String>> m_lists;

    /**
     * @return The lists that contains the chains lists
     */
    public List<List<String>> getLists() {
        return m_lists;
    }

    /**
     * @param lists
     *            The List that contains the Lists that represent the chains
     */
    public void setLists(List<List<String>> lists) {
        this.m_lists = lists;
    }

    /**
     * @return The chain
     */
    public List<String> getChain() {
        return m_chain;
    }

    /**
     * @param chain
     *            The List that is going to be set as the actual chain
     */
    public void setChain(List<String> chain) {
        this.m_chain = chain;
    }

    /**
     * @return The ProgressMonitor
     */
    public IProgressMonitor getMonitor() {
        return m_monitor;
    }

    /**
     * @param monitor
     *            The given ProgressMonitor
     */
    public void setMonitor(IProgressMonitor monitor) {
        this.m_monitor = monitor;
    }

    /**
     * @return The parent SpecTestCase
     */
    public ISpecTestCasePO getParentSpecTC() {
        return m_parentSpecTC;
    }

    /**
     * @param parent
     *            The parent SpecTestCase that is to be set
     */
    public void setParentSpecTC(ISpecTestCasePO parent) {
        this.m_parentSpecTC = parent;
    }

    /**
     * @return ArrayList that contains the parent SpecTestCases of a chain
     */
    public List<ISpecTestCasePO> getParents() {
        return m_chainParents;
    }

    /**
     * @param parents
     *            The given List with ISpecTestCasePOs
     */
    public void setParents(List<ISpecTestCasePO> parents) {
        this.m_chainParents = parents;
    }

    /**
     * @return ArrayList that contains every chain that has been found
     */
    public List<Object> getChains() {
        return m_chains;
    }

    /**
     * @author volker
     * 
     */
    static class NodeOperation extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /**
         * the given ProgressMonitor
         */
        private IProgressMonitor m_monitor;

        /** local cache */
        private List<String> m_singleUseNodeGUID = new ArrayList<String>();

        /**
         * Constructor
         * 
         * @param monitor
         *            The given ProgressMonitor
         */
        public NodeOperation(IProgressMonitor monitor) {
            setMonitor(monitor);
        }

        /**
         * @return The ProgressMonitor
         */
        public IProgressMonitor getMonitor() {
            return m_monitor;
        }

        /**
         * @param monitor
         *            The given ProgressMonitor
         */
        public void setMonitor(IProgressMonitor monitor) {
            this.m_monitor = monitor;
        }

        /**
         * @return The Map that contains the singleUsedNodeGUIDs
         */
        private List<String> getSingleUsedNodeGUIDs() {
            return m_singleUseNodeGUID;
        }

        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (!(node instanceof IEventExecTestCasePO)) {

                if (node instanceof ISpecTestCasePO) {

                    ISpecTestCasePO spec = (ISpecTestCasePO) node;

                    // save the Guid and the matching parentProjectGuid
                    PortableNodeInformationHelper.getNodeInformation().put(
                            spec.getGuid(), spec.getParentProjectId());

                    // check if the SpecTestCase is referenced only one time
                    // and has only one child
                    if ((!getSingleUsedNodeGUIDs().contains(spec.getGuid()))
                            && (NodePM.getInternalExecTestCases(spec.getGuid(),
                                    (spec.getParentProjectId()))).size() == 1
                            && ((spec.getUnmodifiableNodeList().size() <= 1))) {

                        getSingleUsedNodeGUIDs().add(spec.getGuid());
                        return !alreadyVisited;
                    }
                }

                if (node instanceof IExecTestCasePO) {

                    IExecTestCasePO exec = (IExecTestCasePO) node;
                    exec.getSpecTestCase();
                    if (exec.getSpecTestCase() != null) {

                        ISpecTestCasePO spec = exec.getSpecTestCase();

                        if (spec.getParentProjectId() != GeneralStorage
                                .getInstance().getProject().getId()) {
                            PortableNodeInformationHelper.getNodeInformation()
                                    .put(spec.getGuid(),
                                            spec.getParentProjectId());

                            if ((!getSingleUsedNodeGUIDs().contains(
                                    spec.getGuid()))
                                    && (NodePM.getInternalExecTestCases(
                                            spec.getGuid(),
                                            (spec.getParentProjectId())))
                                            .size() == 1
                                    && (spec.getParentProjectId().toString()
                                            .equals(GeneralStorage
                                                    .getInstance().getProject()
                                                    .getId().toString()))) {
                                getSingleUsedNodeGUIDs().add(spec.getGuid());
                                return !alreadyVisited;
                            }
                        }
                    }
                    return !alreadyVisited;
                }
                getMonitor().worked(1);
                if (getMonitor().isCanceled()) {
                    getMonitor().done();
                    throw new OperationCanceledException();
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
            workAmount = 3 + ((int) NodePM.getNumNodes(
                    ((IProjectPO) obj2analyze).getId(), 
                    GeneralStorage.getInstance().getMasterSession()));
        } else if (obj2analyze instanceof INodePO) {
            workAmount = 3 + ((int) NodePM.getNumNodes(
                    ((INodePO) obj2analyze).getParentProjectId(),
                    GeneralStorage.getInstance().getMasterSession()));
        }
        monitor.beginTask("", workAmount);
        monitor.subTask(Messages.AnalysingProjects);
        

        NodeOperation nodeOp = new NodeOperation(monitor);

        traverse(obj2analyze, nodeOp, ProjectContextHelper.getObjContType());

        monitor.subTask(Messages.ChainBeginning);
        // set the parents List
        setParents(new ArrayList<ISpecTestCasePO>());
        monitor.worked(1);
        if (monitor.isCanceled()) {
            monitor.done();
            throw new OperationCanceledException();
        }
        
        monitor.subTask(Messages.PossibleChilds);
        anlayzePossibleChainElements(nodeOp);
        if (monitor.isCanceled()) {
            monitor.done();
            throw new OperationCanceledException();
        }

        
        List<ISpecTestCasePO> l = getParents();
        l.size();
        monitor.subTask(Messages.ConstructChains);
        constructChains();
        
        if (monitor.isCanceled()) {
            monitor.done();
            throw new OperationCanceledException();
        }
        return new AnalyzeResult(resultType, getLists());
    }

    /**
     * Creates a new instance of the TreeTraverser, adds the given Operation and
     * traverses
     * 
     * @param obj
     *            The given Selection
     * @param nodeOp
     *            The given NodeOperation
     * @param objContType
     *            The given ObjContType
     */
    private void traverse(Object obj, NodeOperation nodeOp, 
            String objContType) {

        if (obj instanceof INodePO && objContType.equals("IExecObjContPO")) {
            final INodePO root = (INodePO) obj;
            TreeTraverser tt = new TreeTraverser(root, nodeOp, true, true) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        } else if (obj instanceof INodePO
                && objContType.equals("ISpecObjContPO")) {
            final INodePO root = (INodePO) obj;
            TreeTraverser tt = new TreeTraverser(root, nodeOp, true, true) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        } else if (obj instanceof INodePO && objContType.equals("project")) {
            final INodePO root = (INodePO) obj;
            TreeTraverser tt = new TreeTraverser(root, nodeOp, true, true) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        } else if (obj instanceof INodePO && objContType.equals("")) {
            final INodePO root = (INodePO) obj;
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

    /**
     * Checks if there are chains in the SingleUsedNodeGuid-List in the
     * NodeOperation
     * 
     * @param nodeOp
     *            The given NodeOperation
     */
    private void anlayzePossibleChainElements(NodeOperation nodeOp) {
        boolean isParent;

        for (String guid : nodeOp.getSingleUsedNodeGUIDs()) {
            isParent = true;
            ISpecTestCasePO toCheck = NodePM.getSpecTestCase(
                    PortableNodeInformationHelper.getNodeInformation()
                            .get(guid), guid);
            if (toCheck != null) {

                for (String ppGuid : nodeOp.getSingleUsedNodeGUIDs()) {

                    ISpecTestCasePO possibleParent = NodePM.getSpecTestCase(
                            PortableNodeInformationHelper.getNodeInformation()
                                    .get(ppGuid), ppGuid);
                    if (possibleParent != null
                            && possibleParent.getUnmodifiableNodeList().
                                size() == 1) {

                        IExecTestCasePO exec = (IExecTestCasePO) possibleParent
                                .getUnmodifiableNodeList().get(0);
                        if (exec != null) {

                            ISpecTestCasePO spec = exec.getSpecTestCase();

                            if (spec != null) {

                                if ((spec.getGuid().equals(toCheck.getGuid()))
                                        || (NodePM.getInternalExecTestCases(
                                                toCheck.getGuid(),
                                                toCheck.getParentProjectId())
                                                .size() == 0)
                                        || toCheck.getUnmodifiableNodeList()
                                                .size() == 0) {
                                    isParent = false;
                                }
                            }
                        }
                    }
                }
                if (isParent
                        && (!getParents().contains(toCheck))
                        && (toCheck.getUnmodifiableNodeList().size() == 1)
                        && (NodePM.getInternalExecTestCases(toCheck.getGuid(),
                                toCheck.getParentProjectId()).size() == 1)) {
                    getParents().add(toCheck);
                }
            }
        }
    }

    /**
     * 
     */
    private void constructChains() {

        setLists(new ArrayList<List<String>>());

        List<ISpecTestCasePO> l = getParents();
//        System.out.println("parents");

        ISpecTestCasePO currentSTC;
        for (ISpecTestCasePO curr : getParents()) {

            setChain(new ArrayList<String>());
            // add the parent as the first element of the chain
            getChain().add(curr.getGuid());
            getChainChildren(curr, getChain());
            getLists().add(getChain());
        }
    }

    /**
     * get the childs of this parent and add them to the chainList
     * 
     * @param parent
     *            the parentSpecTC
     * @param chain
     *            The given chainList
     * @return the childnode
     */
    private ISpecTestCasePO getChainChildren(ISpecTestCasePO parent,
            List<String> chain) {
        if (parent.getUnmodifiableNodeList().size() == 1) {

            if (parent.getUnmodifiableNodeList().get(0) 
                    instanceof IExecTestCasePO) {

                IExecTestCasePO exec = (IExecTestCasePO) parent
                        .getUnmodifiableNodeList().get(0);
                ISpecTestCasePO child = exec.getSpecTestCase();

                chain.add(child.getGuid());
                getChainChildren(child, chain);
            }
        }
        return null;
    }
}
