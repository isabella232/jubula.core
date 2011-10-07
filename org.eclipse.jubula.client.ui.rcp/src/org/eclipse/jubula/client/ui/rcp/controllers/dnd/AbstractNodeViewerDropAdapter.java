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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.INodePO;

/**
 * @author BREDEX GmbH
 * @created Sep 16, 2010
 */
public abstract class AbstractNodeViewerDropAdapter extends ViewerDropAdapter {
    /**
     * @param viewer
     *            the viewer
     */
    protected AbstractNodeViewerDropAdapter(Viewer viewer) {
        super(viewer);
    }

    /**
     * @param viewer
     *            the target viewer
     * @return the fallback target for dropping
     */
    protected Object getFallbackTarget(Viewer viewer) {
        if (viewer != null) {
            Object fallbackTarget = null;
            Object viewerInput = ((Object[])viewer.getInput())[0];
            if (viewerInput instanceof INodePO) {
                List<INodePO> viewerRootChildren = ((INodePO) viewerInput)
                        .getUnmodifiableNodeList();
                int childrenCount = 1;
                if (viewerRootChildren != null) {
                    childrenCount = viewerRootChildren.size();
                }
                if (childrenCount > 0) {
                    fallbackTarget = viewerRootChildren.get(childrenCount - 1);
                } else {
                    fallbackTarget = viewerInput;
                }
            }
            return fallbackTarget;
        }
        return null;
    }
}
