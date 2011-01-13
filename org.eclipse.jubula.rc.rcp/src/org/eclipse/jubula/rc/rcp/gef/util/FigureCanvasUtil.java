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
package org.eclipse.jubula.rc.rcp.gef.util;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.jubula.rc.rcp.accessor.Startup;
import org.eclipse.jubula.rc.rcp.gef.factory.DefaultEditPartAdapterFactory;
import org.eclipse.jubula.rc.rcp.gef.identifier.IEditPartIdentifier;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


/**
 * Utility class for interacting with a FigureCanvas.
 * 
 * @author BREDEX GmbH
 * @created Jun 26, 2009
 */
public class FigureCanvasUtil {

    /**
     * Private constructor
     */
    private FigureCanvasUtil() {
    // Nothing to initialize
    }

    /**
     * 
     * @param figureCanvas
     *            The canvas for which to find the viewer.
     * @return the graphical viewer associated with the given canvas, or
     *         <code>null</code> if no such viewer could be found.
     */
    public static GraphicalViewer getViewer(FigureCanvas figureCanvas) {
        Composite parent = figureCanvas;
        while (parent != null
                && !(parent.getData(Startup.GD_GEF_VIEWER_DATA_KEY) 
                        instanceof GraphicalViewer)) {
            parent = parent.getParent();
        }

        if (parent != null) {

            return (GraphicalViewer)parent
                    .getData(Startup.GD_GEF_VIEWER_DATA_KEY);
        }

        return null;
    }

    /**
     * 
     * @param display The display containing the edit part to find.
     * @param viewer The viewer containing the edit part to find.
     * @return the edit part for the viewer at the current mouse pointer
     *         coordinates, or <code>null</code> if no such edit part can be
     *         found.
     */
    public static EditPart findAtCurrentMousePosition(
            Display display, EditPartViewer viewer) {
        Point cursorLocation = new Point(display.map(null, viewer.getControl(),
                display.getCursorLocation()));
        EditPart editPart = viewer.findObjectAt(cursorLocation);
        EditPart primaryEditPart = getPrimaryEditPart(editPart, viewer
                .getRootEditPart());

        return primaryEditPart;
    }

    /**
     * Searches the path to root for the first element considered to be testable
     * by GUIdancer. Testable in this case means that an ID can be acquired for
     * the edit part.
     * 
     * @param editPart
     *            The starting edit part.
     * @param root
     *            The root for <code>editPart</code>. This is used to avoid
     *            identifying the root edit part as the primary edit part.
     * @return the first edit part on the path to root that has an ID.
     */
    public static EditPart getPrimaryEditPart(final EditPart editPart,
            RootEditPart root) {

        EditPart currentEditPart = editPart;

        while (currentEditPart != root.getContents() 
                && currentEditPart != null) {
            IEditPartIdentifier identifier = DefaultEditPartAdapterFactory
                    .loadFigureIdentifier(currentEditPart);
            if (identifier != null) {
                return currentEditPart;
            }
            currentEditPart = currentEditPart.getParent();
        }

        return null;
    }

}
