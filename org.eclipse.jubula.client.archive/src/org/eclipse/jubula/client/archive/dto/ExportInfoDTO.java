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
package org.eclipse.jubula.client.archive.dto;

import org.osgi.framework.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** @author BREDEX GmbH */
public class ExportInfoDTO {
    
    /** */
    private String m_encoding;
    /** */
    private int m_majorVersion, m_minorVersion, m_microVersion;
    /** */
    private String m_date;
    
    
    /** needed because json mapping */
    public ExportInfoDTO() { }
    
    /**
     * @return encoding
     */
    @JsonProperty("encoding")
    public String getEncoding() {
        return m_encoding;
    }
    
    /**
     * @param encoding 
     */
    public void setEncoding(String encoding) {
        this.m_encoding = encoding;
    }
    
    /**
     * @return version of json export
     */
    @JsonIgnore
    public Version getVersion() {
        return new Version(m_majorVersion, m_minorVersion, m_microVersion);
    }
    
    /**
     * @return majorVersion
     */
    @JsonProperty("majorVersion")
    public int getMajorVersion() {
        return m_majorVersion;
    }
    
    /**
     * @param majorVersion 
     */
    public void setMajorVersion(int majorVersion) {
        this.m_majorVersion = majorVersion;
    }
    
    /**
     * @return minorVersion
     */
    @JsonProperty("minorVersion")
    public int getMinorVersion() {
        return m_minorVersion;
    }
    
    /**
     * @param minorVersion 
     */
    public void setMinorVersion(int minorVersion) {
        this.m_minorVersion = minorVersion;
    }
    
    /**
     * @return microVersion
     */
    @JsonProperty("microVersion")
    public int getMicroVersion() {
        return m_microVersion;
    }
    
    /**
     * @param microVersion 
     */
    public void setMicroVersion(int microVersion) {
        this.m_microVersion = microVersion;
    }
    
    /**
     * @param version 
     */
    public void setVersion(Version version) {
        this.m_majorVersion = version.getMajor();
        this.m_minorVersion = version.getMinor();
        this.m_microVersion = version.getMicro();
    }
    
    /**
     * @return date
     */
    @JsonProperty("date")
    public String getDate() {
        return m_date;
    }
    
    /**
     * @param date 
     */
    public void setDate(String date) {
        this.m_date = date;
    }
}
