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
package org.eclipse.jubula.client.analyze.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


/**
 * The DefaultHandler for the AnalyzeToolbarButton. Currently it returns null
 * because it has no real function
 * 
 * @author volker
 * 
 */
public class ToolbarButtonDefaultHandler extends AbstractHandler {

    /**
     * @param event The given event
     * @return null 
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        return null;
    }
}
