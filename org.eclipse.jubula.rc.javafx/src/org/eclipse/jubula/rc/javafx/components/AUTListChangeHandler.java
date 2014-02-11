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

import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;

import javafx.collections.ListChangeListener;

/**
 * Handles List Changes in the AUT
 *
 * @author BREDEX GmbH
 * @created 24.10.2013
 */
public class AUTListChangeHandler implements ListChangeListener<Object> {

    /** Hierarchy **/
    private AUTJavaFXHierarchy m_hierarchy = ComponentHandler.getAutHierarchy();

    @Override
    public void onChanged(Change<? extends Object> c) {
        c.next();
        List<? extends Object> changedObjects = c.getAddedSubList();
        for (Object o : changedObjects) {
            m_hierarchy.createHierarchyFrom(o);
        }
        changedObjects = c.getRemoved();
        for (Object o : changedObjects) {
            m_hierarchy.removeComponentFromHierarchy(o);
        }
    }

}
