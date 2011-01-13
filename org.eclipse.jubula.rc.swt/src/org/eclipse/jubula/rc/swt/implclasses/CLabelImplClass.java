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

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swt.interfaces.ICLabelImplClass;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;


/**
 * Implementation class for swt CLabel
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class CLabelImplClass extends LabelImplClass
    implements ICLabelImplClass {

    /** the CLabel from the AUT */
    private CLabel m_clabel;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_clabel = (CLabel)graphicsComponent;
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_clabel;
    }
    
    /**
     * Verifies if the label shows the passed text.
     * @param text The text to verify.
     * @param operator The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        String actual = (String)getEventThreadQueuer().invokeAndWait(
            "getText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return SwtUtils.removeMnemonics(m_clabel.getText());
                }
            });
        Verifier.match(actual, text, operator);
    }
    
    /**
     * Action to read the value of a JLabel to store it in a variable
     * in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        String text = (String)getEventThreadQueuer().invokeAndWait(
            "getText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return SwtUtils.removeMnemonics(m_clabel.getText());
                }
            });
        return text;
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {SwtUtils.removeMnemonics(m_clabel.getText())};
    }
}