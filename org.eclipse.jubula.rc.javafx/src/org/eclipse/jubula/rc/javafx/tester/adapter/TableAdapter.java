/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.util.AbstractTraverser;
import org.eclipse.jubula.rc.javafx.util.GenericTraverseHelper;
import org.eclipse.jubula.rc.javafx.util.Rounding;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.StringParsing;

import com.sun.javafx.scene.control.skin.TableColumnHeader;

/**
 * Adapter for a TableView(Table)
 *
 * @author BREDEX GmbH
 * @created 7.11.2013
 */
public class TableAdapter extends JavaFXComponentAdapter<TableView<?>> 
                          implements ITableComponent {

    /**
     * Workaround to support nested Columns without modifying classes which would
     * affect other toolkits
     **/
    private List<TableColumn> m_columns = new ArrayList<TableColumn>();
    /**
     * Creates an adapter for a TableView.
     *
     * @param objectToAdapt
     *            the object which needed to be adapted
     */
    public TableAdapter(TableView objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public String getText() {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait("getText", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        ObservableList sCells = getRealComponent()
                                .getSelectionModel().getSelectedCells();
                        if (!sCells.isEmpty()) {
                            TablePosition pos = (TablePosition) sCells.get(0);
                            return getCellText(pos.getRow(), pos.getColumn());
                        }
                        throw new StepExecutionException("No selection found", //$NON-NLS-1$
                                EventFactory
                                        .createActionError(TestErrorEvent.
                                                NO_SELECTION));
                    }
                });
        return result;
    }
    
    @Override
    public int getColumnCount() {
        int result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnCount", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws Exception {
                        int counter = 0;
                        for (TableColumn column : getRealComponent()
                                .getColumns()) {
                            counter += new GenericTraverseHelper
                                    <TableColumn, TableColumn>()
                                    .getInstancesOf(
                                            new AbstractTraverser
                                            <TableColumn, TableColumn>(
                                                    column) {

                                                @Override
                                                public Iterable<TableColumn> 
                                                getTraversableData() {
                                                    return this.getObject()
                                                            .getColumns();
                                                }
                                            }, TableColumn.class).size();
                        }
                        return counter + getRealComponent().getColumns().size();
                    }
                });
        return result;
    }

    @Override
    public int getRowCount() {
        int result = EventThreadQueuerJavaFXImpl.invokeAndWait("getRowCount", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return getRealComponent().getItems().size();
                    }
                });
        return result;
    }

    @Override
    public String getCellText(final int row, final int column) {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        TableView<?> table = getRealComponent();
                        TableColumn col = null;
                        if (m_columns.size() == 0) {
                            col = table.getVisibleLeafColumn(column);
                        } else {
                            col = m_columns.get(column);
                        }
                        table.scrollTo(row);
                        table.scrollToColumn(col);
                        table.layout();
                        List<? extends TableCell> tCells = ComponentHandler
                                .getAssignableFrom(TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col
                                    && cell.getTableView() == table) {
                                String txt = cell.getText();
                                if (txt == null
                                        && cell instanceof TextFieldTableCell
                                        && cell.isEditing()) {
                                    // The cell is in its editing state,
                                    // therefore its text property is empty.
                                    // We have to check the TextField for the
                                    // text.
                                    TextField f = (TextField) cell.getGraphic();
                                    txt = f.getText();
                                }
                                return txt;
                            }
                        }
                        return null;
                    }
                });
        return result;
    }

    @Override
    public String getColumnHeaderText(final int column) {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnHeaderText", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        if (m_columns.size() > 0) {
                            TableColumn tCol = m_columns.get(column);
                            return tCol.getText();
                        }
                        TableColumn tCol = getRealComponent().
                                getVisibleLeafColumn(column);
                        return tCol.getText();
                    }
                });
        return result;
    }

    @Override
    public int getColumnFromString(final String colPath, final String op) {
        Integer result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnFromString", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws Exception {
                        TableView table = getRealComponent();
                        TableColumn column = null;
                        List<String> path = StringParsing.splitToList(colPath,
                                TestDataConstants.PATH_CHAR_DEFAULT,
                                TestDataConstants.ESCAPE_CHAR_DEFAULT, false);
                        ObservableList<TableColumn> columns;
                        if (colPath.contains("" + TestDataConstants.PATH_CHAR_DEFAULT)) { //$NON-NLS-1$
                            columns = table.getColumns();
                        } else {
                            columns = table.getVisibleLeafColumns();
                        }
                        Iterator<String> pathIterator = path.iterator();
                        String currCol = null;
                        while (pathIterator.hasNext()) {
                            try {
                                currCol = pathIterator.next();
                                int usrIdxCol = Integer.parseInt(currCol);
                                if (usrIdxCol == 0) {
                                    usrIdxCol = usrIdxCol + 1;
                                }
                                int i = IndexConverter
                                        .toImplementationIndex(usrIdxCol);
                                if (pathIterator.hasNext()) {
                                    columns = columns.get(i).getColumns();
                                } else {
                                    column = columns.get(i);
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    if (path.size() <= 1) {
                                        columns = table.getColumns();
                                    }
                                    if (columns.size() <= 0) {
                                        throw new StepExecutionException(
                                                "No Columns", EventFactory.createActionError(//$NON-NLS-1$
                                                                TestErrorEvent.
                                                                NO_HEADER));
                                    }
                                    for (TableColumn c: columns) {
                                        String header = c.getText();
                                        if (MatchUtil.getInstance().match(
                                                header, currCol, op)) {
                                            column = c;
                                            if (pathIterator.hasNext()) {
                                                columns = c.getColumns();
                                            }
                                            break;
                                        }
                                    }
                                } catch (IllegalArgumentException iae) {
                                    // do nothing here
                                }
                            }
                        } 
                        if (column == null) {
                            throw new StepExecutionException("Column not found", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                                    TestErrorEvent.NOT_FOUND));
                        }
                        if (table.getVisibleLeafColumns().contains(column)) {
                            return table.getVisibleLeafColumns().
                                    indexOf(column);
                        }
                        m_columns.add(column);
                        return m_columns.indexOf(column); 
                    }
                });
        return result.intValue();
    }

    @Override
    public String getRowText(int row) {
        // TableView does not act like lists
        return null;
    }

    @Override
    public int getRowFromString(final String row, final String operator) {
        Integer result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRowFromString", new Callable<Integer>() { //$NON-NLS-1$
                    @Override
                    public Integer call() throws Exception {
                        int rowInt = -2;
                        TableView<?> table = getRealComponent();
                        try {
                            rowInt = IndexConverter
                                    .toImplementationIndex(Integer
                                            .parseInt(row));
                            if (rowInt == -1) {
                                if (table.getColumns().size() <= 0) {
                                    throw new StepExecutionException(
                                            "No Header", //$NON-NLS-1$
                                            EventFactory
                                                    .createActionError(
                                                            TestErrorEvent.
                                                            NO_HEADER));
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            for (int i = 0; i < table.getItems().size(); i++) {
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
        return result.intValue();
    }

    @Override
    public Rectangle getHeaderBounds(final int column) {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getHeaderBounds", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TableView<?> table = getRealComponent();
                        TableColumn col;
                        if (m_columns.size() > 0) {
                            col = m_columns.get(column);
                        } else {
                            col = getRealComponent().
                                getVisibleLeafColumn(column);
                        }
                        table.scrollToColumn(col);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        Parent headerRow = (Parent) table
                                .lookup("TableHeaderRow"); //$NON-NLS-1$
                        Set<Node> columnHeaders = headerRow
                                .lookupAll("column-header"); //$NON-NLS-1$
                        Point2D parentPos = table.localToScreen(0, 0);

                        for (Node n : columnHeaders) {
                            //DEPENDENCY TO INTERNAL API
                            TableColumnHeader colH = (TableColumnHeader) n;
                            if (colH.getTableColumn().equals(col)) {
                                Bounds b = n.getBoundsInParent();
                                Point2D pos = n.localToScreen(0, 0);
    
                                return new Rectangle(Rounding
                                        .round(pos.getX() - parentPos.getX()),
                                        Rounding.round(pos.getY()
                                                - parentPos.getY()), Rounding
                                                .round(b.getWidth()), Rounding
                                                .round(b.getHeight()));
                            }
                        }
                        return null;
                    }
                });
        return result;
    }

    @Override
    public Cell getSelectedCell() throws StepExecutionException {
        Cell result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedCell", new Callable<Cell>() { //$NON-NLS-1$

                    @Override
                    public Cell call() throws StepExecutionException {
                        TableView table = getRealComponent();
                        ObservableList list = table
                                .getSelectionModel().getSelectedCells();

                        if (list.size() > 0) {
                            TablePosition pos = null;
                            for (Object object : list) {
                                TablePosition curr = (TablePosition) object;
                                if (curr.getRow() == table.getSelectionModel()
                                        .getSelectedIndex()) {
                                    pos = curr;
                                    break;
                                }
                            }
                            if (pos != null) {
                                return new Cell(pos.getRow(), pos.getColumn());
                            }
                        }
                        throw new StepExecutionException("No selection found", //$NON-NLS-1$
                                EventFactory
                                .createActionError(TestErrorEvent.
                                        NO_SELECTION));
                    }
                });
        return result;
    }

    @Override
    public boolean isHeaderVisible() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isHeaderVisible", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        Pane header = (Pane) getRealComponent().lookup(
                                "TableHeaderRow"); //$NON-NLS-1$
                        if (header != null) {
                            return header.isVisible();
                        }
                        return false;
                    }
                });
        return result;
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isCellEditable", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TableView<?> table = getRealComponent();
                        if (table.isEditable()) {
                            TableColumn col = null;
                            if (m_columns.size() == 0) {
                                col = table.getVisibleLeafColumn(column);
                            } else {
                                col = m_columns.get(column);
                            }
                            if (col.isEditable()) {
                                table.scrollTo(row);
                                table.scrollToColumn(col);
                                table.layout();
                                List<? extends TableCell> tCells = 
                                        ComponentHandler.getAssignableFrom(
                                                TableCell.class);
                                for (TableCell<?, ?> cell : tCells) {
                                    if (cell.getIndex() == row
                                            && cell.getTableColumn() == col
                                            && cell.getTableView() == table) {
                                        return cell.isEditable();
                                    }
                                }
                            }
                        }
                        return false;
                    }
                });
        return result;
    }

    @Override
    public boolean hasCellSelection() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "hasCellSelection", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TableView<?> table = getRealComponent();

                        return table.getSelectionModel().getSelectedCells()
                                .size() > 0;
                    }
                });
        return result;
    }

    @Override
    public Rectangle scrollCellToVisible(final int row, final int column)
        throws StepExecutionException {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "scrollCellToVisible", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TableView<?> table = getRealComponent();
                        TableColumn col = null;
                        if (m_columns.size() == 0) {
                            col = table.getVisibleLeafColumn(column);
                        } else {
                            col = m_columns.get(column);
                        }

                        table.scrollTo(row);
                        table.scrollToColumn(col);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        List<? extends TableCell> tCells = ComponentHandler
                                .getAssignableFrom(TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col
                                    && cell.getTableView() == table) {

                                Bounds b = cell.getBoundsInParent();
                                Point2D pos = cell.localToScreen(0, 0);
                                Point2D parentPos = table.localToScreen(0, 0);
                                return new Rectangle(
                                        Rounding.round(pos.getX()
                                        - parentPos.getX()),
                                        Rounding.round(pos.getY()
                                        - parentPos.getY()),
                                        Rounding.round(b.getWidth()),
                                        Rounding.round(b.getHeight()));
                            }
                        }
                        return null;
                    }
                });
        return result;
    }

    @Override
    public Object getTableHeader() {
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTableHeader", new Callable<Object>() { //$NON-NLS-1$

                    @Override
                    public Object call() throws Exception {
                        return getRealComponent().lookup("TableHeaderRow"); //$NON-NLS-1$
                    }
                });
        return result;
    }

}
