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
package org.eclipse.jubula.app.cmd.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 * @created 10.12.2010
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.jubula.app.cmd.i18n.messages"; //$NON-NLS-1$
    
    public static String ActionBuilderCutItem;
    public static String ActionBuilderCutToolTip;
    public static String ActionBuilderEdit;
    public static String ActionBuilderExitItem;
    public static String ActionBuilderExportAll;
    public static String ActionBuilderHelpContentItem;
    public static String ActionBuilderHelpContentToolTip;
    public static String ActionBuilderMyFileEntry;
    public static String ActionBuilderMyHelpEntry;
    public static String ActionBuilderNavigateEntry;
    public static String ActionBuilderopenPerspective;
    public static String ActionBuilderPasteItem;
    public static String ActionBuilderPasteToolTip;
    public static String ActionBuilderPreferencesItem;
    public static String ActionBuilderrefreshItem;
    public static String ActionBuilderresetPerspective;
    public static String ActionBuilderRun;
    public static String ActionBuilderSaveAllItem;
    public static String ActionBuilderSaveAllToolTip;
    public static String ActionBuilderSaveAs;
    public static String ActionBuilderSaveAsPoint;
    public static String ActionBuilderSaveItem;
    public static String ActionBuilderSaveToolTip;
    public static String ActionBuildershowView;
    public static String ActionBuilderWindowEntry;
    public static String ConnectionToAUT_Agent;
    public static String ErrorMessageAUT_TOOLKIT_NOT_AVAILABLE;
    public static String ErrorWhileInitializingTestResult;
    public static String ErrorWhileStoppingAUT;
    public static String EventHandler;
    public static String ExecutionControllerAbort;
    public static String ExecutionControllerAUT;
    public static String ExecutionControllerAUTStart;
    public static String ExecutionControllerAUTStarted;
    public static String ExecutionControllerAUTStartError;
    public static String ExecutionControllerAUTStopped;
    public static String ExecutionControllerCheckError;
    public static String ExecutionControllerCheckOM;
    public static String ExecutionControllerCheckSpecTc;
    public static String ExecutionControllerCheckTD;
    public static String ExecutionControllerDatabase;
    public static String ExecutionControllerDataBaseEnd;
    public static String ExecutionControllerDatabaseStart;
    public static String ExecutionControllerDotNetInstallProblem;
    public static String ExecutionControllerInvalidDataError;
    public static String ExecutionControllerInvalidDBDataError;
    public static String ExecutionControllerInvalidDBschemeError;
    public static String ExecutionControllerInvalidJarError;
    public static String ExecutionControllerInvalidJREError;
    public static String ExecutionControllerInvalidMainError;
    public static String ExecutionControllerLoadingProject;
    public static String ExecutionControllerLogPathError;
    public static String ExecutionControllerProjectCompleteness;
    public static String ExecutionControllerProjectLoaded;
    public static String ExecutionControllerServer;
    public static String ExecutionControllerServerNotInstantiated;
    public static String ExecutionControllerTestExecution;
    public static String ExecutionControllerTestJobBegin;
    public static String ExecutionControllerTestSuiteBegin;
    public static String ExecutionControllerTestSuiteEnd;
    public static String ReceivedShutdownCommand;
    public static String RetryStep;
    public static String Step;
    public static String TestCase;
    public static String TestSuite;
    public static String UtilsSeparator;
    public static String WatchdogTimer;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    /**
     * Constructor
     */
    private Messages() {
        // hide
    }
}
