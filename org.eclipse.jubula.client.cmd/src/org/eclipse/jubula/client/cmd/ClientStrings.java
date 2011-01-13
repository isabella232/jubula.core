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

/**
 * This Class contains all the argument names, which can
 * be set either by command line or by the XML config file
 */
package org.eclipse.jubula.client.cmd;


/**
 * Single space for all strings used in CmdLineClient arguments
 * 
 * @author BREDEX GmbH
 * @created Feb 10, 2007
 * 
 */
public final class ClientStrings {
    /** String */
    public static final String CLIENT_CMD = "GUIdancerCmd"; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_JOBFILE = "Client.JobFileError"; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_EXITCODE = "Client.ExitCode";  //$NON-NLS-1$
    /** Error message */
    public static final String ERR_EXITCODE_TASK = "Client.ExitCodeTask";  //$NON-NLS-1$
    /** Error message */
    public static final String ERR_CONFIGFILE = "Client.ConfigFile"; //$NON-NLS-1$  
    /** Error message */
    public static final String ERR_NOHELPOPT = "Client.HelpOpt"; //$NON-NLS-1$   
    /** Error message */
    public static final String ERR_NORUNOPT = "Client.NoRunOpt"; //$NON-NLS-1$   
    /** Error message */
    public static final String ERR_AUTO_SCREENSHOT = "Client.AutoScreenshot"; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_TEST_EXECUTION_RELEVANT = "Client.RelevantFlag"; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_QUIETOPT = "Client.QuietOpt"; //$NON-NLS-1$  
    /** Error message */
    public static final String ERR_CONFIGOPT = "Client.ConfigOpt"; //$NON-NLS-1$   
    /** Error message */
    public static final String ERR_DBURLOPT = "Client.DburlOpt"; //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_DBSCHEMEOPT = "Client.DbschemeOpt";  //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_DBUSEROPT = "Client.DbuserOpt";  //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_DBPWOPT = "Client.DbpwOpt";   //$NON-NLS-1$
    /** Error message */
    public static final String ERR_SERVEROPT = "Client.ServerOpt"; //$NON-NLS-1$  
    /** Error message */
    public static final String ERR_STARTSERVEROPT = "Client.StartServerOpt"; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_PORTOPT = "Client.PortOpt";  //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_PROJECTOPT = "Client.ProjectOpt";  //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_PROJECTVERSIONOPT = "Client.ProjectVersionOpt";  //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_LANGUAGEOPT = "Client.LanguageOpt"; //$NON-NLS-1$  
    /** Error message */
    public static final String ERR_AUTCONFIGOPT = "Client.AutconfigOpt";  //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_AUTIDOPT = "Client.AutIdOpt";  //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_RESULTDIROPT = "Client.ResultdirOpt"; //$NON-NLS-1$  
    /** Error message */
    public static final String ERR_TESTSUITEOPT = "Client.TestSuiteOpt"; //$NON-NLS-1$ 
    /** Error message */
    public static final String ERR_TESTJOBOPT = "Client.TestJobOpt"; //$NON-NLS-1$ 
    /** object attribute */
    public static final String ERR_TIMEOUT = "Client.Timeout"; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_UNEXPECTED =
        "An unexpected error occured in command-line-client: "; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_CLIENT = "Client.Error"; //$NON-NLS-1$
    /** Error message */
    public static final String ERR_MISSING_ARGS = "Client.MissingArgs"; //$NON-NLS-1$

    /** object attribute */
    public static final String QUIET = "q"; //$NON-NLS-1$
    /** object attribute */
    public static final String NORUN = "n"; //$NON-NLS-1$
    /** object attribute */
    public static final String CONFIG = "c"; //$NON-NLS-1$
    /** object attribute */
    public static final String HELP = "h"; //$NON-NLS-1$
    /** object attribute */
    public static final String CONFIGFILE = "configfile"; //$NON-NLS-1$
    /** object attribute */
    public static final String ERR_DATA_DIROPT = "Client.DataFile"; //$NON-NLS-1$

    /** to prevent instantiation */
    private ClientStrings() {
        // do nothing
    }
}
 
