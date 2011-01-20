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
package org.eclipse.jubula.client.ui.businessprocess;

import org.eclipse.jubula.client.ui.widgets.CheckedText;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;


/**
 * @author BREDEX GmbH
 * @created 19.06.2006
 */
public class TextControlBP {

    /**
     * private utility contructor
     */
    private TextControlBP() {
        // do nothing
    }
    
    /**
     * @param text the text to set
     * @param control the control to set the text in
     */
    @SuppressWarnings("unchecked")
    public static void setText(String text, Control control) {
        if (control instanceof JBText) {
            ((JBText)control).setText(text);
        } else if (control instanceof DirectCombo) {
            ((DirectCombo<String>)control).setSelectedObject(text);
        } else if (control instanceof CCombo) {
            ((CCombo)control).setText(text);
        } else {
            throw new ClassCastException("not supported control"); //$NON-NLS-1$
        }
    }
    
    /**
     * gets the text of the current control
     * @param control the current control
     * @return the text of the given control
     */
    public static String getText(Control control) {
        if (control instanceof JBText) {
            return ((JBText)control).getText();
        } else if (control instanceof DirectCombo) {
            return ((DirectCombo)control).getText();
        } else if (control instanceof CCombo) {
            return ((CCombo)control).getText();
        }
        throw new ClassCastException("not supported control"); //$NON-NLS-1$
    }
    
    /**
     * selects the text of the GDText
     * @param control the current control
     */
    public static void selectAll(Control control) {
        if (control instanceof JBText) {
            ((JBText)control).selectAll();
        } else if (!(control instanceof DirectCombo 
                || control instanceof CCombo)) {
            
            throw new ClassCastException("not supported control"); //$NON-NLS-1$     
        }
    }
    
    /**
     * selects the text of the GDText
     * @param control the current control
     * @param index the index to set the selection
     */
    public static void setSelection(Control control, int index) {
        if (control instanceof JBText) {
            ((JBText)control).setSelection(index);
        } else if (!(control instanceof DirectCombo 
                || control instanceof CCombo)) {
            
            throw new ClassCastException("not supported control"); //$NON-NLS-1$     
        }
    }
    
    /**
     * Checks if the text of the given Control is valid.
     * If the given Control is an instance of {@link CheckedText}, the
     * isValid method will be called to validate. In case of all other
     * Controls, this methid returns true.
     * @param control a Control.
     * @return CheckedText#isValid() in case of control 
     * is an instance of CheckedText, true otherwise.
     */
    public static boolean isTextValid(Control control) {
        if (control instanceof CheckedText) {
            final CheckedText checkedText = (CheckedText)control;
            return checkedText.isValid();
        }
        return true;
    }
    
}