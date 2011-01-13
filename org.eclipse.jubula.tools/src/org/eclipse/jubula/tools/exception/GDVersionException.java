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
package org.eclipse.jubula.tools.exception;

import java.util.List;

/**
 * This exception should be thrown if there is a version conflict between the
 * Client and AutStarter.
 *
 * @author BREDEX GmbH
 * @created 10.07.2006
 */
public class GDVersionException extends GDException {
    
    /** list with Strings of detailled errror messages */
    private List m_errorMsgs;

    /**
     * @param message
     * @param id
     * {@inheritDoc}
     */
    public GDVersionException(String message, Integer id) {
        super(message, id);
    }
    
    /**
     * @param msg log message
     * @param id An ErrorMessage.ID
     * @param errorMsgs detailed error messages
     */
    public GDVersionException(String msg, Integer id, 
        List errorMsgs) {
        super(msg, id);
        m_errorMsgs = errorMsgs;
    }

    /**
     * @param message
     * @param cause
     * @param id
     * {@inheritDoc}
     */
    public GDVersionException(String message, Throwable cause, Integer id) {
        super(message, cause, id);
    }
    
    /**
     * @return Returns the errorMsgs.
     */
    public List getErrorMsgs() {
        return m_errorMsgs;
    }

}
