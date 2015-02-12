/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent;

import java.io.IOException;

import org.eclipse.jubula.autagent.AutStarter.Verbosity;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.utils.NetUtil;

/**
 * @author BREDEX GmbH
 */
public class EmbeddedAUTAgent {
    /** the embedded AUT Agent instance */
    private static AutStarter embedded = null;

    /** Constructor */
    private EmbeddedAUTAgent() {
        // hide
    }

    /**
     * @return a sharable instance of an embedded AUTAgent
     * @throws CommunicationException
     */
    public static AUTAgent instance() throws CommunicationException {
        int port = -1;
        if (embedded == null) {
            port = NetUtil.getFreePort();
            embedded = AutStarter.getInstance();
            try {
                embedded.start(port, false, Verbosity.QUIET, false);
            } catch (JBVersionException | IOException e) {
                throw new CommunicationException(e);
            }
        } else {
            port = embedded.getAgent().getPort();
        }
        AUTAgent agent = MakeR.createAUTAgent(EnvConstants.LOCALHOST_ALIAS,
                port);
        return agent;
    }
}