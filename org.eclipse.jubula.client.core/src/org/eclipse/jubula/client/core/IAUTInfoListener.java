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
package org.eclipse.jubula.client.core;

/**
 * The interface to pass information about the AUT to interested listeners.
 * 
 * @author BREDEX GmbH
 * @created 04.10.2004
 */
public interface IAUTInfoListener {

    // constants to be used for error()

    /** a timeout has expired */
    public static final int ERROR_TIMEOUT = 1;

    /** a communication error has occured */
    public static final int ERROR_COMMUNICATION = 2;

    /**
     * This method will be called if an error has occured.
     * 
     * @param reason
     *            see constants
     */
    public void error(int reason);

}
