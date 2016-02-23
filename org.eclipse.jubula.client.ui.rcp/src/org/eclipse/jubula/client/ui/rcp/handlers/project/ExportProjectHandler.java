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
package org.eclipse.jubula.client.ui.rcp.handlers.project;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.archive.JsonStorage;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.ui.handlers.project.AbstractProjectHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
public class ExportProjectHandler extends AbstractProjectHandler {
    
    /** Extension of XML */
    public static final String XML = ".xml"; //$NON-NLS-1$
    
    /** Extension of JUB. It is an zip file which contain a project and a test result json file*/
    public static final String JUB = ".jub"; //$NON-NLS-1$
    
    /**
     * @author BREDEX GmbH
     * @created Jan 22, 2010
     */
    private static final class ExportFileOperation implements
            IRunnableWithProgress {

        /** the filename to use for the exported file */
        private final String m_fileName;
        
        /** the file extension to use for the exported file */
        private final String m_fileExt;
        
        /** the console to use to display progress and error messages */
        private IProgressConsole m_console;

        /**
         * @param fileName The filename to use for the exported file.
         * @param fileExt extension of file
         * @param console 
         */
        private ExportFileOperation(String fileName, String fileExt,
                IProgressConsole console) {
            m_fileName = fileName;
            m_fileExt = fileExt;
            m_console = console;
        }

        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {
            if (m_fileName != null) {
                try {
                    m_console.writeStatus(new Status(IStatus.INFO,
                        Activator.PLUGIN_ID, Messages
                                .RefreshProjectOperationRefreshing));
                    GeneralStorage gstorage = GeneralStorage.getInstance();
                    gstorage.validateProjectExists(gstorage.getProject());
                    final AtomicReference<IStatus> statusOfRefresh =
                        new AtomicReference<IStatus>();
                    Plugin.getDisplay().syncExec(new Runnable() {
                        public void run() {
                            RefreshProjectHandler rph = 
                                new RefreshProjectHandler();
                            statusOfRefresh.set((IStatus)rph.executeImpl(null));
                        }
                    });
                    // Only proceed with the export if the refresh was
                    // successful.
                    if (statusOfRefresh.get() != null
                            && statusOfRefresh.get().isOK()) {

                        m_console.writeStatus(new Status(IStatus.INFO,
                            Activator.PLUGIN_ID, Messages
                                    .ExportFileActionExporting));
                        SubMonitor subMonitor = SubMonitor.convert(monitor,
                                Messages.ExportFileActionExporting, 1);
                        
                        JsonStorage.save(gstorage.getProject(), m_fileName,
                                true, subMonitor.newChild(1), m_console);
                    }
                } catch (final PMException e) {
                    ErrorHandlingUtil.createMessageDialog(e, null, null);
                } catch (final ProjectDeletedException e) {
                    PMExceptionHandler.handleProjectDeletedException();
                } finally {
                    monitor.done();
                    Plugin.stopLongRunning();
                }
            } else {
                monitor.done();
            }
        }
    }

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ExportProjectHandler.class);

    /**
     * 
     */
    @SuppressWarnings("synthetic-access")
    private void showExportDialog() {
        final FileDialog fileDialog = new FileDialog(getActiveShell(), 
            SWT.SAVE | SWT.APPLICATION_MODAL);
        fileDialog.setText(Messages.ActionBuilderSaveAs);
        String[] filters = new String[]{StringConstants.STAR + JUB};
        fileDialog.setFilterExtensions(filters);
        fileDialog.setFilterPath(Utils.getLastDirPath());
        
        StringBuilder sb = new StringBuilder(
            GeneralStorage.getInstance().getProject().getDisplayName());
        fileDialog.setFileName(sb.toString());
        String fileNameTemp = fileDialog.open();
        
        if (fileNameTemp == null) { // Cancel pressed
            return;
        }
        String extension = filters[fileDialog.getFilterIndex()]
                .replace(StringConstants.STAR, StringConstants.EMPTY);
        fileNameTemp = fileNameTemp.endsWith(extension)
                ? fileNameTemp : fileNameTemp + extension;
        File file = new File(fileNameTemp);
        if (file.exists()) {
            MessageBox mb = new MessageBox(fileDialog.getParent(),
                    SWT.ICON_WARNING | SWT.YES | SWT.NO);
            mb.setText(Messages.ExportFileActionConfirmOverwriteTitle);
            mb.setMessage(NLS.bind(Messages.ExportFileActionConfirmOverwrite,
                    fileNameTemp));
            if (mb.open() == SWT.NO) {
                return;
            }
        }

        Plugin.startLongRunning(Messages.ExportFileActionWaitWhileExporting);
        
        final String fileName = fileNameTemp;
        Utils.storeLastDirPath(fileDialog.getFilterPath());

        IProgressConsole console = Plugin.getDefault();
        IRunnableWithProgress op = new ExportFileOperation(fileName, extension,
                console);

        try {
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile(op);
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(ite.getLocalizedMessage(), ite.getCause());
        } catch (InterruptedException ie) {
            // Operation canceled. 
            // Do nothing.
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        if (Plugin.getDefault().showSaveEditorDialog(getActiveShell())) {
            showExportDialog();
        }
        Plugin.stopLongRunning();
        return null;
    }
}