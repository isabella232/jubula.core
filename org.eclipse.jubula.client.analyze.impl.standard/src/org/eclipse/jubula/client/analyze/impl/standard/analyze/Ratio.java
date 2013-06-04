package org.eclipse.jubula.client.analyze.impl.standard.analyze;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jubula.client.analyze.definition.IAnalyze;
import org.eclipse.jubula.client.analyze.impl.standard.i18n.Messages;
import org.eclipse.jubula.client.analyze.internal.AnalyzeParameter;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.helper.ProjectContextHelper;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.ToolkitConstants;
import org.eclipse.jubula.tools.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.ToolkitPluginDescriptor;
/**
 * 
 * @author volker
 *
 */
public class Ratio implements IAnalyze {
    /**
     * The CountElementOperation which is given to the TreeTraverser
     * @author volker
     */
    static class CountToolkitLvl extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        /**
         * This Map is used to save the amount of the different NodeTypes
         */
        private Map<String, Integer> m_amount = 
                new HashMap<String, Integer>();
        
        /** The given ProgressMonitor */
        private IProgressMonitor m_pm;
        
        /**
         * The Ratio-Elements-String
         */
        private String m_ratioElements;
        

        /**
         * The Ratio-Value-String
         */
        private String m_ratioValue;
        /**
         * This Map is used to save the amount of the different Referenced TestSuites
         */
        private List<IRefTestSuitePO> m_refTestSuites;
        
        /**
         * The ID of the loaded Project
         */
        private long m_projectid;
        /**
         * The CompSystem
         */
        private CompSystem m_compSystem;
        
        /**
         * The Constructor of the CountElementOperation
         * 
         * @param monitor
         *            The given ProgressMonitor
         */
        public CountToolkitLvl(IProgressMonitor monitor) {
            setProgressMonitor(monitor);
            setLoadedProjectID(GeneralStorage.getInstance().getProject()
                    .getId());
            setCompSystem(ComponentBuilder.getInstance().getCompSystem());
            setRatioElements(StringConstants.EMPTY);
            setRatioValue(StringConstants.EMPTY);
        }
        
        /**
         * @return The RatioElementString
         */
        public String getRatioElements() {
            return m_ratioElements;
        }
        
        /**
         * @param ratioElements The given RatioElementsString
         */
        public void setRatioElements(String ratioElements) {
            this.m_ratioElements = ratioElements;
        }
        
        /**
         * @return The RatioValueString
         */
        public String getRatioValue() {
            return m_ratioValue;
        }
        
        /**
         * @param ratioValue The given RatioValueString
         */
        public void setRatioValue(String ratioValue) {
            this.m_ratioValue = ratioValue;
        }
        /**
         * @return The CompSystem
         */
        public CompSystem getCompSystem() {
            return m_compSystem;
        }
        /**
         * @param compSystem The given CompSystem
         */
        public void setCompSystem(CompSystem compSystem) {
            this.m_compSystem = compSystem;
        }
        /**
         * @return The ID of the loaded Project
         */
        public long getLoadedProjectID() {
            return m_projectid;
        }
        /**
         * @param id The given projectId
         */
        public void setLoadedProjectID(long id) {
            this.m_projectid = id;
        }
        /**
         * @return The List with the referenced Test Suites
         */
        public List<IRefTestSuitePO> getRefTestSuites() {
            return m_refTestSuites;
        }
        /**
         * @param reftestSuite The List with the referenced Test Suites
         */
        public void setRefTestSuites(List<IRefTestSuitePO> reftestSuite) {
            this.m_refTestSuites = reftestSuite;
        }

        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {

                // if the element is an instance of IRefTestSuitePO, save its
                // GUID in an arrayList. It is used later to traverse the TestSuites
                // again
            if (node instanceof IRefTestSuitePO) {
                getRefTestSuites().add((IRefTestSuitePO) node);
            }
            if (node instanceof ICapPO) {
                
                ICapPO cap = (ICapPO) node;
                Component comp = getCompSystem().findComponent(
                        cap.getComponentType());
                String type = StringConstants.EMPTY;

                if (comp.getToolkitDesriptor().getName().equals("abstract")
                        || comp.getToolkitDesriptor().getName()
                                .equals("concrete")
                        || comp.getToolkitDesriptor().getName()
                                .equals(StringConstants.EMPTY)) {
                    type = Messages.General;
                } else {
                    ToolkitPluginDescriptor tkpd = 
                            getParentToolkitPluginDescriptor(comp.
                                    getToolkitDesriptor());     
                    type = tkpd.getName();
                }
                Integer ccount = getAmount().get(type);
                if (ccount == null) {
                    ccount = new Integer(0);
                }
                Integer nCount = ccount + 1;
                getAmount().put(type, nCount);
                getProgressMonitor().worked(1);
                if (getProgressMonitor().isCanceled()) {
                    getProgressMonitor().done();
                    throw new OperationCanceledException();
                }
            }
            return true;
        }

        /**
         * 
         * @param toolkitdesc
         *            The given Component
         * @return The parent ToolkitPluginDescriptor or the given one, if there
         *         is no parent ToolkitPluginDescriptor
         */
        private ToolkitPluginDescriptor getParentToolkitPluginDescriptor(
                ToolkitPluginDescriptor toolkitdesc) {

            if (toolkitdesc.getDepends().equals(
                    ToolkitConstants.EMPTY_EXTPOINT_ENTRY)) {
                return toolkitdesc;
            }
            ToolkitPluginDescriptor desc = ComponentBuilder.getInstance()
                    .getCompSystem().getToolkitPluginDescriptor(
                            toolkitdesc.getDepends());
            return getParentToolkitPluginDescriptor(desc);
        }
        
        /**
         * @return The AmountMap containing the amounts of the different NodeTypes
         */
        public Map<String, Integer> getAmount() {
            return m_amount;
        }

        /**
         * @return The ProgressMonitor
         */
        public IProgressMonitor getProgressMonitor() {
            return m_pm;
        }

        /**
         * @param monitor
         *            The given ProgressMonitor
         */
        public void setProgressMonitor(IProgressMonitor monitor) {
            this.m_pm = monitor;
        }
    }

    /**
     * {@inheritDoc}
     */
    public AnalyzeResult execute(Object obj2analyze, IProgressMonitor monitor,
            String resultType, List<AnalyzeParameter> param,
            String analyzeName, ExecutionEvent event) {
        
        int workAmount = 1;
        // get the number of nodes from the NodePersistenceManager to have a
        // representative workAmount value for the ProgressMonitor
        if (obj2analyze instanceof IProjectPO) {
            workAmount = (int) NodePM.getNumNodes(((IProjectPO) obj2analyze)
                    .getId(), GeneralStorage.getInstance().getMasterSession());
        } else if (obj2analyze instanceof INodePO) {
            workAmount = (int) NodePM.getNumNodes(((INodePO) obj2analyze)
                    .getParentProjectId(), GeneralStorage.getInstance()
                    .getMasterSession());
        }
        monitor.beginTask(StringConstants.EMPTY, workAmount);
        monitor.subTask(analyzeName);

        CountToolkitLvl c = new CountToolkitLvl(monitor);
        
        // reset the TestSuiteGUID ArrayList
        c.setRefTestSuites(new ArrayList<IRefTestSuitePO>());
        
        traverse(c, obj2analyze, ProjectContextHelper.getObjContType());

        monitor.worked(1);
        if (monitor.isCanceled()) {
            monitor.done();
            throw new OperationCanceledException();
        }
        
        Map<String, String> result = new HashMap<String, String>();
        
        int complete = 0;
        for (Map.Entry<String, Integer> e : c.getAmount().entrySet()) {
            complete += e.getValue();
        }
        double d = Double.parseDouble(Integer.toString(complete)); 
        for (Map.Entry<String, Integer> e : c.getAmount().entrySet()) {
            
            double curr = Double.parseDouble(Integer.toString(e.getValue())); 
            double resNF = curr / d;
            
            DecimalFormat df = new DecimalFormat("0.00");
            
            String resu = df.format(resNF * 100) + "%";
            
            result.put(e.getKey(), resu);
        }

        return new AnalyzeResult(resultType, result, null);
    }


    /**
     * Creates a new instance of the TreeTraverser, adds the given Operation and
     * traverses
     * 
     * @param count
     *            The instance of CountElementOperation
     * @param obj
     *            The given selection
     * @param objContType
     *            The given ObjContType
     */
    private void traverse(
            CountToolkitLvl count, Object obj, String objContType) {
        if (obj instanceof INodePO && objContType.equals(
                IExecObjContPO.class.getSimpleName())) {
            final INodePO root = (INodePO) obj;
            
            TreeTraverser tt = new TreeTraverser(root, count, true, true) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        } else if (obj instanceof INodePO && objContType.equals(
                        ISpecObjContPO.class.getSimpleName())) {
            
            final INodePO root = (INodePO) obj;
            
            TreeTraverser tt = new TreeTraverser(root, count, true, false) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        }
        
        if (obj instanceof INodePO && objContType.equals("project")) {
            final INodePO root = (INodePO) obj;
            
            TreeTraverser tt = new TreeTraverser(root, count, true, true) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        }
        if (obj instanceof INodePO 
                && objContType.equals(StringConstants.EMPTY)) {
            final INodePO root = (INodePO) obj;
            
            TreeTraverser tt = new TreeTraverser(root, count, true, true) {
                @Override
                protected void traverseReusedProjectSpecPart(
                        ITreeTraverserContext<INodePO> context,
                        IProjectPO project) {
                    // ignore reused Projects
                }
            };
            tt.traverse(true);
        }
        // checks if there are Referenced TestSuites that have to be considered
        if (count.getRefTestSuites().size() != 0) {
            for (int i = 0; i < count.getRefTestSuites().size(); i++) {
                IRefTestSuitePO ref = count.getRefTestSuites().get(i);
                INodePO tsRoot = NodePM.getTestSuite(ref.getTestSuiteGuid());

                TreeTraverser tt = new TreeTraverser(tsRoot);
                tt.addOperation(count);
                tt.traverse(true);
            }
        }
    }
}

