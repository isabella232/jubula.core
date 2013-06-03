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

package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;

/**
 * Data class for storing the set of execution Test Cases.
 *
 * @author BREDEX GmbH
 */
public class ReplaceExecTestCaseData extends ChooseTestCaseData {

    /** The parameter description map between new as key and old as value. */
    private Map<IParamDescriptionPO, IParamDescriptionPO> m_mapParams;

    /**
     * @param execTestCases The set of execution Test Cases, for which the
     *                      usage of the specification Test Case has to changed.
     */
    public ReplaceExecTestCaseData(Set<IExecTestCasePO> execTestCases) {
        super(execTestCases);
    }

    /**
     * @param newParamDesc The new parameter description.
     * @return An array of strings containing all parameter names from the old
     *         specification Test Case with the same type as the given parameter description.
     */
    public List<String> getOldParameterNamesByType(
            IParamDescriptionPO newParamDesc) {
        List<String> matchingNames = new ArrayList<String>();
        for (IParamDescriptionPO oldParamDesc: getOldSpecTestCase()
                .getParameterList()) {
            if (newParamDesc.getType().equals(oldParamDesc.getType())) {
                matchingNames.add(oldParamDesc.getName());
            }
        }
        return matchingNames;
    }

    /**
     * @param mapParams The parameter map between new as key and old as value.
     */
    public void setMapParams(Map<IParamDescriptionPO, IParamDescriptionPO>
            mapParams) {
        this.m_mapParams = mapParams;
    }

    /**
     * @return The parameter map between new as key and old as value.
     */
    public Map<IParamDescriptionPO, IParamDescriptionPO> getMapParams() {
        return m_mapParams;
    }

}
