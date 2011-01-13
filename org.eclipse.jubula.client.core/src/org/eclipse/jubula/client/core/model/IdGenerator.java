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

import java.util.Iterator;

import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.jubula.client.core.persistence.NodePM;


/**
 * @author BREDEX GmbH
 * @created 07.06.2006
 */
public class IdGenerator {
    
    /** the id counter */
    private static long id = 0;

    /**
     * hidden utility constructor
     */
    private IdGenerator() {
        super();
    }
    
    /**
     * Generates IDs for POs
     * @param project the IprojectPO
     */
    public static void generate(IProjectPO project) {
        id = 0;
        for (ISpecPersistable specPers : project.getSpecObjCont()
            .getSpecObjList()) {
            
            if (specPers instanceof CategoryPO) {
                CategoryPO catPO = (CategoryPO)specPers;
                setID(catPO);
            }
            
            if (specPers instanceof SpecTestCasePO) {
                SpecTestCasePO specTcPO = (SpecTestCasePO)specPers;
                setID(specTcPO);
            }
        }
        for (IAUTMainPO aut : project.getAutMainList()) {
            AUTMainPO autMainPO = (AUTMainPO)aut;
            autMainPO.setId(id++);
            Iterator<IAUTConfigPO> confIter = aut.getAutConfigSet().iterator();
            while (confIter.hasNext()) {
                AUTConfigPO autConf = (AUTConfigPO)confIter.next();
                autConf.setId(id++);
            }
        }
    }

    /**
     * @param catPO the Category whose id to set
     */
    private static void setID(CategoryPO catPO) {
        catPO.setId(id++);
        Iterator nodeIter = catPO.getNodeListIterator();
        while (nodeIter.hasNext()) {
            NodePO nodePO = (NodePO)nodeIter.next();
            if (nodePO instanceof CategoryPO) {
                setID((CategoryPO)nodePO);
            } else if (nodePO instanceof SpecTestCasePO) {
                setID((SpecTestCasePO)nodePO);
            }
        }
    }

    /**
     * @param specTcPO the SpecTestCase whose id to set
     */
    private static void setID(SpecTestCasePO specTcPO) {
        specTcPO.setId(id++);
        Iterator<IExecTestCasePO> useLocsIter = 
            NodePM.getInternalExecTestCases(specTcPO.getGuid(), 
                specTcPO.getParentProjectId()).iterator();
        while (useLocsIter.hasNext()) {
            ExecTestCasePO execTcPO = (ExecTestCasePO)
                useLocsIter.next();
            execTcPO.setId(id++);
        }
    }
    
}
