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

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubeContPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;


/**
 * Factory for creating Component Name mappers.
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2009
 */
public final class CompNameMapperFactory {

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(CompNameMapperFactory.class);

    
    /**
     * Private Constructor for utility class
     */
    private CompNameMapperFactory() {
        // private constructor for utility class
    }
    
    /**
     * 
     * @param node The PO for which to find an appropriate Component Name 
     *             mapper. May not be <code>null</code>.
     * @param componentNameCache The cache that will be used by the returned
     *                           Component Name mapper.
     * @return a Component Name mapper capable of managing Component Names for
     *         the given PO.
     */
    public static IWritableComponentNameMapper createCompNameMapper(
            IPersistentObject node, 
            IWritableComponentNameCache componentNameCache)
        throws IllegalArgumentException {
        
        Validate.notNull(node);
        
        if (node instanceof ISpecTestCasePO) {
            return new TestCaseComponentNameMapper(
                    componentNameCache, (ISpecTestCasePO)node);
        } else if (node instanceof ITestSuitePO) {
            return new TestSuiteComponentNameMapper(
                    componentNameCache, (ITestSuitePO)node);
        } else if (node instanceof IAUTMainPO) {
            return new ObjectMappingComponentNameMapper(
                    componentNameCache, (IAUTMainPO)node);
        } else if (node instanceof IProjectPO) {
            return new ProjectComponentNameMapper(
                    componentNameCache, (IProjectPO)node);
        } else if (node instanceof IProjectPropertiesPO) {
            return new NullComponentNameMapper();
        } else if (node instanceof ITestJobPO) {
            return new NullComponentNameMapper();
        } else if (node instanceof ITestDataCubeContPO) {
            return new NullComponentNameMapper();
        }

        // throw and catch an exception in order to get a stacktrace in the 
        // log.
        try {
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException iae) {
            log.warn("Could not find a Component Name mapper that supports context type: "  //$NON-NLS-1$
                    + node.getClass() 
                    + "; Returning an empty mapper implementation.", //$NON-NLS-1$
                    iae);
        }
        return new NullComponentNameMapper();
    }

}
