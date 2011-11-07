/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.utils;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * @author Zeb Ford-Reitz
 * @created Nov 06, 2011
 */
public class SelectionUtils {

    /**
     * Private constructor for utility class.
     */
    private SelectionUtils() {
        // nothing to initialize
    }
    
    /**
     * 
     * @param selection The selection from which to get the first selected 
     *                  object.
     * @param type The expected type of object.
     * @param <T> The expected type of the object. 
     * @return the first element in <code>selection</code>, if 
     *         <code>selection</code> is an {@link IStructuredSelection} and 
     *         the first element is an instance of <code>type</code>. 
     *         Otherwise, returns <code>null</code>.
     */
    public static <T> T getFirstElement(ISelection selection, Class<T> type) {
        if (selection instanceof IStructuredSelection) {
            Object firstElement = 
                    ((IStructuredSelection)selection).getFirstElement();
            if (type.isInstance(firstElement)) {
                return type.cast(firstElement);
            }
        }
        
        return null;
    }
    
}
