/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.businessprocess;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;

/**
 * Collects all NodePO guids from within a project
 * @author BREDEX GmbH
 *
 */
public class CollectAllNodeGuidsOperation
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

    /** The collected guids */
    private Set<String> m_guids = new HashSet<>();

    /** {@inheritDoc} */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent,
            INodePO node, boolean alreadyVisited) {
        if (alreadyVisited) {
            return false;
        }
        m_guids.add(node.getGuid());
        return true;
    }

    /**
     * @return the collected guids
     */
    public Set<String> getGuids() {
        return m_guids;
    }

}
