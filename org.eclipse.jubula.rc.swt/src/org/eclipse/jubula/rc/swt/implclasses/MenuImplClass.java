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

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.driver.RobotSwtImpl;
import org.eclipse.jubula.rc.swt.interfaces.IMenu;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.TimeUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;



/**
 * Implementation class for swt-Menu
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class MenuImplClass extends AbstractWidgetImplClass 
    implements IMenu {

    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(MenuImplClass.class);

    /**
     * Tries to select a menu item in a menu defined by a Text-Path
     * @param namePath the menu item to select
     * @param operator operator used for matching
     */
    public void selectMenuItem(String namePath, final String operator) {        
        final String[] pathItems = MenuUtil.splitPath(namePath);
        if (pathItems.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        
        final Menu menu = getAndCheckComponent();

        try {
            final MenuItem item = MenuUtil.navigateToMenuItem(getRobot(), menu, 
                pathItems, operator);
            if (item == null) {
                throwMenuItemNotFound();
            }
            
            Rectangle bounds = MenuUtil.getMenuItemBounds(item);
            Rectangle nullBounds = new Rectangle(0, 0, 0, 0);
            
            if (bounds.equals(nullBounds)) {
                MenuUtil.selectProgramatically(item);
            } else {
                MenuUtil.clickMenuItem(getRobot(), item, 1);
            }
        } catch (StepExecutionException e) {
            try {
                closeMenu(operator, pathItems);
            } catch (StepExecutionException e1) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
            throw e;
        }
        
    }

    
    /**
     * Tries to select a menu item in a menu defined by an Index-Path
     * @param indexPath the menu item to select
     */
    public void selectMenuItemByIndexpath(String indexPath) {
        final int[] indexItems = MenuUtil.splitIndexPath(indexPath);
        if (indexItems.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final Menu menu = getAndCheckComponent();

        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(),
                    menu, indexItems);
            if (menuItem == null) {
                throwMenuItemNotFound();
            }
            
            Rectangle bounds = MenuUtil.getMenuItemBounds(menuItem);
            Rectangle nullBounds = new Rectangle(0, 0, 0, 0);
            
            if (bounds.equals(nullBounds)) {
                MenuUtil.selectProgramatically(menuItem);
            } else {
                MenuUtil.clickMenuItem(getRobot(), menuItem, 1);
            }
        } catch (StepExecutionException e) {
            try {
                closeMenu(indexItems);
            } catch (StepExecutionException e1) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
            throwMenuItemNotFound();
        }
    }

    /**
     * Verifies if a MenuItem is en-/disabled depending on the enabled
     * parameter.
     * @param namePath the menu item to select
     * @param operator operator used for matching
     * @param enabled for checking enabled or disabled
     */
    public void verifyEnabled(String namePath, String operator, 
        boolean enabled) {
        
        final String[] pathItems = MenuUtil.splitPath(namePath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                getAndCheckComponent(), pathItems, operator);
            if (menuItem == null) {
                throwMenuItemNotFound();
            }
            final boolean isEnabled = MenuUtil.isMenuItemEnabled(menuItem);
            Verifier.equals(enabled, isEnabled);
        } finally {
            try {
                closeMenu(operator, pathItems);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
        }
    }
    
    /**
     * Verifies if a MenuItem is en-/disabled depending on the enabled
     * parameter.
     * @param indexPath the menu item to select
     * @param enabled for checking enabled or disabled
     */
    public void verifyEnabledByIndexpath(String indexPath, boolean enabled) {
        final int[] indexItems = MenuUtil.splitIndexPath(indexPath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                getAndCheckComponent(), indexItems);
            if (menuItem == null) {
                throwMenuItemNotFound();
            }
            final boolean isEnabled = MenuUtil.isMenuItemEnabled(menuItem);
            Verifier.equals(enabled, isEnabled);
        } finally {
            try {
                closeMenu(indexItems);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
        }
    }


    /**
     * @param indexItems the indices of the MenuItems
     */
    protected void closeMenu(final int[] indexItems) {
        MenuUtil.closeMenu(getRobot(), getAndCheckComponent(), 
            indexItems[0], indexItems.length);
    }
    
    /**
     * Verifies if the specified menu item exists
     * @param namePath the menu item to verify against 
     * @param operator operator operator used for matching
     * @param exists for checking existence or unexistence
     */
    public void verifyExists(String namePath, String operator, boolean exists) {
        final String[] pathItems = MenuUtil.splitPath(namePath);
        boolean isExisting = false;
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                getAndCheckComponent(), pathItems, operator);
            if (menuItem != null) {
                isExisting = true;
            }
        } finally {
            try {
                closeMenu(operator, pathItems);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
        }
        Verifier.equals(exists, isExisting);
    }


    /**
     * @param operator operator used for matching
     * @param pathItems the names of the MenuItems
     */
    protected void closeMenu(String operator, final String[] pathItems) {
        MenuUtil.closeMenu(getRobot(), getAndCheckComponent(), 
            pathItems[0], operator, pathItems.length);
    }
    
    /**
     * Verifies if the specified menu item exists
     * @param indexPath the menu item to select
     * @param exists for checking existence or unexistence
     */
    public void verifyExistsByIndexpath(String indexPath, boolean exists) {
        final int[] indexItems = MenuUtil.splitIndexPath(indexPath);
        boolean isExisting = false;
        final Menu menu = getAndCheckComponent();
        try {
            MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), menu, 
                indexItems);
            if (menuItem != null) {
                isExisting = true;
            }
        } finally {
            try {
                closeMenu(indexItems);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
        }
        Verifier.equals(exists, isExisting);
    }
    
    /**
     * Checks if the specified menu item is selected. 
     * @param namePath the menu item to verify against
     * @param operator operator used for matching
     * @param selected for checking selected or not selected
     */
    public void verifySelected(String namePath, String operator, 
        boolean selected) {
        
        final String[] pathItems = MenuUtil.splitPath(namePath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                getAndCheckComponent(), pathItems, operator);
            if (menuItem == null) {
                throwMenuItemNotFound();
            }
            final boolean isSelected = MenuUtil.isMenuItemSelected(menuItem);
            Verifier.equals(selected, isSelected);
        } finally {
            try {
                closeMenu(operator, pathItems);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
        }
    }
    
    /**
     * Checks if the specified menu item is selected.
     * @param indexPath the menu item to verify against
     * @param selected for checking selected or not selected
     */
    public void verifySelectedByIndexpath(String indexPath, boolean selected) {
        final int[] indexItems = MenuUtil.splitIndexPath(indexPath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                getAndCheckComponent(), indexItems);
            if (menuItem == null) {
                throwMenuItemNotFound();
            }
            final boolean isSelected = MenuUtil.isMenuItemSelected(menuItem);
            Verifier.equals(selected, isSelected);
        } finally {
            try {
                closeMenu(indexItems);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
                if (log.isInfoEnabled()) {
                    log.info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
        }
    }
    
    
    /**
     * Overridden from superclass.<br>
     * Gets the Menu from the active Shell, or <code>null</code> if there is .
     * {@inheritDoc}
     */
    public Widget getComponent() {
        final Shell shell = ((RobotSwtImpl)getRobot()).getActiveWindow();
        if (shell == null) {
            setComponent(null);
        } else {
            final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
            
            queuer.invokeAndWait("setMenuBarComponent", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    Menu menu = shell.getMenuBar();
                    setComponent(menu);
                    
                    return null;
                }
            });
        }
        return super.getComponent();
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
     * 
     */
    private void throwMenuItemNotFound() {
        throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
            EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
    }
    
    /**
     * Tries to find the menu bar for the currently active window.
     * 
     * @return the menu bar for the currently active window
     * @throws StepExecutionException if there is no active window or if the 
     *                                active window has no menu bar.
     */
    private Menu getAndCheckComponent() throws StepExecutionException {
        // Verify that there is an active window
        if (((RobotSwtImpl)getRobot()).getActiveWindow() == null) {
            throw new StepExecutionException(
                I18n.getString(TestErrorEvent.NO_ACTIVE_WINDOW), 
                EventFactory.createActionError(
                    TestErrorEvent.NO_ACTIVE_WINDOW));
        }

        Menu menu = (Menu)getComponent();
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
        final String[] componentTextArray;
        Menu menu = (Menu)getComponent();
        if (menu == null) {
            componentTextArray = null;
        } else {
            Item[] itemArray = menu.getItems();
            componentTextArray = getTextArrayFromItemArray(itemArray);         
        }
        return componentTextArray;
    }
    
}