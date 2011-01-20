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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterDetailsPO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestResultPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.MonitoringConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;


/**
 * class to get keywords and summary from testresultnode to persist in database
 *
 * @author BREDEX GmbH
 * @created Mar 4, 2010
 */
public class TestresultSummaryBP {
    /**
     * <code>GDRUN</code>
     * if autconfig is null because gdrun ist used,
     * use this constant for summary table
     */
    public static final String GDRUN = "gdrun"; //$NON-NLS-1$

    /** constant for keyword type Test Step */
    public static final int TYPE_TEST_STEP = 3;
    
    /** constant for keyword type Test Case */
    public static final int TYPE_TEST_CASE = 2;
    
    /** constant for keyword type Test Suite */
    public static final int TYPE_TEST_SUITE = 1;
    
    /** instance */
    private static TestresultSummaryBP instance = null;
    
    /** id of parent keyword*/
    private Long m_parentKeyWordId;
    
    /**
     * @param result TestResultNode
     * @return test result summary to persist in database
     */
    public ITestResultSummaryPO createTestResultSummary(TestResult result) {
        ITestResultSummaryPO sum = PoMaker.createTestResultSummaryPO();
        TestExecution te = TestExecution.getInstance();
        ITestJobPO tj = te.getStartedTestJob();
        ITestSuitePO ts = te.getStartedTestSuite();
        IAUTMainPO startedAut = te.getConnectedAut();
        IAUTMainPO aut = startedAut != null ? startedAut : ts.getAut();
        IAUTConfigPO autConfig = ClientTestFactory.getClientTest()
                .getLastAutConfig();
        addAutId(sum, autConfig);
        if (autConfig != null) {
            sum.setAutConfigName(autConfig.getName());
            sum.setInternalAutConfigGuid(autConfig.getGuid());
            sum.setAutCmdParameter(autConfig.getValue("AUT_ARGUMENTS",  //$NON-NLS-1$
                    StringConstants.EMPTY));
            sum.setAutAgentName(autConfig.getServer());
            // FIXME MT: Workaround for correct localhost resolving (#2818)
            sum.setAutHostname(te.getLocalHostname());
            
        } else {
            sum.setAutConfigName(GDRUN);
            sum.setInternalAutConfigGuid(GDRUN);
            sum.setAutCmdParameter(GDRUN);
            sum.setAutAgentName(GDRUN);
            sum.setAutHostname(GDRUN);
        }
        sum.setAutOS(System.getProperty("os.name")); //$NON-NLS-1$
        if (aut != null) {
            sum.setInternalAutGuid(aut.getGuid());
            sum.setAutName(aut.getName());
            sum.setAutToolkit(aut.getToolkit());
        }
        sum.setTestsuiteDate(new Date());
        sum.setInternalTestsuiteGuid(ts.getGuid());
        sum.setTestsuiteName(ts.getName());

        sum.setInternalProjectGuid(result.getProjectGuid());
        sum.setInternalProjectID(result.getProjectId());
        sum.setProjectName(result.getProjectName() + StringConstants.SPACE
                + result.getProjectMajorVersion() + StringConstants.DOT
                + result.getProjectMinorVersion());
        sum.setProjectMajorVersion(result.getProjectMajorVersion());
        sum.setProjectMinorVersion(result.getProjectMinorVersion());
        Date startTime = ClientTestFactory.getClientTest()
                .getTestsuiteStartTime();
        sum.setTestsuiteStartTime(startTime);
        Date endTime = new Date();
        sum.setTestsuiteEndTime(endTime);
        if (startTime != null && endTime != null) {
            sum.setTestsuiteDuration(getDurationString(startTime, endTime));
        }
        sum.setTestsuiteExecutedTeststeps(te.getNumberOfTestedSteps());
        sum.setTestsuiteExpectedTeststeps(te.getExpectedNumberOfSteps());
        sum.setTestsuiteEventHandlerTeststeps(te.getNumberOfEventHandlerSteps()
                + te.getNumberOfRetriedSteps());
        sum.setTestsuiteFailedTeststeps(te.getNumberOfFailedSteps());
        sum.setTestsuiteLanguage(te.getLocale().getDisplayName());
        sum.setTestsuiteRelevant(ClientTestFactory.getClientTest()
                        .isRelevant());
        if (tj != null) {
            sum.setTestJobName(tj.getName());
            sum.setInternalTestJobGuid(tj.getGuid());
            sum.setTestJobStartTime(ClientTestFactory.getClientTest()
                    .getTestjobStartTime());
        }
        sum.setTestsuiteStatus(result.getRootResultNode().getStatus());      
        //set default monitoring values.       
        sum.setInternalMonitoringId(MonitoringConstants.EMPTY_MONITORING_ID); 
        sum.setReport(MonitoringConstants.EMPTY_REPORT);
        sum.setReportWritten(false);
        sum.setMonitoringValueType(MonitoringConstants.EMPTY_TYPE); 
        return sum;
    }
    
    /**
     * @param result The Test Result.
     * @param summaryId id of test result summary
     * @return session of test result details to persist in database
     */
    public EntityManager createTestResultDetailsSession(TestResult result,
            Long summaryId) {
        final EntityManager sess = Hibernator.instance().openSession();
        Hibernator.instance().getTransaction(sess);
        buildTestResultDetailsSession(
                result.getRootResultNode(), sess, summaryId, 1, 1);
        return sess;
    }
    
    /**
     * Recursively build list of test result details to persist in database.
     * 
     * @param result TestResultNode
     * @param sess Session
     * @param summaryId id of testrun summary
     * @param nodeLevel "Indentation"-level of the node.
     * @param startingNodeSequence Initial sequence number for this section
     *                             of the Test Results.
     *                             
     * @return the continuation of the sequence number.
     */
    private int buildTestResultDetailsSession(TestResultNode result,
            EntityManager sess, Long summaryId, final int nodeLevel, 
            final int startingNodeSequence) {
        int nodeSequence = startingNodeSequence;
        TestResultNode resultNode = result;
        ITestResultPO keyword = PoMaker.createTestResultPO();
        fillNode(keyword, resultNode, sess);
        keyword.setKeywordLevel(nodeLevel);
        keyword.setKeywordSequence(nodeSequence);
        keyword.setInternalTestResultSummaryID(summaryId);
        keyword.setInternalParentKeywordID(m_parentKeyWordId);
        sess.persist(keyword);
        for (TestResultNode node : resultNode.getResultNodeList()) {
            m_parentKeyWordId = keyword.getId();
            nodeSequence = buildTestResultDetailsSession(node, sess, summaryId, 
                    nodeLevel + 1, nodeSequence + 1);
        }
        
        return nodeSequence;
    }
    
    /**
     * fill result node
     * @param keyword ITestResultPO
     * @param node ITestResultPO
     * @param sess the session to use (required e.g. BLOBs)
     */
    private void fillNode(ITestResultPO keyword, TestResultNode node, 
        EntityManager sess) {
        INodePO inode = node.getNode();
        keyword.setKeywordName(node.getNode().getName());
        keyword.setInternalKeywordGuid(inode.getGuid());
        keyword.setKeywordComment(inode.getComment());
        keyword.setInternalKeywordStatus(node.getStatus());
        keyword.setKeywordStatus(node.getStatusString());
        if (node.getTimeStamp() != null) {
            keyword.setTimestamp(node.getTimeStamp());
        }
        
        if (node.getParent() != null) {
            keyword.setInternalParentKeywordID(
                    node.getParent().getNode().getId());
        }
        
        if (inode instanceof ICapPO) {
            keyword.setInternalKeywordType(TYPE_TEST_STEP);
            keyword.setKeywordType("Test Step"); //$NON-NLS-1$
            
            //set component name, type and action name
            ICapPO cap = (ICapPO)inode;
            String compNameGuid = cap.getComponentName();
            keyword.setInternalComponentNameGuid(compNameGuid);
            keyword.setComponentName(
                    StringUtils.defaultString(node.getComponentName()));
            keyword.setInternalComponentType(cap.getComponentType());
            keyword.setComponentType(CompSystemI18n.getString(
                    cap.getComponentType()));
            keyword.setInternalActionName(cap.getActionName());
            keyword.setActionName(CompSystemI18n.getString(
                    cap.getActionName()));
            //set parameters
            addParameterListToResult(keyword, node, cap);
            //add error details
            addErrorDetails(keyword, node, sess);
        } else if (inode instanceof ITestCasePO) {
            keyword.setInternalKeywordType(TYPE_TEST_CASE);
            keyword.setKeywordType("Test Case"); //$NON-NLS-1$
        } else if (inode instanceof ITestSuitePO) {
            keyword.setInternalKeywordType(TYPE_TEST_SUITE);
            keyword.setKeywordType("Test Suite"); //$NON-NLS-1$
        }
    }
    
    /**
     * get a list of parameters for cap
     * @param node TestResultNode
     * @param cap ICapPO
     * @param keyword ITestResultPO
     * @return result mit parameter
     */
    private ITestResultPO addParameterListToResult(ITestResultPO keyword,
            TestResultNode node, ICapPO cap) {
        
        int index = 0;
        for (IParamDescriptionPO param : cap.getParameterList()) {
            IParameterDetailsPO parameter = PoMaker.createParameterDetailsPO();
            
            parameter.setParameterName(param.getName());
            parameter.setInternalParameterType(param.getType());
            parameter.setParameterType(CompSystemI18n.getString(
                    param.getType(), true));
            
            String paramValue = StringConstants.EMPTY;
            //parameter-value
            if (node.getParamValues().size() >= index + 1) {
                final String value = node.getParamValues().get(index);
                if (value != null) {
                    if (value.length() == 0) {
                        paramValue = TestDataConstants.EMPTY_SYMBOL;
                    } else {
                        paramValue = value;                        
                    }
                }
            }
            parameter.setParameterValue(paramValue);
            keyword.addParameter(parameter);
            index++;
        }
        
        return keyword;
    }
    
    /**
     * add error details to test result element
     * @param keyword ITestResultPO
     * @param node TestResultNode
     * @param sess the session to use (required e.g. BLOBs)
     */
    private void addErrorDetails(ITestResultPO keyword, TestResultNode node, 
        EntityManager sess) {
        if (node.getStatus() == TestResultNode.ERROR 
                || node.getStatus() == TestResultNode.RETRYING) {
            keyword.setStatusType(I18n.getString(node.getEvent().getId(),
                    true));
            
            Set keys = node.getEvent().getProps().keySet();
            if (node.getEvent().getId().equals(
                TestErrorEvent.ID.IMPL_CLASS_ACTION_ERROR)) {
                String key = (String)node.getEvent().getProps().get(
                    TestErrorEvent.Property.DESCRIPTION_KEY);
                Object[] args = (Object[])node.getEvent().getProps().get(
                        TestErrorEvent.Property.PARAMETER_KEY);
                //error description
                if (key != null) {
                    keyword.setStatusDescription(String.valueOf(I18n.getString(
                            key, args)));
                }
            } else {
                for (Object key : keys) {
                    String value = String.valueOf(
                            node.getEvent().getProps().get(key));
                    if (key.equals(TestErrorEvent.Property.OPERATOR_KEY)) {
                        keyword.setStatusOperator(value);
                    }
                    if (key.equals(TestErrorEvent.Property.PATTERN_KEY)) {
                        keyword.setExpectedValue(value);
                    }
                    if (key.equals(TestErrorEvent.Property.ACTUAL_VALUE_KEY)) {
                        keyword.setActualValue(value);
                    }
                }
            }
            if (node.getScreenshot() != null) {
                keyword.setImage(node.getScreenshot());
            }
        }
    }
    
    /**
     * add aut id to summary
     * @param summary ITestResultSummaryPO
     * @param autConf IAUTConfigPO
     */
    private void addAutId(ITestResultSummaryPO summary, IAUTConfigPO autConf) {
        if (TestExecution.getInstance().getConnectedAutId() == null) {
            if (autConf != null) {
                summary.setAutId(autConf.getConfigMap().get(
                        AutConfigConstants.AUT_ID));
            } else {
                summary.setAutId(GDRUN);
            }
        } else {
            summary.setAutId(TestExecution.getInstance()
                    .getConnectedAutId().getExecutableName());
        }
    }

    /**
     * @return instance of TestresultSummaryBP
     */
    public static TestresultSummaryBP getInstance() {
        if (instance == null) {
            instance = new TestresultSummaryBP();
        }
        return instance;
    }
    
    /**
     * 
     * @param startTime The start time.
     * @param endTime The end time.
     * @return a String representation of the difference between the provided 
     *         times.
     */
    private String getDurationString(Date startTime, Date endTime) {
        long timeInSeconds = endTime.getTime() - startTime.getTime();
        timeInSeconds = timeInSeconds / 1000;
        long hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        String secondsString = (seconds < 10) ? "0" + seconds : String.valueOf(seconds); //$NON-NLS-1$ 
        String minutesString = (minutes < 10) ? "0" + minutes : String.valueOf(minutes); //$NON-NLS-1$ 
        String hoursString = (hours < 10) ? "0" + hours : String.valueOf(hours); //$NON-NLS-1$ 
        return hoursString + StringConstants.COLON + minutesString 
            + StringConstants.COLON + secondsString;
    }
}
