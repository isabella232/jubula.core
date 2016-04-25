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

import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Cell;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.javafx.tester.adapter.AccordionAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ButtonBaseAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.CellAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ChoiceBoxAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ComboBoxAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ContextMenuAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.DatePickerAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.IContainerAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.JavaFXComponentAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.LabeledAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.LabeledGraphicContainerAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ListViewAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.MenuBarAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.MenuButtonAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ScrollPaneAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.SliderAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.SplitPaneAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TabPaneAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TableAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TextAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TextComponentAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TitledPaneAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.ToolBarAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.TreeTableViewAdapter;
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
        MenuItem.class, MenuBar.class, TextInputControl.class,
        TreeView.class, TableView.class, ContextMenu.class,
        ImageView.class, Text.class, TitledPane.class, ListView.class,
        ComboBox.class, TabPane.class, ChoiceBox.class, Accordion.class,
        ScrollPane.class, SplitPane.class, ToolBar.class,
        TreeTableView.class, MenuButton.class, Labeled.class, DatePicker.class,
        Cell.class, Slider.class
        };

    @Override
    public Class[] getSupportedClasses() {
        return SUPPORTEDCLASSES;
    }

    @Override
    public Object getAdapter(Class targetAdapterClass, Object objectToAdapt) {
        IComponent returnvalue = null;
        if (targetAdapterClass.isAssignableFrom(IComponent.class)) {
            if (objectToAdapt instanceof MenuButton) {
                returnvalue = new MenuButtonAdapter((MenuButton) objectToAdapt);
            } else if (objectToAdapt instanceof ButtonBase) {
                returnvalue = new ButtonBaseAdapter((ButtonBase) objectToAdapt);
            } else if (objectToAdapt instanceof Label) {
                returnvalue = new LabeledAdapter<Label>((Label) objectToAdapt);
            } else if (objectToAdapt instanceof Text) {
                returnvalue = new TextAdapter((Text) objectToAdapt);
            } else if (objectToAdapt instanceof TextInputControl) {
                returnvalue = new TextComponentAdapter(
                        (TextInputControl) objectToAdapt);
            } else if (objectToAdapt instanceof DatePicker) {
                returnvalue = new DatePickerAdapter(
                        (DatePicker) objectToAdapt);
            } else if (objectToAdapt instanceof TreeView) {
                returnvalue = new TreeViewAdapter((TreeView<?>) objectToAdapt);
            } else if (objectToAdapt instanceof TableView) {
                returnvalue = new TableAdapter((TableView<?>) objectToAdapt);
            } else if (objectToAdapt instanceof ContextMenu) {
                returnvalue = new ContextMenuAdapter(
                        (ContextMenu) objectToAdapt);
            } else if (objectToAdapt instanceof MenuBar) {
                returnvalue = new MenuBarAdapter((MenuBar) objectToAdapt);
            } else if (objectToAdapt instanceof ImageView) {
                returnvalue = new JavaFXComponentAdapter<ImageView>(
                        (ImageView) objectToAdapt);
            } else if (objectToAdapt instanceof TitledPane) {
                returnvalue = new LabeledAdapter<TitledPane>(
                        (TitledPane) objectToAdapt);
            } else if (objectToAdapt instanceof ListView) {
                returnvalue = new ListViewAdapter<ListView<?>>(
                        (ListView<?>) objectToAdapt);
            } else if (objectToAdapt instanceof ComboBox) {
                returnvalue = new ComboBoxAdapter<ComboBox<?>>(
                        (ComboBox<?>) objectToAdapt);
            } else if (objectToAdapt instanceof ChoiceBox) {
                returnvalue = new ChoiceBoxAdapter(
                    (ChoiceBox<?>) objectToAdapt);
            } else if (objectToAdapt instanceof Accordion) {
                returnvalue = new AccordionAdapter((Accordion) objectToAdapt);
            } else if (objectToAdapt instanceof TreeTableView) {
                returnvalue = new TreeTableViewAdapter(
                        (TreeTableView<?>) objectToAdapt);
            } else if (objectToAdapt instanceof Cell) {
                returnvalue = new CellAdapter((Cell) objectToAdapt);
            } else if (objectToAdapt instanceof Slider) {
                returnvalue = new SliderAdapter((Slider) objectToAdapt);
            }
        }
        if (returnvalue == null
                && targetAdapterClass.isAssignableFrom(
                        IContainerAdapter.class)) {
            
            if (objectToAdapt instanceof TabPane) {
                returnvalue = new TabPaneAdapter((TabPane) objectToAdapt);
            } else if (objectToAdapt instanceof TitledPane) {
                returnvalue = new TitledPaneAdapter((TitledPane) objectToAdapt);
            } else if (objectToAdapt instanceof ScrollPane) {
                returnvalue = new ScrollPaneAdapter((ScrollPane) objectToAdapt);
            } else if (objectToAdapt instanceof SplitPane) {
                returnvalue = new SplitPaneAdapter((SplitPane) objectToAdapt);
            } else if (objectToAdapt instanceof ToolBar) {
                returnvalue = new ToolBarAdapter((ToolBar) objectToAdapt);
            } else if (objectToAdapt instanceof Labeled) {
                returnvalue = new LabeledGraphicContainerAdapter<Labeled>(
                        (Labeled) objectToAdapt);
            }
        }
        return returnvalue;
    }

}
