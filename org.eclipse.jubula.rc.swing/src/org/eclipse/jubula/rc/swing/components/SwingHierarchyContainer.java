/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.components;

import java.util.EventListener;

import org.eclipse.jubula.rc.common.components.HierarchyContainer;


/**
 * @author BREDEX GmbH
 * @created 04.05.2006
 */
public class SwingHierarchyContainer extends HierarchyContainer {

    /**
     * @param component the SwtComponentIdentifier
     * @param parent the SwingHierarchyContainer
     */
    public SwingHierarchyContainer(SwingComponent component, 
            SwingHierarchyContainer parent) {
        super(component, parent);
    }
    
    /**
     * @param component the SwtComponentIdentifier
     */
    public SwingHierarchyContainer(SwingComponent component) {
        this(component, null);
    }

    /**
     * @param component SwingHierarchyContainer
     */
    public void add(SwingHierarchyContainer component) {
        super.add(component);
    }

    /**
     * @return SwingComponent
     */
    public SwingComponent getComponentID() {
        return (SwingComponent)super.getCompID();
    }

    /**
     * @return SwingHierarchyContainer[]
     */
    public SwingHierarchyContainer[] getComponents() {
        if (super.getComps().length == 0) {
            return new SwingHierarchyContainer[0];
        }
        HierarchyContainer[] containerArray = super.getComps();
        SwingHierarchyContainer[] swingContainerArray = 
            new SwingHierarchyContainer[containerArray.length];
        for (int i = 0; i < containerArray.length; i++) {
            swingContainerArray[i] = 
                (SwingHierarchyContainer)containerArray[i]; 
        }
        return swingContainerArray;
    }

    /**
     * @return EventListener[]
     */
    public EventListener[] getListeners() {
        return super.getListnrs();
    }

    /**
     * @return SwingHierarchyContainer
     */
    public SwingHierarchyContainer getParent() {
        return (SwingHierarchyContainer)super.getPrnt();
    }
    
    /**
     * @param parent SwingHierarchyContainer
     */
    public void setParent(SwingHierarchyContainer parent) {
        super.setPrnt(parent);
    }

    /**
     * @param listener EventListener
     */
    public void addContainerListener(EventListener listener) {
        super.addContainerListnr(listener);
    }

    /**
     * @param listener EventListener
     */
    public void removeContainerListener(EventListener listener) {
        super.removeContainerListener(listener);
    } 
}