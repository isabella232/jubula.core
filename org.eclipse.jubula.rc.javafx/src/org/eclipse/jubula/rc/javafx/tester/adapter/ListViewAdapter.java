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

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;

/**
 * ListView Adapter
 *
 * @param <T> (sub)-class of ListView
 *
 * @author BREDEX GmbH
 * @created 14.03.2014
 */
public class ListViewAdapter<T extends ListView> extends
    JavaFXComponentAdapter<T> implements IListComponent {
    /**
     * Creates an object with the adapted Label.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>ListView</code>
     */
    public ListViewAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public String getText() {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait("getText", //$NON-NLS-1$
            new Callable<String>() {

                @Override
                public String call() throws Exception {
                    ObservableList sItems = getRealComponent() 
                            .getSelectionModel().getSelectedItems();
                    if (!sItems.isEmpty()) {
                        return String.valueOf(sItems.get(0));
                    }
                    throw new StepExecutionException("No selection found", //$NON-NLS-1$
                            EventFactory.createActionError(TestErrorEvent.
                                            NO_SELECTION));
                }
            });
        return result;
    }

    @Override
    public int[] getSelectedIndices() {
        // FIXME MT: IMPLEMENT
        return null;
    }

    @Override
    public void clickOnIndex(Integer i, ClickOptions co) {
        // FIXME MT: IMPLEMENT
    }

    @Override
    public String[] getSelectedValues() {
        // FIXME MT: IMPLEMENT
        return null;
    }

    @Override
    public String[] getValues() {
        // FIXME MT: IMPLEMENT
        return null;
    }
}