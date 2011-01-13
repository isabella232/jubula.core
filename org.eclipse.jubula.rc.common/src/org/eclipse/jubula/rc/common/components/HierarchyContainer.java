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
package org.eclipse.jubula.rc.common.components;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;


/**
* This class manages a container from the AUT. <br>
 * 
 * It holds a reference to the instace of the display in the AUT. The names 
 * for the widgets are also stored in instances of this class.<br>
 * @author BREDEX GmbH
 * @created 02.05.2006
 */
public abstract class HierarchyContainer {

    /** boolean that indicates whether component name is generated */
    private boolean m_nameGenerated;
    
    /** parent of this component */
    private HierarchyContainer m_parent;
    
    /** a reference to the component/container in the AUT this instance represents */
    private AUTComponent m_component;
    
    /** list of HierarchyContainers */
    private List m_containerList = new ArrayList();
    
    /** list of HierarchyContainers */
    private List m_listenerList = new ArrayList();
    
    /** the name of the component */
    private String m_name;
    
    /**
     * constructor
     * @param component the reference to the container in the AUT
     * @param parent parent of the container
     */
    public HierarchyContainer(AUTComponent component, 
            HierarchyContainer parent) {
        
        m_component = component;
        m_parent = parent;
    }
    
    /**
     * constructor
     * @param component the reference to the container in the AUT
     */
    public HierarchyContainer(AUTComponent component) {
        
        this(component, null);
        m_parent = null;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof HierarchyContainer)) {
            return false;
        }
        if (obj == this) {
            return true; // a case of identity
        }
        HierarchyContainer o = (HierarchyContainer)obj;
        return m_component.equals(o.m_component);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return m_component.hashCode();
    }
    
    /**
     * @return Returns the component.
     */
    protected AUTComponent getCompID() {
        return m_component;
    }
    
    /**
     * Adds a component to the container.
     * @param component The component to add.
     */
    public void add(HierarchyContainer component) {
        getContainerList().add(component);
    }
    
    /**
     * Removes a component from the container.
     * @param component The container to add.
     */
    public void remove(HierarchyContainer component) {
        getContainerList().remove(component);
    }

    /**
     * @return Returns the components of the container.
     */
    protected HierarchyContainer[] getComps() {
        if (getContainerList().isEmpty()) {
            return new HierarchyContainer[0];
        }
        Object[] objectArray = getContainerList().toArray();
        HierarchyContainer[] containerArray = 
            new HierarchyContainer[objectArray.length];
        for (int i = 0; i < objectArray.length; i++) {
            containerArray[i] = (HierarchyContainer)objectArray[i]; 
        }
        return containerArray;
    }
    
    /**
     * @param index the index of the component list.
     * @return a component with the given index.
     */
    protected AUTComponent getComp(int index) {
        return (AUTComponent)getContainerList().get(index);
    }
    
    /**
     * Adds GDContainerListener to listener list.
     * @param listener the gdContainerListener
     */
    protected void addContainerListnr(EventListener listener) {
        m_listenerList.add(listener);
    }
    
    /**
     * Removess GDContainerListener from listener list.
     * @param listener the gdContainerListener
     */
    protected void removeContainerListener(EventListener listener) {
        m_listenerList.remove(listener);
    }

    /**
     * @return Returns the listenerList.
     */
    protected EventListener[] getListnrs() {
        if (m_listenerList.isEmpty()) {
            return new EventListener[0];
        }
        return (EventListener[])m_listenerList
            .toArray(new EventListener[m_listenerList.size()]);
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name The name to set.
     * @param nameGenerated A boolean indicating whether name is generated.
     */
    public final void setName(String name, boolean nameGenerated) {
        m_name = name;
        m_nameGenerated = nameGenerated;
    }
    
    /**
     * @return Returns a boolean indicating whether name is generated.
     */
    public final boolean isNameGenerated() {
        return m_nameGenerated;
    }

    /**
     * @return Returns the parent.
     */
    public HierarchyContainer getPrnt() {
        return m_parent;
    }
    
    /**
     * @param parent the parent
     */
    public void setPrnt(HierarchyContainer parent) {
        m_parent = parent; 
    }

    /**
     * @return the containerList
     */
    protected List getContainerList() {
        return m_containerList;
    }
}