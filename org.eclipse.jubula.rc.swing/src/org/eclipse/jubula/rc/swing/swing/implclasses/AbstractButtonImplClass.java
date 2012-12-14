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

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.swing.swing.interfaces.IAbstractButton;


/**
 * The implementation class for <code>AbstractButton</code> and subclasses.
 * Note the "Abstract" in the class name implies only that this class can test 
 * <code>AbstractButton</code> components. The class itself should <em>NOT</em> 
 * be designated <code>abstract</code>, as this class is instantiated using 
 * reflection.
 *
 * @author BREDEX GmbH
 * @created 22.09.2004
 */
public class AbstractButtonImplClass extends AbstractSwingImplClass 
    implements IAbstractButton {

    /** the Button from the AUT */
    private AbstractButton m_button;
    /**
     * The helper to control default button operations.
     */
    private AbstractButtonHelper m_buttonHelper;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_button = (AbstractButton)graphicsComponent;
        m_buttonHelper = new AbstractButtonHelper(this);
    }
    /**
     * @return Returns the button.
     */
    public JComponent getComponent() {
        return m_button;
    }
    /**
     * Clicks the button <code>count</code> times.
     *
     * @param count The number of clicks
     */
    public void gdClick(int count) {
        gdClick(count, 1);
    }
    /**
     * Verifies the selected property.
     *
     * @param selected The selected property value to verify.
     */
    public void gdVerifySelected(boolean selected) {
        m_buttonHelper.verifySelected(selected);
    }
    /**
     * Verifies the passed text.
     *
     * @param text The text to verify
     * @param operator The RegEx operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        m_buttonHelper.verifyText(text, operator);
    }
    /**
     * Verifies the passed text.
     *
     * @param text The text to verify
     */
    public void gdVerifyText(String text) {
        gdVerifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }
    /**
     * {@inheritDoc}
     */
    public void gdVerifyEnabled(boolean enabled) {
        verify(enabled, "isEnabled", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                AbstractButton btn = (AbstractButton)getComponent();
                return btn.getModel().isEnabled()
                    ? Boolean.TRUE : Boolean.FALSE; // see findBugs
            }
        });
    }

    /**
     * Action to read the value of a JButton to store it in a variable
     * in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        return m_button.getText();
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {m_button.getText()};
    }

    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return {@link AbstractButton#getText()} value
     */
    protected String getText() {
        return m_button.getText();
    }
}