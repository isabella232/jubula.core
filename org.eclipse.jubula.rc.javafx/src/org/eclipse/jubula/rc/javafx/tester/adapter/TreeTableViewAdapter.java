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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.util.concurrent.Callable;

import javafx.scene.control.TreeTableView;

import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Implementation of the Tree interface as an adapter for <code>TreeTableView</code>.
 *
 * @author BREDEX GmbH
 * @created 23.06.2014
 */
public class TreeTableViewAdapter extends
        JavaFXComponentAdapter<TreeTableView<?>> implements ITreeComponent {

    /**
     * Creates a new Instance
     * @param objectToAdapt the TreeTableView which should be adapted
     *
     */
    public TreeTableViewAdapter(TreeTableView<?> objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public Object getRootNode() {
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getRootNode", new Callable<Object>() { //$NON-NLS-1$

                    @Override
                    public Object call() throws Exception {
                        return getRealComponent().getRoot().getValue();
                    }
                });
        return result;
    }

    @Override
    public AbstractTreeOperationContext getContext() {
        return new TreeTableOperationContext(getRobotFactory()
                .getEventThreadQueuer(), getRobot(), getRealComponent());
    }

    @Override
    public boolean isRootVisible() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isRootVisible", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        return getRealComponent().showRootProperty().getValue();
                    }
                });

        return result;
    }

}
