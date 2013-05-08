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
package org.eclipse.jubula.client.ui.rcp.propertytester;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.propertytester.AbstractBooleanPropertyTester;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;

/**
 * PropertyTester for search results.
 * It checks if the selected search result element is an exec and
 * that it is from the same and non protected project.
 * @author BREDEX GmbH
 *
 */
public class SearchTestCasePropertyTester 
    extends AbstractBooleanPropertyTester {
    /**
     * ID of the "isExec" property
     */
    public static final String IS_EXEC = "isExec"; //$NON-NLS-1$
    
    
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { IS_EXEC };

    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        if (property.equals(IS_EXEC)) {
            SearchResultElement<Long> searchResult = 
                    (SearchResultElement<Long>) receiver;
            IProjectPO project = GeneralStorage.getInstance().getProject();
            if (project != null && !project.getIsProtected()) {
                INodePO nodePO = GeneralStorage
                        .getInstance()
                        .getMasterSession()
                        .find(NodeMaker.getExecTestCasePOClass(),
                                searchResult.getData());
                if (nodePO != null) {
                    try {
                        IExecTestCasePO exec = (IExecTestCasePO) nodePO;
                        if (exec.getParentProjectId().equals(project.getId())) {
                            return true;
                        }
                    } catch (ClassCastException e) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return SearchResultElement.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }

}
