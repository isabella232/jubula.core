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
package org.eclipse.jubula.client.ui.provider.contentprovider.objectmapping;

import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;

/**
 * Tabular representation of a single Object Mapping. Several instances of this
 * class can be used in order to represent a complete Object Map.
 *
 * @author BREDEX GmbH
 * @created Oct 22, 2008
 */
public class ObjectMappingRow {

    /** constant for no Component Name associated with a row */
    public static final int NO_COMP_NAME = -1;
    
    /** 
     * the index of the logical name represented by this row in the 
     * corresponding Object Mapping association's logical names list 
     */
    private int m_logicalNameIndex;

    /** the association represented by this row */
    private IObjectMappingAssoziationPO m_assoc;

    /**
     * Constructor
     * 
     * @param assoc The association represented by this row.
     * @param logicalNameIndex The index of the logical name represented by
     *                         this row in the given Object Mapping 
     *                         association's logical names list.
     */
    public ObjectMappingRow(IObjectMappingAssoziationPO assoc, 
            int logicalNameIndex) {
        
        m_assoc = assoc;
        m_logicalNameIndex = logicalNameIndex;
    }
    
    /**
     * 
     * @return the association represented by this row.
     */
    public IObjectMappingAssoziationPO getAssociation() {
        return m_assoc;
    }

    /**
     * 
     * @return the index of the logical name represented by this row in the 
     *         given Object Mapping association's logical names list.
     */
    public int getLogicalNameIndex() {
        return m_logicalNameIndex;
    }
}
