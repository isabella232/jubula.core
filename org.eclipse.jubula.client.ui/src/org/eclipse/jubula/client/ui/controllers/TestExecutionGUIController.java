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
package org.eclipse.jubula.client.ui.controllers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.communication.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.core.communication.ConnectionException;
import org.eclipse.jubula.client.core.communication.ServerConnection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.dialogs.nag.RCPAUTStartDelayNagTask;
import org.eclipse.jubula.client.ui.handlers.AbstractStartTestHandler;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.JBThread;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.client.ui.utils.ServerManager.Server;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.communication.message.StopAUTServerMessage;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.osgi.util.NLS;


/**
 * This class is a gui controller, which methods are directly
 * executed by user actions in the graphical user interface.
 * Most of these actions are delegated to the
 * TestExecutionContributor.
 *
 * @author BREDEX GmbH
 * @created Feb 28, 2006
 */
public class TestExecutionGUIController {

    /** The logger */
    static final Logger LOG = 
        LoggerFactory.getLogger(TestExecutionGUIController.class);
    
    /** the timeout for the info nagger dialog if RCP AUT startup takes too long */
    private static final int NAGGER_TIMEOUT = 120 * 1000;

    /** Utility class */
    private TestExecutionGUIController() {
        // empty constructor
    }

    /**
     * @param aut aut to start
     * @param conf associated configuration for AUT to start
     */
    public static void startAUT(final IAUTMainPO aut, final IAUTConfigPO conf) {
        new JBThread() {
            /** inform user if AUT does not start within two minutes */
            private TimerTask m_infoRCPTask = null;
            
            @Override
            public void run() {
                try {
                    if (aut.getToolkit()
                            .equals(CommandConstants.RCP_TOOLKIT)) {
                        AutIdentifier autId = new AutIdentifier(
                                conf.getConfigMap().get(
                                        AutConfigConstants.AUT_ID));
                        m_infoRCPTask = new RCPAUTStartDelayNagTask(autId);
                        Timer timer = new Timer();
                        try {
                            timer.schedule(m_infoRCPTask, NAGGER_TIMEOUT);
                        } catch (IllegalStateException e) {
                            // do nothing if task has already been cancelled
                        }
                    }
                    
                    TestExecutionContributor.getInstance().startAUTaction(
                            aut, conf);
                } catch (ToolkitPluginException tpe) {
                    interrupt();
                    Utils.createMessageDialog(
                            MessageIDs.E_AUT_TOOLKIT_NOT_AVAILABLE);
                }
            }

            @Override
            public void interrupt() {
                disconnectFromServer();
                if (m_infoRCPTask != null) {
                    m_infoRCPTask.cancel();
                }
                super.interrupt();
            }

            @Override
            protected void errorOccured() {
                if (m_infoRCPTask != null) {
                    m_infoRCPTask.cancel();
                }
            }
        } .start();
    }

    /**
     * Stops the Running AUT with the given ID.
     * 
     * @param autId The ID of the Running AUT to stop.
     */
    public static void stopAUT(AutIdentifier autId) {
        TestExecutionContributor.getInstance().stopAUTaction(autId);
    }

    /**
     * starts the selected test suite. Testsuite must be
     * startable
     * @param ts ITestSuitePO
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     * @param autId The ID of the Running AUT on which the test will take place.
     */
    public static void startTestSuite(final ITestSuitePO ts,
        final AutIdentifier autId, final boolean autoScreenshot) {
        TestExecutionContributor.setClientMinimized(true);
        JBThread t = new JBThread("Initialize Test Execution") { //$NON-NLS-1$
            @Override
            public void run() {
                if (!AbstractStartTestHandler.prepareTestExecution()) {
                    stopTestSuite();
                }
                TestExecutionContributor.getInstance()
                        .startTestSuiteAction(ts, autId, autoScreenshot);
            }

            @Override
            protected void errorOccured() {
                // do nothing
            }
        };
        t.start();
        ts.setStarted(true);
    }

    /**
     * stops started TestSuite
     */
    public static void stopTestSuite() {
        JBThread t = new JBThread() {
            @Override
            public void run() {
                TestExecutionContributor.getInstance().
                    stopTestSuiteAction();
                List<ITestSuitePO> testSuites = GeneralStorage.getInstance()
                    .getProject().
                    getTestSuiteCont().getTestSuiteList();
                for (ITestSuitePO ts : testSuites) {
                    ts.setStarted(false);
                }
            }

            @Override
            protected void errorOccured() {
                // nothing

            }
        };
        t.start();
    }

    /**
     * @param server
     *            server to connect Opens a dialog to select a server/port
     *            combination and connect to selected server.
     */
    public static void connectToServer(final Server server) {
        final String jobName = NLS.bind(Messages.UIJobConnectToAUTAgent,
                new Object[]{server.getName(), 
                    String.valueOf(server.getPort())});
        Job connectToAUTAgent = new Job(jobName) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                connectToServerImpl(server);
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        JobUtils.executeJob(connectToAUTAgent, null);
    }


    /**
     * disconnects from server if connected
     */
    public static void disconnectFromServer() {
        JBThread t = new JBThread() {
            @Override
            public void run() {
                try {
                    if (AUTConnection.getInstance().isConnected()) {
                        ServerConnection.getInstance().request(
                                new StopAUTServerMessage(), new ICommand() {
                                    public Message execute() {
                                        return null;
                                    }

                                    public Message getMessage() {
                                        return null;
                                    }

                                    public void setMessage(Message message) {
                                    }

                                    public void timeout() {
                                    }
                                }, 2000);
                    }
                } catch (NotConnectedException e1) {
                    // no need to react, we are in the process of ending the AUT
                } catch (ConnectionException e1) {
                    // no need to react, we are in the process of ending the AUT
                } catch (CommunicationException e1) {
                    // no need to react, we are in the process of ending the AUT
                }
                TestExecutionContributor.getInstance()
                        .disconnectFromServeraction();
            }

            @Override
            protected void errorOccured() {
                // empty
            }
        };
        t.start();
    }


    /**
     * @param server
     *            server to connect
     */
    private static void connectToServerImpl(final Server server) {
        TestExecutionContributor.getInstance().connectToServeraction(
            server.getName(), server.getPort().toString());
        try {
            if (ServerConnection.getInstance().isConnected()) {
                ServerConnection.getInstance();
                ServerConnection connection = ServerConnection.getInstance();
                SendCompSystemI18nMessage message =
                    new SendCompSystemI18nMessage();
                message.setResourceBundles(CompSystemI18n.bundlesToString());
                try {
                    connection.send(message);
                } catch (NotConnectedException e) {
                    LOG.error("Could not send CompSystemI18nResourceBundle to Server", e);  //$NON-NLS-1$
                } catch (IllegalArgumentException e) {
                    LOG.error("Could not send CompSystemI18nResourceBundle to Server", e);  //$NON-NLS-1$
                } catch (CommunicationException e) {
                    LOG.error("Could not send CompSystemI18nResourceBundle to Server", e);  //$NON-NLS-1$
                }
                // FIXME : CHECK_GLOBAL_ACTION
            }
        } catch (ConnectionException e) {
            DataEventDispatcher.getInstance().fireServerConnectionChanged(
                    ServerState.Disconnected);
        }
        if (Plugin.getActiveView() != null) {
            Plugin.showStatusLine(Plugin.getActiveView());
        }
    }
}
