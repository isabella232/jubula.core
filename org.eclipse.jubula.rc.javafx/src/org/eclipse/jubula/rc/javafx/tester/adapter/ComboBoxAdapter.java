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

import javafx.scene.control.ComboBox;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComboComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * ComboBox Adapter
 *
 * @param <T> (sub)-class of ComboBox
 *
 * @author BREDEX GmbH
 * @created 20.03.2014
 */
public class ComboBoxAdapter<T extends ComboBox> extends
    JavaFXComponentAdapter<T> implements IComboComponent {
    /**
     * Creates an object with the adapted Label.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>ComboBox</code>
     */
    public ComboBoxAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public String getText() {
        String text = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        return getRealComponent().getValue().toString();
                    }
                });
        return text;
    }

    @Override
    public boolean isEditable() {
        boolean editable = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isEditable", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        return getRealComponent().isEditable();
                    }
                });
        return editable;
    }

    @Override
    public void selectAll() {
        // FIXME Auto-generated method stub
        
    }

    @Override
    public int getSelectedIndex() {
        // FIXME Auto-generated method stub
        return 0;
    }

    @Override
    public void select(int index) {
        // FIXME Auto-generated method stub
        
    }

    @Override
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public String[] getValues() {
        // FIXME Auto-generated method stub
        return null;
    }

}