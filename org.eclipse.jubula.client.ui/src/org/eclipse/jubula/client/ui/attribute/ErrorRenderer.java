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
package org.eclipse.jubula.client.ui.attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author BREDEX GmbH
 * @created 20.05.2008
 */
public class ErrorRenderer extends AbstractAttributeRenderer {

    /** The exception to render */
    private Throwable m_exception;

    /**
     * Constructor
     * 
     * @param e The exception to render.
     */
    public ErrorRenderer(Exception e) {
        m_exception = e;
    }
    
    /**
     * {@inheritDoc}
     */
    public void renderAttribute(Composite parent) {
        Text text = new Text(parent, SWT.BORDER);
        text.setText(m_exception.toString());
        text.setEditable(false);
    }

}
