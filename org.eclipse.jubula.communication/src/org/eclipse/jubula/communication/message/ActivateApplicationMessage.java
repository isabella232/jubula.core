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

/**
 * @author BREDEX GmbH
 * @created 10.02.2006
 */
 
/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 * 
 * @attribute System.Serializable()
 */

public abstract class ActivateApplicationMessage extends Message {
    /** version */
    private static final double VERSION = 1.0;

    /**
     * {@inheritDoc}
     */
    public abstract String getCommandClass();
    
    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return VERSION;
    }
}