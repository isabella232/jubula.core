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

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.Verifier;


/**
 * A helper class to control default button operations.
 *
 * @author BREDEX GmbH
 * @created 17.08.2005
 */
public class AbstractButtonHelper {
    /**
     * The implementation class which uses this helper.
     */
    private AbstractSwingImplClass m_implClass;
    /**
     * The button to control.
     */
    private AbstractButton m_button;
    /**
     * The contructor. It expects that
     * {@link AbstractSwingImplClass#getComponent()} returns a component of type
     * <code>AbstractButton</code>.
     * 
     * @param implClass
     *            The implementation class.
     */
    public AbstractButtonHelper(AbstractSwingImplClass implClass) {
        m_implClass = implClass;
        m_button = (AbstractButton)implClass.getComponent();
    }
    /**
     * Verifies the selected property.
     * 
     * @param selected
     *            The selected property value to verify.
     */
    public void verifySelected(boolean selected) {
        Boolean actual = (Boolean)m_implClass.getEventThreadQueuer()
            .invokeAndWait("isSelected", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    // see findBugs
                    return m_button.isSelected() ? Boolean.TRUE : Boolean.FALSE;
                }
            });
        Verifier.equals(selected, actual.booleanValue());
    }
    /**
     * Verifies the passed text.
     * 
     * @param text
     *            The text to verify
     * @param operator
     *            an operator
     */
    public void verifyText(String text, String operator) {
        String value = (String)m_implClass.getEventThreadQueuer()
            .invokeAndWait("getText", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    return m_button.getText();
                }
            });
        Verifier.match(value, text, operator);
    }
}
