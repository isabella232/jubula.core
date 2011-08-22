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
package org.eclipse.jubula.client.ui.controllers;

import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * Updates a viewer based on changes to Component Names.
 *
 * @author BREDEX GmbH
 * @created Mar 31, 2009
 */
public abstract class AbstractComponentNameViewerUpdater 
        implements IDataChangedListener {
    /**
     * Constructor
     */
    public AbstractComponentNameViewerUpdater() {
        // empty
    }
    
    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        for (DataChangedEvent e : events) {
            handleDataChanged(e.getPo(), e.getDataState(),
                    e.getUpdateState());
        }
    }
    
    /** {@inheritDoc} */
    public final void handleDataChanged(IPersistentObject po, 
            DataState dataState, UpdateState updateState) {

        if (po instanceof IComponentNamePO) {
            IComponentNamePO compName = (IComponentNamePO)po;
            switch (dataState) {
                case Deleted:
                    remove(compName);
                    break;
                case Renamed:
                    update(compName, null);
                    break;
                case Added:
                    refresh();
                    break;
                default:
                    break;
            }
        } else if (po instanceof IObjectMappingPO) {
            switch (dataState) {
                case StructureModified:
                    refresh();
                default:
                    break;
            }
        }

    }

    /**
     * Refreshes the viewer.
     */
    protected abstract void refresh();

    /**
     * Updates the given Component Name in the viewer.
     * 
     * @param compName The Component Name to update.
     * @param properties The names of the properties to update, or 
     *                   <code>null</code> if not known. 
     */
    protected abstract void update(
            IComponentNamePO compName, String [] properties);

    /**
     * @param compName The Component Name to remove from the viewer.
     */
    protected abstract void remove(IComponentNamePO compName);
}
