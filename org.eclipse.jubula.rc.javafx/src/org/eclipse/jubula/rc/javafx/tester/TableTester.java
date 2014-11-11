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
package org.eclipse.jubula.rc.javafx.tester;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.AbstractTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.util.AbstractTraverser;
import org.eclipse.jubula.rc.javafx.util.GenericTraverseHelper;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.util.Rounding;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Toolkit specific commands for the <code>TableView</code>
 *
 * @author BREDEX GmbH
 * @created 27.11.2013
 */
public class TableTester extends AbstractTableTester {
    /** The AUT Server logger. */
    private static AutServerLogger log = new AutServerLogger(TableTester.class);

    @Override
    protected Object setEditorToReplaceMode(Object editor, boolean replace) {
        Object returnvalue = editor;
        if (replace) {
            getRobot().clickAtCurrentPosition(editor, 3, 1);
        } else {
            returnvalue = getComponent().getRealComponent();
        }
        return returnvalue;
    }

    @Override
    protected Object activateEditor(Cell cell, Rectangle rectangle) {
        Object table = getComponent().getRealComponent();
        getRobot().click(table, rectangle);
        TableCell<?, ?> realCell = getCellAt(cell.getRow(), cell.getCol());
        // Check if setting the cell in its edit state was successful
        if (realCell.isEditing()) {
            ClickOptions co = ClickOptions.create().setClickCount(2);
            getRobot().click(table, rectangle, co);
        }
        return realCell;
    }

    /**
     * Returns the TableCell at the given position.
     *
     * @param row
     *            the row
     * @param column
     *            the column
     * @return the TableCell at the specified position.
     */
    private TableCell<?, ?> getCellAt(final int row, final int column) {
        TableCell<?, ?> result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellText", new Callable<TableCell<?, ?>>() { //$NON-NLS-1$

                    @Override
                    public TableCell<?, ?> call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        table.scrollTo(row);
                        table.scrollToColumnIndex(column);
                        table.layout();
                        TableColumn<?, ?> col = table
                                .getVisibleLeafColumn(column);
                        List<? extends TableCell> tCells = ComponentHandler
                                .getAssignableFrom(TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col) {
                                return cell;
                            }
                        }
                        return null;
                    }

                });
        return result;
    }

    @Override
    protected int getExtendSelectionModifier() {
        return KeyEvent.VK_CONTROL;
    }

    @Override
    protected Cell getCellAtMousePosition() throws StepExecutionException {
        final Point p = getRobot().getCurrentMousePosition();
        Cell result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellAtMousePosition", new Callable<Cell>() { //$NON-NLS-1$

                    @Override
                    public Cell call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        
                        List<? extends TableCell> tCells = ComponentHandler
                                .getAssignableFrom(TableCell.class);
                        for (TableCell<?, ?> cell : tCells) {
                            Rectangle rec = getCellRect(cell);
                            Point2D tablePos = table.localToScreen(0, 0);
                            rec.x = rec.x + Rounding.round(tablePos.getX());
                            rec.y = rec.y + Rounding.round(tablePos.getY());
                            if (rec.contains(p)
                                    && cell.getTableView().equals(table)) {
                                TableColumn cellColumn = cell
                                        .getTableColumn();
                                int col = table.getVisibleLeafIndex(cellColumn);
                                return new Cell(cell.getIndex(), col);
                            }
                        }
                        return null;
                    }
                });
        return result;
    }
    
    /**
     * Gets the index path for a given column 
     * @param column the column
     * @param table the table of the column
     * @return the index path
     */
    private String getColumnPath(TableColumnBase column, TableView table) {
        String colPath = "";
        TableColumnBase nxtColumn = column;
        while (nxtColumn.getParentColumn() != null) {
            colPath = String.valueOf(TestDataConstants.
                    PATH_CHAR_DEFAULT).concat(String.
                            valueOf((nxtColumn.getParentColumn()
                            .getColumns()
                            .indexOf(nxtColumn) + 1) + colPath));
            nxtColumn = nxtColumn.getParentColumn();
        }
        colPath = (table.getColumns()
                .indexOf(nxtColumn) + 1) + colPath;
        return colPath;
    }

    /**
     * Get a rectangle with the Bounds of a cell in its parent.
     *
     * @param cell
     *            the cell to get the rectangle from
     * @return the rectangle
     */
    private Rectangle getCellRect(final TableCell<?, ?> cell) {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellRect", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        Bounds b = cell.getBoundsInParent();
                        Point2D pos = cell.localToScreen(0, 0);
                        Point2D parentPos = table.localToScreen(0, 0);
                        return new Rectangle(Rounding.round(
                                pos.getX() - parentPos.getX()),
                                Rounding.round(pos.getY() - parentPos.getY()),
                                Rounding.round(b.getWidth()),
                                Rounding.round(b.getHeight()));
                    }

                });
        return result;
    }

    @Override
    protected boolean isMouseOnHeader() {
        Point p = getRobot().getCurrentMousePosition();
        final Point2D pos = new Point2D(p.x, p.y);
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTableHeader", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        Parent header = (Parent) table.lookup("TableHeaderRow"); //$NON-NLS-1$
                        return NodeBounds.checkIfContains(pos, header);
                    }
                });
        return result;
    }

    /**
     * Toggles the checkbox in the selected row
     */
    public void rcToggleCheckboxInSelectedRow() {
        int row = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcToggleCheckboxInSelectedRow", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws StepExecutionException {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        return new Integer(table.getSelectionModel()
                                .getSelectedIndex());
                    }
                });
        clickCheckBoxFirstColumn(row);
    }

    /**
     * Toggles the checkbox in the row under the Mouse Pointer
     */
    public void rcToggleCheckboxInRowAtMousePosition() {
        clickCheckBoxFirstColumn(getCellAtMousePosition().getRow());
    }

    /**
     * Verifies whether the checkbox in the row of the selected cell is checked
     *
     * @param checked
     *            true if checkbox in cell should be selected, false otherwise
     * @throws StepExecutionException
     *             If no cell is selected or the verification fails.
     */
    public void rcVerifyCheckboxInSelectedRow(boolean checked)
        throws StepExecutionException {
        int row = ((ITableComponent) getComponent()).getSelectedCell().getRow();
        verifyCheckboxInRow(checked, row);
    }

    /**
     * Verifies whether the checkbox in the row under the mouse pointer is
     * checked
     *
     * @param checked
     *            true if checkbox in cell is selected, false otherwise
     */
    public void rcVerifyCheckboxInRowAtMousePosition(boolean checked) {
        Cell cell = getCellAtMousePosition();
        if (cell != null) {
            int row = cell.getRow();
            verifyCheckboxInRow(checked, row);
        } else {
            log.error("No Ceckbox found at Mouseposition: " //$NON-NLS-1$
                    + getRobot().getCurrentMousePosition());
        }
    }

    /**
     * Clicks on the CheckBox in the first Column of the given row;
     *
     * @param row
     *            the Row
     */
    private void clickCheckBoxFirstColumn(final int row) {

        Node box = getCheckBoxFirstColumn(row);
        if (box != null) {
            getRobot().click(box, null,
                    ClickOptions.create().setClickCount(1).setMouseButton(1));
        }
    }

    /**
     * Verifies whether the checkbox in the row with the given
     * <code>index</code> is checked
     *
     * @param checked
     *            true if checkbox in cell is selected, false otherwise
     * @param row
     *            the row-index of the cell in which the checkbox-state should
     *            be verified
     */
    private void verifyCheckboxInRow(boolean checked, final int row) {
        final CheckBox box = (CheckBox) getCheckBoxFirstColumn(row);
        Boolean checkIndex = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "verifyCheckboxInRow", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws StepExecutionException {
                        return box.isSelected();
                    }
                });
        Verifier.equals(checked, checkIndex.booleanValue());
    }

    /**
     * get the CheckBox in the first TableColumn of the given row
     *
     * @param row
     *            the Row
     * @return the CheckBox or null
     */
    private Node getCheckBoxFirstColumn(final int row) {

        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "clickCheckBoxFirstColumn", new Callable<Node>() { //$NON-NLS-1$

                    @Override
                    public Node call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        table.layout();
                        TableColumn<?, ?> col = table.getVisibleLeafColumn(0);
                        // Check if the CheckBox is realized via a CheckBoxCell
                        List<? extends CheckBoxTableCell> checkboxCells = 
                                ComponentHandler.getAssignableFrom(
                                        CheckBoxTableCell.class);
                        for (CheckBoxTableCell<?, ?> cell : checkboxCells) {
                            if (cell.getTableColumn().equals(col)
                                    && cell.getIndex() == row) {
                                return cell.lookup("CheckBox"); //$NON-NLS-1$
                            }
                        }
                        // No CheckBoxCell found. Now we have to check all
                        // Cells!
                        List<? extends TableCell> cells = ComponentHandler
                                .getAssignableFrom(TableCell.class);
                        for (TableCell<?, ?> cell : cells) {
                            if (cell.getTableColumn().equals(col)
                                    && cell.getIndex() == row) {
                                return cell.lookup("CheckBox"); //$NON-NLS-1$
                            }
                        }
                        return null;
                    }
                });
    }

    @Override
    public void rcVerifyValueInRow(final String row, final String rowOperator,
            final String value, final String operator, final String searchType,
            boolean exists)
        throws StepExecutionException {

        final ITableComponent adapter = (ITableComponent) getComponent();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        // if row is header
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcVerifyValueInRow", new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        boolean valueIsExisting = false;
                        if (implRow == -1) {
                            valueIsExisting = (getColumnByName(value, operator,
                                    searchType, adapter, implRow) != null) 
                                    ? true : false;
                        } else {
                            valueIsExisting = (getColumnByValue(value, operator,
                                    searchType, adapter, implRow) != null) 
                                    ? true : false;
                        }
                        return valueIsExisting;
                    }
                });

        Verifier.equals(exists, result);
    }
    
    /**
     * Returns the internal index of the first column which has the given name
     * CALL IN JAVAFX-THREAD!
     * @param name the name of the column
     * @param operator the operator
     * @param searchType the searchType
     * @param adapter the adapter class
     * @param implRow the row
     * @return String with the column path or null
     */
    private String getColumnByName(final String name, final String operator,
            final String searchType, final ITableComponent adapter,
            final int implRow) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getColumnByName",
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        final int columnCount = adapter.getColumnCount();
                        if (columnCount > 0) {
                            List<TableColumn> columns = getColumnsFromTable(
                                    searchType, adapter);
                            for (TableColumn column : columns) {
                                if (MatchUtil.getInstance().match(
                                        column.getText(), name, operator)) {
                                    return getColumnPath(column,
                                                            (TableView) 
                                                            getRealComponent());
                                }
                            }
                        }
                        return null;
                    }
                });
    }
    
    /**
     * Returns a list of columns. if the search type is "relative" all Columns
     * relative to the current selection.
     * 
     * CALL IN JAVAFX-THREAD!
     * 
     * @param searchType the searchType
     * @param adapter the adapter
     * @return list of columns
     */
    private List<TableColumn> getColumnsFromTable(final String searchType,
            final ITableComponent adapter) {
        ArrayList<TableColumn> columns = new ArrayList<>();
        if (searchType
                .equalsIgnoreCase(SearchType.relative.rcValue())) {
            TableView table = (TableView) adapter.getRealComponent();
            TableColumn selColumn = ((TablePosition) table.getSelectionModel()
                    .getSelectedCells().get(0)).getTableColumn();
            TableColumnBase parCol = selColumn.getParentColumn();
            while (parCol != null) {
                selColumn = (TableColumn) parCol;
                parCol = parCol.getParentColumn();
            }

            columns.addAll(new GenericTraverseHelper<TableColumn, TableColumn>()
                    .getInstancesOf(
                            new AbstractTraverser<TableColumn, TableColumn>(
                                    selColumn) {

                                @Override
                                public Iterable<TableColumn> 
                                    getTraversableData() {
                                    return this.getObject().getColumns();
                                }
                            }, TableColumn.class));
        } else {
            for (TableColumn column : ((TableView<?>) getRealComponent())
                    .getColumns()) {
                columns.addAll(new GenericTraverseHelper
                        <TableColumn, TableColumn>()
                        .getInstancesOf(
                                new AbstractTraverser<TableColumn, TableColumn>(
                                        column) {

                                    @Override
                                    public Iterable<TableColumn> 
                                        getTraversableData() {
                                        return this.getObject().getColumns();
                                    }
                                }, TableColumn.class));
            }
            columns.addAll(((TableView)adapter.getRealComponent()).
                    getColumns());
        }
        return columns;
    }

    /**
     * Returns the internal index of the first column which contains the given value
     * CALL IN JAVAFX-THREAD!
     * @param value the value
     * @param operator the operator
     * @param searchType the searchType
     * @param adapter the adapter class
     * @param implRow the row
     * @return String with the column path or null
     */
    private String getColumnByValue(final String value, final String operator,
            final String searchType, final ITableComponent adapter,
            final int implRow) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnByValue", new Callable<String>() {

                    @Override
                    public String call() {
                        final int columnCount = adapter.getColumnCount();
                        if (columnCount > 0) {
                            int startIndex = getStartingColIndex(searchType) 
                                    - 1;
                            List<TableColumn> columns = 
                                    ((TableView) getRealComponent())
                                    .getVisibleLeafColumns();
                            for (int i = startIndex; i < columns.size(); i++) {
                                TableColumn column = columns.get(i);
                                int index = adapter.getColumnFromString(
                                        getColumnPath(column,
                                                (TableView) getRealComponent()),
                                                "equals");
                                String cellValue = adapter.
                                        getCellText(implRow, index);
                                if (MatchUtil.getInstance().match(
                                        cellValue,
                                        value,
                                        operator)) {
                                    return getColumnPath(column,
                                            (TableView) getRealComponent());
                                }
                            }
                        }
                        return null;
                    }
                    
                });
    }

    @Override
    public void rcSelectCellByColValue(final String row,
            final String rowOperator, final String value,
            final String operator, int clickCount,
            final String extendSelection, final String searchType,
            final int button) {
        final ITableComponent adapter = (ITableComponent) getComponent();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        // if row is header
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "rcSelectCellByColValue", new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        if (implRow == -1) {
                            return getColumnByName(value, operator, searchType,
                                    adapter, implRow);
                        }
                        return getColumnByValue(value, operator,
                                searchType, adapter, implRow);
                    }
                });
        if (result == null) {
            throw new StepExecutionException("no such cell found", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.NOT_FOUND));
        }
        rcSelectCell(row, rowOperator, result, operator, clickCount, 50,
                ValueSets.Unit.percent.rcValue(), 50,
                ValueSets.Unit.percent.rcValue(), extendSelection, button);
    }
    
    /**
     * @param searchType Determines column where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingColIndex(String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(SearchType.relative.rcValue())) {
            Cell c = ((ITableComponent)getComponent())
                    .getSelectedCell();
            if (c == null) {
                throw new StepExecutionException("No selection found", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.
                                        NO_SELECTION));
            }
            startingIndex = c.getCol();
        }
        return startingIndex + 1;
    } 
}
