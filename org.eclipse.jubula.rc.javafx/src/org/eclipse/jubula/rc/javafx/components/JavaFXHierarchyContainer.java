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

import java.util.List;

import org.eclipse.jubula.rc.common.components.AUTComponent;
import org.eclipse.jubula.rc.common.components.HierarchyContainer;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;

/**
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 */
public class JavaFXHierarchyContainer extends HierarchyContainer {

    /**
     * Constructor
     *
     * @param component
     *            the JavaFXComponentWrapper
     */
    public JavaFXHierarchyContainer(JavaFXComponent component) {
        super(component);
    }

    /**
     * Constructor
     *
     * @param component
     *            the JavaFXComponentWrapper
     * @param container
     *            the JavaFHierarchyContainer
     */
    public JavaFXHierarchyContainer(AUTComponent component,
            JavaFXHierarchyContainer container) {
        super(component, container);
    }

    /**
     * @return JavaFXComponent
     */
    public JavaFXComponent getComponent() {
        return (JavaFXComponent) super.getCompID();
    }

    /**
     * Checks if this container has the given container as child container
     *
     * @param child
     *            the child container
     * @return true if this container has the given container, false if not.
     */
    public boolean contains(JavaFXHierarchyContainer child) {
        List<JavaFXHierarchyContainer> children = getContainerList();
        for (JavaFXHierarchyContainer cont : children) {
            if (cont.equals(child)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected List<JavaFXHierarchyContainer> getContainerList() {
        return super.getContainerList();
    }
    
    /**
     * Adds a component to the container. Renames it if there is already a
     * container with this name.
     *
     * @param component
     *            The component to add.
     */
    public void add(JavaFXHierarchyContainer component) {
        if (!(ComponentHandler.getAutHierarchy().isUniqueName(this,
                component.getName(), component))) {
            ComponentHandler.getAutHierarchy().name(component);
        }
        getContainerList().add(component);
    }

}
