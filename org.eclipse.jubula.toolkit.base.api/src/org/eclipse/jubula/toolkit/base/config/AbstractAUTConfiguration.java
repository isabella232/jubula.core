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
package org.eclipse.jubula.toolkit.base.config;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/** @author BREDEX GmbH */
public abstract class AbstractAUTConfiguration implements AUTConfiguration {
    /** the name */
    @Nullable private String m_name;
    /** the autID */
    @NonNull private AUTIdentifier m_autID;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     */
    public AbstractAUTConfiguration(
        @Nullable String name, 
        @NonNull String autID) {
        m_name = name;
        
        Validate.notEmpty(autID, "The AUT-Identifier must not be empty"); //$NON-NLS-1$
        m_autID = new AutIdentifier(autID);
    }

    /**
     * @return the name
     */
    @Nullable public String getName() {
        return m_name;
    }

    /**
     * @return the autID
     */
    @NonNull public AUTIdentifier getAutID() {
        return m_autID;
    }
}