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
package org.eclipse.jubula.communication.message;

import org.eclipse.jubula.tools.constants.CommandConstants;

/**
 * This message is send from the JubulaClient to the AUTserver to start the AUT (invoking the main-method()). <br>
 * It contains no further data. The response message is a
 * <code>AUTStateMessage</code> containing information about the state of the AUT.
 * 
 * @author BREDEX GmbH
 * @created 23.07.2004
 */

/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 * 
 * @attribute System.Serializable()
 * */
public class AUTStartMessage extends Message {

    /**
     * transmitted version of this message.
     */
    private static final double VERSION = 1.0;

    /**
     * Default constructor.
     * Do nothing.
     */
    public AUTStartMessage() {
        super();
    }

    /**
     * Returns the name of the command class for this message
     * @return a <code>String</code> value
     */
    public String getCommandClass() {
        return CommandConstants.AUT_START_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * Returns the version of this message.
     * @return a <code>double</code> value.
     */
    public double getVersion() {
        return VERSION;
    }
}