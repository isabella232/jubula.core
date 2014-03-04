/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.adapter;

import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.javafx.tester.adapter.ButtonBaseAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ContextMenuAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.JavaFXComponentAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.LabelAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.MenuBarAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TableAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TextComponentAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TreeViewAdapter;

/**
 * This is the adapter factory for all JavaFX components. It is creating the
 * specific adapter for a JavaFX component.
 * 
 * Since we are using adapter here, it is a adapter factory. But this must not
 * be the case. It is only relevant that the object is implementing the specific
 * interface.
 * 
 * @author BREDEX GmbH
 * @created 28.10.2013
 * 
 */
public class JavaFXAdapterFactory implements IAdapterFactory {

    /**
     * the supported classes
     */
    private static final Class[] SUPPORTEDCLASSES = new Class[] {
        ButtonBase.class, MenuItem.class, MenuBar.class, Label.class,
        TextInputControl.class, TreeView.class, TableView.class,
        ContextMenu.class, ImageView.class };

    @Override
    public Class[] getSupportedClasses() {
        return SUPPORTEDCLASSES;
    }

    @Override
    public Object getAdapter(Class targetAdapterClass, Object objectToAdapt) {
        IComponent returnvalue = null;
        if (targetAdapterClass.isAssignableFrom(IComponent.class)) {

            if (objectToAdapt instanceof ButtonBase) {
                returnvalue = new ButtonBaseAdapter((ButtonBase) objectToAdapt);
            } else if (objectToAdapt instanceof Label) {
                returnvalue = new LabelAdapter((Label) objectToAdapt);
            } else if (objectToAdapt instanceof TextInputControl) {
                returnvalue = new TextComponentAdapter(
                        (TextInputControl) objectToAdapt);
            } else if (objectToAdapt instanceof TreeView) {
                returnvalue = new TreeViewAdapter((TreeView) objectToAdapt);
            } else if (objectToAdapt instanceof TableView) {
                returnvalue = new TableAdapter((TableView) objectToAdapt);
            } else if (objectToAdapt instanceof ContextMenu) {
                returnvalue = new ContextMenuAdapter(
                        (ContextMenu) objectToAdapt);
            } else if (objectToAdapt instanceof MenuBar) {
                returnvalue = new MenuBarAdapter(
                        (MenuBar) objectToAdapt);
            } else if (objectToAdapt instanceof ImageView) {
                returnvalue = new JavaFXComponentAdapter
                        <ImageView>((ImageView) objectToAdapt);
            }
        }
        return returnvalue;
    }

}
