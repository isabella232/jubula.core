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

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Window;

import org.eclipse.jubula.rc.common.driver.IEventMatcher;
import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.WorkaroundUtil;
import org.eclipse.jubula.rc.javafx.util.JavaFXEventConverter;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;

/**
 * <p>
 * This event confirmer works on a class of AWT events defined by an
 * <code>InterceptorOptions</code> instance. The confirmer adds a
 * {@link java.awt.event.AWTEventListener} to the AWT event queue using the
 * <code>InterceptorOptions</code> event mask.
 * </p>
 * 
 * <p>
 * To confirm an event, call <code>waitToConfirm()</code>.
 * </p>
 * 
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
class RobotEventConfirmerJavaFXImpl implements IRobotEventConfirmer,
        EventHandler<Event> {

    /**
     * The logger.
     */
    private static AutServerLogger log = new AutServerLogger(
            RobotEventConfirmerJavaFXImpl.class);

    /**
     * Stores if the confirmer is enabled.
     */
    private boolean m_enabled = false;
    /**
     * Stores if the confirmer is being waiting for an event to confirm.
     */
    private boolean m_waiting = false;
    /**
     * The interceptor options.
     */
    private InterceptorOptions m_options;
    /**
     * The graphics component on which the event occurs.
     */
    private Object m_eventTarget;
    /**
     * The event matcher.
     */
    private IEventMatcher m_eventMatcher;
    /**
     * Stores all events of a given class after the confirmer has been enabled.
     */
    private LinkedBlockingQueue<Event> m_eventList = 
            new LinkedBlockingQueue<Event>();

    /**
     * Stores Windows on wich Events could occur, this includes Popups such as
     * contextmenus
     */
    private LinkedBlockingQueue<Window> m_sceneGraphs = 
            new LinkedBlockingQueue<Window>();

    /**
     * Creates a new confirmer for a class of events defined by
     * <code>options</code>.
     * 
     * @param options
     *            The options.
     * @param sceneGraphs
     *            List with instances of Windows and their Scene-Graphs
     */
    protected RobotEventConfirmerJavaFXImpl(InterceptorOptions options,
            LinkedBlockingQueue<Window> sceneGraphs) {
        m_options = options;
        m_sceneGraphs = sceneGraphs;
    }

    @Override
    public void waitToConfirm(Object eventTarget, IEventMatcher matcher)
        throws RobotException {
        waitToConfirm(eventTarget, matcher,
                RobotTiming.getEventConfirmTimeout());
    }

    @Override
    public void waitToConfirm(Object eventTarget, IEventMatcher matcher,
            long pTimeout) throws RobotException {
        m_eventTarget = eventTarget;
        m_eventMatcher = matcher;

        if (log.isDebugEnabled()) {
            log.debug("Waiting for EventID: " + String.valueOf(matcher) //$NON-NLS-1$
                    + " on Component: " + String.valueOf(m_eventTarget)); //$NON-NLS-1$
        }

        try {
            m_waiting = true;

            try {
                long timeout = pTimeout;
                long done = System.currentTimeMillis() + timeout;
                long now;
                do {
                    Event e = m_eventList.poll(timeout, TimeUnit.MILLISECONDS);
                    if (e != null && m_eventMatcher.isMatching(e)) {
                        return;
                    }
                    now = System.currentTimeMillis();
                    timeout = done - now;
                } while (m_waiting && (timeout > 0));
            } catch (InterruptedException e) {
                throw new RobotException(e);
            }
            if (m_waiting) {
                // I'm still waiting. This means that the event could not
                // be confirmed during the confirm time interval, that means
                // the event matcher didn't find a matching event.
                // But the event matcher may accept a different event, which has
                // already dispatched, as a fall back.
                boolean fallBackMatching = m_eventMatcher
                        .isFallBackEventMatching(
                                Arrays.asList(m_eventList.toArray()),
                                m_eventTarget);

                if (!fallBackMatching && !WorkaroundUtil.isIgnoreTimeout()) {
                    throw new RobotException(
                            "Timeout received before confirming the posted event: " //$NON-NLS-1$
                                    + m_eventMatcher.getEventId(),
                            EventFactory
                                    .createActionError(TestErrorEvent.
                                            CONFIRMATION_TIMEOUT));
                }
            }

        } finally {
            setEnabled(false);
        }
    }

    /**
     * Enables or disables the confirmer. If the confirmer is enabled, the
     * JavaFX Filter is added to the currently focused stage so that the
     * confirmer starts storing events of the configured class of events. If it
     * is disabled, the listener is removed from the AWT event queue.
     * 
     * @param enabled
     *            <code>true</code> or <code>false</code>.
     */
    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
        m_eventList.clear();
        if (m_enabled) {
            long[] masks = m_options.getEventMask();
            for (int i = 0; i < masks.length; i++) {
                for (Window w : m_sceneGraphs) {
                    w.addEventFilter(
                            JavaFXEventConverter.awtToFX(masks[i]), this);
                }             
            }
        } else {
            long[] masks = m_options.getEventMask();
            for (int i = 0; i < masks.length; i++) {
                for (Window w : m_sceneGraphs) {
                    w.removeEventFilter(
                        JavaFXEventConverter.awtToFX(masks[i]), this);
                }
            }
            m_eventList.clear();
        }
    }

    @Override
    public void handle(Event event) {
        try {
            m_eventList.put(event);
        } catch (InterruptedException e) {
            log.error("InterruptedException: " + event //$NON-NLS-1$
                    + " on Component: " + String.valueOf(m_eventTarget)); //$NON-NLS-1$
        }
    }
}