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
package org.eclipse.jubula.client.analyze.internal.helper;

import java.util.Map;

import org.eclipse.jubula.client.analyze.ExtensionRegistry;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.Renderer;

/**
 * Helper to select the Renderer for the AnalyzeResult
 * 
 * @author volker
 * 
 */
public class RendererSelectionHelper {
    /** Empty Constructor for this HelperClass */
    private RendererSelectionHelper() {
    }

    /**
     * Checks which Renderer has to be taken to render the result of this
     * Analyze
     * 
     * @param analyze
     *            The given Analyze
     * @return The active Renderer or null if no matching Renderer could be
     *         found
     */
    public static Renderer getActiveRenderer(Analyze analyze) {
        for (Map.Entry<String, Renderer> e : ExtensionRegistry.getRenderer()
                .entrySet()) {
            Renderer r = e.getValue();
            if (r.getResultType().equals(analyze.getResultType())) {
                return r;
            }
        }
        return null;
    }
}
