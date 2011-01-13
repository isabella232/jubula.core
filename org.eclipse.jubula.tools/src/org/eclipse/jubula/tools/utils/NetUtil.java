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
package org.eclipse.jubula.tools.utils;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class contains net utility methods
 * 
 * @author BREDEX GmbH
 * @created 10.11.2009
 */
public final class NetUtil {
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(NetUtil.class);

    /** hide constructor */
    private NetUtil() {
    // nothing in here
    }

    /**
     * 
     * @return a free port on the local machine.
     */
    public static int getFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e) {
            LOG.error("Error occurred while searching for available port. Invalid port will be returned.", e); //$NON-NLS-1$
            return -1;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.error("Error occurred while searching for available port.", e); //$NON-NLS-1$
                }
            }
        }
    }
}
