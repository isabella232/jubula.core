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
package org.eclipse.jubula.client.core;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.ITestresultSummaryEventListener;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.TestExecution.PauseMode;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.communication.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.core.communication.ConnectionException;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.registration.AutIdentifier;


/**
 * @author BREDEX GmbH
 * @created 21.07.2005
 */
public interface IClientTest {
    /**
     * connects to server
     * @param autAgentHostName The name of the server.
     * @param port The port number.
     */
    public abstract void connectToAutAgent(
            String autAgentHostName, String port);

    /**
     * disconnects from the Server
     *
     */
    public abstract void disconnectFromAutAgent();

    /**
     * start the application under test.
     * @param conf AutConf
     * @param locale the Locale to start AUT with
     * @param aut the IAUTMainPO the AUT.
     * @throws ToolkitPluginException if the toolkit for the AUT cannot be found
     */
    public abstract void startAut(IAUTMainPO aut, IAUTConfigPO conf, 
        Locale locale) throws ToolkitPluginException;

    /**
     * Stops the Running AUT with the given ID.
     * 
     * @param autId The ID of the Running AUT to stop.
     */
    public abstract void stopAut(AutIdentifier autId);

    /**
     * Starts the object mapping. <br>
     * @param mod key modifier
     * @param inputCode the code representing the input that will collect a 
     *                  UI element
     * @param inputType the type of input that will trigger UI collection
     *                  (key press, mouse click, etc.)
     * @param autId The ID of the AUT for which to start the Object Mapping.
     * 
     * @throws ConnectionException
     * @throws NotConnectedException
     * @throws CommunicationException
     */
    public abstract void startObjectMapping(AutIdentifier autId, int mod, 
            int inputCode, int inputType) throws ConnectionException, 
            NotConnectedException, CommunicationException;

    /**
     * Starts the record mode. <br>
     * @param spec  SpecTestCasePO
     * @param compNamesMapper The ComponentNamesDecorator associated with the 
     *                        edit session of the spec test case.
     * @param recordCompMod key modifier
     * @param recordCompKey key
     * @param recordApplMod key modifier
     * @param recordApplKey key
     * @param checkModeKeyMod key modifier
     * @param checkModeKey key
     * @param checkCompKeyMod key modifier
     * @param checkCompKey key
     * @param dialogOpen boolean
     * @param singleLineTrigger SortedSet
     * @param multiLineTrigger SortedSet
     * @param locale Locale
     */
    public abstract void startRecordTestCase(ISpecTestCasePO spec,
            IWritableComponentNameMapper compNamesMapper, int recordCompMod,
            int recordCompKey, int recordApplMod, int recordApplKey,
            int checkModeKeyMod, int checkModeKey, int checkCompKeyMod,
            int checkCompKey, boolean dialogOpen,
            SortedSet<String> singleLineTrigger,
            SortedSet<String> multiLineTrigger, Locale locale);

    /**
     * Finishes the the object mapping. <br> 
     */
    public abstract void resetToTesting();

    /**
     * Starts the testsuite
     * 
     * @param execTestSuite
     *            The testSuite to be tested
     * @param locale
     *            The locale to be tested
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     * @param autId
     *            The ID of the Running AUT on which the test will take place.
     * @param externalVars
     *            a map of externally set variables; may be <code>null</code>
     */
    public abstract void startTestSuite(ITestSuitePO execTestSuite,
        Locale locale, AutIdentifier autId, boolean autoScreenshot,
        Map<String, String> externalVars);

    /**
     * Starts the given Test Job.
     * 
     * @param testJob The Test Job to start.
     * @param locale The locale to be tested.
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     */
    public abstract void startTestJob(ITestJobPO testJob, Locale locale,
            boolean autoScreenshot);
    
    /**
     * Stops test execution.
     */
    public abstract void stopTestExecution();

    /**
     * Pauses test exectuion.
     * 
     * @param pm
     *            the pause mode to use
     */
    public abstract void pauseTestExecution(PauseMode pm);
    
    /**
     * set pause test execution on error flag.
     * 
     * @param pauseOnError
     *            whether the test execution should automatically pause in case
     *            of errors
     */
    public abstract void pauseTestExecutionOnError(boolean pauseOnError);

    /**
     * @return get the pause test execution on error flag.
     */
    public abstract boolean isPauseTestExecutionOnError();
    
    /**
     * adds an IAUTServerEventListener
     * 
     * @param listener -
     *            the listener to add
     */
    public void addAUTServerEventListener(
            IAUTServerEventListener listener);

    /**
     * add an AutStarterEventListener
     * 
     * @param listener -
     *            the listener to add
     */
    public void addAutAgentEventListener(
            IServerEventListener listener);

    /**
     * add an AUTEventListener
     * 
     * @param listener -
     *            the listener to add
     */
    public void addTestEventListener(IAUTEventListener listener);

    /**
     * add a TestExecutionEventListener
     * @param listener a TestExecutionEventListener
     */
    public void addTestExecutionEventListener(
            ITestExecutionEventListener listener);
    
    /**
     * add a TestresultSummaryEventListener
     * @param listener a TestresultSummaryEventListener
     */
    public void addTestresultSummaryEventListener(
            ITestresultSummaryEventListener listener);

    /**
     * Notify all listeners that have registered interest for notification on
     * changing the state of the connection to the AUTServer.
     * 
     * @param event
     *            the event containing detailed information about the new state
     */
    public void fireAUTServerStateChanged(AUTServerEvent event);

    /**
     * Notify all listeners that have registered interest for notification on
     * this event type.
     * 
     * @param event
     *            the event containing detailed information about the new state
     */
    public void fireAUTStateChanged(AUTEvent event);
    
    /**
     * 
     */
    public void fireEndTestExecution();
    
    /**
     * notify listeners, that test result summary has changed
     */
    public void fireTestresultSummaryChanged();

    /**
     * Notify all listeners that have registered interest for notification on
     * changing the state of the connection to the AUT Agent.
     * 
     * @param event
     *            the event containing detailed information about the new state
     */
    public void fireAutAgentStateChanged(AutAgentEvent event);

    /**
     * Notify all listeners that have registered interest for notification on
     * changing the state of the TestExecution
     * @param event The event containing the information about the state
     */
    public void fireTestExecutionChanged(TestExecutionEvent event);

    /**
     * removes an AUTServerListener
     * 
     * @param listener -
     *            the listener to remove
     */
    public void removeAUTServerEventListener(
            IAUTServerEventListener listener);

    /**
     * remove an AutStarterListener
     * 
     * @param listener -
     *            the listener to remove
     */
    public void removeAutAgentEventListener(
            IServerEventListener listener);

    /**
     * remove an AUTListener
     * 
     * @param listener -
     *            the listener to remove
     */
    public void removeTestEventListener(IAUTEventListener listener);

    /**
     * Remove a TestExecutionEventListener
     * @param listener The listener to remove.
     */
    public void removeTestExecutionEventListener(
            ITestExecutionEventListener listener);
    
    /**
     * Remove a ITestresultSummaryEventListener
     * @param listener The listener to remove.
     */
    public void removeTestresultSummaryEventListener(
            ITestresultSummaryEventListener listener);
    
    /**
     * @return Returns the endTime.
     */
    public Date getEndTime();
    

    /**
     * @return Returns the test suite startTime.
     */
    public Date getTestsuiteStartTime();
    
    /**
     * @return Returns the test job startTime.
     */
    public Date getTestjobStartTime();
    
    /**
     * @param logPath The logPath to set.
     */
    public void setLogPath(String logPath);
    
    /**
     * @param logStyle <code>String</code> representing the style the log
     *                 use (for example, Complete or Errors only)
     */
    public void setLogStyle(String logStyle);

    /**
     * @param relevant
     *            whether the upcoming test execution is relevant for long term
     *            reporting
     */
    public void setRelevantFlag(boolean relevant);
    
    /**
     * @return  whether the upcoming test execution is relevant for long term
     *            reporting
     */
    public boolean isRelevant();
    
    /**
     * Writes a test result report of the executed test on disc
     * after the test is finished.
     */
    public void writeReport();
    
    /**
     * Writes test result to database
     */
    public void writeTestresultToDB();

    /**
     * sending a request to the agent to get the config map from the last
     * connected AUT.
     * @param autId the AUT id to retrieve the map for
     * @return null if no config map available; otherwise the map
     */
    public Map<String, String> requestAutConfigMapFromAgent(String autId);
}