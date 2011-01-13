package org.eclipse.jubula.client.ui.views;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


/*******************************************************************************
 * Copyright (c) 2010, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

/**
 * Comparator for the table view. Implements column-based sorting. The
 * column used for sorting is changed by clicking on a column header.
 * Clicking again on the same column header reverses the direction of the
 * sorting.
 * @created Jan 26, 2010
 */
public class ColumnSortListener extends ViewerComparator implements
    SelectionListener {
    
    /** TableViewer */
    private TableViewer m_tableViewer;
    
    /**
     * Constructor
     * @param viewer TableViewer
     * @param initialSortColumn The column to use initially for sorting.
     */
    public ColumnSortListener(TableViewer viewer,
            TableColumn initialSortColumn) {
        m_tableViewer = viewer;
        initialSortColumn.getParent().setSortColumn(initialSortColumn);
        initialSortColumn.getParent().setSortDirection(SWT.DOWN);
    }

    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void widgetSelected(SelectionEvent e) {
        if (e.widget instanceof TableColumn) {
            TableColumn column = (TableColumn)e.widget;
            Table parent = column.getParent();
            if (parent.getSortColumn() == column) {
                if (parent.getSortDirection() == SWT.DOWN) {
                    parent.setSortDirection(SWT.UP);
                } else {
                    parent.setSortDirection(SWT.DOWN);
                }
            } else {
                parent.setSortColumn(column);
                parent.setSortDirection(SWT.DOWN);
            }
            m_tableViewer.refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (viewer instanceof TableViewer) {
            TableViewer tableViewer = (TableViewer)viewer;
            CellLabelProvider cellLabelProvider = tableViewer
                    .getLabelProvider(tableViewer.getTable().indexOf(
                            tableViewer.getTable().getSortColumn()));
            if (cellLabelProvider instanceof ColumnLabelProvider) {
                ColumnLabelProvider colLabelProvider = (
                        ColumnLabelProvider)cellLabelProvider;
                String text1 = colLabelProvider.getText(e1);
                if (text1 == null) {
                    text1 = StringConstants.EMPTY;
                }
                String text2 = colLabelProvider.getText(e2);
                if (text2 == null) {
                    text2 = StringConstants.EMPTY;
                }

                int result = text1.compareToIgnoreCase(text2);
                return tableViewer.getTable().getSortDirection()
                    == SWT.DOWN ? result : -result;
            }
        }
        return super.compare(viewer, e1, e2);
    }
}