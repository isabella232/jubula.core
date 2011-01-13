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
package org.eclipse.jubula.client.core.model;

import java.util.List;

/**
 * @author BREDEX GmbH
 * @created Jun 28, 2010
 * 
 */
public interface ITestDataCubeContPO extends IPersistentObject {

    /**
     * @return an unmodifiable list of test data cubes
     */
    public abstract List<IParameterInterfacePO> getTestDataCubeList();

    /**
     * @param tdc
     *            test data cube to add
     */
    public abstract void addTestDataCube(IParameterInterfacePO tdc);

    /**
     * @param tdc
     *            test data cube to remove
     */
    public abstract void removeTestDataCube(IParameterInterfacePO tdc);

}