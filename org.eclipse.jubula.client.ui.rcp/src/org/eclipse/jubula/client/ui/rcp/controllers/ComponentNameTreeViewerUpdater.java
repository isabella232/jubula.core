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
package org.eclipse.jubula.client.ui.rcp.controllers;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.IComponentNamePO;


/**
 * Updates a tree viewer based on changes to Component Names.
 *
 * @author BREDEX GmbH
 * @created Mar 3, 2009
 */
public class ComponentNameTreeViewerUpdater 
        extends AbstractComponentNameViewerUpdater {

    /** the viewer maintained by this updater */
    private TreeViewer m_treeViewer;
    
    /**
     * Constructor
     * 
     * @param treeViewer The viewer to be maintained by this updater.
     */
    public ComponentNameTreeViewerUpdater(TreeViewer treeViewer) {
        m_treeViewer = treeViewer;
    }

    /**
     * {@inheritDoc}
     */
    protected void refresh() {
        m_treeViewer.refresh();
    }

    /**
     * {@inheritDoc}
     */
    protected void remove(IComponentNamePO compName) {
        m_treeViewer.remove(compName);
    }

    /**
     * {@inheritDoc}
     */
    protected void update(IComponentNamePO compName, String[] properties) {
        m_treeViewer.update(compName, properties);
    }
}
