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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.MoveNodeHandle;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Case Browser.
 *
 * @author BREDEX GmbH
 * @created 19.10.2011
 */
public abstract class AbstractBrowserDndSupport {

    /**
     * Private constructor
     */
    protected AbstractBrowserDndSupport() {
        // Do nothing
    }

    /**
     * tries to move all selected node into the target node. Operates on the GUI model
     * and on the INodePO model 
     * @param target
     *      GuiNode
     * @param nodes
     *      List <INodePO>
     */
    protected static void doMove(List<INodePO> nodes, IPersistentObject target)
        throws PMException, ProjectDeletedException {
        // persist changes into database
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>();
        List<DataChangedEvent> eventList = 
                new ArrayList<DataChangedEvent>();
        for (INodePO nodeToMove : nodes) {

            // determine old parent
            INodePO oldParent = nodeToMove.getParentNode();

            // create command
            cmds.add(new MoveNodeHandle(nodeToMove, oldParent, target));
            eventList.add(new DataChangedEvent(target, 
                    DataState.StructureModified, UpdateState.notInEditor));
            eventList.add(new DataChangedEvent(oldParent, 
                    DataState.StructureModified, UpdateState.notInEditor));
        }

        
        // execute commands in master session
        MultipleNodePM.getInstance().executeCommands(cmds);

        // notify listener for updates
        DataEventDispatcher.getInstance().fireDataChangedListener(
                eventList.toArray(new DataChangedEvent[0]));
    }
}
