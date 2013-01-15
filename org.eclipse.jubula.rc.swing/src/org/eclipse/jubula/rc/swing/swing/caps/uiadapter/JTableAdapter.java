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
package org.eclipse.jubula.rc.swing.swing.caps.uiadapter;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.eclipse.jubula.rc.common.caps.AbstractMenuCAPs;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.ITableAdapter;
import org.eclipse.jubula.rc.swing.swing.caps.CapUtil;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
/**
 * Implements the table interface as an adapter for the <code>JTable</code>.
 *
 * @author BREDEX GmbH
 */
public class JTableAdapter extends WidgetAdapter implements ITableAdapter {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            AbstractMenuCAPs.class);

    /** The JTable from the AUT   */
    private JTable m_table;
    
    /**
     * Creates an object with the adapted JMenu.
     * @param objectToAdapt the object which needed to be adapted
     */
    public JTableAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_table = (JTable) objectToAdapt;
    }
   
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object element) {
        m_table = (JTable) element;

    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        Integer returnvalue = (Integer) getEventThreadQueuer().invokeAndWait(
                "getColumnCount", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return new Integer(m_table.getColumnCount());
                    }
                });
        return returnvalue.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        Integer returnvalue = (Integer) getEventThreadQueuer().invokeAndWait(
                "getRowCount", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return new Integer(m_table.getRowCount());
                    }
                });
        return returnvalue.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public String getCellText(final int row, final int column) {
        Object o = getEventThreadQueuer().invokeAndWait("getCellText", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        Object value = m_table.getValueAt(row, column);
                        boolean selected = m_table.isCellSelected(row,
                                column);
                        if (log.isDebugEnabled()) {
                            log.debug("Getting cell text:"); //$NON-NLS-1$
                            log.debug("Row, col: " + row + ", " + column); //$NON-NLS-1$ //$NON-NLS-2$
                            log.debug("Value: " + value); //$NON-NLS-1$
                        }
                        TableCellRenderer renderer = m_table.getCellRenderer(
                                row, column);
                        Component c = renderer.getTableCellRendererComponent(
                                m_table, value, selected, true, row,
                                column);

                        return CapUtil.getRenderedText(c);
                    }
                });

        String current = String.valueOf(o);
        return current;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getColumnName(final int column) {
        String returnvalue = (String)getEventThreadQueuer().invokeAndWait(
                "getColumnName", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        return m_table.getColumnName(column);
                    }
                });
        return returnvalue;
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnFromString(final String col, final String operator) {
        Integer returnvalue = (Integer) getEventThreadQueuer().invokeAndWait(
                "getColumnFromString", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
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
                                if (m_table.getTableHeader() == null
                                        || !(m_table.getTableHeader()
                                                .isShowing())) {
                                    throw new StepExecutionException("No Header", //$NON-NLS-1$
                                            EventFactory.createActionError(
                                                TestErrorEvent.NO_HEADER));
                                }
                                for (int i = 0; i < m_table.getColumnCount();
                                        i++) {
                                    String header = m_table.getColumnName(i);
                                    if (MatchUtil.getInstance().match(
                                            header, col, operator)) {
                                        column = i;
                                    }
                                }
                            } catch (IllegalArgumentException iae) {
                                //do nothing here                
                            }
                        }
                        
                        return new Integer(column);
                    }
                });
        return returnvalue.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public String getRowName(final int row) {
        // JTable does not act like lists
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowFromString(final String row, final String operator) {
        Integer returnvalue = (Integer) getEventThreadQueuer().invokeAndWait(
                "getRowFromString", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        int rowInt = -2;        
                        try {
                            rowInt = IndexConverter.toImplementationIndex(
                                    Integer.parseInt(row));
                            if (rowInt == -1) {
                                if (m_table.getTableHeader() == null
                                        || !(m_table.getTableHeader()
                                                .isShowing())) {
                                    throw new StepExecutionException("No Header", //$NON-NLS-1$
                                            EventFactory.createActionError(
                                                TestErrorEvent.NO_HEADER));
                                }                
                            }
                        } catch (NumberFormatException nfe) {
                            for (int i = 0; i < m_table.getRowCount(); i++) {
                                String cellTxt = getCellText(i, 0);
                                if (MatchUtil.getInstance().match(cellTxt, row,
                                        operator)) {
                                    return new Integer(i);
                                }
                            }
                        }        
                        return new Integer(rowInt);
                    }
                });
        return returnvalue.intValue();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public Rectangle getHeaderBounds(final int col) {
        Rectangle returnvalue = (Rectangle) getEventThreadQueuer()
                .invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                    new IRunnable() {                     
                        public Object run() throws StepExecutionException {
                            return m_table.getTableHeader().getHeaderRect(col);
                        }
                    });
        
        return returnvalue;
    }

    /**
     * {@inheritDoc}
     */
    public Cell getSelectedCell() throws StepExecutionException {
        
        Cell returnvalue = (Cell) getEventThreadQueuer().invokeAndWait(
                "getSelectedCell", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                
                        int row = m_table.getSelectedRow();
                        int col = m_table.getSelectedColumn();
                        if (log.isDebugEnabled()) {
                            log.debug("Selected row, col: " + row + ", " + col); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        try {
                            checkRowColBounds(row, col);
                        } catch (StepExecutionException e) {
                            if ((e.getEvent() != null)
                                    && (TestErrorEvent.INVALID_INDEX.equals(
                                            e.getEvent()
                                            .getProps().get(TestErrorEvent
                                                    .Property
                                                    .DESCRIPTION_KEY)))) {
                // set "invalid index" to "no selection" -> better description!
                                throw new StepExecutionException("No selection", //$NON-NLS-1$
                                        EventFactory.createActionError(
                                                TestErrorEvent.NO_SELECTION));
                            }
                            throw e;
                        }
                        return new Cell(row, col);
                    }
                });
        return returnvalue;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHeaderVisible() {
        Boolean returnvalue = (Boolean) getEventThreadQueuer().invokeAndWait(
                "isHeaderVisible", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        if (m_table.getTableHeader() != null) {
                            return m_table.getTableHeader().isVisible()
                                    ? Boolean.TRUE : Boolean.FALSE;
                        }
                        return Boolean.FALSE;
                    }
                });
        return (boolean) returnvalue.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(int row, int col) {
        Boolean editable = (Boolean) getEventThreadQueuer().invokeAndWait(
                "isCellEditable", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        Cell cell = getSelectedCell();
                        // see findBugs
                        return (m_table.isCellEditable(cell.getRow(),
                                cell.getCol())) ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        return editable.booleanValue();
    }

    /**
     * Checks whether <code>0 <= value < count</code>.
     *
     * @param value
     *            The value to check.
     * @param count
     *            The upper bound.
     */
    private void checkBounds(int value, int count) {
        if ((value < 0) || (value >= count)) {
            throw new StepExecutionException("Invalid row/column: " + value, //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INDEX_OR_HEADER));
        }
    }

    /**
     * Checks if the passed row and column are inside the bounds of the JTable.
     *
     * @param row
     *            The row
     * @param column
     *            The column
     * @throws StepExecutionException
     *             If the row or the column is outside of the JTable's bounds.
     */
    private void checkRowColBounds(int row, int column)
        throws StepExecutionException {
        checkBounds(row, m_table.getRowCount());
        checkBounds(column, m_table.getColumnCount());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasCellSelection() {
        try {
            getSelectedCell();
        } catch (StepExecutionException e) {
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public Rectangle scrollCellToVisible(final int row, final int col)
        throws StepExecutionException {
        Rectangle bounds = (Rectangle) getEventThreadQueuer().invokeAndWait(
                "getCellRect", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        return m_table
                                .getCellRect(row, col, true);
                    }
                });

        getRobot().scrollToVisible(m_table, bounds);
        return bounds;
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
        return getEventThreadQueuer()
                .invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                    new IRunnable() {                     
                        public Object run() throws StepExecutionException {
                            return m_table.getTableHeader();
                        }
                    });
        
    }
}
