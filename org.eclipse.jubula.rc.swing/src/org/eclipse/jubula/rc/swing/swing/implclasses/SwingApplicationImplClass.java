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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.OsNotSupportedException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.AbstractApplicationImplClass;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swing.components.SwingComponent;
import org.eclipse.jubula.rc.swing.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.swing.listener.ComponentHandler;
import org.eclipse.jubula.rc.swing.listener.FocusTracker;
import org.eclipse.jubula.rc.swing.swing.interfaces.ISwingApplicationImplClass;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.TimeUtil;


/**
 * Implementation class for general application-wide operations. The class is
 * mapped to a dummy component
 * {@link com.bredexsw.guidancer.autserver.swing.implclasses.GraphicApplication}.
 * 
 * @author BREDEX GmbH
 * @created 03.06.2005
 */
public class SwingApplicationImplClass extends AbstractApplicationImplClass 
    implements IImplementationClass, ISwingApplicationImplClass {
    
    /**
     * This condition is true if the event is an 'window opened' event
     * and the event source is a frame/dialog with a certain title.
     * It is also true if the event is a 'component shown' event and the 
     * event source is a frame/dialog with a certain title.
     */
    private static class WindowOpenedCondition 
        implements EventListener.Condition {
        /**
         * the title
         */
        private final String m_title;
        
        /** the matches operation */
        private final String m_operator;
        
        /**
         * constructor
         * 
         * @param title the title
         * @param operator the matches operation
         */
        public WindowOpenedCondition(String title, String operator) {
            m_title = title;
            m_operator = operator;
        }
        /**
         * {@inheritDoc}
         */
        public boolean isTrue(AWTEvent event) {
            if (event.getID() != WindowEvent.WINDOW_OPENED
                && event.getID() != ComponentEvent.COMPONENT_SHOWN) {
                return false;
            }
            if (event.getSource() instanceof Frame) {
                Frame frame = (Frame)event.getSource();
                return MatchUtil.getInstance().match(
                    frame.getTitle(), m_title, m_operator);
            } else if (event.getSource() instanceof Dialog) {
                Dialog dialog = (Dialog)event.getSource();
                return MatchUtil.getInstance().match(
                    dialog.getTitle(), m_title, m_operator);
            } else {
                return false;
            }
        }
    }
    
    /**
     * This condition is true if the event is an 'window activated' event
     * and the event source is a frame/dialog with a certain title.
     */
    private static class WindowActivatedCondition 
        implements EventListener.Condition {
        /**
         * the title
         */
        private final String m_title;
        
        /** the matches operation */
        private final String m_operator;
        
        /**
         * constructor
         * 
         * @param title the title
         * @param operator the matches operation
         */
        public WindowActivatedCondition(String title, String operator) {
            m_title = title;
            m_operator = operator;
        }
        /**
         * {@inheritDoc}
         */
        public boolean isTrue(AWTEvent event) {
            if (event.getID() != WindowEvent.WINDOW_ACTIVATED) {
                return false;
            }
            if (event.getSource() instanceof Frame) {
                Frame frame = (Frame)event.getSource();
                return MatchUtil.getInstance().match(
                    frame.getTitle(), m_title, m_operator);
            } else if (event.getSource() instanceof Dialog) {
                Dialog dialog = (Dialog)event.getSource();
                return MatchUtil.getInstance().match(
                    dialog.getTitle(), m_title, m_operator);
            } else {
                return false;
            }
        }
    }

    /**
     * This condition is true if the event is an 'window closed' event
     * and the event source is a frame/dialog with a certain title.
     * It is also true if the event is a 'component hidden' event and the 
     * event source is a frame/dialog with a certain title.
     */
    private static class WindowClosedCondition 
        implements EventListener.Condition {
        /**
         * the title
         */
        private final String m_title;
        
        /** the matches operation */
        private final String m_operator;
        
        /**
         * constructor
         * 
         * @param title the title
         * @param operator the matches operation
         */
        public WindowClosedCondition(String title, String operator) {
            m_title = title;
            m_operator = operator;
        }
        /**
         * {@inheritDoc}
         */
        public boolean isTrue(AWTEvent event) {
            if (event.getID() != WindowEvent.WINDOW_CLOSED
                && event.getID() != ComponentEvent.COMPONENT_HIDDEN) {
                return false;
            }
            if (event.getSource() instanceof Frame) {
                Frame frame = (Frame)event.getSource();
                return MatchUtil.getInstance().match(
                    frame.getTitle(), m_title, m_operator);
            } else if (event.getSource() instanceof Dialog) {
                Dialog dialog = (Dialog)event.getSource();
                return MatchUtil.getInstance().match(
                    dialog.getTitle(), m_title, m_operator);
            } else {
                return false;
            }
        }
    }

    /**
     * The logging.
     */
    private static AutServerLogger log = 
        new AutServerLogger(SwingApplicationImplClass.class);
    
    
    /**
     * The Robot factory.
     */
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
     * {@inheritDoc}
     */
    public void highLight(Component component, Color border) {
        // Nothing to be done
    }
    /**
     * {@inheritDoc}
     */
    public void lowLight(Component component) {
        // Nothing to be done
    }
    /**
     * perform a keystroke specified according <a
     * href=http://java.sun.com/j2se/1.4.2/docs/api/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String)>
     * string representation of a keystroke </a>,
     * 
     * @param modifierSpec the string representation of the modifiers
     * @param keySpec the string representation of the key
     */
    public void gdKeyStroke(String modifierSpec, String keySpec) {
        if (keySpec == null || keySpec.trim().length() == 0) {
            throw new StepExecutionException(
                "The base key of the key stroke must not be null or empty", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        String key = keySpec.trim().toUpperCase();
        String mod = KeyStrokeUtil.getModifierString(modifierSpec);
        if (mod.length() > 0) {
            getRobot().keyStroke(mod.toString() + " " + key); //$NON-NLS-1$
        } else {
            int code = getKeyCode(key);
            if (code != -1) {
                gdKeyType(code);
            } else {
                getRobot().keyStroke(key);
            }
        }
    }
 
    /**
     * Checks for the existence of a window with the given title
     * 
     * @param title
     *            the title
     * @param operator
     *            the comparing operator
     * @param exists
     *            <code>True</code> if the window is expected to exist and be
     *            visible, otherwise <code>false</code>.
     */
    public void gdCheckExistenceOfWindow(final String title, String operator,
            boolean exists) {
        Verifier.equals(exists, isWindowOpen(title, operator));
    }
    
    /**
     * Waits <code>timeMillSec</code> if the application opens a window
     * with the given title.
     * 
     * @param title the title
     * @param operator the comparing operator
     * @param pTimeout the time in ms
     * @param delay delay after the window is shown
     */
    public void gdWaitForWindow(final String title, String operator, 
        int pTimeout, int delay) {
        
        EventListener.Condition cond = 
            new WindowOpenedCondition(title, operator);
        EventLock lock = new EventLock();
        AWTEventListener listener = new EventListener(lock, cond);
        Toolkit.getDefaultToolkit().addAWTEventListener(listener,
                AWTEvent.WINDOW_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);

        if (isWindowOpen(title, operator)) {
            lock.release();
        }
        try {
            synchronized (lock) {
                long timeout = pTimeout;
                long done = System.currentTimeMillis() + timeout;
                long now;
                while (!lock.isReleased() && (timeout > 0)) {
                    try {
                        lock.wait(timeout);
                        now = System.currentTimeMillis();
                        timeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        } finally {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
        }
        if (!lock.isReleased() && !isWindowOpen(title, operator)) {
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
     * @param pTimeout the time in ms
     * @param delay delay after the window is activated
     */
    public void gdWaitForWindowActivation(final String title, String operator, 
        int pTimeout, int delay) {
        
        EventListener.Condition cond = new WindowActivatedCondition(title, 
                operator);
        EventLock lock = new EventLock();
        AWTEventListener listener = new EventListener(lock, cond);
        Toolkit.getDefaultToolkit().addAWTEventListener(listener,
                AWTEvent.WINDOW_EVENT_MASK);
        
        if (isWindowActive(title, operator)) {
            lock.release();
        }
        try {
            synchronized (lock) {
                long timeout = pTimeout;
                long done = System.currentTimeMillis() + timeout;
                long now;
                while (!lock.isReleased() && (timeout > 0)) {
                    try {
                        lock.wait(timeout);
                        now = System.currentTimeMillis();
                        timeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }

                }
            }
        } finally {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
        }
        if (!lock.isReleased() && !isWindowActive(title, operator)) {
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
     * @param pTimeout the time in ms
     * @param delay delay after the window is closed
     */
    public void gdWaitForWindowToClose(final String title, String operator, 
        int pTimeout, int delay) {
        
        EventListener.Condition cond = 
            new WindowClosedCondition(title, operator);
        EventLock lock = new EventLock();
        AWTEventListener listener = new EventListener(lock, cond);

        Toolkit.getDefaultToolkit().addAWTEventListener(listener,
                AWTEvent.WINDOW_EVENT_MASK);
        if (!isWindowOpen(title, operator)) {
            lock.release();
        }
        
        try {
            synchronized (lock) {
                long timeout = pTimeout;
                long done = System.currentTimeMillis() + timeout;
                long now;
                while (!lock.isReleased() && (timeout > 0)) {
                    try {
                        lock.wait(timeout);
                        now = System.currentTimeMillis();
                        timeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        } finally {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
        }
        if (!lock.isReleased() && isWindowOpen(title, operator)) {
            throw new StepExecutionException("window did not close", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.TIMEOUT_EXPIRED));
        }
        
        TimeUtil.delay(delay);
    }
    /**
     * Returns <code>true</code> if a window with the given title is open and 
     * visible.
     * 
     * @param title the title
     * @param operator the matches/equals operator
     * @return if the window is open and visible
     */
    private boolean isWindowOpen(String title, String operator) {
        boolean wasInterrupted;
        do {
            try {
                wasInterrupted = false;
                Collection components = ComponentHandler.getAutHierarchy()
                        .getHierarchyMap().keySet();
                for (Iterator it = components.iterator(); it.hasNext();) {
                    Component c = ((SwingComponent)it.next())
                        .getRealComponent();
                    if (c.isShowing()) {
                        if (c instanceof Frame) {
                            Frame frame = (Frame)c;
                            if (MatchUtil.getInstance().match(frame.getTitle(), 
                                title, operator)) {

                                return true;
                            }
                        }
                        if (c instanceof Dialog) {
                            Dialog dialog = (Dialog)c;
                            if (MatchUtil.getInstance().match(dialog.getTitle(),
                                title, operator)) {

                                return true;
                            }
                        }
                    }
                }
            } catch (ConcurrentModificationException e) {
                log.debug("hierarchy modified while traversing", e); //$NON-NLS-1$
                wasInterrupted = true;
            }
        } while (wasInterrupted);
        return false;
    }
    
    /**
     * Returns <code>true</code> if a window with the given title has focus
     * 
     * @param title the title
     * @param operator the matches/equals operator
     * @return if the window has focus
     */
    private boolean isWindowActive(String title, String operator) {
        
        Window activeWindow = WindowHelper.getActiveWindow();
        if (activeWindow != null) {
            String windowTitle = null;
            if (activeWindow instanceof Dialog) {
                windowTitle = ((Dialog)activeWindow).getTitle();
            } else if (activeWindow instanceof Frame) {
                windowTitle = ((Frame)activeWindow).getTitle();
            }
            
            if (MatchUtil.getInstance().match(windowTitle, title, operator)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param keyCodeName
     *            The name of a key code, e.g. <code>TAB</code> for a
     *            tabulator key code
     * @return The key code or <code>-1</code>, if the key code name doesn't
     *         exist in the <code>KeyEvent</code> class
     * @throws StepExecutionException
     *             If the key code name cannot be converted to a key code due to
     *             the reflection call
     */
    public int getKeyCode(String keyCodeName) throws StepExecutionException {
        int code = -1;
        String codeName = "VK_" + keyCodeName; //$NON-NLS-1$
        try {
            code = KeyEvent.class.getField(codeName).getInt(KeyEvent.class);
        } catch (IllegalArgumentException e) {
            throw new StepExecutionException(e.getMessage(), EventFactory
                .createActionError());
        } catch (SecurityException e) {
            throw new StepExecutionException(e.getMessage(), EventFactory
                .createActionError());
        } catch (IllegalAccessException e) {
            throw new StepExecutionException(e.getMessage(), EventFactory
                .createActionError());
        } catch (NoSuchFieldException e) {
            if (log.isInfoEnabled()) {
                log.info("The key expression '" + keyCodeName //$NON-NLS-1$
                    + "' is not a key code, typed as key stroke instead"); //$NON-NLS-1$
            }
        }
        return code;
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
                event = KeyEvent.VK_NUM_LOCK;
                break;
            case 2 : 
                event = KeyEvent.VK_CAPS_LOCK;
                break;
            case 3 : 
                event = KeyEvent.VK_SCROLL_LOCK;
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
        getRobot().click(FocusTracker.getFocusOwner(), null, 
            ClickOptions.create().setClickCount(3).left());
        if (StringConstants.EMPTY.equals(text)) {
            getRobot().keyStroke("DELETE"); //$NON-NLS-1$
        }
        gdInputText(text);
    }
    
    /**
     * @param text text to type
     */
    public void gdInputText(String text) {
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
        
        Window activeWindow = WindowHelper.getActiveWindow();
        if (activeWindow != null) {
            getRobot().click(activeWindow, null, 
                ClickOptions.create()
                    .setClickCount(count)
                    .setConfirmClick(false)
                    .setMouseButton(button), 
                xPos, 
                xUnits.equalsIgnoreCase(AbstractSwingImplClass.POS_UNIT_PIXEL), 
                yPos, 
                yUnits.equalsIgnoreCase(AbstractSwingImplClass.POS_UNIT_PIXEL));
        } else {
            throw new StepExecutionException("No active window.", //$NON-NLS-1$
                EventFactory.createActionError(
                    TestErrorEvent.NO_ACTIVE_WINDOW));
        }
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
        Window activeWindow = WindowHelper.getActiveWindow();
        if (activeWindow != null) {
            Rectangle activeWindowBounds = 
                new Rectangle(activeWindow.getBounds()); 
            activeWindowBounds.setLocation(activeWindow.getLocationOnScreen());
            
            return activeWindowBounds;
        }
        return null;
    }
}
