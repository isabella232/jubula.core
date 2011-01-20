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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.TestresultState;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.utils.DBSchemaPropertyCreator;
import org.eclipse.jubula.client.core.utils.ProgressEvent;
import org.eclipse.jubula.client.core.utils.ProgressEventDispatcher;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.utils.JBThread;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.i18n.I18n;
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
    private static final String HIBERNATE_CONNECTION_PASSWORD = 
        "javax.persistence.jdbc.password"; //$NON-NLS-1$

    /**
     * key for the username property for Hibernate
     */
    private static final String HIBERNATE_CONNECTION_USERNAME = 
        "javax.persistence.jdbc.user"; //$NON-NLS-1$
    
    /** true, if hibernate init was successful */
    private static boolean hibernateInit = false;
    
    /**
     * {@inheritDoc}
     * + check unsaved editors
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
            
            Map<String, Properties> schemeMap = 
                DBSchemaPropertyCreator.getSchemaMap();
            //if only one scheme with user and password is defined don't show login dialog 
            if (schemeMap.size() == 1) {
                connectWithoutLoginDialog(schemeMap);
            } else {
                ProgressEventDispatcher.notifyListener(new ProgressEvent(
                        ProgressEvent.LOGIN, null, null));
            }
            
            Hibernator.setSelectDBAction(false);
            
        }

        /**
         * don't open login dialog, if only one scheme is defined
         * with user and password
         * @param schemeMap Map of schemes
         */
        private void connectWithoutLoginDialog(
                final Map<String, Properties> schemeMap) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Collection<Properties> values = schemeMap.values();
                    Object[] keys = schemeMap.keySet().toArray();
                    Iterator<Properties> it = values.iterator();
                    if (it.hasNext()) {
                        Properties schemeProp = it.next();
                        String predefinedUsername = schemeProp.getProperty(
                                HIBERNATE_CONNECTION_USERNAME);
                        String predefinedPassword = schemeProp.getProperty(
                                HIBERNATE_CONNECTION_PASSWORD);
                        String schemName = keys[0].toString();
                        if (predefinedUsername != null
                            && predefinedPassword != null) {                    
                            Hibernator.setUser(predefinedUsername);
                            Hibernator.setPw(predefinedPassword);
                            Hibernator.setSchemaName(schemName);
                            
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
                                Plugin.getDefault().writeLineToConsole(
                                        I18n.getString(
                                        "SelectDatabaseAction.Info.ConnectSuccessful"), //$NON-NLS-1$
                                            true);
                                Plugin.stopLongRunning();
                            } else {
                                Utils.clearClientUI();
                                Plugin.getDefault().writeLineToConsole(
                                        I18n.getString(
                                        "SelectDatabaseAction.Info.ConnectFailed"), //$NON-NLS-1$
                                            true);
                                Plugin.stopLongRunning();
                            }
                        } else {
                            ProgressEventDispatcher.notifyListener(
                                    new ProgressEvent(ProgressEvent.LOGIN, null,
                                            null));
                        }
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
                //default constructor
            }

            /**
             * {@inheritDoc}
             */
            public void run(IProgressMonitor monitor) {

                monitor.beginTask(I18n.getString("Plugin.connectProgress"), //$NON-NLS-1$
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