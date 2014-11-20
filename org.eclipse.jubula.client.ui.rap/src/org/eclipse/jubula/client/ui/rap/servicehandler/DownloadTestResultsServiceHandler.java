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
package org.eclipse.jubula.client.ui.rap.servicehandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles service requests for downloading exported Test Results.
 * 
 * This Service Handler has the following parameters:<ul> 
 * <li>{@link DownloadTestResultsServiceHandler#PARAM_FILENAME}.</li>
 * </ul>
 * @author BREDEX GmbH
 */
public class DownloadTestResultsServiceHandler implements ServiceHandler {

    /** service handler ID */
    public static final String SERVICE_HANDLER_ID = 
            "org.eclipse.jubula.client.ui.rap.servicehandler.DownloadTestResultsServiceHandler"; //$NON-NLS-1$

    /** name of parameter for name of downloadable file */
    public static final String PARAM_FILENAME = "filename"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(DownloadTestResultsServiceHandler.class);

    /**
     * {@inheritDoc}
     */
    public void service(HttpServletRequest request, 
            HttpServletResponse response)
            throws IOException {
        File downloadFile;
        try {
            downloadFile = new File(new URI(request.getParameter(
                    PARAM_FILENAME)).toURL().getFile());
            response.setContentType("application/octet-stream"); //$NON-NLS-1$
            response.setContentLength((int) downloadFile.length());
            response.setHeader("Content-Disposition", //$NON-NLS-1$
                    "attachment; filename=\"" + downloadFile.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            BufferedOutputStream outputStream = new BufferedOutputStream(
                    response.getOutputStream());
            BufferedInputStream inputStream = new BufferedInputStream(
                    new FileInputStream(downloadFile));

            try {
                int b;
                while ((b = inputStream.read()) != -1) {
                    outputStream.write(b);
                }
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                } finally {
                    if (!downloadFile.delete()) {
                        downloadFile.deleteOnExit();
                    }
                }
            }
        } catch (URISyntaxException use) {
            LOG.error("Unable to initiate Test Result download.", use); //$NON-NLS-1$
        }
    }

}
