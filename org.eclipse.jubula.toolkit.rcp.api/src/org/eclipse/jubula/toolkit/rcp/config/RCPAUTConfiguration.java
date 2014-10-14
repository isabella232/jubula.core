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
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.RcpAccessorConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

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
     * @param workingDir
     *            the working directory
     * @param args
     *            the arguments
     * @param locale
     *            the AUT locale to use
     * @param keyboardLayout
     *            the keyboard layout to use
     */
    public RCPAUTConfiguration(String name, String autID,
        String command, String workingDir, String[] args,
        Locale locale, Locale keyboardLayout) {
        super(name, autID, command, workingDir, args, locale, keyboardLayout);
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