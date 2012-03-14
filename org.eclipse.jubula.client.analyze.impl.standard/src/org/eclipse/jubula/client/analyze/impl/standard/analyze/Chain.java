package org.eclipse.jubula.client.analyze.impl.standard.analyze;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
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
         * The given SubMonitor
         */
        private SubMonitor m_progress;

        /** local cache */
        private List<String> m_singleUseNodeGUID = new ArrayList<String>();
        
        /** contains the SpecTestCases which are single-used */
        private List<ISpecTestCasePO> m_singleUsedSpecTCs = 
                new ArrayList<ISpecTestCasePO>();

        /**
         * Constructor
         * 
         * @param monitor
         *            The given SubMonitor
         */
        public NodeOperation(SubMonitor monitor) {
            setMonitor(monitor);
        }

        /**
         * @param monitor The given SubMonitor
         */
        public void setMonitor(SubMonitor monitor) {
            this.m_progress = monitor;
        }

        /**
         * @return The SubMonitor
         */
        public SubMonitor getMonitor() {
            return m_progress;
        }

        /**
         * @return The List that contains the singleUsedNodeGUIDs
         */
        private List<String> getSingleUsedNodeGUIDs() {
            return m_singleUseNodeGUID;
        }
        /**
         * @return The List that contains the singleUsedSpecTCs
         */
        private List<ISpecTestCasePO> getSingleUsedSpecTCs() {
            return m_singleUsedSpecTCs;
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
                        getSingleUsedSpecTCs().add(spec);
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
                                getSingleUsedSpecTCs().add(spec);
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
        
        SubMonitor progress = SubMonitor.convert(monitor, 100);
        
        monitor.subTask(Messages.AnalysingProjects);

        int traverseAmount = 1;
        
        if (obj2analyze instanceof IProjectPO) {
            
            traverseAmount = ((int) NodePM.getNumNodes(
                    ((IProjectPO) obj2analyze).getId(), GeneralStorage
                            .getInstance().getMasterSession()));
        } else if (obj2analyze instanceof INodePO) {
            
            traverseAmount = ((int) NodePM.getNumNodes(
                    ((INodePO) obj2analyze).getParentProjectId(),
                    GeneralStorage.getInstance().getMasterSession()));
        }
        NodeOperation nodeOp = new NodeOperation(progress.newChild(30)
                .setWorkRemaining(traverseAmount));

        traverse(obj2analyze, nodeOp, ProjectContextHelper.getObjContType());
        monitor.subTask(Messages.ChainBeginning);
        // set the parents List
        setParents(new ArrayList<ISpecTestCasePO>());
        
        monitor.subTask(Messages.PossibleChilds);
        anlayzePossibleChainElements(nodeOp, progress.newChild(65));
        
        List<ISpecTestCasePO> l = getParents();
        l.size();
        monitor.subTask(Messages.ConstructChains);
        constructChains(progress.newChild(5));
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
     * @param monitor
     *            The given SubMonitor
     */
    private void anlayzePossibleChainElements(NodeOperation nodeOp,
            SubMonitor monitor) {
        boolean isParent;

        monitor.setWorkRemaining(nodeOp.getSingleUsedNodeGUIDs().size());
        int counter = 0;
        for (ISpecTestCasePO sp : nodeOp.getSingleUsedSpecTCs()) {
            counter++;
            isParent = true;
            monitor.subTask(Messages.PossibleChilds + " " + counter + "/"
                    + nodeOp.getSingleUsedSpecTCs().size());
            for (ISpecTestCasePO pp : nodeOp.getSingleUsedSpecTCs()) {
                if (pp != null && pp.getUnmodifiableNodeList().size() == 1) {
                    IExecTestCasePO exec = (IExecTestCasePO) pp
                            .getUnmodifiableNodeList().get(0);
                    if (exec != null) {
                        ISpecTestCasePO spec = exec.getSpecTestCase();
                        if (spec != null) {
                            if (spec.getGuid().equals(sp.getGuid())
                                    || (NodePM.getInternalExecTestCases(
                                            sp.getGuid(),
                                            sp.getParentProjectId()).
                                            size() == 0)
                                    || sp.getUnmodifiableNodeList().
                                            size() == 0) {
                                isParent = false;
                            }
                        }
                    }
                }
            }
            if (isParent
                  && (!getParents().contains(sp))
                  && (sp.getUnmodifiableNodeList().size() == 1)
                  && (NodePM.getInternalExecTestCases(sp.getGuid(),
                          sp.getParentProjectId()).size() == 1)) {
                getParents().add(sp);
            }
            monitor.worked(1);
            if (monitor.isCanceled()) {
                monitor.done();
                throw new OperationCanceledException();
            }
        }
    }

    /**
     * Constructs the chains and saves every chain as a List
     * @param monitor 
     */
    private void constructChains(SubMonitor monitor) {
        monitor.setWorkRemaining(getParents().size());
        setLists(new ArrayList<List<String>>());

        for (ISpecTestCasePO curr : getParents()) {

            setChain(new ArrayList<String>());
            // add the parent as the first element of the chain
            getChain().add(curr.getGuid());
            getChainChildren(curr, getChain());
            getLists().add(getChain());
            
            monitor.worked(1);
            if (monitor.isCanceled()) {
                monitor.done();
                throw new OperationCanceledException();
            }
        }
    }

    /**
     * get the children of this parent and add them to the chainList
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

            if ((parent.getUnmodifiableNodeList().get(0) != null)
                    && parent.getUnmodifiableNodeList().get(0) 
                    instanceof IExecTestCasePO) {

                IExecTestCasePO exec = (IExecTestCasePO) parent
                        .getUnmodifiableNodeList().get(0);

                if (exec != null) {

                    ISpecTestCasePO child = exec.getSpecTestCase();
                    if (child != null) {

                        chain.add(child.getGuid());
                        getChainChildren(child, chain);
                    }
                }
            }
        }
        return null;
    }
}
