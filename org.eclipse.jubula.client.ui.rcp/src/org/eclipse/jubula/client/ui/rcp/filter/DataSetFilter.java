/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/** This class has implemented the searching on data set view */
public class DataSetFilter extends ViewerFilter {

    /** <code>.*</code> */
    private final String m_dotStar = StringConstants.DOT + StringConstants.STAR;
    
    /** the pattern */
    private String m_searchString;

    /**
     * @param origText is the new pattern
     */
    public void setSearchText(String origText) {
        String text = cleanText(origText);
        this.m_searchString = text == null ? null
                : m_dotStar + text + m_dotStar;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (m_searchString == null || m_searchString.length() == 0
                || !(element instanceof IDataSetPO)) {
            return true;
        }
        for (String value : ((IDataSetPO)element).getColumnStringValues()) {
            if (value.matches(m_searchString)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param searchString what would have to checking and cleaning
     * @return cleaned string from stars
     */
    private String cleanText(String searchString) {
        if (searchString == null || searchString.isEmpty()) {
            return null;
        }
        String result = searchString;
        while (result.startsWith(StringConstants.STAR)) {
            result = result.substring(1, result.length());
        }
        while (result.endsWith(StringConstants.STAR)) {
            result = result.substring(0, result.length() - 1);
        }
        result = result.replace(StringConstants.STAR, m_dotStar);
        return result;
    }
}
