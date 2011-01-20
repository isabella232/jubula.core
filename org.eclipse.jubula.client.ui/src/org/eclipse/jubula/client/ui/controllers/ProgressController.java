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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IAutStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.TestresultState;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.utils.IProgressListener;
import org.eclipse.jubula.client.core.utils.ProgressEvent;
import org.eclipse.jubula.client.core.utils.ProgressEventDispatcher;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.ImportFileBP;
import org.eclipse.jubula.client.ui.dialogs.DBLoginDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.utils.TimeUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


/**
 * @author BREDEX GmbH
 * @created 14.02.2007
 */
public class ProgressController implements IProgressListener, 
    IAutStateListener, IProjectLoadedListener {

    /** for log messages */
    private static Log log = LogFactory.getLog(ProgressController.class);
    
    /** true, if hibernate init was successful */
    private static boolean hibernateInit = false;
    
    /** progress operation */
    private ProgressOperation m_prog;
    
    /**
     * Constructor
     */
    public ProgressController() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addAutStateListener(this, true);
        ded.addProjectLoadedListener(this, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void reactOnProgressEvent(final ProgressEvent e) {
        Integer eventId = e.getId();
        if (eventId == ProgressEvent.LOGIN) {
            showLoginDataDialog();
        } else if (eventId == ProgressEvent.DB_SCHEME_CREATE) {
            importUnboundModules();
        } else if (eventId == ProgressEvent.OPEN_PROGRESS_BAR) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    m_prog = new ProgressOperation(
                            e.getProgressText());
                    try {
                        IProgressService progressService = 
                            PlatformUI.getWorkbench().getProgressService();
                        progressService.run(true, false, m_prog);
                    } catch (InvocationTargetException ex) {
                        log.error(ex);
                        Utils.createMessageDialog(MessageIDs.E_SYNCHRONIZATION);
                    } catch (InterruptedException ex) {
                        log.error(ex);
                        Utils.createMessageDialog(MessageIDs.E_SYNCHRONIZATION);
                    }
                }
            });
        } else if (eventId == ProgressEvent.CLOSE_PROGRESS_BAR) {
            stopProgressOperation();

            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IProjectPO project = 
                        GeneralStorage.getInstance().getProject();
                    String projectName = null;
                    Integer majorVersion = null;
                    Integer minorVersion = null;
                    if (project != null) {
                        projectName = project.getName();
                        majorVersion = project.getMajorProjectVersion();
                        minorVersion = project.getMinorProjectVersion();
                    }
                    Plugin.setProjectNameInTitlebar(projectName,
                            majorVersion, minorVersion);
                }
            });

        } else {
            final Integer code;
            stopProgressOperation();
            if (e.getMsgId() == null) {
                code = MessageIDs.E_UNEXPECTED_EXCEPTION;
            } else {
                code = e.getMsgId();
            }
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Utils.createMessageDialog(code);
                }
            });
        }             
    }

    /**
     * 
     */
    private void importUnboundModules() {
        ImportFileBP.getInstance().importUnboundModules();        
    }

    /**
     * Stops the ProgressOperation (m_prog).
     */
    private void stopProgressOperation() {
        if (m_prog != null) {
            m_prog.stop();
        }        
    }   

    /**
     * Shows the db login dialog.
     */
    private void showLoginDataDialog() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                DBLoginDialog dialog = new DBLoginDialog(Plugin.getShell());
                dialog.create();
                DialogUtils.setWidgetNameForModalDialog(dialog);
                dialog.open();
                if (dialog.getReturnCode() == Window.OK) {
                    //if db is embedded do not set user and pwd here
                    if (!(dialog.isEmbeddedDb())) {
                        Hibernator.setUser(dialog.getUser());
                        Hibernator.setPw(dialog.getPwd());
                    }
                    Hibernator.setSchemaName(dialog.getSchemaName());
                    
                    if (Hibernator.getSelectDBAction()) {
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
                        
                        ConnectDbOperation connDbOp = new ConnectDbOperation();
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
                                    "SelectDatabaseAction.Info.ConnectSuccessful"), true); //$NON-NLS-1$
                            Plugin.stopLongRunning();
                        } else {
                            Utils.clearClientUI();
                            Plugin.getDefault().writeLineToConsole(
                                    I18n.getString(
                                    "SelectDatabaseAction.Info.ConnectFailed"), true); //$NON-NLS-1$
                            Plugin.stopLongRunning();
                        }
                    }
                    
                    
                } else {
                    Hibernator.setUser(null);
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
     * This class represents a long running operation.
     * @author BREDEX GmbH
     * @created 26.08.2005
     */
    private class ProgressOperation implements IRunnableWithProgress {
        /** the m_text, that is shown in the progress dialog */
        private String m_text;
        
        /**
         * <code>m_monitor</code> the monitor used in this progress operation
         */
        private IProgressMonitor m_monitor = null;
        
        /**
         * ProgressOperation constructor.
         * @param text The text to set.
         */
        public ProgressOperation(String text) {
            m_text = text;
        }
        
        /**
         * {@inheritDoc}
         */
        public void run(final IProgressMonitor monitor) {
            m_monitor = monitor;
            monitor.beginTask(m_text, IProgressMonitor.UNKNOWN);
            while (!monitor.isCanceled()) {
                TimeUtil.delay(50);
            }
        }

        /**
         */
        public synchronized void stop() {
            if (m_monitor != null) {
                m_monitor.setCanceled(true);
                m_monitor.done();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void handleAutStateChanged(AutState state) {
        reactOnProgressEvent(new ProgressEvent(ProgressEvent.CLOSE_PROGRESS_BAR,
            null, null));
    }
    
    /**
     * 
     * @return true, if hibernate init was successful
     */
    public static boolean initHibernate() {
        return hibernateInit;
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        reactOnProgressEvent(new ProgressEvent(ProgressEvent.CLOSE_PROGRESS_BAR,
                null, null));
    }
}