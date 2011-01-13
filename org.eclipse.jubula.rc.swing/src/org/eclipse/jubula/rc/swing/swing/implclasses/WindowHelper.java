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

import java.awt.KeyboardFocusManager;
import java.awt.Window;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * Utility methods
 *
 * @author BREDEX GmbH
 * @created Jun 21, 2007
 */
public class WindowHelper {

    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(WindowHelper.class);
    
    /**
     * Constructor
     */
    private WindowHelper() {
        // hidden utility Constructor
    }
    
    
    /**
     * 
     * @return The active application window, or <code>null</code> if no 
     *         application window is currently active. If the returned value
     *         is not <code>null</code>, it is guaranteed to be of type 
     *         <code>java.awt.Dialog</code> or <code>java.awt.Frame</code>.
     */
    public static Window getActiveWindow() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .getActiveWindow();
    }
    
}
