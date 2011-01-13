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

import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;


/**
 * @author BREDEX GmbH
 * @created 16.04.2007
 */
public class DropdownButtonImplClass extends ButtonImplClass {

    /**
     * Selects an item from the button's dropdown menu.
     * 
     * @param str The string representation of the item we want to select.
     */
    public void gdSelectFromDropdown(String str) {
        openDropdownMenu();

        int index = getIndexForEntry(str);
        
        selectFromDropdownByIndexImpl(index);
    }
    
    /**
     * @param str string
     * @return 0
     */
    private int getIndexForEntry(String str) {
        return 0;
    }

    /**
     * 
     * @param index The index of the item we want to select.
     */
    public void gdSelectFromDropdownByIndex(int index) {
        openDropdownMenu();
        selectFromDropdownByIndexImpl(index);
    }

    /**
     * Opens the dropdown menu for this component by clicking on its chevron
     * on the righthand side.
     */
    private void openDropdownMenu() {
        gdClickDirect(1, 1, 95, POS_UNI_PERCENT, 50, POS_UNI_PERCENT);
    }
    
    /**
     * 
     * @param index The index of the item we want to select.
     */
    private void selectFromDropdownByIndexImpl(int index) {
        // Get location to click (constraints)
        Menu dropdown = getDropdown();
        if (dropdown != null) {
            Rectangle constraints = 
                SwtUtils.getWidgetBounds(dropdown.getItem(index));
            getRobot().click(dropdown, constraints);

        } else {
            // FIXME zeb:
            // Throw StepExecution or Robot Exception: menu not visible
            System.out.println("null"); //$NON-NLS-1$
        }
        
    }
    
    /**
     * 
     * @return the dropdown menu for this button, or <code>null</code> if the 
     *         menu is not currently visible.
     */
    private Menu getDropdown() {
        // FIXME zeb: Right now we only check directly below the button.
        //            This means we will not find the dropdown if it appears
        //            somewhere else.
        
        return null;
    }
}
