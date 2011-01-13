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
package org.eclipse.jubula.client.ui.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.AUTEvent;
import org.eclipse.jubula.client.core.AUTServerEvent;
import org.eclipse.jubula.client.core.AutStarterEvent;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.IAUTEventListener;
import org.eclipse.jubula.client.core.IAUTServerEventListener;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.IServerEventListener;
import org.eclipse.jubula.client.core.ServerEvent;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.commands.AUTModeChangedCommand;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.communication.ConnectionException;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.controllers.jobs.StartAutJob;
import org.eclipse.jubula.client.ui.provider.labelprovider.OMEditorTreeLabelProvider;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.communication.message.ChangeAUTModeMessage;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TimingConstantsClient;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.jubula.tools.utils.TimeUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;


/**
 * @author BREDEX GmbH
 * @created 20.07.2004
 */
public class TestExecutionContributor
    implements IAUTEventListener, IServerEventListener,
    IAUTServerEventListener , ITestExecutionEventListener {

    /** instance of this class */
    private static TestExecutionContributor instance;
    
    /** the logger */
    private static Log log = LogFactory
        .getLog(TestExecutionContributor.class);
    
    /** ClientTest */
    private IClientTest m_clientTest;
    /** the started server */
    private String m_server = StringConstants.EMPTY; 
    /** the port number of the started server */
    private String m_port = StringConstants.EMPTY;
    
    /**
     * Creates an empty editor action bar contributor
     */
    private TestExecutionContributor () {
        super();
        setClientTest(ClientTestFactory.getClientTest());
        registerAsListener();
    }
    
    /**
     * add this Contributor as listener for AUTEvents, AutStarterEvents and
     * AUTServerEvents. Deregistering happens in deregister(), called from
     * dispose()..
     */
    private void registerAsListener() {
        ClientTestFactory.getClientTest().addTestEventListener(this);
        ClientTestFactory.getClientTest().addAutStarterEventListener(this);
        ClientTestFactory.getClientTest().addAUTServerEventListener(this);
        ClientTestFactory.getClientTest().addTestExecutionEventListener(this);
    }

    /**
     * deregister this as listener
     */
    private void deregister() {
        ClientTestFactory.getClientTest().removeTestEventListener(this);
        ClientTestFactory.getClientTest().removeAutStarterEventListener(
            this);
        ClientTestFactory.getClientTest().removeAUTServerEventListener(this);
        ClientTestFactory.getClientTest()
            .removeTestExecutionEventListener(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // remove this as EventListener from ClientTest
        deregister();
        // free ClientTest reference
        setClientTest(null);
    }

    /**
     * @param event AUTEvent
     */
    public void stateChanged(final AUTEvent event) {
        handleEvent(event);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stateChanged(final AutStarterEvent event) {
        handleEvent(event);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stateChanged(final AUTServerEvent event) {
        handleEvent(event);
    }
    
    /**
     * {@inheritDoc}
     * @param event The event.
     */
    public void stateChanged(final TestExecutionEvent event) {
        handleEvent(event);
    }
    
    /**
     * @return Returns the clientTest.
     */
    public IClientTest getClientTest() {
        return m_clientTest;
    }
    
    /**
     * @param clientTest The clientTest to set.
     */
    public void setClientTest(IClientTest clientTest) {
        m_clientTest = clientTest;
    }
    
    /**
     * display a text for an AUTEvent in the status line
     * @param event the raised event, determines the displayed message 
     */
    void handleEvent(AUTEvent event) {
        log.info("handle AUTEvent: " + event.toString()); //$NON-NLS-1$
        String message = Plugin.getStatusLineText();
        String error = null;
        int icon = Plugin.isConnectionStatusIcon();
        switch (event.getState()) {
            case AUTEvent.AUT_STARTED: 
                message = I18n.getString("TestExecutionContributor.AUTStartedTesting");  //$NON-NLS-1$
                icon = Constants.AUT_UP;
                fireAndSetAutState(true);
                AUTModeChangedCommand.setAutMode(ChangeAUTModeMessage.TESTING);
                break;
            case AUTEvent.AUT_ABORTED:
                fireAndSetAutState(false);
                break;
            case AUTEvent.AUT_STOPPED:
                message = I18n.getString("TestExecutionContributor.AUTStopped");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;
            case AUTEvent.AUT_NOT_FOUND:
                error = I18n.getString("TestExecutionContributor.AUTNotFound");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;
            case AUTEvent.AUT_MAIN_NOT_FOUND:
                error = I18n.getString("TestExecutionContributor.mainClassNotFound");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;  
            case AUTEvent.AUT_CLASS_VERSION_ERROR:
                error = I18n.getString("TestExecutionContributor.ClassVersionError");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;  
            case AUTEvent.AUT_START_FAILED:
                error = StringConstants.EMPTY;
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;  
            case AUTEvent.AUT_RESTARTED:
                fireAndSetAutState(true);
                // only important for TestExecution
                break;
            default:
                Assert.notReached("unknown AUT state: " + String.valueOf(event.getState())); //$NON-NLS-1$
                break;  
        }
        Plugin.showStatusLine(icon, message); 
        if (error != null) {            // show error
            if (StringConstants.EMPTY.equals(error)) {
                error = null;
            }
            Utils.createMessageDialog(MessageIDs.E_AUT_START, null, error.split("\n")); //$NON-NLS-1$
        }
    }
    
    /**
     * @param isAutRunning flag, if aut is running or not
     */
    private void fireAndSetAutState(boolean isAutRunning) {
        AutState state = isAutRunning ? AutState.running : AutState.notRunning;
        DataEventDispatcher.getInstance().fireAutStateChanged(state);
    }

    /**
     * display a text for an AUTServerEvent in the status line, also
     * enables/disables the actions 
     * @param event the raised event, determines the displayed message
     */
    void handleEvent(AUTServerEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("handle AUTServerEvent: " + event.toString()); //$NON-NLS-1$            
        }
        int icon = Plugin.isConnectionStatusIcon();
        String message = Plugin.getStatusLineText();
        String error = null;
        switch (event.getState()) {
            case ServerEvent.CONNECTION_CLOSED:                
                message = getConnectionMessage(message);
                message = message + " - " + I18n.getString("TestExecutionContributor.connAUTServClosed"); //$NON-NLS-1$ //$NON-NLS-2$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case ServerEvent.CONNECTION_GAINED:
                message = I18n.getString("TestExecutionContributor.connectedToAUTServer"); //$NON-NLS-1$
                icon = Constants.NO_AUT;
                // no changes for actions, see stateChanged(AUTEvent);
                break;
            case AUTServerEvent.SERVER_NOT_INSTANTIATED:
                error = I18n.getString("TestExecutionContributor.serverNotInstantiated");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.DOTNET_INSTALL_INVALID:
                error = I18n.getString("TestExecutionContributor.DotNetInstallProblem");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.JDK_INVALID:
                error = I18n.getString("TestExecutionContributor.UnrecognizedJavaAgent");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.INVALID_JAVA:
                error = I18n.getString("TestExecutionContributor.startingJavaFailed");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.COULD_NOT_ACCEPTING:
                error = I18n.getString("TestExecutionContributor.openingConnAUTServerFailed");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.COMMUNICATION:
                error = I18n.getString("TestExecutionContributor.establishingCommunicationAUTServerFailed"); //$NON-NLS-1$ 
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.INVALID_JAR:
                error = I18n.getString("TestExecutionContributor.invalidJar");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.NO_MAIN_IN_JAR:
                error = I18n.getString("TestExecutionContributor.noMainInJar");  //$NON-NLS-1$
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.TESTING_MODE:
            case AUTServerEvent.MAPPING_MODE:
            case AUTServerEvent.RECORD_MODE:
            case AUTServerEvent.CHECK_MODE:
                autServerModeChanged(event);
                return;             
            default:
                setAutNotRunningState();
                log.error("unknown AUTServer state: " + event.getState()); //$NON-NLS-1$
        }
        showError(icon, message, error);
    }

    /**
     * show an error info
     * @param icon display info
     * @param message display info
     * @param error display info
     */
    private void showError(int icon, String message, String error) {
        Plugin.showStatusLine(icon, message); 
        if (error != null) {            // show error
            if (StringConstants.EMPTY.equals(error)) {
                showError(null);
            } else {
                showError(error);
            }
        }
    }

    /**
     * @param errorHolder The error string
     */
    private void showError(final String errorHolder) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                Utils.createMessageDialog(MessageIDs.E_AUT_START, null,
                    errorHolder.split("\n")); //$NON-NLS-1$
            }
        });
    }

    /**
     * @param message the serverConnectionStatusMessage to show in status bar
     * @return the serverConnectionStatusMessage to show in status bar
     */
    private String getConnectionMessage(String message) {
        String msg = message;
        try {
            msg = I18n.getString("TestExecutionContributor.connectedToGuiDancerServer1",  //$NON-NLS-1$
                new Object[]{m_server, m_port});
            if (I18n.getString("StartAutBP.localhost").equals(m_server.toLowerCase())) { //$NON-NLS-1$
                msg = I18n.getString("TestExecutionContributor.connectedToGuiDancerServer2",  //$NON-NLS-1$
                    new Object[]{m_server, m_port});
            } else if (m_server.equals(InetAddress.getLocalHost().getHostName())
                || m_server.equals(InetAddress.getLocalHost().getHostAddress())
                || m_server.equals(InetAddress.getLocalHost()
                    .getCanonicalHostName())) {
                
                msg = I18n.getString("TestExecutionContributor.connectedToGuiDancerServer3",  //$NON-NLS-1$
                    new Object[]{m_server, I18n.getString("StartAutBP.localhost"), m_port}); //$NON-NLS-1$
            }
        } catch (UnknownHostException e) {
            // really do nothing
        }
        return msg;
    }

    /**
     * 
     */
    private void setAutNotRunningState() {
        DataEventDispatcher.getInstance().fireAutStateChanged(
            AutState.notRunning);
    }

    /**
     * set the status Line for changed mode
     * @param e AUTServerEvent
     */
    private void autServerModeChanged(AUTServerEvent e) {
        int icon = Plugin.isConnectionStatusIcon();
        String message = Plugin.getStatusLineText();
        switch (e.getState()) {
            case AUTServerEvent.TESTING_MODE:
                message = I18n.getString("TestExecutionContributor.AUTStartedTesting");  //$NON-NLS-1$
                icon = Constants.AUT_UP;
                break;
            case AUTServerEvent.MAPPING_MODE:
                String strCat;
                IObjectMappingCategoryPO cat =
                    ObjectMappingEventDispatcher.getCategoryToCreateIn();
                if (cat != null) {
                    strCat = cat.getName();
                    if (OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat) != null) {
                        strCat = OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat);
                    }
                } else {
                    strCat = I18n.getString("TestExecutionContributor.CatUnassigned"); //$NON-NLS-1$
                }
                
                message = I18n.getString("TestExecutionContributor.AUTStartedMapping",  //$NON-NLS-1$
                    new Object[] {strCat});  
                icon = Constants.MAPPING;
                break;
            case AUTServerEvent.RECORD_MODE:
                //message = I18n.getString("TestExecutionContributor.AUTStartedRecording");  //$NON-NLS-1$
                message = I18n.getString("TestExecutionContributor.AUTStartedRecording"); //$NON-NLS-1$
                icon = Constants.RECORDING;
                break;
            case AUTServerEvent.CHECK_MODE:
                //message = I18n.getString("TestExecutionContributor.AUTStartedRecordingCheckMode");  //$NON-NLS-1$
                message = I18n.getString("TestExecutionContributor.AUTStartedRecordingCheckMode"); //$NON-NLS-1$
                icon = Constants.CHECKING;
                break;
            default:
                break;
        }
        Plugin.showStatusLine(icon, message); 
    }
    
    /**
     * display a text for an AutStarterEvent in the status line 
     * @param event the raised event, determines the displayed message
     */
    void handleEvent(AutStarterEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("handle AUT Agent Event: " + event.toString()); //$NON-NLS-1$
        } 
        String statusLineMessage = Plugin.getStatusLineText();
        Integer messageId = MessageIDs.E_SERVER_ERROR;
        String error = null;
        int icon = Plugin.isConnectionStatusIcon();
        switch (event.getState()) {
            case ServerEvent.CONNECTION_CLOSED:
                statusLineMessage = I18n.getString(
                        "StatusLine.notConnected");  //$NON-NLS-1$
                icon = Constants.NO_SERVER;
                DataEventDispatcher.getInstance().fireServerConnectionChanged(
                        ServerState.Disconnected);
                break;
            case ServerEvent.CONNECTION_GAINED:
                statusLineMessage = getConnectionMessage(statusLineMessage);
                icon = Constants.NO_SC;
                DataEventDispatcher.getInstance()
                    .fireServerConnectionChanged(ServerState.Connected);
                // no changing for the actions, see AUTServerEvent
                break;
            case AutStarterEvent.SERVER_CANNOT_CONNECTED:
                error = I18n.getString("InfoDetail.connGuiDancerServerFailed"); //$NON-NLS-1$
                statusLineMessage = I18n.getString("StatusLine.notConnected");  //$NON-NLS-1$
                DataEventDispatcher.getInstance().fireServerConnectionChanged(
                        ServerState.Disconnected);
                messageId = MessageIDs.I_SERVER_CANNOT_CONNECTED;
                break;
            case AutStarterEvent.VERSION_ERROR:
                error = I18n.getString("ErrorMessage.VERSION_ERROR"); //$NON-NLS-1$
                statusLineMessage = I18n.getString("StatusLine.notConnected"); //$NON-NLS-1$
                DataEventDispatcher.getInstance().fireServerConnectionChanged(
                        ServerState.Disconnected);
                break;
            default:
                log.error("unknown AUT Agent state: " + String.valueOf(event.getState())); //$NON-NLS-1$
                break;
        }
        Plugin.showStatusLine(icon, statusLineMessage);
        if (error != null) {
            //  show error
            if (StringConstants.EMPTY.equals(error)) {
                error = null;
            }
            Utils.createMessageDialog(messageId, 
                    new String [] {m_server, m_port}, error.split("\n")); //$NON-NLS-1$
        }       
    }

    /**
     * enables and disables the start and stopTestSuiteActions
     * @param event the event to determins the enabled/disabled actions
     */
    void handleEvent(TestExecutionEvent event) {
        String message = Plugin.getStatusLineText();
        String error = null;
        int icon = Plugin.isConnectionStatusIcon();
        ICapPO cap = null;
        String testCaseName = null;
        String capName = null;
        switch (event.getState()) {
            case TestExecutionEvent.TEST_EXEC_STOP:
                icon = Constants.AUT_UP;
                message = I18n.getString("TestExecutionContributor.SuiteStop"); //$NON-NLS-1$
                setAllTestSuitesStopped();
                break;
            case TestExecutionEvent.TEST_EXEC_FAILED:
                error = getTestSuiteErrorText(event);
                message = I18n.getString("TestExecutionContributor.SuiteFailed"); //$NON-NLS-1$
                setAllTestSuitesStopped();
                break;
            case TestExecutionEvent.TEST_EXEC_START:
                setClientMinimized(true);
                icon = Constants.AUT_UP;
                message = I18n.getString("TestExecutionContributor.SuiteRun");  //$NON-NLS-1$
                showTestResultTreeView();
                break;
            case TestExecutionEvent.TEST_EXEC_RESULT_TREE_READY:
                message = I18n.getString("TestExecutionContributor.SuiteRun"); //$NON-NLS-1$
                TestExecution.getInstance().setStepSpeed(getStepSpeed());
                break;
            case TestExecutionEvent.TEST_EXEC_FINISHED:
                message = I18n.getString("TestExecutionContributor.SuiteFinished"); //$NON-NLS-1$
                icon = Constants.AUT_UP;
                setAllTestSuitesStopped();
                break;
            case TestExecutionEvent.TEST_EXEC_COMPONENT_FAILED:
                cap = TestExecution.getInstance().getActualCap();
                final String componentName = cap.getComponentName();
                testCaseName = cap.getParentNode().getName();
                capName = cap.getName();
                error = I18n.getString("TestExecutionContributor.CompFailure", new Object[]{componentName, testCaseName, capName}); //$NON-NLS-1$
                message = I18n.getString("TestExecutionContributor.SuiteFailed"); //$NON-NLS-1$
                setAllTestSuitesStopped();
                break;
            case TestExecutionEvent.TEST_EXEC_PAUSED:
                setClientMinimized(false);
                icon = Constants.PAUSED;
                message = I18n.getString("TestExecutionContributor.SuitePaused");  //$NON-NLS-1$
                break;
            case TestExecutionEvent.TEST_RUN_INCOMPLETE_TESTDATA_ERROR:
                setClientMinimized(false);
                error = getIncompleteTestRunMessage(TestExecutionEvent
                    .TEST_RUN_INCOMPLETE_TESTDATA_ERROR);
                message = I18n.getString("TestExecutionContributor.SuiteFailed"); //$NON-NLS-1$
                break;
            case TestExecutionEvent.TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR:
                setClientMinimized(false);
                error = getIncompleteTestRunMessage(TestExecutionEvent
                    .TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR);
                message = I18n.getString("TestExecutionContributor.SuiteFailed"); //$NON-NLS-1$
                break;
            case TestExecutionEvent.TEST_EXEC_RESTART:
                icon = Constants.AUT_UP;
                message = I18n.getString("TestExecutionContributor.SuiteRun");  //$NON-NLS-1$
                break;
            default:
                log.error("unknown TestExecutionEvent"); //$NON-NLS-1$
                setAllTestSuitesStopped();
        }
        showErrorAndStatus(event, message, error, icon);
    }



    /**
     * @param event the current TestExecutionEvent
     * @param message the status message
     * @param error the error message
     * @param icon the status icon
     */
    private void showErrorAndStatus(TestExecutionEvent event, String message, 
            String error, int icon) {
        
        DataEventDispatcher.getInstance().fireTestSuiteStateChanged(event);
        Plugin.showStatusLine(icon, message);
        if (error != null) {
            String[] errorDetail = error.split("\n"); //$NON-NLS-1$
            if (StringConstants.EMPTY.equals(error)) {
                errorDetail = null;
            }
            Utils.createMessageDialog(MessageIDs.E_TEST_EXECUTION_ERROR, null, 
                    errorDetail);
        }
    }

    /**
     * Gets the I18N-String of an error occured in an incomplete TestSuite-Run
     * depending of the given TestExecutionEvent-ID
     * @param testExecEventID the TestExecutionEvent-ID
     * @return the I18N-String of the error description.
     */
    private String getIncompleteTestRunMessage(int testExecEventID) {
        ICapPO cap = TestExecution.getInstance().getActualCap();
        String testCaseName = cap.getParentNode().getName();
        String capName = cap.getName();
        switch(testExecEventID) {
            case TestExecutionEvent.TEST_RUN_INCOMPLETE_TESTDATA_ERROR:
                return I18n.getString("TestExecutionContributor.TEST_RUN_INCOMPLETE_TESTDATA_ERROR", //$NON-NLS-1$
                    new Object[]{testCaseName, capName});
            case TestExecutionEvent.TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR:
                return I18n.getString("TestExecutionContributor.TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR", //$NON-NLS-1$
                    new Object[]{testCaseName, capName});
            default:
                log.error("unknown TestExecutionEvent"); //$NON-NLS-1$
                setAllTestSuitesStopped();  
        }
        return StringConstants.EMPTY;
    }

    /**
     * minimize/maximize when preference is set
     * 
     * @param flag
     *            boolean
     */
    public static void setClientMinimized(final boolean flag) {
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
                Constants.MINIMIZEONSUITESTART_KEY)) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Shell shell = Plugin.getActiveWorkbenchWindowShell();
                    boolean foundShell = shell != null ? true : false;
                    if (!foundShell) {
                        shell = Plugin.getDisplay().getActiveShell();
                        foundShell = shell != null ? true : false;
                    }
                    if (!foundShell) {
                        shell = Display.getDefault().getActiveShell();
                        foundShell = shell != null ? true : false;
                    }
                    if (!foundShell) {
                        shell = Plugin.getShell();
                        foundShell = shell != null ? true : false;
                    }
                    shell.setMinimized(flag);
                    if (shell.getMinimized() != flag) {
                        shell.setMaximized(!flag);
                    }
                }
            });
        }
    }
    
    /**
     * returns an Error Text for displaying in GUI
     * @param event TestExecutionEvent
     * @return Text
     */
    private String getTestSuiteErrorText(TestExecutionEvent event) {
        if (event.getException() != null) {
            return event.getException().getMessage();    
        }
        return I18n.getString("TestExecutionContributor.SuiteFailed"); //$NON-NLS-1$
    }

    /**
     * Shows the TestResultTreeView
     */
    private void showTestResultTreeView() {
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
                Constants.OPENRESULTVIEW_KEY)) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    try {
                        Plugin.getActivePage().showView("org.eclipse.jubula.client.ui.views.TestResultTreeView"); //$NON-NLS-1$
                    } catch (PartInitException pie) {
                        log.error("TestResultTreeView could not be initialised", pie); //$NON-NLS-1$
                    } catch (NullPointerException npe) {
                        log.error("Window is null", npe); //$NON-NLS-1$
                    }
                }
            });
        }
    }
    
    /**
     * @return Returns the stepSpeed 
     */
    private int getStepSpeed() {
        return TestExecution.getInstance().getStartedTestSuite().getStepDelay();
    }
    
    /**
     * the startAUT action
     * @param conf configuration to use for AUT
     * @param aut The IAUTMainPO
     */
    public void startAUTaction(final IAUTMainPO aut, final IAUTConfigPO conf) 
        throws ToolkitPluginException {
        Validate.notNull(conf, "Configuration must not be null."); //$NON-NLS-1$
        Job job = new StartAutJob(aut, conf);
        JobUtils.executeJob(job, null);
    }

    /**
     * the connectServer action
     * @param server The server to start.
     * @param port The port of the server to start.
     */
    public void connectToServeraction(String server, String port) {
        m_server = server;
        m_port = port;
        getClientTest().connectToServer(server, port);
    }

    /**
     * the disconnectServer action
     */
    public void disconnectFromServeraction() {
        getClientTest().disconnectFromServer();
        m_server = StringConstants.EMPTY;
        m_port = StringConstants.EMPTY;
    }

    /**
     * Stops the Running AUT with the given ID.
     * 
     * @param autId The ID of the Running AUT to stop.
     */
    public  void stopAUTaction(AutIdentifier autId) {
        fireAndSetAutState(false);
        if (TestExecution.getInstance().getStartedTestSuite() != null
            && TestExecution.getInstance().getStartedTestSuite().isStarted()) {
            
            stopTestSuiteAction();
        }
        getClientTest().stopAut(autId);
    }
    
    /**
     * Starts the testSuiteExecution
     * @param ts The runnableTestSuite.
     * @param autId The ID of the Running AUT on which the test will take place.
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     */
    public void startTestSuiteAction(ITestSuitePO ts, 
            AutIdentifier autId, boolean autoScreenshot) {
        TimeUtil.delay(TimingConstantsClient.START_TEST_SUITE_DELAY);
        getClientTest().startTestSuite(ts, WorkingLanguageBP.getInstance()
            .getWorkingLanguage(), autId, autoScreenshot);
    }

    /**
     * Stops the TestSuiteExecution
     */
    public void stopTestSuiteAction() {
        getClientTest().stopTestExecution();
    }

    /**
     * Stops the TestSuiteExecution
     */
    public void pauseTestSuiteAction() {
        getClientTest().pauseTestExecution();
    }
    
    /**
     * @return Returns the instance.
     */
    public static TestExecutionContributor getInstance() {
        if (instance == null) {
            instance = new TestExecutionContributor();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public void endTestExecution() {
        // nothing
    }
    
    /**
     * Sets all TestSuites stopped.
     */
    private void setAllTestSuitesStopped() {
        setClientMinimized(false);
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project == null) {
            return;
        }
        List<ITestSuitePO> tsList = project.getTestSuiteCont()
            .getTestSuiteList();
        for (ITestSuitePO ts : tsList) {
            ts.setStarted(false);
        }
        if (TestExecution.getInstance().getStartedTestSuite() != null) {
            TestExecution.getInstance().getStartedTestSuite().setStarted(false);
        }
        try {
            AUTConnection.getInstance().getCommunicator()
                .interruptAllTimeouts();
        } catch (ConnectionException e) {
            log.error(e);
        }
    }
}
