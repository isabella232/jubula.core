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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.editors.TestResultViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Abstract handler for navigating to a test result node.
 *
 * @author BREDEX GmbH
 * @created Jun 3, 2010
 */
public abstract class AbstractGoToTestResultErrorHandler 
        extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public final Object execute(ExecutionEvent event) 
        throws ExecutionException {
        
        IEditorPart activeEditor = HandlerUtil.getActiveEditorChecked(event);
        activeEditor.setFocus();
        if (activeEditor instanceof TestResultViewer) {
            TreeViewer viewer = 
                ((TestResultViewer)activeEditor).getTreeViewer();
            IStructuredSelection selection = 
                (IStructuredSelection)viewer.getSelection();
            ITreeContentProvider contentProvider = 
                (ITreeContentProvider)viewer.getContentProvider();
            TestResultNode startingNode = null;
            if (selection.getFirstElement() instanceof TestResultNode) {
                startingNode = (TestResultNode)selection.getFirstElement();
            } else {
                Object [] rootElements = 
                    contentProvider.getElements(viewer.getInput());
                for (Object element : rootElements) {
                    if (element instanceof TestResultNode) {
                        startingNode = (TestResultNode)element;
                        break;
                    }
                }
            }
            
            TestResultNode targetNode = findTargetNode(viewer, startingNode);

            if (targetNode != null) {
                viewer.reveal(targetNode);
                viewer.setSelection(new StructuredSelection(targetNode));
            }

        }

        return null;
    }

    /**
     * 
     * @param node The node to check.
     * @return <code>true</code> if the node is considered an error node.
     */
    protected final boolean isErrorNode(TestResultNode node) {
        int status = node.getStatus();
        return (status == TestResultNode.ERROR)
                || (status == TestResultNode.ABORT);
    }
    
    /**
     * 
     * @param viewer The viewer containg the contents to search.
     * @param startingNode The node with which the search begins.
     * @return the node that should be navigated to, or <code>null</code> if no
     *         such node could be found.
     */
    protected abstract TestResultNode findTargetNode(
            TreeViewer viewer, TestResultNode startingNode);
    
}