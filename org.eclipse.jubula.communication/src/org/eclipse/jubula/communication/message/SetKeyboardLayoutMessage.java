/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.message;

import java.util.Properties;

import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * Message sent to AUT Server to set the keyboard layout.
 * 
 * @author BREDEX GmbH
 * @created Aug 2, 2011
 */
public class SetKeyboardLayoutMessage extends Message {

    /** Static version */
    public static final double VERSION = 1.0;

    /** the layout to use */
    private Properties m_keyboardLayout;

    /**
     * No-args constructor for XStream.
     * @deprecated
     */
    public SetKeyboardLayoutMessage() {
    }
    
    /**
     * Constructor
     * 
     * @param layout The layout to use.
     */
    public SetKeyboardLayoutMessage(Properties layout) {
        m_keyboardLayout = layout;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.SET_KEYBOARD_LAYOUT_COMMAND;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public double getVersion() {
        return VERSION;
    }

    /**
     * 
     * @return the keyboard layout contained in the message.
     */
    public Properties getKeyboardLayout() {
        return m_keyboardLayout;
    }
}
