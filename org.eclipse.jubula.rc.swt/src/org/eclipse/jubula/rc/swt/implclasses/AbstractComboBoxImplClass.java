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

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.utils.StringParsing;


/**
 * Abstract implementation class for SWT components with "combo box"-like 
 * behavior (i.e. Combo and CCombo). All actions delegate to a concrete 
 * Combo Box helper.
 *
 * @author BREDEX GmbH
 * @created 02.04.2007
 */
public abstract class AbstractComboBoxImplClass 
    extends AbstractControlImplClass {

    /**
     * Gets the ComboBox helper.
     * @return The ComboBox helper
     */
    public abstract IComboBoxHelper getComboBoxHelper();
    
    /**
     * Verifies the editable property. 
     * @param editable The editable property to verify.
     */
    public void gdVerifyEditable(boolean editable) {
        Verifier.equals(editable, getComboBoxHelper().isEditable());
    }
    
    /**
     * Verifies if the combobox has <code>index</code> selected.
     * @param index The index to verify
     * @param isSelected If the index should be selected or not.
     */
    public void gdVerifySelectedIndex(String index, boolean isSelected) {
        int implIdx = IndexConverter.toImplementationIndex(
                IndexConverter.intValue(index));
        int actual = getComboBoxHelper().getSelectedIndex();
        Verifier.equals(implIdx, actual, isSelected);
    }
    
    /**
     * Verifies if the passed text is currently selected in the combobox.
     * @param text The text to verify.
     * @param operator The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        Verifier.match(getComboBoxHelper().getSelectedValue(), text, operator);
    }
    
    /**
     * Verifies if the passed text is currently selected in the combobox.
     * @param text The text to verify.
     */
    public void gdVerifyText(String text) {
        gdVerifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }
    
    /**
     * Checks if the component contains the specified text.
     * @param text check if this text is in the combobox
     */
    public void gdVerifyContainsValue(final String text) {
        Verifier.equals(true, getComboBoxHelper().containsValue(text));
    }
    
    /**
     * Verifies if the list contains an element that renderes <code>value</code>.
     * @param value The text to verify
     * @param operator The operator used to verify
     * @param exists If the value should exist or not.
     */
    public void gdVerifyContainsValue(String value, String operator, 
            boolean exists) {
        
        final boolean contains = getComboBoxHelper().containsValue(
                value, operator);
        Verifier.equals(exists, contains);
    }
    
    /**
     * Types <code>text</code> into the component. This replaces the shown content.
     * @param text the text to type in
     */
    public void gdReplaceText(String text) {
        getComboBoxHelper().input(text, true);
    }
    
    /**
     * Sets the text into the combobox, by typing it into the textfield
     * 
     * @param text the text which should be typed in.
     */
    public void gdInputText(String text) {
        getComboBoxHelper().input(text, false);
    }
    
    /**
     * Selects <code>index</code> in the combobox. 
     * @param index The index to select
     */
    public void gdSelectIndex(String index) {
        int implIdx = IndexConverter.toImplementationIndex(
                IndexConverter.intValue(index));
        try {
            getComboBoxHelper().select(implIdx);
        } catch (StepExecutionException e) {
            throw e;
        }
    }
    
    /**
     * Selects a value from the list of the combobox
     * @param valueList The value or list of values to (not)select
     * @param operator if regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     */
    public void gdSelectValue(String valueList, String operator, 
        String searchType) {
        gdSelectValue(valueList, String.valueOf(VALUE_SEPARATOR), operator, 
            searchType); 
    }
    
    /**
     * Action to read the value of a JComboBox to store it in a variable
     * in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        final String selectedValue = getComboBoxHelper().getSelectedValue();
        return selectedValue != null ? selectedValue : StringConstants.EMPTY;
    }

    /**
     * Selects a value from the list of the combobox 
     * @param valueList the item(s) which should be (not)selected.
     * @param separator The seperator if <code>text</code> is an enumeration of values. Not supported by this implementation class
     * @param operator if regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     */
    protected void gdSelectValue(String valueList, String separator, 
        String operator, String searchType) {
        String[] values = split(valueList, separator);
        try {
            getComboBoxHelper().select(values, operator, searchType);
        } catch (StepExecutionException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * Splits the enumeration of values.
     * 
     * @param values The values to split
     * @param separator The separator, may be <code>null</code>
     * @return The array of values
     */
    protected String[] split(String values, String separator) {
        String[] list = StringParsing.splitToArray(values, (separator == null
            || separator.length() == 0 ? INDEX_LIST_SEP_CHAR 
                : separator.charAt(0)),
            TestDataConstants.ESCAPE_CHAR_DEFAULT);
        return list;
    }

}
