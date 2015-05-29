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
package org.eclipse.jubula.rc.javafx.util;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Utility class for finding instances of a certain type in the hierarchy below
 * a given node
 * 
 * @author BREDEX GmbH
 * @created 25.03.2014
 */
public class NodeTraverseHelper {
    
    /**
     * Constructor
     */
    private NodeTraverseHelper() {
        //private
    }

    /**
     * Finds instances of a certain type in the hierarchy below a given node
     * 
     * @param <T> the type
     * @param parent the parent
     * @param type the type
     * @param r the result
     * @return the result
     */
    private static <T> List<T> findInstancesOf(Parent parent,
            Class<T> type, List<T> r) {
        List<T> result = r;
        for (Node object : parent.getChildrenUnmodifiable()) {
            if (type.isAssignableFrom(object.getClass())) {
                result.add((T) object);
            }
            if (object instanceof Parent) {
                result = findInstancesOf((Parent) object, type, result);
            }
        }
        return result;
    }

    /**
     * Gives instances of a certain type in the hierarchy below a given node
     * 
     * @param <T> the type
     * @param parent the parent
     * @param type the type
     * @return returns all instances of the given type which are below the
     *         parent in the hierarchy
     */
    public static <T> List<T> getInstancesOf(Parent parent, 
            Class<T> type) {
        return findInstancesOf(parent, type, new ArrayList<T>());
    }

    /**
     * Checks if the given node is under the given parent in the scene graph
     * 
     * @param node the possible child node
     * @param parent the parent
     * @return true if the given node is related to the given parent, false if
     *         not
     */
    public static boolean isChildOf(Node node, Parent parent) {
        boolean result = false;
        for (Node n : parent.getChildrenUnmodifiable()) {
            if (!result) {
                if (n == node) {
                    return true;
                }
                if (n instanceof Parent) {
                    result = isChildOf(node, (Parent) n);
                }
            }
        }
        return result;
    }
    
    /**
     * Checks if a given Node is Visible by checking if all parent nodes are visible
     * @param node the node 
     * @return true if visible, false otherwise
     */
    public static boolean isVisible(Node node) {
        if (node == null) {
            return false;
        }
        Node tmp = node;
        while (tmp != null) {
            if (!tmp.isVisible()) {
                return false;
            }
            tmp = tmp.getParent();
        }
        return true;
    }
    
}
