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
package org.eclipse.jubula.autagent;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.jubula.autagent.agent.AutAgent;
import org.eclipse.jubula.autagent.remote.dialogs.ChooseCheckModeDialogBP;
import org.eclipse.jubula.autagent.remote.dialogs.ObservationConsoleBP;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.IConnectionInitializer;
import org.eclipse.jubula.communication.connection.ConnectionState;
import org.eclipse.jubula.communication.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.message.AutRegisteredMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.StartAUTServerStateMessage;
import org.eclipse.jubula.tools.constants.AUTServerExitConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.exception.JBVersionException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.jubula.tools.utils.DevNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The AutStarter for starting, watching and stopping the AUTServer.
 *
 * ExitCodes:
 * <ul>
 * <li>-1: invalid command line options</li>
 * <li>0: option -h(elp)</li>
 * <li>1: security violation when trying accepting connections</li>
 * <li>2: io exception when trying accepting connections</li>
 * <li>3: configuration error</li>
 * </ul>
 *
 * @author BREDEX GmbH
 * @created 26.07.2004
 *
 */
public class AutStarter {
    /**
     * Possible values for determining how much output should be produced.
     */
    public static enum Verbosity {
        /** error messages will be printed to the console */
        QUIET, 
        
        /** normal output */
        NORMAL, 
        
        /** messages will be shown in dialogs */
        VERBOSE
    }
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AutStarter.class);

    /** the instance */
    private static AutStarter instance = null;

    /** the communicator to use */
    private Communicator m_communicator;

    /** the communicator to use to communicate with AUTServer*/
    private Communicator m_autCommunicator;

    /**
     * the timeout for killing the autServerVM when the connection to the
     * JubulaClient was closed, defaults to 10 seconds
     */
    private int m_stopAUTServerTimeout = 10000;

    /** the AUT Agent that is used for AUT registration and deregistration */
    private AutAgent m_agent;
    
    /** sends messages using the Agent's communicator(s) */
    private CommunicationHelper m_messenger;
    
    /** controls the amount and form of output produced */
    private Verbosity m_verbosity;
    
    /**
     * private constructor
     */
    private AutStarter() {
        super();

        AutAgent agent = new AutAgent();
        m_messenger = new CommunicationHelper();
        // AUT Registration listener. Sends registration information to 
        // connected client.
        agent.addPropertyChangeListener(
                AutAgent.PROP_NAME_AUTS, new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        Communicator clientComm = 
                            AutStarter.getInstance().getCommunicator();
                        if (clientComm == null 
                                || clientComm.getConnection() == null) {
                            // No connection. Do nothing.
                            return;
                        }
                        try {
                            Object newValue = evt.getNewValue();
                            if (newValue instanceof AutIdentifier) {
                                clientComm.send(new AutRegisteredMessage(
                                        (AutIdentifier)newValue, true));
                            }

                            Object oldValue = evt.getOldValue();
                            if (oldValue instanceof AutIdentifier) {
                                clientComm.send(new AutRegisteredMessage(
                                        (AutIdentifier)oldValue, false));
                            }
                        } catch (CommunicationException ce) {
                            log.error("Error occurred while sending AUT Registration Message.", ce); //$NON-NLS-1$
                        }
                    }

                });

        m_agent = agent;

    }

    /**
     * starts watching the given process <br>
     * @param process the process representing an AutServer, must not be null
     * @param isAgentSet true if executable file and agent are set.
     * @throws IllegalArgumentException if the given process is null
     * @return false when no more server can watched (it's only one),
     *         true otherwise
     */
    public boolean watchAutServer(Process process, boolean isAgentSet)
        throws IllegalArgumentException {
        // check parameter
        if (process == null) {
            throw new IllegalArgumentException(
                    "process must not be null"); //$NON-NLS-1$
        }

        // start thread waiting for termination
        new AUTServerWatcher(process, isAgentSet, m_messenger).start();
        return true;
    }

    /**
     * Method to get the single instance of this class.
     *
     * @return the instance of this Singleton
     */
    public static AutStarter getInstance() {
        if (instance == null) {
            instance = new AutStarter();
        }
        return instance;
    }

    /**
     * @return Returns the communicator.
     */
    public synchronized Communicator getCommunicator() {
        return m_communicator;
    }

    /**
     * @param communicator
     *            The communicator to set.
     */
    public synchronized void setCommunicator(Communicator communicator) {
        m_communicator = communicator;
    }

    /**
     * @return Returns the communicator.
     */
    public synchronized Communicator getAutCommunicator() {
        return m_autCommunicator;
    }

    /**
     * @param communicator
     *            The communicator to set.
     */
    public synchronized void setAutCommunicator(Communicator communicator) {
        m_autCommunicator = communicator;
    }

    /**
     * @return Returns the stopAUTServerTimeout.
     */
    public int getStopAUTServerTimeout() {
        return m_stopAUTServerTimeout;
    }

    /**
     * sets the timeout used at killing the autServerProcess.
     *
     * @param stopAUTServerTimeout
     *            The stopAUTServerTimeout to set, for negative values zero is
     *            used.
     */
    public void setStopAUTServerTimeout(int stopAUTServerTimeout) {
        if (stopAUTServerTimeout < 0) {
            m_stopAUTServerTimeout = 0;
        } else {
            m_stopAUTServerTimeout = stopAUTServerTimeout;
        }
    }

    /**
     * Start accepting connections. Depending on the value of 
     * <code>isBlocking</code>, this method will block execution until the 
     * AUT Agent is shutdown. 
     * 
     * @param port The port on which the AUT Agent should listen for incoming
     *             connections.
     * @param killDuplicateAuts Whether AUTs attempting to register with an
     *                          already registered AUT ID should be terminated.
     * @param verbosity Controls the amount and form of output produced.
     * @param isBlocking Whether the method should block execution until the 
     *                   AUT Agent is shutdown.
     * @throws IOException 
     * @throws UnknownHostException 
     * @throws JBVersionException 
     */
    public void start(int port, boolean killDuplicateAuts, 
            Verbosity verbosity, boolean isBlocking) 
        throws UnknownHostException, IOException, JBVersionException {

        m_verbosity = verbosity;
        String infoMessage = I18n.getString("AUTAgent.StartErrorText"); //$NON-NLS-1$
        Thread clientSocketThread = null;

        try {
            getAgent().setKillDuplicateAuts(killDuplicateAuts);
            infoMessage = I18n.getString("AUTAgent.StartCommErrorText",  //$NON-NLS-1$
                    new Object[] {StringConstants.EMPTY + port});
            clientSocketThread = initClientConnectionSocket(port);
            initAutConnectionSocket();
            if (m_verbosity.compareTo(Verbosity.VERBOSE) >= 0) {
                infoMessage = I18n.getString("AUTAgent.StartSuccessText") + //$NON-NLS-1$
                    getCommunicator().getLocalPort() + "."; //$NON-NLS-1$
            } else {
                infoMessage = StringConstants.EMPTY;
            }
        } finally {
            // print information box to user
            if (infoMessage.length() > 0) {
                showUserInfo(infoMessage);
            }
            
            if (isBlocking && clientSocketThread != null) {
                try {
                    clientSocketThread.join();
                } catch (InterruptedException e) {
                    log.warn("Primary Thread was interrupted unexpectedly while waiting for client socket Thread to finish. Resuming execution of Primary Thread.", e); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * initializes the Socket for the client to connect.
     * 
     * @param port
     *            int
     *            
     * @return the Thread responsible for accepting connections.
     *         
     * @throws IOException
     *             error
     * @throws JBVersionException
     *             in case of version error between Client and AutStarter
     */
    private Thread initClientConnectionSocket(int port) 
        throws IOException, JBVersionException {

        Map<String, IConnectionInitializer> clientTypeToInitializer =
            new HashMap<String, IConnectionInitializer>();
        
        
        clientTypeToInitializer.putAll(m_agent.getConnectionInitializers());
        clientTypeToInitializer.put(
                ConnectionState.CLIENT_TYPE_COMMAND_SHUTDOWN, 
                new IConnectionInitializer() {
                    public void initConnection(Socket socket, 
                            BufferedReader reader) {
                        System.out.println("Shutdown requested. Shutting down..."); //$NON-NLS-1$
                        Thread.currentThread().interrupt();
                    }
                });
        // create a communicator
        setCommunicator(
                new Communicator(port, this.getClass().getClassLoader(), 
                clientTypeToInitializer));
        
        getCommunicator().addCommunicationErrorListener(
                new CommunicationListener());
        logRunning();
        // start listening
        logStartListening();
        return getCommunicator().run();
    }

    /**
     * initializes the Socket for the AUTServer to connect
     * 
     * @throws IOException
     *             error
     * @throws JBVersionException
     *             in case of a version error between Client and AutStarter
     */
    private void initAutConnectionSocket() throws IOException,
        JBVersionException {

        // create a communicator on any free port
        setAutCommunicator(new Communicator(0, this.getClass()
                .getClassLoader()));
        getAutCommunicator()
                .addCommunicationErrorListener(new CommunicationListener());
        getAutCommunicator().run();
    }

    /**
     * @param infoMessage message to show
     */
    private void showUserInfo(final String infoMessage) {
        if (m_verbosity.compareTo(Verbosity.QUIET) <= 0) {
            System.out.println(infoMessage);
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.
                        getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) { // NOPMD by al on 3/19/07 1:55 PM
              // as this is just for the information box, ignore exceptions.
            } catch (InstantiationException e) { // NOPMD by al on 3/19/07 1:55 PM
              // as this is just for the information box, ignore exceptions.
            } catch (IllegalAccessException e) { // NOPMD by al on 3/19/07 1:55 PM
              // as this is just for the information box, ignore exceptions.
            } catch (UnsupportedLookAndFeelException e) { // NOPMD by al on 3/19/07 1:55 PM
              // as this is just for the information box, ignore exceptions.
            }
            Thread t = new Thread() {
                public void run() {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            JOptionPane.getRootFrame().dispose();
                        }
                    });
                }
            };
            t.start();
            JOptionPane.showMessageDialog(null, infoMessage
                + I18n.getString("AUTAgent.dialogClose"), //$NON-NLS-1$
                I18n.getString("AUTAgent.failedStartDialogTitle"), //$NON-NLS-1$
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * private method which prints a start message (java version)
     */
    private void logRunning() {
        if (log.isInfoEnabled()) {
            String message = "running VM with JRE version: " //$NON-NLS-1$
                    + System.getProperty("java.version"); //$NON-NLS-1$
            log.info(message);
        }
    }

    /**
     * private method which prints a 'listening' message (the port number
     * listening to)
     */
    private void logStartListening() {

        if (log.isInfoEnabled()) {
            String message = "listening to port " //$NON-NLS-1$
                    + getCommunicator().getLocalPort();
            log.info(message);
        }
    }

    /**
     * 
     * @return The AUT Agent responsible for managing running AUTs.
     */
    public AutAgent getAgent() {
        return m_agent;
    }
    
    /**
     * A thread waiting for termination of the autServerVM. Puts the exit code
     * into m_autExitValue and handle the termination.
     *
     * @author BREDEX GmbH
     * @created 03.08.2004
     */
    private static class AUTServerWatcher extends Thread {

        /** lock for synchronizing on m_autServerVM */
        private final Object m_autServerLock = new Object();
        
        /** 
         * the started VM the AUTServer running in, it's null, when no AutServer
         * was started 
         */
        private Process m_autServerProcess;

        /** the exit value of the VM the AUTServer is running in */
        private int m_autExitValue;

        /** used to pick up the 'Unrecognized option' error stream */
        private String m_errorStream;

        /**
         * whether the server is expecting the AUT server to stop. Used for
         * deciding whether to report the stop as an error.
         */
        private boolean m_isExpectingAUTServerStop;

        /** whether the AUT was started using the Java agent mechanism */
        private boolean m_isAgentSet;

        /** sends messages concerning the AUT Server */
        private CommunicationHelper m_messenger;
        
        /**
         * Constructor
         * 
         * @param autServerProcess The process in which the AUT and AUT Server
         *                         are running.
         * @param isAgentSet Whether the AUT was started using the Java agent 
         *                   mechanism.
         * @param messenger Sends messages concerning the AUT Server.
         */
        public AUTServerWatcher(Process autServerProcess, boolean isAgentSet, 
                CommunicationHelper messenger) {
            super("AUTServerWatcher"); //$NON-NLS-1$
            m_autServerProcess = autServerProcess;
            m_isAgentSet = isAgentSet;
            m_messenger = messenger;
        }

        /**
         * handles the termination of the AUTServer, uses m_autExitValue
         */
        private void handleStoppedAUTServer() {
            if (log.isInfoEnabled()) {
                log.info("trying to send message with AUTServer exitcode '" //$NON-NLS-1$
                        + String.valueOf(m_autExitValue)
                        + "' to client"); //$NON-NLS-1$
            }
            StartAUTServerStateMessage message = null;
            ChooseCheckModeDialogBP.getInstance().closeDialog();
            ObservationConsoleBP.getInstance().closeShell();
            switch (m_autExitValue) {
                case AUTServerExitConstants.EXIT_OK:
                    log.info("regular termination of AUTServer"); //$NON-NLS-1$
                    break;
                case AUTServerExitConstants.AUT_START_ERROR:
                    message = new StartAUTServerStateMessage(
                        StartAUTServerStateMessage.ERROR, "Error while starting AUT!"); //$NON-NLS-1$
                    break;
                case AUTServerExitConstants.EXIT_INVALID_ARGS:
                    if (m_isAgentSet && (m_errorStream != null)) {
                        message = new StartAUTServerStateMessage(
                                StartAUTServerStateMessage.JDK_INVALID,
                                "JDK 1.5 or higher is required to start your AUT" + //$NON-NLS-1$
                                " via executable file."); //$NON-NLS-1$
                    } else {
                        message = new StartAUTServerStateMessage(
                                StartAUTServerStateMessage.INVALID_ARGUMENTS,
                                "invalid arguments"); //$NON-NLS-1$
                    }
                    break;
                case AUTServerExitConstants.EXIT_INVALID_NUMBER_OF_ARGS:
                    message = new StartAUTServerStateMessage(
                            StartAUTServerStateMessage.INVALID_ARGUMENTS,
                            "invalid number of arguments"); //$NON-NLS-1$
                    break;
                case AUTServerExitConstants.EXIT_UNKNOWN_ITE_CLIENT:
                    message = new StartAUTServerStateMessage(
                            StartAUTServerStateMessage.COMMUNICATION,
                            "establishing communication failed: invalid client"); //$NON-NLS-1$
                    break;
                case AUTServerExitConstants.EXIT_COMMUNICATION_ERROR:
                    message = new StartAUTServerStateMessage(
                            StartAUTServerStateMessage.COMMUNICATION,
                            "establishing communication failed"); //$NON-NLS-1$
                    break;
                case AUTServerExitConstants
                    .EXIT_SECURITY_VIOLATION_AWT_EVENT_LISTENER:
                case AUTServerExitConstants
                    .EXIT_SECURITY_VIOLATION_COMMUNICATION:
                case AUTServerExitConstants.EXIT_SECURITY_VIOLATION_REFLECTION:
                case AUTServerExitConstants.EXIT_SECURITY_VIOLATION_SHUTDOWN:
                    message = new StartAUTServerStateMessage(
                            StartAUTServerStateMessage.SECURITY,
                            "security violation"); //$NON-NLS-1$
                    break;
                case AUTServerExitConstants.EXIT_AUT_NOT_FOUND:
                case AUTServerExitConstants.EXIT_AUT_WRONG_CLASS_VERSION:
                case AUTServerExitConstants.EXIT_MISSING_AGENT_INFO:
                    // do nothing : AUTServer sent already a message
                    break;
                case AUTServerExitConstants.RESTART:
                    message = m_messenger.handleAutRestart();
                    break;
                default:
                    log.error("unknown AUTServer exit code: " //$NON-NLS-1$
                            + m_autExitValue + "'"); //$NON-NLS-1$
                    message = new StartAUTServerStateMessage(
                        StartAUTServerStateMessage.ERROR,
                        "unknown AUTServer exit code: '" //$NON-NLS-1$
                        + m_autExitValue + "'"); //$NON-NLS-1$
            }
            m_messenger.sendStoppedAUTServerMessage(message);
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            DevNull dn;
            try {
                // clear the streams of the autServerVM
                synchronized (m_autServerLock) {
                    dn = new DevNull(m_autServerProcess.getErrorStream());
                    dn.start();
                    new DevNull(m_autServerProcess.getInputStream()).start();
                }
                // don't synchronized, catching NullPointerException which is
                // raised if the process has already terminated
                m_autExitValue = m_autServerProcess.waitFor();
                // picking up the 'Unrecognized option' error stream
                m_errorStream = dn.getLine();

                synchronized (m_autServerLock) {
                    m_autServerProcess = null;
                }

                if (log.isInfoEnabled()) {
                    log.info("VM stopped with exitValue " //$NON-NLS-1$
                        + String.valueOf(m_autExitValue));
                }

                if (!m_isExpectingAUTServerStop) {
                    handleStoppedAUTServer();
                }

                m_isExpectingAUTServerStop = false;

            } catch (InterruptedException ie) {
                log.info("thread observing autserver process interrupted", ie); //$NON-NLS-1$
            } catch (NullPointerException npe) {
                log.debug("autserver process already terminated", npe); //$NON-NLS-1$
            }
        }
    }

    /**
     * Helper class for sending messages through the AutStarter's 
     * communicator(s).
     * 
     * @author BREDEX GmbH
     * @created Feb 25, 2010
     */
    private class CommunicationHelper {
        /**
         * sends a message via communicator if the AutServer has stopped
         * @param message the message to send
         */
        public void sendStoppedAUTServerMessage(
                StartAUTServerStateMessage message) {
            if (message != null) {
                try {
                    getCommunicator().send(message);
                } catch (CommunicationException bce) { // NOPMD by al on 3/19/07 1:55 PM
                    // communication already closed, do nothing
                } catch (NullPointerException npe) { // NOPMD by al on 3/19/07 1:56 PM
                    // communication already closed, do nothing
                }
            }
        }
        
        /**
         * Handles the restart of the AUT(Server) while test execution
         * @return StartAUTServerStateMessage
         */
        public StartAUTServerStateMessage handleAutRestart() {
            StartAUTServerStateMessage message = null;
            getAutCommunicator().close();
            getAutCommunicator().getConnectionManager()
                .remove(getAutCommunicator().getConnection());
            try {
                initAutConnectionSocket();
            } catch (JBVersionException e) {
                message = new StartAUTServerStateMessage(
                    StartAUTServerStateMessage.COMMUNICATION,
                    "version exception while restart AUT"); //$NON-NLS-1$
            } catch (IOException e) {
                message = new StartAUTServerStateMessage(
                    StartAUTServerStateMessage.COMMUNICATION,
                    "io exception while restart AUT"); //$NON-NLS-1$
            }
            return message;
        }
    }
    
    /**
     * Inner class listening for closing connections. In case of a shutdown the
     * communicator is restarted.
     *
     * @author BREDEX GmbH
     * @created 26.07.2004
     */
    private class CommunicationListener
        implements ICommunicationErrorListener {

        /**
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            log.error("connectingFailed() called although this is a server"); //$NON-NLS-1$
        }
        /**
         * {@inheritDoc}
         */
        public void connectionGained(InetAddress inetAddress, int port) {
            if (log.isInfoEnabled()) {
                try {
                    String message = "accepted connection from " + //$NON-NLS-1$
                        inetAddress.getHostName()
                        + ":" + String.valueOf(port); //$NON-NLS-1$
                    log.info(message);
                } catch (SecurityException se) {
                    log.warn("security violation while getting the host name from ip address", //$NON-NLS-1$
                            se);
                }
            }
        }
        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            log.error("accepting failed on port: " + String.valueOf(port)); //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            log.warn("sending message failed: " + String.valueOf(message)); //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            log.info("connection closed"); //$NON-NLS-1$
        }
    }

}
