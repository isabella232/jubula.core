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
package org.eclipse.jubula.rc.swt.uiadapter;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IMenuAdapter;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IMenuItemAdapter;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Implements the Menu interface for adapting a <code>SWT.Menu</code>
 * 
 * @author BREDEX GmbH
 */
public class MenuAdapter extends AbstractComponentAdapter
    implements IMenuAdapter {
    /** the Menu from the AUT */
    private Menu m_menu;    

    /**
     * 
     * @param component graphics component which will be adapted
     */
    public MenuAdapter(Object component) {
        m_menu = (Menu) component;
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
        return m_menu;
    }
    /**
     * {@inheritDoc}
     */  
    public void setComponent(Object element) {
        m_menu = (Menu) element;
        
    }
    /**
     * {@inheritDoc}
     */
    public IMenuItemAdapter[] getItems() {
        
        MenuItem[] items =
                (MenuItem[]) getEventThreadQueuer().invokeAndWait(
                        "getItems", new IRunnable() { //$NON-NLS-1$
                            public Object run() {
                                return m_menu.getItems();
                            }
                        });
        IMenuItemAdapter[] adapters = new IMenuItemAdapter[items.length];
        for (int i = 0; i < items.length; i++) {
            IMenuItemAdapter menuItem = new MenuItemAdapter(items[i]);
            adapters[i] = menuItem;
        }
        return adapters;
    }
    /**
     * {@inheritDoc}
     */
    public int getItemCount() {
        Integer itemCount = (Integer) getEventThreadQueuer().invokeAndWait(
                "getItemCount", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return new Integer(m_menu.getItemCount());
                    }
                });
        return itemCount.intValue();
    }
}
