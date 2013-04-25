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

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.propertytester.AbstractBooleanPropertyTester;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;

/**
 * PropertyTester for search results
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
            if (GeneralStorage.getInstance().getProject().getIsProtected()) {
                return false;
            }
            INodePO nodePO = GeneralStorage.getInstance().getMasterSession()
                    .find(NodeMaker.getNodePOClass(), searchResult.getData());
            if (nodePO == null || !nodePO.getClass().isAssignableFrom(
                    NodeMaker.getExecTestCasePOClass())) {
                return false;
            }
            return true;
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
