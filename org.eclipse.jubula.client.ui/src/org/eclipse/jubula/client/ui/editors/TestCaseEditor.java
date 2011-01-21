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
package org.eclipse.jubula.client.ui.editors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.IRecordListener;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.commands.CAPRecordedCommand;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.HibernateUtil;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ObjectMappingManager;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.controllers.dnd.TCEditorDndSupport;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.model.CapGUI;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.SelectionChecker;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;




/**
 * Editor for SpecTestCases
 *
 * @author BREDEX GmbH
 * @created 05.09.2005
 */
@SuppressWarnings("synthetic-access")
public class TestCaseEditor extends AbstractTestCaseEditor 
    implements IRecordListener {
    
    /** the OM manager for this editor */
    private ObjectMappingManager m_objectMappingManager = 
        new ObjectMappingManager();

    /**
     * {@inheritDoc}
     * @param parent
     */
    public void createPartControlImpl(Composite parent) {
        super.createPartControlImpl(parent);
        ActionListener actionListener = new ActionListener();
        getTreeViewer().addSelectionChangedListener(actionListener);
        DecoratingLabelProvider ld = new DecoratingLabelProvider(
                new GeneralLabelProvider(), Plugin.getDefault()
                        .getWorkbench().getDecoratorManager()
                        .getLabelDecorator());
        ld.setDecorationContext(new JBEditorDecorationContext());
        getTreeViewer().setLabelProvider(ld);
        getEventHandlerTreeViewer().addSelectionChangedListener(actionListener);
        if (!Plugin.getDefault().anyDirtyStar()) {
            checkAndRemoveUnusedTestData();
        }
    }

   

    /**
     * when objectmapping exists, then lock OM
     * @param monitor
     *      IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {
        IPersistentObject inputTC = 
            getEditorHelper().getEditSupport().getWorkVersion();
        ISpecTestCasePO recordTC = CAPRecordedCommand.getRecSpecTestCase();
        boolean isStillObserving = CAPRecordedCommand.isObserving();
        if (isStillObserving && inputTC.equals(recordTC)) {
            int returnCode = showSaveInObservModeDialog();
            if (returnCode == Window.OK) {
                TestExecutionContributor.getInstance().
                    getClientTest().resetToTesting();
                DataEventDispatcher.getInstance()
                    .fireRecordModeStateChanged(RecordModeState.notRunning);
                isStillObserving = false;
            }
        }
        
        if (!isStillObserving) {
            try {
                m_objectMappingManager.saveMappings();
                refreshOMProfilesForAUTS();
                removeIncorrectCompNamePairsInExecTcs();
                super.doSave(monitor);
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleGDProjectDeletedException();
            } catch (IncompatibleTypeException ite) {
                Utils.createMessageDialog(ite, 
                        ite.getErrorMessageParams(), null);
            }
        }
    }

    /**
     * Removes incorrect CompNamePairs from all referencing Test Cases for
     * which a lock can be acquired.
     */
    private void removeIncorrectCompNamePairsInExecTcs() {
        // Find all Test Case References in this project that reference this
        // Test Case
        INodePO workVersion = 
            (INodePO)getEditorHelper().getEditSupport().getWorkVersion();
        List<Long> parentProjectIds = new ArrayList<Long>();
        parentProjectIds.add(workVersion.getParentProjectId());
        List<IExecTestCasePO> execTcRefs = 
            NodePM.getExecTestCases(workVersion.getGuid(), parentProjectIds);
        Set<INodePO> lockedNodePOs = 
            new HashSet<INodePO>();
        
        EntityManager editorSession = 
            getEditorHelper().getEditSupport().getSession();
        for (IExecTestCasePO execTc : execTcRefs) {
            try {
                INodePO parentNode = execTc.getParentNode();
                INodePO editorSessionParentNode = 
                    (INodePO)editorSession.find(
                            HibernateUtil.getClass(parentNode), 
                            parentNode.getId());
                
                if (LockManager.instance().lockPO(editorSession, 
                        editorSessionParentNode, true)) {
                    lockedNodePOs.add(editorSessionParentNode);
                }
            } catch (PMDirtyVersionException e) {
                // Unable to successfully acquire lock
                // Pairs for this node will not be updated
                // Do nothing
            } catch (PMObjectDeletedException e) {
                // Unable to successfully acquire lock
                // Pairs for this node will not be updated
                // Do nothing
            }
        }
        
        // Remove incorrect pairs for nodes for which we were able to acquire
        // a lock.
        for (INodePO node : lockedNodePOs) {
            CompNamesBP.removeIncorrectCompNamePairs(node);
        }
    }



    /**
     * Refresh all AUTs for the current project, so that we can avoid 
     * NonUniqueObjectException for same ObjectMappingProfilePO for multiple 
     * AUTs
     */
    private void refreshOMProfilesForAUTS() {
        EntityManager sess = GeneralStorage.getInstance().getMasterSession();
        for (IAUTMainPO aut 
                : GeneralStorage.getInstance().getProject()
                    .getAutMainList()) {
            sess.refresh(aut.getObjMap().getProfile());
        }
    }



    /**
     * {@inheritDoc}
     */
    public String getEditorPrefix() {
        return Messages.PluginTC;
    }
    
    /**
     * {@inheritDoc}
     */
    public void capRecorded(final ICapPO newCap, 
        final IComponentIdentifier ci) {
        
        if (newCap == null) {
            Utils.createMessageDialog(MessageIDs.E_TEST_STEP_NOT_CREATED);
        } else {
            final SpecTestCaseGUI tc = (SpecTestCaseGUI)((GuiNode)
                    getTreeViewer().getInput()).getChildren().get(0);
            final IAUTMainPO recordAut = 
                TestExecution.getInstance().getConnectedAut();

            Plugin.getDisplay().syncExec(new Runnable() {
                public void run() {
                    if (getEditorHelper().requestEditableState() 
                            != JBEditorHelper.EditableState.OK) {
                        return;
                    }
                    final CapGUI capGUI = 
                        new CapGUI(newCap.getName(), tc, newCap);
                    // Cap added to model
                    // recorded action with default mapping not being 
                    // added to objmap
                    if (!ObjectMappingEventDispatcher.
                            getObjMapTransient().existTechnicalName(ci)) {
                        
                        String capComponentName = 
                            m_objectMappingManager.addMapping(
                                recordAut, ci, newCap.getComponentName());

                        newCap.setComponentName(capComponentName);
                    }
                    getTreeViewer().refresh(false);

                    getTreeViewer().setSelection(
                            new StructuredSelection(capGUI), true);
                    getEditorHelper().setDirty(true);
                }
            });
        }
    }
    
    /**
     * SelectionListener to en-/disable delete-action
     * 
     * @author BREDEX GmbH
     * @created 02.03.2006
     */
    private class ActionListener implements ISelectionChangedListener {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked") 
        public void selectionChanged(SelectionChangedEvent event) {
            if (!(event.getSelection() instanceof IStructuredSelection)) {
                return;
            }
            IStructuredSelection sel = 
                (IStructuredSelection)event.getSelection();
            boolean topViewerEnabled = (getCurrentTreeViewer() 
                == getTreeViewer());
            boolean specIsNotInSelList = false;
            if (sel != null && !sel.isEmpty()) {
                int[] counter = SelectionChecker.selectionCounter(sel);
                specIsNotInSelList = 
                    counter[SelectionChecker.SPEC_TESTCASE] == 0;
            }
            getInsertNewTCAction().setEnabled(specIsNotInSelList 
                && topViewerEnabled);
            getAddNewTCAction().setEnabled(topViewerEnabled);
            
            if (GeneralStorage.getInstance().getProject() == null
                    || (sel == null 
                            || sel.isEmpty())) {
                
                getCutTreeItemAction().setEnabled(false);
                getPasteTreeItemAction().setEnabled(false);
            } else {
                List<GuiNode> selList = sel.toList();
                enableCutAction(selList);
                enablePasteAction(selList);
            }
        }
        
        /**
         * en-/disable cut-action
         * @param selList actual selection 
         */
        private void enableCutAction(List<GuiNode> selList) {
            getCutTreeItemAction().setEnabled(true);

            for (GuiNode node : selList) {
                if (!(node instanceof ExecTestCaseGUI
                        || node instanceof CapGUI)) {
                    getCutTreeItemAction().setEnabled(false);
                    return;
                }
            }
        }

        /**
         * en-/disable paste-action
         * @param selList actual selection 
         */
        private void enablePasteAction(List<GuiNode> selList) {
            
            getPasteTreeItemAction().setEnabled(false);
            LocalSelectionClipboardTransfer transfer = 
                LocalSelectionClipboardTransfer.getInstance();
            Object cbContents = 
                getEditorHelper().getClipboard().getContents(transfer);

            if (cbContents == null) {
                return;
            }

            for (GuiNode guiNode : selList) {
                if (guiNode == null
                        || !(cbContents instanceof StructuredSelection)
                        || !TCEditorDndSupport.validateDrop(
                                transfer.getSource(), getTreeViewer(), 
                                transfer.getSelection(), 
                                guiNode, false)) {
                    
                    getPasteTreeItemAction().setEnabled(false);
                    return;
                }
            }

            getPasteTreeItemAction().setEnabled(true);

        }

    }

    /**
     * {@inheritDoc}
     */
    public Image getDisabledTitleImage() {
        return IconConstants.DISABLED_TC_EDITOR_IMAGE;
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.ui.editors.TestCaseEditor#reOpenEditor(org.eclipse.jubula.client.core.model.IPersistentObject)
     */
    public void reOpenEditor(IPersistentObject node) throws PMException {
        m_objectMappingManager.clear();
        ((ISpecTestCasePO)node).setIsReused(null);
        super.reOpenEditor(node);
        ISpecTestCasePO recSpecTc = CAPRecordedCommand.getRecSpecTestCase();
        if (recSpecTc != null && recSpecTc.equals(node)) {
            CAPRecordedCommand.setRecSpecTestCase(
                    (ISpecTestCasePO)getEditorInputGuiNode().getContent());
        }
    }
    
    /**
     * Sets the help to the HelpSystem.
     * @param parent the parent composite to set the help id to
     */
    protected void setHelp(Composite parent) {
        Plugin.getHelpSystem().setHelp(parent,
            ContextHelpIds.JB_SPEC_TESTCASE_EDITOR);        
    }
    
    /**
     * Shows information dialog that savin on observation mode is not allowed
     * @return returnCode of Dialog
     */
    private int showSaveInObservModeDialog() {
        MessageDialog dialog = new MessageDialog(Plugin.getShell(), 
            Messages.SaveInObservationModeDialogTitle,
                null, Messages.SaveInObservationModeDialogQuestion,
                MessageDialog.QUESTION, new String[] {
                    Messages.NewProjectDialogMessageButton0,
                    Messages.NewProjectDialogMessageButton0 }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog.getReturnCode();
    }
}
