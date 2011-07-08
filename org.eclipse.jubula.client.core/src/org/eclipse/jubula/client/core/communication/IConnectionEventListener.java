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
package org.eclipse.jubula.client.core.communication;

import org.eclipse.jubula.communication.message.Message;

/**
 * An interface for notifying listeners that something happens to the
 * connection.
 * @author BREDEX GmbH
 * @created 22.07.2004
 */
public interface IConnectionEventListener {

    /**
     * the connection was lost
     * 
     * @param event -
     *            detailed information.
     */
    public void connectionLost(ConnectionEvent event);

    /**
     * the connection was closed regulary.
     * 
     * @param event -
     *            detailed information
     */
    public void connectionClosed(ConnectionEvent event);

    /**
     * the message could not send
     * @param message - the message, do not change!
     */
    public void sendFailed(Message message);
}
