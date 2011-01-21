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
package org.eclipse.jubula.client.ui.search.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.search.result.BasicSearchResult.ObjectMappingSearchResultElementAction;
import org.eclipse.jubula.client.ui.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created Jul 27, 2010
 */
public class ShowWhereUsedComponentNameQuery 
    extends AbstractShowWhereUsedQuery {
    /**
     * <code>m_compName</code>
     */
    private IComponentNamePO m_compName;

    /**
     * @param compName the comp name to search
     */
    public ShowWhereUsedComponentNameQuery(IComponentNamePO compName) {
        setCompName(compName);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("nls")
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTimestamp());
        sb.append(StringConstants.COLON);
        sb.append(StringConstants.SPACE);
        sb.append(Messages.UIJobSearchingCompNames);
        sb.append(" \"");
        sb.append(getCompName().getName());
        sb.append("\"");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor) {
        calculateUseOfLogicalName(getCompName().getGuid(), monitor);
        return Status.OK_STATUS;
    }

    /**
     * shows all places, where a specified component name is used
     * 
     * @param logicalName
     *            String
     * @param monitor
     *            the progress monitor
     */
    protected void calculateUseOfLogicalName(String logicalName, 
        IProgressMonitor monitor) {
        Set<INodePO> reuse = findNodes(logicalName, monitor);
        final List<SearchResultElement> reuseLoc = 
            new ArrayList<SearchResultElement>(
                reuse.size());
        reuseLoc.addAll(getSearchResultList(reuse, null));
        Set<IObjectMappingAssoziationPO> reuseAssocs = 
            ComponentNamesBP.getInstance().findAssocsOfReuse(
                GeneralStorage.getInstance().getProject().getAutMainList(), 
                logicalName);
        for (IObjectMappingAssoziationPO assoc : reuseAssocs) {
            IAUTMainPO aut = null;
            IObjectMappingCategoryPO cat = assoc.getCategory();
            while (cat.getParent() != null) {
                cat = cat.getParent();
            }
            for (IAUTMainPO projAut : GeneralStorage.getInstance()
                    .getProject().getAutMainList()) {
                if (projAut.getObjMap().getMappedCategory().equals(cat)) {
                    aut = projAut;
                    break;
                }
            }
            StringBuilder assocSb = new StringBuilder();
            if (aut != null) {
                assocSb.append(aut.getName()).append(" / ");
            }
            assocSb.append(
                    assoc.getTechnicalName().getComponentNameToDisplay());
            reuseLoc.add(new SearchResultElement<Long>(assocSb.toString(), 
                    assoc.getId(), IconConstants.TECHNICAL_NAME_IMAGE,
                    new ObjectMappingSearchResultElementAction()));
        }
        
        setSearchResult(reuseLoc);
        monitor.done();
    }

    /**
     * Finds Nodes which make use of this component name
     * 
     * @param logicalName
     *            the logicalname of the component
     * @param monitor
     *            the progress monitor
     * @return a list of INodePOs which use this logical name
     */
    protected Set<INodePO> findNodes(String logicalName, 
        IProgressMonitor monitor) {
        Set<INodePO> reuse = new HashSet<INodePO>();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        reuse.addAll(ComponentNamesBP.getInstance().findNodesOfReuse(
                project.getSpecObjCont().getSpecObjList(),
                project.getTestSuiteCont().getTestSuiteList(), logicalName,
                monitor));
        return reuse;
    }
    
    /**
     * @param compName the compName to set
     */
    private void setCompName(IComponentNamePO compName) {
        m_compName = compName;
    }

    /**
     * @return the compName
     */
    protected IComponentNamePO getCompName() {
        return m_compName;
    }
}
