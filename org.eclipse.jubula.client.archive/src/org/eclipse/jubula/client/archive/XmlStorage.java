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
package org.eclipse.jubula.client.archive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.PersistenceException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.Validate;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jubula.client.archive.i18n.Messages;
import org.eclipse.jubula.client.archive.schema.ContentDocument;
import org.eclipse.jubula.client.archive.schema.ContentDocument.Content;
import org.eclipse.jubula.client.archive.schema.Project;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 11.01.2006
 */
@SuppressWarnings("synthetic-access")
public class XmlStorage {
    /**
     * Helper for IO-related tasks that can be cancelled.
     *
     * @author BREDEX GmbH
     * @created Dec 3, 2007
     */
    private static class IOCanceller extends TimerTask {
        
        /** The monitor for which the IO is taking place. */
        private IProgressMonitor m_monitor;
        
        /** The writer in which the IO is taking place. */
        private FileWriterWithEncoding m_writer;
        
        /** The Timer used to schedule regular interruption checks. */
        private Timer m_timer;
        
        /**
         * Constructor
         * 
         * @param monitor
         *            The monitor for which the IO is taking place.
         * @param writer
         *            The writer in which the IO is taking place.
         */
        public IOCanceller(IProgressMonitor monitor,
            FileWriterWithEncoding writer) {

            m_monitor = monitor;
            m_writer = writer;
            m_timer = new Timer();
        }
        
        /**
         * Signal that the IO task is about to start.
         */
        public void startTask() {
            m_timer.schedule(this, 1000, 1000);
        }

        /**
         * Signal that the IO task has finished.
         */
        public void taskFinished() {
            m_timer.cancel();
        }

        /**
         * Check whether the operation has been cancelled. If so, the output
         * stream will be closed.
         */
        private void checkTask() {
            if (m_monitor.isCanceled()) {
                try {
                    m_writer.close();
                } catch (IOException e) {
                    log.error(Messages.ErrorWhileCloseOS, e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            checkTask();
        }
    }
    
    /** XML header encoding definition */
    public static final String RECOMMENDED_CHAR_ENCODING = "UTF-8"; //$NON-NLS-1$
    
    /**
     * The supported character encodings.
     */
    private static final String[] SUPPORTED_CHAR_ENCODINGS = 
        new String[]{RECOMMENDED_CHAR_ENCODING, "UTF-16"};  //$NON-NLS-1$
    
    /**
     * the current XML schema namespace
     */
    private static final String SCHEMA_NAMESPACE = "http://www.eclipse.org/jubula/client/archive/schema"; //$NON-NLS-1$

    /** name of GUIdancer import/export XML element representing Exec Test Cases */
    private static final String EXEC_TC_XML_ELEMENT_NAME = "usedTestcase"; //$NON-NLS-1$

    /** XPATH statement for selecting all Exec Test Cases */
    private static final String XPATH_FOR_EXEC_TCS = "declare namespace s='" + SCHEMA_NAMESPACE + "' " + //$NON-NLS-1$//$NON-NLS-2$
            ".//s:" + EXEC_TC_XML_ELEMENT_NAME; //$NON-NLS-1$

    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(XmlStorage.class);

    /**
     * the old xml schema namespace (< 5.0)
     */
    private static final String OLD_SCHEMA_NAMESPACE = "http://www.bredexsw.com/guidancer/client/importer/gdschema"; //$NON-NLS-1$
        
    /**
     * Generate an XML document representing the content of the project.
     * 
     * @param project
     *            the root of the data
     * @param includeTestResultSummaries
     *            Whether to save the Test Result Summaries as well.
     * @param monitor
     *            The progress monitor for this potentially long-running
     *            operation.
     * @return an input stream to the XML representation, or
     *         <code>null</code> if the operation was cancelled.
     * @throws PMException
     *             of io or encoding errors
     * @throws ProjectDeletedException
     *             in case of current project is already deleted
     */
    private static InputStream save(IProjectPO project, 
            boolean includeTestResultSummaries, IProgressMonitor monitor) 
        throws ProjectDeletedException, PMException {
        XmlOptions genOpts = new XmlOptions();
        genOpts.setCharacterEncoding(RECOMMENDED_CHAR_ENCODING);
        genOpts.setSaveInner();
        genOpts.setSaveAggressiveNamespaces();
        genOpts.setUseDefaultNamespace();
        // Don't make use of pretty print due to http://eclip.se/395788
        // genOpts.setSavePrettyPrint();

        ContentDocument contentDoc = ContentDocument.Factory
            .newInstance(genOpts);
        Content content = contentDoc.addNewContent();

        Project prj = content.addNewProject();

        try {
            new XmlExporter(monitor).fillProject(
                    prj, project, includeTestResultSummaries);
        } catch (OperationCanceledException oce) {
            // Operation was cancelled.
            log.info(Messages.ExportOperationCanceled);
            return null;
        }

        if (monitor.isCanceled()) {
            // Operation was cancelled.
            return null;
        }
        
        XmlOptions options = new XmlOptions(genOpts);

        Collection errors = new ArrayList();
        options.setErrorListener(errors);
        if (!contentDoc.validate(options)) {
            StringBuilder msgs = new StringBuilder(StringConstants.NEWLINE);
            for (Object msg : errors) {
                msgs.append(msg);
            }
            if (log.isDebugEnabled()) {
                log.debug(Messages.ValidateFailed 
                        + StringConstants.COLON, msgs);
                log.debug(Messages.ValidateFailed 
                        + StringConstants.COLON, contentDoc);
            }
            throw new PMSaveException(
                "XML" + Messages.ValidateFailed + msgs.toString(), //$NON-NLS-1$
                MessageIDs.E_FILE_IO);
        }
        return contentDoc.newInputStream(genOpts);
    }
    
    /**
     * Takes the supplied input stream and parses it. According to the content an
     * instance of IProjetPO along with its associated components is created.
     * 
     * @param projectXmlStream
     *            input stream for XML representation of a project
     * @param majorVersion
     *            Major version number for the created object, or
     *            <code>null</code> if the version from the imported XML should
     *            be used.
     * @param minorVersion
     *            Minor version number for the created object, or
     *            <code>null</code> if the version from the imported XML should
     *            be used.
     * @param microVersion
     *            Micro version number for the created object, or
     *            <code>null</code> if the version from the imported XML should
     *            be used.
     * @param versionQualifier
     *            Version Qualifier number for the created object, or
     *            <code>null</code> if the version from the imported XML should
     *            be used.
     * @param paramNameMapper
     *            mapper to resolve param names
     * @param compNameCache
     *            cache to resolve component names
     * @param monitor
     *            The progress monitor for this potentially long-running
     *            operation.
     * @param io
     *            the device to write the import output
     * @param skipTrackingInformation
     *            whether to skip importing of tracked information
     * @return an transient IProjectPO and its components
     * @throws PMReadException
     *             in case of a invalid XML string
     * @throws JBVersionException
     *             in case of version conflict between used toolkits of imported
     *             project and the installed toolkit plug-ins
     * @throws InterruptedException
     *             if the operation was canceled.
     */
    public static IProjectPO load(InputStream projectXmlStream,
        Integer majorVersion, Integer minorVersion,
        Integer microVersion, String versionQualifier,
        IParamNameMapper paramNameMapper,
        IWritableComponentNameCache compNameCache, IProgressMonitor monitor,
        IProgressConsole io, boolean skipTrackingInformation)
        throws PMReadException, JBVersionException, InterruptedException {
        ContentDocument contentDoc;
        try {
            contentDoc = getContent(projectXmlStream);
            Project projectXml = contentDoc.getContent().getProject();
            int numExecTestCases = 
                projectXml.selectPath(XPATH_FOR_EXEC_TCS).length;
            
            monitor.beginTask(StringConstants.EMPTY, numExecTestCases + 1);
            monitor.worked(1);
            
            XmlImporter xmlImporter = new XmlImporter(monitor, io,
                    skipTrackingInformation);
            if ((majorVersion != null || versionQualifier != null)) {
                return xmlImporter.createProject(
                        projectXml, majorVersion, minorVersion,
                        microVersion, versionQualifier, paramNameMapper, 
                        compNameCache);
            }
            return xmlImporter.createProject(projectXml, 
                    paramNameMapper, compNameCache);
        } catch (XmlException e) {
            throw new PMReadException(Messages.InvalidImportFile,
                MessageIDs.E_LOAD_PROJECT);
        } catch (InvalidDataException e) {
            throw new PMReadException(Messages.InvalidImportFile,
                e.getErrorId());
        } 
    }

    /**
     * Reads the content from a string containing XML data
     * @param projectXmlStream an input stream to the project XML data
     * @return a ContentDocument which represents the XML data
     * @throws XmlException  if the parsing fails
     * @throws PMReadException if the validation fails
     */
    private static ContentDocument getContent(InputStream projectXmlStream)
        throws XmlException, PMReadException {
        Map<String, String> substitutes = new HashMap<String, String>();
        substitutes.put(OLD_SCHEMA_NAMESPACE, SCHEMA_NAMESPACE);
        XmlOptions options = new XmlOptions();
        options.setLoadSubstituteNamespaces(substitutes);

        ContentDocument contentDoc = null;
        try {
            contentDoc = ContentDocument.Factory.parse(
                    projectXmlStream, options);
            Collection errors = new ArrayList();
            options.setErrorListener(errors);
            if (!contentDoc.validate(options)) {
                StringBuilder msgs = new StringBuilder(StringConstants.NEWLINE);
                for (Object msg : errors) {
                    msgs.append(msg);
                }
                if (log.isDebugEnabled()) {
                    log.debug(Messages.ValidateFailed 
                            + StringConstants.COLON, msgs);
                    log.debug(Messages.ValidateFailed 
                            + StringConstants.COLON, contentDoc);
                }
                throw new PMReadException(Messages.InvalidImportFile
                        + msgs.toString(), MessageIDs.E_LOAD_PROJECT);
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new PMReadException(e.getLocalizedMessage(), 
                    MessageIDs.E_LOAD_PROJECT);
        } finally {
            IOUtils.closeQuietly(projectXmlStream);
        }
        return contentDoc;
    }

    /**
     * Save a project as XML to a file or return the serialized project as
     * an input stream, if fileName == null!
     * 
     * @param proj
     *            project to be saved
     * @param fileName
     *            name for file to save or null, if wanting to get the project
     *            as serialized string
     * @param includeTestResultSummaries
     *            Whether to save the Test Result Summaries as well.
     * @param monitor
     *            The progress monitor for this potentially long-running
     *            operation.
     * @param writeToSystemTempDir
     *            Indicates whether the project has to be written to the system
     *            temp directory
     * @param listOfProjectFiles
     *            If a project is written into the temp dir then the written
     *            file is added to the list, if the list is not null.
     * @return an input stream to the serialized project if fileName == null<br>
     *         or<br>
     *         <b>Returns:</b><br>
     *         null otherwise. Always returns <code>null</code> if the save
     *         operation was canceled.
     * @throws PMException
     *             if save failed for any reason
     * @throws ProjectDeletedException
     *             in case of current project is already deleted
     */
    public static InputStream save(IProjectPO proj, String fileName,
            boolean includeTestResultSummaries,
            IProgressMonitor monitor, boolean writeToSystemTempDir, 
            List<File> listOfProjectFiles)
        throws ProjectDeletedException, PMException {

        monitor.beginTask(Messages.XmlStorageSavingProject, 
            getWorkToSave(proj));
                
        Validate.notNull(proj);
        FileWriterWithEncoding fWriter = null;
        try {
            InputStream projXMLStream = XmlStorage.save(proj, 
                includeTestResultSummaries, monitor);

            if (fileName == null) {
                return projXMLStream;
            }

            if (writeToSystemTempDir) {
                File fileInTempDir = createTempFile(fileName);
                if (listOfProjectFiles != null) {
                    listOfProjectFiles.add(fileInTempDir);
                }
                fWriter = new FileWriterWithEncoding(fileInTempDir,
                    RECOMMENDED_CHAR_ENCODING);
            } else {
                fWriter = new FileWriterWithEncoding(fileName,
                    RECOMMENDED_CHAR_ENCODING);
            }

            IOCanceller canceller = new IOCanceller(monitor, fWriter);
            canceller.startTask();
            IOUtils.copy(projXMLStream, fWriter, RECOMMENDED_CHAR_ENCODING);
            canceller.taskFinished();
        } catch (FileNotFoundException e) {
            log.debug(Messages.File + StringConstants.SPACE 
                    + Messages.NotFound, e);
            throw new PMSaveException(Messages.File + StringConstants.SPACE 
                    + fileName + Messages.NotFound + StringConstants.COLON 
                    + StringConstants.SPACE 
                    + e.toString(), MessageIDs.E_FILE_IO);
        } catch (IOException e) {
            // If the operation has been canceled, then this is just
            // a result of canceling the IO.
            if (!monitor.isCanceled()) {
                log.debug(Messages.GeneralIoExeption, e);
                throw new PMSaveException(Messages.GeneralIoExeption 
                        + e.toString(), MessageIDs.E_FILE_IO);
            }
        } catch (PersistenceException e) {
            log.debug(Messages.CouldNotInitializeProxy 
                    + StringConstants.DOT, e);
            throw new PMSaveException(e.getMessage(),
                MessageIDs.E_DATABASE_GENERAL);
        } finally {
            if (fWriter != null) {
                try {
                    fWriter.close();
                } catch (IOException e) {
                    // just log, we are already done
                    log.error(Messages.CantCloseOOS + fWriter.toString(), e);
                }
            }
        }
        return null;
    }

    /**
     * Creates a file with the given name in the system temp directory.
     * @param fileName The name of the file to be created in temp dir
     * @return the created file
     */
    private static File createTempFile(String fileName) throws IOException {
        final String fileNamePrefix;
        final String fileNameSuffix;
        int dotIndex = fileName.lastIndexOf(StringConstants.DOT);
        
        if (dotIndex < 0) {
            fileNamePrefix = fileName;
            fileNameSuffix = StringConstants.EMPTY;
        } else {
            fileNamePrefix = fileName.substring(0, dotIndex) 
                + StringConstants.UNDERSCORE;
            fileNameSuffix = fileName.substring(dotIndex);
        }
        File fileInTempDir = 
            File.createTempFile(fileNamePrefix, fileNameSuffix);
        
        return fileInTempDir;
    }

    /**
     * Reads the content of the file and returns it as a string.
     * 
     * @param fileURL
     *            The URL of the project to import
     * @return an input stream to the URL content
     * @throws PMReadException
     *             If the file couldn't be read (wrong file name, IOException)
     */
    private static InputStream openStreamToProjectURL(URL fileURL)
        throws PMReadException {
        try {
            checkCharacterEncoding(fileURL);
            return fileURL.openStream();
        } catch (IOException e) {
            log.debug(e.getLocalizedMessage(), e);
            throw new PMReadException(e.toString(), MessageIDs.E_FILE_IO);
        }
    }

    /**
     * Checks the character encoding of the given XML-URL.
     * 
     * @param xmlProjectURL
     *            a URL-object which must point a valid XML-Structure.
     * @see SUPPORTED_CHAR_ENCODINGS
     * @return the encoding or throws exception if not supported encoding used
     * @throws IOException
     *             in case of reading error.
     */
    public static String checkCharacterEncoding(URL xmlProjectURL)
        throws IOException {
        for (String encoding : SUPPORTED_CHAR_ENCODINGS) {
            try (InputStream xmlProjectStream = xmlProjectURL.openStream();
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(xmlProjectStream, encoding))) {
                final String firstLine = reader.readLine();
                if (firstLine != null && firstLine.contains(encoding)) {
                    return encoding;
                }
            }
        }
        throw new IOException(Messages.NoSupportedFileEncoding
            + StringConstants.EXCLAMATION_MARK);
    }

    /**
     * read a <code> GeneralStorage </code> object from filename <b> call
     * getProjectAutToolKit(String filename) at first </b>
     * 
     * @param fileURL
     *            URL of the project file to read
     * @param paramNameMapper
     *            mapper to resolve param names
     * @param compNameCache
     *            cache to resolve component names
     * @param monitor
     *            The progress monitor for this potentially long-running
     *            operation.
     * @param io
     *            the device to write the import output
     * @return the persisted object
     * @throws PMReadException
     *             in case of error
     * @throws JBVersionException
     *             in case of version conflict between used toolkits of imported
     *             project and the installed Toolkit Plugins
     * @throws InterruptedException
     *             if the operation was canceled.
     */
    public IProjectPO readProject(URL fileURL, 
        IParamNameMapper paramNameMapper, 
        IWritableComponentNameCache compNameCache,
        IProgressMonitor monitor, IProgressConsole io) throws PMReadException, 
        JBVersionException, InterruptedException {
        return load(openStreamToProjectURL(fileURL), null,
                null, null, null, paramNameMapper, compNameCache, monitor, io,
                false);
    }

    /**
     * 
     * @param project The project for which the work is predicted.
     * @return The predicted amount of work required to save a project.
     */
    public static int getWorkToSave(IProjectPO project) {
        return new XmlExporter(new NullProgressMonitor())
            .getPredictedWork(project);
    }

    /**
     * 
     * @param projectsToSave The projects for which the work is predicted.
     * @return The predicted amount of work required to save the
     *         given projects.
     */
    public static int getWorkToSave(List<IProjectPO> projectsToSave) {
        int totalWork = 0;
        
        for (IProjectPO project : projectsToSave) {
            totalWork += getWorkToSave(project);
        }

        return totalWork;
    }
}