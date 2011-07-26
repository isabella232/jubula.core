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

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.constants.TimeoutConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;


/**
 * @author BREDEX GmbH
 * @created 09.05.2007
 */
public class PopupMenuUtil {

    /**
     * Is true, if a popup menu is shown 
     */
    private static class PopupShownCondition implements
            EventListener.Condition {

        /**
         * the popup menu
         */
        private Menu m_popup = null;
        
        /**
         * 
         * @return the popup menu
         */
        public Menu getPopup() {
            return m_popup;
        }
        
        /**
         * {@inheritDoc}
         * @param event event
         * @return result of the condition
         */
        public boolean isTrue(Event event) {

            if (event.type == SWT.Show && event.widget instanceof Menu) {
                m_popup = (Menu)event.widget;
                return true;
            } 
            
            return false;
        }
    }

    /**
     * Private constructor for utility class.
     */
    private PopupMenuUtil() {
        // Nothing
    }
    
    /**
     * Shows and returns the popup menu
     * @param component The component for which to open the popup menu.
     * @param robot The robot to use for the click operations.
     * @param button MouseButton
     * @return the popup menu
     */
    public static Menu showPopup(final Widget component, final IRobot robot, 
            final int button) {
        if (SwtUtils.isMouseCursorInWidget(component)) {
            return showPopup(component, new Runnable() {
                public void run() {
                    RobotTiming.sleepPreShowPopupDelay();
                    
                    robot.clickAtCurrentPosition(component, 1, 
                            button);
                }
            });
        }
        return showPopup(
            component, robot, 50, AbstractSwtImplClass.POS_UNI_PERCENT, 50, 
            AbstractSwtImplClass.POS_UNI_PERCENT, button);
    }

    /**
     * Shows and returns the popup menu
     * 
     * @param component The component for which to open the popup menu.
     * @param robot The robot to use for the click operations.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param button MouseButton
     * @return the popup menu
     * @throws StepExecutionException error
     */
    public static Menu showPopup(final Widget component, final IRobot robot, 
            final int xPos, final String xUnits, final int yPos, 
            final String yUnits, 
            final int button) throws StepExecutionException {

        return showPopup(component, new Runnable() {
            public void run() {
                RobotTiming.sleepPreShowPopupDelay();
                boolean isAbsoluteUnitsX = 
                    AbstractSwtImplClass.POS_UNIT_PIXEL.equalsIgnoreCase(
                            xUnits);
                boolean isAbsoluteUnitsY = 
                    AbstractSwtImplClass.POS_UNIT_PIXEL.equalsIgnoreCase(
                            yUnits);
                robot.click(component, null, 
                    ClickOptions.create().setClickCount(1)
                        .setMouseButton(button), 
                    xPos, isAbsoluteUnitsX, yPos, isAbsoluteUnitsY);
            }
        });
    }

    /**
     * Shows and returns the popup menu
     * 
     * @param component The component for which to open the popup menu.
     * @param showPopup A <code>Runnable</code> that, when run, should display
     *                  a popup menu for the given component.
     * @return the popup menu
     * @throws StepExecutionException error
     */
    private static Menu showPopup(final Widget component, 
        final Runnable showPopup) throws StepExecutionException {

        PopupShownCondition cond = new PopupShownCondition();
        EventLock lock = new EventLock();
        final EventListener listener = new EventListener(lock, cond);
        final Display d = component.getDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addPopupShownListeners", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                d.addFilter(SWT.Show, listener);
                
                return null;
            }
        });
        
        try {
            // showPopup must run in the current thread in order to
            // avoid a race condition.
            showPopup.run();

            synchronized (lock) {
                long timeout = TimeoutConstants.SERVER_TIMEOUT_WAIT_FOR_POPUP;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (timeout > 0)) {
                    lock.wait(timeout);
                    now = System.currentTimeMillis();
                    timeout = done - now;
                }
            } 
        } catch (InterruptedException e) {
            // ignore
        } finally {
            queuer.invokeAndWait("removePopupShownListeners", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    d.removeFilter(SWT.Show, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("popup not shown", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.POPUP_NOT_FOUND));
        }
        return cond.getPopup();
    }

    /**
     * Select a menu item
     *
     * @param robot The robot to use for the click operations.
     * @param popup popup menu
     * @param path path to the menu item
     */
    public static void selectMenuItem(final IRobot robot, Menu popup, 
        int[] path) {
        if (path.length == 0) {
            throw new StepExecutionException(
                    "empty path to menuitem not allowed", //$NON-NLS-1$
                    EventFactory.createActionError());
        }
        MenuItem item = MenuUtil.navigateToMenuItem(robot, popup, path);
        selectItem(item, robot);
    }

    /**
     * Select a menu item
     *
     * @param robot The robot to use for the click operations.
     * @param popup popup menu
     * @param path path to the menu item
     * @param operator operator used for matching
     */
    public static void selectMenuItem(final IRobot robot, Menu popup,
            String[] path, String operator) {
        if (path.length == 0) {
            throw new StepExecutionException(
                    "empty path to menuitem not allowed", //$NON-NLS-1$
                    EventFactory.createActionError());
        }
        MenuItem item = MenuUtil.navigateToMenuItem(robot, popup, path,
                operator);
        selectItem(item, robot);
    }

    /**
     * @param item
     *            the item to select; may be <code>null</code>
     * @param robot
     *            The robot to use for the click operations.
     */
    private static void selectItem(MenuItem item, IRobot robot) {
        if (item == null) {
            throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }

        if (EnvironmentUtils.isMacOS()) {
            MenuUtil.selectProgramatically(item);
            // Press 'ESC' to close menu - for some reason the popup menu itself
            // is already disposed at this point
            robot.keyType(null, SWT.ESC);
        } else {
            MenuUtil.clickMenuItem(robot, item, 1);
        }
    }

    /**
     * @param robot the IRobot
     * @param popup the popup/dropdown menu to close
     * @param pathLength The length of the path to the requested menu entry.
     *                   This is used to determine the maximum number of
     *                   menus that need to be closed.
     */
    public static void closePopup(IRobot robot, Menu popup, int pathLength) {
        for (int i = 0; i < pathLength && popup != null
                && MenuUtil.isMenuVisible(popup); i++) {

            // Press 'ESC' to close menu
            robot.keyType(popup, SWT.ESC);

            RobotTiming.sleepPostMouseUpDelay();
        }
    }
}
