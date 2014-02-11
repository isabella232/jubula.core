package org.eclipse.jubula.rc.javafx.tester;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.AbstractTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.util.Rounding;

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
        TableCell result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellText", new Callable<TableCell>() {

                    @Override
                    public TableCell call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        table.scrollTo(row);
                        table.scrollToColumnIndex(column);
                        table.layout();
                        TableColumn<?, ?> col = table
                                .getVisibleLeafColumn(column);
                        List<Object> tCells = ComponentHandler
                                .getInstancesOfType(TableCell.class);
                        for (Object o : tCells) {
                            TableCell<?, ?> cell = (TableCell<?, ?>) o;
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
                "getCellAtMousePosition", new Callable<Cell>() {

                    @Override
                    public Cell call() throws Exception {
                        TableView table = (TableView<?>) getRealComponent();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        table.layout();
                        List<Object> tCells = ComponentHandler
                                .getInstancesOfType(TableCell.class);
                        for (Object o : tCells) {
                            TableCell<?, ?> cell = (TableCell<?, ?>) o;
                            Rectangle rec = getCellRect(cell);
                            Point2D tablePos = table.localToScreen(0, 0);
                            rec.x = rec.x + Rounding.round(tablePos.getX());
                            rec.y = rec.y + Rounding.round(tablePos.getY());
                            if (rec.contains(p)
                                    && cell.getTableView().equals(table)) {
                                int col = table.getColumns().indexOf(
                                        cell.getTableColumn());
                                return new Cell(cell.getIndex(), col);
                            }
                        }
                        return null;
                    }
                });
        return result;
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
                "getCellRect", new Callable<Rectangle>() {

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
                        Parent header = (Parent) table.lookup("TableHeaderRow");
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
                "rcToggleCheckboxInSelectedRow", new Callable<Integer>() {

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
            log.error("No Ceckbox found at Mouseposition: "
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
                "verifyCheckboxInRow", new Callable<Boolean>() {

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
                "clickCheckBoxFirstColumn", new Callable<Node>() {

                    @Override
                    public Node call() throws Exception {
                        TableView<?> table = (TableView<?>) getRealComponent();
                        table.layout();
                        TableColumn<?, ?> col = table.getVisibleLeafColumn(0);
                        // Check if the CheckBox is realized via a CheckBoxCell
                        List<Object> tCells = ComponentHandler
                                .getInstancesOfType(CheckBoxTableCell.class);
                        for (Object o : tCells) {
                            CheckBoxTableCell cell = (CheckBoxTableCell) o;
                            if (cell.getTableColumn().equals(col)
                                    && cell.getIndex() == row) {
                                return cell.lookup("CheckBox");
                            }
                        }
                        // No CheckBoxCell found. Now we have to check all
                        // Cells!
                        tCells = ComponentHandler
                                .getInstancesOfType(TableCell.class);
                        for (Object o : tCells) {
                            TableCell cell = (TableCell) o;
                            if (cell.getTableColumn().equals(col)
                                    && cell.getIndex() == row) {
                                return cell.lookup("CheckBox");
                            }
                        }
                        return null;
                    }
                });
    }

}
