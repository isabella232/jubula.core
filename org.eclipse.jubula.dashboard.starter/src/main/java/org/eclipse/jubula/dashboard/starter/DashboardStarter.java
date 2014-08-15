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
package org.eclipse.jubula.dashboard.starter;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * Starts the Dashboard server as well as a browser to view the started server.
 * 
 * @author BREDEX GmbH
 */
public class DashboardStarter {
    /** the logger */
    private static final Logger LOG = 
            Logger.getLogger(DashboardStarter.class.getName());
    
    /** 
     * Property name for the port on which the Dashboard server will start. 
     * The value for this property should be loaded from the 
     * dashboardserver.properties file.
     */
    private static final String PROP_PORT = "org.eclipse.jubula.dashboard.port"; //$NON-NLS-1$
    
    /** command to start the Dashboard server */
    private static final String DASHBOARD_SERVER_CMD = "dashboardserver"; //$NON-NLS-1$
    
    /** Dashboard server command path */
    private static final File DASHBOARD_SERVER_CMD_FILE = 
            new File(DASHBOARD_SERVER_CMD).getAbsoluteFile();
    
    /** working directory for the Dashboard server on Mac OS */
    private static final File DASHBOARD_SERVER_BASE_DIR_MAC = new File(
            DASHBOARD_SERVER_CMD + ".app/Contents/MacOS/").getAbsoluteFile(); //$NON-NLS-1$

    /** the name of the properties file to load */
    private static final String PROPERTIES_FILE_NAME = "dashboardserver.properties"; //$NON-NLS-1$
    
    /** the properties file to load */
    private static final File PROPERTIES_FILE = isMac() 
            ? new File(DASHBOARD_SERVER_BASE_DIR_MAC, PROPERTIES_FILE_NAME)
            : new File(PROPERTIES_FILE_NAME);

    /** OSGi command line argument to provide an OSGi console */
    private static final String OSGI_ARG_CONSOLE = "-console"; //$NON-NLS-1$
    
    /** 
     * OSGi console command to shutdown the OSGi framework and exit 
     * the application 
     */
    private static final String OSGI_COMMAND_EXIT = "close"; //$NON-NLS-1$
    
    /**
     * Reads an input stream, ignoring the data that is read.
     * 
     * @author BREDEX GmbH
     */
    private static final class StreamSink implements Runnable {

        /** the stream to read */
        private InputStream m_stream;
        
        /**
         * Constructor
         * 
         * @param stream The stream to read.
         */
        public StreamSink(InputStream stream) {
            m_stream = stream;
        }
        
        @Override
        public void run() {
            InputStreamReader isr = new InputStreamReader(m_stream);
            BufferedReader br = new BufferedReader(isr);
            String line;
            try {
                line = br.readLine();
                while (line != null) {
                    line = br.readLine();
                }
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error occurred while reading from stream.", e); //$NON-NLS-1$
            }
        }
        
    }
    
    /**
     * Private constructor for utility class.
     */
    private DashboardStarter() {
        // nothing to initialize
    }
    
    /**
     * Starts the Dashboard server as well as a browser to view the 
     * started server.
     * 
     * @param args The program arguments.
     * @throws URISyntaxException if the programmatically generated URI is 
     *                            invalid.
     * @throws IOException if an error occurs while finding or launching 
     *                     the default browser.
     * @throws AWTException 
     */
    public static void main(String[] args) 
        throws IOException, URISyntaxException, AWTException {

        loadProperties(PROPERTIES_FILE);

        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append("http://localhost:"); //$NON-NLS-1$
        uriBuilder.append(System.getProperty(PROP_PORT));
        uriBuilder.append("/testresults/dashboard"); //$NON-NLS-1$
        
        URI dashboardUri = new URI(uriBuilder.toString());

        addSystemTrayIcon(dashboardUri);
        
        ProcessBuilder processBuilder = isMac() 
            ? new ProcessBuilder(
                    new File(DASHBOARD_SERVER_BASE_DIR_MAC, 
                            DASHBOARD_SERVER_CMD).getAbsolutePath(), 
                        OSGI_ARG_CONSOLE) 
            : new ProcessBuilder(DASHBOARD_SERVER_CMD_FILE.getAbsolutePath(),
                    OSGI_ARG_CONSOLE);
        if (isMac()) {
            processBuilder.directory(DASHBOARD_SERVER_BASE_DIR_MAC);
        }

        processBuilder.redirectErrorStream(true);
        final Process dashboardServerProcess = processBuilder.start();

        new Thread(new StreamSink(dashboardServerProcess.getInputStream()))
            .start();

        registerProcessListener(dashboardServerProcess);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (dashboardServerProcess != null) {
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(
                                    dashboardServerProcess.getOutputStream()));
                    try {
                        writer.write(OSGI_COMMAND_EXIT);
                        writer.newLine();
                        writer.flush();
                    } catch (IOException ioe) {
                        LOG.log(Level.WARNING, 
                                "Unable to send shutdown command to Dashboard.",  //$NON-NLS-1$
                                ioe);
                    }
                }
            } 
        });

        
        Desktop desktop = Desktop.getDesktop();
        
        waitForServer(dashboardUri.toURL());
        
        desktop.browse(dashboardUri);
    }

    /**
     *
     * @param propFile The file to load.
     * @throws IOException if an error occurs while trying to read the file.
     */
    private static void loadProperties(File propFile) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(propFile);
            System.getProperties().load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Adds the Dashboard Starter icon to the system tray and registers the
     * corresponding context menu.
     * 
     * @param dashboardUri The URI for the Dashboard server.
     * 
     * @throws AWTException if the desktop system tray is missing.
     */
    private static void addSystemTrayIcon(final URI dashboardUri) 
        throws AWTException {
        
        if (SystemTray.isSupported()) {
            ActionListener exitListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };

            ActionListener browseListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().browse(dashboardUri);
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "See the log for further details.",  //$NON-NLS-1$
                                "Error Occurred", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
                        LOG.log(Level.SEVERE, "An error has occurred while browsing to Dashboard URL.",  //$NON-NLS-1$
                                ioe);
                    }
                }
            };
            
            Image trayIconImage = Toolkit.getDefaultToolkit().getImage(
                    DashboardStarter.class.getClassLoader().getResource(
                            "dashboard_32x32.png")); //$NON-NLS-1$

            PopupMenu popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("Exit"); //$NON-NLS-1$
            defaultItem.addActionListener(exitListener);
            
            MenuItem browseItem = new MenuItem("Open Dashboard in browser"); //$NON-NLS-1$
            browseItem.addActionListener(browseListener);
            
            popup.add(browseItem);
            popup.add(defaultItem);

            TrayIcon trayIcon = 
                    new TrayIcon(trayIconImage, "ITE Dashboard", popup); //$NON-NLS-1$
            trayIcon.setImageAutoSize(true);

            trayIcon.setToolTip(dashboardUri.toString());
            
            SystemTray.getSystemTray().add(trayIcon);
        }
    }

    /**
     * Starts a thread that waits for the given process to end. When the process
     * ends, the JVM will be terminated as well.
     * 
     * @param process The process.
     */
    private static void registerProcessListener(
            final Process process) {

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int exitCode = process.waitFor();
                        System.exit(exitCode);
                    } catch (InterruptedException e) {
                        LOG.log(Level.WARNING, "Interrupted while waiting for Dashboard to close.", e); //$NON-NLS-1$
                    }
                }
            }
        } .start();
    }

    /**
     * Polls the given URL, returning when a connection can be successfully
     * established.
     * 
     * @param serverUrl The URL to poll.
     */
    private static void waitForServer(URL serverUrl) {

        boolean connectionWasSuccessful = false;
        while (!connectionWasSuccessful) {
            InputStream stream = null;
            try {
                stream = serverUrl.openStream();
                connectionWasSuccessful = true;
            } catch (IOException ioe) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // nothing to handle
                    // looping will continue
                }
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Error while closing stream", e); //$NON-NLS-1$
                    }
                }
            }
        }

    }

    /**
     * 
     * @return <code>true</code> if the current OS is a Mac. Otherwise, 
     *         <code>false</code>.
     */
    private static boolean isMac() {
        String os = System.getProperty("os.name"); //$NON-NLS-1$
        if (os != null) {
            return os.startsWith("Mac"); //$NON-NLS-1$
        }
        
        return false;
    }
}
