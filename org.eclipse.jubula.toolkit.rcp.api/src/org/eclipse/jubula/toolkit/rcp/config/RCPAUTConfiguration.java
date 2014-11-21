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

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.toolkit.swt.config.SWTAUTConfiguration;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
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
    public RCPAUTConfiguration(
        @Nullable String name, 
        @NonNull String autID,
        @NonNull String command, 
        @NonNull String workingDir, 
        @Nullable String[] args,
        @NonNull Locale locale, 
        @NonNull Locale keyboardLayout) {
        super(name, autID, command, workingDir, args, locale, keyboardLayout);
        
        // Toolkit specific information
        getLaunchInformation().put(
                ToolkitConstants.ATTR_TOOLKITID,
            CommandConstants.RCP_TOOLKIT);
        
        getLaunchInformation().put(
                AutConfigConstants.NAME_TECHNICAL_COMPONENTS,
            Boolean.TRUE.toString());
    }
}