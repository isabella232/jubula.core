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

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestDataPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.tools.xml.businessmodell.Action;
import org.eclipse.jubula.tools.xml.businessmodell.Param;


/**
 * @author BREDEX GmbH
 * @created 21.12.2005
 */
public class TestDataBP {
    /** singleton instance */
    private static TestDataBP instance = null;

    /** constructor for singleton */
    private TestDataBP() {
    // nothing
    }

    /**
     * @return the only instance of this class
     */
    public static TestDataBP instance() {
        if (instance == null) {
            instance = new TestDataBP();
        }
        return instance;
    }

    /**
     * Creates a new test data instance without a reference or value set.
     * 
     * @return The new test data instance
     */
    public ITestDataPO createEmptyTestData() {
        return PoMaker.createTestDataPO();
    }
    
    /**
     * Checks if the given value is a value of the value set of the given CAP
     * of the given parameter description.
     * @param cap the cap
     * @param paramDesc The parameter description
     * @param paramValue the value to check
     * @return true if the value is in the value set, false otherwise
     */
    public boolean isValueSetParam(ICapPO cap, IParamDescriptionPO paramDesc, 
        String paramValue) {
        
        Action action = CapBP.getAction(cap);
        Param param = action.findParam(paramDesc.getUniqueId());
        return param.findValueSetElementByValue(paramValue) != null;
    }
    
    /**
     * Retrieves the Test Data for the given arguments.
     * 
     * @param paramNode The execution node for which the Test Data will be
     *                  retrieved.
     * @param testDataManager The data manager from which the Test Data will be
     *                        retrieved.
     * @param paramDesc The Parameter for which the Test Data will be retrieved.
     * @param dataSetNum The number (index) of the Data Set (within the given 
     *                   data manager) from which to retrieve the Test Data. 
     * @return the retrieved Test Data, or <code>null</code> if no such Test 
     *         Data exists.
     */
    public ITestDataPO getTestData(IParamNodePO paramNode, 
            ITDManager testDataManager, IParamDescriptionPO paramDesc,
            int dataSetNum) {
        IParameterInterfacePO refDataCube = paramNode.getReferencedDataCube();
        int column = 
            testDataManager.findColumnForParam(
                    paramDesc.getUniqueId());
        
        if (refDataCube != null) {
            // if referencing a Data Cube, then the Parameter needs to be
            // referenced (indirectly) by name
            IParamDescriptionPO dataCubeParam = 
                refDataCube.getParameterForName(
                    paramDesc.getName());
            if (dataCubeParam != null) {
                column = testDataManager.findColumnForParam(
                        dataCubeParam.getUniqueId());
            }
        }
 
        IDataSetPO dataSet = testDataManager.getDataSet(dataSetNum);
        if (column != -1 && column < dataSet.getColumnCount()) {
            return dataSet.getColumn(column);
        }
        
        return null;
    }
}
