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
package org.eclipse.jubula.client.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jubula.client.core.utils.LocaleUtil;


/**
 * 
 *
 * class to manage language lists and the conversion between Locale and String
 * representation of languages
 * 
 * @author BREDEX GmbH
 * @created 10.10.2005
 *
 */
public class LanguageHelper {
    
    /**
     * <code>m_supportedObj</code> object which use the LanguageHelper
     */
    private ILangSupport m_supportedObj = null;
    
    /** the list of languages */
    private List<Locale> m_languageList = new ArrayList<Locale>();
       
    /**
     * @param obj supported object
     */
    public LanguageHelper(ILangSupport obj) {
        m_supportedObj = obj;
    }    
    
    /**
     * @return Returns the unmodifiable languageList.
     */
    public List<Locale> getLanguageList() {
        return Collections.unmodifiableList(getInternalLangList());
    }
    
    /**
     * only for internal use
     * @return languageList
     */
    private List<Locale> getInternalLangList() {
        if (m_supportedObj.isModified()) {
            setLanguageList(m_supportedObj.getHbmLanguageList());
            m_supportedObj.setModified(false);
        }
        return m_languageList;
    }
    
    /**
     * @param lang The languageList to set.
     */
    public void addLanguageToList(Locale lang) {
        getInternalLangList().add(lang);
        m_supportedObj.addLangToList(lang.toString());
    }
    
    /**
     * clears the language list in memory and the language list for Persistence (JPA / EclipseLink)
     */
    public void clearLangList() {
        getInternalLangList().clear();
        m_supportedObj.clearLangList();
    }

    /**
     * @param languageSet The languageSet to set.
     */
    void setLanguageList(Set<String> languageSet) {
        m_languageList.clear();
        for (String lang : languageSet) {
            m_languageList.add(LocaleUtil.convertStrToLocale(lang));
        }    
    }
    
    /**
     * @return iterator of languageList
     */
    public Iterator<Locale> getLangListIterator() {
        return getLanguageList().iterator();
    }
    
    /**
     * @param lang language to verify
     * @return flag, if given locale is contained in languagelist
     */
    public boolean containsItem(Locale lang) {
        return getInternalLangList().contains(lang);
    }

}
