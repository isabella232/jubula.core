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
package org.eclipse.jubula.rc.swing.swing.tester.adapter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListAdapter;
import org.eclipse.jubula.rc.swing.swing.implclasses.JComboBoxImplClass;
import org.eclipse.jubula.rc.swing.swing.tester.TesterUtil;
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
        
        if (maxWidth != JComboBoxImplClass.NO_MAX_WIDTH
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
    public Integer[] findIndicesOfValues(
            final String[] values, final String  operator,
            final String searchType) {

        final Set indexSet = new HashSet();
        getEventThreadQueuer().invokeAndWait("findIndices", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
                    ListCellRenderer renderer = m_list.getCellRenderer();
                    ListModel model = m_list.getModel();
                    for (int i = getStartingIndex(searchType);
                            i < model.getSize(); ++i) {
                        Object obj = model.getElementAt(i);
                        m_list.ensureIndexIsVisible(i);
                        Component comp = renderer
                                .getListCellRendererComponent(
                            m_list, obj, i, false, false);
                        String str = TesterUtil.getRenderedText(comp);
                        if (MatchUtil.getInstance().
                            match(str, values, operator)) {
                            indexSet.add(new Integer(i));
                        }
                    }
                    return null; // return value is not used
                }
            });

        Integer[] indices = new Integer[indexSet.size()];
        indexSet.toArray(indices);
        return indices;
    }
    
    /**
     * Finds the indices of the list elements that are rendered with the passed
     * values.
     *
     * @param values The values
     * @param operator operator to use
     * @return The array of indices. It's length is equal to the length of the
     *         values array, but may contains <code>null</code> elements for
     *         all values that are not found in the list
     */
    public Integer[] findIndicesOfValues(
        final String[] values, final String  operator) {

        return findIndicesOfValues(values, operator, 
                CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsValue(String value, String operator) {
        Integer[] indices = null;
        if (operator.equals(MatchUtil.NOT_EQUALS)) {
            indices = findIndicesOfValues(new String[] { value },
                MatchUtil.EQUALS);
            return indices.length == 0;
        }
        indices = findIndicesOfValues(new String[] { value },
            operator);
        return indices.length > 0;
    }
    
    /**
     * Clicks on the index of the passed list.
     *
     * @param i
     *            The index to click
     * @param co the click options to use
     */
    public void clickOnIndex(final Integer i, ClickOptions co) {
        clickOnIndex(i, co, JComboBoxImplClass.NO_MAX_WIDTH);
    }

    /**
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingIndex(final String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(
                CompSystemConstants.SEARCH_TYPE_RELATIVE)) {
            int [] selectedIndices = getSelectedIndices();
            // Start from the last selected item
            startingIndex = selectedIndices[selectedIndices.length - 1] + 1;
        }
        return startingIndex;
    }
}
