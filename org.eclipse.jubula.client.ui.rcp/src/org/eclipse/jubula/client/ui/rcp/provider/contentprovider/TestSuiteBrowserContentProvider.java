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
package org.eclipse.jubula.client.ui.rcp.provider.contentprovider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.rcp.businessprocess.WorkingLanguageBP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class TestSuiteBrowserContentProvider 
    extends AbstractTreeViewContentProvider {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(TestSuiteBrowserContentProvider.class);
    
    /**
     * @param parentElement Object
     * @return object array
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IExecObjContPO[]) {
            return new Object[] { ((IExecObjContPO[])parentElement)[0] };
        }

        if (parentElement instanceof IExecObjContPO) {
            IExecObjContPO execObjects = (IExecObjContPO)parentElement;
            List<Object> elements = new ArrayList<Object>();
            elements.addAll(execObjects.getExecObjList());
            return elements.toArray();
        }

        if (parentElement instanceof IExecTestCasePO) {
            ISpecTestCasePO referencedTestCase = 
                ((IExecTestCasePO)parentElement).getSpecTestCase();
            if (referencedTestCase != null) {
                Collection<IEventExecTestCasePO> eventTCs = Collections
                        .unmodifiableCollection(referencedTestCase
                                .getAllEventEventExecTC());
                List<INodePO> nodes = referencedTestCase
                        .getUnmodifiableNodeList();
                return ArrayUtils.addAll(eventTCs.toArray(), nodes.toArray());
            }
            
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        
        if (parentElement instanceof ITestSuitePO) {
            ITestSuitePO testSuite = (ITestSuitePO)parentElement;
            Locale workLang = WorkingLanguageBP.getInstance()
                    .getWorkingLanguage();
            if (testSuite.getAut() != null
                    && !WorkingLanguageBP.getInstance()
                            .isTestSuiteLanguage(workLang, testSuite)) {
                return ArrayUtils.EMPTY_OBJECT_ARRAY;
            }
            // fall through
        }
        
        if (parentElement instanceof INodePO) {
            List<INodePO> nodes = ((INodePO)parentElement)
                .getUnmodifiableNodeList();
            return nodes.toArray();
        }

        LOG.error("Wrong type for parent element: " + parentElement); //$NON-NLS-1$
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
}