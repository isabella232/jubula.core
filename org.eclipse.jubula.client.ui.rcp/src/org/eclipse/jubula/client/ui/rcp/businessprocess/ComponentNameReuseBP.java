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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.jubula.client.core.utils.ControlledCache;


/**
 * Caching mechanism for tracking whether or not Component Names are used in 
 * the current project.
 *
 * @author BREDEX GmbH
 * @created Mar 10, 2009
 */
public class ComponentNameReuseBP 
        implements IDataChangedListener {
    
    /** the single instance */
    private static ComponentNameReuseBP instance = null;
    
    /** mapping from Component Name GUID to reused status */
    private ControlledCache<String, Boolean> m_compNameGuidToIsReusedMap =
            new ControlledCache<String, Boolean>(
                    ControlledCache.ControlTypes.PROJECT_LOADED);

    /**
     * Adds this instance as a listener for data events.
     */
    private ComponentNameReuseBP() {
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addDataChangedListener(this, false);
    }

    /**
     * 
     * @param compNameGuid The GUID of the Component Name to check.
     * @return <code>true</code> if the Component Name with GUID 
     *         <code>compNameGuid</code> is used within 
     *         the currently opened Project. Otherwise, <code>false</code>.
     */
    public boolean isCompNameReused(String compNameGuid) {
        if (m_compNameGuidToIsReusedMap.get(compNameGuid) != null) {
            return m_compNameGuidToIsReusedMap.get(
                    compNameGuid).booleanValue();
        }
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        if (currentProject != null) {
            List<ISpecPersistable> specsToSearch = 
                currentProject.getSpecObjCont().getSpecObjList();
            Collection<IAUTMainPO> autsToSearch = 
                currentProject.getAutMainList();
            boolean isReused = ComponentNamesBP.getInstance().isCompNameReused(
                    specsToSearch, 
                    TestSuiteBP.getListOfTestSuites(currentProject), 
                    autsToSearch, compNameGuid);
            m_compNameGuidToIsReusedMap.add(compNameGuid, isReused);
            return isReused;
        }
        
        return false;
    }
    
    /**
     * 
     * @param searchedCompNameGuids The GUIDs of the Component Names to check.
     * @return the reused GUIDs of the used component names
     */
    public Set<String> getUsedCompNames(Set<String> searchedCompNameGuids) {
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        Set<String> usedComponents = new HashSet<>();
        if (currentProject != null) {
            List<ISpecPersistable> specsToSearch = currentProject
                    .getSpecObjCont().getSpecObjList();
            Collection<IAUTMainPO> autsToSearch = currentProject
                    .getAutMainList();

            boolean isAllSearchedCompNameCached = true;
            for (String compNameGuid : searchedCompNameGuids) {
                
                // Check the component name in the exists
                if (m_compNameGuidToIsReusedMap.get(compNameGuid) != null) {
                    if (m_compNameGuidToIsReusedMap.get(compNameGuid)
                            .booleanValue()) {
                        usedComponents.add(compNameGuid);
                        continue;
                    }
                } else {
                    // There is a searched component name, which indicates
                    // a new search is necessary
                    isAllSearchedCompNameCached = false;
                }

                // Check associations of the component names
                if (!ComponentNamesBP.getInstance()
                        .findAssocsOfReuse(autsToSearch, compNameGuid)
                        .isEmpty()) {
                    usedComponents.add(compNameGuid);
                }
            }
            
            if (isAllSearchedCompNameCached) {
                // All searched component is in the cache, no need 
                // to complete a new traversing on the tree
                for (String compNameGuid : m_compNameGuidToIsReusedMap
                        .getKeySet()) {
                    if (m_compNameGuidToIsReusedMap.get(compNameGuid)
                            .booleanValue()) {
                        usedComponents.add(compNameGuid);
                    }
                }
                return usedComponents;
            }
            
            usedComponents
                    .addAll(ComponentNamesBP.getInstance()
                            .getReusedCompNames(specsToSearch, TestSuiteBP
                                    .getListOfTestSuites(currentProject),
                            searchedCompNameGuids));
            
            // Put the checked component names to the cache
            for (String searchedCompNameGuid : searchedCompNameGuids) {
                m_compNameGuidToIsReusedMap.add(searchedCompNameGuid,
                        usedComponents.contains(searchedCompNameGuid));
            }
        }

        return usedComponents;
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        for (DataChangedEvent e : events) {
            if (e.getPo() instanceof IComponentNamePO) {
                IComponentNamePO compName = (IComponentNamePO)e.getPo();
                switch (e.getDataState()) {
                    case ReuseChanged:
                        m_compNameGuidToIsReusedMap.remove(compName.getGuid());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 
     * @return the single instance.
     */
    public static ComponentNameReuseBP getInstance() {
        if (instance == null) {
            instance = new ComponentNameReuseBP();
        }
        return instance;
    }
}
