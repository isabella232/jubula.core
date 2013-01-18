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
package org.eclipse.jubula.rc.common.uiadapter.interfaces;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * This interface holds all methods which are needed to test
 * ComboBox like components.
 * @author BREDEX GmbH
 *
 */
public interface IComboBoxAdapter extends ITextVerifiable {
    /**
     * @return the value if the Text Component is editable
     */
    boolean isEditable();

    /**
     * @param value The value to check
     * @param operator The operator used to verify
     * @return <code>true</code> if the combobox contains an element rendered with the passed value
     */
    public boolean containsValue(String value, String operator);
    
    /**
     * select the whole text of  the textfield by clicking three times.
     */
    void selectAll();
    
    
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
     * Selects the specified item in the combobox.
     * 
     * @param value
     *            the value which should be selected
     * @param operator
     *            if regular expressions are used
     * @param searchType
     *            Determines where the search begins ("relative" or "absolute")
     * @throws StepExecutionException
     *             if an error occurs during selecting the item
     * @throws IllegalArgumentException
     *             if <code>component</code> or <code>text</code> are null
     */
    public void select(final String value, String operator, String searchType)
        throws StepExecutionException, IllegalArgumentException; 
    
    /**
     * Inputs <code>text</code> to <code>component</code>.<br> 
     * @param text the text to type
     * @param replace whether to replace the text or not
     * @throws StepExecutionException if an error occurs during typing <code>text</code>
     * @throws IllegalArgumentException if <code>component</code> or <code>text</code> are null
     */
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException;
}
