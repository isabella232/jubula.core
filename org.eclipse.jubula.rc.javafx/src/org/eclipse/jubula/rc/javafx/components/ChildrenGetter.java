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
package org.eclipse.jubula.rc.javafx.components;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

/**
 * This class handles the receiving of children from different components from a
 * Hierarchical point of view.
 *
 * @author BREDEX GmbH
 * @created 25.10.2013
 */
public class ChildrenGetter {

    /**
     * Private Constructor
     */
    private ChildrenGetter() {
        // private
    }

    /**
     * Returns a list of children of a given Object
     *
     * @param o
     *            the object
     * @return the children in a List of Objects.
     */
    public static List<Object> getAsList(Object o) {
        List<Object> result = new ArrayList<Object>();
        if (o instanceof Menu) {
            result.addAll(getFrom((Menu) o));
        } else if (o instanceof Parent) {
            if (o instanceof ScrollPane) {
                result.add(getFrom((ScrollPane) o).getValue());
            } else {
                result.addAll(getFrom((Parent) o));
            }
        } else if (o instanceof Stage) {
            result.add(getFrom((Stage) o).getValue());
        } else if (o instanceof Scene) {
            result.add(getFrom((Scene) o).getValue());
        }
        return result;
    }

    /**
     * Returns either an ObservableList of children or an
     * <Code>ObjectProperty</Code> of a given Object
     *
     * @param o
     *            the object
     * @return the children in a List of Objects.
     */
    public static Object getAsRealType(Object o) {
        Object result = null;
        if (o instanceof MenuBar) {
            result = getFrom((MenuBar) o);
        } else if (o instanceof Menu) {
            result = getFrom((Menu) o);
        } else if (o instanceof Parent) {
            if (o instanceof ScrollPane) {
                result = getFrom((ScrollPane) o);
            } else {
                result = getFrom((Parent) o);
            }
        } else if (o instanceof Stage) {
            result = getFrom((Stage) o);
        } else if (o instanceof Scene) {
            result = getFrom((Scene) o);
        }
        return result;
    }

    /**
     * Returns the Root Property of a Scene.
     *
     * @param scene
     *            the Scene
     * @return Root Property
     */
    public static ReadOnlyObjectProperty<Parent> getFrom(Scene scene) {
        return scene.rootProperty();
    }

    /**
     * Returns the Child Property of a Stage, the Scene Property.
     *
     * @param stage
     *            the stage
     * @return the Scene Property
     */
    public static ReadOnlyObjectProperty<Scene> getFrom(Stage stage) {
        return stage.sceneProperty();
    }

    /**
     * Returns the Children of a Parent Node, which could be Nodes or other
     * Parent Nodes. Some of this Nodes are Nodes from the Internal API like the
     * Text on a Button.
     *
     * @param parent
     *            the Parent
     * @return List with the child nodes
     */
    public static ObservableList<Node> getFrom(Parent parent) {
        return parent.getChildrenUnmodifiable();
    }

    /**
     * Even though a ScrollPane is an Instance of Parent, the Hierarchical child
     * Nodes of a ScrollPane aren't in the Children list, but accessible over
     * getContent. The Return vale will be one node.
     *
     * @param scPane
     *            the ScrollPane
     * @return List with one Node, the content from the ScrollPane
     */
    public static ObjectProperty<Node> getFrom(ScrollPane scPane) {
        return scPane.contentProperty();
    }

    /**
     * Returns a list of MenuItems which also could be a Menu and therefore have
     * children.
     *
     * @param menu
     *            the Menu
     * @return List with MenuItems
     */
    public static ObservableList<Object> getFrom(Menu menu) {
        ObservableList<Object> result = FXCollections.observableArrayList();
        result.addAll(menu.getItems());
        return result;
    }

}
