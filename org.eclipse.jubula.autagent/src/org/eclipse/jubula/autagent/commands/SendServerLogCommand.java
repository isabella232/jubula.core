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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendServerLogMessage;
import org.eclipse.jubula.communication.message.ServerLogResponseMessage;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;


/**
 * @author BREDEX GmbH
 * @created Feb 8, 2007
 */
public class SendServerLogCommand implements ICommand {
    /** the logger */
    private static org.slf4j.Logger log = LoggerFactory.getLogger(
            SendServerLogCommand.class);

    /** the message */
    private SendServerLogMessage m_message;

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    public Message execute() {
        log.info("sending server log"); //$NON-NLS-1$

        ServerLogResponseMessage response = new ServerLogResponseMessage();
        // Get location of log file
        Logger logger = (Logger)LoggerFactory.getLogger("ROOT"); //$NON-NLS-1$
        Iterator<Appender<ILoggingEvent>> appenders = logger
                .iteratorForAppenders();
        FileAppender fileAppender = null;
        while (appenders.hasNext() && fileAppender == null) {
            Object enumElement = appenders.next();
            if (enumElement instanceof FileAppender) {
                fileAppender = (FileAppender)enumElement;
            }
        }

        if (fileAppender != null) {
            final File clientLogFile = new File(fileAppender.getFile());
            // Send log
            try {
                BufferedReader reader = new BufferedReader(new FileReader(
                        clientLogFile));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n"); //$NON-NLS-1$
                }
                response.setServerLog(sb.toString());
            } catch (FileNotFoundException e) {
                // Set error status
                response.setStatus(ServerLogResponseMessage.FILE_NOT_FOUND);
            } catch (IOException ioe) {
                // Set error status
                response.setStatus(ServerLogResponseMessage.IO_EXCEPTION);
            } catch (SecurityException e) {
                // Set error status
                response.setStatus(ServerLogResponseMessage.CONFIG_ERROR);
            } catch (IllegalArgumentException e) {
                // Set error status
                response.setStatus(ServerLogResponseMessage.CONFIG_ERROR);
            }
        } else {
            // No file logger found, set error status
            response.setStatus(ServerLogResponseMessage.FILE_NOT_ENABLED);
        }

        return response;

    }

    /** {@inheritDoc} */
    public Message getMessage() {
        return m_message;
    }

    /** {@inheritDoc} */
    public void setMessage(Message message) {
        m_message = (SendServerLogMessage) message;

    }

    /** {@inheritDoc} */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}
