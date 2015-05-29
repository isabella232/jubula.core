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
import java.util.List;
import java.util.concurrent.Callable;

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.PathBasedTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.SelectTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperationConstraint;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.adapter.TreeTableOperationContext;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.util.NodeTraverseHelper;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * Tester Class for the <code>TreeTableView</code>. If you are looking for more
 * implemented actions on TreeTables look at <code>TreeTableOperationContext</code>.
 *
 * @author BREDEX GmbH
 * @created 23.06.2014
 */
public class TreeTableViewTester extends TreeViewTester {

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

        TreeTableOperationContext context = new TreeTableOperationContext(
                getEventThreadQueuer(), getRobot(),
                (TreeTableView<?>) getRealComponent());
        context.setColumn(implCol);

        String text = context.getRenderedTextOfColumn(
                context.getSelectedNode());
        
        Verifier.match(text, pattern, operator);

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
     * @throws StepExecutionException If the path traversion fails.
     */
    private void traverseLastElementByPath(INodePath treePath, 
            String pathType, int preAscend, TreeNodeOperation operation,
            int column) 
        throws StepExecutionException {

        Validate.notNull(treePath);
        Validate.notNull(operation);

        TreeTableOperationContext context = new TreeTableOperationContext(
                getEventThreadQueuer(), getRobot(),
                (TreeTableView<?>) getRealComponent());
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
}
