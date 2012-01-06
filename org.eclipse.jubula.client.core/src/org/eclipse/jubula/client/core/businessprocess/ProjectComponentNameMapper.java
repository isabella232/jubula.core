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
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;


/**
 * Maps Component Names within the context of a Project.
 *
 * @author BREDEX GmbH
 * @created Feb 10, 2009
 */
public class ProjectComponentNameMapper extends AbstractComponentNameMapper {

    /**
     * Resets the GUID for reuse locations of certain Component Names to match
     * the GUID of the corresponding existing Component Names.
     *
     * @author BREDEX GmbH
     * @created Feb 10, 2009
     */
    private class ExistingCompTypeHandler 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        /** 
         * mapping from GUID of Component Names to save to GUID of existing 
         * Component Names with the same name 
         */
        private Map<String, String> m_guidToCompNameMap;
        
        /**
         * Constructor
         * 
         * @param guidToCompNameMap Mapping from GUID of Component Names to 
         *                          save to GUID of existing Component Names 
         *                          with the same name. 
         */
        public ExistingCompTypeHandler(Map<String, String> guidToCompNameMap) {
            m_guidToCompNameMap = guidToCompNameMap;
        }

        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {

            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTc = (IExecTestCasePO)node;
                for (ICompNamesPairPO pair : execTc.getCompNamesPairs()) {
                    if (m_guidToCompNameMap.containsKey(pair.getFirstName())) {
                        pair.setFirstName(
                            m_guidToCompNameMap.get(pair.getFirstName()));
                    }
                    if (m_guidToCompNameMap.containsKey(pair.getSecondName())) {
                        pair.setSecondName(
                            m_guidToCompNameMap.get(pair.getSecondName()));
                    }
                }
            } else if (node instanceof ICapPO) {
                ICapPO capPo = (ICapPO)node;
                if (m_guidToCompNameMap.containsKey(capPo.getComponentName())) {
                    capPo.setComponentName(
                        m_guidToCompNameMap.get(capPo.getComponentName()));
                }
            }
            return true;
        }
    }

    /**
     * Gathers all reuse types for a given Component Name.
     *
     * @author BREDEX GmbH
     * @created Feb 10, 2009
     */
    private static class ComponentTypeCollector 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** GUID of the Component Name for which to find the reuse types */
        private String m_compNameGuid;
        
        /** the cache to use for finding Component Names */
        private IWritableComponentNameCache m_compNameCache;
        
        /** the found types of reuse */
        private Set<String> m_typeSet = new HashSet<String>();
        
        /**
         * Constructor
         * 
         * @param componentNameGuid GUID of the Component Name for which to 
         *                          find the reuse types.
         * @param componentNameCache The cache to use for finding 
         *                           Component Names.
         */
        public ComponentTypeCollector(String componentNameGuid, 
                IWritableComponentNameCache componentNameCache) {
           
            m_compNameGuid = componentNameGuid;
            m_compNameCache = componentNameCache;
        }

        /**
         * @return the found reuse types.
         */
        public Set<String> getTypes() {
            return m_typeSet;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                INodePO parent, INodePO node, boolean alreadyVisited) {

            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTc = (IExecTestCasePO)node;
                for (ICompNamesPairPO pair : execTc.getCompNamesPairs()) {
                    if (m_compNameGuid.equals(pair.getSecondName())) {
                        m_typeSet.add(
                            m_compNameCache.getCompNamePo(
                                pair.getFirstName()).getComponentType());
                    }
                }
            } else if (node instanceof ICapPO) {
                ICapPO capPo = (ICapPO)node;
                if (m_compNameGuid.equals(capPo.getComponentName())) {
                    m_typeSet.add(capPo.getComponentType());
                }
            }
            return true;
        }
    }
    
    /**
     * Constructor
     * 
     * @param componentNameCache The cache for the Component Names.
     * @param project The Project to which Component Names using this
     *                mapper will be bound.
     */
    public ProjectComponentNameMapper(
            IWritableComponentNameCache componentNameCache, 
            IProjectPO project) {
        super(componentNameCache, project);
    }

    /**
     * {@inheritDoc}
     */
    protected IProjectPO getContext() {
        return (IProjectPO)super.getContext();
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<String> getUsedTypes(String compNameGuid) {
        ComponentTypeCollector operation = 
            new ComponentTypeCollector(compNameGuid, getCompNameCache());
        new TreeTraverser(getContext(), operation, true).traverse(true);
        Set<String> usedTypes = new HashSet<String>(operation.getTypes());
        
        for (IAUTMainPO aut : getContext().getAutMainList()) {
            for (IObjectMappingAssoziationPO assoc 
                    : aut.getObjMap().getMappings()) {
                
                if (assoc.getLogicalNames().contains(compNameGuid) 
                        && assoc.getTechnicalName() != null) {
                    
                    usedTypes.add(
                        assoc.getTechnicalName().getSupportedClassName());
                }
            }
        }
        
        return usedTypes;
    }

    /**
     * {@inheritDoc}
     */
    public void handleExistingNames(Map<String, String> guidToCompNameMap) {
        ExistingCompTypeHandler operation = 
            new ExistingCompTypeHandler(guidToCompNameMap);
        new TreeTraverser(getContext(), operation, true).traverse(true);
        for (IAUTMainPO aut : getContext().getAutMainList()) {
            for (IObjectMappingAssoziationPO assoc 
                    : aut.getObjMap().getMappings()) {
                
                Set<String> guidIntersection = new HashSet<String>();
                guidIntersection.retainAll(assoc.getLogicalNames());
                for (String guid : guidIntersection) {
                    assoc.removeLogicalName(guid);
                    assoc.addLogicalName(guidToCompNameMap.get(guid));
                }
            }
        }
        
    }

}
