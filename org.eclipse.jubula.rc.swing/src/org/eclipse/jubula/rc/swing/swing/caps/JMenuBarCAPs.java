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
package org.eclipse.jubula.rc.swing.swing.caps;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import org.eclipse.jubula.rc.common.caps.AbstractMenuCAPs;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IComponentAdapter;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IMenuAdapter;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IMenuItemAdapter;
import org.eclipse.jubula.rc.swing.swing.implclasses.WindowHelper;
import org.eclipse.jubula.rc.swing.swing.uiadapter.JMenuItemAdapter;

/**
 * Toolkit specific commands for the <code>JMenuBar</code>.
 * 
 * @author BREDEX GmbH
 */
public class JMenuBarCAPs extends AbstractMenuCAPs {
            
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        
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
    /**
     * @return the component
     */
    public IComponentAdapter getComponent() {
        if (super.getComponent().getRealComponent() instanceof JPopupMenu) {
            return super.getComponent();
        }
        Window activeWindow = WindowHelper.getActiveWindow();
        if (activeWindow == null) {
            getLog().warn("JMenuBarImplClass.getComponent(): No active window."); //$NON-NLS-1$
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
        return super.getComponent();
    }

    /**
     *{@inheritDoc}
     */
    protected void closeMenu(IMenuAdapter menuBar, String[] textPath,
            String operator) {
        if (menuBar.getRealComponent() instanceof JPopupMenu) {
            for (int i = 0; i < textPath.length; i++) {
                getRobot().keyType(menuBar.getRealComponent(),
                        KeyEvent.VK_ESCAPE);
            }
            return;
        }
        super.closeMenu(menuBar, textPath, operator);
            
    }
    
    /**
     *{@inheritDoc}
     */
    protected void closeMenu(IMenuAdapter menuBar, int[] path) {
        if (menuBar.getRealComponent() instanceof JPopupMenu) {
            for (int i = 0; i < path.length; i++) {
                getRobot().keyType(menuBar.getRealComponent(),
                        KeyEvent.VK_ESCAPE);
            }
            return;
        }
        super.closeMenu(menuBar, path);
    }
    
    /**
     * {@inheritDoc}
     */
    protected IMenuItemAdapter newMenuItemAdapter(Object component) {
        return new JMenuItemAdapter(component);
    }

}
