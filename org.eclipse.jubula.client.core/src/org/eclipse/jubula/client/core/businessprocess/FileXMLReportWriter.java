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
package org.eclipse.jubula.client.core.businessprocess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;

/**
 * Writes an XML document to a file. Also writes an HTML document based on the
 * XML document to another file.
 *
 * @author BREDEX GmbH
 * @created Jan 23, 2007
 */
public class FileXMLReportWriter implements IXMLReportWriter {

    /** file extension for XML */
    public static final String FILE_EXTENSION_XML = ".xml"; //$NON-NLS-1$
    
    /** file extension for HTML */
    public static final String FILE_EXTENSION_HTML = ".htm"; //$NON-NLS-1$

    /**
     * <code>ENCODING</code>
     */
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

    /** The logger */
    private static final Log LOG = LogFactory.getLog(
        FileXMLReportWriter.class);
    
    /** The target file to write to */
    private String m_file;

    /** XSL file for transformation to html */
    private URL m_xsl;
    
    /** Directory for HTML res files */
    private String m_htmlDir;

    
    
    /**
     * @param file
     *      given file name
     * @param xsl
     *      given xsl File for Transformation to html
     * @param htmlDir
     *      Directory for html res files
     */
    public FileXMLReportWriter(String file, URL xsl, String htmlDir) {
        m_file = file;
        m_xsl = xsl;
        m_htmlDir = htmlDir;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void write(Document document) {
        OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
        xmlFormat.setEncoding(ENCODING);
        OutputFormat htmlFormat = OutputFormat.createCompactFormat();
        htmlFormat.setEncoding(ENCODING);
        // write xml
        try {
            final Writer writer = new OutputStreamWriter(
                new FileOutputStream(m_file + FILE_EXTENSION_XML), ENCODING); 
            
            XMLWriter fileWriter = new XMLWriter(writer, xmlFormat);
            fileWriter.write(document);
            fileWriter.close();
        } catch (UnsupportedEncodingException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        } catch (IOException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        }
        if (m_xsl == null) {
            return;
        }
        // write html, transformed by XSLT
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            final Transformer transformer = factory.newTransformer(
                    new StreamSource(m_xsl.openStream()));
            DocumentSource source = new DocumentSource(document);
            DocumentResult result = new DocumentResult();
            transformer.transform(source, result);
            Document transformedDoc = result.getDocument();
            File htmlFile = new File(m_file + FILE_EXTENSION_HTML);
            final Writer writer = new OutputStreamWriter(
                new FileOutputStream(htmlFile), ENCODING); 
            XMLWriter fileWriter = new XMLWriter(writer, htmlFormat);
            fileWriter.write(transformedDoc);
            fileWriter.close();

            // Copy needed html Files
            File targetDir = htmlFile.getParentFile();
            File srcDir = new File(m_htmlDir);
            targetDir = new File(targetDir.getPath() + "/html"); //$NON-NLS-1$
            targetDir.mkdir();

            File[] fileList = srcDir.listFiles();
            for (Object f : fileList) {
                File fileIter = (File)f;
                File targetFile = new File(targetDir.getAbsoluteFile() 
                    + "/" + fileIter.getName()); //$NON-NLS-1$
                if (!targetFile.exists()
                    && fileIter.exists()
                    && fileIter.isFile()) {
                    FileUtils.copyFile(fileIter, targetFile);
                }
            }
        } catch (TransformerConfigurationException e1) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e1);
        } catch (TransformerException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        } catch (IOException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        }
    }

}
