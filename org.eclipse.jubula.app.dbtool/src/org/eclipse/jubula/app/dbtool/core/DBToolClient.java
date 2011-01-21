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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jubula.app.dbtool.i18n.Messages;
import org.eclipse.jubula.client.archive.businessprocess.FileStorageBP;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;
import org.eclipse.jubula.client.core.businessprocess.JobConfiguration;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.persistence.TestResultPM;
import org.eclipse.jubula.client.core.persistence.TestResultSummaryPM;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;


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
    protected void preRun() {
        // nothing here        
    }
    
    /**
     * {@inheritDoc}
     */
    public int doRun() {
        boolean keepSummariesOnDelete = false;

        setupDB();
        
        final CommandLine cmdLine = getCmdLine();
        
        if (cmdLine.hasOption(OPTION_KEEPSUMMARY_ON_DELETE)) {
            keepSummariesOnDelete = true;
        }
        
        if (cmdLine.hasOption(OPTION_DELETE)) {
            final String[] projValues = cmdLine.getOptionValues(
                    OPTION_DELETE);
            if ((projValues != null) && (projValues.length == 2)) {
                deleteProject(projValues[0], projValues[1],
                        keepSummariesOnDelete);
            }
        }
        
        if (cmdLine.hasOption(OPTION_DELETE_ALL)) {
            deleteAllProjects(keepSummariesOnDelete);
        }
        
        String exportDir = cmdLine.getOptionValue(OPTION_DIR, 
                StringConstants.DOT);

        if (cmdLine.hasOption(OPTION_EXPORT)) {
            final String[] projValues = cmdLine.getOptionValues(
                    OPTION_EXPORT);
            if ((projValues != null) && (projValues.length == 2)) {
                exportProject(projValues[0], projValues[1], exportDir);
            }
            
        }
        
        if (cmdLine.hasOption(OPTION_EXPORT_ALL)) {
            exportAll(exportDir);
        }

        if (cmdLine.hasOption(OPTION_IMPORT)) {
            importProject(cmdLine.getOptionValue(OPTION_IMPORT), exportDir);
        }

        return EXIT_CODE_OK;
    }

    /**
     * Import a project from an export file
     * @param fileName export file name
     * @param exportDir directory to use
     */
    private void importProject(String fileName, String exportDir) {
        File impFile = new File(fileName);
        if (!impFile.isAbsolute()) {
            impFile = new File(new File(exportDir), fileName);
        }
        try {
            FileStorageBP.importFiles(
                    new String[] { impFile.getAbsolutePath() }, 
                    new NullProgressMonitor(), this, false);
        } catch (PMException pme) {
            writeErrorLine(pme.getLocalizedMessage());
        } catch (ProjectDeletedException gdpde) {
            writeErrorLine(gdpde.getLocalizedMessage());
        }
    }

    /**
     * Export a project from the database
     * @param name project name
     * @param version version number in <major>.<minor> format
     * @param exportDir Directory for export. The directory must exist
     */
    private void exportProject(String name, String version, String exportDir) {
        int versionNrs[] = buildVersionNrs(name, version);
        if (versionNrs != null) {
            File export = new File(exportDir);
            if (!export.isDirectory() || !export.canWrite()) {
                reportBadDirectory(exportDir);
                return;
            }
            String dirName = export.getAbsolutePath() + File.separator;
            final EntityManager session = Hibernator.instance().openSession();
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
                FileStorageBP.exportProjectList(exportProjects,
                        dirName, session, new NullProgressMonitor(), false,
                        listOfProjectFiles, this);
            } catch (JBException e) {
                reportExportAllFailed(exportDir, e);
            } catch (InterruptedException e) {
                // the monitor doesn't allow cancelation
            } finally {                
                Hibernator.instance().dropSession(session);
            }
            
        }
        
    }

    /**
     * Get all project from the db and export them as XML files into 
     * exportDir
     * @param exportDir Directory for export. The directory must exist
     * and must not contain any entries.
     */
    private void exportAll(String exportDir) {
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
        final EntityManager session = Hibernator.instance().openSession();
        try {
            List<IProjectPO> projects = ProjectPM.findAllProjects(session);
            List<File> listOfProjectFiles = 
                new ArrayList<File>(projects.size());
            FileStorageBP.exportProjectList(projects, dirName,
                    session, new NullProgressMonitor(), false,
                    listOfProjectFiles, this);
        } catch (JBException e) {
            reportExportAllFailed(exportDir, e);
        } catch (InterruptedException e) {
            // the monitor doesn't allow cancelation
        } finally {            
            Hibernator.instance().dropSession(session);
        }
    }

    /**
     * Delete a project from the database
     * @param name project name
     * @param version version number in <major>.<minor> format
     * @param keepSummaryOnDelete test result summary will not be deleted when true
     */
    private void deleteProject(String name, String version,
            boolean keepSummaryOnDelete) {
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
            if (project == null) { // 
                reportMissingProject(name, version);
            } else {
                try {
                    ProjectPM.deleteProject(project, false);
                    if (keepSummaryOnDelete) {
                        TestResultSummaryPM.deleteTestrunsByProject(
                                project.getGuid(),
                                project.getMajorProjectVersion(),
                                project.getMinorProjectVersion(), true);
                    } else {
                        TestResultSummaryPM.deleteTestrunsByProject(
                                project.getGuid(),
                                project.getMajorProjectVersion(),
                                project.getMinorProjectVersion(), false);
                    }
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
     */
    private void deleteAllProjects(boolean keepSummaryOnDelete) {
        
        List<IProjectPO> projects;
        try {
            projects = ProjectPM.findAllProjects();
            for (IProjectPO proj : projects) {
                ProjectPM.deleteProject(proj, false);
            }
            if (keepSummaryOnDelete) {
                TestResultPM.deleteAllTestresultDetails();
            } else {
                TestResultSummaryPM.deleteAllTestresultSummaries();
                TestResultPM.deleteAllTestresultDetails();
            }
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
        Hibernator.setSchemaName(getJob().getDbscheme());
        Hibernator.setUser(getJob().getDbuser());
        Hibernator.setPw(getJob().getDbpw());
        Hibernator.setUrl(getJob().getDb());
        Hibernator.setHeadless(true);
        if (!Hibernator.init()) {
            throw new IllegalArgumentException(Messages
                    .ExecutionControllerInvalidDBDataError, null);
        }
    }

    /** {@inheritDoc} */
    public String getCmdlineClientName() {
        return Messages.DBToolName;
    }
}
