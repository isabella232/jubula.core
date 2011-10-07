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
package org.eclipse.jubula.client.ui.rcp.search.query;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestDataPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.rcp.search.data.AbstractSearchData.SearchableType;
import org.eclipse.jubula.client.ui.rcp.search.data.TestDataSearchData;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.TestDataCubeSearchResultElementAction;


/**
 * @author BREDEX GmbH
 * @created Aug 10, 2010
 */
public class TestDataSearchQuery extends AbstractSearchQuery {
    
    /**
     * Constructor
     * 
     * @param searchData
     *            the search data to use for this query
     */
    public TestDataSearchQuery(TestDataSearchData searchData) {
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
        Set<INodePO> setOfNodes = new HashSet<INodePO>();
        monitor.beginTask(
                "Collecting all Elements...", listOfTypesToSearchIn.size()); //$NON-NLS-1$
        Set<IParameterInterfacePO> centralTestData = 
            new HashSet<IParameterInterfacePO>();
        for (SearchableType type : listOfTypesToSearchIn) {
            Class searchType = type.getType();
            if (INodePO.class.isAssignableFrom(searchType)) {
                setOfNodes.addAll(NodeBP
                        .getAllNodesForGivenTypeInCurrentProject(searchType));
            } else if (ITestDataCubePO.class.isAssignableFrom(searchType)) {
                IProjectPO cProject = GeneralStorage.getInstance().getProject();
                centralTestData.addAll(cProject.getTestDataCubeCont()
                        .getTestDataCubeList());
            }
            monitor.worked(1);
        }

        monitor.beginTask(
                "Searching...", setOfNodes.size() + centralTestData.size()); //$NON-NLS-1$
        String searchString = getSearchData().getSearchString();
        Set<INodePO> nodeResultList = new HashSet<INodePO>();

        // Search in Nodes
        CollectionUtils.filter(setOfNodes,
                InstanceofPredicate.getInstance(IParamNodePO.class));
        for (INodePO node : setOfNodes) {
            IParamNodePO paramNode = (IParamNodePO)node;
            if (containsTestDataValue(paramNode, searchString, operation)) {
                nodeResultList.add(node);
            }
            monitor.worked(1);
        }

        List<SearchResultElement> results = getSearchResultList(nodeResultList,
                Constants.JB_DATASET_VIEW_ID);

        // Search in central test data sets
        for (IParameterInterfacePO testDataCube : centralTestData) {
            if (containsTestDataValue(testDataCube, searchString, operation)) {
                results.add(new SearchResultElement<Long>(testDataCube
                        .getName(), testDataCube.getId(),
                        getImageForNode(testDataCube),
                        new TestDataCubeSearchResultElementAction(),
                        null,
                        Constants.JB_DATASET_VIEW_ID));
            }
            monitor.worked(1);
        }

        setSearchResult(results);
        return Status.OK_STATUS;
    }
    
    /**
     * @param paramObj
     *            the param obj to search the test data value in
     * @param searchString
     *            the string to search in test data
     * @param operation
     *            the operation
     * @return true if value has been found for this node; false otherwise
     */
    private boolean containsTestDataValue(IParameterInterfacePO paramObj,
            String searchString, int operation) {
        Locale workingLanguage = WorkingLanguageBP.getInstance()
                .getWorkingLanguage();

        List<IParamDescriptionPO> usedParameters = paramObj.getParameterList();
        IParameterInterfacePO refDataCube = paramObj.getReferencedDataCube();
        ITDManager testDataManager = paramObj.getDataManager();
        for (IDataSetPO dataSet : testDataManager.getDataSets()) {
            for (IParamDescriptionPO paramDesc : usedParameters) {
                int column = testDataManager.findColumnForParam(paramDesc
                        .getUniqueId());

                if (refDataCube != null) {
                    IParamDescriptionPO dataCubeParam = refDataCube
                            .getParameterForName(paramDesc.getName());
                    if (dataCubeParam != null) {
                        column = testDataManager
                                .findColumnForParam(
                                        dataCubeParam.getUniqueId());
                    }
                }

                if (column != -1 && column < dataSet.getColumnCount()) {
                    ITestDataPO testData = dataSet.getColumn(column);
                    if (testData != null) {
                        ModelParamValueConverter converter = 
                            new ModelParamValueConverter(
                                testData, paramObj, workingLanguage, null);
                        String value = converter.getGuiString();
                        if (value != null
                                && compare(
                                        value, searchString, operation)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * @param searchData the searchData to set
     */
    private void setSearchData(TestDataSearchData searchData) {
        super.setSearchData(searchData);
    }

    /**
     * @return the searchData
     */
    public TestDataSearchData getSearchData() {
        return (TestDataSearchData)super.getSearchData();
    }
}
