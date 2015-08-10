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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.util.AbstractTraverser;
import org.eclipse.jubula.rc.javafx.util.GenericTraverseHelper;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.util.NodeTraverseHelper;
import org.eclipse.jubula.rc.javafx.util.Rounding;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.StringParsing;

import com.sun.javafx.scene.control.skin.TableColumnHeader;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;
/**
 * This context holds the tree and supports access to the Robot. It also
 * implements some general operations on the tree inside a TreeTableView.
 *
 * @author BREDEX GmbH
 * @created 23.06.2014
 */
public class TreeTableOperationContext extends
        AbstractTreeOperationContext<TreeTableView<?>, Object> {
    /** The AUT Server logger. */
    private static AutServerLogger log = new AutServerLogger(
            TreeTableOperationContext.class);
    
    /** The column on which to perform the operation **/
    private int m_column = 0;
    
    /**
     * Workaround to support nested Columns without modifying classes which would
     * affect other toolkits
     **/
    private List<TreeTableColumn<?, ?>> m_columns =
            new ArrayList<TreeTableColumn<?, ?>>();
    
    /**
     * Creates a new instance.
     *
     * @param queuer
     *            The queuer
     * @param robot
     *            The Robot
     * @param treeTable
     *            The treeTable
     */
    public TreeTableOperationContext(IEventThreadQueuer queuer, IRobot robot,
            TreeTableView<?> treeTable) {
        super(queuer, robot, treeTable);
        if (treeTable.getRoot() == null) {
            throw new StepExecutionException(
                    "Tree Table is empty.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
    }
    
    /**
     * Sets the column this operation context operates on.
     * @param column the column
     */
    public void setColumn(int column) {
        m_column = column;
    }

    @Override
    protected String convertValueToText(final Object node, final int row)
        throws StepExecutionException {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "convertValueToText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        if (node instanceof TreeItem) {
                            TreeItem<?> item = (TreeItem<?>) node;
                            if (item != null) {
                                Object val = item.getValue();
                                if (val != null) {
                                    return val.toString();
                                }
                            }
                        }
                        return node.toString();
                    }
                });
        return result;
    }

    @Override
    public Collection<String> getNodeTextList(final Object node) {
        List<String> res = new ArrayList<String>();
        int rowNotUsed = 0;
        String valText = convertValueToText(node, rowNotUsed);
        if (valText != null) {
            res.add(valText);
        }
        String rendText = getRenderedText(node);
        if (rendText != null) {
            res.add(rendText);
        }
        return res;
    }

    @Override
    public String getRenderedText(final Object node)
        throws StepExecutionException {
        int indexOfTreeColumn = getIndexOfTreeColumn();
        return getRenderedTextFromCell(node, indexOfTreeColumn);
    }

    /**
     * @return the index of the column containing the tree
     */
    private int getIndexOfTreeColumn() {
        ObservableList<?> columns = getTree().getColumns();
        TreeTableColumn<?, ?> treeColumn = getTree().getTreeColumn();
        int indexOfColumnContainingTree = 0;
        if (columns.contains(treeColumn)) {
            indexOfColumnContainingTree = columns.indexOf(treeColumn);
        }
        return indexOfColumnContainingTree;
    }
    
    /**
     * Gets the rendered Text from the cell of the currently set column
     * @param node the node
     * @return the rendered text
     * @throws StepExecutionException
     */
    public String getRenderedTextOfColumn(final Object node)
        throws StepExecutionException {
        return getRenderedTextFromCell(node, m_column);
    }

    /**
     * Gets the rendered text of a TreeTableCell
     * 
     * @param node
     *            this can be a cell or a tree item
     * @param col
     *            if node is a tree item this parameter is used to find the cell
     *            to get the rendered text from
     * @return the rendered text
     */
    private String getRenderedTextFromCell(final Object node, final int col) {
        if (node instanceof TreeItem<?>) {
            scrollNodeToVisible(node);
        }
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRenderedText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        if (node instanceof TreeTableCell) {
                            TreeTableCell<?, ?> cell = 
                                    (TreeTableCell<?, ?>) node;
                            return cell.getText();
                        } else if (node instanceof TreeItem) {
                            TreeItem<?> item = (TreeItem<?>) node;
                            TreeTableView<?> treeTable = getTree();
                            List<TreeTableCell> cells = NodeTraverseHelper
                                    .getInstancesOf(treeTable,
                                            TreeTableCell.class);
                            for (TreeTableCell<?, ?> cell : cells) {
                                // Nullchecks because of the virtual flow cells
                                // are created which might not be associated
                                // with a row or an item
                                TreeTableRow<?> ttRow = cell.getTreeTableRow();
                                if (ttRow == null) {
                                    continue;
                                }
                                TreeItem<?> checkItem = ttRow.getTreeItem();
                                if (checkItem == null) {
                                    continue;
                                }
                                if (item != null
                                        && checkItem.equals(item)
                                        && treeTable.getColumns().indexOf(
                                                cell.getTableColumn()) 
                                                == col) {
                                    return cell.getText();
                                }
                            }
                        }
                        return null;
                    }
                });
        return result;
    }


    @Override
    public boolean isVisible(final Object node) {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait("isVisible", //$NON-NLS-1$
                new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        TreeItem<?>  item = (TreeItem<?>) node;
                        return item.isExpanded() && getTree().isVisible();
                    }
                });
        return result;
    }

    @Override
    public Rectangle getVisibleRowBounds(final Rectangle rowBounds) {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getVisibleRowBounds", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TreeTableView<?> tree = getTree();
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        tree.layout();
                        Rectangle visibleTreeBounds = new Rectangle(0, 0,
                                Rounding.round(tree.getWidth()), Rounding
                                        .round(tree.getHeight()));
                        return rowBounds.intersection(visibleTreeBounds);
                    }
                });
        return result;
    }

    @Override
    public void scrollNodeToVisible(final Object node) {
        EventThreadQueuerJavaFXImpl.invokeAndWait("scrollNodeToVisible", //$NON-NLS-1$
                new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        int index = ((TreeTableView) getTree())
                                .getRow((TreeItem<?>) node);
                        getTree().scrollTo(index);
                        getTree().scrollToColumnIndex(m_column);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        getTree().layout();
                        return null;
                    }
                });
    }

    @Override
    public void clickNode(final Object node, final ClickOptions clickOps) {
        scrollNodeToVisible(node);
        Rectangle rowBounds = getNodeBounds(node);
        Rectangle visibleRowBounds = getVisibleRowBounds(rowBounds);
        getRobot().click(getTree(), visibleRowBounds, clickOps);
    }

    @Override
    public void expandNode(final Object node) {
        scrollNodeToVisible(node);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait("expandNode", //$NON-NLS-1$
                new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        TreeItem<?> item = (TreeItem<?>) node;
                        TreeTableView<?> treeTable = getTree();
                        List<TreeTableCell> cells = NodeTraverseHelper
                                .getInstancesOf(treeTable, TreeTableCell.class);
                        for (TreeTableCell<?, ?> treeTableCell : cells) {
                            // Nullchecks because of the virtual flow cells
                            // are created which might not be associated
                            // with a row or an item
                            TreeTableRow<?> ttRow = treeTableCell
                                    .getTreeTableRow();
                            if (ttRow == null) {
                                continue;
                            }
                            TreeItem<?> checkItem = ttRow.getTreeItem();
                            if (checkItem == null) {
                                continue;
                            }
                            if (item != null
                                    && checkItem.equals(item)
                                    && !item.isExpanded()) {
                                return treeTableCell.getTreeTableRow()
                                        .getDisclosureNode();
                            }
                        }
                        return null;
                    }

                });
        if (result != null) {
            getRobot().click(result, null,
                    ClickOptions.create().setClickCount(1).setMouseButton(1));
        }
        EventThreadQueuerJavaFXImpl.invokeAndWait("expandNodeCheckIfExpanded", //$NON-NLS-1$
                new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        TreeItem<?> item = (TreeItem<?>) node;
                        if (!getTree().isDisabled() && !item.isExpanded()) {
                            log.warn("Expand node fallback used for: " //$NON-NLS-1$
                                    + item.getValue());
                            item.setExpanded(true);
                        }
                        return null;
                    }
                });
    }

    @Override
    public void collapseNode(final Object node) {
        scrollNodeToVisible(node);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait("collapseNode", //$NON-NLS-1$
                new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        TreeItem<?> item = (TreeItem<?>) node;
                        TreeTableView<?> treeTable = getTree();
                        List<TreeTableCell> cells = NodeTraverseHelper
                                .getInstancesOf(treeTable, TreeTableCell.class);
                        for (TreeTableCell<?, ?> treeTableCell : cells) {
                            // Nullchecks because of the virtual flow cells
                            // are created which might not be associated
                            // with a row or an item
                            TreeTableRow<?> ttRow = treeTableCell
                                    .getTreeTableRow();
                            if (ttRow == null) {
                                continue;
                            }
                            TreeItem<?> checkItem = ttRow.getTreeItem();
                            if (checkItem == null) {
                                continue;
                            }
                            if (item != null
                                    && checkItem.equals(item)
                                    && item.isExpanded()) {
                                return treeTableCell.getTreeTableRow()
                                        .getDisclosureNode();
                            }
                        }
                        return null;
                    }

                });
        if (result != null) {
            getRobot().click(result, null,
                    ClickOptions.create().setClickCount(1).setMouseButton(1));
        }
        EventThreadQueuerJavaFXImpl.invokeAndWait("collapseNodeCheckIfExpanded", //$NON-NLS-1$
                new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        TreeItem<?> item = (TreeItem<?>) node;
                        if (!getTree().isDisabled() && item.isExpanded()) {
                            log.warn("Expand node fallback used for: " //$NON-NLS-1$
                                    + item.getValue());

                            item.setExpanded(true);
                        }
                        return null;
                    }
                });
    }

    @Override
    public Object getSelectedNode() {
        TreeItem<?> result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedNode", new Callable<TreeItem<?>>() { //$NON-NLS-1$

                    @Override
                    public TreeItem<?> call() throws Exception {
                        return getTree().getSelectionModel().getSelectedItem();
                    }
                });
        if (result != null) {
            SelectionUtil.validateSelection(new Object[] { result });
        } else {
            SelectionUtil.validateSelection(new Object[] {});
        }
        return result;
    }

    @Override
    public Object[] getSelectedNodes() {
        Object[] result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedNodes", new Callable<Object[]>() { //$NON-NLS-1$

                    @Override
                    public Object[] call() throws Exception {

                        return getTree().getSelectionModel().getSelectedItems()
                                .toArray();
                    }
                });
        SelectionUtil.validateSelection(result);
        return result;
    }

    @Override
    public Object[] getRootNodes() {
        Object[] result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRootNodes", new Callable<Object[]>() { //$NON-NLS-1$

                    @Override
                    public Object[] call() throws Exception {
                        TreeTableView<?> tree = getTree();

                        // If the root is visible, just return that.
                        if (tree.showRootProperty().getValue()) {
                            return new Object[] { tree.getRoot() };
                        }

                        // If the root is not visible, return all direct
                        // children of the
                        // non-visible root.
                        return getChildren(tree.getRoot());
                    }
                });
        return result;
    }

    @Override
    public Object getParent(final Object child) {
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait("getParent", //$NON-NLS-1$
                new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {

                        return ((TreeItem<?>) child).getParent();
                    }
                });

        return result;
    }

    @Override
    public Object getChild(final Object parent, final int index) {
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait("getChild", //$NON-NLS-1$
                new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {

                        return ((TreeItem<?>)parent).getChildren().get(index);
                    }
                });

        return result;
    }

    @Override
    public int getNumberOfChildren(final Object parent) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getNumberOfChildren", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {

                        return ((TreeItem<?>) parent).getChildren().size();
                    }
                });
    }

    @Override
    public boolean isLeaf(final Object node) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("isLeaf", //$NON-NLS-1$
                new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {

                        return ((TreeItem<?>) node).getChildren().size() == 0;
                    }
                });
    }

    @Override
    public Object[] getChildren(final Object parent) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getChildren", //$NON-NLS-1$
                new Callable<Object[]>() {

                    @Override
                    public Object[] call() throws Exception {
                        return ((TreeItem<?>) parent).getChildren().toArray();
                    }
                });
    }

    @Override
    public Rectangle getNodeBounds(final Object node) {
        scrollNodeToVisible(node);
        Rectangle nodeBounds = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeBounds", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TreeTableView<?> treeTable = getTree();
                        treeTable.layout();
                        TreeItem<?> item = (TreeItem<?>) node;
                        List<TreeTableCell> cells = NodeTraverseHelper
                                .getInstancesOf(treeTable, TreeTableCell.class);
                        for (TreeTableCell<?, ?> cell : cells) {
                            // Nullchecks because of the virtual flow cells
                            // are created which might not be associated
                            // with a row or an item
                            TreeTableRow<?> ttRow = cell.getTreeTableRow();
                            if (ttRow == null) {
                                continue;
                            }
                            TreeItem<?> checkItem = ttRow.getTreeItem();
                            if (checkItem == null) {
                                continue;
                            }
                            if (item != null
                                    && checkItem.equals(item)
                                    && treeTable.getColumns().indexOf(
                                            cell.getTableColumn()) 
                                            == m_column) {
                                Rectangle b = NodeBounds
                                        .getAbsoluteBounds(cell);
                                Rectangle treeB = NodeBounds
                                        .getAbsoluteBounds(treeTable);
                                return new Rectangle(
                                        Math.abs(treeB.x - b.x),
                                        Math.abs(treeB.y - b.y),
                                        Rounding.round(b.getWidth()),
                                        Rounding.round(b.getHeight()));
                            }
                        }
                        return null;
                    }
                });
        if (nodeBounds == null) {
            throw new StepExecutionException(
                    "Could not retrieve visible node bounds.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_VISIBLE));
        }
        return nodeBounds;
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getIndexOfChild", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        if (parent == null) {
                            Object[] rootNodes = getRootNodes();
                            for (int i = 0; i < rootNodes.length; i++) {
                                if (ObjectUtils.equals(rootNodes[i], child)) {
                                    return i;
                                }
                            }

                            return -1;
                        }
                        List<?> children = ((TreeItem<?>)parent).getChildren();
                        if (children.contains(child)) {
                            return children.indexOf(child);
                        }
                        return -1;
                    }
                });
    }
    
    /**
     * Gets row index from string with index or text of first column
     * 
     * @param row
     *            index or value in first row
     * @param operator
     *            the operation used to verify
     * @return integer of String of row
     */
    public int getRowFromString(final String row, final String operator) {
        Integer result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRowFromString", new Callable<Integer>() { //$NON-NLS-1$
                    @Override
                    public Integer call() throws Exception {
                        Integer rowInt = null;
                        TreeTableView<?> treetable = getTree();
                        if (treetable.getColumns().size() <= 0) {
                            throw new StepExecutionException(
                                    "No Header", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                            TestErrorEvent.NO_HEADER));
                        }
                        try {
                            rowInt = IndexConverter
                                    .toImplementationIndex(Integer
                                            .parseInt(row));
                        } catch (NumberFormatException nfe) {
                            TreeTableColumn<?, ?> firstColumn =
                                    treetable.getColumns().get(0);
                            int i = 0;
                            String cellTxt = getCellText(i, 0);
                            while (cellTxt != null) {
                                cellTxt = getCellText(i, 0);
                                if (MatchUtil.getInstance().match(cellTxt, row,
                                        operator)) {
                                    return new Integer(i);
                                }
                                i++;
                            }
                        }
                        if (rowInt == null) {
                            throw new StepExecutionException(
                                "Row not found", //$NON-NLS-1$
                                EventFactory
                                        .createActionError(TestErrorEvent.
                                                NOT_FOUND));
                        }
                        return rowInt;
                    }
                });
        return result.intValue();
    }
    
    /**
     * Gets column index from string with index or text of first row
     * 
     * @param colPath
     *            index or value in first col
     * @param op
     *            the operation used to verify
     * @return integer of String of col
     */
    public int getColumnFromString(final String colPath, final String op) {
        Integer result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnFromString", new Callable<Integer>() { //$NON-NLS-1$
                    @Override
                    public Integer call() throws Exception {
                        TreeTableView<?> treeTable = getTree();
                        List<String> path = StringParsing.splitToList(colPath,
                                TestDataConstants.PATH_CHAR_DEFAULT,
                                TestDataConstants.ESCAPE_CHAR_DEFAULT, false);
                        TreeTableColumn<?, ?> column = determineColumn(
                                colPath, op, treeTable, path); 
                        if (column == null) {
                            throw new StepExecutionException("Column not found", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                                    TestErrorEvent.NOT_FOUND));
                        }
                        if (treeTable.getVisibleLeafColumns()
                                .contains(column)) {
                            return treeTable.getVisibleLeafColumns()
                                    .indexOf(column);
                        }
                        if (!m_columns.contains(column)) {
                            m_columns.add(column);
                        }
                        return m_columns.indexOf(column); 
                    }
                });
        return result.intValue();
    }
    
    /**
     * @param colPath
     *            index or value in first col
     * @param op
     *            the operation used to verify
     * @param treeTable
     *            the tree table
     * @param path
     *            path
     * @return the column matching the path
     */
    private TreeTableColumn<?, ?> determineColumn(final String colPath,
            final String op, TreeTableView<?> treeTable,
            List<String> path) {
        ObservableList<?> columns;
        if (colPath.contains("" + TestDataConstants.PATH_CHAR_DEFAULT)) { //$NON-NLS-1$
            columns = treeTable.getColumns();
        } else {
            columns = treeTable.getVisibleLeafColumns();
        }
        TreeTableColumn<?, ?> column = null;
        Iterator<String> pathIterator = path.iterator();
        String currCol = null;
        while (pathIterator.hasNext()) {
            try {
                currCol = pathIterator.next();
                int usrIdxCol = Integer.parseInt(currCol);
                if (usrIdxCol == 0) {
                    usrIdxCol = usrIdxCol + 1;
                }
                int i = IndexConverter.toImplementationIndex(usrIdxCol);
                if (pathIterator.hasNext()) {
                    columns = ((TreeTableColumn<?, ?>)
                            columns.get(i)).getColumns();
                } else {
                    column = (TreeTableColumn<?, ?>) columns.get(i);
                }
            } catch (NumberFormatException nfe) {
                try {
                    if (path.size() <= 1) {
                        columns = treeTable.getColumns();
                    }
                    if (columns.size() <= 0) {
                        throw new StepExecutionException(
                                "No Columns", EventFactory.createActionError(//$NON-NLS-1$
                                        TestErrorEvent.NO_HEADER));
                    }
                    for (Object c: columns) {
                        TreeTableColumn<?, ?> col =
                                (TreeTableColumn<?, ?>) c;
                        String header = col.getText();
                        if (MatchUtil.getInstance().match(
                                header, currCol, op)) {
                            column = col;
                            if (pathIterator.hasNext()) {
                                columns = col.getColumns();
                            }
                            break;
                        }
                    }
                } catch (IllegalArgumentException iae) {
                    // do nothing here
                }
            }
        }
        return column;
    }
    
    /**
     * gets header bounds for column
     * 
     * @param column
     *            the zero based index of the column.
     * @return The rectangle of the header
     */
    public Rectangle getHeaderBounds(final int column) {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getHeaderBounds", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TreeTableView<?> treeTable = getTree();
                        TreeTableColumn col;
                        if (m_columns.size() > 0) {
                            col = m_columns.get(column);
                        } else {
                            col = treeTable.getVisibleLeafColumn(column);
                        }
                        treeTable.scrollToColumn(col);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        treeTable.layout();
                        Parent headerRow = (Parent) treeTable
                                .lookup("TableHeaderRow"); //$NON-NLS-1$
                        Set<Node> columnHeaders = headerRow
                                .lookupAll("column-header"); //$NON-NLS-1$
                        Point2D parentPos = treeTable.localToScreen(0, 0);

                        for (Node n : columnHeaders) {
                            //DEPENDENCY TO INTERNAL API
                            TableColumnHeader colH = (TableColumnHeader) n;
                            if (colH.getTableColumn().equals(col)) {
                                Rectangle b = NodeBounds
                                        .getAbsoluteBounds(n);
                                Rectangle tableB = NodeBounds
                                        .getAbsoluteBounds(treeTable);
                                return new Rectangle(
                                        Math.abs(tableB.x - b.x),
                                        Math.abs(tableB.y - b.y),
                                        Rounding.round(b.getWidth()),
                                        Rounding.round(b.getHeight()));
                            }
                        }
                        return null;
                    }
                });
        return result;
    }
    
    /**
     * @return The TableHeader if there is one,otherwise
     *                  the table is returned.
     */
    public Object getTableHeader() {
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTableHeader", new Callable<Object>() { //$NON-NLS-1$

                    @Override
                    public Object call() throws Exception {
                        return getTree().lookup("TableHeaderRow"); //$NON-NLS-1$
                    }
                });
        return result;
    }

    /**
     * Scrolls the passed cell (as row and column) to visible.<br>
     * This method must return null if there is no scrolling.
     * 
     * @param row
     *            zero based index of the row.
     * @param column
     *            zero based index of the column.
     * @return The rectangle of the cell.
     * @throws StepExecutionException
     *             If getting the cell rectangle or the scrolling fails.
     */
    public Rectangle scrollCellToVisible(final int row, final int column)
        throws StepExecutionException {
        Rectangle result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "scrollCellToVisible", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TreeTableView<?> treeTable = getTree();
                        TreeTableColumn col = null;
                        if (m_columns.size() == 0) {
                            col = treeTable.getVisibleLeafColumn(column);
                        } else {
                            col = m_columns.get(column);
                        }

                        treeTable.scrollTo(row);
                        treeTable.scrollToColumn(col);
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        treeTable.layout();
                        List<? extends TreeTableCell> tCells =
                                NodeTraverseHelper.getInstancesOf(
                                        treeTable, TreeTableCell.class);
                        for (TreeTableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col
                                    && cell.getTreeTableView() == treeTable) {

                                Rectangle b = NodeBounds
                                        .getAbsoluteBounds(cell);
                                Rectangle tableB = NodeBounds
                                        .getAbsoluteBounds(treeTable);
                                return new Rectangle(
                                        Math.abs(tableB.x - b.x),
                                        Math.abs(tableB.y - b.y),
                                        Rounding.round(b.getWidth()),
                                        Rounding.round(b.getHeight()));
                            }
                        }
                        return null;
                    }
                });
        return result;
    }
    
    /**
     * @return The currently selected cell of the Table.
     * @throws StepExecutionException
     *             If no cell is selected.
     */
    public Cell getSelectedCell() throws StepExecutionException {
        Cell result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedCell", new Callable<Cell>() { //$NON-NLS-1$

                    @Override
                    public Cell call() throws StepExecutionException {
                        TreeTableView<?> treeTable = getTree();
                        ObservableList<?> list = treeTable
                                .getSelectionModel().getSelectedCells();

                        if (list.size() > 0) {
                            TreeTablePosition<?, ?> pos = null;
                            for (Object object : list) {
                                TreeTablePosition<?, ?> curr =
                                        (TreeTablePosition<?, ?>) object;
                                if (curr.getRow() == treeTable
                                        .getSelectionModel()
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
    
    /**
     * 
     * @param row
     *            zero based index of the row
     * @param column
     *            zero based index of the column
     * @return <code>true</code> if the Cell is editable, <code>false</code>
     *         otherwise
     */
    public boolean isCellEditable(final int row, final int column) {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isCellEditable", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TreeTableView treeTable = getTree();
                        if (treeTable.isEditable()) {
                            TreeTableColumn<?, ?> col = null;
                            if (m_columns.size() == 0) {
                                col = treeTable.getVisibleLeafColumn(column);
                            } else {
                                col = m_columns.get(column);
                            }
                            if (col.isEditable()) {
                                treeTable.scrollTo(row);
                                treeTable.scrollToColumn(col);
                                treeTable.layout();
                                List<? extends TreeTableCell> tCells = 
                                        NodeTraverseHelper.getInstancesOf(
                                                treeTable, TreeTableCell.class);
                                for (TreeTableCell<?, ?> cell : tCells) {
                                    if (cell.getIndex() == row
                                            && cell.getTableColumn() == col
                                            && cell.getTreeTableView()
                                                == treeTable
                                            && NodeTraverseHelper
                                            .isVisible(cell)) {
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
    
    /**
     * Returns the text of the column appearing in the view at column position
     * <code>column</code>.
     * 
     * @param column
     *            the zero based index of the column in the view being queried
     * @return the text of the column at position <code>column</code> in the
     *         view where the first column is column 0
     */
    public String getColumnHeaderText(final int column) {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getColumnHeaderText", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        if (m_columns.size() > 0) {
                            TreeTableColumn<?, ?> tCol = m_columns.get(column);
                            return tCol.getText();
                        }
                        TreeTableColumn<?, ?> tCol = getTree()
                                .getVisibleLeafColumn(column);
                        return tCol.getText();
                    }
                });
        return result;
    }
    
    /**
     * Gets the number of rows
     * 
     * @return the number of rows
     */
    public int getRowCount() {
        int result = EventThreadQueuerJavaFXImpl.invokeAndWait("getRowCount", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return getTree().getExpandedItemCount();
                    }
                });
        return result;
    }
    
    /**
     * Gets the number of columns
     * 
     * @return the number of columns
     */
    public int getColumnCount() {
        int result = EventThreadQueuerJavaFXImpl.invokeAndWait(
            "getColumnCount", new Callable<Integer>() { //$NON-NLS-1$
                @Override
                public Integer call() throws Exception {
                    int counter = 0;
                    for (TreeTableColumn<?, ?> column
                            : getTree().getColumns()) {
                        counter += new GenericTraverseHelper
                                <TreeTableColumn, TreeTableColumn>()
                                .getInstancesOf(
                                        new AbstractTraverser
                                        <TreeTableColumn, TreeTableColumn>(
                                                column) {

                                            @Override
                                            public Iterable<TreeTableColumn> 
                                                getTraversableData() {
                                                return this.getObject()
                                                        .getColumns();
                                            }
                                        }, TreeTableColumn.class).size();
                    }
                    return counter + getTree().getColumns().size();
                }
            });
        return result;
    }
    
    /**
     * @param row
     *            the zero based index of the row
     * @param column
     *            the zero based index of the column
     * @return the text of the cell of the given coordinates
     */
    public String getCellText(final int row, final int column) {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getCellText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        TreeTableView table = getTree();
                        TreeTableColumn<?, ?> col = null;
                        if (m_columns.size() == 0) {
                            col = table.getVisibleLeafColumn(column);
                        } else {
                            col = m_columns.get(column);
                        }
                        table.scrollTo(row);
                        table.scrollToColumn(col);
                        table.layout();
                        List<? extends TreeTableCell> tCells =
                                NodeTraverseHelper.getInstancesOf(table,
                                        TreeTableCell.class);
                        for (TreeTableCell<?, ?> cell : tCells) {
                            if (cell.getIndex() == row
                                    && cell.getTableColumn() == col
                                    && cell.getTreeTableView() == table
                                    && NodeTraverseHelper.isVisible(cell)) {
                                String txt = cell.getText();
                                if (txt == null && cell.isEditing()) {
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

    /**
     * 
     * @return <code>true</code> if the header is visible, <code>false</code> otherwise
     */
    public boolean isHeaderVisible() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isHeaderVisible", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        Pane header = (Pane) getTree().lookup("TableHeaderRow"); //$NON-NLS-1$
                        if (header != null) {
                            return header.isVisible();
                        }
                        return false;
                    }
                });
        return result;
    }
}