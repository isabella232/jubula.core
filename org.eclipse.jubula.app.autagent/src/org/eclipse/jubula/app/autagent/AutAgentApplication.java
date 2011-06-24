/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.app.autagent;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.tools.exception.JBVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author BREDEX GmbH
 * @created Jun 21, 2011
 */
public class AutAgentApplication implements IApplication {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AutAgentApplication.class);
    
    /**
     * <code>COMMANDLINE_OPTION_STOP</code>
     */
    private static final String COMMANDLINE_OPTION_STOP = "stop"; //$NON-NLS-1$

    /** exit code in case of invalid options */
    private static final int EXIT_INVALID_OPTIONS = -1;

    /** exit code in case of option -h(elp) */
    private static final int EXIT_HELP_OPTION = 0;

    /** exit code in case of a security exception */
    private static final int EXIT_SECURITY_VIOLATION = 1;

    /** exit code in case of an io exception */
    private static final int EXIT_IO_EXCEPTION = 2;

    /** exit code in case of a version error between Client and AutStarter */
    private static final int EXIT_CLIENT_SERVER_VERSION_ERROR = 4;

    /**
     * 
     * {@inheritDoc}
     */
    public Object start(IApplicationContext context) throws Exception {
        String[] args = (String[])context.getArguments().get(
                IApplicationContext.APPLICATION_ARGS);
        if (args == null) {
            args = new String[0];
        }

        
        // create the single instance here
        final AutStarter server = AutStarter.getInstance();

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmd = parser.parse(createOptions(), args);
            if (cmd.hasOption("h")) { //$NON-NLS-1$
                printHelp();
                return EXIT_HELP_OPTION;
            }
            server.setCmd(cmd);
        } catch (ParseException pe) {
            String message = "invalid option: "; //$NON-NLS-1$
            LOG.error(message, pe);
            printHelp();
            System.exit(EXIT_INVALID_OPTIONS);
        }

        try {
            server.start();
        } catch (SecurityException se) {
            LOG.error("security violation", se); //$NON-NLS-1$
            return EXIT_SECURITY_VIOLATION;
        } catch (IOException ioe) {
            String message = "could not open socket: "; //$NON-NLS-1$
            LOG.error(message, ioe);
            return EXIT_IO_EXCEPTION;
        } catch (NumberFormatException nfe) {
            String message = "invalid value for option port"; //$NON-NLS-1$
            LOG.error(message, nfe);
            return EXIT_INVALID_OPTIONS;
        } catch (NullPointerException npe) {
            LOG.error("no command line", npe); //$NON-NLS-1$
            printHelp();
            return EXIT_INVALID_OPTIONS;
        } catch (JBVersionException ve) {
            LOG.error(ve.getMessage(), ve);
            return EXIT_CLIENT_SERVER_VERSION_ERROR;
        }

        return IApplication.EXIT_OK;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void stop() {
        // no-op
    }

    /**
     * method to create an options object, filled with all options
     *
     * @return the options
     */
    @SuppressWarnings("nls")
    private static Options createOptions() {
        Options options = new Options();

        Option portOption = new Option("p", true, "the port to listen to");
        portOption.setArgName("port");
        options.addOption(portOption);
        
        options.addOption("l", false, "lenient mode; does not shutdown AUTs " 
                + "that try to register themselves using an already " 
                + "registered AUT ID");
        options.addOption("h", false, "prints this help text and exits");

        OptionGroup verbosityOptions = new OptionGroup();
        verbosityOptions.addOption(new Option("q", false, "quiet mode"));
        verbosityOptions.addOption(new Option("v", false, "verbose mode"));
        options.addOptionGroup(verbosityOptions);

        OptionGroup startStopOptions = new OptionGroup();
        startStopOptions.addOption(new Option("start", false,
                "startup mode"));
        
        OptionBuilder.hasOptionalArg();
        Option stopOption = OptionBuilder.create(COMMANDLINE_OPTION_STOP);
        stopOption.setDescription("stops a running AUT Agent instance "
                        + "for the given port "
                        + "on the given hostname (default \"localhost\")");
        stopOption.setArgName("hostname");
        startStopOptions.addOption(stopOption);
        options.addOptionGroup(startStopOptions);
        
        return options;
    }

    /**
     * prints a formatted help text
     */
    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("autagent", //$NON-NLS-1$
            createOptions(), true);
    }

}
