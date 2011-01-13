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
package org.eclipse.jubula.rc.swing;

import java.awt.AWTError;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.swing.components.SwingComponent;
import org.eclipse.jubula.rc.swing.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.swing.listener.CheckListener;
import org.eclipse.jubula.rc.swing.listener.ComponentHandler;
import org.eclipse.jubula.rc.swing.listener.FocusTracker;
import org.eclipse.jubula.rc.swing.listener.MappingListener;
import org.eclipse.jubula.rc.swing.listener.RecordListener;
import org.eclipse.jubula.tools.constants.AUTServerExitConstants;



/**
 * The AutServer controling the AUT. <br>
 * A quasi singleton: the instance is created from main(). <br>
 * Expected arguments to main are, see also
 * StartAUTServerCommand.createCmdArray():
 * <ul>
 * <li>The name of host the GuiDancerCLient is running on, must be InetAddress
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
 * @created 26.07.2004
 */
public class SwingAUTServer extends AUTServer {
    
    /** the logger */
    private static final Log LOG = LogFactory.getLog(SwingAUTServer.class);
    
    /** 
     * private constructor
     * instantiates the listeners
     */
    public SwingAUTServer() {
        super(new MappingListener(), new RecordListener(), new CheckListener());
    }

    /**
     * {@inheritDoc}
     */
    protected void startToolkitThread() {
        // add a dummy listener to start the AWT-Thread
        Toolkit.getDefaultToolkit().addAWTEventListener(
                new AWTEventListener() {

                    public void eventDispatched(AWTEvent event) {
                        // do nothing
                    }
                }, 0L);
    }

    /**
     * {@inheritDoc}
     */
    protected void addToolkitEventListeners() {
        // install the component handler
        addToolkitEventListener(new ComponentHandler());
        // install the focus tracker
        addToolkitEventListener(new FocusTracker());
    }

    /**
     * {@inheritDoc}
     * @param listener
     */
    protected void addToolkitEventListener(BaseAUTListener listener) {
        if (LOG.isInfoEnabled()) {
            LOG.info("installing AWTEventListener " //$NON-NLS-1$ 
                + listener.toString());
        }
        try {
            long mask = 0;
            for (int i = 0; i < listener.getEventMask().length; i++) {
                mask = mask | listener.getEventMask()[i];
            }
            Toolkit.getDefaultToolkit().addAWTEventListener(
                    (AWTEventListener)listener, mask);
        } catch (AWTError awte) {
            // no default toolkit
            LOG.error(awte);
        } catch (SecurityException se) {
            // no permission to add an AWTEventListener
            LOG.fatal(se);
            System.exit(AUTServerExitConstants
                    .EXIT_SECURITY_VIOLATION_AWT_EVENT_LISTENER);
        }
    }

    /**
     * {@inheritDoc}
     * @param listener
     */
    protected void removeToolkitEventListener(BaseAUTListener listener) {
        if (LOG.isInfoEnabled()) {
            LOG.info("removing AWTEventListener " //$NON-NLS-1$ 
                + listener.toString());
        }
        try {
            Toolkit.getDefaultToolkit().removeAWTEventListener(
                    (AWTEventListener)listener);
        } catch (AWTError awte) {
            // no default toolkit
            LOG.error(awte);
        } catch (SecurityException se) {
            // no permission to remove an AWTEventListener,
            // should not occur, because addAWTEventListener() should be called 
            // first. But just in case, close the vm
            LOG.fatal(se);
            System.exit(AUTServerExitConstants
                    .EXIT_SECURITY_VIOLATION_AWT_EVENT_LISTENER);
        }
    }

    /**
     * {@inheritDoc}
     * @throws ExceptionInInitializerError
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected void startTasks() throws ExceptionInInitializerError, 
        InvocationTargetException, NoSuchMethodException {
        
        startToolkitThread();
        addToolKitEventListenerToAUT();
        AUTServer.getInstance().invokeAUT();
    }

    /**
     * {@inheritDoc}
     * @return true if the AUT has been stoppen, false otherwise
     */
    protected boolean closeAUT() {
        boolean isClosed = false;
        // new HashSet to avoid ConcurrentModificationException!
        final Set keys = new HashSet(ComponentHandler.getAutHierarchy()
            .getHierarchyMap().keySet());
        final Iterator keyIter = keys.iterator();
        while (keyIter.hasNext()) {
            SwingComponent ci = (SwingComponent)
                keyIter.next();
            final Component comp = ci.getRealComponent();
            if (comp instanceof Window) {
                Window window = (Window)comp;
                window.dispose();
                isClosed = true;
            }
        }
        return isClosed;
    }

    /**
     * {@inheritDoc}
     */
    public IRobot getRobot() {
        IRobotFactory robotFactory = new RobotFactoryConfig().getRobotFactory();
        return robotFactory.getRobot();
    }
    
    
}
