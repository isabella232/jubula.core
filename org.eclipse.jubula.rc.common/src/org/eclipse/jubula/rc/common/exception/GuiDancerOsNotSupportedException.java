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
package org.eclipse.jubula.rc.common.exception;

import org.eclipse.jubula.tools.exception.GDException;

/**
 * Exception thrown when the current OS is not supported by GUIdancer.
 * @author BREDEX GmbH
 * @created 15.01.2007
 */
public class GuiDancerOsNotSupportedException extends GDException {

    /**
     * @param message The detailed message for this exception.
     * @param id An ErrorMessage.ID.
     */
    public GuiDancerOsNotSupportedException(String message, Integer id) {
        super(message, id);
    }
}