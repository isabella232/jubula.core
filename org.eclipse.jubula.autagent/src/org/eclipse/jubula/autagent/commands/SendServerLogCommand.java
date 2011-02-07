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
package org.eclipse.jubula.autagent.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendServerLogMessage;
import org.eclipse.jubula.communication.message.ServerLogResponseMessage;


/**
 * @author BREDEX GmbH
 * @created Feb 8, 2007
 * 
 */
public class SendServerLogCommand implements ICommand {

    /** the logger */
    private static Log log = LogFactory.getLog(SendServerLogCommand.class);

    /** the message */
    private SendServerLogMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        log.info("sending server log"); //$NON-NLS-1$

        ServerLogResponseMessage response = new ServerLogResponseMessage();
        // Get location of log file
        // FIXME: replace with code for slf4j
//      FileAppender enumFileAppender = null; 
//        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
//        Object enumElement = null;
//        while (appenders.hasMoreElements() && enumFileAppender == null) {
//            enumElement = appenders.nextElement();
//            if (enumElement instanceof FileAppender) {
//                enumFileAppender = (FileAppender)enumElement;
//            }
//        }
        
//        if (enumFileAppender != null) {
//            // Send log
//            try {
//                File logFile = new File(enumFileAppender.getFile());
//                BufferedReader reader = 
//                    new BufferedReader(new FileReader(logFile));
//                StringBuffer sb = new StringBuffer();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n"); //$NON-NLS-1$
//                }
//                response.setServerLog(sb.toString());
//            } catch (FileNotFoundException e) {
//                // Set error status
//                response.setStatus(ServerLogResponseMessage.FILE_NOT_FOUND);
//            } catch (IOException ioe) {
//                // Set error status
//                response.setStatus(ServerLogResponseMessage.IO_EXCEPTION);
//            }
//
//        } else {
            // No file logger found, set error status
            response.setStatus(ServerLogResponseMessage.FILE_NOT_ENABLED);
//        }
        
        return response;
        
    }

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (SendServerLogMessage) message;

    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}
