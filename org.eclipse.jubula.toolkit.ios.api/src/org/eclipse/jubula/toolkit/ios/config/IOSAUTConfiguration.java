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
package org.eclipse.jubula.toolkit.ios.config;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.toolkit.base.config.AbstractOSAUTConfiguration;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public class IOSAUTConfiguration extends AbstractOSAUTConfiguration {
    /** the remote host name */
    @NonNull private String m_remoteHostName;
    /** the remote port number */
    private int m_remotePort;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param workingDir
     *            the working directory
     * @param remoteHostName
     *            the remote device host name to connect to
     * @param remoteHostPort
     *            the remote device port number to connect to
     */
    public IOSAUTConfiguration(
            @Nullable String name, 
            @NonNull String autID,
            @NonNull String workingDir,
            @NonNull String remoteHostName,
            int remoteHostPort) {
        super(name, autID, workingDir);
        
        Validate.notNull(remoteHostName, "The remote host name must not be null"); //$NON-NLS-1$
        m_remoteHostName = remoteHostName;

        m_remotePort = remoteHostPort;
        
        // Toolkit specific information
        add(AutConfigConstants.AUT_HOST, remoteHostName);
        add(AutConfigConstants.AUT_HOST_PORT, String.valueOf(remoteHostPort));
        add(ToolkitConstants.ATTR_TOOLKITID, CommandConstants.IOS_TOOLKIT);
    }

    /**
     * @return the remoteHostName
     */
    public String getRemoteHostName() {
        return m_remoteHostName;
    }

    /**
     * @return the remotePort
     */
    public int getRemotePort() {
        return m_remotePort;
    }
}