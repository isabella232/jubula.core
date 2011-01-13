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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.search.data.StructureSearchData;
import org.eclipse.jubula.client.ui.search.data.AbstractSearchData.SearchableType;


/**
 * @author BREDEX GmbH
 * @created Aug 9, 2010
 */
public class StructureSearchQuery extends AbstractSearchQuery {
    /**
     * Constructor
     * 
     * @param searchData
     *            the search data to use for this query
     */
    public StructureSearchQuery(StructureSearchData searchData) {
        setSearchData(searchData);
    }
    
    /**
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor) {
        int operation = 1;
        if (getSearchData().isUseRegex()) {
            operation = 3;
        } else if (getSearchData().isCaseSensitive()) {
            operation = 2;
        }
        List<SearchableType> listOfTypesToSearchIn = getSearchData()
                .getTypesToSearchIn();
        Set<INodePO> listOfNodes = new HashSet<INodePO>();
        monitor.beginTask(
                "Collecting all Elements...", listOfTypesToSearchIn.size()); //$NON-NLS-1$
        for (SearchableType type : listOfTypesToSearchIn) {
            listOfNodes.addAll(
                    NodeBP.getAllNodesForGivenTypeInCurrentProject(
                            type.getType()));
            monitor.worked(1);
        }

        monitor.beginTask("Searching...", listOfNodes.size()); //$NON-NLS-1$
        String searchString = getSearchData().getSearchString();
        Set<INodePO> resultList = new HashSet<INodePO>();
        for (INodePO node : listOfNodes) {
            if (compare(node.getName(), searchString, operation)) {
                resultList.add(node);
            }
            monitor.worked(1);
        }

        setSearchResult(getSearchResultList(resultList, null));
        return Status.OK_STATUS;
    }

    /**
     * @param searchData the searchData to set
     */
    private void setSearchData(StructureSearchData searchData) {
        super.setSearchData(searchData);
    }

    /**
     * @return the searchData
     */
    public StructureSearchData getSearchData() {
        return (StructureSearchData)super.getSearchData();
    }
}
