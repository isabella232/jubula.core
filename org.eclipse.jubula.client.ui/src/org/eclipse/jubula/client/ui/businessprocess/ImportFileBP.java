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
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jubula.client.archive.businessprocess.FileStorageBP;
import org.eclipse.jubula.client.archive.errorhandling.IProjectNameConflictResolver;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.progress.AbstractRunnableWithProgress;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.actions.OpenProjectAction;
import org.eclipse.jubula.client.ui.actions.OpenProjectAction.OpenProjectOperation;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.ComboBoxDialog;
import org.eclipse.jubula.client.ui.dialogs.ImportProjectDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.GDThread;
import org.eclipse.jubula.tools.exception.GDProjectDeletedException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
@SuppressWarnings("synthetic-access")
public class ImportFileBP implements IProjectNameConflictResolver {

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
         * return 0 if whole project should be imported otherwise, sth greater 0
         * @return int
         */
        public int getImportTarget();
        
        /**
         * @return the selection state of the open project checkbox
         */
        public boolean getIsOpenProject();

        /**
         * @return what elements should be imported
         */
        public int getSelectedElements();
    }
    
    /** the logger */
    private static Log log = LogFactory.getLog(ImportFileBP.class);
    
    /** single instance of ImExportCvsBP */
    private static ImportFileBP instance = null;

    /** Constructor */
    public ImportFileBP() {
        FileStorageBP.setProjectNameConflictResolver(this);
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
     * 
     *
     */
    public void importFile() {
        Plugin.startLongRunning();
        GDThread t = new GDThread() {
            public void run() {
                if (!Hibernator.init()) {
                    Plugin.stopLongRunning();
                    return;
                }
                Plugin.getDisplay().syncExec(new Runnable() {
                    public void run() {
                        if (GeneralStorage.getInstance().getProject() != null
                            && Plugin.getDefault().anyDirtyStar()) {

                            if (Plugin.getDefault().
                                showSaveEditorDialog()) {

                                showImportDialog();
                            }
                            Plugin.stopLongRunning();
                            return;
                        }
                        showImportDialog();
                    }
                });
            }
            /**
             * {@inheritDoc}
             */
            protected void errorOccured() {
                Plugin.stopLongRunning();
            }
        };
        t.start();
        Plugin.stopLongRunning();
    }
    
    /**
     * 
     */
    public void importUnboundModules() {
        Plugin.startLongRunning();
        GDThread t = new GDThread() {
            public void run() {
                Plugin.getDisplay().syncExec(new ImportUnboundThread());
            }
            /**
             * {@inheritDoc}
             */
            protected void errorOccured() {
                Plugin.stopLongRunning();
            }
        };
        t.start();
        while (t.isAlive()) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
        Plugin.stopLongRunning();
        
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
    private void importProject(final int elements, final String [] fileNames, 
            final boolean openProject) {
        Plugin.startLongRunning(I18n.getString("ImportFileBP.waitWhileImporting")); //$NON-NLS-1$
        try {
            if (fileNames == null) {
                return;
            }
            AbstractRunnableWithProgress<IProjectPO> importProjectRunnable =
                new AbstractRunnableWithProgress<IProjectPO>() {

                    public void run(IProgressMonitor monitor)
                        throws InterruptedException {

                        try {
                            setResult(FileStorageBP.importProject(
                                elements, fileNames, 
                                monitor, Plugin.getDefault(), openProject));
                        } catch (PMException pme) {
                            PMExceptionHandler
                                .handlePMExceptionForMasterSession(pme);
                        } catch (GDProjectDeletedException gdpde) {
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
                    new OpenProjectAction.OpenProjectOperation(
                            projectToOpen);
                try {
                    PlatformUI.getWorkbench().getProgressService()
                        .busyCursorWhile(openOp);

                    Plugin.getDisplay().syncExec(new Runnable() {
                        public void run() {
                            Plugin.setProjectNameInTitlebar(
                                    projectToOpen.getName(),
                                    projectToOpen.getMajorProjectVersion(),
                                    projectToOpen.getMinorProjectVersion());
                        }
                    });
                } catch (InvocationTargetException ite) {
                    // Exception occurred during operation
                    log.error("Error occurred during import.", ite.getCause()); //$NON-NLS-1$
                    openOp.handleOperationException();
                } catch (InterruptedException e) {
                    // Operation was canceled.
                    openOp.handleOperationException();
                }
            }
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error("An error occurred during import.", ite.getCause()); //$NON-NLS-1$
        } catch (InterruptedException e) {
            // Operation was canceled.
            showCancelImport(Plugin.getDefault());
        } finally {
            Plugin.stopLongRunning();
        }

    }

    /**
     * brings up the ImportDiaog
     * 
     */
    void showImportDialog() {
        ImportProjectDialog importProjectWizard = new ImportProjectDialog();
        WizardDialog dialog = 
            new WizardDialog(Plugin.getShell(), importProjectWizard) {
                protected void createButtonsForButtonBar(Composite parent) {
                    super.createButtonsForButtonBar(parent);
                    Button finishButton = getButton(IDialogConstants.FINISH_ID);
                    finishButton.setText(IDialogConstants.OK_LABEL);
                }
            };
        importProjectWizard.setWindowTitle(I18n.getString("ImportProjectDialog.title")); //$NON-NLS-1$
        dialog.setHelpAvailable(true);
        
        int val = dialog.open();
        if (val == Window.OK) {
            importProjects(importProjectWizard.getImportInfoProvider());
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
        int importTarget = importInfo.getImportTarget();
        try {
            if (importTarget == FileStorageBP.IMPORT_ALL) {
                FileStorageBP.importProject(0, fileNames, monitor, 
                        Plugin.getDefault(), openProject);
            } else if (importTarget == FileStorageBP.IMPORT_TESTCASES) {
                FileStorageBP.importProject(
                    importInfo.getSelectedElements(), 
                    fileNames, monitor, Plugin.getDefault(), openProject);
            }
        } catch (PMException pme) {
            PMExceptionHandler
                .handlePMExceptionForMasterSession(pme);
        } catch (GDProjectDeletedException gdpde) {
            PMExceptionHandler
                .handleGDProjectDeletedException();
        }
    }
    
    /**
     * Performs an import using the information provided by the argument.
     * 
     * @param importInfo Provides information relevant to the import.
     */
    public void importProjects(IProjectImportInfoProvider importInfo) {
        String [] fileNames = importInfo.getFiles();
        boolean openProject = importInfo.getIsOpenProject();
        int importTarget = importInfo.getImportTarget();
        if (importTarget == FileStorageBP.IMPORT_ALL) {
            importProject(0, fileNames, openProject);
        } else if (importTarget == FileStorageBP.IMPORT_TESTCASES) {
            importProject(
                importInfo.getSelectedElements(), fileNames,
                openProject);
        }
    }

    /**
     * 
     * Import the unbound modules library into a database
     * 
     * @author BREDEX GmbH
     * @created 05.06.2008
     */
    private final class ImportUnboundThread implements Runnable {
        
        /**
         * {@inheritDoc}
         */
        public void run() {
            importProject(0, findUnboundModules(), false); // load all elements
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
                I18n.getString("ImportFileComboAction.ProjMessage"), //$NON-NLS-1$
                I18n.getString("ImportFileAction.ProjTitle"), //$NON-NLS-1$
                Plugin.getImage(IconConstants.IMPORT_PROJECT_STRING), 
                I18n.getString("ImportFileAction.ProjShell"), //$NON-NLS-1$
                I18n.getString("ImportFileAction.ProjLabel")); //$NON-NLS-1$
            
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
            I18n.getString(
                "ImportFileAction.Error.ImportFailed",  //$NON-NLS-1$
                new Object [] {"Import operation cancelled by user"})); //$NON-NLS-1$
    }

}