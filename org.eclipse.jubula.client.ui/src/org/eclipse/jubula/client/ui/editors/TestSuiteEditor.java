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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.GuiNodeBP;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.dnd.GuiNodeViewerDropAdapter;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionTransfer;
import org.eclipse.jubula.client.ui.controllers.dnd.TSEditorDndSupport;
import org.eclipse.jubula.client.ui.controllers.dnd.TreeViewerContainerDragSourceListener;
import org.eclipse.jubula.client.ui.model.CapGUI;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.TestCaseBrowserRootGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.client.ui.provider.contentprovider.TestCaseEditorContentProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.DisplayableLanguages;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.views.TreeBuilder;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;


/**
 * Editor for ExecTestCases
 *
 * @author BREDEX GmbH
 * @created 05.09.2005
 */
public class TestSuiteEditor extends AbstractTestCaseEditor {
    /**
     * <code>EXEC_TC_ED_ROOT_NAME</code>
     */
    public static final String EXEC_TC_ED_ROOT_NAME = "ExecTCEd_root"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * @param parent
     */
    public void createPartControlImpl(Composite parent) {
        super.createPartControlImpl(parent);
        ActionListener actionListener = new ActionListener();
        getTreeViewer().addSelectionChangedListener(actionListener);
        addTreeListener();
        if (!Plugin.getDefault().anyDirtyStar())  {
            checkAndRemoveUnusedTestData();
        }
    }

    /**
     * Adds DoubleClickListener to Treeview.
     */
    private void addTreeListener() {
        getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                CommandHelper.executeCommand(
                        CommandIDs.REFERENCE_TC_COMMAND_ID, getSite());
            }
        });
    }

    /**
     * Sets the input of the tree viewer for specificaion.
     */
    public void setInitialInput() {
        GuiNode root = new TestCaseBrowserRootGUI(EXEC_TC_ED_ROOT_NAME);
        ITestSuitePO rootPO = 
            (ITestSuitePO)getEditorHelper().getEditSupport().getWorkVersion();
        TreeBuilder.buildTestSuiteEditorTree(rootPO, root);
        getTreeViewer().setContentProvider(
            new TestCaseEditorContentProvider());
        initTopTreeViewer(root);
    }
    
    /**
     * adds Drag and Drop support for the trees.
     */
    protected void addDragAndDropSupport() {
        int ops = DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] { 
                LocalSelectionTransfer.getInstance()};
        getTreeViewer().addDragSupport(ops, transfers,
            new TreeViewerContainerDragSourceListener(getTreeViewer()));
        getTreeViewer().addDropSupport(ops, transfers, 
            new  TSEditorDropTargetListener(this)); 
    }
    
    /**
     * Checks, if all fields were filled in correctly.
     * @return True, if all fields were filled in correctly. 
     */
    protected boolean checkCompleteness() {
        ITestSuitePO tsWorkVersion = 
            (ITestSuitePO)getEditorHelper().getEditSupport().getWorkVersion();
        if (!checkWorkingLanguage(tsWorkVersion)) {
            Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR, null, 
                    new String[]{I18n.getString("TestCaseEditor.unsupportedAUTLanguage")}); //$NON-NLS-1$
            return false;
        }
        if (tsWorkVersion.getName() == null
                || StringConstants.EMPTY.equals(tsWorkVersion.getName())) {
            Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR, null, 
                    new String[]{I18n.getString("TestCaseEditor.noTsuiteName")}); //$NON-NLS-1$
            return false;
        } 
        if (tsWorkVersion.getName().startsWith(BLANK) 
            || tsWorkVersion.getName().endsWith(BLANK)) { 
            
            Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR, null, 
                    new String[]{I18n.getString("TestCaseEditor.wrongTsuiteName")});  //$NON-NLS-1$
            return false;
        }
        final IProjectPO project = GeneralStorage.getInstance().
            getProject();

        if (!tsWorkVersion.getName().equals(
                getEditorHelper().getEditSupport().getOriginal().getName())
            && ProjectPM.doesTestSuiteExists(project.getId(), 
                tsWorkVersion.getName())) {
            Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR, null,
                new String[]{I18n.getString("TestCaseEditor.doubleTsuiteName")});  //$NON-NLS-1$
            return false;
        }
        if (tsWorkVersion.getStepDelay() == -1) { // empty step delay
            Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR, null, 
                    new String[]{I18n.getString("TestSuiteEditor.EmptyStepDelay")}); //$NON-NLS-1$
            return false;
        }
        Iterator iter = tsWorkVersion.getNodeListIterator();
        while (iter.hasNext()) {
            INodePO child = (INodePO)iter.next();
            if (Hibernator.isPoSubclass(child, IExecTestCasePO.class)) {
                IExecTestCasePO execTC = (IExecTestCasePO)child;
                if (!checkExecTCCompleteness(execTC)) {
                    
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param tsWorkVersion the actual workversion
     * @return true, current working language is supported by the current AUT
     */
    private boolean checkWorkingLanguage(ITestSuitePO tsWorkVersion) {
        Locale workLang = WorkingLanguageBP.getInstance()
            .getWorkingLanguage();
        if (tsWorkVersion.getAut() != null) {
            for (IAUTMainPO aut 
                : GeneralStorage.getInstance().getProject().getAutMainList()) {
                
                if (aut.equals(tsWorkVersion.getAut())) {
                    for (Locale autLang : aut.getLangHelper()
                            .getLanguageList()) {
                        
                        if (workLang.equals(autLang)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Checks, if all fields were filled in correctly.
     * @return True, if all fields were filled in correctly.
     * @param testCase the checked testCase.
     */
    private boolean checkExecTCCompleteness(IExecTestCasePO testCase) {
        
        Object[] tcName = new Object[]{testCase.getName()};
        String name = testCase.getName();
        if (name == null || StringConstants.EMPTY.equals(name)) {
            Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX, 
                    tcName, new String[]{I18n.getString("TestCaseEditor.noExecTcName")}); //$NON-NLS-1$
            return false;
        } 
        if (testCase.getName().startsWith(BLANK) 
            || testCase.getName().endsWith(BLANK)) { 
            
            Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX, 
                    tcName, new String[]{I18n.getString("TestCaseEditor.wrongExecTcName")});  //$NON-NLS-1$
            return false;
        }
        for (ICompNamesPairPO compNamesPair : testCase.getCompNamesPairs()) {
            if (compNamesPair.getSecondName().equals(
                StringConstants.EMPTY)) {
                
                Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX,
                        tcName, new String[]{I18n.getString("TestCaseEditor.CompNameError", //$NON-NLS-1$
                                new Object[]{compNamesPair.getFirstName()}) 
                            + I18n.getString("TestCaseEditor.EmptyCompName")});  //$NON-NLS-1$
                return false;
            }
            if (compNamesPair.getSecondName().startsWith(BLANK) 
                || compNamesPair.getSecondName().endsWith(BLANK)) { 
                Utils.createMessageDialog(MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX,
                        tcName, new String[]{I18n.getString("TestCaseEditor.CompNameError", //$NON-NLS-1$
                                new Object[]{compNamesPair.getFirstName()}) 
                            + I18n.getString("TestCaseEditor.WrongCompName")});  //$NON-NLS-1$
                return false;
            }
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getEditorPrefix() {
        return I18n.getString("Plugin.TS"); //$NON-NLS-1$
    }
    
    /**
     * {@inheritDoc}
     */
    protected void fillContextMenu(IMenuManager mgr) {
        IStructuredSelection selection = getCurrentSelection();
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        if (selection.getFirstElement() == null) {
            return;
        }
        MenuManager submenuAdd = new MenuManager(I18n
            .getString("TestSuiteBrowser.Add"), ADD_ID); //$NON-NLS-1$
        MenuManager submenuRefactor = new MenuManager(
            I18n.getString("TestCaseEditor.Refactor"), REFACTOR_ID); //$NON-NLS-1$
        MenuManager submenuInsert = new MenuManager(I18n.getString("TestSuiteEditor.Insert"), INSERT_ID); //$NON-NLS-1$
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.REFERENCE_TC_COMMAND_ID);
        mgr.add(submenuAdd);
        mgr.add(submenuInsert);
        mgr.add(submenuRefactor);
        CommandHelper.createContributionPushItem(submenuRefactor,
                CommandIDs.EXTRACT_TESTCASE_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.REVERT_CHANGES_COMMAND_ID);
        mgr.add(new Separator());
        mgr.add(getCutTreeItemAction());
        mgr.add(getPasteTreeItemAction());
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.TOGGLE_ACTIVE_STATE_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.DELETE_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.SHOW_WHERE_USED_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.OPEN_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.SHOW_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.EXPAND_TREE_ITEM_COMMAND_ID);
    }
    
    /**
     * Drop adapter for this TestSuiteEditor
     * @author BREDEX GmbH
     * @created 24.10.2005
     */
    public class TSEditorDropTargetListener extends GuiNodeViewerDropAdapter {

        /** <code>m_editor</code> */
        private AbstractTestCaseEditor m_editor;
        
        /**
         * @param editor the TestCaseEditor.
         */
        public TSEditorDropTargetListener(AbstractTestCaseEditor editor) {
            super(editor.getTreeViewer());
            m_editor = editor;
            boolean scrollExpand = Plugin.getDefault().getPreferenceStore().
                getBoolean(Constants.TREEAUTOSCROLL_KEY);
            setScrollExpandEnabled(scrollExpand);
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean performDrop(Object data) {
            LocalSelectionTransfer transfer = 
                LocalSelectionTransfer.getInstance();
            Object target = getCurrentTarget();
            int location = getCurrentLocation();
            if (target == null) {
                target = getFallbackTarget(getTreeViewer());
                location = ViewerDropAdapter.LOCATION_AFTER;
            }
            if (target instanceof GuiNode) {
                GuiNode targetGuiNode = (GuiNode)target;
                IStructuredSelection toDrop = transfer.getSelection();
                return TSEditorDndSupport.performDrop(m_editor, toDrop,
                        targetGuiNode, location);
                
            }

            return false;
        }

        /**
         * {@inheritDoc}
         */
        public boolean validateDrop(Object target, int operation,
                TransferData transferType) {
            LocalSelectionTransfer transfer = LocalSelectionTransfer
                    .getInstance();
            Object targetNode = target;
            if (targetNode == null) {
                targetNode = getFallbackTarget(getTreeViewer());
            }

            return TSEditorDndSupport.validateDrop(transfer.getSource(),
                    m_editor.getTreeViewer(), transfer.getSelection(),
                    targetNode, true);

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
            
            getPasteTreeItemAction().setEnabled(true);
            LocalSelectionClipboardTransfer transfer = 
                LocalSelectionClipboardTransfer.getInstance();
            Object cbContents = 
                getEditorHelper().getClipboard().getContents(transfer);

            if (cbContents instanceof IStructuredSelection) {
                IStructuredSelection cbSelection = 
                    (IStructuredSelection)cbContents;
                for (GuiNode guiNode : selList) {
                    if (guiNode == null
                            || !(cbContents instanceof StructuredSelection)
                            || !TSEditorDndSupport.validateDrop(
                                    transfer.getSource(), 
                                    TestSuiteEditor.this.getTreeViewer(),
                                    cbSelection, guiNode, false)) {
                        
                        getPasteTreeItemAction().setEnabled(false);
                        return;
                    }
                }
            } else {
                getPasteTreeItemAction().setEnabled(false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (adapter == DisplayableLanguages.class) {
            return getDisplayableLanguages();
        }
        return super.getAdapter(adapter);
    }

    /**
     * @return the displayable Languages
     */
    private DisplayableLanguages getDisplayableLanguages() {
        if (!(getTreeViewer().getSelection() instanceof IStructuredSelection)) {
            return new DisplayableLanguages(new ArrayList<Locale>());
        }
        GuiNode selNode = (GuiNode)((IStructuredSelection)getTreeViewer()
                .getSelection()).getFirstElement();
        GuiNodeBP.getTestSuiteOfNode(selNode);
        TestSuiteGUI tsGUI = GuiNodeBP.getTestSuiteOfNode(selNode);
        ITestSuitePO ts = (ITestSuitePO)tsGUI.getContent();
        List<Locale> langList = WorkingLanguageBP.getInstance()
            .getLanguages(ts.getAut());
        if (langList.size() > 0) {
            return new DisplayableLanguages(langList);
        }
        langList = new ArrayList<Locale>(1);  
        langList.add(GeneralStorage.getInstance().getProject()
            .getDefaultLanguage());
        return new DisplayableLanguages(langList);
    }
    
    /**
     * {@inheritDoc}
     */
    public Image getDisabledTitleImage() {
        return IconConstants.DISABLED_TS_EDITOR_IMAGE;
    }
    
    /**
     * Sets the help to the HelpSystem.
     * @param parent the parent composite to set the help id to
     */
    protected void setHelp(Composite parent) {
        Plugin.getHelpSystem().setHelp(parent, 
            ContextHelpIds.TEST_SUITE_EDITOR);     
    }

}