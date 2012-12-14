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

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swing.swing.interfaces.IJMenuBarDefaultMapping;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.TimeUtil;


/**
 * Implementation class for <code>JMenuBar</code>.
 * @author BREDEX GmbH
 * @created 12.04.2005
 */
public class JMenuBarImplClass extends AbstractSwingImplClass 
    implements IJMenuBarDefaultMapping {

    /** the logger */
    private static AutServerLogger log =
        new AutServerLogger(JMenuBarImplClass.class);

    /** the menu bar which should be inspected. */
    private JMenuBar m_menuBar;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        this.m_menuBar = (JMenuBar)graphicsComponent;
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {

        Window activeWindow = WindowHelper.getActiveWindow();
        if (activeWindow == null) {
            log.warn("JMenuBarImplClass.getComponent(): No active window."); //$NON-NLS-1$
        } else {
            JMenuBar menuBar = null;
            Container rootPane = null;
            if (activeWindow instanceof JDialog) {
                JDialog dialog = (JDialog)activeWindow;
                menuBar = dialog.getJMenuBar();
                rootPane = dialog.getRootPane();
            } else if (activeWindow instanceof JFrame) {
                JFrame frame = (JFrame)activeWindow;
                menuBar = frame.getJMenuBar();
                rootPane = frame.getRootPane();
            }

            if (menuBar == null) {
                menuBar = getMenuBarWorkaround(rootPane);
            }

            setComponent(menuBar);
        }
        return m_menuBar;
    }

    /**
     * implementation for "wait for component"
     * @param timeout the maximum amount of time to wait for the component
     * @param delay the time to wait after the component is found
     */
    public void waitForComponent(int timeout, int delay) {
        if (getComponent() == null) {
            long start = System.currentTimeMillis();
            do {
                RobotTiming.sleepWaitForComponentPollingDelay();
            } while (System.currentTimeMillis() - start < timeout
                    && getComponent() == null);
            if (getComponent() == null) {
                throw new StepExecutionException("No Menubar found.", //$NON-NLS-1$
                        EventFactory.createComponentNotFoundErrorEvent());
            }
        }
        TimeUtil.delay(delay);
    }
    
    /**
     * Tries to select a menu item in a menu.
     * @param menuItem the menu item to select
     * @param operator operator used for matching
     */
    public void selectMenuItem(String menuItem, String operator) {
        selectMenuItem(MenuUtil.splitPath(menuItem), operator);
    }

    /**
     * Tries to select a menu item in a menu.
     * @param menuItem the menu item to select
     * @param operator operator used for matching
     */
    public void selectMenuItem(String[] menuItem, String operator) {
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        JMenuItem item = MenuUtil.navigateToMenuItem(
                getRobot(), getAndCheckMenu(),
                menuItem, operator);
        if (item == null) {
            try {
                MenuUtil.closeMenu(getRobot(), getAndCheckMenu(), menuItem[0]);
            } catch (StepExecutionException see) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
            }
            throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        MenuUtil.clickMenuItem(getRobot(), item);
    }

    /**
     * Tries to select a menu item in a menu.
     * @param path path to the menu item
     */
    public void selectMenuItemByIndexpath(String path) {
        selectMenuItemByIndexpath(MenuUtil.splitIndexPath(path));
    }

    /**
     * Tries to select a menu item in a menu.
     * @param path path to the menu item
     */

    public void selectMenuItemByIndexpath(int[] path) {
        if (path.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        JMenuItem item = 
            MenuUtil.navigateToMenuItem(getRobot(), getAndCheckMenu(), path);
        if (item == null) {
            try {
                MenuUtil.closeMenu(getRobot(), getAndCheckMenu(), path[0]);
            } catch (StepExecutionException see) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
            }
            throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        MenuUtil.clickMenuItem(getRobot(), item);
    }

    /**
     * Verifies if the specified menu item exists
     * @param menuItem the menu item to verifiy against
     * @param operator operator used for matching
     * @param exists should the menu item exist?
     */
    public void verifyExists(String menuItem, String operator, boolean exists) {
        verifyExists(MenuUtil.splitPath(menuItem), operator, exists);
    }

    /**
     * Verifies if the specified menu item exists
     * @param menuItem the menu item to verify against
     * @param operator operator used for matching
     * @param exists should the menu item exist?
     */
    public void verifyExists(String[] menuItem,
                             String operator,
                             boolean exists) {
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_PARAM_VALUE));
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                getAndCheckMenu(), menuItem, operator);
        try {
            verify(exists, JMenuBarImplClass.class.getName() + ".verifyExists", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem))
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            JMenu menu = 
                MenuUtil.findMenu(getAndCheckMenu(), menuItem[0], operator);
            if ((menu != null) && (menu.isPopupMenuVisible())) {
                getRobot().click(menu, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
            }
        }
    }

    /**
     * Verifies if the specified menu item exists
     * @param menuItem the menu item to verifiy against
     * @param exists should the menu item exist?
     */
    public void verifyExistsByIndexpath(String menuItem, boolean exists) {
        verifyExistsByIndexpath(MenuUtil.splitIndexPath(menuItem), exists);
    }

    /**
     * Verifies if the specified menu item exists
     * @param menuItem the menu item to verify against
     * @param exists should the menu item exist?
     */
    public void verifyExistsByIndexpath(int[] menuItem, boolean exists) {
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                getAndCheckMenu(), menuItem);
        try {
            verify(exists, JMenuBarImplClass.class.getName() + ".verifyExists", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem))
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            JMenu menu = MenuUtil.findMenu(getAndCheckMenu(), menuItem[0]);
            if ((menu != null) && (menu.isPopupMenuVisible())) {
                getRobot().click(menu, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
            }
        }
    }

    /**
     * Checks if the specified menu item is enabled.
     * @param menuItem the menu item as a text path to verify against
     * @param operator operator used for matching
     * @param enabled is the specified menu item enabled?
     */
    public void verifyEnabled(String menuItem,
                              String operator,
                              boolean enabled) {
        verifyEnabled(MenuUtil.splitPath(menuItem), operator, enabled);
    }

    /**
     * Checks if the specified menu item is enabled.
     * @param menuItem the menu item to verify against
     * @param operator operator used for matching
     * @param enabled is the specified menu item enabled?
     */
    public void verifyEnabled(String[] menuItem,
                              String operator,
                              boolean enabled) {
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                getAndCheckMenu(), menuItem, operator);
        try {
            verify(enabled, JMenuBarImplClass.class.getName() + ".verifyEnabled", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && item.isEnabled()) ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            JMenu menu = 
                MenuUtil.findMenu(getAndCheckMenu(), menuItem[0], operator);
            if ((menu != null) && (menu.isPopupMenuVisible())) {
                getRobot().click(menu, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
            }
        }
    }

    /**
     * Checks if the specified menu item is enabled.
     * @param menuItem the menu item as a text path to verify against
     * @param enabled is the specified menu item enabled?
     */
    public void verifyEnabledByIndexpath(String menuItem, boolean enabled) {
        verifyEnabledByIndexpath(MenuUtil.splitIndexPath(menuItem), enabled);
    }

    /**
     * Checks if the specified menu item is enabled.
     * @param menuItem the menu item to verify against
     * @param enabled is the specified menu item enabled?
     */
    public void verifyEnabledByIndexpath(int[] menuItem, boolean enabled) {
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                getAndCheckMenu(), menuItem);
        try {
            verify(enabled, JMenuBarImplClass.class.getName() + ".verifyEnabled", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && item.isEnabled()) ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            JMenu menu = MenuUtil.findMenu(getAndCheckMenu(), menuItem[0]);
            if ((menu != null) && (menu.isPopupMenuVisible())) {
                getRobot().click(menu, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
            }
        }
    }

    /**
     * Checks if the specified menu item is selected.
     * @param menuItem the menu item to verify against
     * @param operator operator used for matching
     * @param selected is the specified menu item selected?
     */
    public void verifySelected(String menuItem,
                               String operator,
                               boolean selected) {
        verifySelected(MenuUtil.splitPath(menuItem), operator, selected);
    }

    /**
     * Checks if the specified menu item is selected.
     * @param menuItem the menu item to verify against
     * @param operator operator used for matching
     * @param selected is the specified menu item selected?
     */
    public void verifySelected(String[] menuItem,
                               String operator,
                               boolean selected) {
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                getAndCheckMenu(), menuItem, operator);
        try {
            verify(selected, JMenuBarImplClass.class.getName() + ".verifySelected", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && ((JMenuItem)item).isSelected())
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            JMenu menu = MenuUtil.findMenu(
                    getAndCheckMenu(), menuItem[0], operator);
            if ((menu != null) && (menu.isPopupMenuVisible())) {
                getRobot().click(menu, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
            }
        }
    }
    /**
     * Checks if the specified menu item is selected.
     * @param menuItem the menu item to verify against
     * @param selected is the specified menu item selected?
     */
    public void verifySelectedByIndexpath(String menuItem, boolean selected) {
        verifySelectedByIndexpath(MenuUtil.splitIndexPath(menuItem), selected);
    }

    /**
     * Checks if the specified menu item is selected.
     * @param menuItem the menu item to verify against
     * @param selected is the specified menu item selected?
     */
    public void verifySelectedByIndexpath(int[] menuItem, boolean selected) {
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                getAndCheckMenu(), menuItem);
        try {
            verify(selected, JMenuBarImplClass.class.getName() + ".verifySelected", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && ((JMenuItem)item).isSelected())
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            JMenu menu = MenuUtil.findMenu(getAndCheckMenu(), menuItem[0]);
            if ((menu != null) && (menu.isPopupMenuVisible())) {
                getRobot().click(menu, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
            }
        }
    }

    /**
     *
     * @return the JMenuBar.
     * @throws StepExecutionException if the active window has no menu bar.
     */
    private JMenuBar getAndCheckMenu() throws StepExecutionException {
        JMenuBar menu = (JMenuBar)getComponent();
        // Verify that the active window has a menu bar
        if (menu == null) {
            throw new StepExecutionException(
                I18n.getString(TestErrorEvent.NO_MENU_BAR), 
                EventFactory.createActionError(
                    TestErrorEvent.NO_MENU_BAR));
        }
        return menu;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        List visibleMenuList = new ArrayList();
        MenuElement [] subElements = m_menuBar.getSubElements();
        for (int i = 0; i < subElements.length; i++) {
            if (subElements[i] instanceof JMenu) {
                JMenu menu = (JMenu)subElements[i];
                if ((menu != null) && !menu.isShowing()) {
                    visibleMenuList.add(menu);
                }
            }
        }

        JMenu[] visibleMenuArray =
            (JMenu [])visibleMenuList.toArray(
                    new JMenu[visibleMenuList.size()]);
        final String[] componentTextArray = new String[visibleMenuArray.length];

        for (int i = 0; i < componentTextArray.length; i++) {
            JMenu menu = visibleMenuArray[i];
            if (menu == null) {
                componentTextArray[i] = null;
            } else {
                componentTextArray[i] = menu.getText();
            }
        }
        return componentTextArray;
    }

    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return always null
     */
    protected String getText() {
        return null;
    }

    /**
     * Workaround to get the menu bar existing somewhere in the given 
     * container's hierarchy. This method should <b>only</b> be used if 
     * {@link JFrame#getJMenuBar()} / {@link JDialog#getJMenuBar()} return 
     * <code>null</code>, which is a very rare case.
     * 
     * This method also performs some unorthodox visibility testing in order
     * to avoid retrieving the wrong menu.
     * 
     * @param rootPane The root container from which to start the search for
     *                 the menu bar.
     * @return the first menu bar found in the hierarchy that: <ul>
     *          <li>is showing</li>
     *          <li>contains at least one visible menu</li>
     */
    private JMenuBar getMenuBarWorkaround(Container rootPane) {
        JMenuBar menuBar = null;
        List menuList = new ArrayList();
        collectMenuBarsWorkaround(rootPane, menuList);
        Iterator menuIter = menuList.iterator();
        while (menuIter.hasNext() && menuBar == null) {
            JMenuBar menu = (JMenuBar)menuIter.next();
            boolean hasAtLeastOneItem = false;
            MenuElement [] subElements = menu.getSubElements();
            for (int i = 0; 
                    i < subElements.length && !hasAtLeastOneItem; 
                    i++) {
                if (subElements[i] instanceof JMenu) {
                    JMenu subMenu = (JMenu)subElements[i];
                    hasAtLeastOneItem = 
                        subMenu != null && subMenu.isShowing();
                }
            }
            if (hasAtLeastOneItem) {
                menuBar = menu;
            }
        }
        return menuBar;
    }

    /**
     * Adds all menu bars found in the hierarchy <code>container</code> to 
     * <code>menuBarList</code>. This is part of a workaround for finding menus
     * in AUTs that don't make proper use of 
     * {@link JFrame#setJMenuBar()} / {@link JDialog#setJMenuBar()}.
     * 
     * @see #getMenuBarWorkaround(Container)
     * 
     * @param container The root container from which to start the search for
     *                  the menu bars. 
     * @param menuBarList The list to which each menu bar found will be added.
     *                    Only objects of type {@link JMenuBar} will be added
     *                    to this list.
     */
    private void collectMenuBarsWorkaround(
            Container container, List menuBarList) {
        Component [] children = container.getComponents();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof JMenuBar
                    && children[i].isShowing()) {
                menuBarList.add(children[i]);
            }
        }
        
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Container
                    && children[i].isVisible()) {
                collectMenuBarsWorkaround((Container)children[i], menuBarList);
            }
        }
        
    }

}