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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for the metadata report table view.
 * 
 * @author BREDEX GmbH
 * @created Oct 21, 2008
 */
public class TestresultSummaryContentProvider 
    implements IStructuredContentProvider {
    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Object[]) {
            return (Object[])inputElement;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // Nothing to dispose.
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Do nothing.
    }

}
