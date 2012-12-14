/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.implclasses;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.ChildTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.IndexNodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.ParentTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.PathBasedTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.SelectTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.SiblingTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.StringNodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperationConstraint;
import org.eclipse.jubula.rc.swt.interfaces.ITree;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.StringParsing;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


/**
 * @author BREDEX GmbH
 * @created 25.04.2007
 */
// FIXME zeb: We currently extend TreeImplClass in order to work around the 
//            issue that only one ImplClass per component class can be defined 
//            in config.xml.
public class TableTreeImplClass extends TreeImplClass 
    implements ITree {

    /** The component */
    private Tree m_treeTable;

    /** Ability to interact with Tree-like components */
    private TreeImplClass m_treeImplClass;
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_treeTable;
    }

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        // FIXME zeb: Remove the super() call once we no longer inherit from
        //            Tree
        super.setComponent(graphicsComponent);
        m_treeTable = (Tree)graphicsComponent;
        m_treeImplClass = new TreeImplClass();
        m_treeImplClass.setComponent(m_treeTable);
    }

    /**
     * <p>
     * Expands the Tree. Any node defined by the passed tree path is expanded,
     * if it is collapsed. If the node is already expanded, the Tree is left
     * unchanged. The tree path is a slash-seperated list of nodes that specifies
     * a valid top-down path in the Tree.
     * </p>
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath
     *            The tree path.
     * @param operator
     *            If regular expressions are used to determine the tree path
     * @throws StepExecutionException
     *             If the tree path is invalid or the double click fails.
     */
    public void gdExpand(String pathType, int preAscend, String treePath, 
            String operator) throws StepExecutionException {
        
        m_treeImplClass.gdExpand(pathType, preAscend, treePath, operator);
    }
    
    /**
     * Expands the tree. This method works like {@link #gdExpand(String)}, but
     * expects an enumeration of indices representing the top-down tree path.
     * Any index is the node's position at the current tree level.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param indexPath
     *            The index path
     * @throws StepExecutionException
     *             If the tree path is invalid or the double-click fails.
     */
    public void gdExpandByIndices(String pathType, int preAscend, 
            String indexPath) throws StepExecutionException {

        m_treeImplClass.gdExpandByIndices(pathType, preAscend, indexPath);
    }
    
    /**
     * Collapses the Tree. The passed tree path is a slash-seperated list of
     * nodes that specifies a valid top-down path in the Tree. The last node of
     * the tree path is collapsed if it is currently expanded. Otherwise, the Tree is
     * left unchanged.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath
     *            The tree path.
     * @param operator
     *            Whether regular expressions are used to determine the tree path. 
     *            <code>"matches"</code> for regex, <code>"equals"</code> for simple matching.
     * @throws StepExecutionException
     *             If the tree path is invalid or the double click to collapse
     *             the node fails.
     */
    public void gdCollapse(String pathType, int preAscend, String treePath, 
            String operator) throws StepExecutionException {
        
        m_treeImplClass.gdCollapse(pathType, preAscend, treePath, operator);
    }
    
    /**
     * Collapses the tree. This method works like {@link #gdCollapse(String)},
     * but expects an enumeration of indices representing the top-down tree
     * path. Any index is the node's position at the current tree level.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param indexPath
     *            The index path
     * @throws StepExecutionException
     *             If the tree path is invalid or the double-click to collapse
     *             the node fails.
     */
    public void gdCollapseByIndices(String pathType, int preAscend, 
            String indexPath) throws StepExecutionException {
        
        m_treeImplClass.gdCollapseByIndices(pathType, preAscend, indexPath);
    }

    /**
     * Selects a node relative to the currently selected node.
     * @param direction the direction to move.
     *                  directions:
     *                      UP - Navigates through parents
     *                      DOWN - Navigates through children
     *                      NEXT - Navigates to next sibling
     *                      PREVIOUS - Navigates to previous sibling
     * @param distance the distance to move
     * @param clickCount the click count to select the new cell.
     * @throws StepExecutionException if any error occurs
     */
    public void gdMove(String direction, int distance, int clickCount) 
        throws StepExecutionException {

        if (getColumnCount() > 0) {
            TreeOperationContext context;
            int mouseColumn = getMouseColumn();
            
            if (mouseColumn == -1) {
                context = 
                    new TableTreeOperationContext(
                        getEventThreadQueuer(), getRobot(), m_treeTable);
            } else {
                context = 
                    new TableTreeOperationContext(
                        getEventThreadQueuer(), getRobot(), m_treeTable, 
                        mouseColumn);
            }
            
            TreeItem selectedNode = getSelectedNode(context);
            
            TreeNodeOperation selectOp = 
                new SelectTreeNodeOperation(
                        ClickOptions.create().setClickCount(clickCount));
            TreeNodeOperationConstraint constraint = 
                new TreeNodeOperationConstraint();
            
            if (CompSystemConstants
                    .TREE_MOVE_UP.equalsIgnoreCase(direction)) {
                AbstractTreeNodeTraverser traverser = 
                    new ParentTraverser(context, distance, constraint);
                traverser.traversePath(selectOp, selectedNode);
            } else if (CompSystemConstants
                    .TREE_MOVE_DOWN.equalsIgnoreCase(direction)) {
                TreeNodeOperation expandOp = 
                    new ExpandCollapseTreeNodeOperation(false);
                AbstractTreeNodeTraverser expandTraverser = 
                    new ChildTraverser(context, distance - 1);
                expandTraverser.traversePath(expandOp, selectedNode);
                
                AbstractTreeNodeTraverser selectTraverser = 
                    new ChildTraverser(context, distance, constraint);
                selectTraverser.traversePath(selectOp, selectedNode);
                
            } else if (CompSystemConstants
                    .TREE_MOVE_NEXT.equalsIgnoreCase(direction)) {
                // Look through siblings
                AbstractTreeNodeTraverser traverser = 
                    new SiblingTraverser(context, distance, true, constraint);
                traverser.traversePath(selectOp, selectedNode);
                
            } else if (CompSystemConstants
                    .TREE_MOVE_PREVIOUS.equalsIgnoreCase(direction)) {
                // Look through siblings
                AbstractTreeNodeTraverser traverser = 
                    new SiblingTraverser(context, distance, false, constraint);
                traverser.traversePath(selectOp, selectedNode);
            }
        } else {
            super.gdMove(direction, distance, clickCount);
        }

    }

    /**
     * @return the column where the mouse pointer currently rests. Returns -1 if
     *         the mouse pointer is currently outside of the TableTree or if the
     *         TableTree has no columns.
     */
    private int getMouseColumn() {
        final Tree treeTable = m_treeTable;
        int column = ((Integer)getEventThreadQueuer().invokeAndWait(
                "getMouseColumn", new IRunnable() { //$NON-NLS-1$

                    public Object run() throws StepExecutionException {
                        Rectangle treeTableBounds = 
                            SwtUtils.getWidgetBounds(m_treeTable);
                        Point cursorPosition = 
                            treeTable.getDisplay().getCursorLocation();
                        boolean isCursorInBounds = 
                            treeTableBounds.contains(cursorPosition);
                        if (isCursorInBounds) {
                            int horizontalScrollOffset = 0;
                            ScrollBar horizontalBar = 
                                m_treeTable.getHorizontalBar();
                            if (horizontalBar != null 
                                    && !horizontalBar.isDisposed()) {
                                horizontalScrollOffset = 
                                    horizontalBar.getSelection();
                            }
                            Rectangle columnBounds = new Rectangle(
                                treeTableBounds.x - horizontalScrollOffset, 
                                treeTableBounds.y, 0, treeTableBounds.height);
                            for (int i = 0; 
                                i < treeTable.getColumnCount(); i++) {
                                
                                columnBounds.x += columnBounds.width;
                                columnBounds.width = 
                                    treeTable.getColumn(i).getWidth();
                                if (columnBounds.contains(cursorPosition)) {
                                    return new Integer(i);
                                }
                            }
                        }

                        return new Integer(-1);
                    }

                })).intValue();
        
        return column;
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
    public void gdSelect(String pathType, int preAscend, String treePath, 
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
    public void gdSelectByIndices(String pathType, int preAscend, 
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
     * Verifies whether the first selection in the tree has a rendered text at 
     * column <code>column</code> that is equal to <code>pattern</code>.
     * 
     * @param pattern The pattern
     * @param column
     *            The column containing the text to verify
     * @throws StepExecutionException If no node is selected or the verification fails.
     */
    public void gdVerifySelectedValue(String pattern, int column)
        throws StepExecutionException {

        gdVerifySelectedValue(pattern, MatchUtil.DEFAULT_OPERATOR, column);
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
    public void gdVerifySelectedValue(String pattern, String operator, 
        int column) throws StepExecutionException {
        
        final int implCol = IndexConverter.toImplementationIndex(column);
        checkColumnIndex(implCol);

        TableTreeOperationContext context = new TableTreeOperationContext(
            getEventThreadQueuer(), getRobot(), m_treeTable, implCol);

        String text = context.getNodeTextAtColumn(context.getSelectedNode());
        
        Verifier.match(text, pattern, operator);

    }
    
    
    /**
     * Tests whether the given treePath exists or not
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath the path to check
     * @param operator the RegEx operator
     * @param exists if true, the verify succeeds if the path DOES exist.
     *  If false, the verify succeeds if the path DOES NOT exist.
     */
    public void gdVerifyPath(String pathType, int preAscend, String treePath, 
            String operator, boolean exists) {
        
        m_treeImplClass.gdVerifyPath(
            pathType, preAscend, treePath, operator, exists);
    }
    
    /**
     * Tests whether the given treePath exists or not
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath the path to check
     * @param exists if true, the verify succeeds if the path DOES exist.
     *  If false, the verify succeeds if the path DOES NOT exist.
     */
    public void gdVerifyPathByIndices(String pathType, int preAscend, 
            String treePath, boolean exists) {
        
        m_treeImplClass.gdVerifyPathByIndices(
            pathType, preAscend, treePath, exists);
    }
    
    /**
     * Clicks the tree.
     * If the mouse pointer is in the tree no mouse move will be perfomed.
     * Otherwise, the mouse is first moved to the center of the tree.
     * 
     * @param count Number of mouse clicks
     * @param button Pressed button
     */
    public void gdClick(int count, int button) {
        m_treeImplClass.gdClick(count, button);
    }
    
    /**
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     * 
     * @param treePath The tree path
     * @return An array of string representing the tree path
     */
    private INodePath createIndexNodePath(Integer [] treePath) {
        return new IndexNodePath(treePath);
    }
    
    /**
     * Traverses the tree by searching for the nodes in the tree
     * path entry and calling the given operation on each matching node.
     * @param treePath The tree path.
     * @param pathType For example, "relative" or "absolute".
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param operation The tree node operation.
     * @throws StepExecutionException If the path traversion fails.
     */
    private void traverseTreeByPath(INodePath treePath, String pathType,
            int preAscend, TreeNodeOperation operation) 
        throws StepExecutionException {

        Validate.notNull(treePath);
        Validate.notNull(operation); 
        
        TreeOperationContext context = createContext();
        
        TreeItem startNode = getStartNode(pathType, preAscend, context); 

        AbstractTreeNodeTraverser traverser = 
            new PathBasedTraverser(context, treePath); 
        traverser.traversePath(operation, startNode);
    }    

    /**
     * Runs in the GUI thread.
     * @return the number of columns in the receivers component.
     */
    private int getColumnCount() {
        return ((Integer)getEventThreadQueuer().invokeAndWait(
                "getColumnCount", new IRunnable() { //$NON-NLS-1$

                    public Object run() throws StepExecutionException {
                        return new Integer(m_treeTable.getColumnCount());
                    }
            
                })).intValue();
    }

    /**
     * 
     * @return an appropriate context for operations on the receiver. This is 
     *         based on the number of columns the receiver's component has.
     */
    private TreeOperationContext createContext() {
        if (getColumnCount() > 0) {
            return new TableTreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_treeTable);
        }

        return new TableTreeOperationContext(
            getEventThreadQueuer(), getRobot(), m_treeTable);
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

        TableTreeOperationContext context = new TableTreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_treeTable, column);
        TreeItem startNode = getStartNode(pathType, preAscend, context);

        AbstractTreeNodeTraverser traverser = new PathBasedTraverser(
                context, treePath, new TreeNodeOperationConstraint());
        traverser.traversePath(operation, startNode);
    }
    /**
     * @param pathType
     *            For example, "relative" or "absolute".
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param context
     *            The context of the traversal.
     * @return The node at which to begin the traversal or <code>null</code>
     *         if the traversal should begin at the root of the node.
     */
    private TreeItem getStartNode(String pathType, final int preAscend, 
            final TreeOperationContext context) {

        TreeItem startNode;
        if (pathType.equals(CompSystemConstants.TREE_PATH_TYPE_RELATIVE)) {
            startNode = (TreeItem)getEventThreadQueuer().invokeAndWait(
                    "getStartNode", new IRunnable() { //$NON-NLS-1$

                        public Object run() throws StepExecutionException {
                            TreeItem curNode = getSelectedNode(context);
                            TreeItem child = curNode;
                            for (int i = 0; i < preAscend; ++i) {
                                if (curNode == null) {
                                    TestErrorEvent event = 
                                        EventFactory.createActionError(
                                                TestErrorEvent.
                                                    TREE_NODE_NOT_FOUND);
                                    throw new StepExecutionException(
                                            "Tree node not found: Parent of " //$NON-NLS-1$
                                            + child.toString(), event);
                                }
                                child = curNode;
                                curNode = curNode.getParentItem();
                            }
                            return curNode;
                        }

                    });
        } else if (pathType.equals(
                CompSystemConstants.TREE_PATH_TYPE_ABSOLUTE)) {
            startNode = null;
        } else {
            throw new StepExecutionException(
                    pathType + " is not a valid Path Type", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.INVALID_PARAM_VALUE));

        }
        
        return startNode;
    }

    /**
     * Returns the selected node
     * @param context context
     * @return node
     */
    private TreeItem getSelectedNode(TreeOperationContext context) {
        return (TreeItem)context.getSelectedNode();
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
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     * 
     * @param treePath The tree path
     * @return An array of string representing the tree path
     */
    private String[] splitTextTreePath(String treePath) {
        return StringParsing.splitToArray(treePath, 
                TestDataConstants.PATH_CHAR_DEFAULT,
                TestDataConstants.ESCAPE_CHAR_DEFAULT,
                true);
    }
    
    /**
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     * @param treePath The tree path
     * @return An array of indices (type <code>Integer</code>) representing the tree path
     * @throws StepExecutionException If the values of the passed path cannot be parsed
     */
    private Integer[] splitIndexTreePath(String treePath)
        throws StepExecutionException {
        
        Integer[] indexPath = null;
        String[] path = splitTextTreePath(treePath);
        if (path != null) {
            indexPath = new Integer[path.length];
            for (int i = 0; i < path.length; i++) {
                indexPath[i] = new Integer(IndexConverter.intValue(path[i]));
            }
        }
        return IndexConverter.toImplementationIndices(indexPath);
    }

    /**
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     * 
     * @param treePath The tree path
     * @param operator The operator
     * @return An array of string representing the tree path
     */
    private INodePath createStringNodePath(String [] treePath, 
            String operator) {
        return new StringNodePath(treePath, operator);
    }

    /**
     * 
     * @param index The 0-based column index to check.
     * @throws StepExecutionException if the column index is invalid.
     */
    private void checkColumnIndex(final int index) 
        throws StepExecutionException {
       
        int numColumns = ((Integer)getEventThreadQueuer().invokeAndWait(
                "checkColumnIndex",  //$NON-NLS-1$
                new IRunnable() {

                    public Object run() {
                        return new Integer(m_treeTable.getColumnCount());
                    }
            
                })).intValue();

        if ((index < 0 || index >= numColumns) && index != 0) {
            throw new StepExecutionException("Invalid column: " //$NON-NLS-1$
                + IndexConverter.toUserIndex(index), 
                EventFactory.createActionError(
                    TestErrorEvent.INVALID_INDEX));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void gdPopupByIndexPathAtIndexNode(String treeIndexPath,
            String popupIndexPath) {
        throwUnsupportedAction();
    }

    /**
     * {@inheritDoc}
     */
    public void gdPopupByIndexPathAtSelectedNode(String indexPath) {
        throwUnsupportedAction();
    }

    /**
     * {@inheritDoc}
     */
    public void gdPopupByIndexPathAtTextNode(String treeTextPath,
            String operator, String popupIndexPath) {
        throwUnsupportedAction();
    }

    /**
     * {@inheritDoc}
     */
    public void gdPopupByTextPathAtIndexNode(String treeIndexPath,
            String popupTextPath) {
        throwUnsupportedAction();
    }

    /**
     * {@inheritDoc}
     */
    public void gdPopupByTextPathAtSelectedNode(String textPath) {
        throwUnsupportedAction();
    }

    /**
     * {@inheritDoc}
     */
    public void gdPopupByTextPathAtTextNode(String treeTextPath,
            String operator, String popupTextPath) {
        throwUnsupportedAction();
    }

    /**
     * {@inheritDoc}
     */
    public void gdVerifyTextAtMousePosition(String pattern, String operator) {
        TreeItem itemAtMousePosition = m_treeImplClass.getItemAtMousePosition();
        int column = getMouseColumn();
        AbstractTreeOperationContext context;
        
        if (column != -1) {
            context = 
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(), m_treeTable, column);
        } else {
            context =
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(), m_treeTable);
        }

        Verifier.match(context.getRenderedText(itemAtMousePosition), 
                pattern, operator);

    }

    /**
     * {@inheritDoc}
     */
    public String gdStoreValueAtMousePosition(String variable) {
        TreeItem itemAtMousePosition = m_treeImplClass.getItemAtMousePosition();
        int column = getMouseColumn();
        AbstractTreeOperationContext context;
        
        if (column != -1) {
            context = 
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(), m_treeTable, column);
        } else {
            context =
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(), m_treeTable);
        }

        return context.getRenderedText(itemAtMousePosition);
    }

}
