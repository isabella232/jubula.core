/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.util.StringTokenizer;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.StandardDepthFirstTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.tester.AbstractTreeTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeComponent;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.tester.util.ToggleCheckboxOperation;
import org.eclipse.jubula.rc.swt.tester.util.TreeOperationContext;
import org.eclipse.jubula.rc.swt.tester.util.VerifyCheckboxOperation;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Toolkit specific commands for the <code>Tree</code>
 *
 * @author BREDEX GmbH
 */
public class TreeTester extends AbstractTreeTableTester {

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
     * 
     * @return the Tree
     */
    private Tree getTreeTable() {
        return (Tree) getComponent().getRealComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rcDragByTextPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treeTextPath,
            String operator) {
        postMouseMovementEvent();
        super.rcDragByTextPath(mouseButton, modifier, pathType, preAscend,
                treeTextPath, operator);
        postMouseMovementEvent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rcDropByTextPath(final String pathType, final int preAscend,
            final String treePath, final String operator, int delayBeforeDrop) {
        postMouseMovementEvent();
        super.rcDropByTextPath(pathType, preAscend, treePath, operator,
                delayBeforeDrop);
        postMouseMovementEvent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rcDragByIndexPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treeIndexPath) {
        postMouseMovementEvent();
        super.rcDragByIndexPath(mouseButton, modifier, pathType, preAscend,
                treeIndexPath);
        postMouseMovementEvent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rcDropByIndexPath(final String pathType, final int preAscend,
            final String indexPath, int delayBeforeDrop) {
        postMouseMovementEvent();
        super.rcDropByIndexPath(pathType, preAscend, indexPath,
                delayBeforeDrop);
        postMouseMovementEvent();
    }

    /**
     * Post a MouseMove event in order to break the Display out of its post-drag
     * "freeze". It appears as though the mouse position change needs to be
     * extreme in order to nudge the Display back into action (i.e.
     * (<mouse-location> + 1) was insufficient), so the default Event values (x,
     * y = 0) are used.
     */
    private void postMouseMovementEvent() {
        Event wakeEvent = new Event();
        wakeEvent.type = SWT.MouseMove;
        getTreeTable().getDisplay().post(wakeEvent);
        waitForDisplayUpdate();
        SwtUtils.waitForDisplayIdle(getTreeTable().getDisplay());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        return getEventThreadQueuer().invokeAndWait("getItemAtMousePosition", new IRunnable<TreeItem>() { //$NON-NLS-1$
            
            public TreeItem run() throws StepExecutionException {
                Point mousePos = SwtUtils.convertToSwtPoint(
                        getRobot().getCurrentMousePosition());
                ItemAtPointTreeNodeOperation op = 
                    new ItemAtPointTreeNodeOperation(
                            mousePos, SwtUtils.getWidgetBounds(getTreeTable()));

                TreeItem topItem = getTreeTable().getTopItem();
                if (topItem != null) {
                    
                    // FIXME zeb This may be slow for very large trees, as the 
                    //           search may continue long past the
                    //           visible client area of the tree.
                    //           It may also cause problems with regard to 
                    //           lazy/virtual nodes. 
                    StandardDepthFirstTraverser traverser = 
                        new StandardDepthFirstTraverser(
                            new TreeOperationContext(
                                getEventThreadQueuer(),
                                getRobot(),
                                getTreeTable()));
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
     * @param etq
     *            the EventThreadQueuer to use
     * @param table
     *            the table to use
     * @param row
     *            The row of the cell
     * @param col
     *            The column of the cell
     * @param ti
     *            The tree item
     * @return The bounding rectangle for the cell, relative to the table's
     *         location.
     */
    private static Rectangle getCellBounds(IEventThreadQueuer etq,
        final Tree table, final int row, final int col, final TreeItem ti) {
        Rectangle cellBounds = etq.invokeAndWait(
                "getCellBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() {
                        int column = (table.getColumnCount() > 0 || col > 0) 
                            ? col : 0;
                        org.eclipse.swt.graphics.Rectangle r = 
                                ti.getBounds(column);
                        String text = CAPUtil.getWidgetText(ti,
                                SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                        + column, ti.getText(column));
                        Image image = ti.getImage(column);
                        if (text != null && text.length() != 0) {
                            GC gc = new GC(table);
                            int charWidth = 0; 
                            try {
                                FontMetrics fm = gc.getFontMetrics();
                                charWidth = fm.getAverageCharWidth();
                            } finally {
                                gc.dispose();
                            }
                            r.width = text.length() * charWidth;
                            if (image != null) {
                                r.width += image.getBounds().width;
                            }
                        } else if (image != null) {
                            r.width = image.getBounds().width;
                        }
                        if (column > 0) {
                            TreeColumn tc = table.getColumn(column);
                            int alignment = tc.getAlignment();
                            if (alignment == SWT.CENTER) {
                                r.x += ((double)tc.getWidth() / 2) 
                                        - ((double)r.width / 2);
                            }
                            if (alignment == SWT.RIGHT) {
                                r.x += tc.getWidth() - r.width;
                            }
                        }
                        
                        return new Rectangle(r.x, r.y, r.width, r.height);
                    }
                });
        return cellBounds;
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
     * @param timeout the maximum amount of time to wait for the state to occur
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void rcVerifyCheckbox(final String pathType, final int preAscend,
            final String treePath, final String operator, final boolean checked,
            int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifyCheckBox", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                verifyCheckBoxByPath(pathType, preAscend, 
                        createStringNodePath(
                                splitTextTreePath(treePath), operator), 
                        checked);    
            }
        });
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
     * @param timeout the maximum amount of time to wait for the state to occur
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void rcVerifyCheckboxByIndices(final String pathType,
            final int preAscend, final String indexPath, final boolean checked,
            int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifyChecktboxByIndices", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                verifyCheckBoxByPath(pathType, preAscend,
                        createIndexNodePath(splitIndexTreePath(indexPath)), 
                        checked);    
            }
        });
    }
    
    /**
     * Verifies whether the checkbox of the first selection in the tree is checked
     * 
     * @param checked true if checkbox of node is selected, false otherwise
     * @param timeout the maximum amount of time to wait for the state to occur
     * @throws StepExecutionException If no node is selected or the verification fails.
     */
    public void rcVerifySelectedCheckbox(final boolean checked, int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifySelectedCheckbox", timeout, new Runnable() { //$NON-NLS-1$
            
            public void run() {
                Boolean checkSelected = getEventThreadQueuer().invokeAndWait(
                        "rcVerifyTreeCheckbox", new IRunnable<Boolean>() { //$NON-NLS-1$
                            public Boolean run() {
                                AbstractTreeOperationContext context = 
                                        ((ITreeComponent)getComponent())
                                        .getContext();
                                TreeItem node = 
                                        (TreeItem) getSelectedNode(context);
                                return node.getChecked();
                            }            
                        });       
                Verifier.equals(checked, checkSelected.booleanValue());
                
            }
        });
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
                getEventThreadQueuer(), getRobot(), getTreeTable());
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
                getEventThreadQueuer(), getRobot(), getTreeTable());
        TreeNodeOperation selCheckboxOp = new ToggleCheckboxOperation(context);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);
        
        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(objectPath, pathType, preAscend,
                selCheckboxOp);      
    }
    
    /**
     * Forces all outstanding paint requests for the receiver's component's 
     * display to be processed before this method returns.
     * 
     * @see Display#update()
     */
    private void waitForDisplayUpdate() {
        ((Control)getComponent().getRealComponent())
            .getDisplay().syncExec(new Runnable() {
                    public void run() {
                        ((Control) getComponent().getRealComponent())
                                .getDisplay().update();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void pressOrReleaseModifiers(String modifier, boolean press) {
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
