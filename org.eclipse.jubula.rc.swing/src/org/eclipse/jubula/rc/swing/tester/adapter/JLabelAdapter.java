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
package org.eclipse.jubula.rc.swing.tester.adapter;

import javax.swing.JLabel;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextVerifiable;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class JLabelAdapter extends WidgetAdapter implements ITextVerifiable {

    /**
     * 
     * @param objectToAdapt the component
     */
    public JLabelAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return ((JLabel) getRealComponent()).getText();
    }

}
