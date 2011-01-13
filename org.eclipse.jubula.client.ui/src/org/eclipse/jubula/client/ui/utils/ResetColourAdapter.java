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
package org.eclipse.jubula.client.ui.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * utility class to reset the colour of view part frames
 *
 * @author BREDEX GmbH
 * @created 23.10.2006
 */
public class ResetColourAdapter {
    
    /** the eclipse default background color */
    private Color m_defaultBackgroundColor;
    
    /** parent of the invoking view part */
    private Composite m_parent;

    /**
     * constructor
     * @param parent parent of invoking view part
     */
    public ResetColourAdapter(Composite parent) {
        m_parent = parent;
        m_defaultBackgroundColor = parent.getBackground();
    }
    
    /**
     * reset the colour of view part frame
     */
    public void resetColouredFrame() {
        if (m_parent != null && !m_parent.isDisposed()) {
            m_parent.setBackground(m_defaultBackgroundColor);        
        }
    }
}