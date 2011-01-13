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
package org.eclipse.jubula.client.ui.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.ui.IPersistableElement;


/**
 * @author BREDEX GmbH
 * @created Feb 7, 2007
 */
public class ClientLogInput extends PlatformObject 
                            implements ISimpleEditorInput {

    /** the log file */
    private File m_logFile;
    
    /**
     * 
     * @param logFile the log file
     */
    public ClientLogInput(File logFile) {
        m_logFile = logFile;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean exists() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return I18n.getString("ClientLogViewer.Name"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText() {
        return m_logFile.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getContent() throws CoreException {
        try {
            BufferedReader reader = 
                new BufferedReader(new FileReader(m_logFile));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n"); //$NON-NLS-1$
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR, Plugin.PLUGIN_ID,
                    IStatus.OK, 
                    I18n.getString("ErrorMessage.FILE_NOT_FOUND"), //$NON-NLS-1$
                    e));
        } catch (IOException ioe) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR, Plugin.PLUGIN_ID,
                    IStatus.OK, 
                    I18n.getString("ErrorMessage.IO_EXCEPTION"), //$NON-NLS-1$
                    ioe));
        }
    }

}
