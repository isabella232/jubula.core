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
package org.eclipse.jubula.client.ui.provider.labelprovider.decorators;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.constants.Layout;


/**
 * @author BREDEX GmbH
 * @created May 17, 2010
 */
public class ActiveElementDecorator extends LabelProvider implements
        ILightweightLabelDecorator {
    
    /**
     * <code>INACTIVE_PREFIX</code>
     */
    private static final String INACTIVE_PREFIX = "// "; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof INodePO) {
            INodePO node = (INodePO)element;
            if (node != null && !node.isActive()) {
                decoration.addPrefix(INACTIVE_PREFIX);
                decoration.setForegroundColor(Layout.INACTIVE_COLOR);
            }
        }
    }
}
