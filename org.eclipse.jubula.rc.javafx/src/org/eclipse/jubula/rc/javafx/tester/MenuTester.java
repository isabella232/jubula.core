/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.tester.adapter.MenuItemAdapter;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;

/**
 * Toolkit specific commands for the <code>Menu</code> and <code>MenuBar</code>.
 * 
 * @author BREDEX GmbH
 * @created 10.2.2014
 */
public class MenuTester extends AbstractMenuTester {

    @Override
    public String[] getTextArrayFromComponent() {
        return null;
    }

    @Override
    protected IMenuItemComponent newMenuItemAdapter(Object component) {
        return new MenuItemAdapter<MenuItem>((MenuItem) component);
    }

    @Override
    protected void closeMenu(IMenuComponent menu, String[] textPath,
            String operator) {
        IMenuItemComponent item = navigateToMenuItem(menu,
                Arrays.copyOf(textPath, 1), operator);
        if (item == null || !item.hasSubMenu()) {
            getRobot().keyType(null, KeyEvent.VK_ESCAPE);
        } else {
            // Press Escape twice, because when the MenuItem the mouse is over
            // has a sub Menu, it is opened and has to be close separately
            getRobot().keyType(null, KeyEvent.VK_ESCAPE);

            getRobot().keyType(null, KeyEvent.VK_ESCAPE);
            // Fallback if the item is still visible
            if (item.isShowing()) {
                getRobot().keyType(null, KeyEvent.VK_ESCAPE);
            }
        }
    }

    @Override
    protected void closeMenu(IMenuComponent menu, int[] path) {
        IMenuItemComponent item = navigateToMenuItem(menu,
                Arrays.copyOf(path, 1));
        if (item == null || !item.hasSubMenu()) {
            getRobot().keyType(null, KeyEvent.VK_ESCAPE);
        } else {
            // Press Escape twice, because when the MenuItem the mouse is over
            // has a sub Menu, it is opened and has to be close separately
            getRobot().keyType(null, KeyEvent.VK_ESCAPE);

            getRobot().keyType(null, KeyEvent.VK_ESCAPE);
            // Fallback if the item is still visible
            if (item.isShowing()) {
                getRobot().keyType(null, KeyEvent.VK_ESCAPE);
            }
        }
    }

    @Override
    public IComponent getComponent() {
        IComponent adapt = super.getComponent();
        if (adapt != null
                && (adapt.getRealComponent() instanceof ContextMenu || adapt
                        .getRealComponent() instanceof MenuBar)) {
            return adapt;
        }

        List<? extends MenuBar> bars = 
                ComponentHandler.getAssignableFrom(MenuBar.class);

        if (bars.size() > 1) {
            throw new StepExecutionException("Multiple MenuBars found", //$NON-NLS-1$
                    EventFactory
                            .createActionError(TestErrorEvent.
                                    UNSUPPORTED_OPERATION_IN_TOOLKIT_ERROR));
        }

        setComponent(bars.get(0));
        return super.getComponent();

    }
}
