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
package org.eclipse.jubula.toolkit.javafx.config;

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.toolkit.base.config.AbstractOSProcessAUTConfiguration;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public class JavaFXAUTConfiguration extends AbstractOSProcessAUTConfiguration {
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
     */
    public JavaFXAUTConfiguration(
            @Nullable String name, 
            @NonNull String autID,
            @NonNull String command, 
            @NonNull String workingDir,
            @Nullable String[] args, 
            @NonNull Locale locale) {
        super(name, autID, command, workingDir, args, locale);
        
        // Toolkit specific information
        add(ToolkitConstants.ATTR_TOOLKITID, CommandConstants.JAVAFX_TOOLKIT);
    }
}