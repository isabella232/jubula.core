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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.ListSelectionVerifier;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swt.driver.DragAndDropHelperSwt;
import org.eclipse.jubula.rc.swt.interfaces.IListImplClass;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.StringParsing;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;



/**
 * Implementation class for the swt-List
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class ListImplClass extends AbstractControlImplClass 
    implements IListImplClass {

    /** the List from the AUT */
    private List m_list;
    /** The <code>List</code> helper. */
    private ListHelper m_listHelper;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_list = (List)graphicsComponent;
        m_listHelper = new ListHelper(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_list;
    }
    
    /**
     * Splits the enumeration of values.
     * @param values The values to split
     * @param separator The separator, may be <code>null</code>
     * @return The array of values
     */
    private String[] split(String values, String separator) {
        String[] list = StringParsing.splitToArray(values, (separator == null
            || separator.length() == 0 ? INDEX_LIST_SEP_CHAR 
                : separator.charAt(0)),
            TestDataConstants.ESCAPE_CHAR_DEFAULT);
        return StringUtils.stripAll(list);
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
            String index = list[i];
            try {
                indices[i] = Integer.parseInt(index);
            } catch (NumberFormatException e) {
                throw new StepExecutionException(
                    "Index '" + index + "' is not an integer", //$NON-NLS-1$ //$NON-NLS-2$
                        EventFactory.createActionError(
                            TestErrorEvent.INVALID_INDEX));
            }
        }
        return indices;
    }
    
    /**
     * @return The array of selected indices
     * @throws StepExecutionException If there are no indices selected
     */
    private int[] getCheckedSelectedIndices() throws StepExecutionException {
        int[] selected = m_listHelper.getSelectedIndices(m_list);
        if (selected.length == 0) {
            throw new StepExecutionException("No list element selected", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NO_SELECTION));
        }
        return selected;
    }
    
    /**
     * @param indices The indices to select
     * @param co the click options to use for selecting items
     * @param isExtendSelection Whether this selection extends a previous 
     *                          selection. If <code>true</code>, the first 
     *                          element will be selected with CONTROL as a 
     *                          modifier.
     */
    private void selectIndices(int[] indices, ClickOptions co, 
            boolean isExtendSelection) {
       
        if (indices.length > 0) {
            try {
                if (isExtendSelection) {
                    getRobot().keyPress(null, SWT.MOD1);
                }

                // first selection
                m_listHelper.clickOnIndex(m_list, new Integer(indices[0]), 
                        co);

            } finally {
                if (isExtendSelection) {
                    getRobot().keyRelease(null, SWT.MOD1);
                }
            }
        }
        if (indices.length > 1) {
            try {
                getRobot().keyPress(null, SWT.MOD1);
                // folowing selections
                for (int i = 1; i < indices.length; i++) {
                    m_listHelper.clickOnIndex(m_list, new Integer(indices[i]), 
                            co);
                }
            } finally {
                getRobot().keyRelease(null, SWT.MOD1);
            }
        }
    }
    
    /**
     * Verifies if the passed index or enumeration of indices is selected. The
     * enumeration must be separated by <code>,</code>, e.g. <code>1,3,6</code>.
     * @param indexList The index or indices to verify
     * @param isSelected Whether the index or indices should be selected or not.
     */
    public void gdVerifySelectedIndex(String indexList, boolean isSelected) {
        int[] selected = getCheckedSelectedIndices();
        int[] expected = parseIndices(indexList);
        int[] implExpected = IndexConverter.toImplementationIndices(expected);
        if (isSelected) {
            Verifier.equals(implExpected.length, selected.length);
        }
        for (int i = 0; i < implExpected.length; i++) {
            int index = implExpected[i];
            Verifier.equals(index, selected[i], isSelected);
        }
    }
    
    /**
     * Verifies if the passed value or enumeration of values is selected. By
     * default, the enumeration separator is <code>,</code>
     * @param valueList The value or list of values to verify
     */
    public void gdVerifySelectedValue(String valueList) {
        gdVerifySelectedValue(valueList, MatchUtil.DEFAULT_OPERATOR, true);
    }
    
    /**
     * Verifies if the passed value or enumeration of values is selected. By
     * default, the enumeration separator is <code>,</code>, but may be
     * changed by <code>separator</code>.
     * @param valueList The value or list of values to verify
     * @param operator
     *            The operator to use when comparing the expected and 
     *            actual values.
     * @param isSelected if the value(s) should be selected or not.
     */
    public void gdVerifySelectedValue(String valueList, String operator,
            boolean isSelected) {
        
        gdVerifySelectedValue(valueList, operator, 
            String.valueOf(VALUE_SEPARATOR), isSelected);
        
    }
    
    /**
     * Verifies if the passed value or enumeration of values is selected. By
     * default, the enumeration separator is <code>,</code>, but may be
     * changed by <code>separator</code>.
     * @param valueList The value or list of values to verify
     * @param operator
     *            The operator to use when comparing the expected and 
     *            actual values.
     * @param separator The separator
     * @param isSelected if the value(s) should be selected or not.
     */
    public void gdVerifySelectedValue(String valueList, String operator, 
        String separator, boolean isSelected) {
        
        final String[] current = m_listHelper.getSelectedValues(m_list);
        final ListSelectionVerifier listSelVerifier = 
            new ListSelectionVerifier();
        for (int i = 0; i < current.length; i++) {
            listSelVerifier.addItem(i, current[i], true);
        }
        listSelVerifier.verifySelection(valueList, operator, !isSelected, 
                isSelected);
    }
    
    /**
     * Verifies if all selected elements of a list match a text.
     * @param text The text to verify
     * @param operator The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        String[] selected = m_listHelper.getSelectedValues(m_list);
        final int selCount = selected.length;
        if (selCount < 1) {
            throw new StepExecutionException("No selection", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NO_SELECTION));
        }
        for (int i = 0; i < selCount; i++) {
            Verifier.match(selected[i], text, operator);
        }
    }
    
    /**
     * Selects the passed index or enumeration of indices. The enumeration must
     * be separated by <code>,</code>, e.g. <code>1,3,6</code>.
     * @param indexList The index or indices to select
     * @param extendSelection Whether this selection extends a previous 
     *                        selection.
     * @param button what mouse button should be used
     */
    public void gdSelectIndex(String indexList, final String extendSelection,
            int button) {
        final boolean isExtendSelection = 
            extendSelection.equals(CompSystemConstants.EXTEND_SELECTION_YES);
        selectIndices(IndexConverter.toImplementationIndices(parseIndices(
                indexList)), ClickOptions.create()
                    .setClickCount(1)
                    .setMouseButton(button), isExtendSelection);
    }
    
    /**
     * Selects the passed value or enumeration of values. By default, the
     * enumeration separator is <code>,</code>.
     * @param valueList The value or list of values to select
     * @param operator If regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @param extendSelection Whether this selection extends a previous 
     *                        selection.
     * @param button what mouse button should be used
     */
    public void gdSelectValue(String valueList, String operator, 
            String searchType, final String extendSelection, int button) {
        final boolean isExtendSelection = 
            extendSelection.equals(CompSystemConstants.EXTEND_SELECTION_YES);
        selectValue(valueList, String.valueOf(VALUE_SEPARATOR), operator, 
            searchType, ClickOptions.create()
                .setClickCount(1)
                .setMouseButton(button), isExtendSelection); 
    }
 
    /**
     * Selects the passed value or enumeration of values. By default, the
     * enumeration separator is <code>,</code>, but may be changed by
     * <code>separator</code>.
     * @param valueList The value or list of values to select
     * @param separator The separator, optional
     * @param operator If regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @param clickCount The number of times to click each given entry.
     * @param isExtendSelection Whether this selection extends a previous 
     *                          selection. If <code>true</code>, the first 
     *                          element will be selected with CONTROL as a 
     *                          modifier.
     */
    public void gdSelectValue(String valueList, String separator,
            String operator, String searchType, int clickCount,
            boolean isExtendSelection) {
        selectValue(valueList, separator, operator, searchType, ClickOptions
                .create().setClickCount(clickCount), isExtendSelection);
    }
    
    /**
     * Selects the passed value or enumeration of values. By default, the
     * enumeration separator is <code>,</code>, but may be changed by
     * <code>separator</code>.
     * @param valueList The value or list of values to select
     * @param separator The separator, optional
     * @param operator If regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @param co the click options to use
     * @param isExtendSelection Whether this selection extends a previous 
     *                          selection. If <code>true</code>, the first 
     *                          element will be selected with CONTROL as a 
     *                          modifier.
     */
    private void selectValue(String valueList, String separator,
            String operator, String searchType, ClickOptions co, 
            boolean isExtendSelection) {
        
        String[] values = parseValues(valueList, separator);

        selectValues(operator, searchType, co, values, isExtendSelection);
    }

    /**
     * @param operator If regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @param co the click options to use
     * @param values The values to select
     * @param isExtendSelection Whether this selection extends a previous 
     *                          selection. If <code>true</code>, the first 
     *                          element will be selected with CONTROL as a 
     *                          modifier.
     */
    private void selectValues(String operator, String searchType,
            ClickOptions co, String[] values, boolean isExtendSelection) {
        Integer[] indices = m_listHelper.findIndicesOfValues(m_list, values,
                operator, searchType);
        if (!operator.equals(MatchUtil.NOT_EQUALS) 
                && indices.length < values.length) {
            throw new StepExecutionException("One or more values not found of set: " //$NON-NLS-1$
                + Arrays.asList(values).toString(),
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        selectIndices(ArrayUtils.toPrimitive(indices), co, isExtendSelection);
    }

    /**
     * Parses the enumeration of values.
     * @param valueList The enumeration of values
     * @param separator The separator to use for splitting the string
     * @return The array of parsed values
     */
    private String[] parseValues(String valueList, String separator) {
        String[] values = null;
        if (StringConstants.EMPTY.equals(valueList)) {
            values = new String[1];
            values[0] = StringConstants.EMPTY;
        } else {
            values = split(valueList, separator);
        }
        return values;
    }
    
    /**
     * Verifies if the list contains an element that renderes <code>value</code>.
     * @param value The text to verify
     */
    public void gdVerifyContainsValue(String value) {
        Verifier.equals(true, m_listHelper.containsValue(m_list, value));
    }
    
    /**
     * Verifies if the list contains an element that renderes <code>value</code>.
     * @param value The text to verify
     * @param operator The operator used to verify
     * @param exists if the wanted value should exist or not.
     */
    public void gdVerifyContainsValue(String value, String operator, 
            boolean exists) {
        
        Verifier.equals(exists, m_listHelper.containsValue(m_list, value, 
            operator));
    }
    
    /**
     * Action to read the value of the current selected item of the JList
     * to store it in a variable in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        String[] selected = m_listHelper.getSelectedValues(m_list);
        if (selected.length > 0) {
            return selected[0];
        }
        throw new StepExecutionException("No list item selected", //$NON-NLS-1$
            EventFactory.createActionError(TestErrorEvent.NO_SELECTION));
    }
    
    /**
     * Drags the passed value.
     * 
     * @param mouseButton the mouseButton.
     * @param modifier the modifier.
     * @param value The value to drag
     * @param operator If regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     */
    public void gdDragValue(final int mouseButton, final String modifier, 
            String value, String operator, 
            String searchType) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        
        Integer [] indices = m_listHelper.findIndicesOfValues(
            m_list, new String [] {value}, operator, searchType);
        selectIndices(ArrayUtils.toPrimitive(indices), ClickOptions.create()
                .setClickCount(0), false);
    }
    
    /**
     * Drops onto the passed value.
     * 
     * @param value The value on which to drop
     * @param operator If regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     */
    public void gdDropValue(final String value, final String operator, 
            final String searchType, int delayBeforeDrop) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();
        
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        try {
            getEventThreadQueuer().invokeAndWait("gdDropValue", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                  
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());
                    shakeMouse();
                    // drop
                    // It is important to only take a single element.
                    // Otherwise, a deadlock will occur when trying to press and confirm
                    // CTRL.
                    Integer [] indices = m_listHelper.findIndicesOfValues(
                            m_list, new String [] {value}, operator, 
                            searchType);
                    selectIndices(ArrayUtils.toPrimitive(indices),
                                    ClickOptions.create().setClickCount(0),
                                    false);

                    return null;
                }            
            });

            waitBeforeDrop(delayBeforeDrop);
            
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
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
    public void gdDragIndex(final int mouseButton, final String modifier, 
            int index) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        
        selectIndices(
                new int [] {IndexConverter.toImplementationIndex(index)}, 
                ClickOptions.create().setClickCount(0), false);
    }
    
    /**
     * Drops onto the passed index.
     * 
     * @param index The index on which to drop
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     */
    public void gdDropIndex(final int index, int delayBeforeDrop) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();
        
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        try {
            // drag
            getEventThreadQueuer().invokeAndWait("gdDropIndex", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());
                    shakeMouse();
                    // drop
                    // It is important to only take a single element.
                    // Otherwise, a deadlock will occur when trying to press and 
                    // confirm CTRL.
                    final int implIndex = IndexConverter.toImplementationIndex(
                            index);
                    selectIndices(new int [] {implIndex}, ClickOptions.create()
                            .setClickCount(0), false);
                    return null;
                }
            });

            waitBeforeDrop(delayBeforeDrop);
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

}