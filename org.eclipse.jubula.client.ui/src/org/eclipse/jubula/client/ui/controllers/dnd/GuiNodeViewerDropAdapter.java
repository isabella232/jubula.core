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
package org.eclipse.jubula.client.ui.controllers.dnd;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;


/**
 * @author BREDEX GmbH
 * @created Sep 16, 2010
 */
public abstract class GuiNodeViewerDropAdapter extends ViewerDropAdapter {
    /**
     * @param viewer the viewer
     */
    protected GuiNodeViewerDropAdapter(Viewer viewer) {
        super(viewer);
    }
    
}
