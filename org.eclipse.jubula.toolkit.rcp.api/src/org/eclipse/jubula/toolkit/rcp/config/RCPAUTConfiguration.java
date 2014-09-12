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
package org.eclipse.jubula.toolkit.rcp.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.toolkit.swt.config.SWTAUTConfiguration;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.constants.RcpAccessorConstants;
import org.eclipse.jubula.tools.constants.ToolkitConstants;
import org.eclipse.jubula.tools.registration.AutIdentifier;

/** @author BREDEX GmbH */
public class RCPAUTConfiguration extends SWTAUTConfiguration {
    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param command
     *            the command
     * @param wd
     *            the working directory
     * @param args
     *            the arguments
     * @param keyboardLayout
     *            the keyboard layout to use
     * @param locale
     *            the AUT locale to use
     */
    public RCPAUTConfiguration(String name, AutIdentifier autID,
        String command, String wd, String[] args, Locale keyboardLayout,
        Locale locale) {
        super(name, autID, command, wd, args, keyboardLayout, locale);
    }

    /** {@inheritDoc} */
    public Map<String, String> getLaunchInformation() {
        Map<String, String> launchInformation = new HashMap<String, String>();

        launchInformation.put(AutConfigConstants.AUT_ID, getAutID()
            .getExecutableName());
        launchInformation.put(AutConfigConstants.EXECUTABLE, getCommand());
        launchInformation.put(AutConfigConstants.WORKING_DIR, getWorkingDir());
        launchInformation.put(AutConfigConstants.AUT_ARGUMENTS,
            StringUtils.join(getArgs()));
        launchInformation.put(RcpAccessorConstants.KEYBOARD_LAYOUT,
            getKeyboardLayout().toString());

        launchInformation.put(AutConfigConstants.AUT_LOCALE,
            getLocale().toString());
        
        // Toolkit specific information
        launchInformation.put(ToolkitConstants.ATTR_TOOLKITID,
            CommandConstants.RCP_TOOLKIT);
        launchInformation.put(AutConfigConstants.NAME_TECHNICAL_COMPONENTS,
            Boolean.TRUE.toString());

        return launchInformation;
    }
}