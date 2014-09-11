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
package org.eclipse.jubula.tools.constants;


/**
 * @author BREDEX GmbH
 * @created 19.12.2006
 */
public abstract class RcpAccessorConstants {
    /** constant to set/get system property "GD_KEYBOARD_LAYOUT" */
    public static final String KEYBOARD_LAYOUT = "GD_KEYBOARD_LAYOUT";  //$NON-NLS-1$
    
    /** to prevent instantiation */
    private RcpAccessorConstants() {
        // do nothing
    }
}