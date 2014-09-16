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
package org.eclipse.jubula.rc.common.tester;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.ClickOptions.ClickModifier;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.ListSelectionVerifier;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.StringParsing;

/**
 * @author BREDEX GmbH
 */
public class ListTester extends AbstractTextVerifiableTester {
    
    /**
     * Splits the enumeration of values.
     * @param values The values to split
     * @param separator The separator, may be <code>null</code>
     * @return The array of values
     */
    private String[] split(String values, String separator) {
        String[] list = StringParsing.splitToArray(values, ((separator == null)
            || (separator.length() == 0) ? INDEX_LIST_SEP_CHAR
                : separator.charAt(0)),
            TestDataConstants.ESCAPE_CHAR_DEFAULT);
        list = StringUtils.stripAll(list);
        return list;
    }
    
    /**
     * @return The array of selected indices
     * @throws StepExecutionException If there are no indices selected
     */
    private int[] getCheckedSelectedIndices() throws StepExecutionException {
        int[] selected = getListAdapter().getSelectedIndices();
        SelectionUtil.validateSelection(selected);
        return selected;
    }

    /**
     * 
     * @return the List Adapter
     */
    private IListComponent getListAdapter() {
        return ((IListComponent) getComponent());
    }
    
    /**
     * Verifies if the passed index is selected.
     * 
     * @param index The index to verify
     * @param expectSelected Whether the index should be selected.
     */
    public void rcVerifySelectedIndex(String index, boolean expectSelected) {
        int[] selected = getCheckedSelectedIndices();
        int implIndex = IndexConverter.toImplementationIndex(
                Integer.parseInt(index));

        boolean isSelected = ArrayUtils.contains(selected, implIndex);
        if (expectSelected != isSelected) {
            throw new StepExecutionException(
                    "Selection check failed for index: " + index,  //$NON-NLS-1$
                    EventFactory.createVerifyFailed(
                            String.valueOf(expectSelected), 
                            String.valueOf(isSelected)));
        }
    }
    
    /**
     * Verifies if the passed value or enumeration of values is selected. By
     * default, the enumeration separator is <code>,</code>
     * @param valueList The value or list of values to verify
     */
    public void rcVerifySelectedValue(String valueList) {
        rcVerifySelectedValue(valueList, MatchUtil.DEFAULT_OPERATOR, true);
    }
    
    /**
     * Verifies if the passed value is selected.
     * 
     * @param value The value to verify
     * @param operator The operator to use when comparing the 
     *                 expected and actual values.
     *  @param isSelected if the value should be selected or not.
     */
    public void rcVerifySelectedValue(String value, String operator,
            boolean isSelected) {

        final String[] current = getListAdapter().getSelectedValues();
        final ListSelectionVerifier listSelVerifier =
            new ListSelectionVerifier();
        for (int i = 0; i < current.length; i++) {
            listSelVerifier.addItem(i, current[i], true);
        }
        listSelVerifier.verifySelection(value, operator, isSelected);
    }
    
    /**
     * Verifies if all selected elements of a list match a text.
     * @param text The text to verify
     * @param operator The operator used to verify
     */
    public void rcVerifyText(String text, String operator) {
        String[] selected = getListAdapter().getSelectedValues();
        final int selCount = selected.length;
        SelectionUtil.validateSelection(selected);
        for (int i = 0; i < selCount; i++) {
            Verifier.match(selected[i], text, operator);
        }
    }
    
    /**
     * Selects the passed index or enumeration of indices. The enumeration must
     * be separated by <code>,</code>, e.g. <code>1, 3,6</code>.
     * @param indexList The index or indices to select
     * @param extendSelection Whether this selection extends a previous 
     *                        selection.
     * @param button what mouse button should be used
     * @param clickCount the click count
     */
    public void rcSelectIndex(String indexList, final String extendSelection,
        int button, int clickCount) {
        selectIndices(IndexConverter.toImplementationIndices(
            parseIndices(indexList)),
            ClickOptions.create()
                .setClickCount(clickCount)
                .setMouseButton(button)
                .setClickModifier(getClickModifier(extendSelection)));
    }
    
    /**
     * Selects the passed value or enumeration of values. By default, the
     * enumeration separator is <code>,</code>.
     * @param valueList The value or list of values to select
     * @param operator If regular expressions are used
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param extendSelection Whether this selection extends a previous 
     *                          selection. If <code>true</code>, the first 
     *                          element will be selected with CONTROL as a 
     *                          modifier.
     * @param button what mouse button should be used
     * @param clickCount the click count
     */
    public void rcSelectValue(String valueList, String operator, 
            String searchType, final String extendSelection, int button,
            int clickCount) {
        selectValue(valueList, String.valueOf(VALUE_SEPARATOR), operator, 
            searchType, ClickOptions.create()
                .setClickCount(clickCount)
                .setMouseButton(button)
                .setClickModifier(getClickModifier(extendSelection))); 
    }
    
    /**
     * Verifies if the list contains an element that renders <code>value</code>.
     * @param value The text to verify
     */
    public void rcVerifyContainsValue(String value) {
        Verifier.equals(true, containsValue(value));
    }

    /**
     * Verifies if the list contains an element that renders <code>value</code>.
     * @param value The text to verify
     * @param operator The operator used to verify
     * @param exists if the wanted value should exist or not.
     */
    public void rcVerifyContainsValue(String value, String operator,
            boolean exists) {

        Verifier.equals(exists, containsValue(value, operator));
    }
    
    /**
     * Action to read the value of the current selected item of the JList
     * to store it in a variable in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String rcReadValue(String variable) {
        String[] selected = getListAdapter().getSelectedValues();
        SelectionUtil.validateSelection(selected);
        return selected[0];
    }
    
    /**
     * Drags the passed value.
     *
     * @param mouseButton the mouseButton.
     * @param modifier the modifier.
     * @param value The value to drag
     * @param operator If regular expressions are used
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void rcDragValue(int mouseButton, String modifier, String value,
            String operator, final String searchType) {

        DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);

        Integer [] indices = findIndicesOfValues(
            new String [] {value}, operator, searchType);
        selectIndices(ArrayUtils.toPrimitive(indices), 
                ClickOptions.create().setClickCount(0));

        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }

    /**
     * Drops on the passed value.
     *
     * @param value The value on which to drop
     * @param operator If regular expressions are used
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void rcDropValue(String value, String operator,
        final String searchType, int delayBeforeDrop) {

        DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        try {
            Integer [] indices = findIndicesOfValues(
                new String [] {value}, operator, searchType);
            selectIndices(ArrayUtils.toPrimitive(indices), 
                    ClickOptions.create().setClickCount(0));
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }

    /**
     * Drags the passed index.
     *
     * @param mouseButton the mouseButton.
     * @param modifier the modifier.
     * @param index The index to drag
     */
    public void rcDragIndex(final int mouseButton, final String modifier,
            int index) {

        DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);

        selectIndices(
                new int [] {IndexConverter.toImplementationIndex(index)}, 
                ClickOptions.create().setClickCount(0));

        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }

    /**
     * Drops onto the passed index.
     *
     * @param index The index on which to drop
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void rcDropIndex(final int index, int delayBeforeDrop) {

        DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();

        try {
            selectIndices(
                    new int [] {IndexConverter.toImplementationIndex(index)}, 
                    ClickOptions.create().setClickCount(0));
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }

    /**
     * @param value The value
     * @return <code>true</code> if the list contains an element that is rendered with <code>value</code>
     */
    public boolean containsValue(String value) {
        Integer[] indices = findIndicesOfValues(
                new String[] { value },
                MatchUtil.EQUALS, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
        return indices.length > 0;
    }
    
    /**
     * @param value The value
     * @param operator The operator used to verify
     * @return <code>true</code> if the list contains an element that is rendered with <code>value</code>
     */
    public boolean containsValue(String value, String operator) {
        Integer[] indices = null;
        if (operator.equals(MatchUtil.NOT_EQUALS)) {
            indices = findIndicesOfValues(
                    new String[] { value },
                MatchUtil.EQUALS, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
            return indices.length == 0;
        } 
        indices = findIndicesOfValues(new String[] { value },
            operator, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
        return indices.length > 0;
    }
    
    /**
     * Selects the passed value or enumeration of values. By default, the
     * enumeration separator is <code>,</code>, but may be changed by
     * <code>separator</code>.
     * @param valueList The value or list of values to select
     * @param separator The separator, optional
     * @param operator If regular expressions are used
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param co the click options to use
     */
    private void selectValue(String valueList, String separator,
            String operator, final String searchType, ClickOptions co) {

        String[] values = null;
        if (StringConstants.EMPTY.equals(valueList)) {
            values = new String[1];
            values[0] = StringConstants.EMPTY;
        } else {
            values = split(valueList, separator);
        }
        Integer[] indices = findIndicesOfValues(values,
                operator, searchType);
        Arrays.sort(indices);
        if (!operator.equals(MatchUtil.NOT_EQUALS) 
                && (indices.length < values.length)) {
            throw new StepExecutionException("One or more values not found of set: " //$NON-NLS-1$
                + Arrays.asList(values).toString(),
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        selectIndices(ArrayUtils.toPrimitive(indices), co);
    }
    
    /**
     * Parses the enumeration of indices.
     * @param indexList The enumeration of indices
     * @return The array of parsed indices
     */
    private int[] parseIndices(String indexList) {
        String[] list = StringParsing.splitToArray(indexList,
                INDEX_LIST_SEP_CHAR, TestDataConstants.ESCAPE_CHAR_DEFAULT);
        int[] indices = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            indices[i] = IndexConverter.intValue(list[i]);
        }
        return indices;
    }
    
    /**
     * @param indices
     *            The indices to select
     * @param co
     *            the click options to use
     */
    private void selectIndices(int[] indices, ClickOptions co) {
        int indexToStart = 0;
        final int noOfItemsToSelect = indices.length;
        final ClickModifier currentClickModifier = co.getClickModifier();
        boolean isExtendSelection = currentClickModifier
            .hasModifiers(ClickModifier.M1);
        final IListComponent listAdapter = getListAdapter();
        if (!isExtendSelection) {
            if (noOfItemsToSelect > 0) {
                // set a new first selection
                listAdapter.clickOnIndex(indices[0], co);
            }
            indexToStart++;
        }
        
        // if multiple items should be selected at once implicitly extend selection
        if (noOfItemsToSelect > 1) {
            currentClickModifier.add(ClickModifier.M1);
        }
        
        for (int i = indexToStart; i < noOfItemsToSelect; i++) {
            listAdapter.clickOnIndex(indices[i], co);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }
    
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
     *         values array, but may contains <code>null</code> elements for all
     *         values that are not found in the list
     */
    private Integer[] findIndicesOfValues(final String[] values,
        final String operator, final String searchType) {
        String[] listValues = getListAdapter().getValues();
        int startIndex = getStartingIndex(searchType);
        final int valuesCount = values.length;
        final List<Integer> indexList = new LinkedList<Integer>();
        final MatchUtil matchUtil = MatchUtil.getInstance();
        for (int i = 0; i < valuesCount; i++) {
            final String value = values[i];
            for (int j = startIndex; j < listValues.length; j++) {

                final String listItem = listValues[j];
                if (matchUtil.match(listItem, value, operator)) {

                    indexList.add(j);
                }
            }
        }
        return indexList.toArray(new Integer[indexList.size()]);
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
            int [] selectedIndices = getListAdapter().getSelectedIndices();
            // Start from the last selected item, if any item(s) are selected
            if (selectedIndices.length > 0) {
                startingIndex = selectedIndices[selectedIndices.length - 1] + 1;
            }
        }
        return startingIndex;
    }
}
