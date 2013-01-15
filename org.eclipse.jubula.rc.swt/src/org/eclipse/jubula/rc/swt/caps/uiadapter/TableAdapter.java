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
package org.eclipse.jubula.rc.swt.caps.uiadapter;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.ITableAdapter;
import org.eclipse.jubula.rc.swt.listener.TableSelectionTracker;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
/**
 * Implements the Table interface for adapting a <code>SWT.Table</code>
 * 
 * @author BREDEX GmbH
 */
public class TableAdapter extends WidgetAdapter implements ITableAdapter {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        TableAdapter.class);
    
    /**   */
    private Table m_table;
    
    /**
     * 
     * @param objectToAdapt graphics component which will be adapted
     */
    public TableAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_table = (Table) objectToAdapt;
    }

    /** {@inheritDoc} */
    public int getColumnCount() {
        Integer returnvalue = (Integer) getEventThreadQueuer().invokeAndWait(
                "getColumnCount", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return  new Integer(m_table.getColumnCount());
                    }
                });
        return returnvalue.intValue();
    }

    /** {@inheritDoc} */
    public int getRowCount() {
        Integer returnvalue = (Integer) getEventThreadQueuer().invokeAndWait(
                "getRowCount", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return new Integer(m_table.getItemCount());
                    }
                });
        return returnvalue.intValue();
    }

    /** {@inheritDoc} */
    public String getCellText(final int row, final int column) {
        String current = (String)getEventThreadQueuer().invokeAndWait("getCellText", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        String value = m_table.getItem(row).getText(column);
                        if (log.isDebugEnabled()) {
                            log.debug("Getting cell text:"); //$NON-NLS-1$
                            log.debug("Row, col: " + row + ", " + column); //$NON-NLS-1$ //$NON-NLS-2$
                            log.debug("Value: " + value); //$NON-NLS-1$
                        }
                        return value;
                    }
                });
        return current;
    }

    /** {@inheritDoc} */
    public String getColumnName(final int column) {
        String current = (String)getEventThreadQueuer().invokeAndWait("getColumnName", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        String value = m_table.getColumn(column).getText();
                        return value;
                    }
                });
        return current;
    }

    /** {@inheritDoc} */
    public int getColumnFromString(final String col, final String operator) {
        int column = -2;
        try {
            int usrIdxCol = Integer.parseInt(col);
            if (usrIdxCol == 0) {
                usrIdxCol = usrIdxCol + 1;
            }
            column = IndexConverter.toImplementationIndex(
                    usrIdxCol);
        } catch (NumberFormatException nfe) {
            try {
                Boolean isVisible;
                isVisible = (Boolean)getEventThreadQueuer().invokeAndWait(
                        "getColumnFromString", //$NON-NLS-1$
                        new IRunnable() {
                            public Object run() {
                                return new Boolean(m_table.getHeaderVisible());
                            }
                        });  
                if (!(isVisible.booleanValue())) {
                    throw new StepExecutionException("No Header", //$NON-NLS-1$
                            EventFactory.createActionError(
                                TestErrorEvent.NO_HEADER));
                }
                
                Integer implCol;
                implCol = (Integer)getEventThreadQueuer().invokeAndWait(
                    "getColumnFromString", new IRunnable() { //$NON-NLS-1$
                        public Object run() throws StepExecutionException {
                            for (int i = 0; i < m_table.getColumnCount(); i++) {
                                TableColumn tblCol = m_table.getColumn(i);
                                if (MatchUtil.getInstance().match(
                                        tblCol.getText(), col, operator)) {
                                    return new Integer (i);
                                }
                            }
                            return new Integer (-2);
                        }
                    });                
                column = implCol.intValue();                
            } catch (IllegalArgumentException iae) {
                //do nothing here
            }
        }        
        return column;
    }

    /** {@inheritDoc} */
    public String getRowName(final int row) {
        String current = (String)getEventThreadQueuer().invokeAndWait("getRowName", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        String value = m_table.getItem(row).getText();
                        return value;
                    }
                });
        return current;
    }

    /** {@inheritDoc} */
    public int getRowFromString(final String row, final String operator) {
        int rowInt = -2;        
        try {
            rowInt = IndexConverter.toImplementationIndex(
                    Integer.parseInt(row));                       
            if (rowInt == -1) {
                Boolean isVisible;
                isVisible = (Boolean)getEventThreadQueuer().invokeAndWait(
                        "getRowFromString", //$NON-NLS-1$
                        new IRunnable() {
                            public Object run() {
                                return new Boolean(m_table.getHeaderVisible());
                            }
                        }); 
                if (!(isVisible.booleanValue())) {
                    throw new StepExecutionException("Header not visible", //$NON-NLS-1$
                            EventFactory.createActionError(
                                TestErrorEvent.NO_HEADER));
                }                
            }
        } catch (NumberFormatException nfe) {
            Integer implRow;
            implRow = (Integer)getEventThreadQueuer().invokeAndWait(
                "getRowFromString", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() throws StepExecutionException {
                        for (int i = 0; i < m_table.getItemCount(); i++) {
                            String cellTxt = getCellText(i, 0);
                            if (MatchUtil.getInstance().match(
                                    cellTxt, row, operator)) {
                                return new Integer(i);
                            }
                        }
                        return new Integer(-2);
                    }
                });
            rowInt = implRow.intValue();
        }        
        return rowInt;
    }

    /** {@inheritDoc} */
    public Rectangle getBounds() {
        Rectangle returnvalue = (Rectangle) getEventThreadQueuer()
                .invokeAndWait("getBounds", //$NON-NLS-1$
                    new IRunnable() {                     
                        public Object run() throws StepExecutionException {
                            return m_table.getBounds();
                        }
                    });
        
        return returnvalue;
    }

    /** {@inheritDoc} */
    public Rectangle getHeaderBounds(final int col) {
        Rectangle cellBounds;
        cellBounds = (Rectangle)getEventThreadQueuer().invokeAndWait(
            "getHeaderBounds", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    org.eclipse.swt.graphics.Rectangle rect =
                            m_table.getItem(0).getBounds(col);
                    rect.y = m_table.getClientArea().y;
                    return new Rectangle(rect.x, rect.y, rect.width,
                            rect.height);
                }
            });
        return cellBounds;
    }

    /** {@inheritDoc} */
    public Cell getSelectedCell() throws StepExecutionException {
        Cell cell = (Cell) getEventThreadQueuer().invokeAndWait(
                "getSelectedSell", new IRunnable() {   //$NON-NLS-1$         
                    public Object run() throws StepExecutionException {
                        return TableSelectionTracker.getInstance()
                                .getSelectedCell(m_table);
                    }
                });
        return cell;
    } 

    /** {@inheritDoc} */
    public boolean isHeaderVisible() {
        Boolean isVisible;
        isVisible = (Boolean)getEventThreadQueuer().invokeAndWait(
                "isHeaderVisible", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        return new Boolean(m_table.getHeaderVisible());
                    }
                });
        return isVisible.booleanValue();
    }

    /** {@inheritDoc} */
    public boolean isCellEditable(final int row, final int col) {
        final Control cellEditor = (Control) 
                activateEditor(new Cell(row, col));
        boolean isEditable = ((Boolean)getEventThreadQueuer().invokeAndWait(
                "isCellEditable", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {

                        return isEditable(cellEditor) 
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                })).booleanValue();
        return isEditable;
    }

    /** {@inheritDoc} */
    public boolean hasCellSelection() {
        TableItem[] selItems = (TableItem[])getEventThreadQueuer()
                .invokeAndWait("hasCellSelection", //$NON-NLS-1$
                    new IRunnable() {
                        public Object run() {
                            return m_table.getSelection();
                        }
                    });
        return selItems.length > 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public Rectangle scrollCellToVisible(final int row, final int col)
        throws StepExecutionException {
        final Table table = m_table;
        getEventThreadQueuer().invokeAndWait("scrollCellToVisible", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        if (table.getColumnCount() > 0 || col > 0) {
                            table.showColumn(table.getColumn(col));
                        }
                        table.showItem(table.getItem(row));
                        return null;
                    }
                });

        final Rectangle cellBoundsRelativeToParent = getCellBounds(row, col);
            
        getEventThreadQueuer().invokeAndWait("getCellBoundsRelativeToParent", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
                    org.eclipse.swt.graphics.Point cellOriginRelativeToParent = 
                        table.getDisplay().map(
                                table, table.getParent(), 
                                new org.eclipse.swt.graphics.Point(
                                        cellBoundsRelativeToParent.x, 
                                        cellBoundsRelativeToParent.y));
                    cellBoundsRelativeToParent.x = 
                        cellOriginRelativeToParent.x;
                    cellBoundsRelativeToParent.y = 
                        cellOriginRelativeToParent.y;
                    return null;
                }
            });

        Control parent = (Control)getEventThreadQueuer().invokeAndWait("getParent", //$NON-NLS-1$
                new IRunnable() {
                public Object run() {
                    table.getParent();
                    return null;
                }
            });

            
        getRobot().scrollToVisible(
                parent, cellBoundsRelativeToParent);
        
        return getVisibleBounds(getCellBounds(row, col));
    }
    
    /**
     * 
     * @param row   The row of the cell
     * @param col   The column of the cell
     * @return The bounding rectangle for the cell, relative to the table's 
     *         location.
     */
    private Rectangle getCellBounds(final int row, final int col) {
        Rectangle cellBounds = (Rectangle)getEventThreadQueuer().invokeAndWait(
                "evaluateCellBounds", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        checkRowColBounds(row, col);
                        TableItem ti = m_table.getItem(row);
                        int column = (m_table.getColumnCount() > 0 || col > 0) 
                            ? col : 0;
                        org.eclipse.swt.graphics.Rectangle r = 
                                ti.getBounds(column);
                        String text = ti.getText(column);
                        Image image = ti.getImage(column);
                        if (text != null && text.length() != 0) {
                            GC gc = new GC(m_table);
                            int charWidth = 0; 
                            try {
                                FontMetrics fm = gc.getFontMetrics();
                                charWidth = fm.getAverageCharWidth();
                            } finally {
                                gc.dispose();
                            }
                            r.width = text.length() * charWidth;
                            if (image != null) {
                                r.width += image.getBounds().width;
                            }
                        } else if (image != null) {
                            r.width = image.getBounds().width;
                        }
                        if (column > 0) {
                            TableColumn tc = m_table.getColumn(column);
                            int alignment = tc.getAlignment();
                            if (alignment == SWT.CENTER) {
                                r.x += ((double)tc.getWidth() / 2) 
                                        - ((double)r.width / 2);
                            }
                            if (alignment == SWT.RIGHT) {
                                r.x += tc.getWidth() - r.width;
                            }
                        }
                        
                        return new Rectangle(r.x, r.y, r.width, r.height);
                    }
                });
        return cellBounds;
    }

    /**
     * Checks wether <code>0 <= value < count</code>. 
     * @param value The value to check.
     * @param count The upper bound.
     */
    private void checkBounds(int value, int count) {
        if (value < 0 || value >= count) {
            throw new StepExecutionException("Invalid row/column: " + value, //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INDEX_OR_HEADER));
        }
    }
    
    /**
     * Checks if the passed row and column are inside the bounds of the Table. 
     * @param row The row
     * @param column The column
     * @throws StepExecutionException If the row or the column is outside of the Table's bounds.
     */
    protected void checkRowColBounds(int row, int column)
        throws StepExecutionException {
        checkBounds(row, getRowCount());
        
        // Corner case: Only check the bounds if the table is not being
        //              used as a list or anything other than the first column
        //              is being checked.
        int colCount = getColumnCount();
        if (colCount > 0 || column > 0) {
            checkBounds(column, colCount);
        }
    }
    
    /**
     * Computes the visible cellBounds inside the visible bounds of the table.<br>
     * The result is the intersection of the visible bounds of the table and the 
     * bounds of the cell.
     * @param cellBounds the bounds of the cell to click in. These bounds must
     *                  be relative to the table's location.
     * @return the visible cell bounds, relative to the table's location.
     */
    private Rectangle getVisibleBounds(Rectangle cellBounds) {
        org.eclipse.swt.graphics.Rectangle r = 
            (org.eclipse.swt.graphics.Rectangle)
            getEventThreadQueuer().invokeAndWait("getVisibleCellBounds: " + cellBounds,  //$NON-NLS-1$
                    new IRunnable() {

                    public Object run() {
                        return m_table.getClientArea();
                    }
                });
        
        Rectangle visibleTableBounds = new Rectangle(
            r.x, r.y, r.width, r.height);
        Rectangle visibleCellBounds = 
            visibleTableBounds.intersection(cellBounds);
        return visibleCellBounds;
    }
    
    /**
     * @param cellEditor The cell editor to check.
     * @return <code>true</code> if the given cell editor is editable.
     */
    private boolean isEditable(Control cellEditor) {

        if (cellEditor == null || cellEditor instanceof TableCursor
            || cellEditor == m_table) {
            // No actual editor found.
            return false;
        }
        
        return (cellEditor.getStyle() & SWT.READ_ONLY) == 0;
    } 
    
    /**
     * @param cellEditor The cell editor to check.
     * @return <code>true</code> if the given editor is editable. Otherwise
     *         <code>false</code>.
     */
    private boolean invokeIsEditable(final Control cellEditor) {

        boolean isEditable = ((Boolean)getEventThreadQueuer().invokeAndWait(
                "getSelectedCell", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        return isEditable(cellEditor) 
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                })).booleanValue();
        return isEditable;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object activateEditor(final Cell cell) {

        Rectangle rect = scrollCellToVisible(cell.getRow(), cell.getCol());
        Control editor = getTableCellEditor(cell, rect);
        // sometimes the editor only appears after doubleclick!

        if (!invokeIsEditable(editor)) {
            org.eclipse.swt.graphics.Rectangle cellBounds = 
                    new org.eclipse.swt.graphics.Rectangle(
                    rect.x, rect.y, rect.width, rect.height);
            ClickOptions co = ClickOptions.create().setClickCount(2);
            Control clickTarget = editor == null
                    || editor instanceof TableCursor ? m_table : editor;
            getRobot().click(clickTarget, cellBounds, co);
            editor = getTableCellEditor(cell, rect);
        }

        return editor;

    }
    /**
     * Gets the TableCellEditor of the given cell.
     * The Cell has to be activated before!
     * @param cell the cell.
     * @param rect 
     * @return the TableCellEditor
     */
    private Control getTableCellEditor(final Cell cell, Rectangle rect) {
        org.eclipse.swt.graphics.Rectangle swtRect = 
                new org.eclipse.swt.graphics.Rectangle(rect.x, rect.y, 
                        rect.width, rect.height);
        getRobot().click(m_table, 
            swtRect,
            ClickOptions.create().setClickCount(1));

        
        return SwtUtils.getCursorControl();
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        final Cell selectedCell = getSelectedCell();
        return getCellText(selectedCell.getRow(), selectedCell.getCol());
    }

    /**
     * {@inheritDoc}
     */
    public Object getTableHeader() {
        return m_table;
    }
}
