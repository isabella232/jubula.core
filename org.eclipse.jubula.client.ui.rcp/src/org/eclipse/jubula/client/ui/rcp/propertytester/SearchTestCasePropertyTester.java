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
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.propertytester.AbstractBooleanPropertyTester;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;

/**
 * PropertyTester for search results.
 *
 * It checks, if the selected search result element satisfies the rules
 * depending on the property <b>isExec</b> or <b>isExecOrSpec</b>.
 *
 * @see #testImpl(Object, String, Object[])
 *
 * @author BREDEX GmbH
 */
public class SearchTestCasePropertyTester 
    extends AbstractBooleanPropertyTester {

    /**
     * ID of the "isExec" property.
     */
    public static final String IS_EXEC = "isExec"; //$NON-NLS-1$

    /**
     * ID of the "isExecOrSpec" property.
     */
    public static final String IS_EXEC_OR_SPEC_AND_USES_CTDS =
            "isExecOrSpecAndUsesCTDS"; //$NON-NLS-1$

    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] {
        IS_EXEC, IS_EXEC_OR_SPEC_AND_USES_CTDS };

    /**
     * @return True, if all of the following rules are satisfied, otherwise false:
     * <ol>
     *      <li>the current project is opened,</li>
     *      <li>the current project is not protected,</li>
     *      <li>this node is defined in the current project, and</li>
     *      <li>{@link #checkNode(String, INodePO)}.</li>
     * </ol>
     * {@inheritDoc}
     */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        if (receiver instanceof SearchResultElement) {
            @SuppressWarnings("unchecked")
            SearchResultElement<Long> searchResult = 
                    (SearchResultElement<Long>) receiver;
            IProjectPO project = GeneralStorage.getInstance().getProject();
            if (project != null && !project.getIsProtected()) {
                try {
                    INodePO node = GeneralStorage
                            .getInstance()
                            .getMasterSession()
                            .find(NodeMaker.getTestCasePOClass(),
                                    searchResult.getData());
                    if (node != null) {
                        return checkNode(property, project.getId(), node);
                    }
                } catch (IllegalStateException e) {
                    // Thrown, if the project is closed,
                    // while the project is reloaded. Ignore this case here,
                    // because this property tester will be called again,
                    // after the project is reloaded.
                }
            }
        }
        return false;
    }

    /**
     * @param property The property.
     * @param projectId The project ID.
     * @param node The node.
     * @return True, if the following rules are satisfied, otherwise false:
     * <p>If property is <b>isExec</b>:
     * <ol>
     *      <li>this node is an execution Test Case.</li>
     * </ol>
     * <p>If property is <b>isExecOrSpecAndUsesCTDS</b>:
     * <ol>
     *      <li>this node is a specification or execution Test Case, and</li>
     *      <li>the data cube is not empty.</li>
     * </ol>
     */
    private static boolean checkNode(String property,
            Long projectId, INodePO node) {

        if (property.equals(IS_EXEC)) {
            return node instanceof IExecTestCasePO;
        }
        if (property.equals(IS_EXEC_OR_SPEC_AND_USES_CTDS)) {
            if (node instanceof IExecTestCasePO
                    || node instanceof ISpecTestCasePO) {
                ITestCasePO testCase = (ITestCasePO) node;
                return testCase.getReferencedDataCube() != null;
            }
        }
        // node is not execution and not specification Test Case
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
