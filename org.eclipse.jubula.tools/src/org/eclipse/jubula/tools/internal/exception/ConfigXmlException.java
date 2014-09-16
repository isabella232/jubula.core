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
package org.eclipse.jubula.tools.internal.exception;

/**
 * exceptions concerning any problem with XML configuration files
 * 
 * @author BREDEX GmbH
 * @created 29.11.2005
 */
public class ConfigXmlException extends JBFatalAbortException {
    /**
     * public constructor
     * 
     * @param message
     *            the detailed message
     * @param id
     *            An ErrorMessage.ID. {@inheritDoc}
     */
    public ConfigXmlException(String message, Integer id) {
        super(message, id);
    }
}
