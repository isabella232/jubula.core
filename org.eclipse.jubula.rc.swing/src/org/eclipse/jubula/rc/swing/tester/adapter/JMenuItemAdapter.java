/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.tester.adapter;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Implementation of the MenuItem interface for adapting <code>JMenuItem</code>.
 * @author BREDEX GmbH
 * 
 */
public class JMenuItemAdapter extends AbstractComponentAdapter
    implements IMenuItemComponent {

    /** the JMenuItem from the AUT    */
    private JMenuItem m_menuItem;

    /**
     * 
     * @param objectToAdapt 
     */
    public JMenuItemAdapter(Object objectToAdapt) {
        m_menuItem = (JMenuItem) objectToAdapt;        
    }

    /**
     * Gets the IEventThreadQueuer.
     *
     * @return The Robot
     * @throws RobotException
     *             If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return getRobotFactory().getRobot();
    }
    /**
     * @return The event thread queuer.
     */
    public IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    

    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {

        return m_menuItem;
    }

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object element) {
        m_menuItem = (JMenuItem) element;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        Boolean actual = (Boolean) getEventThreadQueuer().invokeAndWait(
                "isEnabled", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        // see findBugs
                        return ((m_menuItem != null)
                                && m_menuItem.isEnabled()) 
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                });

        return actual.booleanValue();
    }
    /**
     * {@inheritDoc}
     */
    public String getText() {
        return (String) getEventThreadQueuer().invokeAndWait(
                "getText", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_menuItem.getText();
                    }
                });
    }
    /**
     * {@inheritDoc}
     */
    public boolean isShowing() {
        Boolean actual = (Boolean) getEventThreadQueuer().invokeAndWait(
                "isShowing", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        // see findBugs
                        return ((m_menuItem != null)
                                && m_menuItem.isShowing()) 
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                });

        return actual.booleanValue();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isExisting() {
        if (m_menuItem != null) {
            return true;
        }
        return false;
    }
    /**
     * {@inheritDoc}
     */
    public boolean isSelected() {
        Boolean actual = (Boolean) getEventThreadQueuer().invokeAndWait(
                "isSelected", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return ((m_menuItem != null)
                                && m_menuItem.isSelected()) 
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                });

        return actual.booleanValue();
    }
    
    /**
     * {@inheritDoc}
     */
    public IMenuComponent getMenu() {
        if (m_menuItem instanceof JMenu) {
            return new JMenuAdapter(m_menuItem);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasSubMenu() {
        if (m_menuItem.getSubElements().length > 0) {
            return true;
        }
        return false;
    }
    /**
     * {@inheritDoc}
     */
    public boolean isSeparator() {
        if (m_menuItem == null) {
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void selectMenuItem() {
        clickMenuItem(getRobot(), m_menuItem);
        
    }
    
    /**
     * {@inheritDoc}
     */
    public IMenuComponent openSubMenu() {
        if (!m_menuItem.isEnabled()) {
            throw new StepExecutionException("menu item not enabled", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.MENU_ITEM_NOT_ENABLED));
        }
        if (!(m_menuItem instanceof JMenu)) {
            throw new StepExecutionException("unexpected item found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        JMenu menu = (JMenu) m_menuItem;        
        clickMenuItem(getRobot(), m_menuItem);
        RobotTiming.sleepPostShowSubMenuItem(menu.getDelay());
        return getMenu();
    }
    
    /**
     * Clicks on a menu item
     * 
     * @param robot the robot
     * @param item  the menu item
     */
    private void clickMenuItem(IRobot robot, JMenuItem item) {
        if (!item.isEnabled()) {
            throw new StepExecutionException("menu item not enabled", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.MENU_ITEM_NOT_ENABLED));
        }
        if (item.getParent() instanceof JPopupMenu 
                && ((JPopupMenu)item.getParent())
                .getInvoker().getParent() instanceof JMenuBar) {
            
            robot.click(item, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED).setFirstHorizontal(false));
        } else {
            robot.click(item, null, ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
        }
    }
}
