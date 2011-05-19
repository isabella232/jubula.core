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
package org.eclipse.jubula.rc.swt.implclasses;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.OsNotSupportedException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.AbstractApplicationImplClass;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.components.SwtComponent;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.swt.implclasses.EventListener.Condition;
import org.eclipse.jubula.rc.swt.interfaces.ISwtApplicationImplClass;
import org.eclipse.jubula.rc.swt.listener.ComponentHandler;
import org.eclipse.jubula.rc.swt.listener.FocusTracker;
import org.eclipse.jubula.rc.swt.utils.SwtPointUtil;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.TimeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


/**
 * Implementation class for swt-application
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class SwtApplicationImplClass extends AbstractApplicationImplClass 
    implements ISwtApplicationImplClass {
    
    /** The logging. */
    private static AutServerLogger log = 
        new AutServerLogger(SwtApplicationImplClass.class);
    
    /**
     * This condition is true if the event source is a Shell with a matching
     * title.
     *
     * @author BREDEX GmbH
     * @created Jun 17, 2009
     */
    private static class WindowEventCondition implements Condition {

        /** the expected window title */
        private String m_windowTitle;
        
        /** the operator used for matching the window title */
        private String m_matchingOperator;

        /** 
         * determines whether the event source being disposed should be 
         * treated as a match 
         */
        private boolean m_valForDisposed;
        
        /**
         * Constructor
         * 
         * @param windowTitle The expected window title.
         * @param matchingOperator The operator used for matching the
         *                         window title.
         * @param valForDisposed Whether the event source being disposed
         *                       should be treated as a match.
         */
        public WindowEventCondition(String windowTitle, 
                String matchingOperator, boolean valForDisposed) {
            m_windowTitle = windowTitle;
            m_matchingOperator = matchingOperator;
            m_valForDisposed = valForDisposed;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isTrue(Event event) {
            if (event.widget instanceof Shell) {
                Shell window = (Shell)event.widget;
                if (window.isDisposed()) {
                    return m_valForDisposed;
                }
                return MatchUtil.getInstance().match(window.getText(), 
                        m_windowTitle, m_matchingOperator);

            }

            return false;
        }
        
    }
    
    /** The Robot factory. */
    private IRobotFactory m_robotFactory;
    
    /**
     * @return The Robot factory instance
     */
    private IRobotFactory getRobotFactory() {
        if (m_robotFactory == null) {
            m_robotFactory = new RobotFactoryConfig().getRobotFactory();
        }
        return m_robotFactory;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected IRobot getRobot() {
        return getRobotFactory().getRobot();
    }
    
    /**
     * perform a keystroke
     * @param modifierSpec the string representation of the modifiers
     * @param keySpec the string representation of the key
     */
    public void gdKeyStroke(String modifierSpec, String keySpec) {
        if (keySpec == null || keySpec.trim().length() == 0) {
            throw new StepExecutionException(
                "The base key of the key stroke must not be null or empty", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_PARAM_VALUE));
        }
        String keyStrokeSpec = keySpec.trim();
        String mod = KeyStrokeUtil.getModifierString(modifierSpec);
        if (mod.length() > 0) {
            keyStrokeSpec = mod + " " + keyStrokeSpec; //$NON-NLS-1$
        }
        // at this the key stroke specification is not fully fullfilled as the
        // key stroke spec base key is not definitly upper case
        getRobot().keyStroke(keyStrokeSpec);
    }
 
    /**
     * Checks for the existence of a window with the given title
     * 
     * @param title
     *            the title
     * @param operator
     *            the comparing operator
     * @param exists
     *            <code>True</code> if the component is expected to exist and be
     *            visible, otherwise <code>false</code>.
     */
    public void gdCheckExistenceOfWindow(final String title,
            final String operator, final boolean exists) {
        IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        Boolean windowExists = (Boolean)queuer.invokeAndWait(
                "isWindowOpen", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return new Boolean(isWindowOpen(title, operator));
                    }
                });
        Verifier.equals(exists, windowExists.booleanValue());
    }
    
    /**
     * Waits <code>timeMillSec</code> if the application opens a window with the given title.
     * @param title the title
     * @param operator the comparing operator
     * @param timeout the time in ms
     * @param delay delay after the window is shown
     */
    public void gdWaitForWindow(final String title, final String operator, 
        int timeout, int delay) {

        final EventListener.Condition cond = 
            new WindowEventCondition(title, operator, false);
        final EventLock lock = new EventLock();
        final Listener listener = new EventListener(lock, cond);
        final Display display = 
            ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addWindowOpenedListeners", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                display.addFilter(SWT.Activate, listener);
                display.addFilter(SWT.Show, listener);
                if (isWindowOpen(title, operator)) {
                    lock.release();
                }
                
                return null;
            }
        });

        try {
            synchronized (lock) {
                long currentTimeout = timeout;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (currentTimeout > 0)) {
                    try {
                        lock.wait(currentTimeout);                    
                        now = System.currentTimeMillis();
                        currentTimeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }                    
            }
        } finally {
            queuer.invokeAndWait("removeWindowOpenedListeners", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    display.removeFilter(SWT.Activate, listener);
                    display.removeFilter(SWT.Show, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("window did not open", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.TIMEOUT_EXPIRED));
        }
        TimeUtil.delay(delay);
    }
    
    /**
     * Waits <code>timeMillSec</code> if the application activates a window
     * with the given title.
     * 
     * @param title the title
     * @param operator the comparing operator
     * @param timeout the time in ms
     * @param delay delay after the window is activated
     */
    public void gdWaitForWindowActivation(final String title, 
            final String operator, final int timeout, int delay) {
        
        final EventListener.Condition cond = 
            new WindowEventCondition(title, operator, false);
        final EventLock lock = new EventLock();
        final Listener listener = new EventListener(lock, cond);
        final Display display = 
            ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addWindowActiveListeners", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                display.addFilter(SWT.Activate, listener);
                if (isWindowActive(title, operator)) {
                    lock.release();
                }

                return null;
            }
        });
        
        try {
            synchronized (lock) {
                long currentTimeout = timeout;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (currentTimeout > 0)) {
                    try {
                        lock.wait(currentTimeout);                    
                        now = System.currentTimeMillis();
                        currentTimeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }                    
            }
        } finally {
            queuer.invokeAndWait("removeWindowActiveListeners", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    display.removeFilter(SWT.Activate, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("window was not activated", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.TIMEOUT_EXPIRED));
        }
        TimeUtil.delay(delay);
    }

    /**
     * Waits <code>timeMillSec</code> if the application closes (or hides) 
     * a window with the given title. If no window with the given title can
     * be found, then it is assumed that the window has already closed.
     * 
     * @param title the title
     * @param operator the comparing operator
     * @param timeout the time in ms
     * @param delay delay after the window is activated
     */
    public void gdWaitForWindowToClose(final String title, 
            final String operator, int timeout, int delay) {

        final EventListener.Condition cond = 
            new WindowEventCondition(title, operator, true);
        final EventLock lock = new EventLock();
        final Listener listener = new EventListener(lock, cond);
        final Display display = 
            ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addWindowClosedListeners", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                display.addFilter(SWT.Close, listener);
                display.addFilter(SWT.Hide, listener);
                display.addFilter(SWT.Dispose, listener);
                if (!isWindowOpen(title, operator)) {
                    lock.release();
                }
                
                return null;
            }
        });

        try {
            synchronized (lock) {
                long currentTimeout = timeout;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (currentTimeout > 0)) {
                    try {
                        lock.wait(currentTimeout);                    
                        now = System.currentTimeMillis();
                        currentTimeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }                    
            }
        } finally {
            queuer.invokeAndWait("removeWindowClosedListeners", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    display.removeFilter(SWT.Close, listener);
                    display.removeFilter(SWT.Hide, listener);
                    display.removeFilter(SWT.Dispose, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("window did not close", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.TIMEOUT_EXPIRED));
        }
        TimeUtil.delay(delay);
    }

    /**
     * Returns <code>true</code> if a window with the given title is open and 
     * visible
     * 
     * @param title the title
     * @param operator the matches/equals operator
     * @return if the window is open and visible
     */
    private boolean isWindowOpen(final String title, final String operator) {
        boolean wasInterrupted = false;
        boolean equal = false;
        do {
            try {
                wasInterrupted = false;
                Collection components = ComponentHandler
                    .getAutHierarchy().getHierarchyMap()
                        .keySet();
                for (Iterator it = components.iterator(); it.hasNext();) {
                
                    Widget comp = ((SwtComponent)it.next()).getRealComponent();
                    if (comp instanceof Shell 
                            && !comp.isDisposed()
                            && ((Shell)comp).isVisible()) {

                        Shell frame = (Shell)comp;
                        if (MatchUtil.getInstance().match(
                                frame.getText(), title, operator)) {

                            equal = true;
                            break;
                        }
                    }
                }

            } catch (ConcurrentModificationException e) {
                log.debug("hierarchy modified while traversing", e); //$NON-NLS-1$
                wasInterrupted = true;
            }
        } while (wasInterrupted);
        return equal;
    }

    /**
     * Returns <code>true</code> if a window with the given title is active
     * (the window with focus).
     * 
     * @param title the title
     * @param operator the matches/equals operator
     * @return if the window is open and visible
     */
    private boolean isWindowActive(final String title, final String operator) {
        final Shell activeWindow = getActiveWindow();
        
        if (activeWindow == null) {
            if (log.isWarnEnabled()) {
                log.warn("No active Window found while searching for Window with title: '" //$NON-NLS-1$
                        + String.valueOf(title) + "'! " + //$NON-NLS-1$
                    "(SwtApplicationImplClass#isWindowActive(String, String))"); //$NON-NLS-1$
            }
            return false;
        }
        
        final String windowTitle = activeWindow.getText();
        
        return MatchUtil.getInstance().match(windowTitle, title, operator);
    }

    /**
     * Just a server side method, not useable as action.
     * 
     * @param keyCode The key code
     */
    public void gdKeyType(int keyCode) {
        getRobot().keyType(null, keyCode);
    }

    /**
     * Just a server side method, not useable as action.
     * 
     * note : this action only works if application got focus,
     * because using defaultToolkit does not work. You have to
     * use component.getToolKit()s
     * @param key to set
     *      numlock Num Lock 1
     *      caplock Caps Lock 2 
     *      scolllock Scroll 3
     * @param activated 
     *      boolean
     */
    public void gdToggle(int key, boolean activated) {
        
        int event = 0;
        switch (key) {
            case 1 : 
                event = SWT.NUM_LOCK;
                break;
            case 2 : 
                event = SWT.CAPS_LOCK;
                break;
            case 3 : 
                event = SWT.SCROLL_LOCK;
                break;
            default : 
                break;
        }
        if (event != 0) {
            try {
                getRobot().keyToggle(FocusTracker.getFocusOwner(), 
                    event, activated);
            } catch (UnsupportedOperationException usoe) {
                throw new StepExecutionException(
                    TestErrorEvent.UNSUPPORTED_OPERATION_ERROR, 
                    EventFactory.createActionError(
                        TestErrorEvent.UNSUPPORTED_OPERATION_ERROR));
            } catch (OsNotSupportedException e) {
                throw new StepExecutionException(
                        TestErrorEvent.UNSUPPORTED_OPERATION_ERROR, 
                        EventFactory.createActionError(
                            TestErrorEvent.UNSUPPORTED_OPERATION_ERROR));
            }
        }
    }

    /**
     * Types <code>text</code> into the component. This replaces the shown
     * content.
     * @param text the text to type in
     * @deprecated Removed without substitution:
     * Testcases with this action are fragile, because this action assumes the
     * availabality of a text component. Any other case breaks the test.
     */
    public void gdReplaceText(String text) {
        // The number of clicks differs from the Swing implementation
        // because a double-click selects all of the text
        getRobot().click(FocusTracker.getFocusOwner(), null, 
            ClickOptions.create().setClickCount(2).left());
        if (StringConstants.EMPTY.equals(text)) {
            getRobot().keyStroke("DELETE"); //$NON-NLS-1$
        }
        gdInputText(text);
    }
    
    /**
     * @param text text to type
     */
    public void gdInputText(String text) {
        getRobot().move(FocusTracker.getFocusOwner(), null);
        getRobot().type(FocusTracker.getFocusOwner(), text);
    }

    /**
     * activate the AUT
     * 
     * @param method activation method
     */
    public void gdActivate(String method) {
        getRobot().activateApplication(method);
    }
    
    /**
     * clicks into the active window.
     * 
     * @param count amount of clicks
     * @param button what mouse button should be used
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException error
     */
    public void gdClickDirect(int count, int button, 
        int xPos, String xUnits, int yPos, String yUnits) 
        throws StepExecutionException {
        
        clickDirect(count, button, xPos, xUnits, yPos, yUnits);
    }
    
    /**
     * clicks into the active window.
     * 
     * @param count amount of clicks
     * @param button what button should be clicked
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException error
     */
    protected void clickDirect(int count, int button, int xPos, String xUnits, 
        int yPos, String yUnits) 
        throws StepExecutionException {
        
        final Shell activeWindow = getActiveWindow();
        
        if (activeWindow != null) {
            getRobot().click(activeWindow, null,
                ClickOptions.create().setClickCount(count)
                    .setScrollToVisible(false)
                    .setMouseButton(button), 
                xPos, 
                xUnits.equalsIgnoreCase(AbstractSwtImplClass.POS_UNIT_PIXEL), 
                yPos, 
                yUnits.equalsIgnoreCase(AbstractSwtImplClass.POS_UNIT_PIXEL));
        } else {
            throw new StepExecutionException("No active window.", //$NON-NLS-1$
                EventFactory.createActionError(
                    TestErrorEvent.NO_ACTIVE_WINDOW));
        }
    }

    /**
     * 
     * @return The active application window, or <code>null</code> if no 
     *         application window is currently active.
     */
    private Shell getActiveWindow() {
        Shell activeWindow = (Shell)getRobotFactory().getEventThreadQueuer()
        .invokeAndWait(this.getClass().getName() + ".getActiveWindow", //$NON-NLS-1$
            
            new IRunnable() {
                public Object run() { // SYNCH THREAD START
                    Display d = 
                        ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay();
                    return d.getActiveShell();
                    
                }
            }
                    
        );
        
        return activeWindow;
    }

    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getActiveWindowBounds() {
        org.eclipse.swt.graphics.Rectangle activeWindowSize = 
            (org.eclipse.swt.graphics.Rectangle)getRobotFactory()
                .getEventThreadQueuer().invokeAndWait(
                        this.getClass().getName() + ".getActiveWindowBounds", //$NON-NLS-1$
                        new IRunnable() {
                            public Object run() { // SYNCH THREAD START
                                Display d = ((SwtAUTServer)AUTServer
                                        .getInstance()).getAutDisplay();
                                if (d != null && d.getActiveShell() != null) {
                                    return d.getActiveShell().getBounds();
                                }
                                return null;
                            }
                        });
        if (activeWindowSize != null) {
            return SwtPointUtil.toAwtRectangle(activeWindowSize);
        }
        return null;
    }
}