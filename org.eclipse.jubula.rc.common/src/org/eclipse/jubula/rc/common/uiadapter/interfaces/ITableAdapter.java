/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.uiadapter.interfaces;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;

/**
 * Interface for all necessary methods for testing tables.
 * 
 * @author BREDEX GmbH
 */
public interface ITableAdapter extends ITextVerifiable {
    
    
    /**
     * Gets the number of columns
     * 
     * @return the number of columns
     */
    public int getColumnCount();
    
    /**
     * Gets the number of rows
     * 
     * @return the number of rows
     */
    public int getRowCount();
    
    /**
     * @param row
     *            the zero based index of the row
     * @param column
     *            the zero based index of the column
     * @return the text of the cell of the given coordinates
     */
    public String getCellText(int row, int column);
     
    /**
     * Returns the name of the column appearing in the view at column position
     * <code>column</code>.
     * 
     * @param column
     *            the zero based index of the column in the view being queried
     * @return the name of the column at position <code>column</code> in the
     *         view where the first column is column 0
     */
    public String getColumnName(int column);

    /**
     * Gets column index from string with header name or index
     * 
     * @param col
     *            Headername or index of column
     * @param operator
     *            the operation used to verify
     * @return column index
     */
    public int getColumnFromString(String col, String operator);
    
    /**
     * This is only for a specific case where tables could act like lists. And
     * the getText is not working. If this is not the case for the component
     * return <code>null</code>
     * 
     * @param row
     *            the zero based index of the row
     * @return the text of the row
     */
    public String getRowName(int row);
    
    /**
     * Gets row index from string with index or text of first row
     * 
     * @param row
     *            index or value in first col
     * @param operator
     *            the operation used to verify
     * @return integer of String of row
     */
    public int getRowFromString(String row, String operator);
    
    /**
     * gets header bounds for column
     * 
     * @param col
     *            the zero based index of the column.
     * @return The rectangle of the header
     */
    public Rectangle getHeaderBounds(int col);

    /**
     * @return The currently selected cell of the Table.
     * @throws StepExecutionException
     *             If no cell is selected.
     */
    public Cell getSelectedCell() throws StepExecutionException;
    
    /**
     * 
     * @return <code>true</code> if the header is visible, <code>false</code> otherwise
     */
    public boolean isHeaderVisible();

    /**
     * 
     * @param row
     *            zero based index of the row
     * @param col
     *            zero based index of the column
     * @return <code>true</code> if the Cell is editable, <code>false</code>
     *         otherwise
     */
    public boolean isCellEditable(int row, int col);

    /**
     * @return <code>true</code>, if there is any cell selection in the table,
     *         <code>false</code> otherwise.
     */
    public boolean hasCellSelection();
    
    /**
     * Scrolls the passed cell (as row and column) to visible.<br>
     * This method must return null if there is no scrolling.
     * 
     * @param row
     *            zero based index of the row.
     * @param col
     *            zero based index of the column.
     * @return The rectangle of the cell.
     * @throws StepExecutionException
     *             If getting the cell rectangle or the scrolling fails.
     */
    public Rectangle scrollCellToVisible (int row , int col)
        throws StepExecutionException;

    /**
     * @return The TableHeader if there is one,otherwise
     *                  the table is returned.
     */
    public Object getTableHeader();
    

}
