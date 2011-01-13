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
package org.eclipse.jubula.communication.connection;

import org.eclipse.jubula.tools.exception.CommunicationException;

/**
 * @author BREDEX GmbH
 * @created Sep 7, 2009
 */
public class BroadcastException extends CommunicationException {

    /**
     * @param message .
     * @param cause .
     * @param id .
     * @see CommunicationException#CommunicationException(String, Throwable, Integer)
     */
    public BroadcastException(String message, Throwable cause, Integer id) {
        super(message, cause, id);        
    }
    

    /**
     * @param message .
     * @param id .
     * @see CommunicationException#CommunicationException(String, Integer)
     */
    public BroadcastException(String message, Integer id) {
        super(message, id);        
    }

}
