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
package org.eclipse.jubula.rc.swt.components;

import org.eclipse.jubula.rc.common.components.HierarchyContainer;
import org.eclipse.swt.internal.SWTEventListener;


/**
 * @author BREDEX GmbH
 * @created 04.05.2006
 */
public class SwtHierarchyContainer extends HierarchyContainer {

    /**
     * @param component the SwtComponentIdentifier
     * @param parent the SwtHierarchyContainer
     */
    public SwtHierarchyContainer(SwtComponent component, 
            SwtHierarchyContainer parent) {
        super(component, parent);
    }
    
    /**
     * @param component the SwtComponentIdentifier
     */
    public SwtHierarchyContainer(SwtComponent component) {
        this(component, null);
    }

    /**
     * {@inheritDoc}
     */
    public void add(SwtHierarchyContainer component) {
        super.add(component);
    }

    /**
     * {@inheritDoc}
     */
    public SwtComponent getComponentID() {
        return (SwtComponent)super.getCompID();
    }

    /**
     * {@inheritDoc}
     */
    public SwtComponent getComponent(int index) {
        return (SwtComponent)super.getComp(index);
    }

    /**
     * {@inheritDoc}
     */
    public SwtHierarchyContainer[] getComponents() {
        if (super.getComps().length == 0) {
            return new SwtHierarchyContainer[0];
        }
        HierarchyContainer[] containerArray = super.getComps();
        SwtHierarchyContainer[] swtContainerArray = 
            new SwtHierarchyContainer[containerArray.length];
        for (int i = 0; i < containerArray.length; i++) {
            swtContainerArray[i] = (SwtHierarchyContainer)containerArray[i]; 
        }
        return swtContainerArray;
    }

    /**
     * {@inheritDoc}
     */
    public SWTEventListener[] getListeners() {
        return (SWTEventListener[])super.getListnrs();
    }

    /**
     * {@inheritDoc}
     */
    public SwtHierarchyContainer getParent() {
        return (SwtHierarchyContainer)super.getPrnt();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParent(SwtHierarchyContainer parent) {
        super.setPrnt(parent);
    }
    
    /**
     * {@inheritDoc}
     * @param listener
     */
    public void addContainerListener(SWTEventListener listener) {
        super.addContainerListnr(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeContainerListener(SWTEventListener listener) {
        super.removeContainerListener(listener);
    } 
}