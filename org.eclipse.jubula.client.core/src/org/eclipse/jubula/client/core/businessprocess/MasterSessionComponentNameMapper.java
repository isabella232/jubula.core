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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;


/**
 * Maps Component Names within the context of the Master Session.
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2009
 */
public class MasterSessionComponentNameMapper 
        implements IComponentNameMapper {

    /** the single instance */
    private static MasterSessionComponentNameMapper instance = null;
    
    /**
     * Private constructor for singleton.
     * 
     */
    private MasterSessionComponentNameMapper() {
        // Nothing to initialize
    }
    
    /**
     * 
     * @return the single instance of this class.
     */
    public static MasterSessionComponentNameMapper getInstance() {
        if (instance == null) {
            instance = new MasterSessionComponentNameMapper();
        }
        return instance;
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<String> getUsedTypes(String compNameGuid) {
        Long currentProjectId = 
            GeneralStorage.getInstance().getProject().getId();
        Set<String> typeSet = new HashSet<String>();
        Set<Long> emptySet = Collections.emptySet();
        
        // Get reuse instance types from master session
        typeSet.addAll(
                CompNamePM.getReuseTypes(
                        GeneralStorage.getInstance().getMasterSession(), 
                        currentProjectId, compNameGuid, emptySet, emptySet,
                        emptySet));

        return typeSet;
    }

    /**
     * {@inheritDoc}
     */
    public IComponentNameCache getCompNameCache() {
        return ComponentNamesBP.getInstance();
    }

}
