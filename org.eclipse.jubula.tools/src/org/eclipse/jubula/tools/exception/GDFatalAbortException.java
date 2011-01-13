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

/**
 * 
 * this exception signals, that GuiDancer is no longer fit for work
 * @author BREDEX GmbH
 * @created 19.08.2005
 *
 *
 *
 *
 */
public class GDFatalAbortException extends GDFatalException {

    /**
     * @param message detailed message for exception
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public GDFatalAbortException(String message, Integer id) {
        super(message, id);
    }

    /**
     * @param message detailed message for exception
     * @param cause reason
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public GDFatalAbortException(String message, Throwable cause, Integer id) {
        super(message, cause, id);
    }

    /**
     * @param cause reason
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public GDFatalAbortException(Throwable cause, Integer id) {
        super(cause, id);
    }
}