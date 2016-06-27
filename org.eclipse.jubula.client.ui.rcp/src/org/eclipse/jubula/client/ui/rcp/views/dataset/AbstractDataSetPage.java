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
package org.eclipse.jubula.client.ui.rcp.views.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IParamChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.TextControlBP;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.factory.TestDataControlFactory;
import org.eclipse.jubula.client.ui.rcp.filter.DataSetFilter;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamTextContentAssisted;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.CharacterConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.WorkbenchJob;


/**
 * Abstract base class for data set pages
 *
 * @author BREDEX GmbH
 * @created Jul 13, 2010
 */
@SuppressWarnings("synthetic-access") 
public abstract class AbstractDataSetPage extends Page 
    implements ISelectionListener, IAdaptable, IParamChangedListener,
               IProjectLoadedListener, IDataChangedListener {
    /** Constant for the width of the DataSet column in the table */
    protected static final int DATASET_NUMBER_COLUMNWIDTH = 30;
    /** Constant for the default column width */ 
    protected static final int COLUMN_WIDTH = 140;
    
    /** Search delay in millisecond */
    private static final long SEARCH_DELAY = 200;

    /** The data set filter */
    private DataSetFilter m_filter;
    
    /** Filter text field */
    private Text m_searchText;
    
    /** The main table view */
    private TableViewer m_viewer;
    
    /** The current IParameterInterfacePO */
    private IParameterInterfacePO m_paramInterfaceObj;

    /** The state of the buttons for the current editor selection */
    private boolean m_buttonEnabled;
    
    /** the primary control for this page */
    private Control m_control;
    /** The TableViewer for this view */
    private TableViewer m_tableViewer;
    /** the tableCursor */
    private DSVTableCursor m_tableCursor;
    
    /** The Add-Button */
    private Button m_addButton;
    /** The Insert Button */
    private Button m_insertButton;
    /** The Delete Button */
    private Button m_deleteButton;
    /** The Up Button */
    private Button m_upButton;
    /** The Down Button */
    private Button m_downButton;
    
    /** En-/Disabler for swt.Controls */
    private ControlEnabler m_controlEnabler;
    /** bp class */
    private AbstractParamInterfaceBP m_paramBP;
    
    /** the corresponding part */
    private IWorkbenchPart m_currentPart;
    /** The current selection */
    private IStructuredSelection m_currentSelection;
    
    /** The current param's id */
    private Long m_paramId;
    /** The column's widths */
    private int[] m_columnWidths;
    
    /** Constants for the button actions */
    private enum TestDataRowAction { 
        /** Add button clicked */
        ADDED, 
        /** Insert button clicked */
        INSERTED, 
        /** Delete button clicked */
        DELETED, 
        /** Up button clicked */
        MOVED_UP, 
        /** Down button clicked */
        MOVED_DOWN 
    }

    /**
     *  The constructor
     *  @param bp the business process to use for this page
     */
    public AbstractDataSetPage(AbstractParamInterfaceBP bp) {
        setParamBP(bp);
    }

    /**
     * Abstract class for ContentProviders
     * 
     * @author BREDEX GmbH
     * @created 04.04.2006
     */
    private abstract static class AbstractContentProvider implements
            IStructuredContentProvider {
        /** {@inheritDoc} */
        public Object[] getElements(Object inputElement) {
            return new Object[0];
        }

        /** {@inheritDoc} */
        public void dispose() {
        // nothing
        }

        /** {@inheritDoc} */
        public void inputChanged(Viewer viewer, Object oldInput, 
                Object newInput) {
        // nothing
        }
    }
    
    /**
     * Abstract class for ITableLabelProvider
     * @author BREDEX GmbH
     * @created 04.04.2006
     */
    private abstract class AbstractLabelProvider 
        implements ITableLabelProvider, IColorProvider {
        /** {@inheritDoc} */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
        
        /** {@inheritDoc} */
        public String getColumnText(Object element, int columnIndex) {
            return StringConstants.EMPTY;
        }
        
        /** {@inheritDoc} */
        public void addListener(ILabelProviderListener listener) {
            // nothing
        }

        /** {@inheritDoc} */
        public void dispose() {
            // nothing
        }

        /** {@inheritDoc} */
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        /** {@inheritDoc} */
        public void removeListener(ILabelProviderListener listener) {
            // nothing
        }
        
        /**
         * {@inheritDoc}
         */
        public Color getBackground(Object element) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Color getForeground(Object element) {
            if (!getControlEnabler().areControlsEnabled()) {
                return LayoutUtil.GRAY_COLOR;
            }
            return null;
        }
    }
    
    /**
     * @param tableViewer the tableViewer to set
     */
    private void setTableViewer(TableViewer tableViewer) {
        m_tableViewer = tableViewer;
    }

    /**
     * @return the tableViewer
     */
    private TableViewer getTableViewer() {
        return m_tableViewer;
    }
    
    /**
     * @return the tableViewers table control
     */
    private Table getTable() {
        return getTableViewer().getTable();
    }
    
    /**
     * checks the combo selection. Call after any button action!
     * @param action the action of the button
     * @param row the row on which the action was performed
     */    
    private void checkComboSelection(TestDataRowAction action, int row) {
        getTableViewer().refresh();
    }

    
    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        Composite topLevelComposite = new Composite(parent, SWT.NONE);
        topLevelComposite.setData(SwtToolkitConstants.WIDGET_NAME,
                "DataSetViewPage"); //$NON-NLS-1$
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 2;
        layout.marginWidth = LayoutUtil.MARGIN_WIDTH;
        layout.marginHeight = LayoutUtil.MARGIN_HEIGHT;
        topLevelComposite.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        topLevelComposite.setLayoutData(layoutData);
        m_control = topLevelComposite;

        Composite buttonComp = new Composite(topLevelComposite, SWT.BORDER);

        // Set numColumns to 2 for the buttons
        layout = new GridLayout(2, false);
        layout.marginWidth = 1;
        layout.marginHeight = 1;
        buttonComp.setLayout(layout);

        // Create a composite to hold the children
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        buttonComp.setLayoutData(gridData);
        
        initTableViewer(buttonComp); 
        createButtons(buttonComp);
        Plugin.getHelpSystem().setHelp(getTable(),
                ContextHelpIds.JB_DATASET_VIEW);
    }

    /**
     * Add the "Add", "Delete" and "Insert" buttons
     * @param parent the parent composite
     */
    private void createButtons(Composite parent) {
        
        Composite bottomComp = new Composite(parent, SWT.NONE);

        // Set numColumns to 3 for the buttons
        GridLayout layout = new GridLayout(5, false);
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        layout.verticalSpacing = 2;
        GridData gridDataBottom = new GridData(SWT.NONE, SWT.NONE, 
                true, false);
        gridDataBottom.horizontalAlignment = GridData.FILL;
        gridDataBottom.horizontalSpan = 3;
        bottomComp.setLayoutData(gridDataBottom);
        bottomComp.setLayout(layout);
        
        // Create and configure the "Add" button
        setAddButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getAddButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.AddButton"); //$NON-NLS-1$
        getAddButton().setText(Messages.JubulaDataSetViewAppend);
        GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;
        getAddButton().setLayoutData(gridData);
        getAddButton().setEnabled(false);
        getControlEnabler().addControl(getAddButton());
        
        // Create and configure the "Insert" button
        setInsertButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getInsertButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.InsertButton"); //$NON-NLS-1$
        getInsertButton().setText(Messages.DataSetViewInsert);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;
        getInsertButton().setLayoutData(gridData);
        getInsertButton().setEnabled(false);
        getControlEnabler().addControl(getInsertButton());
        
        //  Create and configure the "Delete" button
        setDeleteButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getDeleteButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.DeleteButton"); //$NON-NLS-1$
        getDeleteButton().setText(Messages.JubulaDataSetViewDelete);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 80; 
        getDeleteButton().setLayoutData(gridData); 
        getDeleteButton().setEnabled(false);
        getControlEnabler().addControl(getDeleteButton());
        
        // Create and configure the "Down" button
        setDownButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getDownButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.DownButton"); //$NON-NLS-1$
        getDownButton().setImage(IconConstants.DOWN_ARROW_IMAGE);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
        getDownButton().setLayoutData(gridData);
        getDownButton().setEnabled(false);
        getControlEnabler().addControl(getDownButton());

        // Create and configure the "Up" button
        setUpButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getUpButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.UpButton"); //$NON-NLS-1$
        getUpButton().setImage(IconConstants.UP_ARROW_IMAGE);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
        getUpButton().setLayoutData(gridData);
        getUpButton().setEnabled(false);
        getControlEnabler().addControl(getUpButton());
        
        addListenerToButtons();
    }
    
    /**
     * inits the m_tableViewer
     * @param parent the parent of the m_tableViewer
     */
    private void initTableViewer(Composite parent) {
        m_filter = new DataSetFilter();
        GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);
        m_searchText = new Text(parent, SWT.SINGLE | SWT.BORDER
                | SWT.SEARCH | SWT.ICON_CANCEL);
        m_searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
            | GridData.HORIZONTAL_ALIGN_FILL));
        m_viewer = new TableViewer(parent, 
                SWT.SINGLE | SWT.FULL_SELECTION);
        final Job searchJob = createSearchJob();
        m_searchText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (m_currentPart instanceof AbstractJBEditor) {
                    if (m_searchText.getText().isEmpty()) {
                        m_addButton.setEnabled(true);
                        m_insertButton.setEnabled(true);
                        m_deleteButton.setEnabled(true);
                    } else {
                        m_addButton.setEnabled(false);
                        m_insertButton.setEnabled(false);
                        m_deleteButton.setEnabled(false);
                    }
                }
                searchJob.cancel();
                searchJob.schedule(SEARCH_DELAY);
            }
        });
        final String initialText = WorkbenchMessages.FilteredTree_FilterMessage;
        
        setTableViewer(m_viewer);
        Table table = getTable();
        table.setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.DataTable"); //$NON-NLS-1$
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        table.setLayoutData(gridData);
        getTableViewer().setUseHashlookup(true);
        getTableViewer().setContentProvider(new GeneralContentProvider());
        getTableViewer().setLabelProvider(new GeneralLabelProvider());
        getTableViewer().addFilter(m_filter);
        setTableCursor(new DSVTableCursor(getTable(), SWT.NONE));
    }
    
    /**
     * @return an search job
     */
    protected WorkbenchJob createSearchJob() {
        WorkbenchJob job = new WorkbenchJob("Refresh Filter") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                m_filter.setSearchText(m_searchText.getText());
                m_viewer.refresh();
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        return job;
    }
    
    /**
     * add listener to buttons
     */
    private void addListenerToButtons() {
        getAddButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                addDataSet();
                checkComboSelection(TestDataRowAction.ADDED, index);
                getControlEnabler().selectionChanged(m_currentPart,
                        m_currentSelection);
            }
        });
        getInsertButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                insertDataSetAtCurrentSelection();
                checkComboSelection(TestDataRowAction.INSERTED, index);
            }
        });
        getDeleteButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                removeDataSet();
                checkComboSelection(TestDataRowAction.DELETED, index);
            }
        });
        getUpButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                moveDataSetUp();
                checkComboSelection(TestDataRowAction.MOVED_UP, index);
            }
        });
        getDownButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                moveDataSetDown();
                checkComboSelection(TestDataRowAction.MOVED_DOWN, index);
            }
        });
    }
    
    
    /**
     * @return the zero relative index of the selected data set.
     */
    private int getSelectedDataSet() {
        return getTableViewer().getTable().getSelectionIndex();
    }
    
    /**
     * Moves a data set one row down
     */
    private void moveDataSetDown() {
        final int row = getSelectedDataSet();
        moveDataSet(row, row + 1);
    }
    
    /**
     * Moves a data set one row up
     */
    private void moveDataSetUp() {
        final int row = getSelectedDataSet();
        moveDataSet(row, row - 1);
    }
    
    /**
     * Moves a data set one row down
     * @param fromIndex Position from where to move
     * @param toIndex Target position of the dataset
     */
    private void moveDataSet(int fromIndex, int toIndex) {
        final int rowCount = getParamInterfaceObj().getDataManager()
                .getDataSetCount();
        final AbstractJBEditor editor = (AbstractJBEditor)m_currentPart;
        if (editor.getEditorHelper().requestEditableState()
                == JBEditorHelper.EditableState.OK) {
            if (getParamInterfaceObj() instanceof IExecTestCasePO) {
                ITDManager man = ((IExecTestCasePO)getParamInterfaceObj())
                        .resolveTDReference();
                if (!man.equals(getTableViewer().getInput())) {
                    getTableViewer().setInput(man);
                }
            }
            ITDManager tdman = getParamInterfaceObj().getDataManager();
            if (fromIndex >= 0 && fromIndex < rowCount
                    && toIndex >= 0 && toIndex < rowCount) {
                IDataSetPO selectedDataSet = tdman.getDataSet(fromIndex);
                if (fromIndex > toIndex) {
                    tdman.insertDataSet(selectedDataSet, toIndex);
                    tdman.removeDataSet(fromIndex + 1);
                } else {
                    tdman.insertDataSet(selectedDataSet, toIndex + 1);
                    tdman.removeDataSet(fromIndex);
                }
                getTableCursor().setSelection(toIndex,
                        getTableCursor().getColumn());
                getTableViewer().refresh();
                DataEventDispatcher.getInstance().fireParamChangedListener();
                editor.getEditorHelper().setDirty(true);
            }
        }
    }
    
    /**
     * Add a row as last element.
     */
    private void addDataSet() {
        final int rowCount = getParamInterfaceObj().getDataManager()
                .getDataSetCount();
        insertDataSet(rowCount);
    }
    
    /**
     * Inserts a new data set at the current selection in the table
     */
    private void insertDataSetAtCurrentSelection() {
        final int row = getSelectedDataSet();
        insertDataSet(row);
    }
    
    /**
     * Inserts a new data set at the given row
     * 
     * @param row
     *            the row to insert the new data set
     */
    private void insertDataSet(int row) {
        final AbstractJBEditor editor = (AbstractJBEditor)m_currentPart;
        if (editor.getEditorHelper().requestEditableState()
                == JBEditorHelper.EditableState.OK) {
            if (getParamInterfaceObj() instanceof IExecTestCasePO) {
                ITDManager man = ((IExecTestCasePO)getParamInterfaceObj())
                        .resolveTDReference();
                if (!man.equals(getTableViewer().getInput())) {
                    getTableViewer().setInput(man);
                }
            }
            if (row > -1) {
                getParamBP().addDataSet(getParamInterfaceObj(), row);
            } else {
                // if first data set is added
                addDataSet();
            }
            editor.getEditorHelper().setDirty(true);
            getTableViewer().refresh();
            int rowToSelect = row;
            if (rowToSelect == -1) {
                rowToSelect = getTable().getItemCount();
            } else {
                getTableCursor().setSelection(rowToSelect, 1);
                setFocus();
            }
            getTable().setSelection(rowToSelect);
            DataEventDispatcher.getInstance().fireParamChangedListener();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getControl() {
        return m_control;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setFocus() {
        getTable().setFocus();
    }

    /**
     * @return the controlEnabler
     */
    private ControlEnabler getControlEnabler() {
        if (m_controlEnabler == null) {
            m_controlEnabler = new ControlEnabler();
        }
        return m_controlEnabler;
    }

    /**
     * @param addButton the addButton to set
     */
    private void setAddButton(Button addButton) {
        m_addButton = addButton;
    }

    /**
     * @return the addButton
     */
    private Button getAddButton() {
        return m_addButton;
    }

    /**
     * @param insertButton the insertButton to set
     */
    private void setInsertButton(Button insertButton) {
        m_insertButton = insertButton;
    }

    /**
     * @return the insertButton
     */
    private Button getInsertButton() {
        return m_insertButton;
    }

    /**
     * @param deleteButton the deleteButton to set
     */
    private void setDeleteButton(Button deleteButton) {
        m_deleteButton = deleteButton;
    }

    /**
     * @return the deleteButton
     */
    private Button getDeleteButton() {
        return m_deleteButton;
    }
    
    /**
     * @param upButton the upButton to set
     */
    private void setUpButton(Button upButton) {
        m_upButton = upButton;
    }

    /**
     * @return the upButton
     */
    private Button getUpButton() {
        return m_upButton;
    }
    
    /**
     * @param downButton the downButton to set
     */
    private void setDownButton(Button downButton) {
        m_downButton = downButton;
    }

    /**
     * @return the downButton
     */
    private Button getDownButton() {
        return m_downButton;
    }

    /**
     * Class for En-/Disabling swt.Controls depending of active WorkbenchPart
     * and selection
     * @author BREDEX GmbH
     * @created 06.04.2006
     */
    private abstract class AbstractControlEnabler {
        /** List of Controls */
        private List<Control> m_controlList = new ArrayList<Control>();
        
        /** 
         * tracks whether managed controls were most recently 
         * enabled or disabled 
         */
        private boolean m_areControlsEnabled = true;
        
        /**
         * Adds the given Control to this Listener
         * @param control the Control
         */
        public void addControl(Control control) {
            if (!getControlList().contains(control)) {
                getControlList().add(control);
            }
        }
        
        /**
         * @return the controlList
         */
        protected List<Control> getControlList() {
            return m_controlList;
        }
        
        /**
         * Enables or disables all controls managed by the receiver.
         * 
         * @param enabled <code>true</code> if all managed components should be
         *                enabled. <code>false</code> if all managed components
         *                should be disabled.
         */
        public void setControlsEnabled(boolean enabled) {
            m_areControlsEnabled = enabled;
            for (Control control : getControlList()) {
                control.setEnabled(enabled);
            }
        }

        /**
         * 
         * @return <code>true</code> if all managed components are enabled. 
         *         <code>false</code> if all managed components are disabled.
         */
        public boolean areControlsEnabled() {
            return m_areControlsEnabled;
        }
    }
    
    /**
     * Clears the m_tableViewer
     */
    private void clearTableViewer() {
        getTable().removeAll();
        for (TableColumn column : getTable().getColumns()) {
            column.dispose();
        }
        DSVTableCursor tableCursor = getTableCursor();
        if (tableCursor != null && !tableCursor.isDisposed()) {
            tableCursor.dispose();
            setTableCursor(new DSVTableCursor(getTable(), SWT.NONE));
        }
    }
    
    
    /**
     * Inits and creates the column for the data set numbers
     * @return the name of the column
     */
    private String initDataSetColumn() {
        clearTableViewer();
        final Table table = getTable();
        // create column for data set numer
        TableColumn dataSetNumberCol = new TableColumn(table, SWT.NONE);
        dataSetNumberCol.setText(Messages.DataSetViewControllerDataSetNumber);
        dataSetNumberCol.setWidth(DATASET_NUMBER_COLUMNWIDTH);
        return dataSetNumberCol.getText();
    }
    
    /**
     * Packs the table.
     */
    private void packTable() {
        final Table table = getTable();
        final TableColumn[] columns = table.getColumns();
        final int columnCount = columns.length;
        for (int i = 1; i < columnCount; i++) {
            final TableColumn column = columns[i];
            column.pack();
            column.setWidth(m_columnWidths != null ? m_columnWidths[i] 
                    : COLUMN_WIDTH);
        }
    }
    
    /**
     * creates the TableColumns with Parameter
     */
    private void initTableViewerParameterColumns() {
        if (getParamInterfaceObj() == null) {
            return;
        }
        final Table table = getTable();
        
        if (m_paramId == getParamInterfaceObj().getId()) {
            TableColumn[] tableColumns = table.getColumns();
            if (tableColumns != null && tableColumns.length != 0) {
                m_columnWidths = new int[tableColumns.length];
                int i = 0;
                for (TableColumn column : tableColumns) {
                    m_columnWidths[i++] = column.getWidth();
                }
            }
        } else {
            m_paramId = getParamInterfaceObj().getId();
            m_columnWidths = null;
        }
        
        
        String[] columnProperties = new String[getParamInterfaceObj()
                .getParameterList().size() + 1];
        columnProperties[0] = initDataSetColumn();
        // create columns for parameter
        int i = 1;
        for (IParamDescriptionPO descr : getParamInterfaceObj()
                .getParameterList()) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            String columnName = descr.getName();
            column.setText(columnName);
            columnProperties[i++] = columnName;
            if (m_columnWidths == null) {
                if (column.getWidth() < COLUMN_WIDTH) {
                    column.setWidth(COLUMN_WIDTH);
                }
            } else {
                column.setWidth(m_columnWidths[i - 1]);
            }
        }
        getTableViewer().setColumnProperties(columnProperties);
    }
    
    /**
     * Updates this view. Causes the view to get and display its data.
     */
    private void updateView() {
        clearTableViewer();
        IParameterInterfacePO paramObj = getParamInterfaceObj();
        if (paramObj != null && isNodeValid(paramObj)) {
            getTableViewer().setInput(getInputForTable(paramObj));
            createTable();
        } else {
            getTableViewer().setInput(null);
        }
        getTableViewer().refresh();
    }
    
    /**
     * @param cParamInterfaceObj the param interface object to test
     * @return whether the object is valid
     */
    protected abstract boolean isNodeValid(
            IParameterInterfacePO cParamInterfaceObj);

    /**
     * Creates the table
     */
    private void createTable() {
        initTableViewerParameterColumns();
        packTable();
    }

    
    /**
     * The AbstractContentProvider of the Language-Table.
     * @author BREDEX GmbH
     * @created 03.04.2006
     */
    private static class GeneralContentProvider 
        extends AbstractContentProvider {
        /** {@inheritDoc} */
        public Object[] getElements(Object inputElement) {
            ITDManager tdMan = (ITDManager)inputElement;
            List <IDataSetPO> rows = tdMan.getDataSets();
            return rows.toArray();
        }
    }
    
    /**
     * The label provider to display the default data
     * @author BREDEX GmbH
     * @created 03.04.2006
     */
    private class GeneralLabelProvider extends AbstractLabelProvider {
        /** {@inheritDoc} */
        public String getColumnText(Object element, int columnIndex) {
            if (!(element instanceof IDataSetPO)) {
                // this happens when Content-/LabelProvider changes!
                // see ...ComboListener
                return StringConstants.EMPTY; 
            }
            ITDManager tdMan = (ITDManager)getTableViewer().getInput();
            IDataSetPO row = (IDataSetPO)element;
            int rowCount = tdMan.getDataSets().indexOf(row);
            if (columnIndex == 0) {
                for (TableItem i : Arrays.asList(getTable().getItems())) {
                    if (i instanceof IDataSetPO && ((IDataSetPO)i)
                            .equals(element)) {
                        i.setBackground(columnIndex, getTable().getDisplay()
                                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                        break;
                    }
                }
                return StringConstants.EMPTY + (rowCount + 1); 
            }
            List <IParamDescriptionPO>paramList = 
                getParamInterfaceObj().getParameterList();
            String value = StringConstants.EMPTY;
            if ((columnIndex - 1) < paramList.size()) {
                IParamDescriptionPO desc = paramList.get(columnIndex - 1);
                IParameterInterfacePO paramInterface = getParamInterfaceObj();
                value = getGuiStringForParamValue(paramInterface, desc,
                        rowCount);
            }
            return value;
        }
    }
    
    /**
     * @param paramObj
     *            the param interface object
     * @param desc
     *            the ParamDescriptionP
     * @param rowCount
     *            the row count
     * @return a valid string for gui presentation of the given param value
     */
    public static String getGuiStringForParamValue(
            IParameterInterfacePO paramObj, IParamDescriptionPO desc,
            int rowCount) {
        return AbstractParamInterfaceBP.getGuiStringForParamValue(paramObj,
                desc, rowCount);
    }
    
    /** {@inheritDoc} */
    public void handleParamChanged() {
        initTableViewerParameterColumns();
        updateView();            
    }
    
    /** {@inheritDoc} */
    public void handleProjectLoaded() {
        setParamInterfaceObj(null);
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                getTableViewer().setInput(null);
            }
        });
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        for (DataChangedEvent e : events) {
            handleDataChanged(e.getPo(), e.getDataState());
        }
    }
    
    /** {@inheritDoc} */
    public void handleDataChanged(IPersistentObject po, DataState dataState) {
        if (dataState == DataState.Deleted 
                && po.equals(getParamInterfaceObj())) {
            setParamInterfaceObj(null);
            updateView();
        }

        if (dataState == DataState.StructureModified
                && po instanceof ITestDataCategoryPO) {
            updateView();
        }
        
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                getControlEnabler().selectionChanged(m_currentPart,
                        m_currentSelection);
            }
        });
    }
    
    /**
     * The TableCursor for keyboard support
     * @author BREDEX GmbH
     * @created 11.04.2006
     */
    public class DSVTableCursor extends TableCursor {
        /** The ControlEditor */
        private ControlEditor m_editor;
        /** the current testcase editor */
        private AbstractJBEditor m_tcEditor;
        /** The KeyListener of the editor */
        private KeyAdapter m_keyListener = new EditorKeyListener();
        /** The MouseListener of this Cursor */
        private MouseAdapter m_mouseListener = new EditorMouseListener();
        /** The SelectionListener of this Cursor */
        private CursorListener m_cursorListener = new CursorListener();
        /** The FocusListener of this Cursor */
        private EditorFocusListener m_focusListener = new EditorFocusListener();
        /** true, if editor was activated with enter key */
        private boolean m_wasActivatedWithEnterKey = false;
        /** value to reset, when pressing "ESC" */
        private String m_oldValue;
        /** The untyped Listener of this Cursor */ 
        private Listener m_listener = new Listener() {
            public void handleEvent(Event event) {
                if (event.type == SWT.Selection
                        && event.widget instanceof CCombo) {
                    writeData();
                }
            }
        };
        
        /**
         * @param parent parent
         * @param style style
         */
        public DSVTableCursor(Table parent, int style) {
            super(parent, style);
            addSelectionListener(m_cursorListener);
            addMouseListener(m_mouseListener);
            addKeyListener(m_keyListener);
            m_editor = new ControlEditor(this);
            m_editor.grabHorizontal = true;
            m_editor.grabVertical = true;
        }
        
        /**
         * Gets the zero based column index of the given column property
         * @param columnProperty the property to get the index of
         * @return the zero based column index of the given column property 
         * or -1 if no column with the given property was found
         */
        private int getColumnIndexOfProperty(String columnProperty) {
            Object[] props = getTableViewer().getColumnProperties();
            for (int i = 0; i < props.length; i++) {
                if (columnProperty.equals(props[i])) {
                    return i;
                }
            }
            return -1;
        }
        
        /**
         * assumes the typed data
         */
        private void writeData() {
            if (m_currentPart instanceof AbstractJBEditor) {
                m_tcEditor = (AbstractJBEditor)m_currentPart;
            }
            if (m_tcEditor == null) { // e.g. activeEditor = OMEditor
                return;
            }
            int column = getColumn();
            final Control editor = m_editor.getEditor();
            if (!TextControlBP.isTextValid(editor)) {
                TextControlBP.setText(m_oldValue, editor);
            }
            final String property = getTableViewer().getColumnProperties()
                [column].toString();
            String value = TextControlBP.getText(editor);
            if (m_oldValue != null && m_oldValue.equals(value)) {
                return;
            }
            if (value != null && value.equals(StringConstants.EMPTY)) {
                value = null;
            }
            writeDataSetData(property, value, m_tcEditor);
        }
        
        /**
         * Writes the data to the selected data set
         * @param property the column property
         * @param value the value to write
         * @param edit the editor
         */
        private void writeDataSetData(String property, Object value, 
                AbstractJBEditor edit) {
            final int langIndex = getColumnIndexOfProperty(property);
            final int dsNumber = getSelectedDataSet();
            final int paramIndex = getTable()
                .getSelectionIndex();
            setValueToModel(value, edit, paramIndex, dsNumber);
            getTable().getItem(paramIndex).setText(langIndex, 
                value == null ? StringConstants.EMPTY : (String) value);
        }
        

        /**
         * @param value
         *            the value to set
         * @param editor
         *            the editor
         * @param paramIndex
         *            the index of the parameter
         * @param dsNumber
         *            the number of data set.
         */
        private void setValueToModel(Object value, AbstractJBEditor editor,
                int paramIndex, int dsNumber) {
            if (editor.getEditorHelper().requestEditableState()
                    == JBEditorHelper.EditableState.OK) {
                ParamNameBPDecorator mapper = editor.getEditorHelper()
                        .getEditSupport().getParamMapper();
                GuiParamValueConverter conv = getGuiParamValueConverter(
                        (String)value, getParamInterfaceObj(),
                        getCurrentParamDescription(),
                        ((CheckedParamText)m_editor.getEditor())
                                .getDataValidator());
                if (conv.getErrors().isEmpty()) {
                    getParamBP().startParameterUpdate(conv, dsNumber, mapper);
                    setIsEntrySetComplete(getParamInterfaceObj());
                    editor.getEditorHelper().setDirty(true);
                    new IsAliveThread() {
                        public void run() {
                            Plugin.getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    DataEventDispatcher ded = 
                                            DataEventDispatcher.getInstance();
                                    ded.firePropertyChanged(false);
                                    ded.fireParamChangedListener();
                                }
                            });
                        }
                    } .start();
                }
            }
        }
        
        
        /** {@inheritDoc} */
        public void dispose() {
            removeSelectionListener(m_cursorListener);
            removeMouseListener(m_mouseListener);
            Control editor = m_editor.getEditor();
            if (editor != null && !editor.isDisposed()) {
                editor.removeFocusListener(m_focusListener);
                editor.dispose();
            }
            super.dispose();
        }
        
        /**
         * @return if the value can be modified
         */
        private boolean canModify() {
            if (!(m_currentPart instanceof AbstractJBEditor)) {
                return false;
            }
            final AbstractJBEditor edit = (AbstractJBEditor)m_currentPart;
            // First column is not editable!
            boolean isFirstColumn = getColumn() == 0;
            boolean isEditor = (edit != null);

            return !isFirstColumn && isEditor 
                && getControlEnabler().areControlsEnabled();
        }
        
        /** {@inheritDoc} */
        protected void checkSubclass () {
            // only to subclass
        }
        
        /**
         * @return the editor to enter values
         */
        private Control createEditor() {
            Control control = TestDataControlFactory.createControl(
                    getParamInterfaceObj(), getParamName(), this, SWT.NONE);
            control.addKeyListener(m_keyListener);
            control.setFocus();
            // FIXME: see http://eclip.se/390800
            // control.addFocusListener(m_focusListener);
            // end http://eclip.se/390800
            control.addListener(SWT.Selection, m_listener);
            m_oldValue = getRow().getText(getColumn());
            TextControlBP.setText(m_oldValue, control);
            TextControlBP.selectAll(control);
            return control;
        }
        
        /**
         * @return the current param name
         */
        private String getParamName() {
            return getTableViewer().getTable().getColumn(getColumn()).getText();
        }
        
        /**
         * @return paramDescription for currently edited value
         */
        private IParamDescriptionPO getCurrentParamDescription() {
            String paramName = getParamName();
            return getParamInterfaceObj().getParameterForName(paramName);
        }

        /**
         * activate the editor
         */
        private void activateEditor() {
            if (canModify()) {
                m_editor.setEditor(createEditor());
                // FIXME: see http://eclip.se/390800
                Control editorCtrl = m_editor.getEditor();
                if ((editorCtrl != null) && !editorCtrl.isDisposed()) {
                    editorCtrl.addFocusListener(m_focusListener);
                }
                // end http://eclip.se/390800
                TextControlBP.selectAll(m_editor.getEditor());
            }
        }

        /**
         * KeyListener for the editor
         */
        private class EditorKeyListener extends KeyAdapter {
            /** {@inheritDoc} */
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_DOWN 
                        || e.keyCode == SWT.ARROW_UP
                        || e.keyCode == SWT.ARROW_LEFT
                        || e.keyCode == SWT.ARROW_RIGHT) {
                    
                    return;
                }
                if (!(e.character == CharacterConstants.BACKSPACE
                    || e.character == SWT.DEL // the "DEL"-Key
                    || e.character == SWT.ESC // the "ESC"-Key
                    || e.character == SWT.CR // the "ENTER"-Key
                    || e.character == SWT.KEYPAD_CR // the "ENTER"-Key
                    || (!Character.isISOControl(e.character)))) {
                    return;
                }
                if (e.getSource().equals(m_editor.getEditor())) {
                    // close the text editor when the user hits "ESC"
                    if (e.character == SWT.ESC) {
                        TextControlBP.setText(m_oldValue, m_editor.getEditor());
                        writeData();
                        TableItem rowItem = getRow();
                        final int col = getColumn();
                        rowItem.setText(col, m_oldValue);
                        m_editor.getEditor().dispose();
                        return;
                    }
                    if (e.character == SWT.CR || e.character == SWT.KEYPAD_CR) {
                        if (m_wasActivatedWithEnterKey) {
                            m_wasActivatedWithEnterKey = false;
                            return;
                        }
                        handleCR();
                    }
                }
                if (e.getSource() instanceof DSVTableCursor) {
                    if (e.character == SWT.ESC) {
                        return;
                    }
                    activateEditor();
                    if (m_editor.getEditor() != null 
                            && !m_editor.getEditor().isDisposed()
                            && e.character != SWT.CR
                            && e.character != SWT.KEYPAD_CR
                            && !(m_editor.getEditor() instanceof CCombo)) {
                        String sign = new Character(e.character).toString();
                        if (e.character == SWT.DEL // the "DEL"-Key
                            || e.character == CharacterConstants.BACKSPACE) {
                            sign = StringConstants.EMPTY;
                        }
                        TextControlBP.setText(sign, m_editor.getEditor());
                        TextControlBP.setSelection(m_editor.getEditor(), 1);
                    }
                }
            }

            /**
             * Handles the CR keys
             */
            private void handleCR() {
                final Control editorControl = m_editor.getEditor();
                if (!editorControl.isDisposed()) {
                    writeData();
                }
                // writeData() may actually dispose the control during error
                // handling, a new check is needed!
                if (!editorControl.isDisposed()) {
                    TableItem rowItem = getRow();
                    final int col = getColumn();
                    rowItem.setText(col, TextControlBP.getText(editorControl));
                    editorControl.dispose();
                    final int row = getTable().indexOf(getRow());
                    if (getTable().getColumnCount() > (col + 1)) {
                        setSelection(row, col + 1);
                        getTable().setSelection(row);
                        setFocus();
                    } else if (getTable().getItemCount() > (row + 1)) {
                        setSelection(row + 1, 1);
                        getTable().setSelection(row + 1);
                    } else {
                        getAddButton().setFocus();
                    }
                }
            }
        }
         
        /**
         * The SelectionListener
         */
        private class CursorListener extends SelectionAdapter {

            /** {@inheritDoc} */
            public void widgetDefaultSelected(SelectionEvent e) {
                activateEditor();
                m_wasActivatedWithEnterKey = true;
            }

            /** {@inheritDoc} */
            public void widgetSelected(SelectionEvent e) {
                getTable().setSelection(
                    new TableItem[] {getRow()});
            }
            
        }

        /**
         * MouseListener for the editor
         */
        private class EditorMouseListener extends MouseAdapter {
            /** {@inheritDoc} */
            public void mouseUp(MouseEvent e) {
                activateEditor();
                m_wasActivatedWithEnterKey = false;
            }
        }
        
        /**
         * @author BREDEX GmbH
         * @created 19.06.2006
         */
        private class EditorFocusListener extends FocusAdapter {
            /** {@inheritDoc} */
            public void focusLost(FocusEvent e) {
                if (m_editor.getEditor() 
                        instanceof CheckedParamTextContentAssisted) {
                    CheckedParamTextContentAssisted ed = 
                        (CheckedParamTextContentAssisted)m_editor.getEditor();
                    if (ed.isPopupOpen() && ed.isFocusControl()) {
                        super.focusLost(e);
                        return;
                    }
                }
                writeData();
                m_editor.getEditor().dispose();
                super.focusLost(e);
            }  
        }
    }
    
    /**
     * Removes a selected data set.
     */
    private void removeDataSet() {
        final AbstractJBEditor editor = (AbstractJBEditor)m_currentPart;
        if (editor == null) {
            return;
        }
        if (editor.getEditorHelper().requestEditableState()
                == JBEditorHelper.EditableState.OK) {
            if (getParamInterfaceObj() instanceof IExecTestCasePO) {
                ITDManager man = ((IExecTestCasePO)getParamInterfaceObj())
                        .resolveTDReference();
                if (!man.equals(getTableViewer().getInput())) {
                    getTableViewer().setInput(man);
                }
            }

            int row = getSelectedDataSet();
            try {
                if (row == -1 && getTableCursor().getRow() != null) {
                    row = getTable().indexOf(getTableCursor()
                        .getRow());
                }
                if (row > -1) {
                    editor.getEditorHelper().getEditSupport()
                        .lockWorkVersion();
                    getParamBP().removeDataSet(getParamInterfaceObj(),
                            row, editor.getEditorHelper().getEditSupport()
                                    .getParamMapper());
                    editor.getEditorHelper().setDirty(true);
                    getTableViewer().refresh();                    
                    setIsEntrySetComplete(getParamInterfaceObj());
                    if (getTable().getItemCount() != 0) {
                        if (getTable().getItemCount() <= row
                                && getTable().getItemCount() > 0) {
                            --row;
                            getTable().setSelection(row);
                        } else {
                            getTable().setSelection(row);
                        }
                        getTableCursor().setSelection(row, 1);
                    } else {
                        getDeleteButton().setEnabled(false);
                        getInsertButton().setEnabled(false);
                        getUpButton().setEnabled(false);
                        getDownButton().setEnabled(false);
                    }
                    setFocus();
                    DataEventDispatcher.getInstance()
                            .fireParamChangedListener();
                }
            } catch (PMException pme) {
                PMExceptionHandler.handlePMExceptionForEditor(pme, editor);
            }
        }
    }

    /**
     * Reacts on the changes from the SelectionService of Eclipse.
     * @param part The Workbenchpart.
     * @param selection The selection.
     */
    private void reactOnChange(IWorkbenchPart part,
            IStructuredSelection selection) {
        m_currentPart = part;
        m_currentSelection = selection;
        getControlEnabler().selectionChanged(part, selection);
        
        IParameterInterfacePO paramInterfacePO = 
            getSelectedParamInterfaceObj(selection);
        if (getParamInterfaceObj() != null
                && getParamInterfaceObj() == paramInterfacePO) {
            // identity check is ok here because node of SpecBrowser and
            // SpecEditor are equal but can have different data if the
            // Editor node has been edited!
            return;
        }
        
        setParamInterfaceObj(paramInterfacePO);
        updateView();
    }
    
    /**
     * @param selection
     *            the current selection
     * @return the valid param interface po or <code>null</code> if current
     *         selection does not contain a IParameterInterfacePO
     */
    private IParameterInterfacePO getSelectedParamInterfaceObj(
            IStructuredSelection selection) {
        IParameterInterfacePO paramInterfacePO = null;
        Object firstSel = selection.getFirstElement();
        if (firstSel instanceof IParameterInterfacePO) {
            paramInterfacePO = (IParameterInterfacePO)firstSel;
        }
        return paramInterfacePO;
    }

    /**
     * checks the given IParameterInterfacePO if all entrySets are complete for
     * the given Locale and sets the flag.
     * 
     * @param paramNode
     *            teh ParamNodePO to check.
     */
    protected abstract void setIsEntrySetComplete(
            IParameterInterfacePO paramNode);
    
    /**
     * Class for En-/Disabling swt.Controls depending of active WorkbenchPart
     * and selection
     * @author BREDEX GmbH
     * @created 06.04.2006
     */
    protected class ControlEnabler extends AbstractControlEnabler 
            implements ISelectionListener {
        
        /** {@inheritDoc} */
        public void selectionChanged(IWorkbenchPart part, 
            ISelection selection) {
            if (!(selection instanceof IStructuredSelection)) { 
                // e.g. in Jubula plugin-version you can open an java editor, 
                // that reacts on org.eclipse.jface.text.TextSelection, which
                // is not a StructuredSelection
                return;
            }
            IStructuredSelection strucSelection = 
                    (IStructuredSelection)selection;
            IParameterInterfacePO paramNode = getSelectedParamInterfaceObj(
                    strucSelection);

            boolean correctPart = false;
            if (part != null) {
                correctPart = (part == AbstractDataSetPage.this || part
                        .getAdapter(AbstractJBEditor.class) != null);
            }
            if (!correctPart) {
                getTable().setForeground(LayoutUtil.GRAY_COLOR);
            } else {
                getTable().setForeground(LayoutUtil.DEFAULT_OS_COLOR);
            }
            boolean hasInput = !strucSelection.isEmpty();
            boolean isEditorOpen = isEditorOpen(paramNode);
            boolean hasParameter = false; 
            boolean hasExcelFile = false;
            boolean hasReferencedDataCube = false;
            if (paramNode != null) {
                hasParameter = !paramNode.getParameterList().isEmpty();
                final String dataFile = paramNode.getDataFile();
                hasExcelFile = !(dataFile == null || dataFile.length() == 0);
                hasReferencedDataCube = 
                    paramNode.getReferencedDataCube() != null;
            }
            // En-/disable controls
            boolean isCAP = paramNode instanceof ICapPO;
            m_buttonEnabled = correctPart && hasInput && isEditorOpen
                && !isCAP && !hasExcelFile && !hasReferencedDataCube 
                && hasParameter;
            setControlsEnabled(m_buttonEnabled);
        }
    }
    /**
     * Checks if the given IParameterInterfacePO is in an open editor.
     * @param paramObj the object to check
     * @return true if the given node is in an open editor, false otherwise.
     */
    protected abstract boolean isEditorOpen(IParameterInterfacePO paramObj);
    
    /** {@inheritDoc} */
    public void selectionChanged(IWorkbenchPart part,
            ISelection selection) {
            
        if (!(selection instanceof IStructuredSelection)) { 
            // e.g. in Jubula plugin-version you can open an java editor, 
            // that reacts on org.eclipse.jface.text.TextSelection, which
            // is not a StructuredSelection
            return;
        }
                
        reactOnChange(part, (IStructuredSelection)selection);
    }

    
    
    /**
     * @param paramBP
     *            the paramBP to set
     */
    private void setParamBP(AbstractParamInterfaceBP paramBP) {
        m_paramBP = paramBP;
    }

    /**
     * @return the paramBP
     */
    private AbstractParamInterfaceBP getParamBP() {
        return m_paramBP;
    }

    /**
     * @param paramInterfaceObj
     *            the paramInterfaceObj to set
     */
    private void setParamInterfaceObj(IParameterInterfacePO paramInterfaceObj) {
        m_paramInterfaceObj = paramInterfaceObj;
    }

    /**
     * @return the paramInterfaceObj
     */
    private IParameterInterfacePO getParamInterfaceObj() {
        return m_paramInterfaceObj;
    }
    
    /**
     * hint: the string could be null.
     * 
     * @param value
     *            to convert
     * @param paramInterfaceObj
     *            obj with parameter for this parameterValue
     * @param currentParamDescription
     *            param description associated with current string (parameter
     *            value)
     * @param dataValidator
     *            to use for special validations
     * @return a valid GuiParamValueConverter
     */
    private GuiParamValueConverter getGuiParamValueConverter(String value,
            IParameterInterfacePO paramInterfaceObj,
            IParamDescriptionPO currentParamDescription,
            IParamValueValidator dataValidator) {
        return new GuiParamValueConverter(value, paramInterfaceObj,
                currentParamDescription, dataValidator);
    }
    
    /**
     * 
     * @param paramInterface The object on which the input is based.
     * @return an object suitable for use as input in a DSV table.
     */
    protected ITDManager getInputForTable(
            IParameterInterfacePO paramInterface) {
        return paramInterface.getDataManager();
    }

    /**
     * @param tableCursor the tableCursor to set
     */
    private void setTableCursor(DSVTableCursor tableCursor) {
        m_tableCursor = tableCursor;
    }

    /**
     * @return the tableCursor
     */
    public DSVTableCursor getTableCursor() {
        return m_tableCursor;
    }
}
