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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swing.swing.interfaces.IJLabelImplClass;


/**
 * The implementation class for <code>JLabel</code>.
 *
 * @author BREDEX GmbH
 * @created 13.09.2004
 */
public class JLabelImplClass extends AbstractSwingImplClass 
    implements IJLabelImplClass {

    /** the JLabel from the AUT */
    private JLabel m_label;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_label = (JLabel)graphicsComponent;
    }
    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_label;
    }
    /**
     * Verifies if the label shows the passed text.
     *
     * @param text
     *            The text to verify.
     * @param operator
     *            The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        String actual = (String)getEventThreadQueuer().invokeAndWait(
            "getText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return m_label.getText();
                }
            });
        Verifier.match(actual, text, operator);
    }
    /**
     * Verifies if the label shows the passed text.
     *
     * @param text
     *            The text to verify.
     */
    public void gdVerifyText(String text) {
        gdVerifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }

    /**
     * Action to read the value of a JLabel to store it in a variable
     * in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {getText()};
    }

    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return {@link JLabel#getText()}
     */
    protected String getText() {
        return m_label.getText();
    }

}
