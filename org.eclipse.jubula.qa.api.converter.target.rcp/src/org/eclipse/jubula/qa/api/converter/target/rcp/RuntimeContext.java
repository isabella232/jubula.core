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
package org.eclipse.jubula.qa.api.converter.target.rcp;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.tools.ComponentIdentifier;

/**
 * @created 19.11.2014
 */
public class RuntimeContext {
    
    /** the AUT */
    private AUT m_aut;
    
    /** the object map to use */
    private ObjectMapping om;

    /**
     * @param aut
     *            the AUT
     */
    public RuntimeContext(AUT aut) {
        setAUT(aut);
        
        // TODO: load OM for the AUT
    }

    /**
     * @return the AUT
     */
    public AUT getAUT() {
        return m_aut;
    }

    /**
     * @param aut the AUT to set
     */
    private void setAUT(AUT aut) {
        m_aut = aut;
    }

    /**
     * Gets a component identifier for a given logical component name 
     * from the object mapping for the AUT
     * @param name the logical component name
     * @return the component identifier
     */
    public ComponentIdentifier getIdentifier(String name) {
        return om.get(name);
    }
}