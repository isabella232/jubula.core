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
package org.eclipse.jubula.communication.message;

import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.serialisation.SerializedImage;

/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 * 
 */
public class TakeScreenshotResponseMessage extends Message {
    /** the screenshot */
    private SerializedImage m_screenshot = null;

    /**
     * Default constructor.
     */
    public TakeScreenshotResponseMessage() {
        // empty
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.TAKE_SCREENSHOT_RESPONSE_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return 1.0;
    }

    /**
     * @param screenshot
     *            the screenshot to set
     */
    public void setScreenshot(SerializedImage screenshot) {
        m_screenshot = screenshot;
    }

    /**
     * @return the screenshot
     */
    public SerializedImage getScreenshot() {
        return m_screenshot;
    }

}
