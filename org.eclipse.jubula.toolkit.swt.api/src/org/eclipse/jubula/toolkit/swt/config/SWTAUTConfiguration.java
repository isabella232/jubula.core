/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.swt.config;

import java.util.Locale;

import org.eclipse.jubula.toolkit.base.config.AbstractOSProcessAUTConfiguration;

/** @author BREDEX GmbH */
public abstract class SWTAUTConfiguration extends
    AbstractOSProcessAUTConfiguration {
    /** the keyboardLayout */
    private Locale m_keyboardLayout;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param command
     *            the command
     * @param workingDir
     *            the working directory
     * @param args
     *            the arguments
     * @param locale
     *            the AUT locale to use
     * @param keyboardLayout
     *            the keyboard layout to use
     */
    public SWTAUTConfiguration(String name, String autID,
        String command, String workingDir, String[] args, Locale locale,
        Locale keyboardLayout) {
        super(name, autID, command, workingDir, args, locale);
        setKeyboardLayout(keyboardLayout);
    }

    /**
     * @return the keyboardLayout
     */
    public Locale getKeyboardLayout() {
        return m_keyboardLayout;
    }

    /**
     * @param keyboardLayout
     *            the keyboardLayout to set
     */
    private void setKeyboardLayout(Locale keyboardLayout) {
        m_keyboardLayout = keyboardLayout;
    }
}