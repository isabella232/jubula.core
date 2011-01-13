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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.TestDataCubePM;


/**
 * Business logic operations for Test Data Cubes.
 *
 * @author BREDEX GmbH
 * @created Jul 12, 2010
 */
public class TestDataCubeBP {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestDataCubeBP() {
        // Nothing to initialize
    }
    
    /**
     * 
     * @param cubeName The name of the Test Data Cube to find. 
     *                 Must not be <code>null</code>
     * @param containingProject The Project in which to search for the 
     *                          Test Data Cube.
     * @return the Test Data Cube with the given name within the given project,
     *         or <code>null</code> if no such Data Cube could be found.
     */
    public static IParameterInterfacePO getTestDataCubeByName(String cubeName, 
            IProjectPO containingProject) {
        Validate.notNull(cubeName);
        Validate.notNull(containingProject);
        
        for (IParameterInterfacePO testDataCube 
                : containingProject.getTestDataCubeCont()
                    .getTestDataCubeList()) {
            
            if (cubeName.equals(testDataCube.getName())) {
                return testDataCube;
            }
        }
        
        return null;
    }

    /**
     * @param pio
     *            the param interface object to check for inner project reusage
     * @return true if the cube is reused
     */
    public static boolean isCubeReused(IParameterInterfacePO pio) {
        GeneralStorage gs = GeneralStorage.getInstance();
        return TestDataCubePM.computeReuser(
                pio, gs.getMasterSession()).size() > 0;
    }

    /**
     * @param pio
     *            the param interface object to check for inner project reusage
     * @return true if the cube is reused
     */
    public static List<IParamNodePO> getReuser(IParameterInterfacePO pio) {
        GeneralStorage gs = GeneralStorage.getInstance();
        IProjectPO proj = gs.getProject();
        return TestDataCubePM
                .computeParamNodeReuser(
                        pio, gs.getMasterSession(), proj);
    }
}
