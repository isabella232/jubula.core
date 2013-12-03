/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.decorator;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.propertytester.NodePropertyTester;
import org.eclipse.jubula.client.ui.provider.labelprovider.decorators.AbstractLightweightLabelDecorator;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author BREDEX GmbH
 */
public class TaskDecorator extends AbstractLightweightLabelDecorator {
    /** {@inheritDoc} */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof INodePO) {
            INodePO node = (INodePO) element;
            if (NodePropertyTester.hasTaskIdSet(node)) {
                decoration.addOverlay(TasksUiImages.TASK_REMOTE);
            }
        }
    }
}
