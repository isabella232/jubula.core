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
package org.eclipse.jubula.rc.common.caps;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IComboBoxAdapter;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.utils.StringParsing;

/**
 * General implementation for ComboBoxes and ComboBox like components.
 * @author BREDEX GmbH
 *
 */
public class AbstractComboBoxCAPs extends AbstractTextInputSupport {
    
    /**
     * 
     * @return the <code>IComboBoxAdapter</code>
     */
    private IComboBoxAdapter getCBAdapter() {
        return (IComboBoxAdapter) getComponent();
    }
    
    
    /** {@inheritDoc} */
    public String[] getTextArrayFromComponent() {
        return new String [] {getText()};
    }

    /**
     * Verifies the editable property. 
     * @param editable The editable property to verify.
     */
    public void gdVerifyEditable(boolean editable) {
        Verifier.equals(editable, getCBAdapter().isEditable());
    }
    
    /**
     * Checks if the component contains the specified text.
     * @param text check if this text is in the combobox
     */
    public void gdVerifyContainsValue(final String text) {
        Verifier.equals(true, 
                getCBAdapter().containsValue(text, MatchUtil.EQUALS));
    }
    
    /**
     * Verifies if the list contains an element that renderes <code>value</code>.
     * @param value The text to verify
     * @param operator The operator used to verify
     * @param exists If the value should exist or not.
     */
    public void gdVerifyContainsValue(String value, String operator, 
            boolean exists) {        
        final boolean contains = getCBAdapter().containsValue(value, operator);
        Verifier.equals(exists, contains);
    }
    
    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return value from ComboBoxHelper
     */
    protected String getText() {
        return getCBAdapter().getText();
    }
    
    /**
     * {@inheritDoc}
     */
    public void gdInputText(String text) {        
        getCBAdapter().input(text, false);
    }
   
    /**
     * {@inheritDoc}
     */
    public void gdReplaceText(String text) throws StepExecutionException {
        getCBAdapter().input(text, true);
    }
    
    /**
     * Selects <code>index</code> in the combobox.
     *
     * @param index
     *            The index to select
     */
    public void gdSelectIndex(String index) {
        int implIdx = IndexConverter.toImplementationIndex(
                IndexConverter.intValue(index)); 
        getCBAdapter().select(implIdx);

    }
    
    /**
     * Selects a value from the list of the combobox
     * @param valueList The value or list of values to (not)select
     * @param operator if regular expressions are used
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void gdSelectValue(String valueList, String operator,
        final String searchType) {
        gdSelectValue(valueList, String.valueOf(VALUE_SEPARATOR), operator,
            searchType);
    }
    
    /**
     * Selects a value from the list of the combobox
     *
     * @param valueList
     *            the item(s) which should be (not)selected.
     * @param separator
     *            The separator if <code>text</code> is an enumeration of
     *            values. Not supported by this implementation class
     * @param operator
     *            if regular expressions are used
     * @param searchType
     *            Determines where the search begins ("relative" or "absolute")
     */
    private void gdSelectValue(String valueList, String separator,
        String operator, final String searchType) {
        String[] values = split(valueList, separator);
        getCBAdapter().select(values, operator, searchType);

    }

    /**
     * Verifies if the combobox has <code>index</code> selected.
     * @param index The index to verify
     * @param isSelected If the index should be selected or not.
     */
    public void gdVerifySelectedIndex(String index, boolean isSelected) {
        int implIdx = IndexConverter.toImplementationIndex(
                IndexConverter.intValue(index));
        int actual = getCBAdapter().getSelectedIndex();
        Verifier.equals(implIdx, actual, isSelected);
    }
    
    /**
     * Verifies if the passed text is currently selected in the combobox.
     * @param text The text to verify.
     */
    public void gdVerifyText(String text) {
        gdVerifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }
    
    /**
     * Splits the enumeration of values.
     *
     * @param values
     *            The values to split
     * @param separator
     *            The separator, may be <code>null</code>
     * @return The array of values
     */
    protected String[] split(String values, String separator) {
        String[] list = StringParsing.splitToArray(values, ((separator == null)
            || (separator.length() == 0) ? INDEX_LIST_SEP_CHAR
                : separator.charAt(0)),
            TestDataConstants.ESCAPE_CHAR_DEFAULT);
        return list;
    }

}
