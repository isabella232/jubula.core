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
package org.eclipse.jubula.rc.javafx.tester;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.PathBasedTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.SelectTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperationConstraint;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.adapter.TreeTableOperationContext;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.util.NodeTraverseHelper;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;


/**
 * Tester Class for the <code>TreeTableView</code>. If you are looking for more
 * implemented actions on TreeTables look at <code>TreeTableOperationContext</code>.
 *
 * @author BREDEX GmbH
 * @created 23.06.2014
 */
public class TreeTableViewTester extends TreeViewTester {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            TreeTableViewTester.class);

    @Override
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        Point awtPoint = getRobot().getCurrentMousePosition();
        final Point2D point = new Point2D(awtPoint.x, awtPoint.y);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeAtMousePosition", new Callable<Object>() { //$NON-NLS-1$
                    @Override
                    public Object call() throws Exception {
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        ((TreeTableView<?>) getRealComponent()).layout();

                        List<TreeTableCell> tCells = NodeTraverseHelper
                                .getInstancesOf((Parent) getRealComponent(),
                                        TreeTableCell.class);
                        for (TreeTableCell cell : tCells) {
                            if (NodeBounds.checkIfContains(point, cell)) {
                                return cell;
                            }
                        }
                        throw new StepExecutionException(
                                "No tree node found at mouse position: " //$NON-NLS-1$
                                        + "X: " + point.getX() //$NON-NLS-1$
                                        + "Y: " + point.getY(), //$NON-NLS-1$
                                EventFactory
                                        .createActionError(
                                                TestErrorEvent.NOT_FOUND));
                    }
                });
        return result;
    }
    
    /**
     * Selects the last node of the path given by <code>indexPath</code>
     * at column <code>column</code>.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param indexPath the index path
     * @param clickCount the number of times to click
     * @param column the column of the item to select
     * @param button what mouse button should be used
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void rcSelectByIndices(String pathType, int preAscend, 
                                  String indexPath, int clickCount, int column,
                                  int button)
        throws StepExecutionException {
        final int implCol = IndexConverter.toImplementationIndex(column);
        checkColumnIndex(implCol);

        selectByPath(pathType, preAscend, 
            createIndexNodePath(splitIndexTreePath(indexPath)),
                ClickOptions.create()
                    .setClickCount(clickCount)
                    .setMouseButton(button), implCol);
    }
    
    /**
     * Selects the item at the end of the <code>treepath</code> at column 
     * <code>column</code>.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath The tree path.
     * @param operator
     *  If regular expressions are used to match the tree path
     * @param clickCount the click count
     * @param column the column of the item to select
     * @param button what mouse button should be used
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void rcSelect(String pathType, int preAscend, String treePath, 
                         String operator, int clickCount, int column,
                         int button)
        throws StepExecutionException {
        
        final int implCol = IndexConverter.toImplementationIndex(column);
        checkColumnIndex(implCol);
        selectByPath(pathType, preAscend, 
            createStringNodePath(splitTextTreePath(treePath), operator), 
            ClickOptions.create()
                .setClickCount(clickCount)
                .setMouseButton(button), implCol);

    }
    
    /**
     * @param pathType pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param objectPath objectPath
     * @param co the click options to use
     * @param column the column
     */
    private void selectByPath(String pathType, int preAscend, 
            INodePath objectPath, ClickOptions co, int column) {

        TreeNodeOperation expOp = 
            new ExpandCollapseTreeNodeOperation(false);
        TreeNodeOperation selectOp = 
            new SelectTreeNodeOperation(co);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);

        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(
            objectPath, pathType, preAscend, selectOp, column);
            
    }
    
    /**
     * Verifies if the selected node underneath <code>treePath</code> at column
     * <code>column</code> has a rendered text which is equal to 
     * <code>selection</code>.
     * 
     * @param pattern the pattern
     * @param operator
     *            The operator to use when comparing the expected and 
     *            actual values.
     * @param column
     *            The column containing the text to verify
     * @throws StepExecutionException If there is no tree node selected, the tree path contains no
     *             selection or the verification fails
     */
    public void rcVerifySelectedValue(String pattern, String operator, 
        int column) throws StepExecutionException {
        
        final int implCol = IndexConverter.toImplementationIndex(column);
        checkColumnIndex(implCol);

        TreeTableOperationContext context = getContext();
        context.setColumn(implCol);

        String text = context.getRenderedTextOfColumn(
                context.getSelectedNode());
        
        Verifier.match(text, pattern, operator);

    }

    /**
     * @return the tree table operation context
     */
    private TreeTableOperationContext getContext() {
        return new TreeTableOperationContext(
                getEventThreadQueuer(), getRobot(),
                (TreeTableView<?>) getRealComponent());
    }
    
    /**
     * Traverses the tree by searching for the nodes in the tree
     * path entry and calling the given operation on the last element in the path.
     * @param treePath The tree path.
     * @param pathType For example, "relative" or "absolute".
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param operation The tree node operation.
     * @param column The target column for the operation.
     * @throws StepExecutionException If the path traversal fails.
     */
    private void traverseLastElementByPath(INodePath treePath, 
            String pathType, int preAscend, TreeNodeOperation operation,
            int column) 
        throws StepExecutionException {

        Validate.notNull(treePath);
        Validate.notNull(operation);

        TreeTableOperationContext context = getContext();
        context.setColumn(column);
        TreeItem<?> startNode = (TreeItem<?>) getStartNode(pathType, preAscend,
                context);

        AbstractTreeNodeTraverser traverser = new PathBasedTraverser(
                context, treePath, new TreeNodeOperationConstraint());
        traverser.traversePath(operation, startNode);
    }
    
    /**
     * 
     * @param index The 0-based column index to check.
     * @throws StepExecutionException if the column index is invalid.
     */
    private void checkColumnIndex(final int index) 
        throws StepExecutionException {
       
        int numColumns = getEventThreadQueuer().invokeAndWait(
                "checkColumnIndex", //$NON-NLS-1$
                new IRunnable<Integer>() {

                    public Integer run() {
                        return ((TreeTableView<?>) getRealComponent())
                                .getColumns().size();
                    }
                });

        if ((index < 0 || index >= numColumns) && index != 0) {
            throw new StepExecutionException("Invalid column: " //$NON-NLS-1$
                + IndexConverter.toUserIndex(index), 
                EventFactory.createActionError(
                    TestErrorEvent.INVALID_INDEX));
        }
    }
    
    /**
     * Verifies the editable property of the given indices.
     *
     * @param editable
     *            The editable property to verify.
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     */
    public void rcVerifyEditable(boolean editable, String row,
            String rowOperator, String col, String colOperator) {
        TreeTableOperationContext context = getContext();
        if (context.getRowFromString(row, rowOperator) == -1) {
            throw new StepExecutionException("Unsupported Header Action", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_HEADER_ACTION));
        }        
        selectCell(row, rowOperator, col, colOperator, ClickOptions.create(),
                ValueSets.BinaryChoice.no.rcValue());
        rcVerifyEditable(editable);
    }
    
    /**
     * Verifies the editable property of the current selected cell.
     *
     * @param editable The editable property to verify.
     */
    public void rcVerifyEditable(boolean editable) {
        Cell cell = getContext().getSelectedCell();
        
        Verifier.equals(editable, getContext().isCellEditable(
                cell.getRow(), cell.getCol()));
    }
    
    /**
     * Verifies, whether value exists in row..
     *
     * @param row The row of the cell.
     * @param rowOperator the row header operator
     * @param value The cell text to verify.
     * @param operator The operation used to verify
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param exists true if value exists, false otherwise
     * @throws StepExecutionException
     *             If the row or the column is invalid, or if the rendered text
     *             cannot be extracted.
     */
    public void rcVerifyValueInRow(final String row, final String rowOperator,
            final String value, final String operator, final String searchType,
            boolean exists)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }
    
    /**
     * Verifies, whether value exists in column.
     *
     * @param col The column of the cell.
     * @param colOperator the column header operator
     * @param value The cell text to verify.
     * @param operator The operation used to verify
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param exists true if value exists, false otherwise
     * @throws StepExecutionException
     *             If the row or the column is invalid, or if the rendered text
     *             cannot be extracted.
     */
    public void rcVerifyValueInColumn(final String col,
            final String colOperator, final String value,
            final String operator, final String searchType, boolean exists)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }
    
    /**
     * Verifies the rendered text inside the passed cell.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param text The cell text to verify.
     * @param operator The operation used to verify
     * @throws StepExecutionException If the row or the column is invalid, or if the rendered text cannot be extracted.
     */
    /**
     * Verifies the rendered text inside the passed cell.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param text The cell text to verify.
     * @param operator The operation used to verify
     * @throws StepExecutionException If the row or the column is invalid, or if the rendered text cannot be extracted.
     */
    public void rcVerifyText(String text, String operator, final String row,
            final String rowOperator, final String col,
            final String colOperator) throws StepExecutionException {
        TreeTableOperationContext adapter = getContext();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        final int implCol = adapter.getColumnFromString(col, colOperator);
        String current;
        //if row is header and column is existing
        if (implRow == -1 && implCol > -1) {
            current = adapter.getColumnHeaderText(implCol);        
        } else {
            checkRowColBounds(implRow, implCol);
            adapter.scrollCellToVisible(implRow, implCol);
            current = getCellText(implRow, implCol);
        }
        
        Verifier.match(current, text, operator);
    }
    
    /**
     * Checks if the passed row and column are inside the bounds of the Table. 
     * @param row The row
     * @param column The column
     * @throws StepExecutionException If the row or the column is outside of the Table's bounds.
     */
    protected void checkRowColBounds(int row, int column)
        throws StepExecutionException {
        TreeTableOperationContext adapter = getContext();
        checkBounds(row, adapter.getRowCount());
        
        // Corner case: Only check the bounds if the table is not being
        //              used as a list or anything other than the first column
        //              is being checked.
        int colCount = adapter.getColumnCount();
        if (colCount > 0 || column > 0) {
            checkBounds(column, colCount);
        }
    }
     
    /**
     * Checks whether <code>0 <= value < count</code>. 
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
     * Gets the text from the specific cell which is given
     * by the row and the column.
     * @param row the zero based index of the row
     * @param column the zero based index of the column
     * @return the text of the cell of the given coordinates
     */
    private String getCellText(final int row, final int column) {
        return getContext().getCellText(row, column);

    }
    
    /**
     * Action to read the value of the passed cell of the table
     * to store it in a variable in the Client
     * @param variable the name of the variable
     * @param row the row to select
     * @param rowOperator the row header operator
     * @param col the column to select
     * @param colOperator the column header operator
     * @return the text value.
     */
    public String rcReadValue(String variable, String row, String rowOperator,
            String col, String colOperator) {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }
    
    /**
     * Selects a table cell in the given row and column via click in the middle of the cell.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param co the click options to use
     * @param extendSelection Should this selection be part of a multiple selection
     */
    private void selectCell(final String row, final String rowOperator,
            final String col, final String colOperator,
            final ClickOptions co, final String extendSelection) {
            
        rcSelectCell(row, rowOperator, col, colOperator, co.getClickCount(),
                50, ValueSets.Unit.percent.rcValue(), 50,
                ValueSets.Unit.percent.rcValue(), extendSelection,
                co.getMouseButton());
    }
    
    /**
     * Selects the cell of the Table.<br>
     * With the xPos, yPos, xUnits and yUnits the click position inside the cell can be defined.
     * @param row The row of the cell.
     * @param rowOperator The row header operator
     * @param col The column of the cell.
     * @param colOperator The column header operator
     * @param clickCount The number of clicks with the right mouse button
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param extendSelection Should this selection be part of a multiple selection
     * @param button what mouse button should be used
     * @throws StepExecutionException If the row or the column is invalid
     */
    public void rcSelectCell(final String row, final String rowOperator,
        final String col, final String colOperator,
        final int clickCount, final int xPos, final String xUnits,
        final int yPos, final String yUnits, final String extendSelection,
        int button) 
        throws StepExecutionException {
        TreeTableOperationContext adapter = getContext();
        final int implRow = adapter.getRowFromString(row, rowOperator);
        final int implCol = adapter.getColumnFromString(col, colOperator);
        final boolean isExtendSelection = extendSelection.equals(
                ValueSets.BinaryChoice.yes.rcValue()); 
        if (log.isDebugEnabled()) {
            log.debug("Selecting row, col: " + row + ", " + col); //$NON-NLS-1$//$NON-NLS-2$
        }
        
        Rectangle cellBounds;
        Object source = getRealComponent();
        //if row is header and col is existing
        if (implRow == -1 && implCol > -1) {
            cellBounds = adapter.getHeaderBounds(implCol);
            source = adapter.getTableHeader();
        } else {
            cellBounds = adapter.scrollCellToVisible(implRow, implCol);
        }        
        ClickOptions clickOptions = ClickOptions.create();
        clickOptions.setClickCount(clickCount).setScrollToVisible(false);
        clickOptions.setMouseButton(button);
        try {
            if (isExtendSelection) {
                getRobot().keyPress(getRealComponent(),
                        getExtendSelectionModifier());
            }
            getRobot().click(source, cellBounds, clickOptions, 
                    xPos, xUnits.equalsIgnoreCase(
                            ValueSets.Unit.pixel.rcValue()), 
                    yPos, yUnits.equalsIgnoreCase(
                            ValueSets.Unit.pixel.rcValue()));
        } finally {
            if (isExtendSelection) {
                getRobot().keyRelease(getRealComponent(),
                        getExtendSelectionModifier());
            }
        }
    }
    
    /**
     * Gets The modifier for an extended selection (more than one item)
     * @return the modifier
     */
    protected int getExtendSelectionModifier() {
        return KeyEvent.VK_CONTROL;
    }
}
