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
package org.eclipse.jubula.toolkit.base.config;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public abstract class AbstractOSProcessAUTConfiguration extends
    AbstractOSAUTConfiguration {
    /** the command */
    @NonNull private String m_command;
    /** the args */
    @Nullable private String[] m_args;
    /** the locale */
    @NonNull private Locale m_locale;

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
     *            the working directory for the AUT process. If a relative path
     *            is given the base path is relative to the process working
     *            directory of the connected
     *            {@link org.eclipse.jubula.client.AUTAgent AUTAgent}
     * @param args
     *            the commands arguments
     * @param locale
     *            the locale to use for the AUT process
     */
    public AbstractOSProcessAUTConfiguration(
        @Nullable String name, 
        @NonNull String autID,
        @NonNull String command, 
        @NonNull String workingDir, 
        @Nullable String[] args, 
        @NonNull Locale locale) {
        super(name, autID, workingDir);
        
        Validate.notEmpty(command, "The given command must not be empty"); //$NON-NLS-1$
        m_command = command;
        
        m_args = args;

        Validate.notNull(locale, "The locale must not be null"); //$NON-NLS-1$
        m_locale = locale;

        add(AutConfigConstants.EXECUTABLE, getCommand());
        add(AutConfigConstants.WORKING_DIR, getWorkingDir());
        add(AutConfigConstants.AUT_ARGUMENTS,
                StringUtils.defaultString(StringUtils.join(getArgs(),
                        StringConstants.SPACE)));
        add(AutConfigConstants.AUT_LOCALE, getLocale().toString());
    }

    /**
     * @return the command
     */
    @NonNull public String getCommand() {
        return m_command;
    }

    /**
     * @return the args
     */
    @Nullable public String[] getArgs() {
        return m_args;
    }

    /**
     * @return the locale
     */
    @NonNull public Locale getLocale() {
        return m_locale;
    }
}