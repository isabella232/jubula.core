/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rap.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.AbstractXMLReportGenerator;
import org.eclipse.jubula.client.core.businessprocess.CompleteXMLReportGenerator;
import org.eclipse.jubula.client.core.businessprocess.HtmlResultReportWriter;
import org.eclipse.jubula.client.core.businessprocess.TestResultReportNamer;
import org.eclipse.jubula.client.core.businessprocess.XmlResultReportWriter;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.SummarizedTestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.editors.TestResultViewer.GenerateTestResultTreeOperation;
import org.eclipse.jubula.client.ui.rap.constants.IdConstants;
import org.eclipse.jubula.client.ui.rap.servicehandler.DownloadTestResultsServiceHandler;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.widgets.ExternalBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for exporting Test Result Report(s).
 * 
 * @author BREDEX GmbH
 */
public class ExportTestResultsHandler extends AbstractHandler {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(ExportTestResultsHandler.class);

    /**
     * Job for exporting Test Results.
     * 
     * @author BREDEX GmbH
     */
    private static class ExportTestResultsJob extends Job {

        /** the Test Result Summaries for which to export Test Results */
        private ITestResultSummaryPO[] m_summariesToExport;
        
        /** the display to use for UI operations */
        private Display m_display;
        
        /** the base Dashboard URL */
        private String m_baseUrl;
        
        /** the response to use for encoding the download link */
        private HttpServletResponse m_response;

        /**
         * Constructor
         * 
         * @param summariesToExport The Test Result Summaries for which to 
         *                          export Test Results.
         * @param baseUrl The display to use for UI operations.
         * @param display The base Dashboard URL.
         * @param response The response to use for encoding the download link.
         */
        public ExportTestResultsJob(ITestResultSummaryPO[] summariesToExport,
                String baseUrl, Display display, 
                HttpServletResponse response) {
            super("Export Test Results"); //$NON-NLS-1$
            m_summariesToExport = summariesToExport;
            m_baseUrl = baseUrl;
            m_display = display;
            m_response = response;
        }
        
        /**
         * {@inheritDoc}
         */
        protected IStatus run(IProgressMonitor monitor) {
            try {
                URI downloadFileUri = 
                        performExport(m_summariesToExport, monitor).toURI();
                
                final StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(m_baseUrl).append("?").append(IServiceHandler.REQUEST_PARAM) //$NON-NLS-1$
                    .append("=").append(DownloadTestResultsServiceHandler.SERVICE_HANDLER_ID) //$NON-NLS-1$
                    .append("&").append(DownloadTestResultsServiceHandler.PARAM_FILENAME) //$NON-NLS-1$
                    .append("="); //$NON-NLS-1$
                urlBuilder.append(URLEncoder.encode(downloadFileUri.toString(), 
                        TestResultReportNamer.ENCODING));
                final URI uri = new URI(urlBuilder.toString());
                        
                if (m_display != null && !m_display.isDisposed()) {
                    m_display.asyncExec(new Runnable() {
                        
                        public void run() {
                            ExternalBrowser.open("_blank",  //$NON-NLS-1$
                                    m_response.encodeURL(
                                            uri.toASCIIString()), 
                                    SWT.NONE);
                        }
                    });
                } else {
                    LOG.warn("Could not initiate download. Display is null or disposed."); //$NON-NLS-1$
                }
                return Status.OK_STATUS;
            } catch (IOException e) {
                return new Status(IStatus.ERROR, IdConstants.BUNDLE_ID, 
                        "Error occurred while exporting Test Results.", e); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                return new Status(IStatus.ERROR, IdConstants.BUNDLE_ID, 
                        "Error occurred while downloading Test Results.", e); //$NON-NLS-1$
            }
        }
        
        /**
         * Exports the given Test Result Summaries to a ZIP file.
         * 
         * @param selectedSummaries The Summaries to export.
         * @param monitor The progress monitor.
         * @return the exported file.
         * 
         * @throws IOException if an I/O error occurs during export.
         */
        private File performExport(ITestResultSummaryPO[] selectedSummaries, 
                IProgressMonitor monitor) throws IOException {
            
            SubMonitor subMonitor = 
                    SubMonitor.convert(monitor, "Exporting...",  //$NON-NLS-1$
                            selectedSummaries.length * 2);

            ZipOutputStream zipOutputStream = null;
            OutputStreamWriter zipWriter = null;
            Persistor persistor = Persistor.instance();
            EntityManager session = persistor.openSession();
            try {
                final File exportDest = File.createTempFile("dashboard_results", ".zip"); //$NON-NLS-1$ //$NON-NLS-2$
                zipOutputStream = 
                        new ZipOutputStream(new FileOutputStream(exportDest));
                zipWriter = new OutputStreamWriter(
                        zipOutputStream, TestResultReportNamer.ENCODING);

                for (ITestResultSummaryPO summary : selectedSummaries) {
                    GenerateTestResultTreeOperation operation =
                            new GenerateTestResultTreeOperation(
                                    summary.getId(), 
                                    summary.getInternalProjectID(),
                                    session);

                    operation.run(subMonitor.newChild(1));
                    
                    TestResultNode rootDetailNode = operation.getRootNode();
                    AbstractXMLReportGenerator generator = 
                        new CompleteXMLReportGenerator(
                                new SummarizedTestResult(
                                        summary, rootDetailNode));

                    Document reportDocument = generator.generateXmlReport();

                    TestResultReportNamer reportNamer = 
                            new TestResultReportNamer(summary.getTestsuiteName() + "_" + summary.getTestsuiteStartTime().getTime()); //$NON-NLS-1$
                    zipOutputStream.putNextEntry(
                            new ZipEntry(reportNamer.getXmlEntryName()));
                    XmlResultReportWriter xmlWriter = 
                            new XmlResultReportWriter(zipWriter);
                    xmlWriter.write(reportDocument);
                    zipWriter.flush();
                    
                    zipOutputStream.putNextEntry(
                            new ZipEntry(reportNamer.getHtmlEntryName()));
                    HtmlResultReportWriter htmlWriter =
                            new HtmlResultReportWriter(zipWriter);
                    htmlWriter.write(reportDocument);
                    zipWriter.flush();
                    subMonitor.worked(1);
                }
                return exportDest;
            } finally {
                persistor.dropSession(session);
                monitor.done();
                if (zipWriter != null) {
                    try {
                        zipWriter.close();
                    } catch (IOException e) {
                        LOG.warn("Error while closing ZIP writer.", e); //$NON-NLS-1$
                    }
                }
                if (zipOutputStream != null) {
                    try {
                        zipOutputStream.close();
                    } catch (IOException e) {
                        LOG.warn("Error while closing ZIP output stream.", e); //$NON-NLS-1$
                    }
                }
            }
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IStructuredSelection structuredSelection = null;
        if (selection instanceof IStructuredSelection) {
            structuredSelection = (IStructuredSelection)selection;
        }
        List<ITestResultSummaryPO> selectedSummaryList = 
                new ArrayList<ITestResultSummaryPO>(structuredSelection.size());
        
        // Only try to compute the details if there is an active
        // DB connection. Otherwise we will receive an NPE
        // while trying to initialize the Master Session.
        if (Persistor.instance() != null) {
            for (Object selectedElement : structuredSelection.toArray()) {
                if (selectedElement instanceof ITestResultSummaryPO) {
                    ITestResultSummaryPO summary = (ITestResultSummaryPO)
                        selectedElement;
                    if (summary.hasTestResultDetails()) {
                        selectedSummaryList.add(summary);
                    }
                }
            }
        }
            
        final ITestResultSummaryPO [] selectedSummaries = 
                selectedSummaryList.toArray(
                        new ITestResultSummaryPO[selectedSummaryList.size()]);

        if (selectedSummaries.length == 0) {
            MessageDialog.openInformation(HandlerUtil.getActiveShell(event), 
                    "Export Test Results",  //$NON-NLS-1$
                    "No Test Results were selected for export."); //$NON-NLS-1$
            return null;
        }

        Job exportJob = new ExportTestResultsJob(selectedSummaries, 
                RWT.getRequest().getContextPath() 
                    + RWT.getRequest().getServletPath(),
                Display.getCurrent(),
                RWT.getResponse());
        exportJob.setUser(true);
        
        JobUtils.executeJob(exportJob, HandlerUtil.getActivePart(event));
        
        return null;
    }

}
