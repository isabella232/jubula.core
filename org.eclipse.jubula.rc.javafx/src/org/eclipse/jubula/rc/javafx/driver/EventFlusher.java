/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.driver;

import java.awt.Robot;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;

/**
 * Could be used to ensure that event occur in the right order but currently
 * only does a Thread.sleep for the given timeout.
 *
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public class EventFlusher {

    /** the logger */
    private static AutServerLogger log =
            new AutServerLogger(EventFlusher.class);

    /** the robot to use */
    private final Robot m_robot;
    /** the flush timeout to use */
    private final Long m_flushTimeout;
    /**
     * indicates whether the default toolkit is compatible to the required
     * toolkit implementation for native event flushing
     */
    private boolean m_isCompatibleToolkit = false;

    /**
     * Constructor
     *
     * @param robot
     *            the robot
     * @param flushTimeout
     *            the flush timeout
     */
    public EventFlusher(Robot robot, long flushTimeout) {
        m_robot = robot;
        m_flushTimeout = new Long(flushTimeout);

    }
}
