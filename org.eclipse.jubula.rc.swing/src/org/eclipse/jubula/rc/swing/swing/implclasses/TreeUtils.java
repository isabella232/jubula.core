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

import java.awt.Rectangle;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;


/**
 * Utilities to access JTree properties.
 * 
 * @author BREDEX GmbH
 * @created 22.03.2005
 */
public class TreeUtils {
    /**
     * Constructor.
     */
    private TreeUtils() {
        super();
    }
    /**
     * Calls
     * {@link JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)}
     * on the passed JTree.
     * 
     * @param queuer
     *            The event thread queuer.
     * @param tree
     *            The tree.
     * @param node
     *            The node.
     * @param row
     *            The node row.
     * @return The converted text
     * @throws StepExecutionException
     *             If the method call fails.
     */
    public static String convertValueToText(IEventThreadQueuer queuer,
        final JTree tree, final TreeNode node, final int row)
        throws StepExecutionException {
        return (String)queuer.invokeAndWait(
            "convertValueToText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return tree.convertValueToText(node, false, tree
                        .isExpanded(row), node.isLeaf(), row, false);
                }
            });
    }
    /**
     * Gets the row of the passed tree node.
     * 
     * @param queuer
     *            The event thread queuer.
     * @param tree
     *            The JTree.
     * @param model
     *            The tree model.
     * @param node
     *            The node.
     * @return The node row.
     * @throws StepExecutionException
     *             If the method call fails.
     */
    public static int getRowForTreeNode(IEventThreadQueuer queuer,
        final JTree tree, final DefaultTreeModel model, final TreeNode node)
        throws StepExecutionException {
        Integer row = (Integer)queuer.invokeAndWait(
            "getRowForTreeNode", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    TreePath pathToRoot = new TreePath(model
                        .getPathToRoot(node));
                    return new Integer(tree.getRowForPath(pathToRoot));
                }
            });
        return row.intValue();
    }
    /**
     * Gets the bounds of the passed tree node row.
     * 
     * @param queuer
     *            The event thread queuer.
     * @param tree
     *            The JTree.
     * @param row
     *            The node row.
     * @return The bounds.
     * @throws StepExecutionException
     *             If the method call fails.
     */
    public static Rectangle getRowBounds(IEventThreadQueuer queuer,
        final JTree tree, final int row) 
        throws StepExecutionException {
        return (Rectangle)queuer.invokeAndWait("getRowBounds", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                return tree.getRowBounds(row);
            }
        });
    }
}
