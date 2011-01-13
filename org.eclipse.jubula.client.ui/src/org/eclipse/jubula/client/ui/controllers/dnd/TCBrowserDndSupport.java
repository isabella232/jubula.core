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
package org.eclipse.jubula.client.ui.controllers.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.MoveNodeHandle;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.TestCaseBrowserRootGUI;
import org.eclipse.jubula.client.ui.views.TestCaseBrowser;
import org.eclipse.jubula.tools.exception.GDProjectDeletedException;
import org.eclipse.ui.IViewPart;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Case Browser.
 *
 * @author BREDEX GmbH
 * @created 19.03.2008
 */
public class TCBrowserDndSupport {

    /**
     * Private constructor
     */
    private TCBrowserDndSupport() {
        // Do nothing
    }

    /**
     * Checks whether the nodes in the given selection can legally be moved
     * to the given target location.
     * 
     * @param selection The selection to check.
     * @param target The target location to check.
     * @return <code>true</code> if the move is legal. Otherwise, 
     *         <code>false</code>.
     */
    public static boolean canMove(
            IStructuredSelection selection, Object target) {
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            // check the object to drag
            if ((!(obj instanceof SpecTestCaseGUI) 
                    && !(obj instanceof CategoryGUI))
                || (obj instanceof GuiNode 
                    && !((GuiNode)obj).isEditable())) {
                
                return false;
            }
            // check the object to drop on (target)
            if ((!(target instanceof CategoryGUI) 
                    && !(target instanceof TestCaseBrowserRootGUI))
                || (target instanceof GuiNode
                    && !((GuiNode)target).isEditable())) {
                
                return false;
            }
            if (((GuiNode)obj).getContent().hasCircularDependences(
                ((GuiNode)target).getContent())) {
                
                return false;
            }
        }
        return true;

    }
    
    /**
     * Moves the given nodes to the given target location.
     * 
     * @param nodesToBeMoved The nodes to move.
     * @param target The target location.
     */
    public static void moveNodes(List<GuiNode> nodesToBeMoved, 
            GuiNode target) throws PMException, GDProjectDeletedException {
        if (getSpecView() != null) {
            doMove(nodesToBeMoved, target);
        }
    }
    
    /**
     * tries to move all selected node into the target node. Operates on the GUI model
     * and on the INodePO model 
     * @param target
     *      GuiNode
     * @param nodes
     *      List <GuiNode>
     */
    private static void doMove(List <GuiNode> nodes, GuiNode target) 
        throws PMException, GDProjectDeletedException {

        // persist changes into database
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>();
        for (GuiNode node : nodes) {
            INodePO nodeToMove = node.getContent();

            // determine old parent
            INodePO oldParent = node.getParentNode().getContent();
            
            // determine new parent
            INodePO newParent = target.getContent();
            
            // create command
            cmds.add(new MoveNodeHandle(nodeToMove, oldParent, newParent));
        }
        
        // execute commands in mastersession
        MultipleNodePM.getInstance().executeCommands(cmds);

        // do gui updates
        for (GuiNode node : nodes) {
            GuiNode oldParent = node.getParentNode();
            oldParent.removeNode(node);
            target.addNode(node);
        }

        getSpecView().getTreeViewer().refresh();
        
    }

    /**
     * @return instance of TestCaseBrowser, or null.
     */
    private static TestCaseBrowser getSpecView() {
        IViewPart viewPart = Plugin.getView(Constants.TC_BROWSER_ID);
        if (viewPart != null) {
            return (TestCaseBrowser)viewPart;
        }
        return null;
    }

}
