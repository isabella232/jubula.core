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
package org.eclipse.jubula.rc.swing.tester.adapter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListAdapter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.swing.tester.util.TesterUtil;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class JListAdapter extends WidgetAdapter implements IListAdapter {
    /** */
    private JList m_list;
    
    /**
     * 
     * @param objectToAdapt 
     */
    public JListAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_list = (JList) objectToAdapt;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText() {
        String[] selected = getSelectedValues();
        if (selected.length > 0) {
            return selected[0];
        }
        throw new StepExecutionException("No list item selected", //$NON-NLS-1$
            EventFactory.createActionError(TestErrorEvent.NO_SELECTION));
    }

    /**
     * {@inheritDoc}
     */
    public int[] getSelectedIndices() {
        return (int[])getEventThreadQueuer().invokeAndWait(
            "getSelectedIndices", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return m_list.getSelectedIndices();
                }
            });
    }

    /**
     * Clicks on the index of the passed list.
     *
     * @param i The index to click
     * @param co the click options to use
     * @param maxWidth the maximal width which is used to select the item
     */
    public void clickOnIndex(final Integer i,
            ClickOptions co, double maxWidth) {
        final int index = i.intValue();
        ListModel model = m_list.getModel();
        if ((model == null) || (index >= model.getSize())
            || (index < 0)) {
            throw new StepExecutionException("List index '" + i //$NON-NLS-1$
                + "' is out of range", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.INVALID_INDEX));
        }
        // Call of JList.ensureIndexIsVisible() is not required,
        // because the Robot scrolls the click rectangle to visible.
        Rectangle r = (Rectangle) getRobotFactory().getEventThreadQueuer()
                .invokeAndWait("getCellBounds", new IRunnable() { //$NON-NLS-1$

                    public Object run() throws StepExecutionException {
                        return m_list.getCellBounds(index, index);
                    }
                });        
        
        if (r == null) {
            throw new StepExecutionException(
                "List index '" + i + "' is not visible", //$NON-NLS-1$ //$NON-NLS-2$
                EventFactory.createActionError(TestErrorEvent.NOT_VISIBLE));
        }
        
        // if possible adjust height and width for items
        ListCellRenderer lcr = m_list.getCellRenderer();
        if (lcr != null) {
            Component listItem = lcr.getListCellRendererComponent(m_list, model
                    .getElementAt(index), index, false, false);
            Dimension preferredSize = listItem.getPreferredSize();
            r.setSize(preferredSize);
        }
        
        if (maxWidth != JComboBoxAdapter.NO_MAX_WIDTH
                && r.getWidth() > maxWidth) {
            Dimension d = new Dimension();
            d.setSize(maxWidth, r.getHeight());
            r.setSize(d);
        }

        getRobot().click(m_list, r,
                co.setClickType(ClickOptions.ClickType.RELEASED));
    }

    /**
     * {@inheritDoc}
     */
    public String[] getSelectedValues() {
        final int[] indices = getSelectedIndices();

        return (String[])getEventThreadQueuer().invokeAndWait(
            "getSelectedValues", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    Object[] values = m_list.getSelectedValues();
                    String[] selected = new String[values.length];
                    ListCellRenderer renderer = m_list.getCellRenderer();
                    for (int i = 0; i < values.length; i++) {
                        Object value = values[i];
                        Component c = renderer.getListCellRendererComponent(
                            m_list, value, indices[i], true, false);
                        selected[i] = TesterUtil.getRenderedText(c);
                    }
                    return selected;
                }
            });
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsValue(String value, String operator) {
        String[] listValues = getValues();
        for (int i = 0; i < listValues.length; i++) {
            boolean contains = MatchUtil.getInstance()
                    .match(listValues[i], value, operator);
            if (contains) {
                return contains;
            }
        }
        return false;
    }
    
    /**
     * Clicks on the index of the passed list.
     *
     * @param i
     *            The index to click
     * @param co the click options to use
     */
    public void clickOnIndex(final Integer i, ClickOptions co) {
        clickOnIndex(i, co, JComboBoxAdapter.NO_MAX_WIDTH);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        return (String[]) getEventThreadQueuer().invokeAndWait("getValues", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        String[] values;
                        ListCellRenderer renderer = m_list.getCellRenderer();
                        ListModel model = m_list.getModel();
                        values = new String[model.getSize()];
                        for (int i = 0; i < model.getSize(); ++i) {
                            Object obj = model.getElementAt(i);
                            m_list.ensureIndexIsVisible(i);
                            Component comp = renderer
                                    .getListCellRendererComponent(
                                m_list, obj, i, false, false);
                            String str = TesterUtil.getRenderedText(comp);
                            values[i] = str;
                        }
                        return values; // return value is not used
                    }
                });
    }
}
