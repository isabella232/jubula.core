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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.model.IComponentNameData;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.rcp.utils.ComponentNameVisibility;
import org.eclipse.jubula.tools.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created Apr 7, 2010
 */
public class CompNamesProposalProvider implements IContentProposalProvider {
    /**
     * @author BREDEX GmbH
     * @created 16.08.2005
     */
    private class JBComparator implements Comparator<IComponentNameData> {

        /**
         * @param element0 element 0.
         * @param element1 element 1. 
         * @return a negative integer, zero, or a positive integer as the
         *         first argument is less than, equal to, or greater than the
         *         second. 
         */
        @SuppressWarnings("synthetic-access")
        public int compare(IComponentNameData element0, 
                IComponentNameData element1) {
            
            String name0 = element0.getName() == null ? StringConstants.EMPTY
                    : element0.getName();
            String name1 = element1.getName() == null ? StringConstants.EMPTY
                    : element1.getName();
            ComponentNameVisibility vis0 = 
                ComponentNameVisibility.getVisibility(
                        element0, m_compNameMapper.getCompNameCache());
            ComponentNameVisibility vis1 = 
                ComponentNameVisibility.getVisibility(
                        element1, m_compNameMapper.getCompNameCache()); 
            String type0 = element0.getComponentType() == null 
                ? StringConstants.EMPTY : element0.getComponentType();
            String type1 = element1.getComponentType() == null 
                ? StringConstants.EMPTY : element1.getComponentType();

            // Sorting:
            // 1st: visibility  (local --> global --> aut)
            // 2nd: types       (alphabetical componentTypes)
            // 3rd: names       (alphabetical componentNames)
            
            // matches the names in the types
            if (type0.equals(type1)) {
                return name0.toLowerCase().compareTo(name1.toLowerCase());
            }
            // matches the types in the catgories
            if (vis0.equals(vis1)) {
                StringHelper helper = StringHelper.getInstance();
                return helper.get(type0, true).compareTo(
                    helper.get(type1, true));
            }            
            // matches the categories
            return vis0.compareTo(vis1);
        }
    }

    /** used for looking up Component Names */
    private IComponentNameMapper m_compNameMapper;

    /** 
     * Component Type for which to provide proposals. Only Component Names 
     * with a compatible type will be proposed. This value is *not* set in the
     * constructor because it never has a meaningful value until after 
     * initialization is complete.
     */
    private String m_typeFilter = StringConstants.EMPTY;

    /**
     * Constructor
     * 
     * @param compNameMapper Used for looking up Component Names.
     */
    public CompNamesProposalProvider(IComponentNameMapper compNameMapper) {
        m_compNameMapper = compNameMapper;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("synthetic-access")
    public IContentProposal[] getProposals(final String contents,
            int position) {

        List<IComponentNameData> compNamesList = 
            new ArrayList<IComponentNameData>(
                    m_compNameMapper.getCompNameCache().getComponentNameData());
        final Long currentProjectId = 
            GeneralStorage.getInstance().getProject() != null 
                ? GeneralStorage.getInstance().getProject().getId() : null;
        final String subString;
        if (position == 0) {
            subString = contents;
        } else {
            subString = contents.substring(0, position);
        }
                
        CollectionUtils.filter(compNamesList, new Predicate() {

            public boolean evaluate(Object arg) {
                IComponentNameData elem = (IComponentNameData)arg;
                String item = elem.getName();
                boolean ok = !StringUtils.isEmpty(item) 
                    && item.startsWith(subString)
                    && (elem.getParentProjectId() == null
                           || elem.getParentProjectId().equals(
                                   currentProjectId))
                    && checkFilterInHierarchy(m_typeFilter, elem);
                return ok;
            }
        });
        if (!StringUtils.isEmpty(contents)) {
            Collections.sort(compNamesList, new JBComparator());
        }

        List<IContentProposal> proposals = 
            new ArrayList<IContentProposal>(compNamesList.size());
        

        for (IComponentNameData data : compNamesList) {
            proposals.add(new CompNamesProposal(data,
                    ComponentNameVisibility.getVisibility(data,
                            m_compNameMapper.getCompNameCache())));
        }
        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

    /**
     * Checks component hierachy with current filter.
     * @param filter the filter to check with
     * @param checkable the string to check
     * @return true, if given string is allowed
     */
    private boolean checkFilterInHierarchy(String filter, 
            IComponentNameData checkable) {

        return ComponentNamesBP.getInstance().isCompatible(
            filter, checkable.getName(), m_compNameMapper,
            GeneralStorage.getInstance().getProject().getId()) 
                == null;
        
    }

    /**
     * 
     * @param typeFilter The new filter to use. Only Component Names 
     *                   with a type that is compatible with the given type 
     *                   filter will be proposed. 
     */
    public void setTypeFilter(String typeFilter) {
        m_typeFilter = typeFilter;
    }

    /**
     * 
     * @param compNameMapper The new mapper to use.
     */
    public void setComponentNameMapper(IComponentNameMapper compNameMapper) {
        m_compNameMapper = compNameMapper;
    }
}
