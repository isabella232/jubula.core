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

/**
 * Interface for all necessary methods to test buttons.
 * It extends the <code>IWidgetAdapter</code> to add button specific methods.
 * 
 * 
 * @author BREDEX GmbH
 */

public interface IButtonAdapter extends ITextVerifiable {
    /**
     * Gets the text from the button
     * @return the text which is saved in the component
     */
    public String getText();

    /**
     * isSelected is mostly for RadioButtons and CheckBoxes
     * @return <code>true</code> if the component is selected
     */
    public boolean isSelected();
   
}