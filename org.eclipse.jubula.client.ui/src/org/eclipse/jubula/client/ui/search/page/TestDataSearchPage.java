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
package org.eclipse.jubula.client.ui.search.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.search.data.AbstractSearchData;
import org.eclipse.jubula.client.ui.search.data.TestDataSearchData;
import org.eclipse.jubula.client.ui.search.data.AbstractSearchData.SearchableType;
import org.eclipse.jubula.client.ui.search.query.TestDataSearchQuery;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;


/**
 * @author BREDEX GmbH
 * @created Aug 10, 2010
 */
public class TestDataSearchPage extends AbstractSearchPage {
    /**
     * {@inheritDoc}
     */
    protected ISearchQuery newQuery() {
        List<SearchableType> typesToSearchIn = new ArrayList<SearchableType>();
        for (SearchableType structureType : getSearchData()
                .getTypesToSearchFor()) {
            if (structureType.isEnabled()) {
                typesToSearchIn.add(structureType);
            }
        }
        String searchString = getSearchStringCombo().getText();
        boolean caseSensitive = getSearchData().isCaseSensitive();
        boolean regEx = getSearchData().isUseRegex();
        
        TestDataSearchData searchData = new TestDataSearchData(
                NLS.bind(Messages.TestDataSearchPageSearchName,
                    new Object[] { searchString }), searchString, caseSensitive,
                    regEx, typesToSearchIn);

        return new TestDataSearchQuery(searchData);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractSearchData getSearchData() {
        return TestDataSearchData.getInstance();
    }
}
