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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.rcp.utils.TreeViewerIterator;


/**
 * Handler for navigating to the "next" error in a Test Result.
 *
 * @author BREDEX GmbH
 * @created May 17, 2010
 */
public class GoToNextTestResultErrorHandler 
        extends AbstractGoToTestResultErrorHandler {

    /**
     * {@inheritDoc}
     */
    protected TestResultNode findTargetNode(TreeViewer viewer,
            TestResultNode startingNode) {

        TreeViewerIterator iter = 
            new TreeViewerIterator(viewer, startingNode, true);
        while (iter.hasNext()) {
            Object nextElement = iter.next();
            if (nextElement instanceof TestResultNode) {
                TestResultNode node = (TestResultNode)nextElement;
                if (isErrorNode(node)) {
                    return node;
                }
            }
        }

        return null;
    }

    
}
