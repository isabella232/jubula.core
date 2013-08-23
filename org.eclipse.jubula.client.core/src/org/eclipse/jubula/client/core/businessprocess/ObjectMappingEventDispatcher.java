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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;


/**
 * Utility class for adding logical names to the ObjectMap.
 *
 * @author BREDEX GmbH
 * @created 22.04.2005
 */
public class ObjectMappingEventDispatcher {
    
    
    /** found TestSuites for the given TestCase */
    private static List < IObjectMappingObserver > observer = 
        new ArrayList < IObjectMappingObserver > ();
    
    /** category where om nodes should be created in */
    private static IObjectMappingCategoryPO categoryToCreateIn;
    
    /**
     * Constructor.
     */
    private ObjectMappingEventDispatcher() {
        // do nothing
    }
    
    /**
     * Notifies all observer that there could be changes.
     * @param tc
     *      The tc that was updated
     */
    public static synchronized void notifyRecordObserver(ISpecTestCasePO tc) {
        List <IObjectMappingObserver> obs = 
            Collections.unmodifiableList(observer);
        if (obs.isEmpty()) {
            return;
        }
        ITestSuitePO[] testSuites = getTestSuitesOfSpecTestCase(tc);
        Set < IAUTMainPO > autList = new HashSet < IAUTMainPO > ();
        for (ITestSuitePO ts : testSuites) {
            autList.add(ts.getAut());
        }
        for (IAUTMainPO aut : autList) {
            for (IObjectMappingObserver obsvr : obs) {
                try {
                    obsvr.update(IObjectMappingObserver.EVENT_STEP_RECORDED, 
                        aut);
                } catch (Throwable t) { // NOPMD by al on 3/19/07 1:23 PM
                    // just catch possible errors in listener and continue to notify
                }
            }
        }
    }
    /**
     * adds a technical name to any open editor registered here with listener
     * 
     * @param tech
     *      ComponentIdentifiers
     */
    public static void notifyObjectMappedObserver(IComponentIdentifier[] tech) {
        List <IObjectMappingObserver> obs = 
            Collections.unmodifiableList(observer);
        for (IObjectMappingObserver obsvr : obs) {
            try {
                obsvr.update(IObjectMappingObserver.EVENT_COMPONENT_MAPPED, 
                    tech);
            } catch (Throwable t) { // NOPMD by al on 3/19/07 1:23 PM
                // just catch possible errors in listener and continue to notify
            }
        }
    }

    /**
     * FIXME : Andreas Candidate for util class
     * Gets all TestSuites where the SpecTestCase is used.
     * @param specTc the SpecTestCase
     * @return an Array of TestSuites.
     */
    private static ITestSuitePO[] getTestSuitesOfSpecTestCase(
        ISpecTestCasePO specTc) {
        
        // This method is very(!) inperformant if the given SpecTc has many
        // locations of use.
        List <INodePO> testSuiteList = new ArrayList <INodePO> ();
        // many expansive DB-accesses (this method is recursive!)
        List <IExecTestCasePO> execTestCases = 
            NodePM.getInternalExecTestCases(specTc.getGuid(), 
                specTc.getParentProjectId());
        for (IExecTestCasePO execTc : execTestCases) {
            final INodePO execTcParent = execTc.getParentNode();
            if ((execTcParent instanceof ITestSuitePO)) {
                if (!testSuiteList.contains(execTcParent)) {
                    testSuiteList.add(execTcParent);
                }
            } else if (execTcParent instanceof ISpecTestCasePO) {
                
                testSuiteList.addAll(Arrays.asList(getTestSuitesOfSpecTestCase(
                        (ISpecTestCasePO)execTcParent)));
            }
        }
        return testSuiteList.toArray(new ITestSuitePO[testSuiteList.size()]);
    }
    
    /**
     * adds an Observer to List
     * @param obs
     *      Observer
     */
    public static void addObserver(IObjectMappingObserver obs) {
        if (!observer.contains(obs)) {
            observer.add(obs);
        }
    }

    /**
     * adds an Observer to List
     * @param obs
     *      Observer
     */
    public static void removeObserver(IObjectMappingObserver obs) {
        if (observer.contains(obs)) {
            observer.remove(obs);
        }
    }

    /**
     * @return Returns the categoryToCreateIn.
     */
    public static IObjectMappingCategoryPO getCategoryToCreateIn() {
        return categoryToCreateIn;
    }
    /**
     * @param c The categoryToCreateIn to set.
     */
    public static void setCategoryToCreateIn(IObjectMappingCategoryPO c) {
        categoryToCreateIn = c;
    }
}