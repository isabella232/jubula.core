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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IParamChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITestDataCubeContPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.actions.SearchTreeAction;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.JubulaStateController;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.events.GuiEventDispatcher;
import org.eclipse.jubula.client.ui.filter.JBBrowserPatternFilter;
import org.eclipse.jubula.client.ui.filter.JBFilteredTree;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.provider.SessionBasedLabelDecorator;
import org.eclipse.jubula.client.ui.provider.contentprovider.CentralTestDataContentProvider;
import org.eclipse.jubula.client.ui.provider.labelprovider.CentralTestDataLabelProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.wizards.ImportTestDataSetsWizard;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.menus.CommandContributionItem;


/**
 * @author BREDEX GmbH
 * @created Jun 28, 2010
 */
public class CentralTestDataEditor extends AbstractJBEditor implements
        IParamChangedListener {

    /**
     * <code>m_elementsToRefresh</code> set of elements to refresh after saving
     * the editor
     */
    private Set<ITestDataCubePO> m_elementsToRefresh = 
        new HashSet<ITestDataCubePO>();

    /** label decorator for viewer */
    private SessionBasedLabelDecorator m_labelDecorator;

    /** {@inheritDoc} */
    protected void createPartControlImpl(Composite parent) {
        createMainPart(parent);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        getMainTreeViewer().getControl().setLayoutData(gridData);
        setControl(getMainTreeViewer().getControl());
        getMainTreeViewer().setContentProvider(
                new CentralTestDataContentProvider());
        m_labelDecorator = new SessionBasedLabelDecorator(this, 
                Plugin.getDefault().getWorkbench().getDecoratorManager()
                    .getLabelDecorator());
        DecoratingLabelProvider lp = new DecoratingLabelProvider(
                new CentralTestDataLabelProvider(), m_labelDecorator);
        lp.setDecorationContext(new JBEditorDecorationContext());
        getMainTreeViewer().setLabelProvider(lp);
        getMainTreeViewer().setSorter(new ViewerSorter());
        getMainTreeViewer().setComparer(new PersistentObjectComparer());
        addTreeDoubleClickListener(CommandIDs.NEW_TESTDATACUBE_COMMAND_ID);
        addFocusListener(getMainTreeViewer());
        getEditorHelper().addListeners();
        setActionHandlers();
        setInitialInput();
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addPropertyChangedListener(this, true);
        ded.addParamChangedListener(this, true);
        GuiEventDispatcher.getInstance()
                .addEditorDirtyStateListener(this, true);
    }

    /** {@inheritDoc} */
    public void dispose() {
        m_labelDecorator.dispose();
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeParamChangedListener(this);
        getElementsToRefresh().clear();
        super.dispose();
    }

    /**
     * creates and sets the partName
     */
    protected void createPartName() {
        setPartName(Messages.CentralTestDataEditorName);
    }

    /**
     * @param mainTreeViewer
     *            the tree viewer
     */
    private void addFocusListener(TreeViewer mainTreeViewer) {
        mainTreeViewer.getTree().addFocusListener(new FocusAdapter() {
            /** {@inheritDoc} */
            public void focusGained(FocusEvent e) {
                getMainTreeViewer().setSelection(
                        getMainTreeViewer().getSelection(), true);
            }
        });
    }

    /** {@inheritDoc} */
    protected void fillContextMenu(IMenuManager mgr) {
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.NEW_TESTDATACUBE_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.RENAME_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.EDIT_PARAMETERS_COMMAND_ID);
        mgr.add(new Separator());
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.SHOW_WHERE_USED_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.REVERT_CHANGES_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.DELETE_COMMAND_ID);
        mgr.add(SearchTreeAction.getAction());
        mgr.add(new Separator());
        Map<String, String> params = new HashMap<String, String>();
        params.put(CommandIDs.IMPORT_WIZARD_PARAM_ID, 
                ImportTestDataSetsWizard.ID);
        mgr.add(CommandHelper.createContributionItem(
                CommandIDs.ECLIPSE_RCP_FILE_IMPORT_COMMAND_ID, 
                params, null,
                CommandContributionItem.STYLE_PUSH));
    }

    /** {@inheritDoc} */
    protected void setHelp(Composite parent) {
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.CENTRAL_TESTDATA_EDITOR);
    }

    /** {@inheritDoc} */
    protected void setInitialInput() {
        ITestDataCubeContPO rootPOTop = (ITestDataCubeContPO)getEditorHelper()
                .getEditSupport().getWorkVersion();
        try {
            getTreeViewer().getTree().getParent().setRedraw(false);
            getTreeViewer().setInput(rootPOTop);
            getTreeViewer().expandAll();
        } finally {
            getTreeViewer().getTree().getParent().setRedraw(true);
        }
    }

    /** {@inheritDoc} */
    public void doSave(IProgressMonitor monitor) {
        monitor.beginTask(Messages.EditorsSaveEditors,
                IProgressMonitor.UNKNOWN);
        EditSupport editSupport = getEditorHelper().getEditSupport();
        try {
            editSupport.saveWorkVersion();
            updateReferencedParamNodes();

            getEditorHelper().resetEditableState();
            getEditorHelper().setDirty(false);
        } catch (IncompatibleTypeException pmce) {
            handlePMCompNameException(pmce);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
            try {
                reOpenEditor(
                    ((PersistableEditorInput)getEditorInput()).getNode());
            } catch (PMException e1) {
                PMExceptionHandler.handlePMExceptionForEditor(e, this);
            }
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        } finally {
            monitor.done();
        }

    }

    /**
     * update the param nodes which reference test data cube, e.g. because of
     * renaming of test data cube
     */
    private void updateReferencedParamNodes() {
        Set<INodePO> nodesToRefresh = new HashSet<INodePO>();
        for (ITestDataCubePO tdc : getElementsToRefresh()) {
            nodesToRefresh.addAll(TestDataCubeBP.getReuser(tdc));
        }
        EntityManager masterSession = 
            GeneralStorage.getInstance().getMasterSession();
        for (INodePO node : nodesToRefresh) {
            masterSession.refresh(node);
        }

        getElementsToRefresh().clear();
    }

    /** {@inheritDoc} */
    public Image getDisabledTitleImage() {
        return IconConstants.DISABLED_CTD_EDITOR_IMAGE;
    }

    /** {@inheritDoc} */
    public String getEditorPrefix() {
        return Messages.PluginCTD;
    }

    /** {@inheritDoc} */
    public void handlePropertyChanged(boolean isCompNameChanged) {
        getMainTreeViewer().refresh();
    }

    /** {@inheritDoc} */
    public void handleParamChanged() {
        // assuming that the currently selected element (or rather, 
        // all currently selected elements) have had some kind of param change
        ISelection currentSelection = getMainTreeViewer().getSelection();
        if (currentSelection instanceof IStructuredSelection) {
            for (Object selectedObj 
                    : ((IStructuredSelection)currentSelection).toArray()) {
                if (selectedObj instanceof ITestDataCubePO) {
                    getElementsToRefresh().add((ITestDataCubePO)selectedObj);
                }
            }
        }
        getMainTreeViewer().refresh();
    }

    /** {@inheritDoc} */
    public void handleDataChanged(IPersistentObject po, DataState dataState,
            UpdateState updateState) {
        if (po instanceof ITestDataCubePO) {
            if (updateState == UpdateState.onlyInEditor) {
                getEditorHelper().setDirty(true);
            }
            ITestDataCubePO tdc = (ITestDataCubePO)po;
            handleDataChanged(dataState, tdc, updateState);
        }
        getMainTreeViewer().refresh();
        getEditorHelper().handleDataChanged(po, dataState, updateState);
    }

    /**
     * @param dataState
     *            the data state
     * @param tdc
     *            the data cube
     * @param updateState
     *            the update state
     */
    private void handleDataChanged(DataState dataState, ITestDataCubePO tdc,
            UpdateState updateState) {
        switch (dataState) {
            case Added:
                getTreeViewer().setSelection(new StructuredSelection(tdc));
                break;
            case Deleted:
                break;
            case Renamed:
                getElementsToRefresh().add(tdc);
                break;
            case ReuseChanged:
                break;
            case StructureModified:
                getTreeViewer().setSelection(new StructuredSelection(tdc));
                break;
            default:
                break;
        }
    }

    /**
     * Creates the specification part of the editor
     * 
     * @param parent
     *            Composite.
     */
    protected void createMainPart(Composite parent) {
        final FilteredTree ft = new JBFilteredTree(parent, SWT.MULTI
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
                new JBBrowserPatternFilter(), true);
        setMainTreeViewer(ft.getViewer());
        getMainTreeViewer().setUseHashlookup(true);
        JubulaStateController.getInstance()
                .addSelectionListenerToSelectionService();
        getSite().setSelectionProvider(this);
        firePropertyChange(IWorkbenchPartConstants.PROP_INPUT);
    }

    /**
     * @return the elementsToRefresh
     */
    private Set<ITestDataCubePO> getElementsToRefresh() {
        return m_elementsToRefresh;
    }
    
}
