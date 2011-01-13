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
package org.eclipse.jubula.rc.swt.driver;

import java.util.List;

import org.eclipse.jubula.rc.common.driver.IEventMatcher;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created 20.03.2007
 */
public class WindowSwtEventMatcher implements IEventMatcher {

    /** the event matcher */
    private DefaultSwtEventMatcher m_eventMatcher;
    /** the title */
    private final String m_title; 
    /** the matches operation */
    private final String m_operator;
    /** how to react on disposed widgets */
    private final boolean m_valForDisposed; 
    
    /**
     * constructor
     * @param title the title
     * @param operator the matches operation
     * @param eventCode The SWT event code to match
     * @param valForDisposed Value that is to be returned if the component is
     * already disposed
     */
    public WindowSwtEventMatcher(String title, String operator, int eventCode, 
        boolean valForDisposed) {
        m_title = title;
        m_operator = operator;
        m_eventMatcher = new DefaultSwtEventMatcher(eventCode);
        m_valForDisposed = valForDisposed;
    }

    /**
     * {@inheritDoc}
     */
    public int getEventId() {
        return m_eventMatcher.getEventId();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects, 
            Object graphicsComponent) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMatching(Object eventObject) {
        if (m_eventMatcher.isMatching(eventObject)) {
            final Event event = (Event)eventObject;

            IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
            Boolean matched = (Boolean)evThreadQueuer.invokeAndWait(
                "matchWindow", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        if (event.widget instanceof Shell) {
                            Shell frame = (Shell)event.widget;
                            if (frame.isDisposed()) {
                                return Boolean.valueOf(m_valForDisposed);
                            }
                            return Boolean.valueOf(MatchUtil.getInstance()
                                .match(frame.getText(), m_title, m_operator));

                        }
                        return Boolean.FALSE;
                    }
                });
            return matched.booleanValue();

        }
        return false;
    }
}
