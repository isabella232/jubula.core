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
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.regex.Pattern;

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
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IExecPersistable;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
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
    private static IProjectPO project;

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
            project = GeneralStorage.getInstance().getProject();
            monitor.beginTask(NLS.bind(Messages.ConvertProjectTaskName,
                    project.getName()), IProgressMonitor.UNKNOWN);
            String basePath = genPath + StringConstants.SLASH
                    + genPackage.replace(StringConstants.DOT, 
                            StringConstants.SLASH);
            
            if (project != null) {
                for (IExecPersistable node : project.getExecObjCont()
                        .getExecObjList()) {
                    String path = basePath + StringConstants.SLASH
                            + EXEC_PATH + StringConstants.SLASH;
                    createFileOrDir(monitor, node, new File(path));
                }
                for (ISpecPersistable node : project.getSpecObjCont()
                        .getSpecObjList()) {
                    String path = basePath + StringConstants.SLASH
                            + SPEC_PATH + StringConstants.SLASH;
                    createFileOrDir(monitor, node, new File(path));
                }
            }
            monitor.done();
        }
        
        /**
         * Creates a file for a node and a folder with its content for a category
         * @param monitor the monitor
         * @param node the node
         * @param parentDir the parent directory
         */
        private void createFileOrDir(IProgressMonitor monitor,
                INodePO node, File parentDir) {
            if (monitor.isCanceled()) {
                return;
            }
            if (node instanceof ICategoryPO) {
                ICategoryPO category = (ICategoryPO) node;
                File dir = createDir(parentDir, category);
                if (dir.exists()) {
                    String fqCategoryName = getFullyQualifiedName(category);
                    ErrorMessagePresenter.getPresenter().showErrorMessage(
                            new JBException(
                                NLS.bind(Messages.DuplicateCategory, 
                                    new String[] {fqCategoryName, 
                                        project.getName()}), 
                                MessageIDs.E_DUPLICATE_CATEGORY),
                            new String [] {fqCategoryName},
                            null);
                    monitor.setCanceled(true);
                    return;
                }
                dir.mkdirs();
                
                Iterator iterator = category.getNodeListIterator();
                while (iterator.hasNext()) {
                    INodePO child = (INodePO) iterator.next();
                    if (child instanceof IExecPersistable) {
                        createFileOrDir(monitor, child, dir);
                    }
                }
            }
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