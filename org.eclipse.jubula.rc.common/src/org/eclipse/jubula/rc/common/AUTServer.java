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
package org.eclipse.jubula.rc.common;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.message.AUTServerStateMessage;
import org.eclipse.jubula.communication.message.AUTStartMessage;
import org.eclipse.jubula.communication.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.rc.common.commands.AUTStartCommand;
import org.eclipse.jubula.rc.common.commands.ChangeAUTModeCommand;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.listener.AUTEventListener;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.common.listener.IAutListenerAppender;
import org.eclipse.jubula.rc.common.registration.AgentRegisterAut;
import org.eclipse.jubula.rc.common.registration.IRegisterAut;
import org.eclipse.jubula.tools.constants.AUTServerExitConstants;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.constants.DebugConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.exception.JBVersionException;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.registration.AutIdentifier;



/**
 * The AutServer controling the AUT. <br>
 * A quasi singleton: the instance is created from main(). <br>
 * Expected arguments to main are, see also
 * StartAUTServerCommand.createCmdArray():
 * <ul>
 * <li>The name of host where the client is running on, must be InetAddress
 * conform.</li>
 * <li>The port the JubulaClient is listening to.</li>
 * <li>The main class of the AUT.</li>
 * <li>Any further arguments are interpreted as arguments to the AUT.</li>
 * <ul>
 * When a connetion to the JubulaClient could made, any errors will send as a
 * message to the JubulaClient.
 * 
 * Changing the mode to OBJECT_MAPPING results in installing an AWTEventListener
 * (an instance of <code>MappingListener</code>). For simplification the
 * virtual machine is closed  without sending a message to the client when an
 * error occurs during the installation of the AWTEventListener. The exitcode is
 * the appopriate EXIT_* constant
 * 
 * Changing the mode to TESTING removes the installed MappingListener.
 * 
 * @author BREDEX GmbH
 * @created 10.07.2006
 */
public abstract class AUTServer {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTServer.class);
    
    /** the instance */
    private static AUTServer instance = null;
    
    /** the name of the correct autServer class */
    private static String autServerName;

    /** the communicator to use to communicate with the client*/
    private Communicator m_communicator;

    /** the communicator to use to communicate to the AutStarter*/
    private Communicator m_serverCommunicator;

    /** the listener to the client communicator */
    private ICommunicationErrorListener m_communicationListener;

    /** the listener to the agent communicator */
    private ICommunicationErrorListener m_serverCommunicationListener;

    /** the port the Server is listen to */
    private String m_autStarterPort;

    /** the name of the main class of the AUT */
    private String m_autMainClassName;

    /** the class of the AUT, containing the main - method */
    private Class m_autMainClass;
    
    /** the main method of the AUT to invoke*/
    private Method m_autMainMethod;
    
    /** Thread in which the AUT runs */
    private Thread m_autThread;
   
    /** the args for the AUT */
    private String[] m_autArgs;
    
    /**
     * the mode the AUTserver is in, see constants in ChangeAUTModeMessage, the
     * default is TESTING.
     */
    private int m_mode = ChangeAUTModeMessage.TESTING;
    
    /** Indicator wether the AUT is runnning or not */
    private boolean m_isAutRunning = false;
    
    /** true if open, false otherwise */
    private boolean m_isObservingDialogOpen = false;
    
    /** timestamp for danymic WaitForWindow-Timeout for OberservMode*/
    private long m_observTimestamp = 0;
        
    /** the AWTEventLister for mode OBJECT_MAPPING */
    private AUTEventListener m_mappingListener;

    /** the AWTEventLister for mode RECORD_MODE */
    private AUTEventListener m_recordListener;
    
    /** the AWTEventLister for mode CHECK_MODE */
    private AUTEventListener m_checkListener;
    
    /** true, if this method was called by RcpAccessor Plug-in */
    private boolean m_isRcpAccessible;
    
    /** true, if the main method of AUTServer was called by Java Agent */
    private boolean m_isAgentSet = false;
    
    /** appenders that will be called when the Inspector is activated */
    private List m_inspectorListenerAppenders = new ArrayList();

    /** the hostname of the Aut Agent to use for registration */
    private String m_autAgentHost;

    /** the port number of the Aut Agent to use for registration */
    private String m_autAgentPort;
    
    /** the AUT name to use for registration with the Aut Agent */
    private String m_autName;

    /** 
     * private constructor instantiates the listeners
     * @param mappingListener new instance of toolkit mapping server
     * @param recordListener new instance of toolkit record server
     * @param checkListener new instance of toolkit check server
     */
    protected AUTServer(AUTEventListener mappingListener, 
            AUTEventListener recordListener, AUTEventListener checkListener) {
        
        m_communicationListener = new ClientCommunicationListener();
        m_serverCommunicationListener = new AgentCommunicationListener();
        
        m_mappingListener = mappingListener;
        m_recordListener = recordListener;
        m_checkListener = checkListener;
        
    }
    
    /** 
     * puts the given arguments to member variables
     * @param args the args given to main() 
     */
    protected void setArgs(String args[]) {
        // required arguments 
        m_autStarterPort = args[Constants.ARG_SERVERPORT];
        m_autMainClassName = args[Constants.ARG_AUTMAIN];
        m_autAgentHost = args[Constants.ARG_REG_HOST];
        m_autAgentPort = args[Constants.ARG_REG_PORT];
        m_autName = args[Constants.ARG_AUT_NAME]; 
        // arguments for the aut, is >= 0, see definition of the constants
        int numberAutArgs = args.length - Constants.MIN_ARGS_REQUIRED;
        m_autArgs = new String[numberAutArgs];
        for (int i = 0; i < numberAutArgs; i++) {
            m_autArgs[i] = args[Constants.MIN_ARGS_REQUIRED + i];
        }
        // check if agent is used
        if (args.length == Constants.MIN_ARGS_REQUIRED 
                && args[Constants.ARG_AGENT_SET].equals(
                        CommandConstants.RC_COMMON_AGENT_ACTIVE)) {
            m_isAgentSet = true;
        }
    }

    /**
     * @return Returns the autArgs.
     */
    public String[] getAutArgs() {
        return m_autArgs;
    }

    /**
     * @return Returns the autMainClassName.
     */
    public String getAutMainClassName() {
        return m_autMainClassName;
    }

    /**
     * @return Returns the autMainClass.
     */
    public Class getAutMainClass() {
        return m_autMainClass;
    }
    
    /**
     * @return Returns the autMainMethod.
     */
    public Method getAutMainMethod() {
        return m_autMainMethod;
    }
    
    /**
     * @return Returns the communicator.
     */
    public synchronized Communicator getCommunicator() {
        return m_communicator;
    }

    /**
     * @return Returns the communicator.
     */
    public synchronized Communicator getServerCommunicator() {
        return m_serverCommunicator;
    }

    /**
     * Method to get the single instance of this class.
     * @return the instance of this Singleton
     */
    public static AUTServer getInstance() {
        if (instance == null) {
            try {
                instance = (AUTServer)Class.forName(autServerName)
                    .newInstance();
            } catch (ClassNotFoundException cnfe) {
                log.error("creating an AUTServer sharedInstance for " //$NON-NLS-1$ 
                                + autServerName + "failed:" + //$NON-NLS-1$);
                                cnfe.getMessage());
            } catch (InstantiationException ie) {
                log.error("creating an AUTServer sharedInstance for " //$NON-NLS-1$ 
                                + autServerName + "failed:" + //$NON-NLS-1$);
                                ie.getMessage());
            } catch (IllegalAccessException iae) {
                log.error("creating an AUTServer sharedInstance for " //$NON-NLS-1$ 
                                + autServerName + "failed:" + //$NON-NLS-1$);
                                iae.getMessage());
            }
        }
        return instance;
    }
    
    /**
     * main method:<p>
     * 1. check args and store args<p>
     * 2. call start
     * @param args - the args 
     */
    public static void main(String[] args) {
        InputStream logPropertiesStream = null;
        try {
            logPropertiesStream = 
                AUTServer.class.getClassLoader().getResourceAsStream("resources/logging.properties"); //$NON-NLS-1$
            if (logPropertiesStream != null) {
                LogManager.getLogManager().readConfiguration(
                        logPropertiesStream);
            } else {
                System.err.println("RC logging.properties not found."); //$NON-NLS-1$
            }
        } catch (SecurityException e) {
            System.err.println("RC logging.properties not loaded: " + e.getLocalizedMessage()); //$NON-NLS-1$
        } catch (IOException e) {
            System.err.println("RC logging.properties not loaded: " + e.getLocalizedMessage()); //$NON-NLS-1$
        } finally {
            if (logPropertiesStream != null) {
                try {
                    logPropertiesStream.close();
                } catch (IOException e) {
                    // ignore any errors that occur while closing the stream
                }
            }
        }

        if (args.length < Constants.MIN_ARGS_REQUIRED) {
            log.error("wrong number of arguments: " //$NON-NLS-1$
                    + "must be at least " //$NON-NLS-1$
                    + String.valueOf(Constants.MIN_ARGS_REQUIRED)
                    + ", but were " + Arrays.asList(args).toString());  //$NON-NLS-1$
            System.exit(AUTServerExitConstants.EXIT_INVALID_NUMBER_OF_ARGS);
        }

        if (log.isDebugEnabled()) {
            StringBuffer logMessage = new StringBuffer("Arguments to AUTServer\n"); //$NON-NLS-1$
            for (int i = 0; i < args.length; i++) {
                logMessage.append(String.valueOf(i));
                logMessage.append(args[i] + "\n"); //$NON-NLS-1$ 
            }
            log.debug(logMessage.toString());
        }
        if (log.isInfoEnabled()) {
            log.info(System.getProperty("java.version")); //$NON-NLS-1$
            log.info("user.dir:" + //$NON-NLS-1$
                System.getProperty("user.dir")); //$NON-NLS-1$
        }
        autServerName = args[Constants.ARG_AUTSERVER_NAME];
        AUTServer.getInstance().setArgs(args);
        AUTServer.getInstance().start(false);
    }
    
    /**
     * Starts the AUTServer in its own Thread with its own ClassLoader.
     */
    public void startAUT() {
        if (isRcpAccessible()) {
            return;
        }
        if (isAgentSet()) {
            //if java agent is to be used, start tasks without invoking aut again
            startToolkitThread();
            addToolKitEventListenerToAUT();
            return;
        }
        setAutThread(new Thread(new Runnable() {
            public void run() {
                try {
                    startTasks();
                } catch (ExceptionInInitializerError e) {
                    log.error(String.valueOf(e), e);
                    System.exit(AUTServerExitConstants.AUT_START_ERROR);
                } catch (InvocationTargetException e) {
                    String exception = String.valueOf(e);
                    String targetException = String.valueOf(
                        e.getTargetException());
                    String message = exception + " TargetException: "  //$NON-NLS-1$
                        + targetException;
                    log.error(message, e);
                    System.exit(AUTServerExitConstants.AUT_START_ERROR);
                } catch (NoSuchMethodException e) {
                    log.error(String.valueOf(e), e);
                    System.exit(AUTServerExitConstants.AUT_START_ERROR);
                }
            }
        }, "Main (AUT)")); //$NON-NLS-1$
        getAutThread().setDaemon(false);
        getAutThread().setContextClassLoader(
                ClassLoader.getSystemClassLoader());
        getAutThread().start();
    }
    
    /**
     * Stops the AUT-Thread and sets m_autThread to null.<br>
     * Calls the stop() method on the m_autThread instance!
     * @return true if the AUT has been stoppen, false otherwise
     */
    public boolean stopAUT() {
        return closeAUT();
    }
    
    /**
     * Closes the AUT.
     * @return true if the AUT has been stoppen, false otherwise
     */
    protected abstract boolean closeAUT();
    
    /**
     * initializes the AUTServer. <br>
     * 1. create communicator <br>
     * 2. connect to client <br>
     * 3. register shutdown hook, not yet <br>
     * 4. register a AWTEventListener, not yet <br>
     * 5. load the AUT <br>
     * 6. send message AUTServerReady <br>
     * In case of an error in step >2. send an AUTServerStateMessage with an
     * error code
     * @param isRcpAccessible true, if this method was called by RcpAccessor Plug-in
     */
    public void start(boolean isRcpAccessible) {
        m_isRcpAccessible = isRcpAccessible;
        try {
            IRegisterAut autReg = parseAutReg();
            if (autReg == null) {
                String errorMessage = "Unable to initialize connection to AUT Agent: No connection information provided."; //$NON-NLS-1$
                log.error(errorMessage);
                sendExitReason(errorMessage, 
                        AUTServerExitConstants.EXIT_MISSING_AGENT_INFO);
            }
            if (!isRcpAccessible && !isAgentSet()) {
                loadAUT(); // create a Class of the aut
            }
            if (m_isAgentSet || isRcpAccessible) {
                setAutThread(Thread.currentThread());
            }
            if (m_communicator != null) {
                m_communicator.send(new AUTServerStateMessage(
                        AUTServerStateMessage.READY));
            } else {
                /* calling this method from the AUT-main thread caused an
                 * infinite loop when waiting for the AUT-Display in SWT */
                new Thread("Start AUT Server") { //$NON-NLS-1$ 
                    public void run() {
                        AUTStartCommand startCommand = new AUTStartCommand();
                        startCommand.setMessage(new AUTStartMessage());
                        startCommand.execute();
                    }
                } .start();
            }
            // Keep the thread (and therefore the JVM) alive until the AUT thread is started.
            while ((getAutThread() == null || !getAutThread().isAlive())
                    && !isAutRunning()) {
                try {
                    Thread.sleep(TimingConstantsServer.POLLING_DELAY_AUT_START);
                } catch (InterruptedException e) { /* Do nothing */ }
            }
            if (autReg != null) {
                try {
                    autReg.register();
                } catch (IOException ioe) {
                    log.error("Exception during AUT registration", ioe); //$NON-NLS-1$
                    System.exit(AUTServerExitConstants.AUT_START_ERROR);
                }
            }
        } catch (IllegalArgumentException iae) {
            log.error("Exception in start()", iae); //$NON-NLS-1$
            System.exit(AUTServerExitConstants.EXIT_INVALID_ARGS);
        } catch (CommunicationException ce) {
            log.error("Exception in start()", ce); //$NON-NLS-1$
            System.exit(AUTServerExitConstants.EXIT_COMMUNICATION_ERROR);
        } catch (SecurityException se) {
            log.error("Exception in start()", se); //$NON-NLS-1$
            System.exit(AUTServerExitConstants
                    .EXIT_SECURITY_VIOLATION_REFLECTION);
        } catch (ClassNotFoundException cnfe) {
            sendExitReason(cnfe, AUTServerStateMessage.AUT_NOT_FOUND);
            System.exit(AUTServerExitConstants.EXIT_AUT_NOT_FOUND);
        } catch (NoSuchMethodException nsme) {
            sendExitReason(nsme, AUTServerStateMessage.MAIN_METHOD_NOT_FOUND);
            System.exit(AUTServerExitConstants.EXIT_AUT_NOT_FOUND);
        } catch (UnsupportedClassVersionError ucve) {
            sendExitReason(ucve,
                    AUTServerStateMessage.EXIT_AUT_WRONG_CLASS_VERSION);
            System.exit(AUTServerExitConstants.EXIT_AUT_WRONG_CLASS_VERSION);
        } catch (JBVersionException ve) {
            sendExitReason(ve, 
                    AUTServerStateMessage.EXIT_AUT_WRONG_CLASS_VERSION);
            System.exit(AUTServerExitConstants.EXIT_UNKNOWN_GUIDANCERCLIENT);
        }
    }
    
    
    /**
     * 
     * @return an object capable of registering the AUT managed by this server,
     *         or <code>null</code> if no such object could be created from the
     *         server's current properties. 
     */
    protected IRegisterAut parseAutReg() {
        if (m_autAgentHost != null 
                && m_autAgentPort != null && m_autName != null) {
            try {
                int autAgentPort = Integer.parseInt(m_autAgentPort);
                InetSocketAddress agentAddr = 
                    new InetSocketAddress(m_autAgentHost, autAgentPort);
                AutIdentifier autIdentifier = new AutIdentifier(m_autName);
                return new AgentRegisterAut(agentAddr, autIdentifier);
            } catch (NumberFormatException nfe) {
                log.warn("Unable to parse port number for Aut Agent. Continuing as if no Aut Agent information was provided.", nfe); //$NON-NLS-1$
            }
        }
        return null;
    }

    /**
     * Initializes the communication between:
     * <ul>
     * <li>Client and AUT</li>
     * <li>AUT Agent and AUT</li>
     * </ul>
     * 
     * @param clientHostName
     *            Host name to use for connecting to the client.
     * @param clientPort
     *            Port number to use for connecting to the client.
     * 
     * @throws UnknownHostException
     *             if no IP address can be found for <code>clientHostName</code>
     *             .
     */
    public void initClientCommunication(String clientHostName, int clientPort) 
        throws UnknownHostException {
        
        if (log.isDebugEnabled()) {
            log.debug("initializing communication"); //$NON-NLS-1$                
        }
        // create communicators
        InetAddress clientAddress = 
            InetAddress.getByName(clientHostName);
        createCommunicator(new InetSocketAddress(clientAddress, clientPort));
        connectGdClient();
    }

    /**
     * connect the JubulaClient
     */
    protected void connectGdClient() {
        try { 
            m_communicator.run();
        } catch (SecurityException se) {
            log.error("Exception in start()", se); //$NON-NLS-1$
            System.exit(AUTServerExitConstants
                    .EXIT_SECURITY_VIOLATION_COMMUNICATION);
        } catch (JBVersionException e) {
            log.error(e.toString());
        }
    }
    
    /**
     * sends an AUTServerStateMessage via Communicator if an error occures
     * @param e catched exception
     * @param exitCode the exit code for the catched exception
     */
    protected void sendExitReason(Throwable e, int exitCode) {
        log.error("Exception in start()", e); //$NON-NLS-1$
        sendExitReason(e.getMessage(), exitCode);
    }

    /**
     * sends an AUTServerStateMessage via Communicator if an error occures
     * @param errorMessage Detailed error text for the sent message.
     * @param exitCode the exit code for the error that occurred.
     */
    protected void sendExitReason(String errorMessage, int exitCode) { 
        try {
            m_communicator.send(new AUTServerStateMessage(
                    exitCode, errorMessage));
        } catch (CommunicationException ce) {
            log.error("Exception in start()", ce); //$NON-NLS-1$
        }
    }

    /**
     * Creates a communicator to the client
     * @param clientAddress the clientAdress
     */
    private void createCommunicator(InetSocketAddress clientAddress) {
        m_communicator = createComm(clientAddress);
        m_communicator
                .addCommunicationErrorListener(m_communicationListener);
    }
    
    /**
     * Creates a communicator to the client 
     * @param clientAddress the clientAdress
     * @return A Communicator
     */
    protected Communicator createComm(InetSocketAddress clientAddress) {
        return new Communicator(clientAddress.getAddress(), 
                clientAddress.getPort(), 
                Thread.currentThread().getContextClassLoader());
    }
    

    /**
     * Initializes communication between the receiver and the AUT Agent at the
     * given address.
     * 
     * @param agentAddress
     *            The address of the waiting AUT Agent.
     * @param agentPort
     *            The port on which the AUT Agent is listening.
     * @throws SecurityException
     *             if the security manager does not allow connections.
     * @throws JBVersionException
     *             in case of version error between AUT Agent and AUT Server.
     */
    public void initAutAgentCommunicator(
            InetAddress agentAddress, int agentPort) 
        throws SecurityException, JBVersionException {
        
        m_serverCommunicator = new Communicator(agentAddress, agentPort, 
                Thread.currentThread().getContextClassLoader());
        m_serverCommunicator.addCommunicationErrorListener(
                m_serverCommunicationListener);

        m_serverCommunicator.run();
    }
    
    /**
     * Starts the AWT-EventQueue-Thread. <br>
     * <b>Important:</b> Must be called in complete AUT environment!
     * (Thread, ClassLoader, etc.)
     */
    protected abstract void startToolkitThread();
    
    /**
     * Adds the EventListener to the AUT<br>
     * <b> Important:</b> First start the EventQueue-Thread!
     */
    public void addToolKitEventListenerToAUT() {
        final ClassLoader oldCL = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass()
            .getClassLoader());
        // install the component handler and the focus tracker
        addToolkitEventListeners();
        Thread.currentThread().setContextClassLoader(oldCL);
    }
    
    /**
     * installs the component handler and the focus tracker
     */
    protected abstract void addToolkitEventListeners();
    
    /**
     * loads the aut, does not instantiate the autMainClass nor invoke the main
     * method, just sets the member variable autMainClass and autMainMethod 
     * @throws SecurityException thrown from the security manager
     * @throws ClassNotFoundException thrown by Class.forName()
     * @throws NoSuchMethodException thrown if no main method exists in m_autMainClass
     * @throws UnsupportedClassVersionError thrown if class files are generated with unsupported jre
     */
    private void loadAUT() throws SecurityException, ClassNotFoundException, 
        NoSuchMethodException, UnsupportedClassVersionError {
        
        if (log.isInfoEnabled()) {
            log.info("loading the AUT"); //$NON-NLS-1$
        }
        m_autMainClass = ClassLoader.getSystemClassLoader()
            .loadClass(m_autMainClassName);
        m_autMainMethod = m_autMainClass.getMethod("main", //$NON-NLS-1$
            new Class[] { m_autArgs.getClass() });
    
        // make the main method accesible
        m_autMainMethod.setAccessible(true);
        int mods = m_autMainMethod.getModifiers();
        if (m_autMainMethod.getReturnType() != void.class
            || !Modifier.isStatic(mods)
            || !Modifier.isPublic(mods)) {
            m_autMainMethod = null;
            throw new NoSuchMethodException(
                "no public static main"); //$NON-NLS-1$
        }
    }
    
    /**
     * Invokes the main method of the aut (stored in m_autMainMethod).
     * 
     * @throws ExceptionInInitializerError from call to invoke(), see Method.invoke()
     * @throws InvocationTargetException from call to invoke(), see Method.invoke()
     * @throws NoSuchMethodException if the main method could not be found during starting the AUTServer
     */
    public void invokeAUT() throws ExceptionInInitializerError,
        InvocationTargetException, NoSuchMethodException {
        if (m_autMainMethod == null) {
            log.error("the main method of the AUT could not be found!"); //$NON-NLS-1$
            throw new NoSuchMethodException("no public static main in AUT"); //$NON-NLS-1$
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("invoking main method of the AUT"); //$NON-NLS-1$
            }
            m_isAutRunning = true;
            // invoke a static method 
            m_autMainMethod.invoke(null, new Object[] {m_autArgs});
        } catch (IllegalArgumentException iae) {
            m_isAutRunning = false;
            log.error(DebugConstants.ERROR, iae);
        } catch (IllegalAccessException iae) {
            m_isAutRunning = false;
            log.error(DebugConstants.ERROR, iae);
        } catch (NullPointerException npe) {
            m_isAutRunning = false;
            log.error(DebugConstants.ERROR, npe);
        } catch (RuntimeException re) {
            m_isAutRunning = false;
            log.error("unexpected exception thrown by AUT: ", re); //$NON-NLS-1$
            throw re;
        }
    }
    
    /**
     * changes the mode to <code>newMode</code>
     * @param newMode the new mode to change to, valid values are the constants defined in ChangeAUTModeMessage.
     */
    public void setMode(int newMode) {
        if (log.isInfoEnabled()) {
            log.info("changing mode from "  //$NON-NLS-1$
                    + String.valueOf(m_mode)
                    + " to " //$NON-NLS-1$
                    + String.valueOf(newMode));
        }
        // restore mode to oldMode in case of an unknown mode
        int oldMode = m_mode;
        m_mode = newMode;
        switch (newMode) {
            // => (remove TestAWTEventListener),
            // install a MappingAWTEventListener
            case ChangeAUTModeMessage.OBJECT_MAPPING:
                removeToolkitEventListener(m_mappingListener);
                removeToolkitEventListener(m_recordListener);
                removeToolkitEventListener(m_checkListener);
                refreshMode();
                addToolkitEventListener(m_mappingListener);
                break;
            case ChangeAUTModeMessage.RECORD_MODE:
                removeToolkitEventListener(m_mappingListener);
                removeToolkitEventListener(m_recordListener);
                removeToolkitEventListener(m_checkListener);
                m_mappingListener.cleanUp();
                addToolkitEventListener(m_recordListener);
                if (oldMode != ChangeAUTModeMessage.CHECK_MODE) {
                    setObservTimestamp(0);
                }
                break;
            case ChangeAUTModeMessage.CHECK_MODE:
                removeToolkitEventListener(m_mappingListener);
                removeToolkitEventListener(m_recordListener);
                removeToolkitEventListener(m_checkListener);
                m_mappingListener.cleanUp();
                addToolkitEventListener(m_checkListener);
                break;
            case ChangeAUTModeMessage.TESTING:
                // => remove MappingAWTEventListener
                removeToolkitEventListener(m_mappingListener);
                removeToolkitEventListener(m_recordListener);
                removeToolkitEventListener(m_checkListener);
                m_recordListener.cleanUp();
                m_checkListener.cleanUp();
                break;
            default:
                log.error("unkown mode: " //$NON-NLS-1$ 
                        + String.valueOf(newMode)); 
                m_mode = oldMode;
        }
    }

    /**
     * refreshes AUT
     */
    public void refreshMode() {
        m_mappingListener.cleanUp();
        m_recordListener.cleanUp();
        m_checkListener.cleanUp();
    }

    /**
     * Adds the given listener to the AWTEventQueue. <br>
     * In case of a security exception this method will closing the VM.
     * @param listener the listener to add, can be null
     */
    protected abstract void addToolkitEventListener(BaseAUTListener listener);
    
    /**
     * Removes the given listener from the AWTEventQueue. <br>
     * In case of a security exception this method will closing the VM.
     * 
     * @param listener the listener to remove, can be null
     */
    protected abstract void removeToolkitEventListener(
            BaseAUTListener listener);
    
    /**
     * @return the mode the AUTserver is in, see als <code>setMode()</code>.
     */
    public int getMode() {
        return m_mode;
    }
    
    /**
     * highlight a component sent to server by client
     * @param comp ComponentIdentifier
     * @return boolean successful?
     */
    public boolean highlightComponent(IComponentIdentifier comp) {
        return m_mappingListener.highlightComponent(comp); 
    }

    /**
     * highlight a component sent to server by client
     */
    public void updateHighLighter() {
        m_mappingListener.update(); 
    }

    /**
     * @throws ExceptionInInitializerError Error     
     * @throws InvocationTargetException Error
     * @throws NoSuchMethodException Error
     */
    protected abstract void startTasks() 
        throws ExceptionInInitializerError, 
               InvocationTargetException, 
               NoSuchMethodException;

    /**
     * The listener listening for event from the communicator.
     * @author BREDEX GmbH
     * @created 05.08.2004
     *
     */
    private abstract class AbstractCommunicationListener 
            implements ICommunicationErrorListener {
        
        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            log.debug("acceptingFailed() called although " + //$NON-NLS-1$
                "this is a client"); //$NON-NLS-1$
            terminate();
        }
        /**
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            String message = StringConstants.EMPTY;
            try {
                message  = "connecting to " //$NON-NLS-1$
                    + inetAddress.getHostName() 
                    + ":" + String.valueOf(port) //$NON-NLS-1$
                    + " failed()"; //$NON-NLS-1$
               
                log.error(message); 
            } catch (SecurityException se) {
                log.error("security violation during getting the " //$NON-NLS-1$
                       + " host name from ip address " //$NON-NLS-1$
                       + "in connectingFailed()"); //$NON-NLS-1$
            } finally {
                terminate();
            }

        }
        
        /**
         * {@inheritDoc}
         */
        public void connectionGained(InetAddress inetAddress, int port) {
            if (log.isInfoEnabled()) {
                String message;
                try {
                    message = "connected to "  //$NON-NLS-1$
                        + inetAddress.getHostName() 
                        + ":" + String.valueOf(port); //$NON-NLS-1$
                } catch (SecurityException se) {
                    log.debug("security violation during getting" //$NON-NLS-1$
                        + " the host name from ip address " //$NON-NLS-1$
                        + "in connectingGained()"); //$NON-NLS-1$
                    message = " connected"; //$NON-NLS-1$
                }
                log.info(message);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            log.error("sending message " + message.toString() + "failed"); //$NON-NLS-1$ //$NON-NLS-2$
            terminate();
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            if (log.isInfoEnabled()) {
                log.info("connection closed"); //$NON-NLS-1$                
            }
            if (log.isInfoEnabled()) {
                log.info("exiting with " + String //$NON-NLS-1$ 
                    .valueOf(AUTServerExitConstants.EXIT_COMMUNICATION_ERROR));
            }
            terminate();
        }
        
        /** 
         * Handle terminated connection.
         */
        protected abstract void terminate();
        
    }

    /**
     * Listener for events that occur during communication with an 
     * AUT Agent.
     *
     * @author BREDEX GmbH
     * @created Feb 24, 2010
     *
     */
    private class AgentCommunicationListener 
            extends AbstractCommunicationListener {

        /**
         * 
         * {@inheritDoc}
         */
        protected void terminate() {
            try {
                shutdown();
            } finally {
                System.exit(AUTServerExitConstants.EXIT_COMMUNICATION_ERROR);
            }
        }
    }

    /**
     * Listener for events that occur during communication with a client.
     * 
     * @author BREDEX GmbH
     * @created Feb 24, 2010
     * 
     */
    private class ClientCommunicationListener 
            extends AbstractCommunicationListener {

        /**
         * {@inheritDoc}
         */
        protected void terminate() {
            ChangeAUTModeMessage message = new ChangeAUTModeMessage();
            message.setMode(ChangeAUTModeMessage.TESTING);
            ChangeAUTModeCommand changeModeCmd = new ChangeAUTModeCommand();
            changeModeCmd.setMessage(message);
            changeModeCmd.execute();
            if (m_communicator != null) {
                m_communicator.close();
            }
        }
        
    }
    
    /** @return the autThread **/
    protected Thread getAutThread() {
        return m_autThread;
    }

    /**
     * @return Returns true if the AUT is running, false otherwise.
     */
    public boolean isAutRunning() {
        return m_isAutRunning;
    }
    
    /**
     * @return An IRobot instance
     */
    public abstract IRobot getRobot();

    /**
     * Starts an Inspector that allows data for the next component clicked to 
     * be sent to the client.
     * 
     */
    public final void startInspector() {
        IAutListenerAppender [] inspectorAppenders = 
            (IAutListenerAppender [])m_inspectorListenerAppenders.toArray(
                    new IAutListenerAppender[
                        m_inspectorListenerAppenders.size()]);
        for (int i = 0; i < inspectorAppenders.length; i++) {
            inspectorAppenders[i].addAutListener();
        }
    }

    /**
     * Adds the given appender to the list of appenders that will be called
     * when the Inspector is activated.
     * 
     * @param appender The appender to add.
     */
    public final void addInspectorListenerAppender(
            IAutListenerAppender appender) {
        m_inspectorListenerAppenders.add(appender);
    }
    
    /**
     * <HR NOSHADE><CENTER><FONT color="#FF0000"><b>ONLY TO USE FOR SWT-JUNIT TESTS<br>
     * AND<br>FOR "RcpAccessor" PLUG-IN !!!</b></FONT></CENTER><HR NOSHADE>
     * @param swtAutServerName the swtAutServerName to set
     * @return an single instance of the autSrever
     */
    public static AUTServer getInstance(String swtAutServerName) {
        autServerName = swtAutServerName;
        return getInstance();
    }

    /**
     * @param autStarterPort the AutStarterPort to set
     */
    public void setAutStarterPort(String autStarterPort) {
        m_autStarterPort = autStarterPort;
    }

    /**
     * @return the true, if aut server runs as plugin in RCP-AUT
     */
    public boolean isRcpAccessible() {
        return m_isRcpAccessible;
    }
    
    /**
     * @return true, if aut server got started via Java Agent
     */
    public boolean isAgentSet() {
        return m_isAgentSet;
    }
    
    /**
     * @return the isObservingDialogOpen
     */
    public boolean isObservingDialogOpen() {
        return m_isObservingDialogOpen;
    }

    /**
     * @param isObservingDialogOpen the isObservingDialogOpen to set
     */
    public void setObservingDialogOpen(boolean isObservingDialogOpen) {
        m_isObservingDialogOpen = isObservingDialogOpen;
    }
    
    /**
     * @return timestamp of last recorded action  
     */
    public long getObservTimestamp() {
        return m_observTimestamp;
    }
    
    /**
     * @param timestamp the timestamp of last recorded action
     */
    public void setObservTimestamp(long timestamp) {
        m_observTimestamp = timestamp;
    }
    /**
     * @param autThread the autThread to set
     */
    private void setAutThread(Thread autThread) {
        m_autThread = autThread;
    }

    /**
     * @param singletonInstance the instance to set
     */
    protected static void setInstance(AUTServer singletonInstance) {
        AUTServer.instance = singletonInstance;
    }
    
    /**
     * call back method to tear down AUTServer in case of termination e.g.
     * communication errors, subclasses may override
     */
    public void shutdown() {
        // empty
    }

    /**
     * @param autAgentHost the autAgentHost to set
     */
    public void setAutAgentHost(String autAgentHost) {
        m_autAgentHost = autAgentHost;
    }

    /**
     * @param autAgentPort the autAgentPort to set
     */
    public void setAutAgentPort(String autAgentPort) {
        m_autAgentPort = autAgentPort;
    }

    /**
     * @param autName the autName to set
     */
    public void setAutName(String autName) {
        m_autName = autName;
    }
    
    
}