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
package org.eclipse.jubula.client.ui.rcp.provider;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;


/**
 * HelperClass to decorate controls with control decorations
 *
 * @author BREDEX GmbH
 * @created May 5, 2009
 */
public class ControlDecorator {
    
    /**
     * Private constructor for utility class.
     */
    private ControlDecorator() {
        // nothing to initialize
    }
    
    /**
     * @param control that should be decorated with an info-bobble
     * @param position the position of the info-bobbles
     * @param descriptionkey the i18n-key of the info-bobbles text description
     * @param imageID the imageID for the image to show in this control decoration; 
     * see FieldDecorationRegistry constants for these ids
     * @param showOnFocus set to true shows the info-bobble only if control has focus,
     * avoid setting this parameter to true if the control can not gain any focus
     * e.g. SWT.NO_FOCUS
     */
    protected static void decorate(Control control, int position,  
            String descriptionkey, String imageID, Boolean showOnFocus) {
        ControlDecoration infoBobbles = 
            new ControlDecoration(control, position);
        infoBobbles.setDescriptionText(I18n.getString(descriptionkey));
        infoBobbles.setImage(FieldDecorationRegistry.getDefault()
                .getFieldDecoration(imageID).getImage());
        infoBobbles.setMarginWidth(2);
        infoBobbles.setShowOnlyOnFocus(showOnFocus);
    }
    
    /**
     * 
     * @param control that should be decorated with an info-bobble
     * @param descriptionkey the i18n-key of the info-bobbles text description
     * @param showfocus set to true shows the info-bobble only if control has focus,
     * avoid setting this parameter to true if the control can not gain any focus
     * e.g. SWT.NO_FOCUS
     */
    public static void decorateInfo(Control control, 
            String descriptionkey, Boolean showfocus) { 
        decorate(control, SWT.TRAIL, descriptionkey, 
                FieldDecorationRegistry.DEC_INFORMATION, showfocus);
    }

    /**
     * 
     * @param control contains the component which has an info-bobble
     * @param position the position of the info-bobbles
     * @param descriptionkey contains the text of the info-bobbles
     */
    public static void decorateWarning(Control control, int position, 
            String descriptionkey) {
        decorate(control, position, descriptionkey, 
                FieldDecorationRegistry.DEC_WARNING, false);
    }
}
