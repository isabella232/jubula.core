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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.api.converter.ui.i18n.Messages;
import org.eclipse.jubula.client.core.errorhandling.ErrorMessagePresenter;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IExecPersistable;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
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
    
    /** specific path for executables */
    private static final String EXEC_PATH = "testsuites"; //$NON-NLS-1$
    
    /** specific path for specifications */
    private static final String SPEC_PATH = "testcases"; //$NON-NLS-1$
    
    /** target path of conversion */
    private static String genPath;
    
    /** target package name space */
    private static String genPackage;
    
    /** the project */
    private static IProgressMonitor progressMonitor;

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        
        DirectoryDialog directoryDialog = createDirectoryDialog();
        genPath = directoryDialog.open();
        if (genPath != null) {
            Utils.storeLastDirPath(directoryDialog.getFilterPath());
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
        String filterPath = Utils.getLastDirPath();
        directoryDialog.setFilterPath(filterPath);
        return directoryDialog;
    }
    
    /**
     * @created 24.10.2014
     */
    private static class ConvertProjectOperation implements
        IRunnableWithProgress {
        
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
                // Handle the reused projects
                Iterator iterator = project.getUsedProjects().iterator();
                while (iterator.hasNext()) {
                    IReusedProjectPO reusedProject =
                            (IReusedProjectPO) iterator.next();
                    IProjectPO usedProject;
                    try {
                        usedProject = ProjectPM.loadProject(reusedProject);
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
         * Traverses a project and creates files and directories for its content.
         * @param project the project
         * @param basePath the base path
         */
        private void handleProject(IProjectPO project, String basePath) {
            String projectPath = basePath + StringConstants.SLASH
                    + project.getName().toLowerCase();
            for (IExecPersistable node : project.getExecObjCont()
                    .getExecObjList()) {
                String path = projectPath + StringConstants.SLASH
                        + EXEC_PATH + StringConstants.SLASH;
                handleNode(node, new File(path));
            }
            for (ISpecPersistable node : project.getSpecObjCont()
                    .getSpecObjList()) {
                String path = projectPath + StringConstants.SLASH
                        + SPEC_PATH + StringConstants.SLASH;
                handleNode(node, new File(path));
            }
        }
        
        /**
         * Creates a file for a node and a folder with its content for a category
         * @param node the node
         * @param parentDir the parent directory
         */
        private void handleNode(INodePO node, File parentDir) {
            if (progressMonitor.isCanceled()) {
                return;
            }
            if (node instanceof ICategoryPO) {
                ICategoryPO category = (ICategoryPO) node;
                handleCategory(parentDir, category);
            }
            if (node instanceof ITestCasePO) {
                ITestCasePO testcase = (ITestCasePO) node;
                handleTestCase(parentDir, testcase);
            }
            if (node instanceof ITestSuitePO) {
                ITestSuitePO testsuite = (ITestSuitePO) node;
                handleTestSuite(parentDir, testsuite);
            }
            if (node instanceof ITestJobPO) {
                ITestJobPO testjob = (ITestJobPO) node;
                handleTestJob(parentDir, testjob);
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
                handleNode(child, dir);
            }
        }
        
        /**
         * Handles the conversion for a test case.
         * @param parentDir the parent directory
         * @param testcase the test case
         */
        private void handleTestCase(File parentDir, ITestCasePO testcase) {
            File file = createFile(parentDir, testcase);
            if (file.exists()) {
                displayErrorForDuplicate(testcase);
                return;
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                ErrorHandlingUtil.createMessageDialog(
                        new JBException(e.getMessage(), e,
                                MessageIDs.E_FILE_NO_PERMISSION));
            }
        }
        
        /**
         * Handles the conversion for a test suite.
         * @param parentDir the parent directory
         * @param testsuite the test suite
         */
        private void handleTestSuite(File parentDir, ITestSuitePO testsuite) {
            File file = createFile(parentDir, testsuite);
            if (file.exists()) {
                displayErrorForDuplicate(testsuite);
                return;
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                ErrorHandlingUtil.createMessageDialog(
                        new JBException(e.getMessage(), e,
                                MessageIDs.E_FILE_NO_PERMISSION));
            }
        }
        
        /**
         * Handles the conversion for a test job.
         * @param parentDir the parent directory
         * @param testjob the test job
         */
        private void handleTestJob(File parentDir, ITestJobPO testjob) {
            File file = createFile(parentDir, testjob);
            if (file.exists()) {
                displayErrorForDuplicate(testjob);
                return;
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                ErrorHandlingUtil.createMessageDialog(
                        new JBException(e.getMessage(), e,
                                MessageIDs.E_FILE_NO_PERMISSION));
            }
        }

        /**
         * Displays an error message in the case that a node occurs multiple times
         * @param node the duplicate node
         */
        private void displayErrorForDuplicate(INodePO node) {
            String fqNodeName = getFullyQualifiedName(node);
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
            String dirPath = parentDir.getAbsolutePath() + StringConstants.SLASH
                    + category.getName();
            File dir = new File(dirPath);
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
            String fileName = parentDir.getAbsolutePath()
                    + StringConstants.SLASH
                    + determineClassName(node)
                    + extension;
            File file = new File(fileName);
            while (file.exists()) {
                String oldName = StringUtils.substringBeforeLast(
                        file.getAbsolutePath(), extension);
                file = new File(oldName + StringConstants.UNDERSCORE
                        + extension);
            }
            return file;
        }

        /**
         * Determines a valid Java class name for a given node
         * @param node the node
         * @return the class name
         */
        private String determineClassName(INodePO node) {
            String name = node.getName();
            String [] invalidChars = new String [] {
                StringConstants.AMPERSAND,
                StringConstants.APOSTROPHE,
                StringConstants.BACKSLASH,
                StringConstants.COLON,
                StringConstants.COMMA,
                StringConstants.DOT,
                StringConstants.EQUALS_SIGN,
                StringConstants.EXCLAMATION_MARK,
                StringConstants.LEFT_BRACKET,
                StringConstants.LEFT_INEQUALITY_SING,
                StringConstants.LEFT_PARENTHESES,
                StringConstants.MINUS,
                StringConstants.PIPE,
                StringConstants.PLUS,
                StringConstants.QUESTION_MARK,
                StringConstants.QUOTE,
                StringConstants.RIGHT_BRACKET,
                StringConstants.RIGHT_INEQUALITY_SING,
                StringConstants.RIGHT_PARENTHESES,
                StringConstants.SEMICOLON,
                StringConstants.SLASH,
                StringConstants.STAR,
                StringConstants.UNDERSCORE
            };
            for (String c : invalidChars) {
                name = name.replace(c, StringConstants.SPACE);
            }
            name = WordUtils.capitalize(name);
            name = StringUtils.deleteWhitespace(name);
            name = name.replaceAll("^[0-9]*", StringConstants.EMPTY); //$NON-NLS-1$
            Pattern p = Pattern.compile("^[A-Z][\\w]*$"); //$NON-NLS-1$
            if (!p.matcher(name).matches()) {
                String fqNodeName = getFullyQualifiedName(node);
                ErrorMessagePresenter.getPresenter().showErrorMessage(
                        new JBException(
                            NLS.bind(Messages.InvalidNodeName, 
                                new String[] {fqNodeName}), 
                            MessageIDs.E_INVALID_NODE_NAME),
                        new String [] {fqNodeName},
                        null);
                progressMonitor.setCanceled(true);
            }
            return name;
        }
        
        /** 
         * Returns the fully qualified name of a node involving
         * all of its super category names
         * @param node the node
         * @return the fully qualified name
         */
        private String getFullyQualifiedName(INodePO node) {
            String name = node.getName();
            INodePO parentNode = node.getParentNode();
            if (parentNode != null) {
                name = getFullyQualifiedName(parentNode)
                        + StringConstants.SLASH + name;
            }
            return name;
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