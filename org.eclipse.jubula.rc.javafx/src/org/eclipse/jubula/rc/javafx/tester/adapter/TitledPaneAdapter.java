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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Adapter for the TitledPane.
 * 
 * @author BREDEX GmbH
 * @created 30.05.2014
 */
public class TitledPaneAdapter extends JavaFXComponentAdapter<TitledPane>
        implements IContainerAdapter {

    /**
     * 
     * @param objectToAdapt
     *            a TitledPane
     */
    public TitledPaneAdapter(TitledPane objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public List<Node> getContent() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getContent",
                new Callable<List<Node>>() {

                    @Override
                    public List<Node> call() throws Exception {
                        ArrayList<Node> list = new ArrayList<>();
                        list.add(getRealComponent().getContent());
                        list.add(getRealComponent().getGraphic());
                        return list;
                    }
                });
    }

}
