/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.ui.internal.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.Category;

/**
 * 
 * @author volker
 * 
 */
public class QueryTreeLabelProvider extends LabelProvider {

    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        if (element instanceof Category) {
            Category category = (Category) element;
            return category.getName();
        }
        return ((Analyze) element).getName();

    }

}
