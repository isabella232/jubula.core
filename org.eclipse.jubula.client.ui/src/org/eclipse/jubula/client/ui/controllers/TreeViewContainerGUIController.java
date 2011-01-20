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
package org.eclipse.jubula.client.ui.controllers;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.provider.contentprovider.ISortableTreeViewContentProvider;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;


/**
 * This class contains some general actions, that can be
 * executed on a TreeViewContainer like expanding etc.
 *
 * @author BREDEX GmbH
 * @created Feb 28, 2006
 *
 */
public class TreeViewContainerGUIController {

    /**
     * utility class does not have constructor
     */
    private TreeViewContainerGUIController() {
        // private
    }

    /**
     * toggles the sorting of a SortableTreeViewer
     * @param owner ITreeViewerContainer
     */
    public static void sortTree(ITreeViewerContainer owner) {
        ITreeViewerContainer editor = owner;
        if (editor == null) {
            editor = ((ITreeViewerContainer)Plugin.getActivePart());
        }
        if (editor.getTreeViewer().getContentProvider() 
                instanceof ISortableTreeViewContentProvider) {
            ISortableTreeViewContentProvider prov = 
                (ISortableTreeViewContentProvider)
                    editor.getTreeViewer().getContentProvider();
            prov.setSorting(!prov.isSorting());
            editor.getTreeViewer().refresh();
        }
    }    

    /**
     * collapse or expand the give TreeViewer
     * 
     * @param treeCont
     *            ITreeViewerContainer
     */
    public static void collapseExpandTree(ITreeViewerContainer treeCont) {
        TreeViewer tv = treeCont.getTreeViewer();
        try {
            tv.getTree().setRedraw(false);
            tv.collapseAll();
            int autoExpandLevel = tv.getAutoExpandLevel();
            tv.expandToLevel(autoExpandLevel);
        } finally {
            tv.getTree().setRedraw(true);
        }
    }
    
    /**
     * expand a subtree
     * 
     * @param tv
     *            the tree viewer to use; may be <code>null</code>
     */
    public static void expandSubTree(TreeViewer tv) {
        if (tv == null) {
            return;
        }
        if (tv.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) tv
                    .getSelection();
            for (Object obj : selection.toArray()) {
                tv.expandToLevel(obj, 2);
            }
        }
    }

    /**
     * checks if the expand action is allowed
     * 
     * @param owner ITreeViewerContainer
     * @return true if any selected element got at least a child
     */
    public static boolean isExpandAllowed(ITreeViewerContainer owner) {
        if (owner.getTreeViewer().getSelection() 
                instanceof IStructuredSelection) {
            IStructuredSelection selection = 
                (IStructuredSelection)owner.getTreeViewer().getSelection();
            Iterator iter = selection.iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                if (owner.getTreeViewer().isExpandable(obj)) {
                    return true;
                }
            }
        }
        return false;
    }    

}