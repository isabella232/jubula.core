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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.BoundsTreeNodeOperation;
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
import org.eclipse.jubula.rc.swing.swing.interfaces.IJTreeImplClass;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.StringParsing;


/**
 * This class implements actions on the Swing JTree.
 *
 * @author BREDEX GmbH
 * @created 15.03.2005
 */
public class JTreeImplClass extends AbstractSwingImplClass 
    implements IJTreeImplClass {
    /** Constant for no pre-ascend */
    public static final int NO_PRE_ASCEND = 0;

    /**
     * The JTree on which the actions are performed.
     */
    private JTree m_tree;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_tree = (JTree)graphicsComponent;
    }
    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_tree;
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
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     *
     * @param treePath
     *            The tree path
     * @return An array of indices (type <code>Integer</code>) representing
     *         the tree path
     * @throws StepExecutionException
     *             If the values of the passed path cannot be parsed
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
     * @return An array of string representing the tree path
     */
    private INodePath createIndexNodePath(Integer [] treePath) {
        return new IndexNodePath(treePath);
    }

    /**
     * Traverses the tree by searching for the nodes in the tree
     * path entry and calling the given operation on each matching node.
     *
     * @param treePath
     *            The tree path.
     * @param pathType
     *            For example, "relative" or "absolute".
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param operation
     *            The tree node operation.
     * @throws StepExecutionException
     *             If the path traversion fails.
     */
    private void traverseTreeByPath(INodePath treePath, String pathType,
            int preAscend, TreeNodeOperation operation)
        throws StepExecutionException {

        Validate.notNull(treePath);
        Validate.notNull(operation);

        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_tree);


        TreeNode startNode = getStartNode(pathType, preAscend, context);

        AbstractTreeNodeTraverser traverser =
            new PathBasedTraverser(context, treePath);

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
    private TreeNode getStartNode(String pathType, int preAscend,
            TreeOperationContext context) {
        TreeNode startNode;

        if (pathType.equals(
                CompSystemConstants.TREE_PATH_TYPE_RELATIVE)) {
            startNode = getSelectedNode(context);
            TreeNode child = startNode;
            for (int i = 0; i < preAscend; ++i) {
                if ((startNode == null) || (!m_tree.isRootVisible()
                        && (m_tree.getModel().getRoot() == startNode))) {
                    TestErrorEvent event = EventFactory
                        .createActionError(TestErrorEvent.TREE_NODE_NOT_FOUND);
                    throw new StepExecutionException(
                        "Tree node not found: Parent of " //$NON-NLS-1$
                        + child.toString(), event);
                }
                child = startNode;
                startNode = startNode.getParent();
            }
            // Extra handling for tree without visible root node
            if ((!m_tree.isRootVisible()
                    && (m_tree.getModel().getRoot() == startNode))) {
                startNode = null;
            }
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
     * Traverses the tree by searching for the nodes in the tree
     * path entry and calling the given operation on the last element in the
     * path.
     *
     * @param treePath
     *            The tree path.
     * @param pathType
     *            For example, "relative" or "absolute".
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param operation
     *            The tree node operation.
     * @throws StepExecutionException
     *             If the path traversion fails.
     */
    private void traverseLastElementByPath(INodePath treePath,
            String pathType, int preAscend, TreeNodeOperation operation)
        throws StepExecutionException {

        Validate.notNull(treePath);
        Validate.notNull(operation);

        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_tree);

        TreeNode startNode = getStartNode(pathType, preAscend, context);

        AbstractTreeNodeTraverser traverser =
            new PathBasedTraverser(context, treePath,
                    new TreeNodeOperationConstraint());

        traverser.traversePath(operation, startNode);

    }

    /**
     * <p>
     * Expands the JTree. Any node defined by the passed tree path is expanded,
     * if it is collapsed. The node is expanded by performing a double click
     * onto the node. If the node is already expanded, the JTree is left
     * unchanged. The tree path is a slash-seperated list of nodes that specifies
     * a valid top-down path in the JTree.
     * </p>
     *
     * An example: Say the passed tree path is <code>animals/birds/kakadu</code>.
     * To get a valid expansion, the JTree has to look as follows:
     *
     * <pre>
     * animals
     * |
     * - -- birds
     *      |
     *      - -- kakadu
     * </pre>
     *
     * <code>animals</code> is the JTree's root node, if the root node has
     * been set to visible by calling <code>JTree#setRootVisible(true)</code>,
     * or it is one of the root node's children, if the root node has been set
     * to invisible by calling <code>JTree#setRootVisible(false)</code>.
     *
     * <p>
     * It is important to know that the tree path entries have to match the
     * rendered node texts, but not the underlying user object data etc.
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
    public void gdExpand(String pathType, int preAscend,
        String treePath, String operator) throws StepExecutionException {
        traverseTreeByPath(
                createStringNodePath(splitTextTreePath(treePath), operator),
                pathType, preAscend,
                new ExpandCollapseTreeNodeOperation(false));
    }

    /**
     * Expands the tree. This method works like {@link #gdExpand(String, int, String, String)}, but
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

        try {
            traverseTreeByPath(
                    createIndexNodePath(splitIndexTreePath(indexPath)),
                    pathType, preAscend,
                    new ExpandCollapseTreeNodeOperation(false));
        } catch (NumberFormatException e) {
            throw new StepExecutionException(e.getMessage(), EventFactory
                    .createActionError(TestErrorEvent.INVALID_INDEX));
        }
    }

    /**
     * Collapses the JTree. The passed tree path is a slash-seperated list of
     * nodes that specifies a valid top-down path in the JTree. The last node of
     * the tree path is collapsed if it is currently expanded. Otherwise, the JTree is
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
    public void gdCollapse(String pathType, int preAscend,
        String treePath, String operator) throws StepExecutionException {
        traverseLastElementByPath(
                createStringNodePath(splitTextTreePath(treePath), operator),
                pathType, preAscend,
                new ExpandCollapseTreeNodeOperation(true));
    }

    /**
     * Collapses the tree. This method works like {@link #gdCollapse(String, int, String, String)},
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

        try {
            traverseLastElementByPath(
                    createIndexNodePath(splitIndexTreePath(indexPath)),
                    pathType, preAscend,
                    new ExpandCollapseTreeNodeOperation(true));
        } catch (NumberFormatException e) {
            throw new StepExecutionException(e.getMessage(), EventFactory
                    .createActionError(TestErrorEvent.INVALID_INDEX));
        }
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

        TreeOperationContext context =
            new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_tree);

        TreeNode selectedNode = getSelectedNode(context);

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

    }

    /**
     * Selects the node at the end of the <code>treepath</code>.
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
     * @param button what mouse button should be used
     * @param extendSelection Whether this selection extends a previous 
     *                        selection.
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void gdSelect(String pathType, int preAscend, String treePath,
            String operator, int clickCount, int button, 
            final String extendSelection)
        throws StepExecutionException {
        selectByPath(pathType, preAscend,
                createStringNodePath(splitTextTreePath(treePath), operator),
                ClickOptions.create()
                    .setClickCount(clickCount)
                    .setMouseButton(button)
                    .setClickModifier(getClickModifier(extendSelection)));
    }

    /**
     * Selects the last node of the path given by <code>indexPath</code>
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param indexPath the index path
     * @param clickCount the number of times to click
     * @param button what mouse button should be used
     * @param extendSelection Whether this selection extends a previous 
     *                        selection.
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void gdSelectByIndices(String pathType, int preAscend,
            String indexPath, int clickCount, int button,
            final String extendSelection) 
        throws StepExecutionException {
        
        selectByPath(pathType, preAscend,
                createIndexNodePath(splitIndexTreePath(indexPath)),
                ClickOptions.create()
                .setClickCount(clickCount)
                .setMouseButton(button)
                .setClickModifier(getClickModifier(extendSelection)));
    }

    /**
     * @param pathType pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param objectPath objectPath
     * @param co the click options to use
     */
    private void selectByPath(String pathType, int preAscend,
        INodePath objectPath, ClickOptions co) {

        TreeNodeOperation expOp =
            new ExpandCollapseTreeNodeOperation(false);
        TreeNodeOperation selectOp = new SelectTreeNodeOperation(co);
        INodePath subPath = objectPath.subPath(
                0, objectPath.getLength() - 1);

        // Expand all elements in the path except the last element
        traverseTreeByPath(subPath, pathType, preAscend, expOp);

        traverseLastElementByPath(objectPath, pathType, preAscend, selectOp);

    }

    /**
     * Verifies whether the first selection in the tree has a rendered text that is
     * equal to <code>selection</code>.
     *
     * @param selection
     *            The selection to verify
     * @throws StepExecutionException
     *             If no node is selected or the verification fails.
     */
    public void gdVerifySelectedValue(String selection)
        throws StepExecutionException {
        gdVerifySelectedValue(selection, MatchUtil.DEFAULT_OPERATOR);
    }

    /**
     * Verifies whether the first selection in the tree has a rendered text that is
     * equal to <code>pattern</code>.
     *
     * @param pattern
     *            The expected text
     * @param operator
     *            The operator to use when comparing the expected and
     *            actual values.
     * @throws StepExecutionException
     *             If no node is selected or the verification fails.
     */
    public void gdVerifySelectedValue(String pattern, String operator)
        throws StepExecutionException {

        TreeOperationContext context = new TreeOperationContext(
            getEventThreadQueuer(), getRobot(), m_tree);
        checkNodeText(getSelectedNode(context), pattern, operator);
    }

    /**
     * Checks the text for the given node against the given pattern and 
     * operator.
     * 
     * @param node 
     *          The node containing the text to check.
     * @param pattern 
     *          The expected text.
     * @param operator 
     *          The operator to use when comparing the expected and
     *          actual values.
     * @throws StepVerifyFailedException If the verification fails.
     */
    private void checkNodeText(Object node, String pattern, String operator) 
        throws StepVerifyFailedException {
        
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_tree);
        Collection nodeTextList = context.getNodeTextList(node);
        Iterator it = nodeTextList.iterator();
        boolean isMatched = false;
        while (it.hasNext() && !isMatched) {
            try {
                Verifier.match((String)it.next(), pattern, operator);
                isMatched = true;
            } catch (StepVerifyFailedException svfe) {
                if (!it.hasNext()) {
                    throw svfe;
                }
                // Otherwise just try the next element
            }
        }
    }
    
    /**
     * 
     * @return the tree node at the current mouse position.
     * @throws StepExecutionException If no tree node can be found at the 
     *                                current mouse position.
     */
    private Object getNodeAtMousePosition() throws StepExecutionException {
        return getEventThreadQueuer().invokeAndWait("getNodeAtMousePosition", new IRunnable() { //$NON-NLS-1$
            
            public Object run() throws StepExecutionException {
                Point mousePosition = getRobot().getCurrentMousePosition();
                Point treeLocation = m_tree.getLocationOnScreen();
                Point relativePos = new Point(
                        mousePosition.x - treeLocation.x,
                        mousePosition.y - treeLocation.y);

                int rowAtMousePosition = 
                    m_tree.getRowForLocation(relativePos.x, relativePos.y);
                
                if (rowAtMousePosition != -1) {
                    TreePath treePath = 
                        m_tree.getPathForLocation(relativePos.x, relativePos.y);
                    
                    if (treePath != null 
                            && treePath.getLastPathComponent() != null) {
                        return treePath.getLastPathComponent();
                    }
                    
                }
                
                throw new StepExecutionException("No tree node found at mouse position.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
            }
            
        });

    }
    
    /**
     * Returns the selected node
     * @param context context
     * @return node
     */
    private TreeNode getSelectedNode(TreeOperationContext context) {
        return (TreeNode)context.getSelectedNode();
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
    public void gdVerifyPath(String pathType, int preAscend,
            String treePath, String operator, boolean exists) {
        try {
            gdExpand(pathType, preAscend, treePath, operator);
        } catch (StepExecutionException e) {
            if (exists) {
                throw new StepVerifyFailedException(
                        "Verify failed on tree-path: " //$NON-NLS-1$
                                + treePath, EventFactory.createVerifyFailed(
                                        treePath, StringConstants.EMPTY));
            }
            return;
        }
        if (!exists) {
            throw new StepVerifyFailedException("Verify failed on tree-path: ",  //$NON-NLS-1$
                EventFactory.createVerifyFailed(
                        StringConstants.EMPTY, treePath));
        }
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
        try {
            gdExpandByIndices(pathType, preAscend, treePath);
        } catch (StepExecutionException e) {
            if (exists) {
                throw new StepVerifyFailedException("Verify failed on tree-path: " //$NON-NLS-1$
                    + treePath,
                    EventFactory.createVerifyFailed(treePath, 
                            StringConstants.EMPTY));
            }
            return;
        }
        if (!exists) {
            throw new StepVerifyFailedException("Verify failed on tree-path: ",  //$NON-NLS-1$
                EventFactory.createVerifyFailed(
                        StringConstants.EMPTY, treePath));
        }
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

        if (getRobot().isMouseInComponent(m_tree)) {
            getRobot().clickAtCurrentPosition(m_tree, count, button);
        } else {
            getRobot().click(m_tree, null, 
                ClickOptions.create().setClickCount(count)
                    .setMouseButton(button));
        }
       
    }
    /**
     * Shows a popup at the selected node and selects the item given
     * by indexPath
     * @param indexPath the index path
     * @deprecated will be removed!
     */
    public void gdPopupByIndexPathAtSelectedNode(String indexPath) {
        Rectangle rect = getSelectedNodeBounds();
        gdPopupSelectByIndexPath((int)rect.getCenterX(),
                (int)rect.getCenterY(), POS_UNIT_PIXEL, indexPath);
    }
    /**
     * Shows a popup at the selected node and selects the item given
     * by indexPath
     * @param textPath the path
     * @deprecated will be removed!
     */
    public void gdPopupByTextPathAtSelectedNode(String textPath) {
        Rectangle rect = getSelectedNodeBounds();
        gdPopupSelectByIndexPath((int)rect.getCenterX(),
                (int)rect.getCenterY(), POS_UNIT_PIXEL, textPath);
    }
    /**
     * Gets the rectangular bounds of the selected node
     * @return
     *      the bounds of the selected node
     */
    private Rectangle getSelectedNodeBounds() {
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_tree);
        TreeNode node = getSelectedNode(context);
        return context.getNodeBounds(node);
    }
    /**
     * Gets the bounds of a node specified at a path
     *
     * @deprecated Use {@link #getBounds(INodePath,String,String)} instead
     *
     * @param path
     *      path of the node
     * @return
     *      the bounds
     */
    private Rectangle getBounds(INodePath path) {
        return getBounds(path, CompSystemConstants.TREE_PATH_TYPE_ABSOLUTE,
                NO_PRE_ASCEND);
    }
    /**
     * Gets the bounds of a node specified at a path
     *
     * @param path
     *      path of the node
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @return
     *      the bounds
     */
    private Rectangle getBounds(INodePath path, String pathType,
            int preAscend) {
        BoundsTreeNodeOperation op = new BoundsTreeNodeOperation();
        traverseLastElementByPath(path, pathType, preAscend, op);
        if (op.getBounds() == null) {
            throw new StepExecutionException("node not found", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.NOT_FOUND));
        }
        return op.getBounds();
    }
    /**
     * Shows a popup menu at the specified node and selects an item.
     * The node and menuitem are given by an indexpath
     *
     * @param treeIndexPath path to the node
     * @param popupIndexPath path to the menu item
     * @deprecated will be removed!
     */
    public void gdPopupByIndexPathAtIndexNode(String treeIndexPath,
            String popupIndexPath) {

        Rectangle bounds = getBounds(
                createIndexNodePath(splitIndexTreePath(treeIndexPath)));
        gdPopupSelectByIndexPath(
                (int)bounds.getCenterX(),
                (int)bounds.getCenterY(),
                POS_UNIT_PIXEL, popupIndexPath);
    }
    /**
     * Shows a popup menu at the specified node and selects an item.
     * The node is given by an indexpath and the menuitem by a textpath
     *
     * @param treeIndexPath path to the node
     * @param popupTextPath path to the menu item
     * @deprecated will be removed!
     */
    public void gdPopupByTextPathAtIndexNode(String treeIndexPath,
            String popupTextPath) {

        Rectangle bounds = getBounds(
                createIndexNodePath(splitIndexTreePath(treeIndexPath)));
        gdPopupSelectByTextPath(
                (int)bounds.getCenterX(),
                (int)bounds.getCenterY(),
                POS_UNIT_PIXEL, popupTextPath, MatchUtil.EQUALS);
    }
    /**
     * Shows a popup menu at the specified node and selects an item.
     * The node is given by a treepath and the menuitem by an indexpath
     *
     * @param treeTextPath path to the node
     * @param operator if the path uses regular expressions
     * @param popupIndexPath path to the menu item
     * @deprecated will be removed!
     */
    public void gdPopupByIndexPathAtTextNode(String treeTextPath,
            String operator, String popupIndexPath) {

        Rectangle bounds = getBounds(
                createStringNodePath(
                        splitTextTreePath(treeTextPath), operator));
        gdPopupSelectByIndexPath(
                (int)bounds.getCenterX(),
                (int)bounds.getCenterY(),
                POS_UNIT_PIXEL, popupIndexPath);
    }
    /**
     * Shows a popup menu at the specified node and selects an item.
     * The node is given by a treepath and the menuitem by a treepath
     *
     * @param treeTextPath path to the node
     * @param operator if the path uses regular expressions
     * @param popupIndexPath path to the menu item
     * @deprecated will be removed!
     */
    public void gdPopupByTextPathAtTextNode(String treeTextPath,
            String operator, String popupIndexPath) {
        Rectangle bounds = getBounds(
                createStringNodePath(
                        splitTextTreePath(treeTextPath), operator));
        gdPopupSelectByTextPath(
                (int)bounds.getCenterX(),
                (int)bounds.getCenterY(),
                POS_UNIT_PIXEL, popupIndexPath, MatchUtil.EQUALS);
    }

    /**
     * Drags the node of the given treePath.
     * @param mouseButton the mouse button to press.
     * @param modifier the modifier to press.
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *      Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param treePath The tree path.
     * @param operator If regular expressions are used to match the tree path
     */
    public void gdDragByTextPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treePath, String operator) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        gdSelect(pathType, preAscend, treePath, operator, 0, 1, 
                CompSystemConstants.EXTEND_SELECTION_NO);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }


    /**
     * Drops the before dragged object on the given treePath.
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *      Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param treePath The tree path.
     * @param operator If regular expressions are used to match the tree path
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void gdDropByTextPath(String pathType, int preAscend,
            String treePath, String operator, int delayBeforeDrop) {

        try {
            gdSelect(pathType, preAscend, treePath, operator, 0, 1,
                    CompSystemConstants.EXTEND_SELECTION_NO);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }

    /**
     * Drags the node of the given indexPath.
     * @param mouseButton the mouse button to press.
     * @param modifier the modifier to press.
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *      Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param indexPath The index path.
     */
    public void gdDragByIndexPath(int mouseButton, String modifier,
            String pathType, int preAscend, String indexPath) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        gdSelectByIndices(pathType, preAscend, indexPath, 0, 1,
                CompSystemConstants.EXTEND_SELECTION_NO);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }

    /**
     * Drops the before dragged object on the given indexPath.
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *      Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param indexPath The index path.
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void gdDropByIndexPath(String pathType, int preAscend,
            String indexPath, int delayBeforeDrop) {

        try {
            gdSelectByIndices(pathType, preAscend, indexPath, 0, 1,
                    CompSystemConstants.EXTEND_SELECTION_NO);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return always null
     */
    protected String getText() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void gdVerifyTextAtMousePosition(String pattern, String operator) {
        checkNodeText(getNodeAtMousePosition(), pattern, operator);
    }
    
    /**
     * {@inheritDoc}
     */
    public String gdStoreSelectedNodeValue(String variable) {
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_tree);
        TreeNode selectedNode = 
            getSelectedNode(context);
        if (selectedNode == null) {
            throw new StepExecutionException("No tree item selected", //$NON-NLS-1$ 
                EventFactory.createActionError(TestErrorEvent.NO_SELECTION));
        }
        
        return context.getRenderedText(selectedNode);
    }
    /**
     * {@inheritDoc}
     */
    public String gdStoreValueAtMousePosition(String variable) {
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), m_tree);
        
        return context.getRenderedText(getNodeAtMousePosition());
    }
}
