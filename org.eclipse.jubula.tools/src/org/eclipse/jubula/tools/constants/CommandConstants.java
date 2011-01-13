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
package org.eclipse.jubula.tools.constants;

/**
 * @author BREDEX GmbH
 * @created 19.12.2006
 *
 * 
 *
 *
 *
 */
public abstract class CommandConstants {
    /** constant: "org.eclipse.jubula.rc.swt.SwtAUTServer" */
    public static final String AUT_SWT_SERVER = "org.eclipse.jubula.rc.swt.SwtAUTServer"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.swing.SwingAUTServer" */
    public static final String AUT_SWING_SERVER = "org.eclipse.jubula.rc.swing.SwingAUTServer"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.AutServerLauncher" */
    public static final String AUT_SERVER_LAUNCHER = "org.eclipse.jubula.rc.common.AutServerLauncher"; //$NON-NLS-1$
    /** constant "abstract" */
    public static final String ABSTRACT_TOOLKIT = "com.bredexsw.guidancer.AbstractToolkitPlugin"; //$NON-NLS-1$
    /** constant "concrete" */
    public static final String CONCRETE_TOOLKIT = "com.bredexsw.guidancer.ConcreteToolkitPlugin"; //$NON-NLS-1$
    /** constant: "Swt" */
    public static final String SWT_TOOLKIT = "com.bredexsw.guidancer.SwtToolkitPlugin"; //$NON-NLS-1$
    /** constant: "Swing" */
    public static final String SWING_TOOLKIT = "com.bredexsw.guidancer.SwingToolkitPlugin"; //$NON-NLS-1$
    /** constant: "Rcp" */
    public static final String RCP_TOOLKIT = "com.bredexsw.guidancer.RcpToolkitPlugin"; //$NON-NLS-1$
    /** constant: "Web" */
    public static final String WEB_TOOLKIT = "com.bredexsw.guidancer.WebToolkitPlugin"; //$NON-NLS-1$
    /** constant: "Html" */
    public static final String HTML_TOOLKIT = "com.bredexsw.guidancer.HtmlToolkitPlugin"; //$NON-NLS-1$
    /** constant: "../AutServer/bin" */
    public static final String AUT_SERVER_BIN = "../org.eclipse.jubula.rc.swing/bin"; //$NON-NLS-1$
    /** constant: "../AutSWTServer/bin" */
    public static final String AUT_SWT_SERVER_BIN = "../org.eclipse.jubula.rc.swt/bin"; //$NON-NLS-1$
    /** constant: "./lib/AUTServer.jar" */
    public static final String AUT_SERVER_JAR = "./lib/org.eclipse.jubula.rc.swing.jar"; //$NON-NLS-1$
    /** constant: "./lib/AUTSwtServer.jar" */
    public static final String AUT_SWT_SERVER_JAR = "./lib/org.eclipse.jubula.rc.swt.jar"; //$NON-NLS-1$
    /** /** constant: "./lib/extImplClasses" - path name to external ImplClasses */
    public static final String EXT_IMPLCLASSES_PATH = "./lib/extImplClasses"; //$NON-NLS-1$
    /** constant: "../AUTServerBase/bin" */
    public static final String AUT_SERVER_BASE_BIN = "../org.eclipse.jubula.rc.common/bin"; //$NON-NLS-1$
    /** constant: "./lib/AUTServerBase.jar" */
    public static final String AUT_SERVER_BASE_JAR = "./lib/org.eclipse.jubula.rc.common.jar"; //$NON-NLS-1$
    /** constant: "./AUTLauncher.jar" */
    public static final String AUT_LAUNCHER_JAR = "./AUTLauncher.jar"; //$NON-NLS-1$
    /** constant: "./GDAgent.jar" */
    public static final String GDAGENT_JAR = "./org.eclipse.jubula.rc.common.agent.jar"; //$NON-NLS-1$
    /** constant: "./resources" */
    public static final String AUT_SERVER_RESOURCES = "./resources"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.swing.commands.ActivateApplicationCommand" */
    public static final String ACTIVATE_SWING_APPLICATION_COMMAND = "org.eclipse.jubula.rc.swing.commands.ActivateApplicationCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.swt.commands.ActivateApplicationCommand" */
    public static final String ACTIVATE_SWT_APPLICATION_COMMAND = "org.eclipse.jubula.rc.swt.commands.ActivateApplicationCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.AUTHighlightComponentCommand" */
    public static final String AUT_HIGHLIGHT_COMMAND = "org.eclipse.jubula.rc.common.commands.AUTHighlightComponentCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.AUTStartCommand" */
    public static final String AUT_START_COMMAND = "org.eclipse.jubula.rc.common.commands.AUTStartCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.swt.commands.AUTStartCommand" */
    public static final String SWING_CAP_TEST_COMMAND = "org.eclipse.jubula.rc.swing.commands.CAPTestCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.swt.commands.CAPTestCommand" */
    public static final String SWT_CAP_TEST_COMMAND = "org.eclipse.jubula.rc.swt.commands.CAPTestCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.html.commands.CAPTestCommand" */
    public static final String HTML_CAP_TEST_COMMAND = "org.eclipse.jubula.rc.html.commands.CAPTestCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.ChangeAUTModeCommand" */
    public static final String CHANGE_AUT_MODE_COMMAND = "org.eclipse.jubula.rc.common.commands.ChangeAUTModeCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.swing.commands.ChangeAUTModeCommand" */
    public static final String SWING_SEND_COMPONENTS_COMMAND = "org.eclipse.jubula.rc.swing.commands.SendAUTListOfSupportedComponentsCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.swt.commands.SendAUTListOfSupportedComponentsCommand" */
    public static final String SWT_SEND_COMPONENTS_COMMAND = "org.eclipse.jubula.rc.swt.commands.SendAUTListOfSupportedComponentsCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.html.commands.SendAUTListOfSupportedComponentsCommand" */
    public static final String HTML_SEND_COMPONENTS_COMMAND = "org.eclipse.jubula.rc.html.commands.SendAUTListOfSupportedComponentsCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.AUTHighlightComponentCommand" */
    public static final String HIGHLIGHT_COMPONENT_COMMAND = "org.eclipse.jubula.client.core.commands.AUTHighlightComponentCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.AUTModeChangedCommand" */
    public static final String AUT_MODE_CHANGED_COMMAND = "org.eclipse.jubula.client.core.commands.AUTModeChangedCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.AUTServerStateCommand" */
    public static final String AUT_SERVER_STATE_COMMAND = "org.eclipse.jubula.client.core.commands.AUTServerStateCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.AUTStartedCommand" */
    public static final String AUT_STARTED_COMMAND = "org.eclipse.jubula.client.core.commands.AUTStartedCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.CAPRecordedCommand" */
    public static final String CAP_RECORDED_COMMAND = "org.eclipse.jubula.client.core.commands.CAPRecordedCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.ShowRecordedActionCommand" */
    public static final String SHOW_RECORDED_ACTION_COMMAND = "org.eclipse.jubula.autagent.commands.ShowRecordedActionCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.ShowObservInfoCommand" */
    public static final String SHOW_OBSERVE_INFO_COMMAND = "org.eclipse.jubula.autagent.commands.ShowObservInfoCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.CAPTestResponseCommand" */
    public static final String CAP_TEST_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.CAPTestResponseCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.RcpAccessorStartedCommand" */
    public static final String RCP_STARTED_COMMAND = "org.eclipse.jubula.client.core.commands.RcpAccessorStartedCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.NullCommand" */
    public static final String NULL_COMMAND = "org.eclipse.jubula.rc.common.commands.NullCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.ObjectMappedCommand" */
    public static final String OBJECT_MAPPED_COMMAND = "org.eclipse.jubula.client.core.commands.ObjectMappedCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.SendServerLogCommand" */
    public static final String SEND_SERVER_LOG_COMMAND = "org.eclipse.jubula.autagent.commands.SendServerLogCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.ServerShowDialogCommand" */
    public static final String SERVER_SHOW_OBSERV_CONSOLE_COMMAND = "org.eclipse.jubula.autagent.commands.ServerShowObservConsoleCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.ServerShowActionShellCommand" */
    public static final String SERVER_SHOW_DIALOG_COMMAND = "org.eclipse.jubula.autagent.commands.ServerShowDialogCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.ShowDialogResultCommand" */
    public static final String SERVER_SHOW_DIALOG_RESULT_COMMAND = "org.eclipse.jubula.rc.common.commands.ShowDialogResultCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.RecordActionCommand" */
    public static final String RECORD_ACTION_COMMAND = "org.eclipse.jubula.autagent.commands.RecordActionCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.inspector.ui.commands.InspectorComponentSelectedCommand" */
    public static final String INSPECTOR_COMPONENT_SELECTED_COMMAND = "org.eclipse.jubula.client.inspector.ui.commands.InspectorComponentSelectedCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.StartAUTServerCommand" */
    public static final String START_AUT_SERVER_COMMAND = "org.eclipse.jubula.autagent.commands.StartAUTServerCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.StartAUTServerStateCommand" */
    public static final String START_AUT_SERVER_STATE_COMMAND = "org.eclipse.jubula.client.core.commands.StartAUTServerStateCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.autagent.commands.StopAUTServerCommand" */
    public static final String STOP_AUT_SERVER_COMMAND = "org.eclipse.jubula.autagent.commands.StopAUTServerCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.StopAUTServerStateCommand" */
    public static final String STOP_AUT_SERVER_STATE_COMMAND = "org.eclipse.jubula.client.core.commands.StopAUTServerStateCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.ActivateInspectorCommand" */
    public static final String ACTIVATE_INSPECTOR_COMMAND = "org.eclipse.jubula.rc.common.commands.ActivateInspectorCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.inspector.ui.commands.ActivateInspectorResponseCommand" */
    public static final String ACTIVATE_INSPECTOR_RESPONSE_COMMAND = "org.eclipse.jubula.client.inspector.ui.commands.ActivateInspectorResponseCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.InitTestExecutionCommand" */
    public static final String INIT_TEST_EXECUTION_COMMAND = "org.eclipse.jubula.rc.common.commands.InitTestExecutionCommand"; //$NON-NLS-1$
    /** package for swing implclasses: "org.eclipse.jubula.rc.swing.swing.implclasses" */
    public static final String SWING_IMPLCLASS_PACKAGE = "org.eclipse.jubula.rc.swing.swing.implclasses"; //$NON-NLS-1$
    /** package for swt implclasses: "org.eclipse.jubula.rc.swt.implclasses"  */
    public static final String SWT_IMPLCLASSES_PACKAGE = "org.eclipse.jubula.rc.swt.implclasses"; //$NON-NLS-1$
    /** package name for GUIdancer extension ImplClasses (".guidancerextension.") */
    public static final String GUIDANCER_EXTENSION_PACKAGE = ".guidancerextension."; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.ServerLogResponseCommand"  */
    public static final String SERVER_LOG_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.ServerLogResponseCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.rc.common.commands.TakeScreenshotCommand"  */
    public static final String TAKE_SCREENSHOT_COMMAND = "org.eclipse.jubula.rc.common.commands.TakeScreenshotCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands..TakeScreenshotResponseCommand"  */
    public static final String TAKE_SCREENSHOT_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.TakeScreenshotResponseCommand"; //$NON-NLS-1$
    
    /** constant: "org.eclipse.jubula.rc.common.commands.EndTestExecutionCommand"  */
    public static final String END_TESTEXECUTION_COMMAND = "org.eclipse.jubula.rc.common.commands.EndTestExecutionCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.EndTestExecutionResponseCommand"  */
    public static final String END_TESTEXECUTION_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.EndTestExecutionResponseCommand"; //$NON-NLS-1$

    /** constant: "org.eclipse.jubula.autagent.commands.DisconnectFromAutAgentCommand" */
    public static final String DISCONNECT_FROM_AUT_AGENT_COMMAND = "org.eclipse.jubula.autagent.commands.DisconnectFromAutAgentCommand"; //$NON-NLS-1$
    /** constant: "org.eclipse.jubula.client.core.commands.DisconnectFromAutAgentResponseCommand" */
    public static final String DISCONNECT_FROM_AUT_AGENT_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.DisconnectFromAutAgentResponseCommand"; //$NON-NLS-1$
    
    /** remotely browse for files/directories */
    public static final String SEND_DIRECTORY_COMMAND = "org.eclipse.jubula.autagent.commands.SendDirectoryCommand"; //$NON-NLS-1$
    /** process the directory/files list */
    public static final String PROCESS_DIR_COMMAND = "org.eclipse.jubula.client.ui.businessprocess.HandleRemoteFileBrowsing"; //$NON-NLS-1$

    /** client-side handling for AUT registration with the AUT Agent */
    public static final String AUT_REGISTERED_COMMAND = "org.eclipse.jubula.client.core.commands.AutRegisteredCommand"; //$NON-NLS-1$
    /** client-side handling for AUT deregistration with the AUT Agent */
    public static final String AUT_DEREGISTERED_COMMAND = "org.eclipse.jubula.client.core.commands.AutDeregisteredCommand"; //$NON-NLS-1$

    /** server-side handling to gather a list of AUTs currently registered with the AUT Agent */
    public static final String GET_REGISTERED_AUTS_COMMAND = "org.eclipse.jubula.autagent.commands.GetRegisteredAutListCommand"; //$NON-NLS-1$
    /** client-side handling to gather a list of AUTs currently registered with the AUT Agent */
    public static final String REGISTERED_AUTS_COMMAND = "org.eclipse.jubula.client.core.commands.RegisteredAutListCommand"; //$NON-NLS-1$

    /** sends a message for a Running AUT to connect to a Client */
    public static final String CONNECT_TO_AUT_COMMAND = "org.eclipse.jubula.autagent.commands.ConnectToAutCommand"; //$NON-NLS-1$
    /** client-side handling for the result of an attempt to connect to an AUT */
    public static final String CONNECT_TO_AUT_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.ConnectToAutResponseCommand"; //$NON-NLS-1$

    /** AUT Server - connects to a Client */
    public static final String CONNECT_TO_CLIENT_COMMAND = "org.eclipse.jubula.rc.common.commands.ConnectToClientCommand"; //$NON-NLS-1$
    
    /** AUT Server - prepares for shutdown via Stop AUT */
    public static final String PREPARE_FOR_SHUTDOWN_COMMAND = "org.eclipse.jubula.rc.common.commands.PrepareForShutdownCommand"; //$NON-NLS-1$
    
    /** AUT Server - restarts an AUT */
    public static final String RESTART_AUT_COMMAND = "org.eclipse.jubula.autagent.commands.RestartAutCommand"; //$NON-NLS-1$
    
    /** AUT Agent - display the manual test step to perform */
    public static final String DISPLAY_MANUAL_TEST_STEP_COMMAND = "org.eclipse.jubula.autagent.commands.DisplayManualTestStepCommand"; //$NON-NLS-1$
    
    /** client-side handling for the result of an attempt to display the manual test step to perform */
    public static final String DISPLAY_MANUAL_TEST_STEP_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.DisplayManualTestStepResponseCommand"; //$NON-NLS-1$
    /** client-side handling of incoming monitoring value */
    public static final String GET_MONITORING_DATA_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.GetMonitoringDataResponseCommand"; //$NON-NLS-1$
    /** the generated report will be send to the client */   
    public static final String GET_MONITORING_REPORT_COMMAND = "org.eclipse.jubula.client.core.commands.GetMonitoringReportCommand"; //$NON-NLS-1$
    /** forces the profiling agent to send his execution data  */  
    public static final String GET_MONITORING_DATA_COMMAND = "org.eclipse.jubula.autagent.monitoring.GetMonitoringDataCommand"; ////$NON-NLS-1$
    /** this command will build the (html) report*/
    public static final String BUILD_MONITORING_REPORT_COMMAND = "org.eclipse.jubula.autagent.monitoring.BuildMonitoringReportCommand"; //$NON-NLS-1$
    /** forces the profiling agent to reset the collected execution data */
    public static final String RESET_MONITORING_DATA_COMMAND = "org.eclipse.jubula.autagent.monitoring.ResetMonitoringDataCommand"; //$NON-NLS-1$
    /** AUT Agent - returns the autConfigMap */
    public static final String GET_AUT_CONFIGMAP_COMMAND = "org.eclipse.jubula.autagent.commands.GetAutConfigMapCommand"; //$NON-NLS-1$
    /** Client - handling of the returned autConfigMap */
    public static final String GET_AUT_CONFIGMAP_COMMAND_RESPONSE_COMMAND = "org.eclipse.jubula.client.core.commands.GetAutConfigMapResponseCommand"; //$NON-NLS-1$
       /** to prevent instantiation */
    private CommandConstants() {
        // do nothing
    }
}