/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.alm.mylyn.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.views.ColumnViewerSorter;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public class InspectALMAttributesDialog extends TitleAreaDialog {

    /** the task attribute */
    private TaskAttribute m_taskAttribute;

    /**
     * 
     * @param parentShell
     *            parent shell
     * @param taskAttribute
     *            the attribute which all sub attributes should be displayed
     */
    public InspectALMAttributesDialog(Shell parentShell,
            TaskAttribute taskAttribute) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        m_taskAttribute = taskAttribute;

    }

    /**
     *
     * @param <E>
     *            the type of the value
     */
    private class KeyValue<E> {
        /** the key in cases of TaskAttribute this is mostly the attributeID */
        private String m_key;
        /** value */
        private E m_value;

        /**
         * @param key
         *            key
         * @param value
         *            the value of the specified type
         */
        public KeyValue(String key, E value) {
            m_key = key;
            m_value = value;
        }

        /**
         * @return the key
         */
        public String getKey() {
            return m_key;
        }

        /**
         * @return the value
         */
        public E getValue() {
            return m_value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.InspectorTitle);
        setMessage(Messages.InspectorMessage);
        getShell().setText(Messages.InspectorTitle);
        parent.setLayout(new GridLayout());
        Composite comp = new Composite(parent, SWT.None);
        comp.setLayout(new GridLayout());

        TableViewer tableViewer = new TableViewer(comp, SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.BORDER);
        // make lines and header visible
        final Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        tableViewer.setContentProvider(new IStructuredContentProvider() {

            public void inputChanged(Viewer viewer, Object oldInput,
                    Object newInput) {
                // nothing

            }

            public void dispose() {
                // nothing
            }

            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof Map) {
                    Map<String, TaskAttribute> a = 
                            (Map<String, TaskAttribute>) inputElement;
                    ArrayList<KeyValue<TaskAttribute>> list = 
                            new ArrayList<KeyValue<TaskAttribute>>();
                    for (String attributeID : a.keySet()) {
                        list.add(new KeyValue<TaskAttribute>(attributeID,
                                a.get(attributeID)));
                    }
                    return list.toArray();
                }
                return null;
            }
        });

        createAttributeIDColumn(tableViewer);
        createValueColumn(tableViewer);
        createOptionsColumn(tableViewer);
        createPropertiesColumn(tableViewer);
        tableViewer.setInput(m_taskAttribute.getAttributes());
        GridData gridData2 = new GridData();
        gridData2.verticalAlignment = GridData.FILL;
        gridData2.horizontalSpan = 2;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;
        gridData2.horizontalAlignment = GridData.FILL;
        tableViewer.getControl().setLayoutData(gridData2);

        final GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.heightHint = 600;
        comp.setLayoutData(gridData);

        return parent;
    }

    /**
     * creates the attributesID column
     * @param tableViewer the tableviewer
     */
    @SuppressWarnings("unchecked")
    private void createAttributeIDColumn(TableViewer tableViewer) {
        TableViewerColumn colAttributeID = new TableViewerColumn(tableViewer,
                SWT.NONE);
        colAttributeID.getColumn().setWidth(250);
        colAttributeID.getColumn().setText(Messages.InspectorTableAttributeID);
        colAttributeID.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                KeyValue<TaskAttribute> p = (KeyValue<TaskAttribute>) element;
                return p.getKey();
            }
        });
        new ColumnViewerSorter(tableViewer, colAttributeID) {
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((KeyValue<TaskAttribute>) e1).getKey(),
                        ((KeyValue<TaskAttribute>) e2).getKey());
            }
        };
    }

    /**
     * creates the value column
     * @param tableViewer the tableviewer
     */
    @SuppressWarnings("unchecked")
    private void createValueColumn(TableViewer tableViewer) {
        TableViewerColumn value = new TableViewerColumn(tableViewer, SWT.NONE);
        value.getColumn().setWidth(200);
        value.getColumn().setText(Messages.InspectorTableValue);
        value.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                KeyValue<TaskAttribute> p = (KeyValue<TaskAttribute>) element;
                return p.m_value.getValue();
            }
        });
        new ColumnViewerSorter(tableViewer, value) {
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((KeyValue<TaskAttribute>) e1).getValue().getValue(),
                        ((KeyValue<TaskAttribute>) e2).getValue().getValue());
            }
        };
    }

    /**
     * creates the options column which has a mapping between valid values and its meaning
     * @param tableViewer the tableviewer
     */
    @SuppressWarnings("unchecked")
    private void createOptionsColumn(TableViewer tableViewer) {
        TableViewerColumn options = new TableViewerColumn(
                tableViewer, SWT.NONE);
        options.getColumn().setWidth(300);
        options.getColumn().setText(Messages.InspecotrTableOptions);
        options.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                KeyValue<TaskAttribute> p = (KeyValue<TaskAttribute>) element;
                String text = p.getValue().getOptions().toString();
                text = StringUtils.substring(text, 1, text.length() - 1);
                return text;
            }
        });
        new ColumnViewerSorter(tableViewer, options) {
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((KeyValue<TaskAttribute>) e1).getValue().getOptions()
                                .toString(),
                        ((KeyValue<TaskAttribute>) e2).getValue().getOptions()
                                .toString());
            }
        };
        ColumnViewerToolTipSupport.enableFor(tableViewer);
    }
    
    /**
     * creates the properties column
     * @param tableViewer the tableviewer
     */
    @SuppressWarnings("unchecked")
    private void createPropertiesColumn(TableViewer tableViewer) {
        TableViewerColumn propertiesColumn = new TableViewerColumn(tableViewer,
                SWT.NONE);
        propertiesColumn.getColumn().setWidth(120);
        propertiesColumn.getColumn().setText(Messages.InspectorTableProperties);
        propertiesColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                KeyValue<TaskAttribute> p = (KeyValue<TaskAttribute>) element;
                String returnValue = StringConstants.EMPTY;
                if (p.m_value.getMetaData().isReadOnly()) {
                    returnValue += Messages.InspectorTablePropertieReadOnly;
                }
                if (p.m_value.getMetaData().isDisabled()) {
                    if (returnValue.length() == 0) {
                        returnValue += Messages.InspectorTablePropertieDisabled;
                    } else {
                        returnValue += StringConstants.COMMA 
                                + StringConstants.SPACE
                                + Messages.InspectorTablePropertieDisabled;
                    }
                }
                return returnValue;
            }
        });
    }

    /**
     * @return a null safe natural comparator
     */
    private Comparator getCommonsComparator() {
        return ComparatorUtils.nullHighComparator(ComparatorUtils
                .naturalComparator());
    }
}
