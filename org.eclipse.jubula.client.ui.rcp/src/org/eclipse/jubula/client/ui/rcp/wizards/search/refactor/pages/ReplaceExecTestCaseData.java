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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.swt.widgets.Combo;

/**
 * Data class for storing the set of execution Test Cases.
 *
 * @author BREDEX GmbH
 */
public class ReplaceExecTestCaseData extends ChooseTestCaseData {

    /** The parameter description map between new as key and old as value. */
    private Map<IParamDescriptionPO, IParamDescriptionPO> m_newOldParamMap;

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
     * @param oldParamNameCombos
     *          The list of combo boxes with the selected old parameter names.
     */
    public void setNewOldParameterNames(List<Combo> oldParamNameCombos) {
        ISpecTestCasePO newSpec = getNewSpecTestCase();
        Iterator<Combo> it = oldParamNameCombos.iterator();
        Map<IParamDescriptionPO, IParamDescriptionPO> newOldParamMap =
                new HashMap<IParamDescriptionPO, IParamDescriptionPO>();
        for (IParamDescriptionPO newParamDesc: newSpec.getParameterList()) {
            IParamDescriptionPO oldParamDesc = null;
            Combo combo = it.next();
            if (combo != null) {
                oldParamDesc = getOldSpecTestCase()
                        .getParameterForName(combo.getText());
            }
            newOldParamMap.put(newParamDesc, oldParamDesc);
        }
        this.m_newOldParamMap = newOldParamMap;
    }

    /**
     * @return The parameter map between new as key and old as value.
     */
    public Map<IParamDescriptionPO, IParamDescriptionPO> getMapParams() {
        return m_newOldParamMap;
    }

    /**
     * @return True, if the parameters from the new and old specification
     *         Test Case can not be matched directly, otherwise false.
     */
    public boolean hasMatchableParameters() {
        if (getNewSpecTestCase().getParameterListSize()
                == getOldSpecTestCase().getParameterListSize()) {
            return haveSameTypeCount();
        }
        return false;
    }

    /**
     * @return True, if the new and old Test Case have the same number of
     *         parameter types, otherwise false.
     */
    private boolean haveSameTypeCount() {
        // FIXME RB: not implemented yet
        return true;
    }

}
