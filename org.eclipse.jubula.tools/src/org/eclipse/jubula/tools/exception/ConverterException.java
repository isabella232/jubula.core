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
 * @author BREDEX GmbH
 * @created May 14, 2008
 */
public class ConverterException extends GDException {

    /**
     * @param message The detailed message for this exception.
     * @param id An ErrorMessage.ID.
     */
    public ConverterException(String message, Integer id) {
        super(message, id);
    }

    /**
     * @param message The detailed message for this exception.
     * @param cause The throwable object.
     * @param id An ErrorMessage.ID.
     */
    public ConverterException(String message, Throwable cause, Integer id) {
        super(message, cause, id);
    }

}
