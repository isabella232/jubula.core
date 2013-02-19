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
package org.eclipse.jubula.rc.swt.tester;

import java.util.StringTokenizer;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.ChildTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.ParentTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.PathBasedTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.SelectTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.SiblingTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.StandardDepthFirstTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperationConstraint;
import org.eclipse.jubula.rc.common.tester.AbstractTreeTester;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swt.driver.DragAndDropHelperSwt;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.tester.tree.TableTreeOperationContext;
import org.eclipse.jubula.rc.swt.tester.tree.ToggleCheckboxOperation;
import org.eclipse.jubula.rc.swt.tester.tree.TreeOperationContext;
import org.eclipse.jubula.rc.swt.tester.tree.VerifyCheckboxOperation;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Tree;
/**
 * Toolkit specific commands for the <code>Tree</code>
 *
 * @author BREDEX GmbH
 */
public class TreeTester extends AbstractTreeTester {
    /**
     * Finds the item at a given position in the tree.
     *
     * @author BREDEX GmbH
     * @created Jul 28, 2010
     */
    private static final class ItemAtPointTreeNodeOperation 
            extends AbstractTreeNodeOperation {

        /** the item that was found at the given position */
        private TreeItem m_itemAtPoint;
        
        /** the position (in absolute coordinates) at which to find the item */
        private Point m_absPoint;
        
        /** 
         * the bounds (in absolute coordinates) of the tree in which the 
         * search should take place 
         */
        private Rectangle m_absTreeBounds;
        
    
    /**
     * Constructor
     * 
     * @param absPoint The position (in absolute coordinates) at which to 
     *                 find the item.
     * @param absTreeBounds The bounds (in absolute coordinates) of the 
     *                      tree in which the search should take place. 
     */
        public ItemAtPointTreeNodeOperation(Point absPoint, 
            Rectangle absTreeBounds) {
            m_absPoint = absPoint;
            m_absTreeBounds = absTreeBounds;
        }
    
    /**
     * {@inheritDoc}
     */
        public boolean operate(Object node) throws StepExecutionException {
            if (getContext().isVisible(node) && node instanceof TreeItem) {
                TreeItem currentItem = (TreeItem)node;
                final Rectangle absItemBounds = 
                        SwtUtils.getBounds(currentItem);
                absItemBounds.x = m_absTreeBounds.x;
                absItemBounds.width = m_absTreeBounds.width;
                if (SwtUtils.containsInclusive(
                        absItemBounds, m_absPoint)) {
                    m_itemAtPoint = currentItem;
                    return false;
                }
            }
        
            return true;
        }
    
    /**
     * 
     * @return the item found at the given position, or <code>null</code> if
     *         no item was found. Note that this method will always return 
     *         <code>null</code> if called before or during execution of
     *         {@link #operate(Object)}.
     */
        public TreeItem getItemAtPoint() {
            return m_itemAtPoint;
        }
    }
        
    /**
     * @return The event thread queuer.
     */
    protected IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    /**
     * 
     * @return the Tree
     */
    private Tree getTree() {
        return (Tree) getComponent().getRealComponent();
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcVerifyTextAtMousePosition(String pattern, String operator) {
        TreeItem itemAtMousePosition = (TreeItem) getNodeAtMousePosition();
        int column = getMouseColumn();
        AbstractTreeOperationContext context;
        
        if (column != -1) {
            context = 
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(),
                    getTree(), column);
        } else {
            context =
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(), 
                    getTree());
        }

        Verifier.match(context.getRenderedText(itemAtMousePosition), 
                pattern, operator);

    }
    



    /**
     * {@inheritDoc}
     */
    public void rcDragByTextPath(int mouseButton, String modifier, 
            String pathType, int preAscend, String treePath, String operator) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);

        SwtUtils.waitForDisplayIdle(getTree().getDisplay());

        rcSelect(pathType, preAscend, treePath, operator, 0, 1, 
                CompSystemConstants.EXTEND_SELECTION_NO);

        SwtUtils.waitForDisplayIdle(getTree().getDisplay());
    }

    /**
     * {@inheritDoc}
     */
    public void rcDropByTextPath(final String pathType, final int preAscend, 
            final String treePath, final String operator, int delayBeforeDrop) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();

        try {
            pressOrReleaseModifiers(dndHelper.getModifier(), true);
            getEventThreadQueuer().invokeAndWait("gdDropByTextPath - perform drag", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());                    
                    
                    CAPUtil.shakeMouse();

                    return null;
                }            
            });

            // Post a MouseMove event in order to break the Display out of its
            // post-drag "freeze". It appears as though the mouse position 
            // change needs to be extreme in order to nudge the Display back 
            // into action (i.e. (<mouse-location> + 1) was insufficient), 
            // so the default Event values (x, y = 0) are used.
            Event wakeEvent = new Event();
            wakeEvent.type = SWT.MouseMove;
            getTree().getDisplay().post(wakeEvent);

            waitForDisplayUpdate();

            // drop
            rcSelect(pathType, preAscend, treePath, operator, 0, 1,
                    CompSystemConstants.EXTEND_SELECTION_NO);

            SwtUtils.waitForDisplayIdle(getTree().getDisplay());
            waitBeforeDrop(delayBeforeDrop);
            
        } finally {
            robot.mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(
                    dndHelper.getModifier(), false);
            SwtUtils.waitForDisplayIdle(getTree().getDisplay());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rcDragByIndexPath(int mouseButton, String modifier, 
            String pathType, int preAscend, String indexPath) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        
        SwtUtils.waitForDisplayIdle(getTree().getDisplay());

        rcSelectByIndices(pathType, preAscend, indexPath, 0, 1, 
                CompSystemConstants.EXTEND_SELECTION_NO);
        
        SwtUtils.waitForDisplayIdle(getTree().getDisplay());
    }

    /**
     * {@inheritDoc}
     */
    public void rcDropByIndexPath(final String pathType, final int preAscend, 
            final String indexPath, int delayBeforeDrop) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();
        try {
            pressOrReleaseModifiers(dndHelper.getModifier(), true);
            getEventThreadQueuer().invokeAndWait("gdDropByIndexPath - perform drag", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());

                    CAPUtil.shakeMouse();

                    return null;
                }            
            });

            // Post a MouseMove event in order to break the Display out of its
            // post-drag "freeze". It appears as though the mouse position 
            // change needs to be extreme in order to nudge the Display back 
            // into action (i.e. (<mouse-location> + 1) was insufficient), 
            // so the default Event values (x, y = 0) are used.
            Event wakeEvent = new Event();
            wakeEvent.type = SWT.MouseMove;
            getTree().getDisplay().post(wakeEvent);
            
            waitForDisplayUpdate();

            // drop
            rcSelectByIndices(pathType, preAscend, indexPath, 0, 1, 
                    CompSystemConstants.EXTEND_SELECTION_NO);

            SwtUtils.waitForDisplayIdle(getTree().getDisplay());
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            robot.mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(),
                    false);
            SwtUtils.waitForDisplayIdle(getTree().getDisplay());
        }
    }
    /**
     * {@inheritDoc}
     */
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        return (TreeItem)getEventThreadQueuer().invokeAndWait("getItemAtMousePosition", new IRunnable() { //$NON-NLS-1$
            
            public Object run() throws StepExecutionException {
                Point mousePos = SwtUtils.convertToSwtPoint(
                        getRobot().getCurrentMousePosition());
                ItemAtPointTreeNodeOperation op = 
                    new ItemAtPointTreeNodeOperation(
                            mousePos, SwtUtils.getWidgetBounds(getTree()));

                TreeItem topItem = getTree().getTopItem();
                if (topItem != null) {
                    
                    // FIXME zeb This may be slow for very large trees, as the 
                    //           search may continue long past the
                    //           visible client area of the tree.
                    //           It may also cause problems with regard to 
                    //           lazy/virtual nodes. 
                    StandardDepthFirstTraverser traverser = 
                        new StandardDepthFirstTraverser(
                            new TreeOperationContext(
                                getEventThreadQueuer(), getRobot(), getTree()));
                    traverser.traversePath(op, topItem);
                    if (op.getItemAtPoint() != null) {
                        return op.getItemAtPoint();
                    }
                    
                }

                throw new StepExecutionException("No tree node found at mouse position.", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
            }
        });
    }
    
    /**
     * @return the column where the mouse pointer currently rests. Returns -1 if
     *         the mouse pointer is currently outside of the TableTree or if the
     *         TableTree has no columns.
     */
    private int getMouseColumn() {
        final Tree treeTable = getTree();
        int column = ((Integer)getEventThreadQueuer().invokeAndWait(
                "getMouseColumn", new IRunnable() { //$NON-NLS-1$

                    public Object run() throws StepExecutionException {
                        Rectangle treeTableBounds = 
                            SwtUtils.getWidgetBounds(getTree());
                        Point cursorPosition = 
                            treeTable.getDisplay().getCursorLocation();
                        boolean isCursorInBounds = 
                            treeTableBounds.contains(cursorPosition);
                        if (isCursorInBounds) {
                            int horizontalScrollOffset = 0;
                            ScrollBar horizontalBar = 
                                getTree().getHorizontalBar();
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
    // 
    // Methods for Table Trees following
    // 
    
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
    public void rcMove(String direction, int distance, int clickCount) 
        throws StepExecutionException {

        if (getColumnCount() > 0) {
            TreeOperationContext context;
            int mouseColumn = getMouseColumn();
            
            if (mouseColumn == -1) {
                context = 
                    new TableTreeOperationContext(
                        getEventThreadQueuer(), getRobot(), getTree());
            } else {
                context = 
                    new TableTreeOperationContext(
                        getEventThreadQueuer(), getRobot(), getTree(), 
                        mouseColumn);
            }
            
            TreeItem selectedNode = (TreeItem) getSelectedNode(context);
            
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
            super.rcMove(direction, distance, clickCount);
        }
    }

    /**
     * Runs in the GUI thread.
     * @return the number of columns in the receivers component.
     */
    private int getColumnCount() {
        return ((Integer)getEventThreadQueuer().invokeAndWait(
                    "getColumnCount", new IRunnable() { //$NON-NLS-1$

                        public Object run() throws StepExecutionException {
                            return new Integer(getTree().getColumnCount());
                        }
                
                    })).intValue();
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
     * Verifies whether the first selection in the tree has a rendered text at 
     * column <code>column</code> that is equal to <code>pattern</code>.
     * 
     * @param pattern The pattern
     * @param column
     *            The column containing the text to verify
     * @throws StepExecutionException If no node is selected or the verification fails.
     */
    public void rcVerifySelectedValue(String pattern, int column)
        throws StepExecutionException {

        rcVerifySelectedValue(pattern, MatchUtil.DEFAULT_OPERATOR, column);
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

        TableTreeOperationContext context = new TableTreeOperationContext(
            getEventThreadQueuer(), getRobot(), getTree(), implCol);

        String text = context.getNodeTextAtColumn(context.getSelectedNode());
        
        Verifier.match(text, pattern, operator);

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
                        return new Integer(getTree().getColumnCount());
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
                getEventThreadQueuer(), getRobot(), getTree(), column);
        TreeItem startNode = (TreeItem) getStartNode(pathType,
                preAscend, context);

        AbstractTreeNodeTraverser traverser = new PathBasedTraverser(
                context, treePath, new TreeNodeOperationConstraint());
        traverser.traversePath(operation, startNode);
    }
    
    /**
     * {@inheritDoc}
     */
    public String rcStoreValueAtMousePosition(String variable) {
        TreeItem itemAtMousePosition = (TreeItem) getNodeAtMousePosition();
        int column = getMouseColumn();
        AbstractTreeOperationContext context;
        
        if (column != -1) {
            context = 
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(), getTree(), column);
        } else {
            context =
                new TableTreeOperationContext(
                    getEventThreadQueuer(), getRobot(), getTree());
        }

        return context.getRenderedText(itemAtMousePosition);
    }
    
    /**
     * Selects Checkbox of last node of the path given by <code>treepath</code>.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath The tree path.
     * @param operator
     *  If regular expressions are used to match the tree path
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void rcToggleCheckbox(String pathType, int preAscend, String
            treePath, String operator)
        throws StepExecutionException {
        toggleCheckBoxByPath(pathType, preAscend, 
                createStringNodePath(splitTextTreePath(treePath), operator));
    }
    
    /**
     * Selects Checkbox of last node of the path given by <code>indexPath</code>
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param indexPath the index path
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void rcToggleCheckboxByIndices(String pathType, int preAscend, 
                    String indexPath)
        throws StepExecutionException {
    
        toggleCheckBoxByPath(pathType, preAscend,
                createIndexNodePath(splitIndexTreePath(indexPath)));
    }
    
    /**
     * Verify Selection of checkbox of the node at the end of the <code>treepath</code>.
     * 
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param treePath The tree path.
     * @param operator
     *  If regular expressions are used to match the tree path
     * @param checked true if checkbox of tree node is selected, false otherwise
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void rcVerifyCheckbox(String pathType, int preAscend, String
            treePath, String operator, boolean checked)
        throws StepExecutionException {
        verifyCheckBoxByPath(pathType, preAscend, 
                createStringNodePath(splitTextTreePath(treePath), operator), 
                checked);    
    }
    
    /**
     * Verify Selection of checkbox of last node of the path given by <code>indexPath</code>
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param indexPath the index path
     * @param checked true if checkbox of tree node is selected, false otherwise
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void rcVerifyCheckboxByIndices(String pathType, int preAscend, 
                    String indexPath, boolean checked)
        throws StepExecutionException {
    
        verifyCheckBoxByPath(pathType, preAscend,
                createIndexNodePath(splitIndexTreePath(indexPath)), 
                    checked);    
    }
    
    /**
     * Verifies whether the checkbox of the first selection in the tree is checked
     * 
     * @param checked true if checkbox of node is selected, false otherwise
     * @throws StepExecutionException If no node is selected or the verification fails.
     */
    public void rcVerifySelectedCheckbox(boolean checked)
        throws StepExecutionException {        
        Boolean checkSelected = ((Boolean)getEventThreadQueuer().invokeAndWait(
                "gdVerifyTreeCheckbox", new IRunnable() { //$NON-NLS-1$
                    public Object run() {             
                        TreeItem node = getTree().getSelection()[0];
                        return new Boolean(node.getChecked());
                    }            
                }));       
        
        Verifier.equals(checked, checkSelected.booleanValue());
    }

    /**
     * @param pathType pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param objectPath objectPath
     * @param checked true if Checkbox should be enabled, false otherwise
     */
    private void verifyCheckBoxByPath(String pathType, int preAscend, 
            INodePath objectPath, final boolean checked) {

        TreeNodeOperation expOp = 
            new ExpandCollapseTreeNodeOperation(false);
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), getTree());
        TreeNodeOperation checkboxOp = new VerifyCheckboxOperation(
                checked, context);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);
        
        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(objectPath, pathType, preAscend, checkboxOp);
    }
    
    /**
     * @param pathType pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param objectPath objectPath
     */
    private void toggleCheckBoxByPath(String pathType, int preAscend, 
            INodePath objectPath) {

        TreeNodeOperation expOp = 
            new ExpandCollapseTreeNodeOperation(false);
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), getTree());
        TreeNodeOperation selCheckboxOp = new ToggleCheckboxOperation(context);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);
        
        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(objectPath, pathType, preAscend,
                selCheckboxOp);      
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcClick(int count, int button) {
        super.rcClick(count, button);
        
    }
    
    /**
     * Forces all outstanding paint requests for the receiver's component's 
     * display to be processed before this method returns.
     * 
     * @see Display#update()
     */
    protected void waitForDisplayUpdate() {
        ((Control)getComponent().getRealComponent())
        .getDisplay().syncExec(new Runnable() {
            public void run() {
                ((Control)getComponent()
                        .getRealComponent()).getDisplay().update();
            }
        });
    }
    
    /**
     * Presses or releases the given modifier.
     * @param modifier the modifier.
     * @param press if true, the modifier will be pressed.
     * if false, the modifier will be released.
     */
    public void pressOrReleaseModifiers(String modifier, boolean press) {
        final IRobot robot = getRobot();
        final StringTokenizer modTok = new StringTokenizer(
                KeyStrokeUtil.getModifierString(modifier), " "); //$NON-NLS-1$
        while (modTok.hasMoreTokens()) {
            final String mod = modTok.nextToken();
            final int keyCode = KeyCodeConverter.getKeyCode(mod);
            if (press) {
                robot.keyPress(null, keyCode);
            } else {
                robot.keyRelease(null, keyCode);
            }
        }
    }

}
