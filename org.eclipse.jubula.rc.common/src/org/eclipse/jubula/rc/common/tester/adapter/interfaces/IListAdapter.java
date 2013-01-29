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
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import org.eclipse.jubula.rc.common.driver.ClickOptions;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public interface IListAdapter extends ITextVerifiable {
    /**
     * 
     * @return The array of selected indices
     */
    public int[] getSelectedIndices();

    /**
     * Clicks on the index of the passed list.
     * 
     * @param i
     *            zero based index to click
     * @param co
     *            the click options to use for selecting an index item
     */
    public void clickOnIndex(Integer i, ClickOptions co);
    
    /**
     * @return The array of selected values as the renderer shows them
     */
    public String[] getSelectedValues();
    
    /**
     * 
     * @return the list items as String array
     */
    public String[] getValues();
}
