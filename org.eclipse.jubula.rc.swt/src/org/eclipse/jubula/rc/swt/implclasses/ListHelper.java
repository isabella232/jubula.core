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
package org.eclipse.jubula.rc.swt.implclasses;

import java.util.ArrayList;

import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.List;


/**
 * @author BREDEX GmbH
 * @created 06.12.2006
 */
public class ListHelper {
    
    /** The implementation class. */
    private AbstractControlImplClass m_implClass;
    
    /**
     * The constructor.
     * @param implClass The implementation class
     */
    public ListHelper(AbstractControlImplClass implClass) {
        m_implClass = implClass;
    }
    
    /**
     * Clicks on the index of the passed list.
     * @param list The list to click on
     * @param i The index to click
     * @param co the click options to use for selecting an index item
     */
    public void clickOnIndex(final List list, final Integer i, 
            final ClickOptions co) {
        final int iVal = i.intValue();
        scrollIndexToVisible(list, iVal);
        
        final Rectangle clickConstraints = 
            (Rectangle)m_implClass.getEventThreadQueuer().invokeAndWait(
                "setClickConstraints",  //$NON-NLS-1$
                new IRunnable() {

                    public Object run() throws StepExecutionException {
                        Rectangle constraints = new Rectangle(0, 0, 0, 0);
                        int displayedItemCount = getDisplayedItemCount(list);
                        int numberBelowTop = 0;
                        if (displayedItemCount >= list.getItemCount()) {
                            numberBelowTop = iVal;
                        } else {
                            numberBelowTop = Math.max(0, iVal 
                                - list.getItemCount() + displayedItemCount);
                        }
                        
                        // Set the constraints based on the numberBelowTop
                        constraints.height = list.getItemHeight();
                        constraints.width = list.getBounds().width;
                        constraints.y += (numberBelowTop * constraints.height);

                        return constraints.intersection(list.getClientArea());
                    }
                });
        
        // Note that we set scrollToVisible false because we have already done
        // the scrolling.
        m_implClass.getRobot().click(list, clickConstraints, 
                co.setScrollToVisible(false));

    }

    /**
     * Clicks on the index of the passed list.
     * @param list The list to click on
     * @param i The index to click
     */
    public void clickOnIndex(final List list, final Integer i) {
        clickOnIndex(list, i, ClickOptions.create().setClickCount(1));
    }

    /**
     * Finds the indices of the list elements that are rendered with the passed values.
     * @param list The list
     * @param values The values
     * @param operator operator to use
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @return The array of indices. It's length is equal to the length of the
     *         values array, but may contains <code>null</code> elements for
     *         all values that are not found in the list
     */
    public Integer[] findIndicesOfValues(final List list, final String[] values,
        final String operator, final String searchType) {
        
        final java.util.List indexList = (java.util.List)m_implClass
            .getEventThreadQueuer().invokeAndWait("findIndices", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        final int valuesLength = values.length;
                        final java.util.List idxList = new ArrayList(
                            values.length);
                        final int listItemCount = list.getItemCount();
                        final MatchUtil matchUtil = MatchUtil.getInstance();
                        for (int i = 0; i < valuesLength; i++) {
                            final String value = values[i];
                            for (int j = getStartingIndex(list, searchType); 
                                j < listItemCount; j++) {
                                
                                final String listItem = list.getItem(j);
                                if (matchUtil.match(listItem, value, 
                                    operator)) {
                                    
                                    idxList.add(new Integer(j));
                                }
                            }
                        }
                        return idxList;
                    }
                });
        Integer[] indices = new Integer[indexList.size()];
        indexList.toArray(indices);
        return indices;
    }

    /**
     * @param list The list
     * @param value The value
     * @return <code>true</code> if the list contains an element that is rendered with <code>value</code>
     */
    public boolean containsValue(List list, String value) {
        Integer[] indices = findIndicesOfValues(list, new String[] { value },
                MatchUtil.EQUALS, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
        return indices.length > 0;
    }
    
    /**
     * @param list The list
     * @param value The value
     * @param operator The operator used to verify
     * @return <code>true</code> if the list contains an element that is rendered with <code>value</code>
     */
    public boolean containsValue(List list, String value, String operator) {
        Integer[] indices = null;
        if (operator.equals(MatchUtil.NOT_EQUALS)) {
            indices = findIndicesOfValues(list, new String[] { value },
                MatchUtil.EQUALS, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
            return indices.length == 0;
        } 
        indices = findIndicesOfValues(list, new String[] { value },
            operator, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
        return indices.length > 0;
    }

    /**
     * @param list The list
     * @return The array of selected indices
     */
    public int[] getSelectedIndices(final List list) {
        return (int[])m_implClass.getEventThreadQueuer().invokeAndWait(
            "getSelectedIndices", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return list.getSelectionIndices();
                }
            });
    }
    
    /**
     * @param list The list
     * @return The array of selected values as the renderer shows them
     */
    public String[] getSelectedValues(final List list) {
        return (String[])m_implClass.getEventThreadQueuer().invokeAndWait(
            "getSelectedValues", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return list.getSelection();
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
    private int getStartingIndex(final List list, final String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(
                CompSystemConstants.SEARCH_TYPE_RELATIVE)) {
            int [] selectedIndices = getSelectedIndices(list);
            // Start from the last selected item, if any item(s) are selected
            if (selectedIndices.length > 0) {
                startingIndex = selectedIndices[selectedIndices.length - 1] + 1;
            }
        }
        return startingIndex;
    }

    /**
     * @param list The list for which to get the displayed item count.
     * @return  the number of items displayed in the list.
     */
    private int getDisplayedItemCount(final List list) {
        return ((Integer)m_implClass.getEventThreadQueuer().invokeAndWait(
                "getDisplayedItemCount",  //$NON-NLS-1$
                new IRunnable() {

                public Object run() throws StepExecutionException {
                    int listHeight = SwtUtils.getWidgetBounds(list).height;
                    int itemHeight = list.getItemHeight();
                    
                    return new Integer(listHeight / itemHeight);
                }
            
            })).intValue();
    }

    /**
     * Tries to set the given index as the top element of the list.
     * @param list The list to scroll
     * @param index The index to make visible
     */
    private void scrollIndexToVisible(final List list, final int index) {
        m_implClass.getEventThreadQueuer().invokeAndWait(
            "scrollIndexToVisible",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
           
                    list.setTopIndex(index);

                    return null;
                }
            
            });
    }

}