/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.NodePM.AbstractCmdHandleChild;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.TransactionSupport;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransactAction;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.RefToken;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;

/**
 * @author Markus Tiede
 * @since 1.2
 */
public class SaveAsNewTestCaseHandler extends AbstractRefactorHandler {
    /**
     * @author Markus Tiede
     * @since 1.2
     */
    private static class CloneTransaction implements ITransactAction {
        /**
         * the name of the new test case
         */
        private final String m_newTestCaseName;
        /**
         * the param nodes to clone
         */
        private final List<IParamNodePO> m_nodesToClone;

        /**
         * the new spec test case
         */
        private ISpecTestCasePO m_newSpecTC = null;
        
        /**
         * Constructor
         * 
         * @param newTestCaseName
         *            the name of the new test case
         * @param nodesToClone
         *            the param nodes to clone
         */
        public CloneTransaction(String newTestCaseName,
                List<IParamNodePO> nodesToClone) {
            m_newTestCaseName = newTestCaseName;
            m_nodesToClone = nodesToClone;
        }

        /** {@inheritDoc} */
        public void run(EntityManager s) throws PMException {
            ISpecTestCasePO newTc = NodeMaker
                    .createSpecTestCasePO(m_newTestCaseName);
            final ParamNameBPDecorator pMapper = new ParamNameBPDecorator(
                    ParamNameBP.getInstance());
            s.persist(newTc);
            INodePO parent = ISpecObjContPO.TCB_ROOT_NODE;
            AbstractCmdHandleChild handler = NodePM.getCmdHandleChild(parent,
                    newTc);
            handler.add(parent, newTc, null);
            Map<String, String> oldToNewGuids = new HashMap<String, String>();
            for (IParamNodePO node : m_nodesToClone) {
                addCloneToNewSpecTc(newTc, node, s, pMapper, oldToNewGuids);
            }
            registerParamNamesToSave(newTc, pMapper);
            s.merge(newTc);
            pMapper.persist(s, GeneralStorage.getInstance().getProject()
                    .getId());
            setNewSpecTC(newTc);
        }
        
        /**
         * @param newSpecTC
         *            the new spec test case
         * @param nodeToCopy
         *            the param node to clone and add
         * @param session the session 
         * @param pMapper the param name mapper
         * @param oldToNewGuids the old to new guid map
         * @throws PMException in case of an persitence exception
         */
        private void addCloneToNewSpecTc(ISpecTestCasePO newSpecTC,
            IParamNodePO nodeToCopy, EntityManager session,
            ParamNameBPDecorator pMapper, Map<String, String> oldToNewGuids)
            throws PMException {
            IParamNodePO newParamNode = null;
            if (nodeToCopy instanceof IExecTestCasePO) {
                IExecTestCasePO origExec = (IExecTestCasePO) nodeToCopy;
                IExecTestCasePO newExec = NodeMaker.createExecTestCasePO(
                        origExec.getSpecTestCase());
                fillExec(origExec, newExec);
                newParamNode = newExec;
            } else if (nodeToCopy instanceof ICapPO) {
                ICapPO origCap = (ICapPO) nodeToCopy;
                ICapPO newCap = NodeMaker.createCapPO(
                        origCap.getName(), 
                        origCap.getComponentName(), 
                        origCap.getComponentType(), 
                        origCap.getActionName());
                fillCap(origCap, newCap);
                newParamNode = newCap;
            }
            if (newParamNode != null) {
                addParamsToNewParent(newSpecTC, 
                        newParamNode, pMapper, oldToNewGuids);
                newSpecTC.addNode(newParamNode);
            }
        }

        /**
         * @param newSpecTC
         *            the new parent to modify
         * @param newParamChildNode
         *            the new param child node
         * @param pMapper
         *            the param name mapper
         * @param oldToNewGuids
         *            the old to new guids map
         */
        private void addParamsToNewParent(ISpecTestCasePO newSpecTC,
            IParamNodePO newParamChildNode, ParamNameBPDecorator pMapper,
            Map<String, String> oldToNewGuids) {
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            TDCell cell = null;
            List<Locale> langs = proj.getLangHelper().getLanguageList();
            for (Locale lang : langs) {
                for (Iterator<TDCell> it = newParamChildNode
                        .getParamReferencesIterator(lang); it.hasNext();) {
                    cell = it.next();
                    String guid = newParamChildNode.getDataManager()
                            .getUniqueIds().get(cell.getCol());
                    IParamDescriptionPO childDesc = newParamChildNode
                            .getParameterForUniqueId(guid);
                    // The childDesc can be null if the parameter has been
                    // removed in another session and not yet updated in the 
                    // current editor session.
                    if (childDesc != null) {
                        ModelParamValueConverter conv = 
                                new ModelParamValueConverter(
                                cell.getTestData(), newParamChildNode, lang,
                                childDesc);
                        List<RefToken> refTokens = conv.getRefTokens();
                        for (RefToken refToken : refTokens) {
                            String oldGUID = RefToken.extractCore(refToken
                                    .getModelString());
                            String paramName = ParamNameBP.getInstance()
                                    .getName(oldGUID,
                                            childDesc.getParentProjectId());
                            IParamDescriptionPO parentParamDescr = newSpecTC
                                    .addParameter(childDesc.getType(),
                                            paramName, false, pMapper);
                            if (parentParamDescr != null) {
                                String newGuid = parentParamDescr.getUniqueId();
                                oldToNewGuids.put(oldGUID, newGuid);
                            }
                            // update TestDataPO of child with GUID for reference
                            conv.replaceGuidsInReferences(oldToNewGuids);
                            cell.getTestData().setValue(lang,
                                    conv.getModelString(), proj);
                        }
                    }
                }
            }
        }

        /**
         * @param origCap
         *            the source
         * @param newCap
         *            the target
         */
        private void fillCap(ICapPO origCap, ICapPO newCap) {
            newCap.setActive(origCap.isActive());
            newCap.setComment(origCap.getComment());
            newCap.setGenerated(origCap.isGenerated());
            newCap.setToolkitLevel(origCap.getToolkitLevel());
            origCap.getDataManager().deepCopy(
                    newCap.getDataManager());
        }

        /**
         * @param origExec
         *            the source
         * @param newExec
         *            the target
         */
        private void fillExec(IExecTestCasePO origExec, 
            IExecTestCasePO newExec) {
            newExec.setActive(origExec.isActive());
            newExec.setComment(origExec.getComment());
            newExec.setDataFile(origExec.getDataFile());
            newExec.setGenerated(origExec.isGenerated());
            ISpecTestCasePO origSpecTC = origExec.getSpecTestCase();
            if (!origExec.getName().equals(
                    origSpecTC.getName())) {
                newExec.setName(origExec.getName());
            }
            newExec.setToolkitLevel(origExec.getToolkitLevel());
            if (!origExec.getDataManager().equals(
                    origSpecTC.getDataManager())) {
                newExec.setHasReferencedTD(false);
                origExec.getDataManager().deepCopy(
                        newExec.getDataManager());
            } else {
                newExec.setHasReferencedTD(true);
            }
            newExec.setReferencedDataCube(origExec.getReferencedDataCube());
            
            for (ICompNamesPairPO origPair : origExec.getCompNamesPairs()) {
                ICompNamesPairPO newPair = PoMaker.createCompNamesPairPO(
                        origPair.getFirstName(), origPair.getSecondName(),
                        origPair.getType());
                newPair.setPropagated(origPair.isPropagated());
                newExec.addCompNamesPair(newPair);
            }
        }

        /**
         * @return the m_newSpecTC
         */
        public ISpecTestCasePO getNewSpecTC() {
            return m_newSpecTC;
        }

        /**
         * @param newSpecTC the m_newSpecTC to set
         */
        public void setNewSpecTC(ISpecTestCasePO newSpecTC) {
            m_newSpecTC = newSpecTC;
        }
    }

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        String newTestCaseName = getNewTestCaseName(event);
        if (newTestCaseName != null) {
            ISpecTestCasePO newSpecTC = null;
            IStructuredSelection ss = getSelection();
            final List<IParamNodePO> nodesToClone = 
                    new ArrayList<IParamNodePO>(
                    ss.size());
            Iterator it = ss.iterator();
            while (it.hasNext()) {
                nodesToClone.add((IParamNodePO) it.next());
            }
            newSpecTC = createAndPerformNodeDuplication(newTestCaseName,
                    nodesToClone);

            DataEventDispatcher.getInstance().fireDataChangedListener(
                    newSpecTC, DataState.Added, UpdateState.all);
            TestCaseBrowser tcb = MultipleTCBTracker.getInstance().getMainTCB();
            if (tcb != null) {
                tcb.getTreeViewer().setSelection(
                        new StructuredSelection(newSpecTC), true);
            }
        }
        return null;
    }

    /**
     * @param newTestCaseName
     *            the new test case name
     * @param nodesToClone
     *            the nodes to clone
     * @return the new spec test case with cloned param nodes
     */
    private ISpecTestCasePO createAndPerformNodeDuplication(
            String newTestCaseName, List<IParamNodePO> nodesToClone) {
        final CloneTransaction op = 
                new CloneTransaction(newTestCaseName, nodesToClone);
        try {
            new TransactionSupport().transact(op);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        }
        return op.getNewSpecTC();
    }
}
