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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.TakeScreenshotMessage;
import org.eclipse.jubula.communication.message.TakeScreenshotResponseMessage;
import org.eclipse.jubula.tools.serialisation.SerializedImage;


/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class TakeScreenshotCommand implements ICommand {
    /** Logger */
    private static final Log LOG = LogFactory
            .getLog(TakeScreenshotCommand.class);

    /** message */
    private TakeScreenshotMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        TakeScreenshotResponseMessage response = 
            new TakeScreenshotResponseMessage();
        try {
            Robot robot = new Robot();
            // Determine current screen size
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            Rectangle screenRect = new Rectangle(screenSize);
            BufferedImage bi = robot.createScreenCapture(screenRect);
            response.setScreenshot(SerializedImage.computeSerializeImage(bi));
        } catch (AWTException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e);
            }
        }
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
