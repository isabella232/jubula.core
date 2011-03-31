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
package org.eclipse.jubula.client.ui.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.archive.XmlStorage;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
public class ExportProjectHandler extends AbstractHandler {
    /**
     * @author BREDEX GmbH
     * @created Jan 22, 2010
     */
    private static final class ExportFileOperation implements
            IRunnableWithProgress {

        /** the filename to use for the exported XML file */
        private final String m_fileName;

        /**
         * @param fileName The filename to use for the exported XML file.
         */
        private ExportFileOperation(String fileName) {
            m_fileName = fileName;
        }

        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {
            if (m_fileName != null) {
                try {
                    GeneralStorage gstorage = GeneralStorage.getInstance();
                    gstorage.validateProjectExists(gstorage.getProject());
                    final AtomicReference<IStatus> statusOfRefresh =
                        new AtomicReference<IStatus>();
                    Plugin.getDisplay().syncExec(new Runnable() {
                        public void run() {
                            RefreshProjectHandler rph = 
                                new RefreshProjectHandler();
                            statusOfRefresh.set((IStatus)rph.execute(null));
                        }
                    });
                    
                    // Only proceed with the export if the refresh was
                    // successful.
                    if (statusOfRefresh.get() != null
                            && statusOfRefresh.get().isOK()) {
                        SubMonitor subMonitor = SubMonitor.convert(monitor,
                                Messages.ExportFileActionExporting,
                                1);
                        
                        XmlStorage.save(gstorage.getProject(), m_fileName, 
                                true, subMonitor.newChild(1));
                    }
                } catch (final PMException e) {
                    Utils.createMessageDialog(e, null, null);
                } catch (final ProjectDeletedException e) {
                    PMExceptionHandler.handleGDProjectDeletedException();
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
    private static Log log = LogFactory.getLog(ExportProjectHandler.class);

    /**
     * 
     */
    @SuppressWarnings("synthetic-access")
    private void showExportDialog() {
        final FileDialog fileDialog = new FileDialog(Plugin.getShell(), 
            SWT.SAVE | SWT.APPLICATION_MODAL);
        fileDialog.setText(Messages.ActionBuilderSaveAs);
        fileDialog.setFilterExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
        fileDialog.setFilterPath(Utils.getLastDirPath());
        
        StringBuilder sb = new StringBuilder(
            GeneralStorage.getInstance().getProject().getDisplayName());
        fileDialog.setFileName(sb.append(".xml").toString()); //$NON-NLS-1$
        final String fileNameTemp = fileDialog.open();
        if (fileNameTemp == null) { // Cancel pressed
            return;
        } 

        File file = new File(fileNameTemp);
        if (file.exists()) {
            MessageBox mb = new MessageBox(fileDialog.getParent(),
                    SWT.ICON_WARNING | SWT.YES | SWT.NO);
            mb.setText(Messages.ExportFileActionConfirmOverwriteTitle);
            mb.setMessage(NLS.bind(Messages.ExportFileActionConfirmOverwrite,
                    new Object[] {fileNameTemp }));
            if (mb.open() == SWT.NO) {
                return;
            }
        }

        Plugin.startLongRunning(Messages.ExportFileActionWaitWhileExporting);
        
        final String fileName = fileNameTemp;
        Utils.storeLastDirPath(fileDialog.getFilterPath());

        IRunnableWithProgress op = new ExportFileOperation(fileName);

        try {
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile(op);
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(ite.getCause());
        } catch (InterruptedException ie) {
            // Operation canceled. 
            // Do nothing.
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        if (Plugin.getDefault().anyDirtyStar()) {
            if (Plugin.getDefault().
                showSaveEditorDialog()) {

                showExportDialog();
            }
            Plugin.stopLongRunning();
            return null;
        }
        showExportDialog(); 
        Plugin.stopLongRunning();
        return null;
    }
}