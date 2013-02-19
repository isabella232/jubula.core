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
package org.eclipse.jubula.rc.swing.tester;

import org.eclipse.jubula.rc.swing.tester.adapter.WidgetAdapter;
/**
 * This is a adapter which wraps the graphics component (in this case the JSlider)
 * into a general form. This is needed for the TesterClasses which use the adapter
 * instead of the real graphics component.
 * 
 * In Swing we could extend the <code>WidgetAdapter</code> because all parents from
 * Swing types are from type <code>JComponent</code>.
 *  
 * @author BREDEX GmbH
 *
 */
public class JSliderAdapter extends WidgetAdapter {

    /**
     * This constructor uses his super constructor since we use an
     * existing adapter.
     * @param objectToAdapt the graphics component
     */
    public JSliderAdapter(Object objectToAdapt) {
        super(objectToAdapt);

    }

}
