/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client;

import java.util.Map;

import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/** @author BREDEX GmbH */
public interface AUT extends Remote {
    /**
     * @return the identifier of this AUT
     */
    AutIdentifier getIdentifier();

    /**
     * @param typeMapping
     *            the type mapping to set
     */
    void setTypeMapping(Map<?, ?> typeMapping);

    /**
     * @param cap
     *            the CAP to execute on the AUT
     */
    void execute(MessageCap cap);
}