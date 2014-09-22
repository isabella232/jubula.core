/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH 
 *******************************************************************************/
package org.eclipse.jubula.client.core.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This is a representation of an project version which includes 
 * major, minor, micro and qualifier
 * @author BREDEX GmbH
 *
 */
public class ProjectVersion {
    /** The major version number of this project */
    private Integer m_majorNumber = null;

    /** The minor version number for this project */
    private Integer m_minorNumber = null;

    /** The micro version number for this project */
    private Integer m_microNumber = null;

    /** The version for this project */
    private String m_qualifier = null;

    /**
     * creates a project version which has only a qualifier, everything else is null
     * @param qualifier the qualifier version
     */
    public ProjectVersion(String qualifier) {
        m_qualifier = qualifier;
    }

    /**
     * creates a project version which has only version numbers
     * @param majorVersion major version of the project
     * @param minorVersion minor version of the project could be null
     * @param microVersion micro version of the project could be null
     */
    public ProjectVersion(Integer majorVersion, Integer minorVersion,
            Integer microVersion) {
        m_majorNumber = majorVersion;
        m_minorNumber = minorVersion;
        m_microNumber = microVersion;
    }

    /**
     * creates a complete project version with numbers and qualifier.
     * Either at least <b>majorVersion</b>  or <b>qualifier</b> should be given
     * @param majorVersion major version of the project could be null
     * @param minorVersion minor version of the project could be null
     * @param microVersion micro version of the project could be null
     * @param qualifier version qualifier of the project could be null
     */
    public ProjectVersion(Integer majorVersion, Integer minorVersion,
            Integer microVersion, String qualifier) {
        this(majorVersion, minorVersion, microVersion);
        m_qualifier = qualifier;
    }

    /**
     * @return the major version number
     */
    public Integer getMajorNumber() {
        return m_majorNumber;
    }

    /**
     * @return the minor version number
     */
    public Integer getMinorNumber() {
        return m_minorNumber;
    }

    /**
     * @return the micro version number
     */
    public Integer getMicroNumber() {
        return m_microNumber;
    }

    /**
     * @return the version qualifier
     */
    public String getVersionQualifier() {
        return m_qualifier;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_majorNumber)
                .append(m_minorNumber).append(m_microNumber)
                .append(m_qualifier).toHashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof ProjectVersion)) {
            ProjectVersion version = (ProjectVersion) obj;
            return new EqualsBuilder()
                .append(m_majorNumber, version.getMajorNumber())                
                .append(m_minorNumber, version.getMinorNumber())
                .append(m_microNumber, version.getMicroNumber())
                .append(m_qualifier, version.getVersionQualifier()).isEquals();
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (getMajorNumber() != null) {            
            sb.append(getMajorNumber());
        }
        if (getMinorNumber() != null) {
            sb.append(IProjectPO.VERSION_SEPARATOR);
            sb.append(getMinorNumber());            
        }
        if (getMicroNumber() != null) {
            sb.append(IProjectPO.VERSION_SEPARATOR);
            sb.append(getMicroNumber());            
        }
        if (getVersionQualifier() != null) {
            sb.append(IProjectPO.NAME_SEPARATOR);
            sb.append(getVersionQualifier());            
        }
        return sb.toString();
    }
}
