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

/**
 * Interface for delegation of Combo Box actions.
 *
 * @author BREDEX GmbH
 * @created 02.04.2007
 */
public interface IComboBoxHelper {

    /**
     * Inputs <code>text</code> to <code>component</code>.<br> 
     * @param text the text to type
     * @param replace whether to replace the text or not
     * @throws StepExecutionException if an error occurs during typing <code>text</code>
     * @throws IllegalArgumentException if <code>component</code> or <code>text</code> are null
     */
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException;

    /**
     * Selects the specified item in the combobox.
     * @param values the values which should be (not) selected
     * @param operator if regular expressions are used
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @throws StepExecutionException if an error occurs during selecting the item
     * @throws IllegalArgumentException if <code>component</code> or <code>text</code> are null
     */
    public void select(final String[] values, String operator,
        final String searchType) throws StepExecutionException,
        IllegalArgumentException;

    /**
     * @return The currently selected index for the combo box, or -1 if no
     *          index is currently selected.
     */
    public int getSelectedIndex();

    /**
     * Selects the combobox element with the passed index. 
     * @param index The index to select
     */
    public void select(int index);

    /**
     * performs a <code>count</code> -click on the textfield. 
     * @param count the number of clicks
     */
    public void click(Integer count);

    /**
     * @param value The value to check
     * @param operator The operator used to verify
     * @return <code>true</code> if the combobox contains an element rendered with the passed value
     */
    public boolean containsValue(String value, String operator);

    /**
     * @param value The value to check
     * @return <code>true</code> if the combobox contains an element rendered with the passed value
     */
    public boolean containsValue(String value);

    /**
     * @return If the combobox is editable
     */
    public boolean isEditable();

    /**
     * @return The rendered string of the selected value or <code>null</code>, if no value is selected
     */
    public String getSelectedValue();

}