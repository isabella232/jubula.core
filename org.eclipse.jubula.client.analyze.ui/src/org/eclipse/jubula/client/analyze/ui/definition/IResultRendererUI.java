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
package org.eclipse.jubula.client.analyze.ui.definition;

import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * 
 * @author volker
 *
 */
public interface IResultRendererUI {
    /**
     * @param result The given AnalyzeResult
     * @param composite The given Composite
     */
    void renderResult(AnalyzeResult result, Composite composite);
        
    /** 
     * @return The TopControl of this Renderer. Can be null if there is no TopControl
     */
    Control getTopControl();
}
