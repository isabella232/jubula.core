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
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeTableOperationContext;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeTableComponent;
import org.eclipse.jubula.rc.swt.tester.util.TreeOperationContext;
import org.eclipse.jubula.rc.swt.tester.util.TreeTableOperationContext;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Implements the Tree interface for adapting a <code>SWT.Tree</code>
 * 
 * @author BREDEX GmbH
 */
public class TreeAdapter
        extends ControlAdapter
        implements ITreeTableComponent {

    /**
     * @param objectToAdapt
     *            graphics component which will be adapted
     */
    public TreeAdapter(Object objectToAdapt) {
        super(objectToAdapt);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractTreeOperationContext getContext() {
        return new TreeOperationContext(getEventThreadQueuer(), getRobot(),
                (Tree) getRealComponent());
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractTreeTableOperationContext getContext(int column) {
        return new TreeTableOperationContext(getEventThreadQueuer(), getRobot(),
                (Tree) getRealComponent(), column);
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(final String name, final Object cell) {
        return getEventThreadQueuer().invokeAndWait("getPropertyValueOfCell", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        return getRobot().getPropertyValue(cell, name);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Object getRootNode() {
        return getEventThreadQueuer().invokeAndWait("getRootNode", //$NON-NLS-1$
                new IRunnable<TreeItem[]>() {
                    public TreeItem[] run() {
                        return ((Tree) getRealComponent()).getItems();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRootVisible() {
        return true;
    }

}
