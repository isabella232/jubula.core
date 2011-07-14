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
package org.eclipse.jubula.app.dbtool.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.app.dbtool.i18n.Messages;
import org.eclipse.jubula.client.archive.businessprocess.FileStorageBP;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;
import org.eclipse.jubula.client.cmd.JobConfiguration;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.persistence.TestResultPM;
import org.eclipse.jubula.client.core.persistence.TestResultSummaryPM;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.utils.TimeUtil;
import org.eclipse.osgi.util.NLS;


/**
 * @author BREDEX GmbH
 * @created Mar 12, 2009
 */
public class DBToolClient extends AbstractCmdlineClient {
    /** delete parameter */
    private static final String OPTION_DELETE = "delete"; //$NON-NLS-1$
    /** delete all projects parameter */
    private static final String OPTION_DELETE_ALL = "deleteall"; //$NON-NLS-1$
    /** keep testrun summary */
    private static final String OPTION_KEEPSUMMARY_ON_DELETE = "keepsummary"; //$NON-NLS-1$
    /** project name to delete */
    private static final String PAR_PROJECT = "project-name project-version"; //$NON-NLS-1$
    /** export all parameter */
    private static final String OPTION_EXPORT_ALL = "exportall"; //$NON-NLS-1$
    /** export parameter */
    private static final String OPTION_EXPORT = "export"; //$NON-NLS-1$
    /** Im-/export directory parameter */
    private static final String OPTION_DIR = "directory"; //$NON-NLS-1$
    /** directory parameter */
    private static final String PAR_DIR = "directory path"; //$NON-NLS-1$
    /** import parameter */ 
    private static final String OPTION_IMPORT = "import"; //$NON-NLS-1$
    /** import value */
    private static final String PAR_IMPORT = "import file"; //$NON-NLS-1$

    /** singleton instance */
    private static DBToolClient instance;

    /**
     * hidden constructor
     */
    private DBToolClient() {
        // hide constructor
    }

    /** singleton
     * @return the single instance of this class  
     */
    public static DBToolClient getInstance() {
        if (null == instance) {
            instance = new DBToolClient();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    protected void extendOptions(Options opt, boolean req) {
        
        // delete project
        final Option delOption = createOption(OPTION_DELETE, true, 
                PAR_PROJECT, 
                Messages.DBToolDelete, false);
        delOption.setArgs(2);
        opt.addOption(delOption);
        
        // delete all
        opt.addOption(createOption(OPTION_DELETE_ALL, false, null,
                Messages.DBToolDeleteAll, false));
        
        //keep summary on delete
        opt.addOption(createOption(
                OPTION_KEEPSUMMARY_ON_DELETE, false, null,
                Messages.DBToolDeleteKeepSummary, false));

        // optional directory for import/export
        opt.addOption(createOption(OPTION_DIR, true, PAR_DIR, 
                Messages.DBToolDir, false));

        // export all
        opt.addOption(createOption(OPTION_EXPORT_ALL, false, null,
                Messages.DBToolExportAll, false));
        
        // export one
        final Option exportOption = createOption(OPTION_EXPORT, true,
                PAR_PROJECT, Messages.DBToolExport, false);
        exportOption.setArgs(2);
        opt.addOption(exportOption);
        
        // import
        opt.addOption(createOption(OPTION_IMPORT, true, PAR_IMPORT, 
                Messages.DBToolImport, false));
    }

    /**
     * {@inheritDoc}
     */
    protected void extendValidate(JobConfiguration job, 
            StringBuilder errorMsgs) {
        String[] args = getCmdLine().getOptionValues(OPTION_DELETE);
        if ((args != null) && (args.length != 2)) {
            appendError(errorMsgs, OPTION_DELETE, PAR_PROJECT);
        }
        args = getCmdLine().getOptionValues(OPTION_EXPORT);
        if ((args != null) && (args.length != 2)) {
            appendError(errorMsgs, OPTION_EXPORT, PAR_PROJECT);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int doRun() {
        Job dbToolOperation = new Job(Messages.DBToolPerforming) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(Messages.DBToolPerforming, 
                        IProgressMonitor.UNKNOWN);
                setupDB();
                final CommandLine cmdLine = getCmdLine();
                boolean keepTRSummaries = cmdLine
                    .hasOption(OPTION_KEEPSUMMARY_ON_DELETE) ? true : false;
                
                if (cmdLine.hasOption(OPTION_DELETE)) {
                    final String[] projValues = cmdLine.getOptionValues(
                            OPTION_DELETE);
                    if ((projValues != null) && (projValues.length == 2)) {
                        deleteProject(projValues[0], projValues[1],
                                keepTRSummaries, monitor);
                    }
                }
                
                if (cmdLine.hasOption(OPTION_DELETE_ALL)) {
                    deleteAllProjects(keepTRSummaries, monitor);
                }
                
                String projectDir = cmdLine.getOptionValue(OPTION_DIR,
                        StringConstants.DOT);

                if (cmdLine.hasOption(OPTION_EXPORT)) {
                    final String[] projValues = cmdLine.getOptionValues(
                            OPTION_EXPORT);
                    if ((projValues != null) && (projValues.length == 2)) {
                        exportProject(projValues[0], projValues[1], projectDir,
                                monitor);
                    }
                    
                }
                
                if (cmdLine.hasOption(OPTION_EXPORT_ALL)) {
                    exportAll(projectDir, monitor);
                }

                if (cmdLine.hasOption(OPTION_IMPORT)) {
                    importProject(cmdLine.getOptionValue(OPTION_IMPORT),
                            projectDir, monitor);
                }
                return Status.OK_STATUS;
            }
            
        };
        dbToolOperation.schedule();
        while (dbToolOperation.getState() != Job.NONE) {
            TimeUtil.delay(500);
        }
        IStatus result = dbToolOperation.getResult();
        if (result.getSeverity() == IStatus.OK) {
            return EXIT_CODE_OK;
        }
        return EXIT_CODE_ERROR;
    }

    /**
     * Import a project from an export file
     * @param fileName export file name
     * @param exportDir directory to use
     * @param monitor the progress monitor to use
     */
    private void importProject(String fileName, String exportDir, 
            IProgressMonitor monitor) {
        File impFile = new File(fileName);
        if (!impFile.isAbsolute()) {
            impFile = new File(new File(exportDir), fileName);
        }
        try {
            List<URL> fileURLs = new ArrayList<URL>(1);
            fileURLs.add(impFile.toURI().toURL());
            FileStorageBP.importFiles(fileURLs, monitor, this, false);
        } catch (PMException pme) {
            writeErrorLine(pme.getLocalizedMessage());
        } catch (ProjectDeletedException gdpde) {
            writeErrorLine(gdpde.getLocalizedMessage());
        } catch (MalformedURLException e) {
            writeErrorLine(e.getLocalizedMessage());
        }
    }

    /**
     * Export a project from the database
     * @param name project name
     * @param version version number in <major>.<minor> format
     * @param exportDir Directory for export. The directory must exist
     * @param monitor the progress monitor to use
     */
    private void exportProject(String name, String version, String exportDir,
            IProgressMonitor monitor) {
        int versionNrs[] = buildVersionNrs(name, version);
        if (versionNrs != null) {
            File export = new File(exportDir);
            if (!export.isDirectory() || !export.canWrite()) {
                reportBadDirectory(exportDir);
                return;
            }
            String dirName = export.getAbsolutePath() + File.separator;
            final EntityManager session = Persistor.instance().openSession();
            try {
                List<IProjectPO> projects = ProjectPM.findAllProjects(session);
                List<IProjectPO> exportProjects = new ArrayList<IProjectPO>(1);
                for (IProjectPO project : projects) {
                    if (project.getName().equals(name)
                            && project.getMajorProjectVersion().equals(
                                    new Integer(versionNrs[0]))
                            && project.getMinorProjectVersion().equals(
                                    new Integer(versionNrs[1]))) {
                        exportProjects.add(project);
                    }
                }
                if (exportProjects.size() == 0) {
                    reportMissingProject(name, version);
                    return;
                }
                List<File> listOfProjectFiles = 
                    new ArrayList<File>(exportProjects.size());
                FileStorageBP.exportProjectList(exportProjects, dirName,
                        session, monitor, false, listOfProjectFiles, this);
            } catch (JBException e) {
                reportExportAllFailed(exportDir, e);
            } catch (InterruptedException e) {
                // the monitor doesn't allow cancelation
            } finally {                
                Persistor.instance().dropSession(session);
            }
            
        }
        
    }

    /**
     * Get all project from the db and export them as XML files into 
     * exportDir
     * @param exportDir Directory for export. The directory must exist
     * and must not contain any entries.
     * @param monitor the progress monitor to use
     */
    private void exportAll(String exportDir, IProgressMonitor monitor) {
        File export = new File(exportDir);
        if (!export.isDirectory() || !export.canWrite()) {
            reportBadDirectory(exportDir);
            return;
        }
        if (export.list().length != 0) {
            reportNonEmptyDirectory(exportDir);
            return;            
        }
        String dirName = export.getAbsolutePath() + File.separator;
        final EntityManager session = Persistor.instance().openSession();
        try {
            List<IProjectPO> projects = ProjectPM.findAllProjects(session);
            List<File> listOfProjectFiles = 
                new ArrayList<File>(projects.size());
            FileStorageBP.exportProjectList(projects, dirName, session,
                    monitor, false, listOfProjectFiles, this);
        } catch (JBException e) {
            reportExportAllFailed(exportDir, e);
        } catch (InterruptedException e) {
            // the monitor doesn't allow cancelation
        } finally {            
            Persistor.instance().dropSession(session);
        }
    }

    /**
     * Delete a project from the database
     * @param name project name
     * @param version version number in <major>.<minor> format
     * @param keepSummaryOnDelete test result summary will not be deleted when true
     * @param monitor the progress monitor to use
     */
    private void deleteProject(String name, String version,
            boolean keepSummaryOnDelete, IProgressMonitor monitor) {
        int[] versionNr = buildVersionNrs(name, version);
        if (versionNr != null) {
            IProjectPO project;
            try {
                project = ProjectPM.loadProjectByNameAndVersion(name,
                        versionNr[0], versionNr[1]);
            } catch (JBException e) {
                reportMissingProject(name, version);
                return;
            }
            if (project == null) { 
                reportMissingProject(name, version);
            } else {
                try {
                    String pName = project.getName();
                    int pMajVer = project.getMajorProjectVersion();
                    int pMinVer = project.getMinorProjectVersion();
                    monitor.subTask(NLS.bind(Messages.DBToolDeletingProject,
                            new Object[] { pName, pMajVer, pMinVer }));
                    ProjectPM.deleteProject(project, false);
                    monitor.subTask((NLS.bind(Messages.DBToolDeleteFinished,
                            pName)));
                    monitor.subTask(Messages.DBToolDeletingTestResultDetails);
                    if (keepSummaryOnDelete) {
                        TestResultSummaryPM.deleteTestrunsByProject(
                                project.getGuid(), pMajVer, pMinVer, true);
                    } else {
                        TestResultSummaryPM.deleteTestrunsByProject(
                                project.getGuid(), pMajVer, pMinVer, false);
                    }
                    monitor.subTask(
                            Messages.DBToolDeletingTestResultDetailsFinished);
                } catch (JBException e) {
                    reportDeleteFailed(name, version, e);
                } catch (InterruptedException e) {
                    // can't happen, this could only be thrown by a user
                    // interaction
                }
            }
        }
    }
    
    /**
     * Delete All projects from the database including testresults
     * @param keepSummaryOnDelete summary will not be deleted, when true
     * @param monitor the progress monitor to use
     */
    private void deleteAllProjects(boolean keepSummaryOnDelete, 
            IProgressMonitor monitor) {
        
        List<IProjectPO> projects;
        try {
            projects = ProjectPM.findAllProjects();
            monitor.subTask(NLS.bind(Messages.DBToolDeletingAllProjects,
                    projects.size()));
            for (IProjectPO proj : projects) {
                String pName = proj.getName();
                monitor.subTask(NLS.bind(
                        Messages.DBToolDeletingProject,
                        new Object[] { pName,
                                proj.getMajorProjectVersion(),
                                proj.getMinorProjectVersion() }));
                ProjectPM.deleteProject(proj, false);
                monitor.subTask((NLS.bind(
                        Messages.DBToolDeleteFinished, pName)));
            }
            if (!keepSummaryOnDelete) {
                monitor.subTask(Messages.DBToolDeletingTestResultSummaries);
                TestResultSummaryPM.deleteAllTestresultSummaries();
                monitor.subTask(
                        Messages.DBToolDeletingTestResultSummariesFinished);
            }
            monitor.subTask(Messages.DBToolDeletingTestResultDetails);
            TestResultPM.deleteAllTestresultDetails();
            monitor.subTask(Messages.DBToolDeletingTestResultDetailsFinished);
        } catch (JBException e) {
            printlnConsoleError(e.getMessage());
        } catch (InterruptedException e) {
            // can't happen, this could only be thrown by a user
            // interaction
        }
    }
    
    /**
     * get major/mnir version from string value in major.minir format
     * @param name for error reporting
     * @param version versinon in m.m format
     * @return an array with 2 ints, idex 0 = major, index 1 = minor
     * or null if the input isn't a valid version
     */
    private int[] buildVersionNrs(String name, String version) {
        int sepPos = version.indexOf('.');
        if ((sepPos == -1) || (sepPos == 0)) {
            reportBadVersion(name, version);
            return null;
        }
        
        String majorStr = version.substring(0, sepPos);
        String minorStr = version.substring(sepPos + 1);
        int versionNr[] = new int[2];
        try {
            versionNr[0] = Integer.parseInt(majorStr);
            versionNr[1] = Integer.parseInt(minorStr);                        
        } catch (NumberFormatException e) {
            reportBadVersion(name, version);
            return null;
        }
        return versionNr;
    }

    /**
     * The deletion of the projct failed
     * @param name project name
     * @param version project version
     * @param e error condition
     */
    private void reportDeleteFailed(String name, String version, 
            Exception e) {
        StringBuilder msg = new StringBuilder(Messages.DBToolDeleteFailed);
        msg.append(StringConstants.SPACE);
        msg.append(name);
        msg.append(StringConstants.SPACE + StringConstants.LEFT_BRACKET);
        msg.append(version);
        msg.append(StringConstants.RIGHT_BRACKET);
        msg.append(StringConstants.NEWLINE);
        msg.append(e.getLocalizedMessage());
        printlnConsoleError(msg.toString());

    }

    /**
     * Report a bad version
     * @param name project name
     * @param version illegal project version
     */
    private void reportBadVersion(String name, String version) {
        StringBuilder msg = new StringBuilder(Messages.DBToolInvalidVersion);
        msg.append(StringConstants.SPACE);
        msg.append(name);
        msg.append(StringConstants.SPACE + StringConstants.LEFT_BRACKET);
        msg.append(version);
        msg.append(StringConstants.RIGHT_BRACKET);
        printlnConsoleError(msg.toString());
    }

    /**
     * Report a missing project
     * @param name project name
     * @param version illegal project version
     */
    private void reportMissingProject(String name, String version) {
        StringBuilder msg = new StringBuilder(Messages.DBToolMissingProject);
        msg.append(StringConstants.SPACE);
        msg.append(name);
        msg.append(StringConstants.SPACE + StringConstants.LEFT_BRACKET);
        msg.append(version);
        msg.append(StringConstants.RIGHT_BRACKET);
        printlnConsoleError(msg.toString());
    }

    /**
     * Report a bad version
     * @param dirName directory name
     */
    private void reportBadDirectory(String dirName) {
        StringBuilder msg = new StringBuilder(Messages
                .DBToolInvalidExportDirectory);
        msg.append(StringConstants.SPACE);
        msg.append(dirName);
        printlnConsoleError(msg.toString());
    }

    /**
     * Report a bad version
     * @param dirName directory name
     */
    private void reportNonEmptyDirectory(String dirName) {
        StringBuilder msg = new StringBuilder(Messages
                .DBToolNonEmptyExportDirectory);
        msg.append(StringConstants.SPACE);
        msg.append(dirName);
        printlnConsoleError(msg.toString());
    }
    
    /**
     * @param exportDir directory name
     * @param e error condition
     */
    private void reportExportAllFailed(String exportDir, JBException e) {
        StringBuilder msg = new StringBuilder(Messages.DBToolExportAllFailed);
        msg.append(StringConstants.SPACE);
        msg.append(exportDir);
        msg.append(StringConstants.NEWLINE);
        msg.append(e.getLocalizedMessage());
        printlnConsoleError(msg.toString());       
    }

    /**
     * 
     */
    private void setupDB() {
        Persistor.setDbConnectionName(getJob().getDbscheme());
        Persistor.setUser(getJob().getDbuser());
        Persistor.setPw(getJob().getDbpw());
        Persistor.setUrl(getJob().getDb());
        try {
            if (!Persistor.init()) {
                throw new IllegalArgumentException(
                        Messages.ExecutionControllerInvalidDBDataError, null);
            }
        } catch (JBFatalException e) {
            throw new IllegalArgumentException(
                    Messages.ExecutionControllerInvalidDBDataError, e);
        }
    }

    /** {@inheritDoc} */
    public String getCmdlineClientExecName() {
        return Messages.DBToolName;
    }
}
