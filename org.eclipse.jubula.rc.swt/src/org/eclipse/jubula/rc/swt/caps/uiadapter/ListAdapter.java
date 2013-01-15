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
package org.eclipse.jubula.rc.swt.caps.uiadapter;

import java.util.ArrayList;

import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IListAdapter;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.List;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class ListAdapter extends WidgetAdapter implements IListAdapter {

    /**  */
    private List m_list;
    /**
     * 
     * @param objectToAdapt -
     */
    public ListAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_list = (List) objectToAdapt;
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
                        return m_list.getSelectionIndices();
                    }
                });
    }
    /**
     * {@inheritDoc}
     */
    public void clickOnIndex(Integer i, ClickOptions co) {
        final int iVal = i.intValue();
        scrollIndexToVisible(iVal);
        
        final Rectangle clickConstraints = 
            (Rectangle)getEventThreadQueuer().invokeAndWait(
                "setClickConstraints",  //$NON-NLS-1$
                new IRunnable() {

                    public Object run() throws StepExecutionException {
                        Rectangle constraints = new Rectangle(0, 0, 0, 0);
                        int displayedItemCount = getDisplayedItemCount();
                        int numberBelowTop = 0;
                        if (displayedItemCount >= m_list.getItemCount()) {
                            numberBelowTop = iVal;
                        } else {
                            numberBelowTop = Math.max(0, iVal 
                                - m_list.getItemCount() + displayedItemCount);
                        }
                        
                        // Set the constraints based on the numberBelowTop
                        constraints.height = m_list.getItemHeight();
                        constraints.width = m_list.getBounds().width;
                        constraints.y += (numberBelowTop * constraints.height);
                        // explicitly use list relative bounds here - as e.g. on
                        // Mac OS systems list.getClientArea() is not relative
                        // see bug 353905
                        Rectangle actualListBounds =
                            new Rectangle(0, 0, m_list.getClientArea().width, 
                                    m_list.getClientArea().height);
                        return constraints.intersection(actualListBounds);
                    }
                });
        
        // Note that we set scrollToVisible false because we have already done
        // the scrolling.
        getRobot().click(m_list, clickConstraints, 
                co.setScrollToVisible(false));

    }
    /**
     * {@inheritDoc}
     */
    public String[] getSelectedValues() {
        return (String[])getEventThreadQueuer().invokeAndWait(
                "getSelectedValues", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_list.getSelection();
                    }
                });
    }
    /**
     * {@inheritDoc}
     */
    public Integer[] findIndicesOfValues(final String[] values,
            final String operator, final String searchType) {
        final java.util.List indexList = (java.util.List)
                getEventThreadQueuer().invokeAndWait("findIndices", //$NON-NLS-1$
                    new IRunnable() {
                        public Object run() {
                            final int valuesLength = values.length;
                            final java.util.List idxList = new ArrayList(
                                values.length);
                            final int listItemCount = m_list.getItemCount();
                            final MatchUtil matchUtil = MatchUtil.getInstance();
                            for (int i = 0; i < valuesLength; i++) {
                                final String value = values[i];
                                for (int j = getStartingIndex(searchType); 
                                    j < listItemCount; j++) {
                                    
                                    final String listItem = m_list.getItem(j);
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
     * @return  the number of items displayed in the list.
     */
    private int getDisplayedItemCount() {
        return ((Integer)getEventThreadQueuer().invokeAndWait(
                "getDisplayedItemCount",  //$NON-NLS-1$
                new IRunnable() {

                public Object run() throws StepExecutionException {
                    int listHeight = SwtUtils.getWidgetBounds(m_list).height;
                    int itemHeight = m_list.getItemHeight();
                    
                    return new Integer(listHeight / itemHeight);
                }
            
            })).intValue();
    }
    
    /**
     * @param index The index to make visible
     */
    private void scrollIndexToVisible(final int index) {
        getEventThreadQueuer().invokeAndWait(
            "scrollIndexToVisible",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
           
                    m_list.setTopIndex(index);

                    return null;
                }
            
            });
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
            // Start from the last selected item, if any item(s) are selected
            if (selectedIndices.length > 0) {
                startingIndex = selectedIndices[selectedIndices.length - 1] + 1;
            }
        }
        return startingIndex;
    }
}
