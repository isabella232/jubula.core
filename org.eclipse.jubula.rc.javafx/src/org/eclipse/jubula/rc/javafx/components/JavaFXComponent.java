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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

import org.eclipse.jubula.rc.common.components.AUTComponent;

/**
 * Wrapper for concrete JavaFX components.
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 */
public class JavaFXComponent extends AUTComponent {

    /** concrete JavaFX component **/
    private Object m_realComponent;

    /**
     * the type of the component. This is also stored because, on various Points
     * there has to be a distinction between Stage,Scene and Nodes
     */
    private Class<?> m_type;

    /**
     * create an instance from a JavaFX component. This constructor is used when
     * working with real instances.
     *
     * @param component
     *            the JavaFX component
     *
     */
    public JavaFXComponent(Object component) {
        super(component);
        m_type = component.getClass();
        m_realComponent = component;
        addChangeListener();
    }

    /**
     *
     * @return the real AUT component instance
     */
    public Object getRealComponent() {
        return m_realComponent;
    }

    /**
     *
     * @return the real AUT component type
     */
    public Class<?> getRealComponentType() {
        return m_type;
    }

    /**
     * Add a change Listener
     */
    public void addChangeListener() {
        Object children = ChildrenGetter.getAsRealType(m_realComponent);
        if (children instanceof ReadOnlyObjectProperty) {
            ChildrenListenerHelper
                    .addListener((ReadOnlyObjectProperty<?>) children);
        } else if (children instanceof ObservableList) {
            ChildrenListenerHelper.addListener((ObservableList<?>) children);
        }
    }

    /**
     * Remove a change Listener
     */
    public void removeChangeListener() {
        Object children = ChildrenGetter.getAsRealType(m_realComponent);
        if (children instanceof ObjectProperty) {
            ChildrenListenerHelper.removeListener((ObjectProperty<?>) children);
        } else if (children instanceof ObservableList) {
            ChildrenListenerHelper.removeListener((ObservableList<?>) children);
        }
    }

}
