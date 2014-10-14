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

import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/** @author BREDEX GmbH */
public abstract class AbstractAUTConfiguration implements AUTConfiguration {
    /** the name */
    private String m_name;
    /** the autID */
    private AutIdentifier m_autID;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     */
    public AbstractAUTConfiguration(String name, String autID) {
        setName(name);
        setAutID(autID);
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name
     *            the name to set
     */
    private void setName(String name) {
        m_name = name;
    }

    /**
     * @return the autID
     */
    public AutIdentifier getAutID() {
        return m_autID;
    }

    /**
     * @param autID
     *            the autID to set
     */
    private void setAutID(String autID) {
        m_autID = new AutIdentifier(autID);
    }
}