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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.attributes.DefaultInitializer;
import org.eclipse.jubula.client.core.attributes.IDocAttributeInitializer;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created 22.04.2008
 */
@Entity
@Table(name = "DOC_ATTR_DESC")
class DocAttributeDescriptionPO implements IDocAttributeDescriptionPO {

    /** hibernate OID */
    private transient Long m_id = null;

    /** hibernate version id */
    private transient Integer m_version = null;
    
    /**
     * List of descriptions contained within this description.
     */
    private Set<IDocAttributeDescriptionPO> m_subDescriptions = 
        new HashSet<IDocAttributeDescriptionPO>();
    
    /** 
     * i18n key for the label for this attribute description.
     * This also serves as a way of uniquely identifying the description.
     */
    private String m_labelKey;

    /** 
     * Name of the class capable of displaying and storing attributes of 
     * this type. 
     */
    private String m_displayClassName;
    
    /** I18n keys for possible values for attributes of this type. */
    private Set<String> m_valueSetKeys;
    
    /** Default value for attributes of this type. */
    private String m_defaultValue;

    /** whether this attribute was defined by BREDEX GmbH */
    private boolean m_isBXAttribute;
    
    /** 
     * initializer class name
     */
    private String m_initializerClassName;
    
    /**
     * only for hibernate
     */
    DocAttributeDescriptionPO() {
        // only for hibernate
    }

    /**
     * Constructor
     * 
     * @param labelKey The i18n key used to fetch the label for this attribute
     *                 type. This also serves as a unique id for the attribute 
     *                 type.
     * @param displayClassName The name of a class capable of loading and 
     *                         storing objects of this type.
     * @param initializerClassName The name of a class capable of initializing 
     *                             objects of this type.
     */
    DocAttributeDescriptionPO(String labelKey, String displayClassName, 
            String initializerClassName) {
        setLabelKey(labelKey);
        setDisplayClassName(displayClassName);
        setInitializerClassName(initializerClassName);
    }
    
    /**
     * Constructor
     * 
     * @param labelKey The i18n key used to fetch the label for this attribute
     *                 type. This also serves as a unique id for the attribute 
     *                 type.
     * @param displayClassName The name of a class capable of loading and 
     *                         storing objects of this type.
     * @param initializerClassName The name of a class capable of initializing 
     *                             objects of this type.
     * @param valueKeySet I18n keys for the possible values for attributes of 
     *                    this type. A value of <code>null</code> indicates that
     *                    any value is allowed.
     * @param defaultValue I18n key for the default value for attributes of this
     *                     type. Must be contained within 
     *                     <code>valueKeySet</code> if <code>valueKeySet</code>
     *                     is not <code>null</code>.
     */
    DocAttributeDescriptionPO(String labelKey, String displayClassName, 
            String initializerClassName, Set<String> valueKeySet, 
            String defaultValue) {
        this(labelKey, displayClassName, initializerClassName);
        setValueSetKeys(valueKeySet);
        setDefaultValue(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getDefaultValue() {
        return getHbmDefaultValue();
    }
    /**
     * 
     * @param newDefaultValue The new default value for attributes of this type.
     *                        Note that changing the default value has no effect
     *                        on already existing attributes.
     */
    private void setDefaultValue(String newDefaultValue) { 
        if (getValueSetKeys().contains(newDefaultValue)) {
            setHbmDefaultValue(newDefaultValue);
        } else {
            StringBuffer sb = new StringBuffer(
                    Messages.AttemptedToSetADefaultValueThatDoesNotExist);
            sb.append(StringConstants.DOT);
            sb.append(StringConstants.SPACE);
            sb.append(Messages.Value);
            sb.append(StringConstants.COLON);
            sb.append(StringConstants.SPACE);
            sb.append(newDefaultValue);
            sb.append(StringConstants.SEMICOLON);
            sb.append(StringConstants.SPACE);
            sb.append(Messages.ValueSet);
            sb.append(StringConstants.COLON);
            sb.append(StringConstants.SPACE);
            sb.append(StringConstants.LEFT_BRACKET);
            for (String value : getValueSetKeys()) {
                sb.append(value);
                sb.append(StringConstants.COMMA + StringConstants.SPACE);
            }
            if (getValueSetKeys().isEmpty()) {
                sb.append(StringConstants.RIGHT_BRACKET);
            } else {
                sb.replace(sb.lastIndexOf(StringConstants.COMMA 
                    + StringConstants.SPACE), sb.length(), 
                        StringConstants.RIGHT_BRACKET);
            }
            
            throw new IllegalArgumentException(sb.toString());
        }
    }
    
    /**
     * 
     * @return the default value for this type.
     */
    @Basic
    @Column(name = "DEFAULT_VALUE")
    private String getHbmDefaultValue() {
        return m_defaultValue;
    }
    /**
     * 
     * @param defaultValue The default value.
     */
    private void setHbmDefaultValue(String defaultValue) {
        m_defaultValue = defaultValue;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "LABEL_KEY", unique = true)
    public String getLabelKey() {
        return m_labelKey;
    }
    /**
     * 
     * @param labelKey The new label key.
     */
    private void setLabelKey(String labelKey) {
        m_labelKey = labelKey; 
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "DISPLAY_CLASS")
    public String getDisplayClassName() {
        return m_displayClassName;
    }
    /**
     * 
     * @param displayClassName The new display class name.
     */
    public void setDisplayClassName(String displayClassName) {
        m_displayClassName = displayClassName;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @ElementCollection
    @CollectionTable(name = "VALUE_SETS")
    @Column(name = "VALUE")
    @JoinColumn(name = "FK_DOC_ATTR_DESCRIPTION")
    public Set<String> getValueSetKeys() {
        return m_valueSetKeys;
    }
    /**
     * 
     * @param valueSet The new value set.
     */
    private void setValueSetKeys(Set<String> valueSet) {
        m_valueSetKeys = valueSet;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "BX")
    public boolean isBXAttribute() {
        return m_isBXAttribute;
    }

    /**
     * 
     * @param isBXAttribute Whether this attribute was defined by BREDEX.
     */
    @SuppressWarnings("unused")
    private void setBXAttribute(boolean isBXAttribute) {
        m_isBXAttribute = isBXAttribute;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IDocAttributeDescriptionPO)) {
            return false;
        }
        IDocAttributeDescriptionPO otherDesc = (IDocAttributeDescriptionPO)obj;
        
        return new EqualsBuilder()
            .append(getLabelKey(), otherDesc.getLabelKey()).isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(getLabelKey()).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueValid(String value) {
        return getValueSetKeys() == null || getValueSetKeys().isEmpty() 
            || getValueSetKeys().contains(value);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @ManyToMany(cascade = CascadeType.ALL, 
                targetEntity = DocAttributeDescriptionPO.class)
    @JoinTable(name = "DOC_ATTR_DESC_LIST", 
               joinColumns = @JoinColumn(name = "PARENT"), 
               inverseJoinColumns = @JoinColumn(name = "CHILD"))
    public Set<IDocAttributeDescriptionPO> getSubDescriptions() {
        return m_subDescriptions;
    }
    /**
     * For hibernate.
     * 
     * @param subDescriptions The subDescriptions to use.
     */
    @SuppressWarnings("unused")
    private void setSubDescriptions(
            Set<IDocAttributeDescriptionPO> subDescriptions) {
        
        m_subDescriptions = subDescriptions;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean removeSubDescription(
            IDocAttributeDescriptionPO description) {
        
        return getSubDescriptions().remove(description);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public boolean addSubDescription(IDocAttributeDescriptionPO description) {
        return getSubDescriptions().add(description);
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
     * @return Long
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }
    /**
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public IDocAttributeInitializer getInitializer() {
        if (StringUtils.defaultString(
                getInitializerClassName()).length() == 0) {
            
            return new DefaultInitializer();
        }
        
        try {
            Class initializerClass = 
                Class.forName(getInitializerClassName());
            Object instance = initializerClass.newInstance();
            if (instance instanceof IDocAttributeInitializer) {
                return (IDocAttributeInitializer)instance; 
            }
            
            return new DefaultInitializer(); 
        } catch (ClassNotFoundException e) {
            return new DefaultInitializer(); 
        } catch (InstantiationException e) {
            return new DefaultInitializer(); 
        } catch (IllegalAccessException e) {
            return new DefaultInitializer(); 
        }
    }

    /**
     * 
     * @return the name of the initializer class.
     */
    @Basic
    @Column(name = "INITIALIZER_CLASS")
    private String getInitializerClassName() {
        return m_initializerClassName;
    }

    /**
     * 
     * @param className The new initializer class name.
     */
    public void setInitializerClassName(String className) {
        m_initializerClassName = className;
    }
}
