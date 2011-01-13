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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.ExportAllBP;
import org.eclipse.jubula.client.ui.utils.GDThread;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created Jul 25, 2007
 */
public class ExportAllAction extends AbstractAction {
    
    /** the logger */
    private static Log log = LogFactory.getLog(ExportAllAction.class);

    /**
     * 
     * 
     * @author BREDEX GmbH
     * @created Jul 25, 2007
     */
    private static class ExportOperation implements IRunnableWithProgress {
        
        /** name of the directory for exporting */
        private String m_dirName;

        /** dialog that was used to select export directory */
        private DirectoryDialog m_dirDialog;
        
        /**
         * Constructor
         * 
         * @param dirName The name of the directory to which projects will
         *                be exported.
         * @param dirDialog The dialog that was used to select the directory
         */
        public ExportOperation(String dirName, DirectoryDialog dirDialog) {
            m_dirName = dirName;
            m_dirDialog = dirDialog;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {

            if (m_dirName != null) {
                // Export all projects, logging to console as we do so
                ExportAllBP.getInstance().showStartingExport();
                Hibernator hib = Hibernator.instance();
                EntityManager exportSession = null;
                boolean nodeCacheIsUsed = NodePM.getInstance().isUseCache();
                if (!nodeCacheIsUsed) {
                    NodePM.getInstance().setUseCache(true);
                }
                try {
                    exportSession = hib.openSession();
                    List<IProjectPO> allProjects = 
                        ProjectPM.findAllProjects(exportSession);
                    
                    // export all projects from the project list
                    ExportAllBP.getInstance().exportProjectList(
                            allProjects, m_dirName,
                            exportSession, monitor, false, null);

                    Utils.storeLastDirPath(m_dirDialog.getFilterPath());
                    ExportAllBP.getInstance().showFinishedExport();
                } catch (GDException gde) {
                    log.error("Export aborted.", gde); //$NON-NLS-1$
                    ExportAllBP.getInstance().showAbortExport(gde);
                } catch (InterruptedException ie) {
                    ExportAllBP.getInstance().showCancelExport();
                } finally {
                    if (!nodeCacheIsUsed) {
                        NodePM.getInstance().setUseCache(false);
                    }
                    hib.dropSession(exportSession);
                    Plugin.stopLongRunning();
                    monitor.done();
                }
            }
        }

        /**
         * 
         * {@inheritDoc}
         */
        protected void errorOccured() {
            Plugin.stopLongRunning();
        }

    }
    
    /**
     * {@inheritDoc}
     *      org.eclipse.swt.widgets.Event)
     */
    public void runWithEvent(IAction action, Event event) { 
        if (action != null && !action.isEnabled()) {
            return;
        }
        
        GDThread showDialog = new GDThread() {

            public void run() {
                Plugin.startLongRunning();
                if (Hibernator.init()) {
                    Plugin.getDisplay().syncExec(new Runnable() {
                        public void run() {
                            if (GeneralStorage.getInstance().getProject() 
                                    != null
                                && Plugin.getDefault().anyDirtyStar()) {
                               
                                if (Plugin.getDefault().
                                    showSaveEditorDialog()) {

                                    showExportDialog();
                                }
                                Plugin.stopLongRunning();
                                return;
                            }
                            showExportDialog(); 
                            Plugin.stopLongRunning();
                        }
                    });
                } else {
                    Plugin.stopLongRunning();
                }
            }

            protected void errorOccured() {
                Plugin.stopLongRunning();
            }
            
        };
        
        showDialog.start();
    }

    /**
     * 
     */
    private void showExportDialog() {
        final DirectoryDialog dirDialog = 
            new DirectoryDialog(Plugin.getShell(), SWT.APPLICATION_MODAL);
        dirDialog.setText(I18n.getString("ActionBuilder.ExportAll")); //$NON-NLS-1$
        dirDialog.setFilterPath(Utils.getLastDirPath());
        boolean done = false;
        String tempDirName = null;
        while (!done) {
            tempDirName = dirDialog.open();
            Plugin.startLongRunning(I18n.getString("ExportFileAction.waitWhileExporting")); //$NON-NLS-1$
            if (tempDirName == null) {
                done = true;
                Plugin.stopLongRunning();
            } else {
                File dir = new File(tempDirName);
                if (dir.list().length > 0) {
                    MessageBox mb = new MessageBox(dirDialog.getParent(), 
                        SWT.ICON_WARNING | SWT.OK);
                    mb.setMessage(I18n.getString("ExportAllAction.DirectoryNotEmpty", new Object[] { //$NON-NLS-1$
                        tempDirName}));
                    mb.open();
                } else {
                    done = true;
                }
            }
            dirDialog.setFilterPath(tempDirName);
        }
        
        
        if (tempDirName != null 
                && tempDirName.charAt(tempDirName.length() - 1) != '/') {
            
            tempDirName += "/"; //$NON-NLS-1$
        }
        final String dirName = tempDirName;
        
        if (dirName != null) {
            ExportOperation op = new ExportOperation(dirName, dirDialog);
            try {
                PlatformUI.getWorkbench().getProgressService()
                    .busyCursorWhile(op);
            } catch (InvocationTargetException ite) {
                // Exception occurred during operation
                log.error(ite.getCause());
            } catch (InterruptedException ie) {
                // Operation was canceled. This is already handled by the 
                // operation. Do nothing.
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return ExportAllBP.getInstance();
    }
}
