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
package org.eclipse.jubula.rc.common.commands;

import java.awt.image.BufferedImage;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotMessage;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotResponseMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.tools.internal.serialisation.SerializedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class TakeScreenshotCommand implements ICommand {
    /** Logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(TakeScreenshotCommand.class);

    /** message */
    private TakeScreenshotMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        TakeScreenshotResponseMessage response = 
                new TakeScreenshotResponseMessage();
        final BufferedImage createScreenCapture = AUTServer.getInstance()
                .getRobot().createFullScreenCapture();
        final SerializedImage computedSerializeImage = SerializedImage
                .computeSerializeImage(createScreenCapture);
        response.setScreenshot(computedSerializeImage);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(Message message) {
        m_message = (TakeScreenshotMessage)message;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return m_message;
    }
}
