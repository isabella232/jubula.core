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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.xml.businessmodell.Component;


/**
 * Maps Component Names within the context of an AUT (and its corresponding
 * Object Mapping).
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2009
 */
public class ObjectMappingComponentNameMapper 
        extends AbstractComponentNameMapper {

    /**
     * Constructor
     * 
     * @param componentNameCache The cache for the Component Names.
     * @param aut The AUT (Object Mapping) to which Component Names using this
     *                     binding will be bound.
     */
    public ObjectMappingComponentNameMapper(
            IWritableComponentNameCache componentNameCache, IAUTMainPO aut) {
        super(componentNameCache, aut);
        // Get list of all GUIDs, which have ComponentNames
        Set <String> guids = new HashSet<String>();
        Set <IObjectMappingAssoziationPO> links = aut.getObjMap().getMappings();
        for (IObjectMappingAssoziationPO link : links) {
            guids.addAll(link.getLogicalNames());
        }
        // this might not be the best place to - do the preload.
        getCompNameCache().initCache(guids);
    }

    /**
     * {@inheritDoc}
     */
    protected IAUTMainPO getContext() {
        return (IAUTMainPO)super.getContext();
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleExistingNames(Map<String, String> guidToCompNameMap) {
        for (IObjectMappingAssoziationPO assoc 
                : getContext().getObjMap().getMappings()) {
                
            Set<String> guidIntersection = 
                new HashSet<String>(assoc.getLogicalNames());
            guidIntersection.retainAll(guidToCompNameMap.keySet());
            for (String guid : guidIntersection) {
                assoc.removeLogicalName(guid);
                assoc.addLogicalName(guidToCompNameMap.get(guid));
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Set<String> getUsedTypes(String compNameGuid) {
        Long currentProjectId = 
            GeneralStorage.getInstance().getProject().getId();
        Set<String> typeSet = new HashSet<String>();
        Set<Long> autIds = new HashSet<Long>();
        autIds.add(getContext().getId());
        
        // Get reuse instance types from given test case
        for (IObjectMappingAssoziationPO assoc 
                : getContext().getObjMap().getMappings()) {
                
            if (assoc.getLogicalNames().contains(compNameGuid)) {
                List<Component> availableComponents = 
                    ComponentBuilder.getInstance().getCompSystem()
                        .getComponents(getFilterToolkitId(), true);
                String impliedType = 
                    assoc.getComponentType(getCompNameCache(), 
                            availableComponents);
                if (impliedType != null) {
                    typeSet.add(impliedType);
                }
            }
        }
        
        // Get reuse instance types from master session
        typeSet.addAll(
                CompNamePM.getReuseTypes(
                        GeneralStorage.getInstance().getMasterSession(), 
                        currentProjectId, compNameGuid, 
                        new HashSet<Long>(), new HashSet<Long>(),
                        autIds));

        return typeSet;
    }

    /**
     * {@inheritDoc}
     */
    protected String getFilterToolkitId() {
        return getContext().getToolkit();
    }
}
