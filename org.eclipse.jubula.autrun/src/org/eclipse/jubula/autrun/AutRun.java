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
package org.eclipse.jubula.autrun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.autagent.commands.IStartAut;
import org.eclipse.jubula.communication.connection.ConnectionState;
import org.eclipse.jubula.communication.connection.RestartAutProtocol;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Dec 9, 2009
 */
public class AutRun {
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(AutRun.class);
    
    /**
     * <code>LAUNCHER_NAME</code>
     */
    private static final String LAUNCHER_NAME = "autrun"; //$NON-NLS-1$

    /** <code>TOOLKIT_RCP</code> */
    private static final String TK_RCP = "rcp"; //$NON-NLS-1$

    /** <code>TK_SWT</code> */
    private static final String TK_SWT = "swt"; //$NON-NLS-1$

    /** <code>TK_SWING</code> */
    private static final String TK_SWING = "swing"; //$NON-NLS-1$

    /** <code>DEFAULT_NAME_TECHNICAL_COMPONENTS</code> */
    private static final boolean DEFAULT_NAME_TECHNICAL_COMPONENTS = true;

    /** <code>DEFAULT_AUT_AGENT_PORT</code> */
    private static final int DEFAULT_AUT_AGENT_PORT = 60000;

    /** <code>DEFAULT_AUT_AGENT_HOST</code> */
    private static final String DEFAULT_AUT_AGENT_HOST = "localhost"; //$NON-NLS-1$

    // - Command line options - Start //
    /** port number for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_PORT = "p"; //$NON-NLS-1$

    /** port number for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_PORT_LONG = "autagentport"; //$NON-NLS-1$
    
    /** hostname for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_HOST = "a"; //$NON-NLS-1$
    
    /** hostname for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_HOST_LONG = "autagenthost"; //$NON-NLS-1$
    
    /** help option */
    private static final String OPT_HELP = "h"; //$NON-NLS-1$
    
    /** hostname for the Aut Agent with which to register */
    private static final String OPT_HELP_LONG = "help"; //$NON-NLS-1$
    
    /** name of the AUT to register */
    private static final String OPT_AUT_ID = "i"; //$NON-NLS-1$
    
    /** name of the AUT to register */
    private static final String OPT_AUT_ID_LONG = "autid"; //$NON-NLS-1$
    
    /** flag for name generation for certain technical components */
    private static final String OPT_NAME_TECHNICAL_COMPONENTS = "g"; //$NON-NLS-1$

    /** flag for name generation for certain technical components */
    private static final String OPT_NAME_TECHNICAL_COMPONENTS_LONG = "generatenames"; //$NON-NLS-1$
    
    /** keyboard layout */
    private static final String OPT_KEYBOARD_LAYOUT = "k"; //$NON-NLS-1$

    /** keyboard layout */
    private static final String OPT_KEYBOARD_LAYOUT_LONG = "kblayout"; //$NON-NLS-1$

    /** AUT working directory */
    private static final String OPT_WORKING_DIR = "w"; //$NON-NLS-1$

    /** AUT working directory */
    private static final String OPT_WORKING_DIR_LONG = "workingdir"; //$NON-NLS-1$
    
    /** executable file used to start the AUT */
    private static final String OPT_EXECUTABLE = "e"; //$NON-NLS-1$
    
    /** executable file used to start the AUT */
    private static final String OPT_EXECUTABLE_LONG = "exec"; //$NON-NLS-1$
    // - Command line options - End //

    /** settings used to start the AUT */
    private Map<String, Object> m_autConfiguration;
    
    /** the object responsible for actually starting the AUT */
    private IStartAut m_startAut;

    /** the address for the AUT Agent */
    private InetSocketAddress m_agentAddr;
    
    /**
     * Constructor
     * 
     * @param autToolkit Toolkit for the AUT managed by this instance.
     * @param autIdentifier Identifier for the AUT managed by this instance.
     * @param agentAddr Address of the Aut Agent with which the AUT should be
     *                  registered.
     * @param autConfiguration Properties required for starting the AUT.
     * 
     * @throws ClassNotFoundException If no class can be found for starting an
     *                                AUT for the given toolkit.
     * @throws InstantiationException 
     * @throws IllegalAccessException
     */
    public AutRun(String autToolkit, AutIdentifier autIdentifier, 
            InetSocketAddress agentAddr, Map<String, Object> autConfiguration) 
        throws ClassNotFoundException, InstantiationException, 
               IllegalAccessException {
        String className = "org.eclipse.jubula.autagent.commands.Start" //$NON-NLS-1$
            + autToolkit + "AutServerCommand"; //$NON-NLS-1$
        Class< ? > autServerClass = Class.forName(className);
        m_agentAddr = agentAddr;
        m_autConfiguration = new HashMap<String, Object>(autConfiguration);
        m_autConfiguration.put(AutConfigConstants.AUT_AGENT_HOST, 
                agentAddr.getHostName());
        m_autConfiguration.put(AutConfigConstants.AUT_AGENT_PORT, 
                String.valueOf(agentAddr.getPort()));
        m_autConfiguration.put(AutConfigConstants.AUT_NAME, 
                autIdentifier.getExecutableName());
        m_startAut = (IStartAut)autServerClass.newInstance();
    }
    
    /**
     * Main method.
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main(String[] args) throws ClassNotFoundException, 
            InstantiationException, IllegalAccessException, 
            IOException {
        Options options = createCmdLineOptions();
        Parser parser = new BasicParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args, false);
        } catch (ParseException pe) {
            printHelp(pe);
        }
        
        if (cmdLine != null && !cmdLine.hasOption(OPT_HELP)) {
            String toolkit = StringConstants.EMPTY;
            if (cmdLine.hasOption(TK_SWING)) {
                toolkit = "Swing"; //$NON-NLS-1$
            } else if (cmdLine.hasOption(TK_SWT)) {
                toolkit = "Swt"; //$NON-NLS-1$
            } else if (cmdLine.hasOption(TK_RCP)) {
                toolkit = "Rcp"; //$NON-NLS-1$
            }
            
            int autAgentPort = DEFAULT_AUT_AGENT_PORT;
            if (cmdLine.hasOption(OPT_AUT_AGENT_PORT)) {
                try {
                    autAgentPort = Integer.parseInt(cmdLine
                            .getOptionValue(OPT_AUT_AGENT_PORT));
                } catch (NumberFormatException nfe) {
                    // use default
                }
            }
            String autAgentHost = DEFAULT_AUT_AGENT_HOST;
            if (cmdLine.hasOption(OPT_AUT_AGENT_HOST)) {
                autAgentHost = cmdLine.getOptionValue(OPT_AUT_AGENT_HOST);
            }
            
            InetSocketAddress agentAddr = 
                new InetSocketAddress(autAgentHost, autAgentPort);
            AutIdentifier autId = new AutIdentifier(cmdLine
                    .getOptionValue(OPT_AUT_ID));
            
            Map<String, Object> autConfiguration = createAutConfig(cmdLine);
            
            AutRun runner = new AutRun(toolkit, autId, agentAddr,
                    autConfiguration);
            try {
                runner.run();
            } catch (ConnectException ce) {
                LOG.info("Could not connect to AUT Agent.", ce); //$NON-NLS-1$
                System.err.println(I18n.getString("InfoDetail.connGuiDancerServerFailed")); //$NON-NLS-1$
            }
        } else {
            printHelp(null);
        }
    }

    /**
     * prints help options
     * @param pe a parse Execption - may also be null
     */
    private static void printHelp(ParseException pe) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(new OptionComparator());
        if (pe != null) {
            formatter.printHelp(
                    LAUNCHER_NAME,
                    StringConstants.EMPTY,
                    createCmdLineOptions(), 
                    "\n" + pe.getLocalizedMessage(),  //$NON-NLS-1$
                    true);
        } else {
            formatter.printHelp(LAUNCHER_NAME,
                    createCmdLineOptions(), true);
        }
    }

    /**
     * Creates and returns settings for starting an AUT based on the given
     * command line.
     *  
     * @param cmdLine Provides the settings for the AUT configuration.
     * @return new settings for starting an AUT.
     */
    private static Map<String, Object> createAutConfig(CommandLine cmdLine) {
        Map<String, Object> autConfig = new HashMap<String, Object>();
        if (cmdLine.hasOption(OPT_WORKING_DIR)) {
            autConfig.put(AutConfigConstants.WORKING_DIR, cmdLine
                    .getOptionValue(OPT_WORKING_DIR));
        } else {
            autConfig.put(AutConfigConstants.WORKING_DIR, System
                    .getProperty("user.dir")); //$NON-NLS-1$
        }
        
        if (cmdLine.hasOption(OPT_NAME_TECHNICAL_COMPONENTS)) {
            autConfig.put(AutConfigConstants.NAME_TECHNICAL_COMPONENTS, Boolean
                    .valueOf(cmdLine
                            .getOptionValue(OPT_NAME_TECHNICAL_COMPONENTS)));
        } else {
            autConfig.put(AutConfigConstants.NAME_TECHNICAL_COMPONENTS,
                    DEFAULT_NAME_TECHNICAL_COMPONENTS);
        }
        autConfig.put(AutConfigConstants.EXECUTABLE, cmdLine
                .getOptionValue(OPT_EXECUTABLE));
        
        if (cmdLine.hasOption(OPT_KEYBOARD_LAYOUT)) {
            autConfig.put(AutConfigConstants.KEYBOARD_LAYOUT, 
                    cmdLine.getOptionValue(OPT_KEYBOARD_LAYOUT));
        }
        
        String[] autArguments = cmdLine.getOptionValues(OPT_EXECUTABLE);
        if (autArguments.length > 1) {
            autConfig.put(AutConfigConstants.AUT_RUN_AUT_ARGUMENTS, 
                    ArrayUtils.subarray(autArguments, 1, autArguments.length));
        }
        
        return autConfig;
    }

    /**
     * Starts the AUT managed by the receiver.
     * 
     * @throws ConnectException If unable to connect to the AUT Agent (if, 
     *         for example, there is no AUT Agent running on the given 
     *         hostname / port)
     * @throws IOException if an I/O error occurs during AUT startup.
     */
    public void run() throws IOException, ConnectException {
        // Establish connection to AUT Agent
        final Socket agentSocket = 
            new Socket(m_agentAddr.getAddress(), m_agentAddr.getPort());

        final PrintWriter writer = new PrintWriter(
                agentSocket.getOutputStream(), true);
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(agentSocket.getInputStream()));
        
        String clientTypeRequest = reader.readLine();
        writer.println(ConnectionState.CLIENT_TYPE_AUTRUN);
        
        writer.println(
                m_autConfiguration.get(AutConfigConstants.AUT_NAME));
        
        Thread agentConnectionThread = new Thread("AUT Agent Connection") { //$NON-NLS-1$
            public void run() {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        if (line.equals(
                                RestartAutProtocol.REQ_PREPARE_FOR_RESTART)) {
                            
                            // make sure that we have a system thread running so
                            // the JVM won't shut down during AUT restart
                            Thread restartThread = new Thread() {
                                public void run() {
                                    writer.println("Response.OK"); //$NON-NLS-1$
                                    
                                    try {
                                        String restartReq = reader.readLine();
                                        if (RestartAutProtocol.REQ_RESTART
                                            .equals(restartReq)) {
                                            
                                            AutRun.this.run();
                                        }
                                    } catch (IOException e) {
                                        LOG.error("Error occured while restarting AUT.", e); //$NON-NLS-1$
                                    } finally {
                                        try {
                                            agentSocket.close();
                                        } catch (IOException e) {
                                            // Error while closing socket. Ignore.
                                        }
                                    }
                                };
                            };
                            restartThread.setDaemon(false);
                            restartThread.start();
                        }
                    }
                } catch (IOException e) {
                    LOG.error("Error occured while restarting AUT.", e); //$NON-NLS-1$
                }
            };
        };
        
        agentConnectionThread.setDaemon(true);
        agentConnectionThread.start();
        
        m_startAut.startAut(m_autConfiguration);
    }

    /**
     * @return the command line options available when invoking the main method. 
     */
    @SuppressWarnings("nls")
    private static Options createCmdLineOptions() {
        Options options = new Options();
        Option autAgentHostOption = 
            new Option(OPT_AUT_AGENT_HOST, true,
                "AUT Agent Host"
                    + " (default \"" + DEFAULT_AUT_AGENT_HOST + "\")");
        autAgentHostOption.setLongOpt(OPT_AUT_AGENT_HOST_LONG);
        autAgentHostOption.setArgName("hostname");
        options.addOption(autAgentHostOption);

        Option autAgentPortOption = 
            new Option(OPT_AUT_AGENT_PORT, true,
                "AUT Agent Port between 1024 and 65536"
                    + " (default \"" + DEFAULT_AUT_AGENT_PORT + "\")");
        autAgentPortOption.setLongOpt(OPT_AUT_AGENT_PORT_LONG);
        autAgentPortOption.setArgName("port");
        options.addOption(autAgentPortOption);

        OptionGroup autToolkitOptionGroup = new OptionGroup();
        autToolkitOptionGroup.addOption(
                new Option(TK_SWING, "AUT uses Swing toolkit"));
        autToolkitOptionGroup.addOption(
                new Option(TK_SWT,   "AUT uses SWT toolkit"));
        autToolkitOptionGroup.addOption(
                new Option(TK_RCP,   "AUT uses RCP toolkit"));
        autToolkitOptionGroup.setRequired(true);
        options.addOptionGroup(autToolkitOptionGroup);
        
        Option autIdOption =
            new Option(OPT_AUT_ID, true, "AUT ID");
        autIdOption.setLongOpt(OPT_AUT_ID_LONG);
        autIdOption.setArgName("id");
        autIdOption.setRequired(true);
        options.addOption(autIdOption);
        
        Option nameTechnicalComponentsOption =
            new Option(OPT_NAME_TECHNICAL_COMPONENTS, 
                true, "Generate Names for Technical Components (true / false)");
        nameTechnicalComponentsOption
            .setLongOpt(OPT_NAME_TECHNICAL_COMPONENTS_LONG);
        nameTechnicalComponentsOption.setArgName("true / false");
        options.addOption(nameTechnicalComponentsOption);
        
        Option keyboardLayoutOption =
            new Option(OPT_KEYBOARD_LAYOUT, 
                true, "Keyboard Layout");
        keyboardLayoutOption
            .setLongOpt(OPT_KEYBOARD_LAYOUT_LONG);
        keyboardLayoutOption.setArgName("locale");
        options.addOption(keyboardLayoutOption);
        
        Option workingDirOption = new Option(OPT_WORKING_DIR, 
                true, "AUT Working Directory");
        workingDirOption.setLongOpt(OPT_WORKING_DIR_LONG);
        workingDirOption.setArgName("directory");
        options.addOption(workingDirOption);
        
        Option helpOption = new Option(OPT_HELP, 
                false, "Displays this help");
        helpOption.setLongOpt(OPT_HELP_LONG);
        options.addOption(helpOption);
        
        OptionBuilder.hasArgs();
        Option autExecutableFileOption = OptionBuilder.create(OPT_EXECUTABLE);
        autExecutableFileOption.setDescription("AUT Executable File");
        autExecutableFileOption.setLongOpt(OPT_EXECUTABLE_LONG);
        autExecutableFileOption.setRequired(true);
        autExecutableFileOption.setArgName("command");
        options.addOption(autExecutableFileOption);
        
        return options;
    }
    
    /**
     * This class implements the <code>Comparator</code> interface for comparing
     * Options.
     */
    private static class OptionComparator implements Comparator {
        /** {@inheritDoc} */
        public int compare(Object o1, Object o2) {
            Option opt1 = (Option)o1;
            Option opt2 = (Option)o2;
            // always list -exec as last option
            if (opt1.getOpt().equals(OPT_EXECUTABLE)) {
                return 1;
            }
            if (opt2.getOpt().equals(OPT_EXECUTABLE)) {
                return -1;
            }
            return 0;
        }
    }
}
