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

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ReusedCompNameValidator;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IResetFrameColourListener;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.editors.IJBEditor;
import org.eclipse.jubula.client.ui.editors.TestSuiteEditor;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.utils.ResetColourAdapter;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.widgets.CompNameCellValidator;
import org.eclipse.jubula.client.ui.widgets.CompNamePopupTextCellEditor;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;


/**
 * @author BREDEX GmbH
 * @created Sep 10, 2008
 */
@SuppressWarnings("synthetic-access")
public class CompNamesPage extends Page implements ISelectionListener {

    /**
     * Internal name of the second column.
     */
    private static final String COLUMN_PROPAGATE = "propagate"; //$NON-NLS-1$
    /**
     * Internal name of the third column.
     */
    private static final String COLUMN_OLD_NAME = "firstName"; //$NON-NLS-1$
    /**
     * Internal name of the fourth column.
     */
    private static final String COLUMN_NEW_NAME = "secondName"; //$NON-NLS-1$
    /**
     * Internal name of the fifth column.
     */
    private static final String COLUMN_TYPE_NAME = "type"; //$NON-NLS-1$
    
    /** Constant for the default column witdh */ 
    private static final int COLUMN_WIDTH = 70;

    /**
     * The business process that performs component name operations.
     */
    private CompNamesBP m_compNamesBP = new CompNamesBP();
    /**
     * The currently selected test execution node, may be <code>null</code>,
     * if no test execution node ist selected.
     */
    private IExecTestCasePO m_selectedExecNode;
    
    /**
     * The currently selected test execution node, may be <code>null</code>,
     * if no test execution node ist selected.
     */
    private IExecTestCasePO m_oldSelectedExecNode;
    /**
     * The owner of the currently selected test execution node, may be
     * <code>null</code>, if no test execution node ist selected or if the
     * part has been closed.
     */
    private IWorkbenchPart m_selectedExecNodeOwner;
    /**
     * The owner of the currently selected test execution node, may be
     * <code>null</code>, if no test execution node ist selected or if the
     * part has been closed.
     */
    private IWorkbenchPart m_oldSelectedExecNodeOwner;
    /**
     * The table viewer
     */
    private CheckboxTableViewer m_tableViewer;
    
    /** adapter to set the frame colour */
    private ResetColourAdapter m_colourAdapter;
    
    /** the primary control for this page */
    private Control m_control;

    /** observation of events need a reset of frame colour */
    private IResetFrameColourListener m_resetFrameColourListener = 
        new IResetFrameColourListener() {
        
            public void eventOccured(List< ? extends Object> params) {
                m_colourAdapter.resetColouredFrame();
            }
            public void checkGenericListElementType(
                    List< ? extends Object> params) {
            // do nothing
            }
        };
    
    /**
     * The currently selected compNamesPair, or <code>nully</code> if no 
     * compNamesPair is currently selected.
     */
    private ICompNamesPairPO m_selectedPair = null;

    /**
     * The selection changed listener of the tableViewer
     */
    private TableSelectionChangedListener m_selectionChangedListener = 
        new TableSelectionChangedListener();
    
    /** the cell editor for this table */
    private CompNamePopupTextCellEditor m_cellEdit;
    /** the cell modifier of this the actual tableViewer */
    private CellModifier m_cellModifier;
    /** the cell editor listener */
    private CellEditorListener m_cellEditorListener = new CellEditorListener();
    /** the check state listener */
    private CheckStateListener m_checkStateListener = new CheckStateListener();
    
    /** is view editable at the moment */
    private boolean m_editable;
    
    /** the component mapper to use for finding and modifying components */
    private IComponentNameMapper m_compMapper;
    
    /** flag to indicate if invalid data has been entered */
    private boolean m_invalidData = false;
    
    /**
     * @author BREDEX GmbH
     * @created Jan 22, 2007
     */
    private final class CheckStateListener implements ICheckStateListener {
        /**
         * {@inheritDoc}
         */
        public void checkStateChanged(CheckStateChangedEvent event) {
            ICompNamesPairPO pair = (ICompNamesPairPO)event.getElement();
            m_cellModifier.setModifiable(m_editable
                && !StringConstants.EMPTY.equals(
                    pair.getType()));
            if (!m_cellModifier.isModifiable() 
                || m_selectedExecNodeOwner instanceof TestSuiteEditor) { 
                
                // Reset the old value if the table is non-editable.
                m_tableViewer.setChecked(pair, pair.isPropagated());
                return;
            }
            updatePropagated(pair, event.getChecked());
        }
    }
    
    /**
     * @author BREDEX GmbH
     * @created Jan 22, 2007
     */
    private final class CellEditorListener implements ICellEditorListener {
        /**
         * {@inheritDoc}
         */
        public void applyEditorValue() {
            final Object value = m_cellEdit.getValue();
            final String newName = (value != null) 
                ? value.toString() : StringConstants.EMPTY;
            if (m_cellEdit.isDirty()) {
                if (updateSecondName(m_selectedPair, newName)) {
                    setActualTCEditorDirty();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public void cancelEditor() {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void editorValueChanged(boolean oldValidState, 
            boolean newValidState) { 
            // Do nothing
        }
    }
    
    /**
     * The content provider of the table.
     */
    private class CompNamesViewContentProvider implements
        IStructuredContentProvider {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            return ((List)inputElement).toArray();
        }
        
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // Nothing to dispose
        }
        
        /**
         * {@inheritDoc}
         *      java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput,
            Object newInput) {
            // Nothing to be done
        }
    }
    /**
     * The label provider of the table.
     */
    private class LabelProvider implements ITableLabelProvider {
        /**
         * {@inheritDoc}
         */
        public void addListener(ILabelProviderListener listener) {
            // No listeners supported
        }
        
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // Nothing to dispose
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeListener(ILabelProviderListener listener) {
            // No listeners supported
        }
        
        /**
         * {@inheritDoc}
         */
        public Image getColumnImage(Object element, int columnIndex) {
            searchAndSetComponentType((ICompNamesPairPO)element);
            String type = ((ICompNamesPairPO)element).getType();
            switch (columnIndex) {
                case 0:
                    if (StringConstants.EMPTY.equals(type)) { 
                        m_tableViewer.getTable().getColumn(0).pack();
                        return IconConstants.WARNING_IMAGE;
                    }
                    return null;
                default:
                    break;
            }
            return null;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getColumnText(Object element, int columnIndex) {
            ICompNamesPairPO pair = (ICompNamesPairPO)element;
            Table table = m_tableViewer.getTable();
            switch (columnIndex) {
                case 0:
                    return StringConstants.EMPTY;
                case 1:
                    if (m_compMapper != null) {
                        return m_compMapper.getCompNameCache()
                            .getName(pair.getFirstName());
                    }
                    return ComponentNamesBP.getInstance().getName(
                            pair.getFirstName());
                case 2:
                    if (m_compMapper != null) {
                        return m_compMapper.getCompNameCache()
                            .getName(pair.getSecondName());
                    }
                    return ComponentNamesBP.getInstance().getName(
                            pair.getSecondName());
                case 3:
                    searchAndSetComponentType(pair);
                    String type = pair.getType();
                    for (int i = 0; i < table.getItems().length; i++) {
                        if (table.getItems()[i].getData() != null
                            && table.getItems()[i].getData().equals(pair)) {
                            
                            if (StringConstants.EMPTY.equals(type)) {
                                type = "CompNamesView.errorText"; //$NON-NLS-1$
                                
                                TableItem item = table.getItem(i);
                                item.setForeground(3, Layout.ERROR_COLOR);
                                item.setFont(3, Layout.ITALIC_TAHOMA);
                                if (m_selectedExecNodeOwner 
                                        instanceof AbstractTestCaseEditor) {
                                    
                                    m_tableViewer.setGrayed(pair, false);
                                    m_tableViewer.setChecked(pair, false);
                                }
                            } else {
                                TableItem item = table.getItem(i);
                                item.setForeground(3, Layout.GRAY_COLOR);
                                item.setFont(3, Layout.NORMAL_TAHOMA);
                                if (m_selectedExecNodeOwner 
                                        instanceof AbstractTestCaseEditor) {
                                    
                                    item.setForeground(3, Layout
                                        .DEFAULT_OS_COLOR);
                                    m_tableViewer.setGrayed(pair, true);
                                    m_tableViewer.setChecked(pair, true);
                                }
                            }
                        }
                    }
                    return CompSystemI18n.getString(type);
                default:
                    break;
            }
            return null;
        }
    }
    
    /**
     * 
     * @param pair the current compNamesPairPO
     */
    private void searchAndSetComponentType(final ICompNamesPairPO pair) {
        if (pair.getType() != null 
                && !StringConstants.EMPTY.equals(pair.getType())) {
            return;
        }
        if ((m_selectedExecNodeOwner instanceof IJBEditor)) {
            final IPersistentObject orig = 
                ((IJBEditor)m_selectedExecNodeOwner).getEditorHelper()
                    .getEditSupport().getOriginal();
            if (orig instanceof ISpecTestCasePO 
                || orig instanceof ITestSuitePO) {
                
                INodePO origNode = (INodePO)orig;
                for (Object node : origNode.getUnmodifiableNodeList()) {
                    if (CompNamesBP.searchCompType(pair, node)) {
                        return;
                    }
                }
            }
        }
        // if exec was added to an editor session
        if (pair.getType() == null 
                || StringConstants.EMPTY.equals(pair.getType())) {
            CompNamesBP.searchCompType(pair, m_selectedExecNode);
        }
    }


    /**
     * The cell modifier of the table. It supports the modification of the
     * second (new) component name.
     */
    public class CellModifier implements ICellModifier {
        /**
         * Flag to indicate if the table is modifiable.
         */
        private boolean m_modifiable = true;
        
        /**
         * @param element
         *            The current table element
         * @return The component name pair
         */
        private ICompNamesPairPO getPair(Object element) {
            return (ICompNamesPairPO)(element instanceof Item ? ((Item)element)
                .getData() : element);
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean canModify(Object element, String property) {
            if (element instanceof ICompNamesPairPO) {
                final ICompNamesPairPO compNamesPair = (ICompNamesPairPO)
                    element;
                return COLUMN_NEW_NAME.equals(property) && isModifiable()
                    && compNamesPair.getType().length() != 0;
            }
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getValue(Object element, String property) {
            if (m_compMapper != null) {
                return m_compMapper.getCompNameCache().getName(
                        getPair(element).getSecondName());
            }
            return ComponentNamesBP.getInstance().getName(
                    getPair(element).getSecondName());
        }
        
        /**
         * {@inheritDoc}
         */
        public void modify(Object element, String property, Object value) {
            if (getValue(element, property).equals(value)
                    || (value != null 
                        && ((String)value).trim().length() == 0)) {
                return;
            }
            updateSecondName(getPair(element), (String)value);
        }
        
        /**
         * @param modifiable The modifiable property to set.
         */
        public void setModifiable(boolean modifiable) {
            m_modifiable = modifiable;
        }
        
        /**
         * @return Returns the modifiable property.
         */
        public boolean isModifiable() {
            return m_modifiable;
        }
    }
    
    /**
     * SelectionChangedListener for the tableViewer
     *
     * @author BREDEX GmbH
     * @created 13.06.2006
     */
    private class TableSelectionChangedListener 
        implements ISelectionChangedListener {
        
        /**
         * {@inheritDoc}
         * @param event
         */
        public void selectionChanged(SelectionChangedEvent event) {
            if (!(event.getSelection() instanceof IStructuredSelection)) {
                return;
            }
            Object o = ((IStructuredSelection)event.getSelection())
                .getFirstElement();
            if (o instanceof ICompNamesPairPO 
                    && m_selectedExecNodeOwner 
                        instanceof AbstractTestCaseEditor) {
                
                ICompNamesPairPO pair = (ICompNamesPairPO)o;
                m_selectedPair = pair;
                m_cellModifier.setModifiable(m_editable 
                    && !StringConstants.EMPTY.equals(pair.getType()));
                if (!m_cellModifier.isModifiable()) {
                    return;
                }
                String filter = pair.getType();
                if (filter == null || StringConstants.EMPTY.equals(filter)) {
                    IExecTestCasePO execNode = m_selectedExecNode;
                    // search in the original session the correct compType
                    IPersistentObject po = ((AbstractTestCaseEditor)
                        m_selectedExecNodeOwner).getEditorHelper()
                        .getEditSupport().getOriginal();
                    if (po instanceof ISpecTestCasePO) {
                        for (Object node : ((ISpecTestCasePO)po)
                                .getUnmodifiableNodeList()) {
                            
                            if (node.equals(m_selectedExecNode)) {
                                execNode = (IExecTestCasePO)node;
                                break;
                            }
                        }
                    }
                    filter = Utils.getComponentType(execNode, 
                        pair.getFirstName());
                }
                m_cellEdit.setFilter(filter);
                setInvalidData(false);
            } else {
                m_selectedPair = null;
            }
        }
    }
    
    /**
     * Updates the view by setting the editor dirty (this implies a lock on the
     * current editied test specification node) and refreshes the table viewer.
     * Notifies the model listeners. This method is called whenever the model
     * (the selected test execution node and/or a component name pair) is being
     * changed.
     */
    private void updateViewAndNotifyModelListeners() {
        setActualTCEditorDirty();
        m_tableViewer.refresh();
    }
    
    /**
     * Sets the actual editor dirty.
     */
    private void setActualTCEditorDirty() {
        AbstractTestCaseEditor edit = Plugin.getDefault().getActiveTCEditor();
        if (edit != null 
                && edit.getEditorHelper().requestEditableState() 
                    == JBEditorHelper.EditableState.OK
                && !edit.isDirty()) {
            
            edit.getEditorHelper().setDirty(true);
        }
    }
    
    /**
     * Updates the propagated property of the pair.
     * 
     * @param pair The pair
     * @param propagated The propagated property
     */
    private void updatePropagated(ICompNamesPairPO pair, boolean propagated) {
        boolean updated = m_compNamesBP.updateCompNamesPair(m_selectedExecNode,
            pair, propagated);
        if (updated) {
            updateViewAndNotifyModelListeners();
        }
    }
    
    /**
     * Updates the second name property of the pair.
     * @param pair The pair
     * @param secondName The second name
     * @return true if update has been successfull
     */
    private boolean updateSecondName(ICompNamesPairPO pair, String secondName) {
        boolean updateSuccessfull = false;
        if (!getInvalidData()) {
            if (m_selectedExecNodeOwner instanceof IJBEditor) {
                if (m_selectedExecNode != null) {
                    final IJBEditor editor = (IJBEditor)m_selectedExecNodeOwner;
                    final IWritableComponentNameMapper compMapper = editor
                            .getEditorHelper().getEditSupport().getCompMapper();
                    try {
                        final boolean updated = m_compNamesBP
                                .updateCompNamesPair(m_selectedExecNode, pair,
                                        secondName, compMapper);
                        if (updated) {
                            updateSuccessfull = updated;
                            updateViewAndNotifyModelListeners();
                        }
                    } catch (IncompatibleTypeException e) {
                        setInvalidData(true);
                        Utils.createMessageDialog(e, e.getErrorMessageParams(),
                                null);
                    } catch (PMAlreadyLockedException pme) {
                        setInvalidData(true);
                        PMExceptionHandler.handlePMAlreadyLockedException(pme,
                                new String[] { "The component name \"" //$NON-NLS-1$
                                                + secondName
                                                + "\" is already in use in a different editor." //$NON-NLS-1$
                                                , "Please save these editor(s) first." }); //$NON-NLS-1$
                    } catch (PMException pme) {
                        setInvalidData(true);
                        PMExceptionHandler.handlePMExceptionForEditor(pme, 
                                editor);
                    }
                }
            } else {
                Assert.notReached("m_selectedExecNodeOwner is not an instance of IGDEditor"); //$NON-NLS-1$
            }
        }
        return updateSuccessfull;
    }
    
    /**
     * Updates the table input by calling
     * {@link CompNamesBP#getAllCompNamesPairs(ExecTestCasePO)} (passing the
     * selected node).
     *  @param editable If <code>false</code>, the table is set to non-editable,
     *            otherwise, it is set to editable.
     */
    private void updateTableInput(boolean editable) {
        List<ICompNamesPairPO> input = null;
        if (m_selectedExecNode != null) {
            input = m_compNamesBP.getAllCompNamesPairs(m_selectedExecNode);

            // Add validator
            for (ICompNamesPairPO pair : input) {
                searchAndSetComponentType(pair);
            }

            IWorkbenchPart activePart = Plugin.getActivePart();
            if (activePart instanceof IJBEditor) {
                m_compMapper = ((IJBEditor)activePart).getEditorHelper()
                    .getEditSupport().getCompMapper();
            }
            m_cellEdit.setValidator(new CompNameCellValidator(m_tableViewer, 
                new ReusedCompNameValidator(m_compMapper)));
        }
        m_tableViewer.setInput(input);
        // Set the table to (non-) editable. We don't use Control.setEditable()
        // here because this crays out the table, and this doesn't fit
        // the look and feel of other views.
        m_cellModifier.setModifiable(editable);
        m_editable = editable;
        for (TableItem item : m_tableViewer.getTable().getItems()) {
            if (editable) {
                item.setForeground(Layout.DEFAULT_OS_COLOR);
            } else {
                item.setForeground(Layout.GRAY_COLOR);
            }
            item.setGrayed(!editable);
        }
        // packs all cloumns (inspite of the first)
        Table table = m_tableViewer.getTable();
        if (table.getItemCount() != 0) {
            final TableColumn[] columns = table.getColumns();
            final int columnCount = columns.length;
            for (int i = 1; i < columnCount; i++) {
                TableColumn column = columns[i];
                column.pack();
                if (column.getWidth() < COLUMN_WIDTH) {
                    column.setWidth(COLUMN_WIDTH);
                }
            }
            for (ICompNamesPairPO pair : input) {
                m_tableViewer.setChecked(pair, pair.isPropagated());
            }
            table.getColumn(0).setWidth(38);
            table.getColumn(0).setResizable(false);
        }
    }
    
    
    
    
    /**
     * Sets the layout of the parent composite.
     * @param parent the parent composite 
     */
    private void setParentLayout(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 2;
        layout.marginWidth = Layout.MARGIN_WIDTH;
        layout.marginHeight = Layout.MARGIN_HEIGHT;
        parent.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        parent.setLayoutData(layoutData);
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        m_tableViewer.getTable().setFocus();
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        m_cellEdit.removeListener(m_cellEditorListener);
        m_tableViewer.removeCheckStateListener(m_checkStateListener);
        m_tableViewer.removeSelectionChangedListener(
            m_selectionChangedListener);
        m_tableViewer = null;
        m_selectedExecNode = null;
        m_selectedExecNodeOwner = null;
        super.dispose();
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleEditorSaved(IWorkbenchPart part, ISelection selection) {
        m_oldSelectedExecNode = null;
        selectionChanged(part, selection);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        Composite topLevelComposite = new Composite(parent, SWT.NONE);
        setParentLayout(topLevelComposite);
        m_control = topLevelComposite;
        
        m_colourAdapter = new ResetColourAdapter(topLevelComposite);
        setParentLayout(topLevelComposite);
        Table table = new Table(topLevelComposite, SWT.BORDER | SWT.CHECK
                | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        final TableColumn tc1 = new TableColumn(table, SWT.CENTER);
        tc1.setImage(IconConstants.PROPAGATE_IMAGE);
        final TableColumn tc2 = new TableColumn(table, SWT.LEFT);
        tc2.setText(I18n.getString("CompNamesView.OldNameColumn")); //$NON-NLS-1$
        final TableColumn tc3 = new TableColumn(table, SWT.LEFT);
        tc3.setText(I18n.getString("CompNamesView.NewNameColumn")); //$NON-NLS-1$
        final TableColumn tc4 = new TableColumn(table, SWT.LEFT);
        tc4.setText(I18n.getString("CompNamesView.typeColumn")); //$NON-NLS-1$
        final CompNamesViewContentProvider provider =
            new CompNamesViewContentProvider();
        m_tableViewer = new CheckboxTableViewer(table);
        m_tableViewer.setContentProvider(provider);
        m_tableViewer.setLabelProvider(new LabelProvider());
        m_tableViewer.setColumnProperties(new String[] { COLUMN_PROPAGATE, 
            COLUMN_OLD_NAME, COLUMN_NEW_NAME, COLUMN_TYPE_NAME });

        m_compMapper = Plugin.getActiveCompMapper();
        m_cellEdit = 
            new CompNamePopupTextCellEditor(m_compMapper, table);
        setInvalidData(false);
        m_tableViewer.setCellEditors(new CellEditor[] { null, null, 
            m_cellEdit, null});
        m_cellEdit.addListener(m_cellEditorListener);
        m_cellModifier = new CellModifier();
        m_tableViewer.setCellModifier(m_cellModifier); 
        m_tableViewer.addCheckStateListener(m_checkStateListener);
        m_tableViewer.addSelectionChangedListener(m_selectionChangedListener);
        Plugin.getHelpSystem().setHelp(topLevelComposite, 
                ContextHelpIds.COMP_NAME);
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
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!(selection instanceof StructuredSelection)) { 
            // e.g. in Jubula plugin-version you can open an java editor, 
            // that reacts on org.eclipse.jface.text.TextSelection, which
            // is not a StructuredSelection
            return;
        }
        boolean isEditor = (part instanceof AbstractTestCaseEditor);
        IStructuredSelection sel = (IStructuredSelection)selection;
        if (sel.getFirstElement() instanceof ExecTestCaseGUI) {
            m_selectedExecNode = (IExecTestCasePO)
                ((ExecTestCaseGUI)sel.getFirstElement()).getContent();
            m_selectedExecNodeOwner = part;
        } else {
            m_selectedExecNode = null;
        }
        if ((m_selectedExecNode == null) || (m_selectedExecNode != null 
                && !(m_selectedExecNode.equals(m_oldSelectedExecNode)))
                || (!(m_selectedExecNodeOwner.equals(
                        m_oldSelectedExecNodeOwner)))) {
            
            if (part instanceof IJBEditor) {
                m_cellEdit.setComponentNameMapper(
                    ((IJBEditor)part).getEditorHelper().getEditSupport()
                        .getCompMapper());
            }
            
            updateTableInput(isEditor);
            if (isEditor) {
                AbstractTestCaseEditor editor = (AbstractTestCaseEditor)part;
                if (editor.getEditorInput() instanceof ITestSuitePO) {
                    m_cellModifier.setModifiable(false);
                }
            }
        }
        m_oldSelectedExecNode = m_selectedExecNode;
        m_oldSelectedExecNodeOwner = m_selectedExecNodeOwner;
    }

    /**
     * @param invalidData the flag to indicate that invalid data has been detected
     */
    private void setInvalidData(boolean invalidData) {
        m_invalidData = invalidData;
    }

    /**
     * @return the invalidData flag
     */
    private boolean getInvalidData() {
        return m_invalidData;
    }

}
