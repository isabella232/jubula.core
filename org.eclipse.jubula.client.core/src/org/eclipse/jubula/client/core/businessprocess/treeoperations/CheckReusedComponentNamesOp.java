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
package org.eclipse.jubula.client.core.businessprocess.treeoperations;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;

/**
 * Operation for checking which Component Names are reused.
 *
 * @author BREDEX GmbH
 * @created Mar 6, 2009
 */
public class CheckReusedComponentNamesOp 
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {

    /** GUIDs of Component Name to use for this operation */
    private Set<String> m_compNameGuids;
    
    /**
     * GUIDs of the used component names.
     */
    private Set<String> m_usedCompNameGuids = new HashSet<>();
    
    /** the operation used to find instances of reuse */

    /**
     * Constructor
     * 
     * @param compNameGuidsToCheck The GUIDs of the Component Names for to check
     *                    whether they are reused.
     */
    public CheckReusedComponentNamesOp(Set<String> compNameGuidsToCheck) {
        m_compNameGuids = compNameGuidsToCheck;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean operate(
            ITreeTraverserContext<INodePO> ctx, INodePO parent, INodePO node, 
            boolean alreadyVisited) {
        
        if (node instanceof ICapPO) {
            ICapPO cap = (ICapPO)node;
            
            if (cap.getComponentName() != null 
                    && m_compNameGuids.contains(cap.getComponentName())) {
                m_usedCompNameGuids.add(cap.getComponentName());
            }
        } else if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTc = (IExecTestCasePO)node;
            for (ICompNamesPairPO pair : execTc.getCompNamesPairs()) {
                
                if (m_compNameGuids.contains(pair.getFirstName())) {
                    m_usedCompNameGuids.add(pair.getFirstName());
                }
                if (m_compNameGuids.contains(pair.getSecondName())) {
                    m_usedCompNameGuids.add(pair.getSecondName());
                }
            }
        }

        return true;
    }

    /**
     * 
     * @return GUIDs of used component names.
     */
    public Set<String> getUsedCompNameGuids() {
        return m_usedCompNameGuids;
    }
    
    
}
