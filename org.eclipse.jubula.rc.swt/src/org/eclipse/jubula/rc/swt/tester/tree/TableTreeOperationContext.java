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
import org.eclipse.jubula.rc.swt.tester.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtPointUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
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
            (org.eclipse.swt.graphics.Rectangle)getQueuer().invokeAndWait(
                "getVisibleNodeBounds " + node, new IRunnable() { //$NON-NLS-1$

                    public Object run() {
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
        String nodeText = (String)getQueuer().invokeAndWait(
            "getNodeText: " + node + " at column: " + userIndex,  //$NON-NLS-1$ //$NON-NLS-2$
            new IRunnable() {

                public Object run() {
                    return CAPUtil.getWidgetText(node,
                            SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                            + m_column, node.getText(m_column));

                }
            });

        return nodeText;
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
                new IRunnable() {

                    public Object run() {
                        getTree().showColumn(getTree().getColumn(m_column));
                        return null;
                    }

                });

        final Rectangle nodeBoundsRelativeToParent = getNodeBounds(node);
        final Tree tree = getTree();
        
        getQueuer().invokeAndWait("getNodeBoundsRelativeToParent", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
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

        Control parent = (Control) getQueuer().invokeAndWait("getParent", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        tree.getParent();
                        return null;
                    }
                });
        
        getRobot().scrollToVisible(parent, 
                SwtPointUtil.toSwtRectangle(nodeBoundsRelativeToParent));
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getNodeBounds(final TreeItem node) {
        org.eclipse.swt.graphics.Rectangle r = 
            (org.eclipse.swt.graphics.Rectangle)
            getQueuer().invokeAndWait("getNodeBounds: " + node,  //$NON-NLS-1$
                    new IRunnable() {

                    public Object run() {
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
