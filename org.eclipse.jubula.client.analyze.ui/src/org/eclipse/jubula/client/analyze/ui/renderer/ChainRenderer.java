package org.eclipse.jubula.client.analyze.ui.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.helper.PortableNodeInformationHelper;
import org.eclipse.jubula.client.analyze.ui.i18n.Messages;
import org.eclipse.jubula.client.analyze.ui.internal.definition.IResultRendererUI;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
/**
 * 
 * @author volker
 *
 */
public class ChainRenderer implements IResultRendererUI {
    /**
     * The given AnalyzeResult
     */
    private AnalyzeResult m_analyzeResult;
    
    /**
     * The TopControl
     */
    private Control m_topControl;

    /**
     * The ChainRenderer that is used to display the chains
     */
    public ChainRenderer() {
    }
    
    /**
     * @return The AnalyzeResult
     */
    public AnalyzeResult getAnalyzeResult() {
        return m_analyzeResult;
    }
    
    /**
     * @param result The given AnalyzeResult
     */
    public void setAnalyzeResult(AnalyzeResult result) {
        this.m_analyzeResult = result;
    }
    /**
     * {@inheritDoc}
     */
    public Control getTopControl() {
        return m_topControl;
    }
    /**
     * @param control The given Control
     */
    public void setTopControl(Control control) {
        this.m_topControl = control;
    }

    /**
     * {@inheritDoc}
     */
    public void renderResult(AnalyzeResult result, Composite cmp) {

        setAnalyzeResult(result);
        createViewer(cmp);
    }

    /**
     * @param cmp The givenn Composite
     */
    private void createViewer(Composite cmp) {
        
        Chainmodel cm = new Chainmodel(getAnalyzeResult().getResult());
        
        TreeViewer viewer = new TreeViewer(cmp);
        viewer.setContentProvider(new ChainContentProvider(cm));
        viewer.setLabelProvider(new ChainLabelProvider());
        viewer.setInput(cm);
        setTopControl(viewer.getControl());
    }
    
    /**
     * @author volker
     */
    public class Chainmodel {

        /**
         * The given AnalyzeResult
         */
        private List<List<String>> m_result;
        
        /**
         * @param result result
         */
        public Chainmodel(Object result) {
            setResult(null);
            setResult(result);
        }
        
        /**
         * @param result The given Result
         */
        public void setResult(Object result) {
            if (result == null) {
                this.m_result = new ArrayList<List<String>>();
            } else if (result instanceof List<?>) {
                this.m_result = (List<List<String>>) result;
            }
        }
        
        /**
         * @return The Result
         */
        public List<List<String>> getResult() {
            return m_result;
        }
        /**
         * @param length The chainlength
         * @return the parents array
         */
        public String[] getParents(Integer length) {
            List<String> parents = new ArrayList<String>();
            for (int i = 0; i < getResult().size(); i++) {
                if (getResult().get(i).size() == length) {
                    parents.add(getResult().get(i).get(0));
                }
            }
            return parents.toArray(new String[0]);
        }
        /**
         * @return The chainLengths Array
         */
        public String[] getChainlength() {
            Set<String> chainl = new HashSet<String>();
            
            if (getResult().size() == 0) {
                String[] result = {Messages.NoChainFound};
                return result;
            }
            for (int i = 0; i < getResult().size(); i++) {
                chainl.add(Messages.Chainlength 
                    + Integer.toString(getResult().get(i).size()));
            }
            List<String> list = new ArrayList<String>(chainl);
            Collections.sort(list);
            String[] chainlength = list.toArray(new String[0]);
            
            return chainlength;
        }
    }

    /**
     * @author volker
     */
    class ChainContentProvider implements ITreeContentProvider {

        /**
         * the given analyzeResult
         */
        private Chainmodel m_chains;

        /**
         * @param model The given Chainmodel
         */
        public ChainContentProvider(Chainmodel model) {
            setChainmodel(model);
        }
        
        /**
         * 
         * @param model The given Chainmodel
         */
        public void setChainmodel(Chainmodel model) {
            this.m_chains = model;
        }
        
        /**
         * @return The analyzeResult
         */
        public Chainmodel getChainModel() {
            return m_chains;
        }
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            
        }

        /**
         * {@inheritDoc}
         */
        public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {
            
        }
        /**
         * {@inheritDoc}
         */
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Chainmodel) {
                Chainmodel cm = (Chainmodel) inputElement;
                return cm.getChainlength();
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof String) {

                String parent = (String) parentElement;
                String length = parent.replace(Messages.Chainlength, "");
                ISpecTestCasePO[] res = new ISpecTestCasePO[getChainModel()
                        .getParents(Integer.parseInt(length)).length];

                for (int i = 0; i < getChainModel().getParents(
                        Integer.parseInt(length)).length; i++) {
                    String guid = getChainModel().getParents(
                            Integer.parseInt(length))[i];
                    ISpecTestCasePO node = NodePM.getSpecTestCase(
                            GeneralStorage.getInstance().getProject().getId(),
                            guid);
                    res[i] = node;

                }
                return res;
            } else if (parentElement instanceof ISpecTestCasePO) {
                String parentGUID = ((ISpecTestCasePO) parentElement).getGuid();
                
                for (int i = 0; i < getChainModel().getResult().size(); i++) {
                    List<String> list = getChainModel().getResult().get(i);
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).equals(parentGUID)) {
                            ISpecTestCasePO[] arr = new ISpecTestCasePO[1];
                            ISpecTestCasePO child = NodePM.getSpecTestCase(
                                    PortableNodeInformationHelper
                                            .getNodeInformation().get(
                                                    list.get(j + 1)), list
                                            .get(j + 1));
                            
                            Map<String, Long> map = 
                                    PortableNodeInformationHelper
                                    .getNodeInformation();
                            
                            arr[0] = child;
                            return arr;
                        }
                    }
                }
            }
            return null;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getParent(Object element) {

            for (List<String> list : getChainModel().getResult()) {
                String prev = "";
                for (Iterator it = list.iterator(); it.hasNext();) {
                    String guid = (String) it.next();
                    if (prev.equals(guid)) {

                        ISpecTestCasePO node = NodePM.getSpecTestCase(
                                GeneralStorage.getInstance().getProject()
                                        .getId(), (String) it.next());
                        String[] res = new String[1];
                        res[0] = node.getName();
                        return res;
                    } else if (prev.equals("")) {
                        String[] res = new String[1];
                        res[0] = Messages.Chainlength
                                + Integer.toString(list.size());
                        return res;
                    }
                    prev = guid;
                }
            }
//                
//            }
            return null;
        }
        /**
         * {@inheritDoc}
         */
        public boolean hasChildren(Object element) {
            if (element instanceof String) {
                for (List<String> list : getChainModel().getResult()) {
                    for (Iterator it = list.iterator(); 
                            it.hasNext();) {
                        return it.hasNext();
                    }
                }
            } else if (element instanceof ISpecTestCasePO) {
               
                String parentGUID = ((ISpecTestCasePO) element).getGuid();
                
                for (int i = 0; i < getChainModel().getResult().size(); i++) {
                    List<String> list = getChainModel().getResult().get(i);
                    for (int j = 0; j < list.size(); j++) {
                        if ((list.get(j).equals(parentGUID))
                                && (j + 1) < list.size()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    /**
     * @author volker
     *
     */
    class ChainLabelProvider extends LabelProvider {
        
        /**
         * {@inheritDoc}
         */
        public String getText(Object element) {
            if (element instanceof String) {
                return (String) element;
            } else if (element instanceof ISpecTestCasePO) {
                ISpecTestCasePO spec = (ISpecTestCasePO) element;
                return spec.getName();
            }
            return null;
        }
    }
}
