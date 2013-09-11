/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.app.dashboard;

import java.io.FileInputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.eclipse.equinox.http.jetty.JettyConstants;
import org.eclipse.jubula.app.dashboard.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 */
public class DashboardHttpServerApp implements IApplication {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(DashboardHttpServerApp.class);
    
    /** ID for HTTP server instance */
    private static final String HTTP_SERVER_ID = "dashboard"; //$NON-NLS-1$
    
    /**
     * command line argument: path to configuration file
     */
    private static final String OPTION_CONFIG_PATH = "c"; //$NON-NLS-1$

    /** default path to configuration file */
    private static final String DEFAULT_CONFIG_PATH = 
            "dashboardserver.properties"; //$NON-NLS-1$

    /**
     * command line argument: help / usage
     */
    private static final String OPTION_HELP = "h"; //$NON-NLS-1$

    /**
     * configuration file property: port
     */
    private static final String PROP_PORT = "org.eclipse.jubula.dashboard.port"; //$NON-NLS-1$

    /** default port */
    private static final int DEFAULT_PORT = 60002;
    
    /**
     * FQN of Dashboard Jetty customizer
     */
    private static final String CUSTOMIZER_CLASS_NAME = 
            "org.eclipse.jubula.dashboard.jettycustomizer.DashboardSessionCustomizer"; //$NON-NLS-1$
    
    /**
     *  
     * {@inheritDoc} 
     */
    public Object start(IApplicationContext context) throws Exception {
        
        Options options = createOptions();

        String [] applicationArgs = (String[])context.getArguments().get(
                IApplicationContext.APPLICATION_ARGS);
        CommandLine commandLine = 
                new PosixParser().parse(options, applicationArgs);
        
        if (commandLine.hasOption(OPTION_HELP)) {
            new HelpFormatter().printHelp("dashboardserver", options, true); //$NON-NLS-1$
            System.exit(0);
        } else {
            try {
                String configPath = DEFAULT_CONFIG_PATH;
                if (commandLine.hasOption(OPTION_CONFIG_PATH)) {
                    configPath = 
                            commandLine.getOptionValue(OPTION_CONFIG_PATH);
                }

                FileInputStream configFileInputStream = null;
                Properties configuration = new Properties();
                try {
                    configFileInputStream = new FileInputStream(configPath);
                    configuration.load(configFileInputStream);
                    System.getProperties().putAll(configuration);
                } finally {
                    if (configFileInputStream != null) {
                        configFileInputStream.close();
                    }
                }

                JettyConfigurator.startServer(
                        HTTP_SERVER_ID, createJettySettings(configuration));
            } catch (Exception e) {
                LOG.error("Fatal error occurred. Application will be shutdown.", e); //$NON-NLS-1$
                System.exit(1);
            }
        }

        return IApplication.EXIT_OK;
    }

    /**
     * 
     * @return the command line options for this application.
     */
    private static Options createOptions() {
        Options options = new Options();

        Option configPathOption = 
            new Option(OPTION_CONFIG_PATH, true, "the path to the configuration file"); //$NON-NLS-1$
        configPathOption.setArgName("path"); //$NON-NLS-1$
        options.addOption(configPathOption);

        options.addOption(OPTION_HELP, false, "print this message"); //$NON-NLS-1$
        
        return options;
    }

    /**
     *  
     * {@inheritDoc} 
     */
    public void stop() {
        // currently empty
    }

    /**
     * Creates and returns settings suitable for use in starting a Jetty 
     * instance based on the given command line arguments.
     * 
     * @param configuration The configuration properties.
     * @return the created settings.
     */
    private Dictionary<String, Object> createJettySettings(
            Properties configuration) {
        
        Hashtable<String, Object> settings = new Hashtable<String, Object>();

        String portArgValue = 
                configuration.getProperty(PROP_PORT);
        if (portArgValue == null) {
            LOG.info(Messages.MissingArgument_PortNumber);
            portArgValue = String.valueOf(DEFAULT_PORT);
        }

        try {
            settings.put(JettyConstants.HTTP_PORT, 
                    Integer.parseInt(portArgValue));
        } catch (NumberFormatException nfe) {
            LOG.error(Messages.InvalidArgument_PortNumber, nfe);
        }

        settings.put(JettyConstants.CUSTOMIZER_CLASS, CUSTOMIZER_CLASS_NAME);
        
        return settings;
    }
}
