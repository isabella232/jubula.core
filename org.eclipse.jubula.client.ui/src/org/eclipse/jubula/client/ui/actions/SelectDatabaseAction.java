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
package org.eclipse.jubula.client.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.TestresultState;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnection;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnectionConverter;
import org.eclipse.jubula.client.core.utils.ProgressEvent;
import org.eclipse.jubula.client.core.utils.ProgressEventDispatcher;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.JBThread;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

/**
 * @author BREDEX GmbH
 * @created 18.04.2005
 */
public class SelectDatabaseAction extends AbstractAction {
    /**
     * <code>HIBERNATE_CONNECTION_PASSWORD</code>
     */
    private static final String HIBERNATE_CONNECTION_PASSWORD = "javax.persistence.jdbc.password"; //$NON-NLS-1$

    /**
     * key for the username property for Hibernate
     */
    private static final String HIBERNATE_CONNECTION_USERNAME = "javax.persistence.jdbc.user"; //$NON-NLS-1$

    /** true, if hibernate init was successful */
    private static boolean hibernateInit = false;

    /**
     * {@inheritDoc} + check unsaved editors
     */
    public void runWithEvent(IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        if (GeneralStorage.getInstance().getProject() != null
                && Plugin.getDefault().anyDirtyStar()
                && !Plugin.getDefault().showSaveEditorDialog()) {

            Plugin.stopLongRunning();
            return;
        }

        new Loader().start();
    }

    /**
     * Runnable to connect to DB
     * 
     * @author BREDEX GmbH
     * 
     */
    private static class Loader extends JBThread {

        /**
         * run method
         */
        public void run() {
            Plugin.startLongRunning();

            Hibernator.setSelectDBAction(true);

            List<DatabaseConnection> availableConnections = 
                DatabaseConnectionConverter.computeAvailableConnections();
            
            // if only one scheme with user and password is defined don't show
            // login dialog
            if (availableConnections.size() == 1) {
                connectWithoutLoginDialog(
                        availableConnections.get(0).getConnectionInfo());
            } else {
                ProgressEventDispatcher.notifyListener(new ProgressEvent(
                        ProgressEvent.LOGIN, null, null));
            }

            Hibernator.setSelectDBAction(false);

        }

        /**
         * Initializes the Hibernator using the given connection information.
         * If the information contains a username <em>and</em> password, then 
         * these are used for initialization. If not, then a login dialog is 
         * presented.
         * 
         * @param connectionInfo The information to use to initialize the 
         *                       Hibernator.
         */
        private void connectWithoutLoginDialog(
                final DatabaseConnectionInfo connectionInfo) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    String predefinedUsername = 
                        connectionInfo.getProperty(
                                HIBERNATE_CONNECTION_USERNAME);
                    String predefinedPassword = 
                        connectionInfo.getProperty(
                                HIBERNATE_CONNECTION_PASSWORD);
                    if (predefinedUsername != null
                            && predefinedPassword != null) {
                        Hibernator.setUser(predefinedUsername);
                        Hibernator.setPw(predefinedPassword);
                        Hibernator.setDbConnectionName(connectionInfo);

                        if (Hibernator.instance() != null) {
                            CompNamePM.dispose();
                            GeneralStorage.getInstance().dispose();
                            if (LockManager.isRunning()) {
                                LockManager.instance().dispose();
                            }
                            Hibernator.instance().dispose();
                            DataEventDispatcher.getInstance()
                                    .fireTestresultChanged(
                                            TestresultState.Clear);
                        }

                        ConnectDbOperation connDbOp =
                            new ConnectDbOperation();
                        try {
                            PlatformUI.getWorkbench().getProgressService()
                                    .run(true, false, connDbOp);
                        } catch (InvocationTargetException e) {
                            hibernateInit = false;
                        } catch (InterruptedException e) {
                            hibernateInit = false;
                        }

                        if (hibernateInit) {
                            Utils.clearClient();
                            LockManager.instance();
                            Plugin.getDefault().writeLineToConsole(Messages.
                                SelectDatabaseActionInfoConnectSuccessful,
                                true);
                        } else {
                            Utils.clearClient(true);
                            Plugin.getDefault().writeLineToConsole(
                                            Messages.
                                    SelectDatabaseActionInfoConnectFailed,
                                            true);
                        }
                        Plugin.stopLongRunning();
                    } else {
                        ProgressEventDispatcher
                                .notifyListener(new ProgressEvent(
                                        ProgressEvent.LOGIN, null, null));
                    }
                }
            });
        }

        /**
         * 
         * 
         * @author BREDEX GmbH
         * @created Jan 2, 2008
         */
        public class ConnectDbOperation implements IRunnableWithProgress {

            /**
             * constructor
             */
            public ConnectDbOperation() {
                // default constructor
            }

            /**
             * {@inheritDoc}
             */
            public void run(IProgressMonitor monitor) {

                monitor.beginTask(Messages.PluginConnectProgress,
                        IProgressMonitor.UNKNOWN);

                try {
                    if (Hibernator.init()) {
                        hibernateInit = true;
                    } else {
                        hibernateInit = false;
                    }
                } catch (JBFatalException e) {
                    hibernateInit = false;
                    ProgressEventDispatcher.notifyListener(new ProgressEvent(
                            ProgressEvent.SHOW_MESSAGE,
                            MessageIDs.E_ERROR_IN_SCHEMA_CONFIG, null));
                } finally {
                    monitor.done();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        protected void errorOccured() {
            Plugin.stopLongRunning();
        }
    }
}