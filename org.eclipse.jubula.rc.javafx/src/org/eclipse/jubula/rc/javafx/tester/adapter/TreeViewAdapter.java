/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
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

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Implementation of the Tree interface as an adapter for <code>TreeView</code>.
 *
 * @author BREDEX GmbH
 * @created 19.11.2013
 */
public class TreeViewAdapter extends JavaFXComponentAdapter<TreeView<?>>
        implements ITreeComponent {

    /**
     * Constructor
     *
     * @param objectToAdapt
     *            the object to adapt
     */
    public TreeViewAdapter(TreeView<?> objectToAdapt) {
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
    public AbstractTreeOperationContext<TreeView<?>, TreeItem<?>> getContext() {
        return new TreeOperationContext(getRobotFactory()
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


    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, Object cell) {
        Object prop = EventThreadQueuerJavaFXImpl.invokeAndWait("getProperty", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        try {
                            return getRobot().getPropertyValue(cell, name);
                        } catch (RobotException e) {
                            throw new StepExecutionException(
                                    e.getMessage(),
                                    EventFactory
                                            .createActionError(TestErrorEvent.
                                                    PROPERTY_NOT_ACCESSABLE));
                        }
                    }
                });
        return String.valueOf(prop);
    }
}
