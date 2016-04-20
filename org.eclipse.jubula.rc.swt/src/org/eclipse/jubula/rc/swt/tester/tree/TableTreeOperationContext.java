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
package org.eclipse.jubula.rc.swt.tester.tree;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.swt.tester.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtPointUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;



/**
 * @author BREDEX GmbH
 * @created 25.04.2007
 */
public class TableTreeOperationContext extends TreeOperationContext {

    /** the target column for this operation */
    private int m_column;
    
    /**
     * @param queuer queuer
     * @param robot robot
     * @param tree tree
     * @param column column
     */
    public TableTreeOperationContext(
        IEventThreadQueuer queuer, IRobot robot, Tree tree, int column) {
        
        super(queuer, robot, tree);
        m_column = column;
    }

    /**
     * @param queuer queuer
     * @param robot robot
     * @param tree tree
     */
    public TableTreeOperationContext(
        IEventThreadQueuer queuer, IRobot robot, Tree tree) {
        
        this(queuer, robot, tree, 0);
    }

    /**
     * {@inheritDoc}
     */
    public void clickNode(final TreeItem node, ClickOptions clickOps) {
        scrollNodeToVisible(node);
        org.eclipse.swt.graphics.Rectangle visibleItemBounds = 
            getQueuer().invokeAndWait(
                "getVisibleNodeBounds " + node, new IRunnable<org.eclipse.swt.graphics.Rectangle>() { //$NON-NLS-1$
                    public org.eclipse.swt.graphics.Rectangle run() {
                        final Rectangle nodeBounds = 
                            SwtPointUtil.toAwtRectangle(
                                SwtUtils.getRelativeBounds(node, m_column));
                        return SwtPointUtil.toSwtRectangle(
                                getVisibleRowBounds(nodeBounds));
                    }
                });
        
        getRobot().click(getTree(), visibleItemBounds,  
            clickOps.setScrollToVisible(false));
    }

    /**
     * @param node the node
     * @return The text rendered at this 
     *  <code>TableTreeOperationContext</code>'s column for the given node.
     */
    public String getNodeTextAtColumn(final TreeItem node) {
        final int userIndex = IndexConverter.toUserIndex(m_column);
        return getQueuer().invokeAndWait(
                "getNodeText: " + node + " at column: " + userIndex, //$NON-NLS-1$ //$NON-NLS-2$
                new IRunnable<String>() {
                    public String run() {
                        return CAPUtil.getWidgetText(node,
                                SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                        + m_column, node.getText(m_column));
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String getRenderedText(TreeItem node) throws StepExecutionException {
        return getNodeTextAtColumn(node);
    }
    
    /**
     * {@inheritDoc}
     */
    public void scrollNodeToVisible(final TreeItem node) {
        super.scrollNodeToVisible(node);

        getQueuer().invokeAndWait("showColumn: " + node, //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() {
                        getTree().showColumn(getTree().getColumn(m_column));
                        return null;
                    }
                });

        final Rectangle nodeBoundsRelativeToParent = getNodeBounds(node);
        final Tree tree = getTree();
        
        getQueuer().invokeAndWait("getNodeBoundsRelativeToParent", //$NON-NLS-1$
            new IRunnable<Void>() {
                public Void run() {
                    org.eclipse.swt.graphics.Point cellOriginRelativeToParent = 
                        tree.getDisplay().map(
                                tree, tree.getParent(), 
                                new org.eclipse.swt.graphics.Point(
                                        nodeBoundsRelativeToParent.x, 
                                        nodeBoundsRelativeToParent.y));
                    nodeBoundsRelativeToParent.x = 
                        cellOriginRelativeToParent.x;
                    nodeBoundsRelativeToParent.y = 
                        cellOriginRelativeToParent.y;
                    return null;
                }
            });

        Control parent = getQueuer().invokeAndWait("getParent", //$NON-NLS-1$
                new IRunnable<Control>() {
                    public Control run() {
                        return tree.getParent();
                    }
                });
        
        getRobot().scrollToVisible(parent, 
                SwtPointUtil.toSwtRectangle(nodeBoundsRelativeToParent));
    }
    
    /**
     * gets the column index based on the given string
     * @param col the column
     * @param operator the operator
     * @return the index or -2 if no column was found
     */
    public int getColumnFromString(final String col, final String operator) {
        int column = -2;
        try {
            int usrIdxCol = Integer.parseInt(col);
            if (usrIdxCol == 0) {
                usrIdxCol = usrIdxCol + 1;
            }
            column = IndexConverter.toImplementationIndex(usrIdxCol);
        } catch (NumberFormatException nfe) {
            try {
                Boolean isVisible = getQueuer().invokeAndWait(
                        "getColumnFromString", //$NON-NLS-1$
                        new IRunnable<Boolean>() {
                            public Boolean run() {
                                return getTree().getHeaderVisible();
                            }
                        });
                if (!(isVisible.booleanValue())) {
                    throw new StepExecutionException("No Header", //$NON-NLS-1$
                            EventFactory.createActionError(
                                    TestErrorEvent.NO_HEADER));
                }

                Integer implCol = getQueuer().invokeAndWait(
                        "getColumnFromString", new IRunnable<Integer>() { //$NON-NLS-1$
                            public Integer run() throws StepExecutionException {
                                for (int i = 0; i < getTree()
                                        .getColumnCount(); i++) {
                                    String colHeader = getColumnHeaderText(i);
                                    if (MatchUtil.getInstance().match(colHeader,
                                            col, operator)) {
                                        return i;
                                    }
                                }
                                return -2;
                            }
                        });
                column = implCol.intValue();
            } catch (IllegalArgumentException iae) {
                // do nothing here
            }
        }
        return column;
    }
    
    /**
     * get the text in the header of a column
     * @param colIdx the column index
     * @return the header text
     */
    public String getColumnHeaderText(final int colIdx) {
        return getQueuer().invokeAndWait("getColumnName", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        final TreeColumn column = getTree().getColumn(colIdx);
                        return CAPUtil.getWidgetText(column, column.getText());
                    }
                });
    }
    
    /**
     * get the bounds of the header of a column
     * @param col the column index
     * @return the header bounds
     */
    public Rectangle getHeaderBounds(final int col) {
        return getQueuer().invokeAndWait("getHeaderBounds", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        Tree tree = getTree();
                        org.eclipse.swt.graphics.Rectangle rect = tree
                                .getItem(0).getBounds(col);
                        rect.y = tree.getClientArea().y;
                        return new Rectangle(rect.x, rect.y, rect.width,
                                rect.height);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getNodeBounds(final TreeItem node) {
        org.eclipse.swt.graphics.Rectangle r = 
            getQueuer().invokeAndWait("getNodeBounds: " + node,  //$NON-NLS-1$
                    new IRunnable<org.eclipse.swt.graphics.Rectangle>() {
                    public org.eclipse.swt.graphics.Rectangle run() {
                        Tree tree = getTree();
                        org.eclipse.swt.graphics.Rectangle bounds = 
                            SwtUtils.getBounds(node, m_column);
                        Point relativeLocation = 
                            tree.toControl(bounds.x, bounds.y);
                        bounds.x = relativeLocation.x;
                        bounds.y = relativeLocation.y;
                        
                        return bounds;
                    }
                });
        Rectangle nodeBounds = new Rectangle(
            r.x, r.y, r.width, r.height);

        return nodeBounds;
    }
}
