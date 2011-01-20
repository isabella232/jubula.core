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
package org.eclipse.jubula.client.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ICompletenessCheckListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ILanguageChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.actions.SearchTreeAction;
import org.eclipse.jubula.client.ui.businessprocess.GuiNodeBP;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.controllers.JubulaStateController;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionTransfer;
import org.eclipse.jubula.client.ui.controllers.dnd.TreeViewerContainerDragSourceListener;
import org.eclipse.jubula.client.ui.editors.TestJobEditor;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.ProjectGUI;
import org.eclipse.jubula.client.ui.model.RefTestSuiteGUI;
import org.eclipse.jubula.client.ui.model.TestJobGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.client.ui.provider.DecoratingCellLabelProvider;
import org.eclipse.jubula.client.ui.provider.contentprovider.TestSuiteBrowserContentProvider;
import org.eclipse.jubula.client.ui.provider.labelprovider.TestSuiteBrowserLabelProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.DisplayableLanguages;
import org.eclipse.jubula.client.ui.utils.NodeSelection;
import org.eclipse.jubula.client.ui.utils.SelectionChecker;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 05.07.2004
 */
@SuppressWarnings("synthetic-access")
public class TestSuiteBrowser extends AbstractJBTreeView implements
    ITreeViewerContainer, IJBPart,
    ILanguageChangedListener, ICompletenessCheckListener {

    /** Identifies the workbench plug-in */
    public static final String OPEN_WITH_ID = PlatformUI.PLUGIN_ID + ".OpenWithSubMenu"; //$NON-NLS-1$
    /** New-menu */
    public static final String NEW_ID = PlatformUI.PLUGIN_ID + ".NewSubMenu"; //$NON-NLS-1$  
    /** Add-Submenu ID */
    public static final String ADD_ID = PlatformUI.PLUGIN_ID + ".AddSubMenu"; //$NON-NLS-1$
    /** standard logging */
    static final Log LOG = LogFactory.getLog(TestSuiteBrowser.class);
    /** flag for initialization state of context menu */
    private boolean m_isContextMenuInitialized = false;
    /** menu manager for context menu */
    private final MenuManager m_mgr = new MenuManager();
    /** menu listener for <code>m_menuMgr</code> */
    private MenuListener m_menuListener = new MenuListener();

    /**
     * Creates the SWT controls for this workbench part.
     * @param parent Composite
     */
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        ColumnViewerToolTipSupport.enableFor(getTreeViewer());
        getTreeViewer().setContentProvider(
                new TestSuiteBrowserContentProvider());
        getTreeViewer().setLabelProvider(new DecoratingCellLabelProvider(
            new TestSuiteBrowserLabelProvider(), Plugin.getDefault()
                .getWorkbench().getDecoratorManager().getLabelDecorator()));
        
        initInitialContext();
        getTreeViewer().setInput(
                Plugin.getDefault().getTestSuiteBrowserRootGUI());
        Plugin.getHelpSystem().setHelp(getTreeViewer().getControl(),
                ContextHelpIds.TEST_SUITE_VIEW);
        JubulaStateController.getInstance()
            .addSelectionListenerToSelectionService();
        
        int ops = DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] {LocalSelectionTransfer
            .getInstance()};
        getTreeViewer().addDragSupport(ops, transfers,
            new TreeViewerContainerDragSourceListener(getTreeViewer()));
        
        m_mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu menu = m_mgr.createContextMenu(getTreeViewer().getControl());
        getTreeViewer().getControl().setMenu(menu);
        getViewSite().registerContextMenu(m_mgr, getTreeViewer());
        m_mgr.addMenuListener(m_menuListener);
        // Register menu for extension.
        DataEventDispatcher.getInstance().addLanguageChangedListener(this, 
            true);
        DataEventDispatcher.getInstance().addCompletenessCheckListener(this);
        getTreeViewer().setAutoExpandLevel(DEFAULT_EXPANSION + 1);
        if (GeneralStorage.getInstance().getProject() != null) {
            handleProjectLoaded();
        }
    }

    /**
     * the initial model
     */
    private void initInitialContext() {
        if (Plugin.getDefault().getTestSuiteBrowserRootGUI() == null) {
            Plugin.getDefault().setTestSuiteBrowserRootGUI(
                    new TestSuiteGUI("TestExView_Root")); //$NON-NLS-1$   
        }
    }

    /**
     * Adds DoubleClickListener to Treeview.
     */
    protected void addTreeListener() {
        getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = getSuiteTreeSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof TestSuiteGUI) {
                    TestSuiteGUI tsGUI = (TestSuiteGUI)firstElement;
                    if (((ITestSuitePO)tsGUI.getContent()).isEditable()) {
                        runCommand(CommandIDs.OPEN_TESTSUITE_EDITOR_COMMAND_ID);
                    }
                } else if (firstElement instanceof TestJobGUI) {
                    TestJobGUI tsGUI = (TestJobGUI)firstElement;
                    if (((ITestJobPO)tsGUI.getContent()).isEditable()) {
                        runCommand(CommandIDs.OPEN_TESTJOB_EDITOR_COMMAND_ID);
                    }
                } else if (firstElement instanceof ProjectGUI) {
                    runCommand(CommandIDs
                            .OPEN_CENTRAL_TESTDATA_EDITOR_COMMAND_ID);
                } else if (firstElement instanceof CategoryGUI) {
                    String categoryName = ((CategoryGUI)firstElement).getName();
                    if (categoryName.equals(TreeBuilder.TS_CAT_NAME)) {
                        runCommand(CommandIDs.NEW_TESTSUITE_COMMAND_ID);
                    } else if (categoryName.equals(TreeBuilder.TJ_CAT_NAME)) {
                        runCommand(CommandIDs.NEW_TESTJOB_COMMAND_ID);
                    }
                } else {
                    try {
                        getSite().getPage().showView(Constants.PROPVIEW_ID,
                                null, IWorkbenchPage.VIEW_VISIBLE);
                    } catch (PartInitException e) {
                        LOG.error("unable to open GDPropertiesView!", e); //$NON-NLS-1$
                    }
                }
            }
            
            /**
             * runs the given command
             * 
             * @param commandID the commandId to execute
             */
            private void runCommand(String commandID) {
                CommandHelper.executeCommand(commandID, getSite());
            }
        });
    }

    /**
     * Create context menu.
     * @param mgr current menu manager
     */
    private void createContextMenu(IMenuManager mgr) {
        if (!m_isContextMenuInitialized) {
            MenuManager submenuNew = new MenuManager(I18n.getString("TestSuiteBrowser.New"), NEW_ID); //$NON-NLS-1$
            MenuManager submenuOpenWith = new MenuManager(I18n.getString("TestSuiteBrowser.OpenWith"), OPEN_WITH_ID); //$NON-NLS-1$
            CommandHelper.createContributionPushItem(submenuNew,
                    CommandIDs.NEW_TESTSUITE_COMMAND_ID);
            CommandHelper.createContributionPushItem(submenuNew,
                    CommandIDs.NEW_TESTJOB_COMMAND_ID);
            CommandHelper.createContributionPushItem(submenuOpenWith,
                    CommandIDs.OPEN_TESTJOB_EDITOR_COMMAND_ID);
            CommandHelper.createContributionPushItem(submenuOpenWith,
                    CommandIDs.OPEN_TESTSUITE_EDITOR_COMMAND_ID);
            CommandHelper.createContributionPushItem(submenuOpenWith,
                    CommandIDs.OPEN_OBJECTMAPPING_EDITOR_COMMAND_ID);
            CommandHelper.createContributionPushItem(submenuOpenWith,
                    CommandIDs.OPEN_CENTRAL_TESTDATA_EDITOR_COMMAND_ID);
            mgr.add(submenuNew);
            mgr.add(new Separator());
            CommandHelper.createContributionPushItem(mgr,
                    CommandIDs.RENAME_COMMAND_ID);
            mgr.add(SearchTreeAction.getAction());
            CommandHelper.createContributionPushItem(mgr,
                    CommandIDs.DELETE_COMMAND_ID);
            CommandHelper.createContributionPushItem(mgr,
                    CommandIDs.OPEN_SPECIFICATION_COMMAND_ID);
            CommandHelper.createContributionPushItem(mgr,
                    CommandIDs.SHOW_SPECIFICATION_COMMAND_ID);
            CommandHelper.createContributionPushItem(mgr,
                    CommandIDs.EXPAND_TREE_ITEM_COMMAND_ID);
            mgr.add(new Separator());
            CommandHelper.createContributionPushItem(mgr,
                    CommandIDs.REFRESH_COMMAND_ID);
            mgr.add(new Separator());
            mgr.add(submenuOpenWith);
            mgr.add(new Separator());
            CommandHelper.createContributionPushItem(mgr,
                    CommandIDs.PROJECT_PROPERTIES_COMMAND_ID);
            m_isContextMenuInitialized = true;
        }
    }


    /**
     * Fills the context menu, if there is any selection in this view. 
     * @param mgr IMenuManager
     */
    protected void fillContextMenu(IMenuManager mgr) {
        if (!m_isContextMenuInitialized) {
            createContextMenu(mgr);
        }
        TestSuiteGUI tsGUI = null;
        IStructuredSelection selection = getSuiteTreeSelection();
        // Action enabling happens here, because GDStateController cant handle OpenActions
        ITestSuitePO tsContent = null;
        if (selection.size() > 0) {
            int[] counter = SelectionChecker.selectionCounter(selection);
            boolean enabled = !(counter[SelectionChecker.PROJECT] < 1);
            if (counter[SelectionChecker.EXEC_TESTSUITE] == 1 
                && selection.getFirstElement() instanceof TestSuiteGUI) {
                
                tsGUI = (TestSuiteGUI)selection.getFirstElement();
                tsContent = (ITestSuitePO)tsGUI.getContent();                
                enabled = (tsContent).isEditable() && !enabled;
            } 
        }
    }

    /**
     * @return the selected tree item
     */
    public IStructuredSelection getSuiteTreeSelection() {
        return (getTreeViewer().getSelection() instanceof IStructuredSelection)
            ? (IStructuredSelection)getTreeViewer().getSelection()
                    : StructuredSelection.EMPTY;
    }

    /**
     * Asks this part to take focus within the workbench.
     */
    public void setFocus() {
        getTreeViewer().getControl().setFocus();
        Plugin.showStatusLine(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        try {
            JubulaStateController.getInstance()
                .removeSelectionListenerFromSelectionService();
        } finally {
            m_mgr.removeMenuListener(m_menuListener);
            DataEventDispatcher.getInstance().removeDataChangedListener(this);
            DataEventDispatcher.getInstance()
                .removeLanguageChangedListener(this);
            DataEventDispatcher.getInstance()
                .removeCompletenessCheckListener(this);
            super.dispose();
        }
    }
  
    /**
     * @author BREDEX GmbH
     * @created Jan 22, 2007
     */
    private final class MenuListener implements IMenuListener {
        /**
         * {@inheritDoc}
         */
        public void menuAboutToShow(IMenuManager imgr) {
            fillContextMenu(imgr);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void rebuildTree() {
        TreeBuilder.buildTestSuiteBrowserTree(
            GeneralStorage.getInstance().getProject());
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                getTreeViewer().setInput(Plugin.getDefault()
                    .getTestSuiteBrowserRootGUI());
                getTreeViewer().expandToLevel(DEFAULT_EXPANSION + 1);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void handleDataChanged(final IPersistentObject po, 
        final DataState dataState, final UpdateState updateState) {
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                // due to checkstyle BUG this indirection is necessary
                handleDataChangedImpl(po, dataState, updateState);
            }
        });
    }

    /**
     * @param po
     *            the PO
     * @param dataState
     *            the data state
     * @param updateState
     *            the update state
     */
    private void handleDataChangedImpl(final IPersistentObject po,
            final DataState dataState, final UpdateState updateState) {
        // changes on the aut do not affect structure of this view
        if (po instanceof IAUTMainPO) {
            getTreeViewer().refresh();
            return;
        }
        if (updateState == UpdateState.onlyInEditor) {
            return;
        }
        final GuiNode root = (GuiNode)getTreeViewer().getInput();
        switch (dataState) {
            case Added:
                handleDataAdded(po, root);
                break;
            case Deleted:
                handleDataDeleted(po, root);
                break;
            case Renamed:
                if ((po instanceof IProjectPO 
                        || po instanceof ITestSuitePO
                        || po instanceof ITestJobPO 
                        || po instanceof ITestCasePO)
                        && countNodePOsInTree((INodePO)po) > 0) {
                    getTreeViewer().refresh();
                }
                break;
            case StructureModified:
                if (po instanceof IProjectPO) {
                    handleProjectLoaded();
                }
                if ((po instanceof ISpecTestCasePO)
                        || (po instanceof ITestSuitePO)
                        || (po instanceof ITestJobPO)) {

                    // get old expand status
                    List<NodeSelection> selElemList = Utils
                            .getSelectedTreeItems(getTreeViewer());
                    List<Object> expElemList = Utils
                            .getExpandedTreeItems(getTreeViewer());

                    // update elements
                    GuiNodeBP.rebuildBrowserGuiNode(root, (INodePO)po);

                    // refresh treeview
                    getTreeViewer().refresh();
                    // restore expand status
                    Utils.restoreTreeState(getTreeViewer(), expElemList,
                            selElemList);
                }
                if (po instanceof IObjectMappingPO) {
                    getTreeViewer().refresh();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * @param po The persistent object that was deleted
     * @param root The root of the GUI tree
     */
    private void handleDataDeleted(final IPersistentObject po, 
        final GuiNode root) {
        
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                if (po instanceof ITestSuitePO
                        || po instanceof ITestJobPO) {
                    GuiNodeBP.deleteGuiNode(root, (INodePO)po);
                    getTreeViewer().refresh();
                    getTreeViewer().setSelection(
                        new StructuredSelection(
                            getRootGuiNode().getChildren().get(0)));
                } else if (po instanceof IProjectPO) {
                    getTreeViewer().setInput(null);
                    getTreeViewer().refresh();
                }
            }
        });
    }

    /**
     * @param po
     *            The persistent object that was added
     * @param root
     *            The root of the GUI tree
     */
    private void handleDataAdded(IPersistentObject po, GuiNode root) {
        if ((po instanceof ISpecTestCasePO) || (po instanceof ICategoryPO)) {
            return;
        }
        GuiNode testSuiteBrowserRoot = root.getChildren().get(0);
        GuiNode rootChild = null;
        if (po instanceof ITestJobPO) {
            rootChild = testSuiteBrowserRoot.getChildren().get(1);
        } else if (po instanceof ITestSuitePO) {
            rootChild = testSuiteBrowserRoot.getChildren().get(0);
        }
        if (rootChild != null) {
            GuiNode newNode = GuiNodeBP.addGUINodeInTestSuiteBrowser(rootChild,
                    (INodePO)po, null, new NullProgressMonitor());
            getTreeViewer().refresh();
            getTreeViewer()
                    .setSelection(new StructuredSelection(newNode), true);
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
        GuiNode selNode = (GuiNode)((IStructuredSelection)
              getTreeViewer().getSelection()).getFirstElement();
        TestSuiteGUI tsGUI = GuiNodeBP.getTestSuiteOfNode(selNode);
        INodePO content = tsGUI.getContent();
        if (content instanceof ITestSuitePO) {
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

        List<Locale> emptyList = Collections.emptyList();
        return new DisplayableLanguages(emptyList);
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleLanguageChanged(Locale locale) {
        getTreeViewer().refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void completenessCheckFinished() {
        getTreeViewer().getTree().getDisplay().syncExec(new Runnable() {
            public void run() {
                getTreeViewer().refresh();
                IDecoratorManager dm = 
                    Plugin.getDefault().getWorkbench().getDecoratorManager();
                dm.update(Constants.TESTDATA_DECORATOR_ID);
            }
        });
    }

    /**
     * Adds the given test suite to the selected test job as a reference. This
     * method is only allowed from an editor context since it relies on the
     * Session provided by the editors EditSupport.
     * @param ts the test suite to reference.
     * @param tj the target TestJob
     * @param position the position to insert. If null, the position is 
     * @return the referenced test suite.
     * @throws PMReadException in case of db read error
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     */
    public GuiNode addReferencedTestSuite(ITestSuitePO ts, INodePO tj,
            int position) throws PMReadException, PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        RefTestSuiteGUI exTcGUI = null;
        ITestSuitePO workTs = null;
        workTs = createWorkVersionofTs(ts);
        if (workTs != null) {
            IRefTestSuitePO refTs = TestSuiteBP.addReferencedTestSuite(
                    getEditSupport(), tj, workTs, position);
            DataEventDispatcher.getInstance().fireDataChangedListener(refTs,
                    DataState.Added, UpdateState.onlyInEditor);
        }
        return exTcGUI;
    }
    
    /**
     * @return the EditSupport for the current active editor. This methods
     * throws a GDFatalExecption if called with no IGDEditor subclass active.
     */
    private EditSupport getEditSupport() {
        TestJobEditor edit = getTJEditor();            
        EditSupport editSupport = edit.getEditorHelper().getEditSupport();
        return editSupport;
    }
    
    /**
     * @return the actual active TJ editor
     */
    private TestJobEditor getTJEditor() {
        TestJobEditor edit = Plugin.getDefault().getActiveTJEditor();
        if (edit == null) {
            String msg = "no active TC editor, please fix the method"; //$NON-NLS-1$
            LOG.fatal(msg); 
            throw new JBFatalException(msg, MessageIDs.E_NO_OPENED_EDITOR);
        }
        return edit;
    }
    
    /**
     * get the WorkVerstion to origTs
     * @param origTs original specTc
     * @return workorigTs or null
     * @throws PMReadException in case of db read error
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     */
    private ITestSuitePO createWorkVersionofTs(
            ITestSuitePO origTs) throws PMReadException, 
            PMAlreadyLockedException, PMDirtyVersionException, PMException {
        ITestSuitePO workTs = null;
        EditSupport editSupport = getEditSupport();
        workTs = (ITestSuitePO)editSupport.createWorkVersion(origTs);
        return workTs;
    }    

}