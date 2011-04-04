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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;


/**
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public class TestCaseEditorContentProvider implements ITreeContentProvider {

    /**
     * 
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ISpecTestCasePO) {
            return ((ISpecTestCasePO)parentElement)
                .getUnmodifiableNodeList().toArray();
        }
        
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void dispose() {
        // no-op
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // no-op
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        Validate.isTrue(inputElement instanceof INodePO[]);
        return (INodePO[])inputElement;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Object getParent(Object element) {
        if (element instanceof IExecTestCasePO) {
            return ((IExecTestCasePO)element).getParentNode();
        }
        
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        if (element instanceof ISpecTestCasePO) {
            return ((ISpecTestCasePO)element).getNodeListSize() > 0;
        }
        
        return false;
    }   
    
}