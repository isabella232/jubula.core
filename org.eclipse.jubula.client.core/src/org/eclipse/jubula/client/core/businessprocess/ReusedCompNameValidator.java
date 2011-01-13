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
package org.eclipse.jubula.client.core.businessprocess;

import org.eclipse.jubula.client.core.persistence.GeneralStorage;


/**
 * Validates use of a Component Name (for a Test Step or as the second name of
 * a Component Names Pair) with regard to component type.
 *
 * @author BREDEX GmbH
 * @created Jan 30, 2009
 */
public class ReusedCompNameValidator implements ICompNameValidator {

    /** the component mapper to use for finding and modifying components */
    private IComponentNameMapper m_compMapper;
    
    /**
     * Constructor
     * 
     * @param compMapper The component mapper to use for finding and modifying 
     *                   Component Names.
     */
    public ReusedCompNameValidator(IComponentNameMapper compMapper) {
        m_compMapper = compMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    public String isValid(String type, String checkableName) {
        return ComponentNamesBP.getInstance().isCompatible(
                type, checkableName, m_compMapper,
                GeneralStorage.getInstance().getProject().getId());
    }

}
