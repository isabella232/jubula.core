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
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendServerLogMessage;
import org.eclipse.jubula.communication.message.ServerLogResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Feb 8, 2007
 */
public class SendServerLogCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
            SendServerLogCommand.class);

    /** the message */
    private SendServerLogMessage m_message;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public Message execute() {
        log.info("sending server log"); //$NON-NLS-1$

        ServerLogResponseMessage response = new ServerLogResponseMessage();
        // Get location of log file

        FileHandler fileHandler = null;
        Enumeration loggersNames = LogManager.getLogManager().getLoggerNames();
        while (loggersNames.hasMoreElements() && fileHandler == null) {
            java.util.logging.Logger logger = LogManager.getLogManager()
                    .getLogger((String)loggersNames.nextElement());
            Handler[] handlers = logger.getHandlers();
            for (int i = 0; i < handlers.length; ++i) {
                if (handlers[i] instanceof FileHandler) {
                    fileHandler = (FileHandler)handlers[i];
                }
            }
        }

        if (fileHandler != null) {
            // Send log
            try {
                Field filesField = 
                    fileHandler.getClass().getDeclaredField("files"); //$NON-NLS-1$
                filesField.setAccessible(true);
                File[] filesValue = (File[])filesField.get(fileHandler);
                File logFile = filesValue[0];
                BufferedReader reader = new BufferedReader(new FileReader(
                        logFile));
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
            } catch (NoSuchFieldException e) {
                // Set error status
                response.setStatus(ServerLogResponseMessage.CONFIG_ERROR);
            } catch (IllegalArgumentException e) {
                // Set error status
                response.setStatus(ServerLogResponseMessage.CONFIG_ERROR);
            } catch (IllegalAccessException e) {
                // Set error status
                response.setStatus(ServerLogResponseMessage.CONFIG_ERROR);
            }
        } else {
            // No file logger found, set error status
            response.setStatus(ServerLogResponseMessage.FILE_NOT_ENABLED);
        }

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
