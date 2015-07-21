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
package org.eclipse.jubula.ext.rc.javafx.tester.adapter;

import java.util.List;
import java.util.concurrent.Callable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.stage.Window;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.adapter.AbstractComponentAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.IContainerAdapter;
/**
 * 
 * @author BREDEX GmbH
 */
public class CustomContainerAdapter<T extends ListCell<?>> extends
        AbstractComponentAdapter<T> implements IContainerAdapter {

    public CustomContainerAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public List<Node> getContent() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getContent",
                new Callable<List<Node>>() {

                    @Override
                    public List<Node> call() throws Exception {
                        return getRealComponent().getChildrenUnmodifiable();
                    }
                });
    }

    @Override
    public ReadOnlyObjectProperty<? extends Window> getWindow() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getWindow",
                new Callable<ReadOnlyObjectProperty<? extends Window>>() {

                    @Override
                    public ReadOnlyObjectProperty<? extends Window> call()
                            throws Exception {
                        return getRealComponent().getScene().windowProperty();
                    }
                });
    }

}
