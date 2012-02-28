/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;

/** 
 * 
 * @author volker
 *
 */
public class QueryResult implements ISearchResult {
    
    /**
     * contains the Analyzes and its AnalyzeResults
     */
    private Map<Analyze, AnalyzeResult> m_result = 
            new HashMap<Analyze, AnalyzeResult>();
    
    /** The Query */
    private final Query m_query;
    
    /**
     * @param query the query
     */
    public QueryResult(Query query) {
        this.m_query = query;
    }

    /** {@inheritDoc} */
    public void addListener(ISearchResultListener l) {
 
    }

    /** {@inheritDoc} */
    public void removeListener(ISearchResultListener l) {
 
    }

    /** {@inheritDoc} */
    public String getLabel() {
        return getQuery().getLabel();
    }

    /** {@inheritDoc} */
    public String getTooltip() {
        return getQuery().getLabel();
    }

    /** {@inheritDoc} */
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /** {@inheritDoc} */
    public ISearchQuery getQuery() {
        return m_query;
    }
    /**
     * @return The QueryResult Map
     */
    public Map<Analyze, AnalyzeResult> getResultMap() {
        return m_result;
    }

}
