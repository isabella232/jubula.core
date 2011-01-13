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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;



/**
 * @author BREDEX GmbH
 * @created 05.04.2007
 */
public abstract class AbstractComboBoxHelper implements IComboBoxHelper {

    /** number of clicks to give focus without selecting any text */
    public static final int CLICK_COUNT_FOR_SELECTING_NONE = 3;

    /**
     * {@inheritDoc}
     */
    public void select(final String[] values, String operator, 
        final String searchType) 
        throws StepExecutionException, IllegalArgumentException {
    
        for (int i = 0; i < values.length; i++) {
            String text = values[i];
            Validate.notNull(text, "text must not be null"); //$NON-NLS-1$
        }
        
        Integer[] indices = findIndicesOfValues(values, 
                operator, searchType);
        Arrays.sort(indices);
        if (indices.length == 0) {
            throw new StepExecutionException("Text '" + Arrays.asList(values).toString() //$NON-NLS-1$ 
                + "' not found", //$NON-NLS-1$ 
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        select(indices[0].intValue());
    }
    
    /**
     * {@inheritDoc}
     */
    public void select(int index) {
        
        int comboItemCount = getItemCount();

        if (index >= comboItemCount
            || index < 0) {
            throw new StepExecutionException("Combo Box index '" + index //$NON-NLS-1$
                + "' is out of range", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.INVALID_INDEX));
        }

        if (isComboEnabled()) {
            // FIXME zeb: Needs special handling if style is not DROP_DOWN
            openDropdownList();
            selectImpl(index);
        }
    }

    /**
     * @return <code>true</code> if the combo box is currently enabled.
     */
    protected abstract boolean isComboEnabled();

    /**
     * 
     * @param index idx
     */
    protected abstract void selectImpl(int index);
    
    /**
     * Opens the combobox dropdown list. May also be
     * called if the list is already visible.
     */
    protected abstract void openDropdownList();

    /**
     * Finds the indices of the list elements that are rendered with the passed
     * values.
     * 
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
    private Integer[] findIndicesOfValues(final String [] values, 
            final String operator, final String searchType) {
        
        final Set indexSet = new HashSet();

        for (int i = getStartingIndex(searchType); 
                i < getItemCount(); 
                ++i) {
            
            String str = getItem(i);
            if (MatchUtil.getInstance().
                match(str, values, operator)) {
                indexSet.add(new Integer(i));
            }
        }
                    
        
        Integer[] indices = new Integer[indexSet.size()];
        indexSet.toArray(indices);
        return indices;

    }

    /**
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected item.
     */
    private int getStartingIndex(final String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(
                CompSystemConstants.SEARCH_TYPE_RELATIVE)) {
            startingIndex = getSelectedIndex();
        }
        return startingIndex;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(String value, String operator) {
        Integer[] indices = null;
        if (operator.equals(MatchUtil.NOT_EQUALS)) {
            indices = findIndicesOfValues(new String[] { value },
                MatchUtil.EQUALS, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
            return indices.length == 0;
        } 
        indices = findIndicesOfValues(new String[] { value },
            operator, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
        return indices.length > 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(String value) {
        return containsValue(value, MatchUtil.EQUALS);
    }

    /**
     * Returns the number of items contained in the combo list.
     * @return  the number of items.
     */
    protected abstract int getItemCount();

    /**
     * Returns the item at the given, zero-relative index in the combo list. 
     * Throws an exception if the index is out of range.
     * @param index the index of the item to return
     * @return  the item at the given index
     */
    protected abstract String getItem(final int index);

}
