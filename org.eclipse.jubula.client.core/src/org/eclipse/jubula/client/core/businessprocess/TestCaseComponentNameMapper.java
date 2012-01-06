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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;


/**
 * Maps Component Names within the context of a Test Case.
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2009
 */
public class TestCaseComponentNameMapper 
        extends AbstractComponentNameMapper {

    /**
     * Constructor
     * 
     * @param componentNameCache The cache for the Component Names.
     * @param specTestCase The Test Case to which Component Names using this
     *                     mapper will be bound.
     */
    public TestCaseComponentNameMapper(
            IWritableComponentNameCache componentNameCache, 
            ISpecTestCasePO specTestCase) {
        
        super(componentNameCache, specTestCase);
    }

    /**
     * {@inheritDoc}
     */
    protected ISpecTestCasePO getContext() {
        return (ISpecTestCasePO)super.getContext();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void handleExistingNames(
            Map<String, String> guidToCompNameMap) {
       
        Iterator iter = getContext().getNodeListIterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof IExecTestCasePO) {
                IExecTestCasePO execTestCase = (IExecTestCasePO)o;
                for (ICompNamesPairPO pair : execTestCase.getCompNamesPairs()) {
                    if (guidToCompNameMap.containsKey(pair.getSecondName())) {
                        pair.setSecondName(
                                guidToCompNameMap.get(
                                        pair.getSecondName()));
                    }
                }
            }
            if (o instanceof ICapPO) {
                ICapPO capPO = (ICapPO)o;
                if (guidToCompNameMap.containsKey(capPO.getComponentName())) {
                    capPO.setComponentName(
                            guidToCompNameMap.get(
                                    capPO.getComponentName()));
                }
            }
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<String> getUsedTypes(String compNameGuid) {
        Long currentProjectId = 
            GeneralStorage.getInstance().getProject().getId();
        Set<String> typeSet = new HashSet<String>();
        Set<Long> namePairIds = new HashSet<Long>();
        Set<Long> capIds = new HashSet<Long>();
        
        // Get reuse instance types from given test case
        Iterator iter = getContext().getNodeListIterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof IExecTestCasePO) {
                IExecTestCasePO execTestCase = (IExecTestCasePO)o;
                for (ICompNamesPairPO pair : execTestCase.getCompNamesPairs()) {
                    if (pair.getId() != null) {
                        namePairIds.add(pair.getId());
                    }
                    if (compNameGuid.equals(pair.getSecondName())) {
                        CompNamesBP.searchCompType(pair, execTestCase);
                        typeSet.add(pair.getType());
                    }
                }
            }
            if (o instanceof ICapPO) {
                ICapPO capPO = (ICapPO)o;
                if (capPO.getId() != null) {
                    capIds.add(capPO.getId());
                }
                if (compNameGuid.equals(capPO.getComponentName())) {
                    typeSet.add(capPO.getComponentType());
                }
            }
        }
        
        // Get reuse instance types from master session
        typeSet.addAll(
                CompNamePM.getReuseTypes(
                        GeneralStorage.getInstance().getMasterSession(), 
                        currentProjectId, compNameGuid, namePairIds, capIds,
                        new HashSet<Long>()));

        return typeSet;
    }

}
