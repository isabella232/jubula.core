/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.rc.rcp.tabbedproperties.adapter;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListComponent;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.swt.tester.adapter.ControlAdapter;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList.ListElement;

/**
 * The adapter for the {@link TabbedPropertyList}
 * @author BREDEX GmbH
 *
 */
public class TabbedPropertiesAdapter extends ControlAdapter
        implements IListComponent<TabbedPropertyList> {
    
    /** the {@link TabbedPropertyList}*/
    private TabbedPropertyList m_list;
    
    /**
     * constructor
     * @param objectToAdapt the {@link TabbedPropertyList}
     */
    public TabbedPropertiesAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_list = (TabbedPropertyList) objectToAdapt;
            
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText() {
        String[] selected = getSelectedValues();
        SelectionUtil.validateSelection(selected);
        return selected[0];
    }

    /**
     * {@inheritDoc}
     */
    public String getPropteryValue(String propertyname) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {
        return m_list;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getSelectedIndices() {
        return new int[]{m_list.getSelectionIndex()};
    }

    /**
     * {@inheritDoc}
     */
    public void clickOnIndex(Integer i, ClickOptions co) {
        Canvas a = (Canvas) m_list.getElementAt(i.intValue());
        TabItem c = null;
        getRobot().click(a, null, co);
        
    }

    /**
     * {@inheritDoc}
     */
    public String[] getSelectedValues() {
        ListElement element =
                (ListElement) m_list.getElementAt(m_list.getSelectionIndex());
        return new String[] { element.getTabItem().getText() };
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        String[] values = new String[m_list.getNumberOfElements()];
        for (int j = 0; j < m_list.getNumberOfElements(); j++) {
            ListElement element = (ListElement) m_list.getElementAt(j);
            String string = element.getTabItem().getText();
            values[j] = string;
        }
        
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, TabbedPropertyList cell) {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }
    
}
