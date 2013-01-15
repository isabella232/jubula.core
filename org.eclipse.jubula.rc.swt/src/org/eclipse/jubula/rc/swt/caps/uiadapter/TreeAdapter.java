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
package org.eclipse.jubula.rc.swt.caps.uiadapter;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.ITreeAdapter;
import org.eclipse.jubula.rc.swt.implclasses.TreeOperationContext;
import org.eclipse.swt.widgets.Tree;
/**
 * Implements the Tree interface for adapting a <code>SWT.Tree</code>
 * 
 *  @author BREDEX GmbH
 */
public class TreeAdapter extends WidgetAdapter implements ITreeAdapter {


    /**
     * 
     * @param objectToAdapt graphics component which will be adapted
     */
    public TreeAdapter(Object objectToAdapt) {
        super(objectToAdapt);
    }
    /**
     * 
     * @return the caste object
     */
    private Tree getTree() {
        return (Tree) getRealComponent();
    }
    /**
     * {@inheritDoc}
     */
    public Object getRootNode() {
        return getEventThreadQueuer()
                .invokeAndWait("getRootNode", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return getTree().getItems();
                    }
                });
    }
    /**
     * {@inheritDoc}
     */
    public AbstractTreeOperationContext getContext() {
        return new TreeOperationContext(getEventThreadQueuer(),
                getRobot(), getTree());
    }
    /**
     * {@inheritDoc}
     */
    public boolean isRootVisible() {
        
        return true;
    }

}
