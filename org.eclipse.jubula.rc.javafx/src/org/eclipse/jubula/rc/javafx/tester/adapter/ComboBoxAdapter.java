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

import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComboComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.util.NodeTraverseHelper;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

/**
 * ComboBox Adapter
 *
 * @param <T> (sub)-class of ComboBox
 *
 * @author BREDEX GmbH
 * @created 20.03.2014
 */
public class ComboBoxAdapter<T extends ComboBox<?>> extends
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
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        Object value = getRealComponent().getValue();
                        if (value == null) {
                            return null;
                        }
                        return value.toString();
                    }
                });
    }

    @Override
    public boolean isEditable() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isEditable", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        return getRealComponent().isEditable();
                    }
                });
    }

    @Override
    public void selectAll() {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public int getSelectedIndex() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getSelectedIndex",  //$NON-NLS-1$
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return getRealComponent().getSelectionModel()
                                .getSelectedIndex();
                    }
                });
    }

    @Override
    public void select(final int index) {
        final ListView<?> lv = getComboBoxList();
        T comboBox = getRealComponent();
        setOpenedStatus(comboBox, true);
        try {
            ListViewAdapter<ListView<?>> listViewAdapter = 
                new ListViewAdapter<ListView<?>>(lv);
            listViewAdapter.clickOnIndex(index, ClickOptions.create().
                    setClickCount(1).setMouseButton(1));
        } finally {
            setOpenedStatus(comboBox, false);
        }
    }

    @Override
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public String[] getValues() {
        final ListView<?> lv = getComboBoxList();
        T comboBox = getRealComponent();
        setOpenedStatus(comboBox, true);
        String[] values = new String[0];
        try {
            ListViewAdapter<?> listViewAdapter = 
                new ListViewAdapter<ListView<?>>(lv);
            values = listViewAdapter.getValues();
        } finally {
            setOpenedStatus(comboBox, false);
        }
        return values;
    }

    /**
     * Returns the list with the items of the combo box.
     * @return the list
     */
    private ListView<?> getComboBoxList() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getValues", //$NON-NLS-1$
                new Callable<ListView<?>>() {
                    /** {@inheritDoc} **/
                    public ListView<?> call() throws Exception {
                        T comboBox = getRealComponent();
                        NodeTraverseHelper<ListView> helper = 
                                new NodeTraverseHelper<ListView>();
                        List<ListView> listViewList = helper.getInstancesOf(
                                comboBox, ListView.class);
                        if (listViewList.size() == 1) {
                            return listViewList.get(0);
                        }
                        // If there is not exactly one list inside the combo box,
                        // then use internal API
                        ComboBoxListViewSkin<?> comboBoxListViewSkin = 
                                (ComboBoxListViewSkin<?>) comboBox.getSkin();
                        return (ListView<?>)comboBoxListViewSkin
                            .getPopupContent();
                    }
                });
    }

    /**
     * Opens or closes the combo box list.
     * @param comboBox the combo box
     * @param openStatus whether the combo box should be opened or not
     */
    private void setOpenedStatus(T comboBox, boolean openStatus) {
        if (comboBox.isShowing() != openStatus) {
            getRobot().click(comboBox, null,
                    ClickOptions.create().setClickCount(1).setMouseButton(1));
        }
    }
}