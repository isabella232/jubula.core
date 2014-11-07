/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter.ui.handlers;

import static org.eclipse.jubula.client.api.converter.utils.Utils.EXEC_PATH;
import static org.eclipse.jubula.client.api.converter.utils.Utils.SPEC_PATH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.api.converter.NodeGenerator;
import org.eclipse.jubula.client.api.converter.NodeInfo;
import org.eclipse.jubula.client.api.converter.exceptions.InvalidNodeNameException;
import org.eclipse.jubula.client.api.converter.ui.i18n.Messages;
import org.eclipse.jubula.client.api.converter.utils.Utils;
import org.eclipse.jubula.client.core.errorhandling.ErrorMessagePresenter;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IExecPersistable;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 24.10.2014
 */
public class ConvertProjectHandler extends AbstractHandler {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(ConvertProjectHandler.class);
    
    /** target path of conversion */
    private static String genPath;
    
    /** target package name space */
    private static String genPackage;
    
    /** the project language */
    private static Locale language =
            WorkingLanguageBP.getInstance().getWorkingLanguage();

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        
        DirectoryDialog directoryDialog = createDirectoryDialog();
        genPath = directoryDialog.open();
        if (genPath != null) {
            org.eclipse.jubula.client.ui.rcp.utils.Utils.storeLastDirPath(
                    directoryDialog.getFilterPath());
            File directory = new File(genPath);
            if (directory.list().length == 0) {
                InputDialog inputDialog = new InputDialog(getActiveShell(),
                        Messages.InputDialogName, Messages.InputDialogMessage,
                        StringConstants.EMPTY, new PackageNameValidator());
                if (inputDialog.open() == Window.OK) {
                    genPackage = inputDialog.getValue();
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    try {
                        workbench.getProgressService().run(true, true,
                                new ConvertProjectOperation());
                    } catch (InvocationTargetException
                            | InterruptedException e) {
                        LOG.error(Messages.ErrorWhileConverting, e);
                    }
                }
            } else {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_NON_EMPTY_DIRECTORY);
            }
        }
        return null;
    }

    /**
     * @return a directory dialog
     */
    private DirectoryDialog createDirectoryDialog() {
        DirectoryDialog directoryDialog = 
                new DirectoryDialog(getActiveShell(), SWT.SAVE);
        String filterPath =
                org.eclipse.jubula.client.ui.rcp.utils.Utils.getLastDirPath();
        directoryDialog.setFilterPath(filterPath);
        return directoryDialog;
    }
    
    /**
     * @created 24.10.2014
     */
    private static class ConvertProjectOperation implements
        IRunnableWithProgress {
        
        /** the project */
        private static IProgressMonitor progressMonitor;
        /** the default toolkit */
        private static String defaultToolkit;
        
        /** {@inheritDoc} */
        public void run(IProgressMonitor monitor) {
            progressMonitor = monitor;
            IProjectPO project = GeneralStorage.getInstance().getProject();
            progressMonitor.beginTask(NLS.bind(Messages.ConvertProjectTaskName,
                    project.getName()), IProgressMonitor.UNKNOWN);
            String basePath = genPath + StringConstants.SLASH
                    + genPackage.replace(StringConstants.DOT, 
                            StringConstants.SLASH);
            
            if (project != null) {
                defaultToolkit = determineDefaultToolkit(project);
                                
                // Handle the reused projects
                Iterator iterator = project.getUsedProjects().iterator();
                while (iterator.hasNext()) {
                    IReusedProjectPO reusedProject =
                            (IReusedProjectPO) iterator.next();
                    IProjectPO usedProject;
                    try {
                        usedProject = ProjectPM
                            .loadReusedProjectInMasterSession(reusedProject);
                        handleProject(usedProject, basePath);
                    } catch (JBException e) {
                        ErrorHandlingUtil.createMessageDialog(
                                new JBException(e.getMessage(), e,
                                        MessageIDs.E_LOAD_PROJECT));
                    }
                }
                // Handle the project itself
                handleProject(project, basePath);
            }
            progressMonitor.done();
        }

        /**
         * Returns the default toolkit for a project
         * by inspecting its first AUT
         * @param project the project
         * @return the name of the default toolkit
         */
        private String determineDefaultToolkit(IProjectPO project) {
            String toolkit = null;
            IAUTMainPO firstAUT = null;
            try {
                firstAUT = project.getAutCont()
                        .getAutMainList().iterator().next();
            } catch (NoSuchElementException e) {
                ErrorMessagePresenter.getPresenter().showErrorMessage(
                        new JBException(
                            Messages.NoAutInProject, 
                            MessageIDs.E_NO_AUT_IN_PROJECT),
                        null, null);
                progressMonitor.setCanceled(true);
            }
            if (firstAUT != null) {
                toolkit = firstAUT.getToolkit();
            }
            return toolkit;
        }

        /**
         * Traverses a project and creates files and directories for its content.
         * @param project the project
         * @param basePath the base path
         */
        private void handleProject(IProjectPO project, String basePath) {
            String projectName = StringConstants.EMPTY;
            try {
                projectName = Utils.translateToPackageName(project);
            } catch (InvalidNodeNameException e) {
                displayErrorForInvalidName(project);
            }
            String projectPath = basePath + StringConstants.SLASH + projectName;
            for (IExecPersistable node : project.getExecObjCont()
                    .getExecObjList()) {
                String path = projectPath + StringConstants.SLASH
                        + EXEC_PATH + StringConstants.SLASH;
                handleNode(new File(path), node);
            }
            for (ISpecPersistable node : project.getSpecObjCont()
                    .getSpecObjList()) {
                String path = projectPath + StringConstants.SLASH
                        + SPEC_PATH + StringConstants.SLASH;
                handleNode(new File(path), node);
            }
        }
        
        /**
         * Creates a file for a node and a folder with its content for a category
         * @param parentDir the parent directory
         * @param node the node
         */
        private void handleNode(File parentDir, INodePO node) {
            if (progressMonitor.isCanceled()) {
                return;
            }
            if (node instanceof ICategoryPO) {
                ICategoryPO category = (ICategoryPO) node;
                handleCategory(parentDir, category);
            } else {
                handleTestCaseSuiteOrJob(parentDir, node);
            }
        }

        /**
         * Handles the conversion for a category.
         * @param parentDir the parent directory
         * @param category the category
         */
        private void handleCategory(File parentDir, ICategoryPO category) {
            File dir = createDir(parentDir, category);
            if (dir.exists()) {
                displayErrorForDuplicate(category);
                return;
            }
            dir.mkdirs();
            
            Iterator iterator = category.getNodeListIterator();
            while (iterator.hasNext()) {
                INodePO child = (INodePO) iterator.next();
                handleNode(dir, child);
            }
        }
        
        /**
         * Handles the conversion for a test case.
         * @param parentDir the parent directory
         * @param node the test case
         */
        private void handleTestCaseSuiteOrJob(File parentDir, INodePO node) {
            File file = createFile(parentDir, node);
            if (file.exists()) {
                displayErrorForDuplicate(node);
                return;
            }
            try {
                file.createNewFile();
                NodeGenerator gen = new NodeGenerator();
                NodeInfo info = new NodeInfo(file.getName(), node,
                        genPackage, defaultToolkit, language);
                String content = gen.generate(info);
                writeContentToFile(file, content);
            } catch (IOException e) {
                ErrorHandlingUtil.createMessageDialog(
                        new JBException(e.getMessage(), e,
                                MessageIDs.E_FILE_NO_PERMISSION));
            }
        }
        
        /**
         * Writes a string into a given file.
         * @param file the file
         * @param content the content
         */
        private void writeContentToFile(File file, String content)
            throws IOException {
            FileOutputStream fop = new FileOutputStream(file);
            byte[] contentInBytes = content.getBytes();
            IOUtils.write(contentInBytes, fop);
        }

        /**
         * Displays an error message in the case that a node occurs multiple times
         * @param node the duplicate node
         */
        private void displayErrorForDuplicate(INodePO node) {
            String fqNodeName = Utils.getFullyQualifiedName(node);
            ErrorMessagePresenter.getPresenter().showErrorMessage(
                    new JBException(
                        NLS.bind(Messages.DuplicateNode, 
                            new String[] {fqNodeName}), 
                        MessageIDs.E_DUPLICATE_NODE),
                    new String [] {fqNodeName},
                    null);
            progressMonitor.setCanceled(true);
        }
        
        /**
         * Creates a directory for a category with given parent directory
         * @param parentDir the parent directory
         * @param category the category
         * @return the directory
         */
        private File createDir(File parentDir, ICategoryPO category) {
            String dirPath;
            File dir = null;
            try {
                dirPath = parentDir.getAbsolutePath() + StringConstants.SLASH
                        + Utils.translateToPackageName(category);
                dir = new File(dirPath);
            } catch (InvalidNodeNameException e) {
                displayErrorForInvalidName(category);
            }
            return dir;
        }
        
        /**
         * Creates a directory for a category with given parent directory
         * @param parentDir the parent directory
         * @param node the category
         * @return the directory
         */
        private File createFile(File parentDir, INodePO node) {
            String extension = ".java"; //$NON-NLS-1$
            String className = StringConstants.EMPTY;
            try {
                className = Utils.determineClassName(node);
            } catch (InvalidNodeNameException e) {
                displayErrorForInvalidName(node);
            }
            String fileName = parentDir.getAbsolutePath()
                    + StringConstants.SLASH
                    + className
                    + extension;
            File file = new File(fileName);
            while (file.exists()) {
                String oldName = StringUtils.substringBeforeLast(
                        file.getAbsolutePath(), extension);
                file = new File(oldName + StringConstants.UNDERSCORE
                        + extension);
                
                Plugin.getDefault().writeErrorLineToConsole(
                        "Duplicate filename error:" + fileName, true); //$NON-NLS-1$
            }
            return file;
        }

        /**
         * Displays an error for the case of an invalid node name
         * @param node the node
         */
        private void displayErrorForInvalidName(INodePO node) {
            String fqNodeName = Utils.getFullyQualifiedName(node);
            ErrorMessagePresenter.getPresenter().showErrorMessage(
                    new JBException(
                        NLS.bind(Messages.InvalidNodeName, 
                            new String[] {fqNodeName}), 
                        MessageIDs.E_INVALID_NODE_NAME),
                    new String [] {fqNodeName},
                    null);
            progressMonitor.setCanceled(true);
        }
    }
    
    /**
     * @created 27.10.2014
     */
    private static class PackageNameValidator implements IInputValidator {
        
        /** {@inheritDoc} */
        public String isValid(String newText) {
            if (newText.isEmpty()) {
                return Messages.NoPackageNameSpecified;
            }
            Pattern p = Pattern.compile(
                    "^[a-zA-Z_][\\w]*(\\.[a-zA-Z_][\\w]*)*$"); //$NON-NLS-1$
            if (!p.matcher(newText).matches()) {
                return Messages.InvalidPackageName;
            }
            return null;
        }
    }
    
}