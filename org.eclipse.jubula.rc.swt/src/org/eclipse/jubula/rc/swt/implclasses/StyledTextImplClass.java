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
import org.eclipse.jubula.rc.swt.interfaces.IStyledText;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;


/**
 * Implementation class for swt styled text
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class StyledTextImplClass extends TextImplClass 
    implements IStyledText {

    /** the styled text from the AUT */
    private StyledText m_styledText;
    
    /**
     * @return The <code>swt Text</code> instance.
     */
    private StyledText getTextComponent() {
        return (StyledText)getComponent();
    }

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_styledText = (StyledText)graphicsComponent;
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_styledText;
    }
    
    /**
     * @return The text
     */
    protected String getText() {
        String actual = (String)getEventThreadQueuer().invokeAndWait(
            "getText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return getTextComponent().getText();
                }
            });
        return actual;
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean isEditable() {
        return ((Boolean)getEventThreadQueuer().invokeAndWait(
                "isEditable", //$NON-NLS-1$
                new IRunnable() {
                public Object run() {
                    return getTextComponent().getEditable() 
                        ? Boolean.TRUE : Boolean.FALSE; // see findBugs
                }
            })).booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    protected void selectAll() {
        getEventThreadQueuer().invokeAndWait("styledText.selectAll", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
                    getTextComponent().selectAll();
                    return null;
                }
            });

    }

    /**
     * {@inheritDoc}
     */
    protected void setSelection(final int start, final int end) {
        getEventThreadQueuer().invokeAndWait("setSelection", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
                    getTextComponent().setSelection(start, end);
                    return null;
                }
            });

    }

    /**
     * {@inheritDoc}
     */
    protected void setSelection(final int start) {
        getEventThreadQueuer().invokeAndWait("setSelection", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
                    getTextComponent().setSelection(start);
                    return null;
                }
            });

    }
    
}