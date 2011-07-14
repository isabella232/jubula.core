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
import org.eclipse.jubula.client.ui.search.data.AbstractSearchData.SearchableType;
import org.eclipse.jubula.client.ui.search.data.StructureSearchData;
import org.eclipse.jubula.client.ui.search.query.StructureSearchQuery;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;


/**
 * @author BREDEX GmbH
 * @created Jul 26, 2010
 */
public class KeywordSearchPage extends AbstractSearchPage {
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

        StructureSearchData searchData = new StructureSearchData(
                NLS.bind(Messages.SimpleSearchPageSearchName,
                        searchString), searchString,
                caseSensitive, regEx, typesToSearchIn);

        return new StructureSearchQuery(searchData);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractSearchData getSearchData() {
        return StructureSearchData.getInstance();
    }
}
