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
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.businessprocess.UINodeBP;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 25.10.2005
 *
 */
public class ShowSpecificationHandler extends AbstractShowSpecificationHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel instanceof IStructuredSelection) {
            ISpecTestCasePO specTc = UINodeBP
                    .getSpecTC((IStructuredSelection)sel);
            if (specTc != null) {
                showSpecUINode(specTc, Constants.TC_BROWSER_ID);
            }
        }
        return null;
    }
}