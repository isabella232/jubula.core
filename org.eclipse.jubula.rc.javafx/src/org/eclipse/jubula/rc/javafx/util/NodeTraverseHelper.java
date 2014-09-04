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
 * @param <T>
 *            the type
 */
public class NodeTraverseHelper<T extends Node> {

    /** The result where all instances of the given type are stored */
    private List<T> m_result = new ArrayList<T>();

    /**
     * Finds instances of a certain type in the hierarchy below a given node
     * 
     * @param parent the parent
     * @param type the type
     */
    private void findInstancesOf(Parent parent, Class<T> type) {
        for (Node object : parent.getChildrenUnmodifiable()) {
            if (type.isAssignableFrom(object.getClass())) {
                m_result.add((T) object);
            }
            if (object instanceof Parent) {
                findInstancesOf((Parent) object, type);
            }
        }
    }

    /**
     * Gives instances of a certain type in the hierarchy below a given node
     * 
     * @param parent the parent
     * @param type the type
     * @return returns all instances of the given type which are below the
     *         parent in the hierarchy
     */
    public List<T> getInstancesOf(Parent parent, Class<T> type) {
        m_result.clear();
        findInstancesOf(parent, type);
        return m_result;
    }

    /**
     * Checks if the given node is under the given parent in the scene graph
     * 
     * @param node the possible child node
     * @param parent the parent
     * @return true if the given node is related to the given parent, false if
     *         not
     */
    public boolean isChildOf(Node node, Parent parent) {
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
    
}
