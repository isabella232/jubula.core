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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.ITestResultEventListener;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.actions.SearchTreeAction;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.controllers.JubulaStateController;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.provider.contentprovider.TestResultTreeViewContentProvider;
import org.eclipse.jubula.client.ui.provider.labelprovider.TestResultTreeViewLabelProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;


/**
 * @author BREDEX GmbH
 * @created 11.10.2004
 */
public class TestResultTreeView extends ViewPart 
    implements ITreeViewerContainer, IJBPart, IDataChangedListener, 
    ITestResultEventListener, ITestExecutionEventListener, IAdaptable,
    IProjectLoadedListener {
    
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;     
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 3;    
    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 2;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 2;


    /** TreeViewer */
    private TreeViewer m_treeViewer;
    /** the parent composite */
    private Composite m_parentComposite;

    /**
     * The default constructor.
     */
    public TestResultTreeView() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (adapter.equals(IPropertySheetPage.class)) {
            return new JBPropertiesView(false, null);
        }
        return super.getAdapter(adapter);
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        m_parentComposite = parent;
        GridLayout layout = new GridLayout();
        layout.numColumns = NUM_COLUMNS_1;
        layout.verticalSpacing = VERTICAL_SPACING;
        layout.marginWidth = MARGIN_WIDTH;
        layout.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(layout);

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compLayout = new GridLayout(NUM_COLUMNS_1, false);
        compLayout.marginWidth = 0;
        compLayout.marginHeight = 0;
        composite.setLayout(compLayout);
        GridData gridData = 
            new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
        composite.setLayoutData(gridData);
        
        setTreeViewer(new TreeViewer(composite));
        getTreeViewer().setContentProvider(
                new TestResultTreeViewContentProvider());
        getTreeViewer().setLabelProvider(new DecoratingLabelProvider(
                new TestResultTreeViewLabelProvider(), Plugin.getDefault()
                    .getWorkbench().getDecoratorManager().getLabelDecorator()));
        
        ClientTestFactory.getClientTest().addTestExecutionEventListener(this);
        getTreeViewer().setUseHashlookup(true);
        getTreeViewer().setInput(getInput());
        getTreeViewer().expandToLevel(0);
        addTreeListener();
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.verticalAlignment = GridData.FILL;
        Plugin.getHelpSystem().setHelp(getTreeViewer().getControl(),
            ContextHelpIds.RESULT_TREE_VIEW);
        getTreeViewer().getControl().setLayoutData(layoutData);
        getSite().setSelectionProvider(getTreeViewer());
        JubulaStateController.getInstance().
            addSelectionListenerToSelectionService();
        createContextMenu();
        DataEventDispatcher.getInstance().addDataChangedListener(this, true);
        DataEventDispatcher.getInstance().addProjectLoadedListener(this, 
            true);
    }
    
    /**
     * Create context menu.
     */
    private void createContextMenu() {
        // Create menu manager.
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr);
            }
        });
        // Create menu.
        Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
        getTreeViewer().getControl().setMenu(menu);
        // Register menu for extension.
        getViewSite().registerContextMenu(menuMgr, getTreeViewer());
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        ClientTestFactory.getClientTest()
            .removeTestExecutionEventListener(this);
        DataEventDispatcher.getInstance().removeProjectLoadedListener(this);
        DataEventDispatcher.getInstance().removeDataChangedListener(this);
        getSite().setSelectionProvider(null);
        super.dispose();
    }

    /**
     * Fills the context menu.
     * @param mgr IMenuManager
     */
    protected void fillContextMenu(IMenuManager mgr) {
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        mgr.add(SearchTreeAction.getAction());
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.OPEN_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.SHOW_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.EXPAND_TREE_ITEM_COMMAND_ID);
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        getTreeViewer().getControl().setFocus();
        Plugin.showStatusLine(this);
    }

   
    /**
     * @return The parent composite of this workbench part.
     */
    public Composite getParentComposite() {
        return m_parentComposite;
    }
    
    
    /**
     * Clears the view/tree.
     */
    public void clear() {
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                getTreeViewer().setInput(null);
                getTreeViewer().refresh();
            }
        });
    }
    
    /**
     * refreshes the content, if needed
     */
    public void checkContent() {
        if (getTreeViewer().getInput() != null
            && !((TestResultNode)getTreeViewer().getInput()).
                equals(TestResultBP.getInstance().getResultTestModel())) {
            getTreeViewer().setInput(getInput());
            getTreeViewer().refresh();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void stateChanged(TestExecutionEvent event) {
        if (event.getState() == TestExecutionEvent.
                TEST_EXEC_RESULT_TREE_READY) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    if (!getTreeViewer().getControl().isDisposed()) { 
                        TestResult oldInput = ((TestResult) 
                                getTreeViewer().getInput());
                        if (oldInput != null) {
                            removeListenerFrom(oldInput.getRootResultNode());
                        }
                        getTreeViewer().getTree().setRedraw(false);
                        getTreeViewer().setInput(getInput());
                        TestResult newInput = ((TestResult) 
                                getTreeViewer().getInput());
                        if (newInput != null) {
                            addListenerTo(newInput.getRootResultNode());
                        } else {
                            return;
                        }
                        getTreeViewer().expandToLevel(1);
                        if (Plugin.getDefault().getPreferenceStore()
                            .getBoolean(Constants.TRACKRESULTS_KEY)) {
                            
                            TestResult input = 
                                (TestResult)getTreeViewer().getInput();
                            if (input != null) {
                                ISelection selection = new StructuredSelection(
                                    input.getRootResultNode());
                                getTreeViewer().setSelection(selection);
                            }
                        }
                        getTreeViewer().getTree().setRedraw(true);
                    }
                }
            });
        } 
    }

    /**
     * the initial model
     * @return TestResultNode rootNode
     */
    protected TestResult getInput() {
        return TestResultBP.getInstance().getResultTestModel();
    }
    
    /**
     * should be called when a test result is changed
     * @param resultNode The added event
     */
    public void testResultChanged(final TestResultNode resultNode) {
        if (!getTreeViewer().getControl().isDisposed()) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    refreshNode(resultNode);
                    if (resultNode != null
                        && (Plugin.getDefault().getPreferenceStore().getBoolean(
                            Constants.TRACKRESULTS_KEY)
                            || Hibernator.isPoSubclass(resultNode.getNode(),
                                ITestSuitePO.class))) {

                        getTreeViewer().reveal(resultNode);
                    }
                }
            });
        }        
    }

    /**
     * refreshes a Node
     * @param findNode TestResultNodeGUI
     */
    protected void refreshNode(final TestResultNode findNode) {
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
            Constants.TRACKRESULTS_KEY)) {
            
            getTreeViewer().expandToLevel(findNode, 1);
        }
        getTreeViewer().refresh(findNode);
    }

    /**
     * Adds a Test Result.
     * @param parent TestResultNode
     * @param index int
     * @param newNode TestResultNode
     */
    public void testResultNodeUpdated(final TestResultNode parent,
            final int index, final TestResultNode newNode) {
        
        addListenerTo(((TestResultNode)getTreeViewer().getInput()));
        if (!getTreeViewer().getControl().isDisposed()) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    // Refreshing the parent allows Event Handlers to be 
                    // displayed as they are being executed, rather than having
                    // to wait until the end of the test.
                    if (parent != null) {
                        refreshNode(parent);
                    }
                    refreshNode(newNode);
                }
            });
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    public void endTestExecution() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (!getTreeViewer().getControl().isDisposed()) { 
                    if (getTreeViewer().getInput() != null) {
                        ISelection selection = 
                            new StructuredSelection(((TestResult)
                                    getTreeViewer().getInput())
                                        .getRootResultNode());
                        getTreeViewer().setSelection(null);
                        getTreeViewer().setSelection(selection);
                    }
                    getTreeViewer().refresh();
                }
            }
        });
    }
    
    /**
     * adds listener to all resultObjects in resultTestSuite
     * @param resultNode the resultNode object to add listener
     */
    void addListenerTo(TestResultNode resultNode) {
        List resultNodeList = resultNode.getResultNodeList();
        Iterator it = resultNodeList.iterator();
        while (it.hasNext()) {
            TestResultNode resNode = (TestResultNode)it.next();
            resNode.addTestResultChangedListener(this);
            addListenerTo(resNode);
        }
    }       
           
    /**
     * Removes the listener from all resultObjects in resultTestSuite
     * @param resultNode the resultNode object to remove listener from
     */
    public void removeListenerFrom(TestResultNode resultNode) {
        List resultNodeList = resultNode.getResultNodeList();
        Iterator it = resultNodeList.iterator();
        while (it.hasNext()) {
            TestResultNode resTest = (TestResultNode)it.next();
            resTest.removeTestResultChangedListener(this);
            removeListenerFrom(resTest);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void handleDataChanged(final IPersistentObject po, 
        final DataState dataState, 
        UpdateState updateState) {
        
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                switch (dataState) {
                    case Added:
                        if (po instanceof IProjectPO) {
                            clear();
                        }
                        break;
                    case Deleted:
                        if (po instanceof IProjectPO) {
                            IProjectPO project = 
                                GeneralStorage.getInstance().getProject();
                            if (project != null && project.equals(po)) {
                                clear();
                            }
                        }
                        break;
                    case Renamed:
                        break;
                    case StructureModified:
                        break;
                    default:
                        break;
                }
            }
        });
    }    
    
    /**
     * Adds DoubleClick-Support to Treeview. Adds SelectionChanged-Support to
     * TreeView.
     */
    private void addTreeListener() {
        getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {
            @SuppressWarnings("synthetic-access") 
            public void doubleClick(DoubleClickEvent event) {
                handleDClick();
            }
        });
    }  

    /**
     * handles a DClick on the tree
     */
    private void handleDClick() {
        CommandHelper.executeCommand(CommandIDs.SHOW_SPECIFICATION_COMMAND_ID,
                getSite());
    }
    
    /**
     * recursivly find the SpecTestCase to an ExecTestCase and shows it in Specification View
     * @param spec the SpecTestCase you are looking for
     * @param current the current GuiNode
     * @return GuiNode  
     */
    private GuiNode recursivlyfindNode(INodePO spec, GuiNode current) {
        if (current == null) {
            // GuiNode does not exist. This happens with Reused Projects
            // because we don't show the lower-level test cases/steps.
            return null;
        }
        if (current.getContent() != null 
                && current.getContent().equals(spec)) {
            return current;
        }
        for (GuiNode nextNode : current.getChildren()) {
            GuiNode result = recursivlyfindNode(spec, nextNode);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        clear();
    }

    /**
     * @param treeViewer the treeViewer to set
     */
    private void setTreeViewer(TreeViewer treeViewer) {
        m_treeViewer = treeViewer;
    }

    /**
     * @return the treeViewer
     */
    public TreeViewer getTreeViewer() {
        return m_treeViewer;
    }
}