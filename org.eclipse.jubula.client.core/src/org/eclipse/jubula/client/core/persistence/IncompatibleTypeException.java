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
package org.eclipse.jubula.client.core.persistence;

import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.tools.exception.JBException;


/**
 * @author BREDEX GmbH
 * @created Jun 27, 2008
 */
public class IncompatibleTypeException extends JBException {
    
    /**
     * The IComponentNamePO
     */
    private IComponentNamePO m_compName = null;
    
    /**
     * Error details
     */
    private String[] m_errorMessageParams = null;
    
    /**
     * @param compName IComponentNamePO
     * @param message guess what...
     * @param id An ErrorMessage.ID.
     */
    public IncompatibleTypeException(IComponentNamePO compName, String message, 
            Integer id) {
        
       this(compName, message, id, null);
    }
    
    /**
     * @param compName IComponentNamePO
     * @param message guess what...
     * @param id An ErrorMessage.ID.
     * @param details the details of the ErrorMessage defined in ErrorMessage.ID
     */
    public IncompatibleTypeException(IComponentNamePO compName, String message, 
            Integer id, String[] details) {
        
        super(message, id);
        m_compName = compName;
        m_errorMessageParams = details;
    }

    /**
     * 
     * @return the error details
     */
    public String[] getErrorMessageParams() {
        return m_errorMessageParams;
    }

    /**
     * 
     * @return the IComponentNamePO
     */
    public IComponentNamePO getCompName() {
        return m_compName;
    }

}
