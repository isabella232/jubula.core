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
package org.eclipse.jubula.client.ui.handlers.rename;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.views.TestSuiteBrowser;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 *@author Clemens Fabig
 * @created 09.03.2006
 */
public class RenameTreeItemHandlerTSBrowser 
    extends AbstractRenameTreeItemHandler {
    
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (part instanceof TestSuiteBrowser
                && sel instanceof IStructuredSelection) {
            dialogPopUp((IStructuredSelection)sel);
        }

        return null;
    }

}