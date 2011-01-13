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
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.widgets.Button;


/**
 * A helper class to control default button operations.
 *
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public class ButtonHelper {
    /** The implementation class which uses this helper. */
    private AbstractControlImplClass m_implClass;
    
    /** The button to control. */
    private Button m_button;
    
    /**
     * The contructor. It expects that
     * {@link AbstractSwtImplClass#getComponent()} returns a component of type
     * <code>Button</code>.
     * @param implClass  The implementation class.
     */
    public ButtonHelper(AbstractControlImplClass implClass) {
        m_implClass = implClass;
        m_button = (Button)implClass.getComponent();
    }
    
    /**
     * Verifies the selected property.
     * @param selected The selected property value to verify.
     */
    public void verifySelected(boolean selected) {
        Boolean actual = (Boolean)m_implClass.getEventThreadQueuer()
            .invokeAndWait("isSelected", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return m_button.getSelection() 
                        ? Boolean.TRUE : Boolean.FALSE; // see findBugs;
                }
            });
        Verifier.equals(selected, actual.booleanValue());
    }
    
    /**
     * @return the text for this button.
     */
    public String getText() {
        String text = (String)m_implClass.getEventThreadQueuer()
            .invokeAndWait("getText", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    return SwtUtils.removeMnemonics(m_button.getText());
                }
            });
        
        return text;
    }

    /**
     * Verifies the passed text.
     * @param text The text to verify
     * @param operator an operator
     */
    public void verifyText(String text, String operator) {
        String value = getText();
        Verifier.match(value, text, operator);
    }
}