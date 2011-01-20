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
package org.eclipse.jubula.client.ui.businessprocess;

import java.io.File;
import java.util.Enumeration;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 09.04.2008
 */
public final class ShowClientLogBP extends AbstractActionBP {

    /** single instance */
    private static ShowClientLogBP instance = null;

    /**
     * private constructor
     */
    private ShowClientLogBP() {
        // Nothing to initialize
    }

    /**
     * @return single instance
     */
    public static ShowClientLogBP getInstance() {
        if (instance == null) {
            instance = new ShowClientLogBP();
        }
        return instance;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the client log file and handles occurring errors.
     * @return The client log file, if file logging is activated. Otherwise
     *         null is returned.
     */
    public File getClientLogFile() {
        final File clientLogFile;
        
        // Get location of log file
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        FileAppender enumFileAppender = null;
        while (appenders.hasMoreElements() && enumFileAppender == null) {
            Object enumElement = appenders.nextElement();
            if (enumElement instanceof FileAppender) {
                enumFileAppender = (FileAppender)enumElement;
            }
        }
        
        if (enumFileAppender != null) {
            clientLogFile = new File(enumFileAppender.getFile());
        } else {
            clientLogFile = null;
            
            // Ask user to turn on file logging
            Utils.createMessageDialog(MessageIDs.I_FILE_LOGGING_NOT_ENABLED,
                new String[] {"Jubula"}, null); //$NON-NLS-1$
        }
            
        return clientLogFile;
    }

}
