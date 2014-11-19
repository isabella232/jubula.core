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

/**
 *  @created 19.11.2014
 */
public class RuntimeContext {
    
    /** the AUT */
    private AUT m_aut;
    
    /**
     * @param aut the AUT
     */
    public RuntimeContext(AUT aut) {
        m_aut = aut;
    }
    
    /**
     * @return the AUT
     */
    public AUT getAUT() {
        return m_aut;
    }
    
    /**
     * Changes the AUT during runtime
     * @param aut the AUT to set
     */
    public void setAUT(AUT aut) {
        m_aut = aut;
    }
}
