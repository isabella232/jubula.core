/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.swing.implclasses;

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
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;


/**
 * A helper class for robot operations and method calls on <code>JList</code>.
 *
 * @author BREDEX GmbH
 * @created 12.08.2005
 */
public class JListHelper {

    /**
     * The implementation class.
     */
    private final AbstractSwingImplClass m_implClass;
    /**
     * The constructor.
     *
     * @param implClass The implementation class
     */
    public JListHelper(AbstractSwingImplClass implClass) {
        m_implClass = implClass;
    }
    /**
     * Clicks on the index of the passed list.
     *
     * @param list
     *            The list to click on
     * @param i
     *            The index to click
     * @param co the click options to use
     * @param maxWidth the maximal width which is used to select the item
     */
    public void clickOnIndex(final JList list, final Integer i,
            ClickOptions co, double maxWidth) {
        final int index = i.intValue();
        ListModel model = list.getModel();
        if ((model == null) || (index >= model.getSize())
            || (index < 0)) {
            throw new StepExecutionException("List index '" + i //$NON-NLS-1$
                + "' is out of range", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.INVALID_INDEX));
        }
        // Call of JList.ensureIndexIsVisible() is not required,
        // because the Robot scrolls the click rectangle to visible.
        Rectangle r = (Rectangle) m_implClass.getEventThreadQueuer()
                .invokeAndWait("getCellBounds", new IRunnable() {

                    public Object run() throws StepExecutionException {
                        return list.getCellBounds(index, index);
                    }
                });        
        
        if (r == null) {
            throw new StepExecutionException(
                "List index '" + i + "' is not visible", //$NON-NLS-1$ //$NON-NLS-2$
                EventFactory.createActionError(TestErrorEvent.NOT_VISIBLE));
        }
        
        // if possible adjust height and width for items
        ListCellRenderer lcr = list.getCellRenderer();
        if (lcr != null) {
            Component listItem = lcr.getListCellRendererComponent(list, model
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

        m_implClass.getRobot().click(list, r,
                co.setClickType(ClickOptions.ClickType.RELEASED));
    }

    /**
     * Clicks on the index of the passed list.
     *
     * @param list
     *            The list to click on
     * @param i
     *            The index to click
     * @param co the click options to use
     */
    public void clickOnIndex(final JList list, final Integer i,
            ClickOptions co) {
        clickOnIndex(list, i, co, JComboBoxImplClass.NO_MAX_WIDTH);
    }
    
    /**
     * Clicks on the index of the passed list.
     *
     * @param list
     *            The list to click on
     * @param i
     *            The index to click
     */
    public void clickOnIndex(final JList list, final Integer i) {
        clickOnIndex(list, i, ClickOptions.create().setClickCount(1));
    }

    /**
     * Finds the indices of the list elements that are rendered with the passed
     * values.
     *
     * @param list
     *            The list
     * @param values
     *            The values
     * @param operator
     *            operator to use
     * @param searchType
     *            Determines where the search begins ("relative" or "absolute")
     * @return The array of indices. It's length is equal to the length of the
     *         values array, but may contains <code>null</code> elements for
     *         all values that are not found in the list
     */
    public Integer[] findIndicesOfValues(final JList list,
        final String[] values, final String  operator,
        final String searchType) {

        final Set indexSet = new HashSet();
        m_implClass.getEventThreadQueuer().invokeAndWait("findIndices", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
                    ListCellRenderer renderer = list.getCellRenderer();
                    ListModel model = list.getModel();

                    for (int i = getStartingIndex(list, searchType);
                            i < model.getSize();
                            ++i) {
                        Object obj = model.getElementAt(i);
                        list.ensureIndexIsVisible(i);
                        Component comp = renderer.getListCellRendererComponent(
                            list, obj, i, false, false);
                        String str = m_implClass.getRenderedText(comp, false);
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
     * @param list
     *            The list
     * @param values
     *            The values
     * @param operator
     *            operator to use
     * @return The array of indices. It's length is equal to the length of the
     *         values array, but may contains <code>null</code> elements for
     *         all values that are not found in the list
     */
    public Integer[] findIndicesOfValues(final JList list,
        final String[] values, final String  operator) {

        return findIndicesOfValues(list, values, operator, 
                CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
    }

    /**
     * @param list
     *            The list
     * @param value
     *            The value
     * @return <code>true</code> if the list contains an element that is
     *         rendered with <code>value</code>
     */
    public boolean containsValue(JList list, String value) {
        Integer[] indices = findIndicesOfValues(list, new String[] { value },
                MatchUtil.EQUALS);
        return indices.length > 0;
    }

    /**
     * @param list
     *            The list
     * @param value
     *            The value
    * @param operator
    *            The operator used to verify
     * @return <code>true</code> if the list contains an element that is
     *         rendered with <code>value</code>
     */
    public boolean containsValue(JList list, String value, String operator) {
        Integer[] indices = null;
        if (operator.equals(MatchUtil.NOT_EQUALS)) {
            indices = findIndicesOfValues(list, new String[] { value },
                MatchUtil.EQUALS);
            return indices.length == 0;
        }
        indices = findIndicesOfValues(list, new String[] { value },
            operator);
        return indices.length > 0;
    }

    /**
     * @param list
     *            The list
     * @return The array of selected indices
     */
    public int[] getSelectedIndices(final JList list) {
        return (int[])m_implClass.getEventThreadQueuer().invokeAndWait(
            "getSelectedIndices", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return list.getSelectedIndices();
                }
            });
    }
    /**
     * @param list
     *            The list
     * @return The array of selected values as the renderer shows them
     */
    public String[] getSelectedValues(final JList list) {
        final int[] indices = getSelectedIndices(list);

        return (String[])m_implClass.getEventThreadQueuer().invokeAndWait(
            "getSelectedValues", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    Object[] values = list.getSelectedValues();
                    String[] selected = new String[values.length];
                    ListCellRenderer renderer = list.getCellRenderer();
                    for (int i = 0; i < values.length; i++) {
                        Object value = values[i];
                        Component c = renderer.getListCellRendererComponent(
                            list, value, indices[i], true, false);
                        selected[i] = m_implClass.getRenderedText(c, false);
                    }
                    return selected;
                }
            });
    }

    /**
     * @param list
     *            The list
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected cell.
     */
    private int getStartingIndex(final JList list, final String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(
                CompSystemConstants.SEARCH_TYPE_RELATIVE)) {
            int [] selectedIndices = getSelectedIndices(list);
            // Start from the last selected item
            startingIndex = selectedIndices[selectedIndices.length - 1] + 1;
        }
        return startingIndex;
    }
}
