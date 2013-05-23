/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;

/**
 * A map between the parameter names and the set of corresponding execution Test Cases.
 * @author BREDEX GmbH
 */
public class ParameterNames {

    /** The map between the parameter description and the set of Test Cases. */
    private Map<IParamDescriptionPO, Set<ITestCasePO>> m_map;

    /** The old parameter description. */
    private IParamDescriptionPO m_oldParamDescription;

    /** The new parameter description. */
    private IParamDescriptionPO m_newParamDescription;

    /**
     * Creates a map between all parameter names and the corresponding
     * Test Cases given by the set of Test Cases.
     * @param testCases The set of execution and specification Test Cases.
     */
    public ParameterNames(Set<ITestCasePO> testCases) {
        m_map = new HashMap<IParamDescriptionPO, Set<ITestCasePO>>();
        for (ITestCasePO testCase : testCases) {
            for (IParamDescriptionPO param : testCase.getParameterList()) {
                Set<ITestCasePO> paramNametestCases =
                        m_map.get(findByNameAndType(param));
                if (paramNametestCases == null) {
                    // parameter description with the same name and type has not been found
                    paramNametestCases = new HashSet<ITestCasePO>();
                    // add new pair of parameter description and set of Test Cases
                    m_map.put(param, paramNametestCases);
                }
                // add execution Test Cases to the set corresponding to parameter description
                paramNametestCases.add(testCase);
            }
        }
    }

    /**
     * @param paramDesc The parameter description searching for.
     * @return The existing parameter description this map contains with
     *         the same name and type as the given parameter description,
     *         otherwise null.
     */
    private IParamDescriptionPO findByNameAndType(
            IParamDescriptionPO paramDesc) {
        for (IParamDescriptionPO existingParam : m_map.keySet()) {
            if (existingParam.getName().equals(paramDesc.getName())) {
                if (existingParam.getType().equals(paramDesc.getType())) {
                    return existingParam;
                }
            }
        }
        return null;
    }

    /**
     * @return An array of all parameter descriptions, which can be selected.
     */
    public Object[] getAllParamDescriptions() {
        return m_map.keySet().toArray();
    }

    /**
     * @param paramDesc The parameter description as a key for the map.
     * @return An array of Test Cases corresponding to the given parameter description.
     */
    public Object[] getTestCasesOfParamDescription(
            IParamDescriptionPO paramDesc) {
        return m_map.get(paramDesc).toArray();
    }

    /**
     * @return An array of all column names used in the Central Test Data Set.
     */
    public String[] getAllColumnNamesOfCTDS() {
        return getCTDS().getParamNames().toArray(new String[] {});
    }

    /**
     * @param paramDesc The old parameter description to set.
     * @return An array of column names used in the Central Test Data Set
     *         with the same type and a different parameter name than the
     *         given one. If no matching parameter description has been found,
     *         the array has the length of 0.
     */
    public String[] setOldParamDescription(IParamDescriptionPO paramDesc) {
        m_oldParamDescription = paramDesc;
        IParameterInterfacePO dataCube = getCTDS();
        List<String> columnNames = new ArrayList<String>();
        for (String paramName : dataCube.getParamNames()) {
            IParamDescriptionPO currentParamDesc =
                    dataCube.getParameterForName(paramName);
            if (!paramDesc.getName().equals(currentParamDesc.getName())
                    && paramDesc.getType().equals(currentParamDesc.getType())) {
                // current parameter has different name and same type
                columnNames.add(paramName);
            }
        }
        return columnNames.toArray(new String[columnNames.size()]);
    }

    /**
     * @return The set of Test Cases, which are currently selected,
     *         or null if nothing is selected.
     */
    public Set<ITestCasePO> getSelectedTestCases() {
        return m_map.get(m_oldParamDescription);
    }

    /**
     * @return The old parameter description.
     */
    public IParamDescriptionPO getOldParamDescription() {
        return m_oldParamDescription;
    }

    /**
     * @param paramName The new parameter name to set.
     */
    public void setNewParamName(String paramName) {
        m_newParamDescription = getCTDS().getParameterForName(paramName);
    }

    /**
     * @return The new parameter description.
     */
    public IParamDescriptionPO getNewParamDescription() {
        return m_newParamDescription;
    }

    /**
     * @return True, if the old and new parameter description have been set,
     *         otherwise false.
     */
    public boolean isComplete() {
        return m_oldParamDescription != null && m_newParamDescription != null;
    }

    /**
     * @return The Central Test Data Set.
     */
    private IParameterInterfacePO getCTDS() {
        ITestCasePO testCase = m_map.values()
                .iterator().next().iterator().next();
        IParameterInterfacePO dataCube = testCase.getReferencedDataCube();
        return dataCube;
    }

    /**
     * @param testCase The Test Case getting the new parameter from.
     * @return The list of new parameters for the given Test Case with the
     *         renamed new parameter.
     */
    public List<Parameter> getNewParametersFromSpecTestCase(
            ISpecTestCasePO testCase) {
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        for (IParamDescriptionPO param : testCase.getParameterList()) {
            Parameter newParam = new Parameter(param);
            if (param.getName().equals(m_oldParamDescription.getName())) {
                newParam.setName(m_newParamDescription.getName());
            }
            params.add(newParam);
        }
        return params;
    }

}
