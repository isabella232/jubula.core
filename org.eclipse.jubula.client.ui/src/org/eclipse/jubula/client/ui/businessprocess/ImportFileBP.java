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
package org.eclipse.jubula.client.ui.businessprocess;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.archive.businessprocess.FileStorageBP;
import org.eclipse.jubula.client.archive.errorhandling.IProjectNameConflictResolver;
import org.eclipse.jubula.client.archive.i18n.Messages;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.progress.AbstractRunnableWithProgress;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.core.utils.DatabaseStateDispatcher;
import org.eclipse.jubula.client.core.utils.DatabaseStateEvent;
import org.eclipse.jubula.client.core.utils.IDatabaseStateListener;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.ComboBoxDialog;
import org.eclipse.jubula.client.ui.handlers.project.OpenProjectHandler;
import org.eclipse.jubula.client.ui.handlers.project.OpenProjectHandler.OpenProjectOperation;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
@SuppressWarnings("synthetic-access")
public class ImportFileBP implements IProjectNameConflictResolver,
        IDatabaseStateListener {
    /**
     * Interface for classes wishing to provide information for an import 
     * operation (i.e. which projects to import and which parts to import).
     *
     * @author BREDEX GmbH
     * @created May 20, 2010
     */
    public static interface IProjectImportInfoProvider {
        
        /**
         * @return the file names of the projects to import
         */
        public String [] getFiles();
        
        /**
         * @return the selection state of the open project checkbox
         */
        public boolean getIsOpenProject();

    }
    
    /** the logger */
    private static Log log = LogFactory.getLog(ImportFileBP.class);
    
    /** single instance of ImExportCvsBP */
    private static ImportFileBP instance = null;

    /** Constructor */
    public ImportFileBP() {
        FileStorageBP.setProjectNameConflictResolver(this);
        DatabaseStateDispatcher.addDatabaseStateListener(this);
    }

    /**
     * get single instance
     * 
     * @return single instance of ImportFileBP
     */
    public static ImportFileBP getInstance() {
        if (instance == null) {
            instance = new ImportFileBP();
        }
        return instance;
    }     

    /**
     * Imports a choosed project from a file.
     * 
     * @param elements
     *            What to import ? 0 = all >0 = elements
     * @param fileNames
     *            The names of the files to import.
     * @param openProject
     *            Flag indicating whether the imported project should be 
     *            immediately opened after import.
     */
    public void importProject(final int elements, final String [] fileNames, 
            final boolean openProject) {
        try {
            if (fileNames == null) {
                return;
            }
            AbstractRunnableWithProgress<IProjectPO> importProjectRunnable =
                new AbstractRunnableWithProgress<IProjectPO>() {

                    public void run(IProgressMonitor monitor)
                        throws InterruptedException {
                        monitor.setTaskName(Messages.
                                ImportFileBPWaitWhileImporting);
                        try {
                            setResult(FileStorageBP.importProject(
                                elements, fileNames, 
                                monitor, Plugin.getDefault(), openProject));
                        } catch (PMException pme) {
                            PMExceptionHandler
                                .handlePMExceptionForMasterSession(pme);
                        } catch (ProjectDeletedException gdpde) {
                            PMExceptionHandler
                                .handleGDProjectDeletedException();
                        }
                    }
                
                };
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
                    importProjectRunnable);
            final IProjectPO projectToOpen = importProjectRunnable.getResult();
            if (projectToOpen != null) { 
                OpenProjectOperation openOp = 
                    new OpenProjectHandler.OpenProjectOperation(
                            projectToOpen);
                try {
                    PlatformUI.getWorkbench().getProgressService()
                        .busyCursorWhile(openOp);
                } catch (InvocationTargetException ite) {
                    // Exception occurred during operation
                    log.error(org.eclipse.jubula.client.ui.i18n.Messages.
                            ErrorOccurredDuringImport, ite.getCause());
                    openOp.handleOperationException();
                } catch (InterruptedException e) {
                    // Operation was canceled.
                    openOp.handleOperationException();
                }
            }
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(org.eclipse.jubula.client.ui.i18n.Messages.
                    ErrorOccurredDuringImport, ite.getCause());
        } catch (InterruptedException e) {
            // Operation was canceled.
            showCancelImport(Plugin.getDefault());
        } finally {
            Plugin.stopLongRunning();
        }
    }

    /**
     * Performs an import using the information provided by the argument.
     * 
     * @param importInfo Provides information relevant to the import.
     * @param monitor The progress monitor for the operation.
     * 
     * @throws InterruptedException if the operation was canceled or the thread
     *                              was interrupted.
     */
    public void importProjects(IProjectImportInfoProvider importInfo, 
            IProgressMonitor monitor) throws InterruptedException {
        String [] fileNames = importInfo.getFiles();
        boolean openProject = importInfo.getIsOpenProject();

        try {
            FileStorageBP.importProject(0, fileNames, monitor, 
                    Plugin.getDefault(), openProject);
        } catch (PMException pme) {
            PMExceptionHandler
                .handlePMExceptionForMasterSession(pme);
        } catch (ProjectDeletedException gdpde) {
            PMExceptionHandler
                .handleGDProjectDeletedException();
        }
    }
    
    /**
     * 
     * Import the unbound modules library into a database
     * 
     * @author BREDEX GmbH
     * @created 05.06.2008
     */
    private final class ImportUnboundModulesJob extends Job {
        /**
         * @param name
         *            the name of the job
         */
        public ImportUnboundModulesJob(String name) {
            super(name);
        }

        /**
         * {@inheritDoc}
         */
        protected IStatus run(IProgressMonitor monitor) {
            Plugin.getDisplay().syncExec(new Runnable() {
                public void run() {
                    // load all elements;
                    importProject(0, findUnboundModules(), false);
                }
            });
            return Status.OK_STATUS;
        }

        /**
         * look for library files
         * @return an array of filenames or null
         */
        private String[] findUnboundModules() {
            File ubmDir = findInstallationDir();
            if (ubmDir != null) {
                File[] files = ubmDir
                        .listFiles(new FilenameFilter() {
                            public boolean accept(File dir,
                                    String name) {
                                return name
                                        .startsWith("unbound_modules_") //$NON-NLS-1$
                                        && name.endsWith(".xml"); //$NON-NLS-1$
                            }
                        });
                String[] filenames = new String[files.length];
                int index = 0;
                for (File file : files) {
                    if (file.isFile() && file.canRead()) {
                        filenames[index++] = file.getAbsolutePath();
                    }
                }
                return filenames;
            }
            return null; // no directory
        }

        /**
         * find the installation directory. if running in the IDE
         * also check the "current" installation directory.
         * @return a valid directory or null if none could be found
         */
        private File findInstallationDir() {
            File ubmDir = null;
            try {
                Location instLoc = Platform.getInstallLocation();
                if (instLoc != null) {
                    File instDir = new File(instLoc.getURL()
                            .toURI());
                    ubmDir = new File(instDir,
                            "../examples/testCaseLibrary"); //$NON-NLS-1$
                    if (!ubmDir.isDirectory()) {
                        ubmDir = null;
                    }
                }
            } catch (URISyntaxException e) {
                ubmDir =  null; // installation directory not
                                // available
            }
            // fallback for current build
            if (ubmDir == null) {
                ubmDir = new File("../examples/testCaseLibrary"); //$NON-NLS-1$
                if (!ubmDir.isDirectory()) {
                    ubmDir = null;
                }                            
            }
            // try to find something for the IDE
            if (ubmDir == null) {
                Location wsLoc = Platform.getInstanceLocation();
                if (wsLoc != null) {
                    try {
                        File wsDir = 
                            new File(wsLoc.getURL().toURI());
                        ubmDir = new File(wsDir,
                                "../jubula/org.eclipse.jubula.examples/" //$NON-NLS-1$ 
                                + "resources/projects/library"); //$NON-NLS-1$
                        if (!ubmDir.isDirectory()) {
                            ubmDir = null;
                        }
                    } catch (URISyntaxException e) {
                        ubmDir = null;
                    }
                }
            }                        
            return ubmDir;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String resolveNameConflict(List<String> availableNames) {
        ComboBoxDialog dialog = new ComboBoxDialog(
                Plugin.getShell(), 
                new ArrayList<String>(availableNames), 
                org.eclipse.jubula.client.ui.i18n.Messages.
                ImportFileComboActionProjMessage,
                org.eclipse.jubula.client.ui.i18n.Messages.
                ImportFileActionProjTitle,
                Plugin.getImage(IconConstants.IMPORT_PROJECT_STRING), 
                org.eclipse.jubula.client.ui.i18n.Messages.
                ImportFileActionProjShell,
                org.eclipse.jubula.client.ui.i18n.Messages.
                ImportFileActionProjLabel);
            
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.DIALOG_PROJECT_IMPORT_RENAME);
        dialog.open();
        return (dialog.getReturnCode() == Window.OK) ? dialog.getSelection()
                : null;
    }

    /**
     * @param string @see Plugin#writeLineToConsole(String, boolean)
     * @param b @see Plugin#writeLineToConsole(String, boolean)
     */
    protected void writeLineToConsole(String string, boolean b) {
        Plugin.getDefault().writeLineToConsole(string, b);
    }

    /**
     * @param string @see Plugin#writeErrorLineToConsole(String, boolean)
     * @param b @see Plugin#writeErrorLineToConsole(String, boolean)
     */
    protected void writeErrorLineToConsole(String string, boolean b) {
        Plugin.getDefault().writeErrorLineToConsole(string, b);
    }

    /**
     * Report to the user that the import operation was cancelled.
     * 
     * @param console
     *              The console to use to display pogress and 
     *              error messages.
     */
    private void showCancelImport(IProgressConsole console) {
        console.writeErrorLine(
            NLS.bind(org.eclipse.jubula.client.ui.i18n.Messages.
                    ImportFileActionErrorImportFailed,
                new Object [] {org.eclipse.jubula.client.ui.i18n.Messages.
                        ImportOperationCancelledByUser}));
    }

    /**
     * {@inheritDoc}
     */
    public void reactOnDatabaseEvent(DatabaseStateEvent e) {
        switch (e.getState()) {
            case DB_SCHEME_CREATED:
                Job importUnboundModules = new ImportUnboundModulesJob(
                        Messages.ImportUnboundModulesJob);
                importUnboundModules.setUser(true);
                importUnboundModules.schedule();
                break;
            default:
                break;
        }
    }
}