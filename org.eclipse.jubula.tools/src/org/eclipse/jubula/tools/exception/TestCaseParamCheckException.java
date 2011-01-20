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

import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * This exception class indicates all errors during test case parameter
 * checks.
 *
 * @author BREDEX GmbH
 * @created 25.08.2005
 */
public class TestCaseParamCheckException extends JBException {
    /**
     * @param message The message
     * @param id The message ID
     */
    public TestCaseParamCheckException(String message, Integer id) {
        super(message, id);
    }
    /**
     * @param message The message
     */
    public TestCaseParamCheckException(String message) {
        super(message, null);
    }
    /**
     * @return <code>true</code> if this exception has been initialized
     * with an error ID
     */
    public boolean isSystemLevelError() {
        return getErrorId() != null;
    }
    /**
     * Creates the error message depending on wether this exception has been
     * created with an error ID.
     * 
     * @return The message
     */
    public String createMessage() {
        return !isSystemLevelError() ? MessageIDs.getMessage(getErrorId())
            : getMessage();
    }
}
