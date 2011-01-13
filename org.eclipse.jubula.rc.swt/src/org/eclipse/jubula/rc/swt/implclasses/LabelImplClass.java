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
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swt.interfaces.ILabelImplClass;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;


/**
 * The implementation class for <code>SWT Label</code>.
 *
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public class LabelImplClass extends AbstractControlImplClass
    implements ILabelImplClass {
    
    /** the Label from the AUT */
    private Label m_label;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_label = (Label)graphicsComponent;
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_label;
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
                    try {
                        return SwtUtils.removeMnemonics(m_label.getText());
                    } catch (NullPointerException e) {
                        throw new StepExecutionException("component must not be null", //$NON-NLS-1$
                            EventFactory.createComponentNotFoundErrorEvent());
                    }
                }
            });
        Verifier.match(actual, text, operator);
    }
    
    /**
     * Verifies if the label shows the passed text.
     * @param text The text to verify.
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
        String text = (String)getEventThreadQueuer().invokeAndWait(
            "getText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return SwtUtils.removeMnemonics(m_label.getText());
                }
            });
        return text;
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {SwtUtils.removeMnemonics(m_label.getText())};
    }
}