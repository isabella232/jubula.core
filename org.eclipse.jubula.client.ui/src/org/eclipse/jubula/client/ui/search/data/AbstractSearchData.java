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
package org.eclipse.jubula.client.ui.search.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.tools.i18n.I18n;


/**
 * @author BREDEX GmbH
 * @created Jul 26, 2010
 */
public abstract class AbstractSearchData {
    /**
     * <code>m_typesToSearchFor</code>
     */
    private List<SearchableType> m_typesToSearchFor = 
        new ArrayList<SearchableType>();
    
    /** recent queries */
    private final List <String> m_recent = new ArrayList<String>(5);
    
    /**
     * <code>m_typesToSearchFor</code> during query
     */
    private List<SearchableType> m_typesToSearchIn = 
        new ArrayList<SearchableType>();
    
    /**
     * <code>m_searchName</code>
     */
    private String m_searchName;
    
    /**
     * <code>m_searchString</code>
     */
    private String m_searchString;

    /**
     * <code>m_useRegex</code>
     */
    private boolean m_useRegex = false;

    /**
     * <code>m_caseSensitive</code>
     */
    private boolean m_caseSensitive = false;

    /**
     * @param searchName
     *            the searchName to set
     */
    protected void setSearchName(String searchName) {
        m_searchName = searchName;
    }

    /**
     * @return the searchName
     */
    public String getSearchName() {
        return m_searchName;
    }

    /**
     * @param useRegex the useRegex to set
     */
    public void setUseRegex(boolean useRegex) {
        m_useRegex = useRegex;
    }

    /**
     * @return the useRegex
     */
    public boolean isUseRegex() {
        return m_useRegex;
    }

    /**
     * @param caseSensitive the caseSensitive to set
     */
    public void setCaseSensitive(boolean caseSensitive) {
        m_caseSensitive = caseSensitive;
    }

    /**
     * @return the caseSensitive
     */
    public boolean isCaseSensitive() {
        return m_caseSensitive;
    }

    /**
     * @param searchString the searchString to set
     */
    protected void setSearchString(String searchString) {
        m_searchString = searchString;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return m_searchString;
    }
    
    /**
     * @param typesToSearchIn the typesToSearchFor to set
     */
    protected void setTypesToSearchIn(List<SearchableType> typesToSearchIn) {
        m_typesToSearchIn = typesToSearchIn;
    }

    /**
     * @return the typesToSearchFor
     */
    public List<SearchableType> getTypesToSearchIn() {
        return m_typesToSearchIn;
    }

    /**
     * @param typesToSearchFor the typesToSearchFor to set
     */
    public void setTypesToSearchFor(List<SearchableType> typesToSearchFor) {
        m_typesToSearchFor = typesToSearchFor;
    }

    /**
     * @return the typesToSearchFor
     */
    public List<SearchableType> getTypesToSearchFor() {
        return m_typesToSearchFor;
    }

    /**
     * @return the recent
     */
    public List <String> getRecent() {
        return m_recent;
    }

    /**
     * @author BREDEX GmbH
     * @created Aug 9, 2010
     */
    public static class SearchableType {
        /**
         * <code>m_type</code>
         */
        private Class<? extends IPersistentObject> m_type;
        
        /**
         * <code>m_enabled</code>
         */
        private boolean m_enabled = true;

        /**
         * @param type the type
         * @param enablement the enablement state
         */
        public SearchableType(Class<? extends IPersistentObject> type, 
            boolean enablement) {
            setType(type);
            setEnabled(enablement);
        }

        /**
         * @param type the type to set
         */
        public void setType(Class<? extends IPersistentObject> type) {
            m_type = type;
        }

        /**
         * @return the type
         */
        public Class<? extends IPersistentObject> getType() {
            return m_type;
        }

        /**
         * @param enabled the enabled to set
         */
        public void setEnabled(boolean enabled) {
            m_enabled = enabled;
        }

        /**
         * @return the enabled
         */
        public boolean isEnabled() {
            return m_enabled;
        }

        /**
         * @return a human readable / displayable name
         */
        public String getName() {
            return I18n.getString(m_type.getName());
        }
    }
}
