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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.util.concurrent.Callable;

import javafx.scene.control.Menu;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Adapter for a Menu in a MenuBar. This is handled as MenuItem to realize the
 * opening of this Menu.
 * 
 * @author Bredex GmbH
 * @created 10.3.2014
 */
public class MenuBarItemAdapter extends MenuItemAdapter<Menu> {

    /**
     * Creates an adapter for a MenuBarItem.
     * 
     * @param objectToAdapt
     *            the object which needs to be adapted
     */
    public MenuBarItemAdapter(Menu objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    protected void clickMenuItem() {
        EventThreadQueuerJavaFXImpl.invokeAndWait("clickMenuBarItem", //$NON-NLS-1$
                new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        // .show() because there is no reliable way to get the
                        // MenuBarButton this menu belongs to.
                        getRealComponent().show();
                        return null;
                    }
                });
    }

}
