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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.tools.constants.StringConstants;

/**
 * class to manage and persist internationalized testdata </br>
 * (values and references)
 * 
 * @author BREDEX GmbH
 * @created 12.01.2005
 */
@Entity
@Table(name = "I18N_STRING")
public class I18NStringPO implements II18NStringPO {
    
    /** hibernate OID */
    private transient Long m_id = null;
    
    /** maps languages (locales) to string values
      * key: Locale (string representation), value: value as string
      */
    @SuppressWarnings("unchecked") // because of XDoclet
    private Map<String, String> m_map = new HashMap();

    /** hibernate version id */
    private transient Integer m_version;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * default constructor
     */
    I18NStringPO() {
        // just hide it, used by hibernate and PoMaker
    }
    
    /**
     * constructor
     * @param loc locale, for which to create an I18NString object
     * @param value value to internationalize
     * @param project associated project
     * inside of project
     */
    I18NStringPO(Locale loc, String value, IProjectPO project) {
        
        setValue(loc, value, project);
    }
    
    /**
     *  
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }
     
    /**
     *    
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * 
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * set the value for the given language
     * @param lang language, for which to set the value
     * @param value value
     * @param project associated project
     */
    public void setValue(Locale lang, String value, IProjectPO project) {
        
        if (validateLang(lang, project)) {
            if (value != null && value.length() != 0) {
                getMap().put(lang.toString(), value);
                setParentProjectId(project.getId());
            } else {
                getMap().remove(lang);
            }
        }
    }
    
    /**
     * @param lang language to validate
     * @param project associated project, for which to use an I18N string object
     * @return flag, if the given language is a supported language inside of
     * actual project
     */
    private boolean validateLang(Locale lang, IProjectPO project) {
        return project.getLangHelper().containsItem(lang);
    }

    /**
     * get the value for a given locale
     * @param lang language, for which to get the value
     * @return value
     */
    public String getValue(Locale lang) {
        Validate.notNull(lang);
        return getMap().get(lang.toString());
    }
    
    /**
     * only for hibernate
     * 
     * @return Returns the map.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "LOCALE_TO_TD")
    @MapKeyColumn(name = "LOCALE")
    @Column(name = "TD_VALUE", length = 4000)
    @JoinColumn(name = "I18N_STR")
    private Map<String, String> getMap() {
        return m_map;
    }
    /**
     * only for hibernate
     * @param map The map to set.
     */
    void setMap(Map<String, String> map) {
        m_map = map;
    }
    
    /**
     * @return a set of all Locale's used in this I18NString
     */
    @Transient
    public Set<Locale> getLanguages() {
        Set<Locale> supportedLocales = new java.util.HashSet<Locale>();
        for (String localeCode : getMap().keySet()) {
            supportedLocales.add(LocaleUtils.toLocale(localeCode));
        }
        
        return supportedLocales;
    }
    
    /**
     * Compares this I18nString object to the given object.
     * Returns true if the values  for all locales are equal.
     * @param obj the object to compare.
     * @return a <code>boolean</code> value. <br>
     * true if the two objects are equal, otherwise false. 
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof I18NStringPO) {
            I18NStringPO i18nStr = (I18NStringPO) obj;
            return getMap().equals(i18nStr.getMap());
        }
        return false;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {        
        return m_version;
    }

    /** 
     * {@inheritDoc}
     */
    void setVersion(Integer version) {
        m_version = version;
    }
    
    /**
     * @return a deep copy of this
     */
    public II18NStringPO deepCopy() {
        I18NStringPO i18nStr = new I18NStringPO();
        i18nStr.getMap().putAll(getMap());
        i18nStr.setParentProjectId(getParentProjectId());
        return i18nStr;
    }

    /**
     * {@inheritDoc}
     * @return empty string
     */
    @Transient
    public String getName() {
        return StringConstants.EMPTY;
    }

    /**
     * {@inheritDoc}
     * @return the hashcode in concordance with equals()
     */
    public int hashCode() {
        return getMap().hashCode();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public String toString() {
        SortedSet<String> sorter = new TreeSet<String>();
        for (Entry<String, String> entry : getMap().entrySet()) {
            sorter.add(entry.getKey() + ':' + entry.getValue());
        }
        StringBuilder result = new StringBuilder(sorter.size() * 100);
        for (String line : sorter) {
            result.append(line);
            result.append('\n');
        }
        return result.toString();
    }

}
