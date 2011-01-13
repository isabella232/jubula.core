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
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.model.RefTestSuiteGUI;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Aug 11, 2010
 */
public class ShowSpecificationHandlerRefTS extends
        AbstractShowSpecificationHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection iss = (IStructuredSelection)sel;
            Object firstElement = iss.getFirstElement();
            if (firstElement instanceof RefTestSuiteGUI) {
                RefTestSuiteGUI refTS = (RefTestSuiteGUI)firstElement;
                INodePO node = refTS.getContent();
                if (node instanceof IRefTestSuitePO) {
                    String tsGUID = ((IRefTestSuitePO)node).getTestSuiteGuid();
                    ITestSuitePO testSuite = NodePM.getTestSuite(tsGUID);
                    showSpecGUINode(testSuite, Plugin.getDefault()
                            .getTestSuiteBrowserRootGUI(),
                            Constants.TS_BROWSER_ID);
                }
            }
        }
        return null;
    }
}