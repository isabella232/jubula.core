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

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swing.swing.interfaces.IJComboBoxImplClass;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.StringParsing;


/**
 * The implementation class for <code>JComboBox</code>.
 *
 * @author BREDEX GmbH
 * @created 08.03.2005
 */
public class JComboBoxImplClass extends AbstractSwingImplClass 
    implements IJComboBoxImplClass {

    /**
     * <code>INVALID_MAX_WIDTH</code>
     */
    public static final int NO_MAX_WIDTH = -1;
    /**
     * The ComboBox helper this instance delegates to.
     */
    private JComboBoxHelper m_comboBoxHelper;
    /**
     * the combobox which should be inspected.
     */
    private JComboBox m_comboBox;


    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        this.m_comboBox = (JComboBox)graphicsComponent;
    }
    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_comboBox;
    }
    /**
     * Parses the index and returns an integer.
     *
     * @param index
     *            The index to parse
     * @return The integer value
     * @throws StepExecutionException
     *             If the index cannot be parsed
     */
    private int parseIndex(String index) throws StepExecutionException {
        try {
            return Integer.parseInt(index);
        } catch (NumberFormatException e) {
            throw new StepExecutionException("Index '" + index //$NON-NLS-1$
                + "' is not an integer", EventFactory.createActionError(//$NON-NLS-1$
                        TestErrorEvent.INVALID_INDEX));
        }
    }
    /**
     * Gets the ComboBox helper. The helper is created once per instance.
     *
     * @return The ComboBox helper
     */
    public JComboBoxHelper getComboBoxHelper() {
        if (m_comboBoxHelper == null) {
            m_comboBoxHelper = new JComboBoxHelper(this);
        }
        return m_comboBoxHelper;
    }
    /**
     * Verifies the editable property.
     *
     * @param editable
     *            The editable property to verify.
     */
    public void gdVerifyEditable(boolean editable) {
        Verifier.equals(editable, getComboBoxHelper().isEditable());
    }
    /**
     * Verifies if the combobox has <code>index</code> selected.
     *
     * @param index The index to verify
     * @param isSelected If the index should be selected or not.
     */
    public void gdVerifySelectedIndex(String index, boolean isSelected) {
        int implIdx = IndexConverter.toImplementationIndex(parseIndex(index));
        Integer actual = (Integer)getEventThreadQueuer()
            .invokeAndWait(
                JComboBoxImplClass.class.getName() + ".getSelectedIndex", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        return new Integer(m_comboBox.getSelectedIndex());
                    }
                });
        Verifier.equals(implIdx, actual.intValue(), isSelected);
    }
    /**
     * Verifies if the passed text is currently selected in the combobox.
     *
     * @param text
     *            The text to verify.
     * @param operator
     *            The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        Verifier.match(getComboBoxHelper().getSelectedValue(), text, operator);
    }
    /**
     * Verifies if the passed text is currently selected in the combobox.
     *
     * @param text
     *            The text to verify.
     */
    public void gdVerifyText(String text) {
        gdVerifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }

    /**
     * Checks if the component contains the specified text.
     *
     * @param text
     *            check if this text is in the combobox
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
     * Types <code>text</code> into the component. This replaces the shown
     * content.
     *
     * @param text
     *            the text to type in
     */
    public void gdReplaceText(String text) {
        getComboBoxHelper().input(text, true);
    }

    /**
     * Sets the text into the combobox, by typing it into the textfield
     *
     * @param text
     *            the text which should be typed in.
     */
    public void gdInputText(String text) {
        if (!hasFocus()) {
            getComboBoxHelper().click(new Integer(1));
        }
        getComboBoxHelper().input(text, false);
    }
    /**
     * Selects <code>index</code> in the combobox.
     *
     * @param index
     *            The index to select
     */
    public void gdSelectIndex(String index) {
        int implIdx = IndexConverter.toImplementationIndex(parseIndex(index));
        double maxWidth = getMaxWidth();
        try {
            getComboBoxHelper().select(implIdx, maxWidth);
        } catch (StepExecutionException e) {
            m_comboBox.hidePopup();
            throw e;
        }
    }


    /**
     * @return the maximal width for the selection; -1 if none available
     * e.g. the preferred width of the combo box itself is 100 pixel although
     * the preferred size of the embedded items is more than two times bigger
     * --> click outside of component (JList) #3013 
     */
    private double getMaxWidth() {
        double maxWidth = NO_MAX_WIDTH;
        Dimension d = getComponent().getPreferredSize();
        if (d != null) {
            maxWidth = d.getWidth();
        }
        return maxWidth;
    }
    /**
     * Selects a value from the list of the combobox
     *
     * @param valueList
     *            the item(s) which should be (not)selected.
     * @param separator
     *            The seperator if <code>text</code> is an enumeration of
     *            values. Not supported by this implementation class
     * @param operator
     *            if regular expressions are used
     * @param searchType
     *            Determines where the search begins ("relative" or "absolute")
     */
    private void gdSelectValue(String valueList, String separator,
        String operator, final String searchType) {
        String[] values = split(valueList, separator);
        double maxWidth = getMaxWidth();
        try {
            getComboBoxHelper().select(values, operator, searchType, maxWidth);
        } catch (StepExecutionException e) {
            m_comboBox.hidePopup();
            throw e;
        } catch (IllegalArgumentException e) {
            m_comboBox.hidePopup();
            throw e;
        }
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
    private String[] split(String values, String separator) {
        String[] list = StringParsing.splitToArray(values, ((separator == null)
            || (separator.length() == 0) ? INDEX_LIST_SEP_CHAR
                : separator.charAt(0)),
            TestDataConstants.ESCAPE_CHAR_DEFAULT);
        return list;
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
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return value from ComboBoxHelper
     */
    protected String getText() {
        return getComboBoxHelper().getText();
    }
    
    /**
     * Verifies if the component has the focus.
     *
     * @param hasFocus
     *            The hasFocus property to verify.
     */
    public void gdVerifyFocus(final boolean hasFocus) {
        verify(hasFocus, "hasFocus", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                if (m_comboBox.isEditable()) {
                    boolean editorFocus  = m_comboBox.getEditor()
                        .getEditorComponent().hasFocus();
                    if (editorFocus) {
                        return Boolean.TRUE;
                    }
                }                
                // see findBugs
                return getComponent().hasFocus() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }


}
