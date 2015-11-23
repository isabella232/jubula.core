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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO.AlmReportStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class TestresultSummaryDTO {

    /** */
    private List<MonitoringValuesDTO> m_monitoringValues =
            new ArrayList<MonitoringValuesDTO>();

    /** ALM reported flag */
    private AlmReportStatus m_almStatus = AlmReportStatus.NOT_CONFIGURED;
    
    /** AUT server */
    private String m_autAgentName;

    /** cmd param */
    private String m_autCmdParameter;
    
    /** AUT conf */
    private String m_autConfigName;
    
    /** AUT hostname */
    private String m_autHostname;

    /** AUT ID */
    private String m_autId;
    
    /** AUT name */
    private String m_autName;

    /** AUT OS */
    private String m_autOS;
    
    /** information about the used Toolkit */
    private String m_autToolkit;
    
    /** AUT conf guid */
    private String m_autConfigGuid;
    
    /** AUT Guid */
    private String m_autGuid;

    /** the GUID */
    private String m_guid;
    
    /** used coverage tooling for this summary */
    private String m_monitoringId;
    
    /** project guid */
    private String m_projectGuid;
    
    /** project id */
    private Long m_projectID;
    
    /** Ts guid */
    private String m_testsuiteGuid;

    /** monitoring value type */
    private String m_monitoringValueType;
    
    /** project Major Version */
    private Integer m_projectMajorVersion;
    
    /** project Minor Version */
    private Integer m_projectMinorVersion;
    
    /** project name */
    private String m_projectName;
    
    /** true if blob was written, false otherwise */
    private boolean m_blobWritten = false;
    
    /** Date of test run */
    private Date m_testsuiteDate;
    
    /** duration */
    private String m_testsuiteDuration;
    
    /** end time */
    private Date m_testsuiteEndTime;
    
    /** eventhandler caps */
    private int m_testsuiteEventHandlerTeststeps;

    /** executed caps */
    private int m_testsuiteExecutedTeststeps;
    
    /** expected caps */
    private int m_testsuiteExpectedTeststeps;

    /** number of failed test steps */
    private int m_testsuiteFailedTeststeps;

    /** language */
    private String m_testsuiteLanguage;

    /** Ts name */
    private String m_testsuiteName;

    /** Tj Start time **/
    private Date m_testJobStartTime;

    /** Ts Start time **/
    private Date m_testsuiteStartTime;

    /** Ts status */
    private int m_testsuiteStatus;

    /** needed because json mapping */
    public TestresultSummaryDTO() { }
    
    /**
     * @param trs 
     */
    public TestresultSummaryDTO(ITestResultSummaryPO trs) {
        m_almStatus = trs.getAlmReportStatus();
        m_autAgentName = trs.getAutAgentName();
        m_autCmdParameter = trs.getAutCmdParameter();
        m_autConfigName = trs.getAutConfigName();
        m_autHostname = trs.getAutHostname();
        m_autId = trs.getAutId();
        m_autName = trs.getAutName();
        m_autOS = trs.getAutOS();
        m_autToolkit = trs.getAutToolkit();
        m_autConfigGuid = trs.getInternalAutConfigGuid();
        m_autGuid = trs.getInternalAutGuid();
        m_guid = trs.getInternalGuid();
        m_monitoringId = trs.getInternalMonitoringId();
        m_projectGuid = trs.getInternalProjectGuid();
        m_projectID = trs.getInternalProjectID();
        m_testsuiteGuid = trs.getInternalTestsuiteGuid();
        m_monitoringValueType = trs.getMonitoringValueType();
        m_projectMajorVersion = trs.getProjectMajorVersion();
        m_projectMinorVersion = trs.getProjectMinorVersion();
        m_projectName = trs.getProjectName();
        m_blobWritten = trs.isReportWritten();
        m_testsuiteDate = trs.getTestsuiteDate();
        m_testsuiteDuration = trs.getTestsuiteDuration();
        m_testsuiteEndTime = trs.getTestsuiteEndTime();
        m_testsuiteEventHandlerTeststeps =
                trs.getTestsuiteEventHandlerTeststeps();
        m_testsuiteExecutedTeststeps = trs.getTestsuiteExecutedTeststeps();
        m_testsuiteExpectedTeststeps = trs.getTestsuiteExpectedTeststeps();
        m_testsuiteFailedTeststeps = trs.getTestsuiteFailedTeststeps();
        m_testsuiteName = trs.getTestsuiteName();
        m_testJobStartTime = trs.getTestJobStartTime();
        m_testsuiteStartTime = trs.getTestsuiteStartTime();
        m_testsuiteStatus = trs.getTestsuiteStatus();
    }
    
    
    /**
     * @return monitoringValues
     */
    @JsonProperty("monitoringValues")
    public List<MonitoringValuesDTO> getMonitoringValues() {
        return m_monitoringValues;
    }
    /**
     * @param monitoringValue 
     */
    public void addMonitoringValue(MonitoringValuesDTO monitoringValue) {
        this.m_monitoringValues.add(monitoringValue);
    }
    
    /**
     * @return almStatus
     */
    @JsonProperty("almStatus")
    public AlmReportStatus getAlmStatus() {
        return m_almStatus;
    }
    
    /**
     * @param almStatus 
     */
    public void setAlmStatus(AlmReportStatus almStatus) {
        this.m_almStatus = almStatus;
    }
    
    /**
     * @return autAgentName
     */
    @JsonProperty("autAgentName")
    public String getAutAgentName() {
        return m_autAgentName;
    }
    
    /**
     * @param autAgentName 
     */
    public void setAutAgentName(String autAgentName) {
        this.m_autAgentName = autAgentName;
    }
    
    /**
     * @return autCmdParameter
     */
    @JsonProperty("autCmdParameter")
    public String getAutCmdParameter() {
        return m_autCmdParameter;
    }
    
    /**
     * @param autCmdParameter 
     */
    public void setAutCmdParameter(String autCmdParameter) {
        this.m_autCmdParameter = autCmdParameter;
    }
    
    /**
     * @return autConfigName
     */
    @JsonProperty("autConfigName")
    public String getAutConfigName() {
        return m_autConfigName;
    }
    
    /**
     * @param autConfigName 
     */
    public void setAutConfigName(String autConfigName) {
        this.m_autConfigName = autConfigName;
    }
    
    /**
     * @return autHostname
     */
    @JsonProperty("autHostname")
    public String getAutHostname() {
        return m_autHostname;
    }
    
    /**
     * @param autHostname 
     */
    public void setAutHostname(String autHostname) {
        this.m_autHostname = autHostname;
    }
    
    /**
     * @return autId
     */
    @JsonProperty("autId")
    public String getAutId() {
        return m_autId;
    }
    
    /**
     * @param autId 
     */
    public void setAutId(String autId) {
        this.m_autId = autId;
    }
    
    /**
     * @return autName
     */
    @JsonProperty("autName")
    public String getAutName() {
        return m_autName;
    }
    
    /**
     * @param autName 
     */
    public void setAutName(String autName) {
        this.m_autName = autName;
    }
    
    /**
     * @return autOS
     */
    @JsonProperty("autOS")
    public String getAutOS() {
        return m_autOS;
    }
    
    /**
     * @param autOS 
     */
    public void setAutOS(String autOS) {
        this.m_autOS = autOS;
    }
    
    /**
     * @return autToolkit
     */
    @JsonProperty("autToolkit")
    public String getAutToolkit() {
        return m_autToolkit;
    }
    
    /**
     * @param autToolkit 
     */
    public void setAutToolkit(String autToolkit) {
        this.m_autToolkit = autToolkit;
    }
    
    /**
     * @return autConfigGuid
     */
    @JsonProperty("autConfigGuid")
    public String getAutConfigGuid() {
        return m_autConfigGuid;
    }
    
    /**
     * @param autConfigGuid 
     */
    public void setAutConfigGuid(String autConfigGuid) {
        this.m_autConfigGuid = autConfigGuid;
    }
    
    /**
     * @return autGuid
     */
    @JsonProperty("autGuid")
    public String getAutGuid() {
        return m_autGuid;
    }
    
    /**
     * @param autGuid 
     */
    public void setAutGuid(String autGuid) {
        this.m_autGuid = autGuid;
    }
    
    /**
     * @return guid
     */
    @JsonProperty("guid")
    public String getGuid() {
        return m_guid;
    }
    
    /**
     * @param guid 
     */
    public void setGuid(String guid) {
        this.m_guid = guid;
    }
    
    /**
     * @return monitoringId
     */
    @JsonProperty("monitoringId")
    public String getMonitoringId() {
        return m_monitoringId;
    }
    
    /**
     * @param monitoringId 
     */
    public void setMonitoringId(String monitoringId) {
        this.m_monitoringId = monitoringId;
    }
    
    /**
     * @return projectGuid
     */
    @JsonProperty("projectGuid")
    public String getProjectGuid() {
        return m_projectGuid;
    }
    
    /**
     * @param projectGuid 
     */
    public void setProjectGuid(String projectGuid) {
        this.m_projectGuid = projectGuid;
    }
    
    /**
     * @return projectID
     */
    @JsonProperty("projectID")
    public Long getProjectID() {
        return m_projectID;
    }
    
    /**
     * @param projectID 
     */
    public void setProjectID(Long projectID) {
        this.m_projectID = projectID;
    }
    
    /**
     * @return testsuiteGuid
     */
    @JsonProperty("testsuiteGuid")
    public String getTestsuiteGuid() {
        return m_testsuiteGuid;
    }
    
    /**
     * @param testsuiteGuid 
     */
    public void setTestsuiteGuid(String testsuiteGuid) {
        this.m_testsuiteGuid = testsuiteGuid;
    }
    
    /**
     * @return monitoringValueType
     */
    @JsonProperty("monitoringValueType")
    public String getMonitoringValueType() {
        return m_monitoringValueType;
    }
    
    /**
     * @param monitoringValueType 
     */
    public void setMonitoringValueType(String monitoringValueType) {
        this.m_monitoringValueType = monitoringValueType;
    }
    
    /**
     * @return projectMajorVersion
     */
    @JsonProperty("projectMajorVersion")
    public Integer getProjectMajorVersion() {
        return m_projectMajorVersion;
    }
    
    /**
     * @param projectMajorVersion 
     */
    public void setProjectMajorVersion(Integer projectMajorVersion) {
        this.m_projectMajorVersion = projectMajorVersion;
    }
    
    /**
     * @return projectMinorVersion
     */
    @JsonProperty("projectMinorVersion")
    public Integer getProjectMinorVersion() {
        return m_projectMinorVersion;
    }
    
    /**
     * @param projectMinorVersion 
     */
    public void setProjectMinorVersion(Integer projectMinorVersion) {
        this.m_projectMinorVersion = projectMinorVersion;
    }
    
    /**
     * @return projectName
     */
    @JsonProperty("projectName")
    public String getProjectName() {
        return m_projectName;
    }
    
    /**
     * @param projectName 
     */
    public void setProjectName(String projectName) {
        this.m_projectName = projectName;
    }
    
    /**
     * @return blobWritten
     */
    @JsonProperty("blobWritten")
    public boolean isBlobWritten() {
        return m_blobWritten;
    }
    
    /**
     * @param blobWritten 
     */
    public void setBlobWritten(boolean blobWritten) {
        this.m_blobWritten = blobWritten;
    }
    
    /**
     * @return testsuiteDate
     */
    @JsonProperty("testsuiteDate")
    public Date getTestsuiteDate() {
        return m_testsuiteDate;
    }
    
    /**
     * @param testsuiteDate 
     */
    public void setTestsuiteDate(Date testsuiteDate) {
        this.m_testsuiteDate = testsuiteDate;
    }
    
    /**
     * @return testsuiteDuration
     */
    @JsonProperty("testsuiteDuration")
    public String getTestsuiteDuration() {
        return m_testsuiteDuration;
    }
    
    /**
     * @param testsuiteDuration 
     */
    public void setTestsuiteDuration(String testsuiteDuration) {
        this.m_testsuiteDuration = testsuiteDuration;
    }
    
    /**
     * @return testsuiteEndTime
     */
    @JsonProperty("testsuiteEndTime")
    public Date getTestsuiteEndTime() {
        return m_testsuiteEndTime;
    }
    
    /**
     * @param testsuiteEndTime 
     */
    public void setTestsuiteEndTime(Date testsuiteEndTime) {
        this.m_testsuiteEndTime = testsuiteEndTime;
    }
    
    /**
     * @return testsuiteEventHandlerTeststeps
     */
    @JsonProperty("testsuiteEventHandlerTeststeps")
    public int getTestsuiteEventHandlerTeststeps() {
        return m_testsuiteEventHandlerTeststeps;
    }
    
    /**
     * @param testsuiteEventHandlerTeststeps 
     */
    public void setTestsuiteEventHandlerTeststeps(
            int testsuiteEventHandlerTeststeps) {
        this.m_testsuiteEventHandlerTeststeps = testsuiteEventHandlerTeststeps;
    }
    
    /**
     * @return testsuiteExecutedTeststeps
     */
    @JsonProperty("testsuiteExecutedTeststeps")
    public int getTestsuiteExecutedTeststeps() {
        return m_testsuiteExecutedTeststeps;
    }
    
    /**
     * @param testsuiteExecutedTeststeps 
     */
    public void setTestsuiteExecutedTeststeps(int testsuiteExecutedTeststeps) {
        this.m_testsuiteExecutedTeststeps = testsuiteExecutedTeststeps;
    }
    
    /**
     * @return testsuiteExpectedTeststeps
     */
    @JsonProperty("testsuiteExpectedTeststeps")
    public int getTestsuiteExpectedTeststeps() {
        return m_testsuiteExpectedTeststeps;
    }
    
    /**
     * @param testsuiteExpectedTeststeps 
     */
    public void setTestsuiteExpectedTeststeps(int testsuiteExpectedTeststeps) {
        this.m_testsuiteExpectedTeststeps = testsuiteExpectedTeststeps;
    }
    
    /**
     * @return testsuiteFailedTeststeps
     */
    @JsonProperty("testsuiteFailedTeststeps")
    public int getTestsuiteFailedTeststeps() {
        return m_testsuiteFailedTeststeps;
    }
    
    /**
     * @param testsuiteFailedTeststeps 
     */
    public void setTestsuiteFailedTeststeps(int testsuiteFailedTeststeps) {
        this.m_testsuiteFailedTeststeps = testsuiteFailedTeststeps;
    }
    
    /**
     * @return testsuiteLanguage
     */
    @JsonProperty("testsuiteLanguage")
    public String getTestsuiteLanguage() {
        return m_testsuiteLanguage;
    }
    
    /**
     * @param testsuiteLanguage 
     */
    public void setTestsuiteLanguage(String testsuiteLanguage) {
        this.m_testsuiteLanguage = testsuiteLanguage;
    }
    
    /**
     * @return testsuiteName
     */
    @JsonProperty("testsuiteName")
    public String getTestsuiteName() {
        return m_testsuiteName;
    }
    
    /**
     * @param testsuiteName 
     */
    public void setTestsuiteName(String testsuiteName) {
        this.m_testsuiteName = testsuiteName;
    }
    
    /**
     * @return testJobStartTime
     */
    @JsonProperty("testJobStartTime")
    public Date getTestJobStartTime() {
        return m_testJobStartTime;
    }
    
    /**
     * @param testJobStartTime 
     */
    public void setTestJobStartTime(Date testJobStartTime) {
        this.m_testJobStartTime = testJobStartTime;
    }
    
    /**
     * @return testsuiteStartTime
     */
    @JsonProperty("testsuiteStartTime")
    public Date getTestsuiteStartTime() {
        return m_testsuiteStartTime;
    }
    
    /**
     * @param testsuiteStartTime 
     */
    public void setTestsuiteStartTime(Date testsuiteStartTime) {
        this.m_testsuiteStartTime = testsuiteStartTime;
    }
    
    /**
     * @return testsuiteStatus
     */
    @JsonProperty("testsuiteStatus")
    public int getTestsuiteStatus() {
        return m_testsuiteStatus;
    }
    
    /**
     * @param testsuiteStatus 
     */
    public void setTestsuiteStatus(int testsuiteStatus) {
        this.m_testsuiteStatus = testsuiteStatus;
    }
}
