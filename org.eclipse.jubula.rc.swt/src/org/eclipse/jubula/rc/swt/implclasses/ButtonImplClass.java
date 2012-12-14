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
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.swt.interfaces.IButton;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


/**
 * The implementation class for <code>SWTButton</code> and subclasses.
 *
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public class ButtonImplClass extends AbstractControlImplClass 
    implements IButton {
    
    /** the Button from the AUT */
    private Button m_button;
    
    /**
     * The helper to control default swt button operations.
     */
    private ButtonHelper m_buttonHelper;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_button = (Button)graphicsComponent;
        m_buttonHelper = new ButtonHelper(this);
    }
    
    /**
     * @return Returns the button.
     */
    public Control getComponent() {
        return m_button;
    }

    /**
     * Verifies the selected property.
     * @param selected The selected property value to verify.
     */
    public void gdVerifySelected(boolean selected) {
        m_buttonHelper.verifySelected(selected);
    }
    
    /**
     * Verifies the passed text.
     * @param text The text to verify
     * @param operator The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        m_buttonHelper.verifyText(text, operator);
    }
    
    /**
     * Verifies the passed text.
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
                Button btn = (Button)getComponent();
                // see findBugs
                return btn.isEnabled() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }
    
    /**
     * Action to read the value of a Button to store it in a variable in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        return m_buttonHelper.getText();
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {SwtUtils.removeMnemonics(m_button.getText())};
    }
}