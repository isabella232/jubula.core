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
package org.eclipse.jubula.client.ui.provider.contentprovider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.ITestDataCubeContPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.exception.Assert;


/**
 * @author BREDEX GmbH
 * @created 13.09.2005
 *
 */
public abstract class AbstractGDTreeViewContentProvider 
    implements IGDSortableTreeViewContentProvider {
    
    /** should getChildren returns sorted lists */
    private boolean m_outPutSorted = false;

    /** {@inheritDoc} */
    public void setSorting(boolean sort) {
        if (sort != m_outPutSorted) {
            m_outPutSorted = sort;
        }
    }
    
    /** {@inheritDoc} */
    public boolean isSorting() {
        return m_outPutSorted;
    }
    
    /** {@inheritDoc} */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }
    
    /** {@inheritDoc} */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }
    
    /** {@inheritDoc} */
    public void dispose() {
        // do nothing
    }

    /** {@inheritDoc} */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing yet
    }

    /** {@inheritDoc} */
    public Object getParent(Object element) {
        if (element instanceof GuiNode) {
            return ((GuiNode)element).getParentNode();
        }
        if (element instanceof ITestDataCubeContPO
                || element instanceof ITestDataCubePO
                || element instanceof SearchResultElement) {
            return null;
        }
        Assert.notReached("Wrong type of element!"); //$NON-NLS-1$
        return null;
    }
}