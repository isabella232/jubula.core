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

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public interface ISortableTreeViewContentProvider extends
        ITreeContentProvider {

    /**
     * set true if output should be sorted
     * @param sort boolean
     */
    public void setSorting(boolean sort);
    
    /**
     * @return should output be sorted ?
     */
    public boolean isSorting();
}