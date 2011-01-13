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
package org.eclipse.jubula.client.ui.handlers.filter.testcases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;


/**
 * @author BREDEX GmbH
 * @created 03.07.2009
 */
public class FilterUsedTestCases extends ViewerFilter {
    /** local cache */
    private Map<GuiNode, Boolean> m_alreadyVisited = 
        new HashMap<GuiNode, Boolean>();
    
    /**
     * {@inheritDoc}
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof SpecTestCaseGUI) {
            SpecTestCaseGUI tc = (SpecTestCaseGUI)element;
            INodePO content = tc.getContent();
            if (content != null) {
                List<IExecTestCasePO> execTestCases;
                if (!m_alreadyVisited.containsKey(tc)) {
                    execTestCases = NodePM.getInternalExecTestCases(content
                            .getGuid(), content.getParentProjectId());
                    if (execTestCases.isEmpty()) {
                        m_alreadyVisited.put(tc, new Boolean(true));
                        return true;
                    }
                } else {
                    return m_alreadyVisited.get(tc).booleanValue();
                }
            }
            return false;
        } else if (element instanceof CategoryGUI) {
            CategoryGUI cat = (CategoryGUI)element;
            List<GuiNode> children = cat.getChildren();
            for (GuiNode child : children) {
                if (!m_alreadyVisited.containsKey(child)) {
                    if (select(viewer, parentElement, child)) {
                        m_alreadyVisited.put(child, new Boolean(true));
                        return true;
                    }
                } else {
                    return m_alreadyVisited.get(child).booleanValue();
                }
            }
            return false;
        }
        return true;
    }
    
    /**
     * clear the cache
     */
    public void resetCache() {
        m_alreadyVisited.clear();
    }
}
