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
package org.eclipse.jubula.toolkit.winapps.config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.toolkit.base.config.AbstractAUTConfiguration;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public class WinAppsAUTConfiguration extends AbstractAUTConfiguration {
    /** the app name */
    @NonNull private String m_appName;
    /** the app arguments */
    @Nullable private String[] m_args;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param appName
     *            the name of the win apps application to start 
     * @param args
     *            the arguments
     */
    public WinAppsAUTConfiguration(
            @Nullable String name, 
            @NonNull String autID,
            @NonNull String appName,
            @Nullable String[] args) {
        super(name, autID);
        
        Validate.notEmpty(appName, "The given appName must not be empty"); //$NON-NLS-1$
        m_appName = appName;

        m_args = args;
        
        // Toolkit specific information
        add(AutConfigConstants.APP_NAME, 
                m_appName);
        add(AutConfigConstants.AUT_ARGUMENTS,
                StringUtils.defaultString(StringUtils.join(getArgs(),
                        StringConstants.SPACE)));
        add(ToolkitConstants.ATTR_TOOLKITID, 
                CommandConstants.WIN__APPS_TOOLKIT);
    }

    /**
     * @return the appName
     */
    @NonNull public String getAppName() {
        return m_appName;
    }

    /**
     * @return the args
     */
    @Nullable public String[] getArgs() {
        return m_args;
    }
}