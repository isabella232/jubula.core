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
package org.eclipse.jubula.client.ui.rcp.search.data;

import java.util.List;

import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;


/**
 * @author BREDEX GmbH
 * @created Aug 9, 2010
 */
public class StructureSearchData extends AbstractSearchData {
    /**
     * <code>instance</code>
     */
    private static StructureSearchData instance = null;

    /**
     * @param searchName
     *            the human readable search name
     * @param searchString
     *            the string to search for
     * @param caseSensitive
     *            the case sensitive option
     * @param useRegex
     *            the use regex option
     * @param typesToSearchIn
     *            a list of types to search for
     */
    public StructureSearchData(String searchName, String searchString,
            boolean caseSensitive, boolean useRegex,
            List<SearchableType> typesToSearchIn) {
        this();
        setSearchName(searchName);
        setSearchString(searchString);
        setCaseSensitive(caseSensitive);
        setUseRegex(useRegex);
        setTypesToSearchIn(typesToSearchIn);
    }

    /**
     * Constructor
     */
    public StructureSearchData() {
        getTypesToSearchFor().add(
                new SearchableType(NodeMaker.getTestJobPOClass(), true));
        getTypesToSearchFor().add(
                new SearchableType(PoMaker.getTestSuiteClass(), true));
        getTypesToSearchFor().add(
                new SearchableType(NodeMaker.getSpecTestCasePOClass(), true));
        getTypesToSearchFor().add(
                new SearchableType(NodeMaker.getCapPOClass(), true));
        getTypesToSearchFor().add(
                new SearchableType(NodeMaker.getCategoryPOClass(), true));
        getTypesToSearchFor().add(
                new SearchableType(NodeMaker.getRefTestSuitePOClass(), true));
        getTypesToSearchFor().add(
                new SearchableType(NodeMaker.getExecTestCasePOClass(), true));
    }

    /**
     * Method to get the single instance of this class.
     * 
     * @return the instance of this Singleton
     */
    public static synchronized StructureSearchData getInstance() {
        if (instance == null) {
            instance = new StructureSearchData();
        }
        return instance;
    }
}
