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
import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.util.NodeTraverseHelper;
import org.eclipse.jubula.rc.javafx.util.Rounding;
/**
 * This context holds the tree and supports access to the Robot. It also
 * implements some general operations on the tree inside a TreeTableView.
 *
 * @author BREDEX GmbH
 * @created 23.06.2014
 */
public class TreeTableOperationContext 
    extends AbstractTreeOperationContext<TreeTableView<?>> {
    /** The AUT Server logger. */
    private static AutServerLogger log = new AutServerLogger(
            TreeTableOperationContext.class);
    
    /** The column **/
    private int m_column = 0;
    
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
        Validate.notNull(treeTable.getRoot());
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
        return getRenderedTextFromCell(node, 0);
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
                            TreeTableView<?> treeTable = 
                                    getTree();
                            List<TreeTableCell> cells = 
                                    new NodeTraverseHelper<TreeTableCell>()
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
                        return item.isExpanded()
                                && getTree().isVisible();
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
    public boolean isExpanded(final Object node) {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isExpanded", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        TreeItem<?>  item = (TreeItem<?>) node;
                        return item.isExpanded();
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
                        getTree()
                                .scrollToColumnIndex(m_column);
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
                        List<TreeTableCell> cells = 
                                new NodeTraverseHelper<TreeTableCell>()
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
                        if (!getTree().isDisabled()
                                && !item.isExpanded()) {
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
                        List<TreeTableCell> cells = 
                                new NodeTraverseHelper<TreeTableCell>()
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
                        if (!getTree().isDisabled()
                                && item.isExpanded()) {
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
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedNode", new Callable<Object>() { //$NON-NLS-1$

                    @Override
                    public Object call() throws Exception {
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
                "getSelectedNode", new Callable<Object[]>() { //$NON-NLS-1$

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
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeBounds", new Callable<Rectangle>() { //$NON-NLS-1$

                    @Override
                    public Rectangle call() throws Exception {
                        TreeTableView<?> treeTable = getTree();
                        treeTable.layout();
                        TreeItem<?> item = (TreeItem<?>) node;
                        List<TreeTableCell> cells = 
                                new NodeTraverseHelper<TreeTableCell>()
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
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getIndexOfChild", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {

                        return ((TreeItem<?>) parent).getChildren().indexOf(
                                child);
                    }
                });
    }

}
