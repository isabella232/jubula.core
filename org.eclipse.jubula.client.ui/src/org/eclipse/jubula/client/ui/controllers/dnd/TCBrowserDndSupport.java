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
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.MoveNodeHandle;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.views.TestCaseBrowser;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
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
            if ((!(obj instanceof ISpecTestCasePO) 
                    && !(obj instanceof ICategoryPO))
                || (obj instanceof INodePO 
                    && !NodeBP.isEditable((INodePO)obj))) {
                
                return false;
            }
            // check the object to drop on (target)
            if (!(target instanceof ICategoryPO
                    || target instanceof IProjectPO)
                    || (target instanceof INodePO
                            && !NodeBP.isEditable((INodePO)target))) {
                
                return false;
            }
            if (((INodePO)obj).hasCircularDependences(((INodePO)target))) {
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
    public static void moveNodes(List<INodePO> nodesToBeMoved, 
            INodePO target) throws PMException, ProjectDeletedException {
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
     *      List <INodePO>
     */
    private static void doMove(List <INodePO> nodes, INodePO target) 
        throws PMException, ProjectDeletedException {

        // persist changes into database
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>();
        for (INodePO nodeToMove : nodes) {

            // determine old parent
            INodePO oldParent = nodeToMove.getParentNode();
            
            // create command
            cmds.add(new MoveNodeHandle(nodeToMove, oldParent, target));
        }
        
        // execute commands in mastersession
        MultipleNodePM.getInstance().executeCommands(cmds);

        // do gui updates
        for (INodePO node : nodes) {
            INodePO oldParent = node.getParentNode();
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
