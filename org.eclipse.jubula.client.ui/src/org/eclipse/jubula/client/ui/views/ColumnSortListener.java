/*******************************************************************************
 * Copyright (c) 2010, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Comparator for the table view. Implements column-based sorting. The column
 * used for sorting is changed by clicking on a column header. Clicking again on
 * the same column header reverses the direction of the sorting.
 * 
 * @created Jan 26, 2010
 */
public class ColumnSortListener implements SelectionListener {

    /** TableViewer */
    private TableViewer m_tableViewer;

    /**
     * Constructor
     * 
     * @param viewer
     *            TableViewer
     * @param initialSortColumn
     *            The column to use initially for sorting.
     */
    public ColumnSortListener(TableViewer viewer, 
        TableColumn initialSortColumn) {
        m_tableViewer = viewer;
        final Table parent = initialSortColumn.getParent();
        parent.setSortColumn(initialSortColumn);
        parent.setSortDirection(SWT.DOWN);
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
            TableColumn column = (TableColumn) e.widget;
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
}