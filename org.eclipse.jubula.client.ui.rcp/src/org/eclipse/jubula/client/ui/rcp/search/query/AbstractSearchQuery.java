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
package org.eclipse.jubula.client.ui.rcp.search.query;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.search.data.AbstractSearchData;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.NodeSearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created Jul 27, 2010
 */
public abstract class AbstractSearchQuery implements ISearchQuery {
    /**
     * <code>patternCompiler</code>
     */
    private static PatternCompiler patternCompiler = new Perl5Compiler();
    
    /**
     * <code>patternMatcher</code>
     */
    private static PatternMatcher patternMatcher = new Perl5Matcher();
    
    /**
     * <code>m_testDataSearchData</code>
     */
    private AbstractSearchData m_searchData;
    
    /**
     * <code>m_searchResult</code>
     */
    private BasicSearchResult m_searchResult;
    
    /**
     * <code>m_timestamp</code>
     */
    private Date m_timestamp = new Date();

    /** {@inheritDoc} */
    public boolean canRerun() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean canRunInBackground() {
        return true;
    }
    
    /**
     * @return a timestamp for the query
     */
    public String getTimestamp() {
        return DateFormat.getInstance().format(m_timestamp);
    }
    
    /**
     * @param searchData the searchData to set
     */
    protected void setSearchData(AbstractSearchData searchData) {
        m_searchData = searchData;
    }

    /**
     * @return the searchData
     */
    protected AbstractSearchData getSearchData() {
        return m_searchData;
    }

    /**
     * @param searchResult the searchResult to set
     */
    protected void setSearchResult(BasicSearchResult searchResult) {
        m_searchResult = searchResult;
    }
    
    /** {@inheritDoc} */
    public ISearchResult getSearchResult() {
        return m_searchResult;
    }

    /**
     * @param reuse
     *            the reusing node po's
     * @param viewId
     *            the viewId to open before jumping to search result; may be
     *            <code>null</code>
     * @return a list of SearchResultElements for the given NodePOs
     */
    protected List<SearchResultElement> getSearchResultList(
            Set<INodePO> reuse, String viewId) {
        final List<SearchResultElement> searchResult = 
            new ArrayList<SearchResultElement>(
                reuse.size());
        for (INodePO node : reuse) {
            INodePO parent = node.getParentNode();
            String resultName = validParent(parent) ? parent.getName()
                    + " / " + node.getName() : node.getName(); //$NON-NLS-1$
            searchResult.add(new SearchResultElement<Long>(resultName, node
                    .getId(), getImageForNode(node),
                    new NodeSearchResultElementAction(), node.getComment(),
                    viewId));
        }
        return searchResult;
    }
    
    /**
     * @param reuseLoc the list of reuse locations
     */
    protected void setSearchResult(List<SearchResultElement> reuseLoc) {
        BasicSearchResult sr = new BasicSearchResult(this);
        sr.setResultList(reuseLoc);
        setSearchResult(sr);
    }
    
    /**
     * Gets an Image for the given po used in method showLocationsOfReuse
     * 
     * @param po
     *            the persistent object
     * @return an Image or null if po not supported
     */
    public Image getImageForNode(IPersistentObject po) {
        if (Persistor.isPoSubclass(po, IEventExecTestCasePO.class)) {
            return IconConstants.EH_IMAGE;
        } else if (Persistor.isPoSubclass(po, ICapPO.class)) {
            return IconConstants.CAP_IMAGE;
        } else if (Persistor.isPoSubclass(po, ISpecTestCasePO.class)) {
            return IconConstants.TC_IMAGE;
        } else if (Persistor.isPoSubclass(po, ICategoryPO.class)) {
            return IconConstants.CATEGORY_IMAGE;
        } else if (Persistor.isPoSubclass(po, ITestSuitePO.class)) {
            return IconConstants.TS_IMAGE;
        } else if (Persistor.isPoSubclass(po, IExecTestCasePO.class)) {
            return IconConstants.TC_REF_IMAGE;
        } else if (Persistor.isPoSubclass(po, ITestJobPO.class)) {
            return IconConstants.TJ_IMAGE;
        } else if (Persistor.isPoSubclass(po, ITestDataCubePO.class)) {
            return IconConstants.TDC_IMAGE;
        }    
        return null;
    }
    
    /**
     * @param parent the parent to check
     * @return true if valid parent to display
     */
    private boolean validParent(INodePO parent) {
        return parent != null && !(parent instanceof IProjectPO);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return getTimestamp() + ": " + getSearchData().getSearchName(); //$NON-NLS-1$
    }
    
    /**
     * compares two strings
     * @param string String 1
     * @param pattern String 2
     * @param operation Operation 1 = indexOf(ignoreCase), 2 = indexOf, 3 = regex
     * @return boolean
     */
    public static boolean compare(String string, String pattern, 
        int operation) {
        switch (operation) {
            case 1 : 
                return (string.toLowerCase().indexOf(
                        pattern.toLowerCase()) != -1);
            case 2 : 
                return (string.indexOf(pattern) != -1);
            case 3 : 
                try {
                    Pattern p = patternCompiler.compile(pattern);
                    return patternMatcher.matches(string, p);
                } catch (MalformedPatternException exc) {
                    return false;
                }
            default : 
                return (string.toLowerCase().indexOf(
                        pattern.toLowerCase()) != -1);
        }
    }
}
