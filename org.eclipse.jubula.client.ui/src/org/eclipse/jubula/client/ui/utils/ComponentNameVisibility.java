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
package org.eclipse.jubula.client.ui.utils;

import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.model.IComponentNameData;



/**
 * @author BREDEX GmbH
 * @created Jan 17, 2007
 */
public enum ComponentNameVisibility {
    
        /** local test cases (for component names) */
        LOCAL, 
        /** global test cases (for component names)*/
        GLOBAL, 
        /** AUT test cases (for component names)*/
        AUT;
    
    /**
     * 
     * @param compNameData Component Name data.
     * @param compNameCache The context for the visibility check.
     * @return the visibility of the given component name data within the
     *         context of the given mapper.
     */
    public static final ComponentNameVisibility getVisibility(
            IComponentNameData compNameData, 
            IComponentNameCache compNameCache) {
     
        if (compNameCache.getLocalComponentNameData().contains(compNameData)) {
            return LOCAL;
        }
        
        switch (compNameData.getCreationContext()) {
            case OBJECT_MAPPING:
                return AUT;
            case OVERRIDDEN_NAME:
                // fall through
            case STEP:
                // fall through
            default:
                return GLOBAL;
        }
        
    }
}
